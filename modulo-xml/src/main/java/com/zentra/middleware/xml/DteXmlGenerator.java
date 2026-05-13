package com.zentra.middleware.xml;

import com.zentra.middleware.core.model.DocumentoElectronico;
import com.zentra.middleware.core.util.DescripcionTipoTransaccion;
import com.zentra.middleware.core.util.SifenUtil;
import java.math.BigDecimal;
import java.math.RoundingMode;
import com.zentra.middleware.sifen.schema.*;
import com.zentra.middleware.core.model.*;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.Marshaller;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.GregorianCalendar;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.util.logging.Logger;

@Service
public class DteXmlGenerator {

    private static final Logger logger = Logger.getLogger(DteXmlGenerator.class.getName());
    private static JAXBContext jaxbContext;
    private final ObjectFactory factory = new ObjectFactory();

    static {
        try {
            jaxbContext = JAXBContext.newInstance("com.zentra.middleware.sifen.schema");
        } catch (Exception e) {
            logger.severe("Error inicializando JAXBContext: " + e.getMessage());
        }
    }

    public String generarXml(DocumentoElectronico dte) {
        try {
            if (dte.getEmisor() == null) {
                throw new IllegalArgumentException("El DTE debe tener un emisor asignado.");
            }

            // 1. Generar CDC Real
            String tipoDoc = dte.getTipoDocumento() != null ? dte.getTipoDocumento() : "1";
            String ruc = dte.getEmisor().getRuc();
            String dv = dte.getEmisor().getDv();
            String estab = dte.getEmisor().getCodEstablecimiento();
            String punto = dte.getEmisor().getPuntoExpedicion();
            
            String numComp = dte.getNumeroComprobante() != null ? dte.getNumeroComprobante() : "001-001-0000001";
            String[] partesNro = numComp.split("-");
            String nro = partesNro[partesNro.length - 1].replaceAll("[^0-9]", "");
            if (nro.isEmpty()) nro = "0000001";
            
            ZonedDateTime zdtCreacion = ZonedDateTime.of(dte.getFechaCreacion(), ZoneId.systemDefault())
                                              .withZoneSameInstant(ZoneId.of("America/Asuncion"));
            String fechaStr = zdtCreacion.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
            String codSeg = String.format("%09d", (int)(Math.random() * 1000000000));

            // SIFEN v150: iTipCont (25) e iTipEmi (34) deben coincidir entre CDC y tags XML. 
            // Forzamos 2 (Jurídica) si el RUC es > 80000000.
            String iTipCont = (Long.parseLong(ruc.replaceAll("[^0-9]", "0")) > 80000000) ? "2" : "1";
            String iTipEmi = "1"; // Siempre 1 para Normal, 2 para Contingencia

            // SIFEN v150: generarCdc(tipoDoc, ruc, dv, estab, punto, nro, iTipCont, fecha, iTipEmi, codSeg)
            String cdc = SifenUtil.generarCdc(tipoDoc, ruc, dv, estab, punto, nro, iTipCont, fechaStr, iTipEmi, codSeg);
            dte.setCdc(cdc);

            // Guardar iTipEmi en el dte para que gOpeDE sea consistente
            dte.setTipoEmision(Integer.parseInt(iTipEmi));

            // 2. Poblar objetos JAXB (Mapeo Exhaustivo)
            RDE rde = factory.createRDE();
            rde.setDVerFor(new BigInteger("150"));

            TDE de = factory.createTDE();
            de.setId(cdc);
            
            // SIFEN v150: dDVId DEBE ser el último dígito del CDC (posición 44).
            // Si hay discrepancia entre este tag y el Id del <DE>, SIFEN devuelve Error 1003.
            String dvCdc = cdc.substring(43);
            de.setDDVId(new BigInteger(dvCdc));
            logger.info("Inyectando dDVId: " + dvCdc + " para CDC: " + cdc);
            
            ZonedDateTime zdtSystem = ZonedDateTime.of(dte.getFechaCreacion(), ZoneId.systemDefault());
            // Se restan 5 minutos para compensar latencia y reloj del servidor SIFEN que puede estar atrasado (Evita Error 1004)
            ZonedDateTime zdtAsuncion = zdtSystem.withZoneSameInstant(ZoneId.of("America/Asuncion")).minusMinutes(5);
            GregorianCalendar gcal = GregorianCalendar.from(zdtAsuncion);
            XMLGregorianCalendar xmlCal = DatatypeFactory.newInstance().newXMLGregorianCalendar(gcal);
            xmlCal.setFractionalSecond(null);
            xmlCal.setTimezone(javax.xml.datatype.DatatypeConstants.FIELD_UNDEFINED);
            de.setDFecFirma(xmlCal);
            de.setDSisFact(1);

            TgCOpeDE gOpeDE = factory.createTgCOpeDE();
            int tipEmi = dte.getTipoEmision() != null ? dte.getTipoEmision() : 1;
            gOpeDE.setITipEmi(BigInteger.valueOf(tipEmi));
            gOpeDE.setDDesTipEmi(tipEmi == 1 ? TdDesTipEmi.NORMAL : TdDesTipEmi.CONTINGENCIA);
            gOpeDE.setDCodSeg(codSeg);
            de.setGOpeDE(gOpeDE);

            TgDTim gTimb = factory.createTgDTim();
            gTimb.setITiDE(new BigInteger(tipoDoc));
            gTimb.setDDesTiDE(mapearTipoDocEnum(tipoDoc)); 
            gTimb.setDNumTim(dte.getTimbrado() != null && !dte.getTimbrado().isEmpty() ? dte.getTimbrado() : "12345678");
            gTimb.setDEst(estab);
            gTimb.setDPunExp(punto);
            gTimb.setDNumDoc(String.format("%07d", Long.parseLong(nro)));

            // CAMBIO v2-2: dFeIniT = fecha de INICIO DEL TIMBRADO (registrada en Marangatu),
            // NO la fecha de emisión. SIFEN valida que coincida con el timbrado registrado.
            // InventivaFE aprobado: <dFeIniT>2023-10-30</dFeIniT>
            // Zentra anterior (rechazado): <dFeIniT>2026-04-25</dFeIniT> (fecha de hoy = error)
            if (dte.getEmisor().getFechaInicioTimbrado() != null) {
                GregorianCalendar gcalTimb = new GregorianCalendar();
                gcalTimb.setTime(java.sql.Date.valueOf(dte.getEmisor().getFechaInicioTimbrado()));
                XMLGregorianCalendar xmlCalTimb = DatatypeFactory.newInstance().newXMLGregorianCalendar(gcalTimb);
                // Solo fecha (sin hora) — SIFEN espera formato yyyy-MM-dd
                xmlCalTimb.setHour(javax.xml.datatype.DatatypeConstants.FIELD_UNDEFINED);
                xmlCalTimb.setMinute(javax.xml.datatype.DatatypeConstants.FIELD_UNDEFINED);
                xmlCalTimb.setSecond(javax.xml.datatype.DatatypeConstants.FIELD_UNDEFINED);
                xmlCalTimb.setMillisecond(javax.xml.datatype.DatatypeConstants.FIELD_UNDEFINED);
                xmlCalTimb.setTimezone(javax.xml.datatype.DatatypeConstants.FIELD_UNDEFINED);
                gTimb.setDFeIniT(xmlCalTimb);
            } else {
                // Fallback: si no hay fecha de timbrado en BD, usar la fecha de emisión
                gTimb.setDFeIniT(xmlCal);
                logger.warning("ADVERTENCIA: fechaInicioTimbrado no configurada en BD. Usando fecha de emisión como dFeIniT.");
            }
            de.setGTimb(gTimb);

            // gDatGralOpe (Datos Generales)
            TgDaGOC gDatGralOpe = factory.createTgDaGOC();
            gDatGralOpe.setDFeEmiDE(xmlCal);
            
            // Documento Asociado if present
            if (dte.getCdcDocumentoAsociado() != null && !dte.getCdcDocumentoAsociado().isEmpty()) {
                TgCamDEAsoc gCamDEAsoc = factory.createTgCamDEAsoc();
                gCamDEAsoc.setITipDocAso(BigInteger.valueOf(dte.getTipoDocumentoAsociado() != null ? dte.getTipoDocumentoAsociado() : 1));
                gCamDEAsoc.setDDesTipDocAso(gCamDEAsoc.getITipDocAso().equals(BigInteger.valueOf(1)) ? TdDesTipDocAso.ELECTRÓNICO : TdDesTipDocAso.IMPRESO);
                gCamDEAsoc.setDCdCDERef(dte.getCdcDocumentoAsociado());
                de.getGCamDEAsoc().add(gCamDEAsoc);
            }
            
            // Mapeo detallado del Emisor
            gDatGralOpe.setGEmis(mapearEmisor(dte));
            
            // gOpeCom (Operación Comercial - OBLIGATORIO en v150 para Factura/Autofactura)
            TgOpeCom gOpeCom = factory.createTgOpeCom();
            // 1 = IVA, 2 = ISC, 3 = Renta, 4 = Ninguno, 5 = IVA-Renta
            gOpeCom.setITImp(BigInteger.valueOf(1));
            gOpeCom.setDDesTImp(TdDesTImp.IVA);
            gOpeCom.setCMoneOpe(CMondT.PYG);
            gOpeCom.setDDesMoneOpe("Guarani");
            // Tipo de transacción: Obligatorio si C002 = 1, 2, 3 o 4. No informar si es 5, 6 o 7 (Notas de Crédito/Débito/Remisión)
            int tDoc = Integer.parseInt(tipoDoc != null ? tipoDoc : "1");
            if (tDoc >= 1 && tDoc <= 4) {
                int tipTra = resolverTipoTransaccion(dte);
                gOpeCom.setITipTra(tipTra);
                gOpeCom.setDDesTipTra(TdDesTiTran.fromValue(DescripcionTipoTransaccion.getDescripcion(tipTra)));
            }
            gDatGralOpe.setGOpeCom(gOpeCom);

            
            // Mapeo detallado del Receptor
            gDatGralOpe.setGDatRec(mapearReceptor(dte));
            
            de.setGDatGralOpe(gDatGralOpe);
            
            // gDtipDE (Datos específicos según tipoDoc)
            TgDtipDE gDtipDE = factory.createTgDtipDE();
            
            // Si es Factura Electrónica (tipo 1)
            if ("1".equals(tipoDoc) || "2".equals(tipoDoc) || "3".equals(tipoDoc)) {
                TgCamFE gCamFE = factory.createTgCamFE();
                int indPres = dte.getIndicadorPresencia() != null ? dte.getIndicadorPresencia() : 1;
                gCamFE.setIIndPres(BigInteger.valueOf(indPres));
                gCamFE.setDDesIndPres(mapearDesPresencia(indPres));
                
                if (dte.getComprasPublicas() != null) {
                    TgCompPub gComp = factory.createTgCompPub();
                    gComp.setDModCont(dte.getComprasPublicas().getModalidadContratacion());
                    gComp.setDEntCont(BigInteger.valueOf(dte.getComprasPublicas().getEntidadContratante()));
                    gComp.setDAnoCont(BigInteger.valueOf(dte.getComprasPublicas().getAnioContratacion()));
                    gComp.setDSecCont(BigInteger.valueOf(dte.getComprasPublicas().getSecuencialContrato()));
                    gComp.setDFeCodCont(dte.getComprasPublicas().getFechaCodigoContrato());
                    gCamFE.setGCompPub(gComp);
                }
                gDtipDE.setGCamFE(gCamFE);
            } else if ("4".equals(tipoDoc)) {
                // Autofactura Electrónica
                // TgCamAE no tiene iIndPres; esos campos son de TgCamFE
                TgCamAE gCamAE = factory.createTgCamAE();
                int natVen = dte.getNaturalezaVendedor() != null ? dte.getNaturalezaVendedor() : 1;
                gCamAE.setINatVen(BigInteger.valueOf(natVen));
                gCamAE.setDDesNatVen(natVen == 1 ? TdDesNatVen.NO_CONTRIBUYENTE : TdDesNatVen.EXTRANJERO);

                // Datos del vendedor (campos obligatorios del XSD para gCamAE)
                String rucRec = dte.getRucReceptor() != null ? dte.getRucReceptor() : "0";
                gCamAE.setITipIDVen(BigInteger.valueOf(1));  // 1 = Cédula paraguaya
                gCamAE.setDDTipIDVen(TdDtipDoc.CÉDULA_PARAGUAYA);
                gCamAE.setDNumIDVen(rucRec);
                gCamAE.setDNomVen(dte.getReceptorRazonSocial() != null ? dte.getReceptorRazonSocial() : "Sin nombre");
                // Dirección y ubicación del vendedor (obligatorios según XSD)
                gCamAE.setDDirVen("Dir. no especificada");
                gCamAE.setDNumCasVen(BigInteger.ONE);
                gCamAE.setCDepVen(BigInteger.ONE);
                gCamAE.setDDesDepVen(TDesDepartamento.CAPITAL);
                gCamAE.setCCiuVen(BigInteger.ONE);
                gCamAE.setDDesCiuVen("ASUNCION (DISTRITO)");
                // Dirección del proveedor (misma que vendedor para simplificar)
                gCamAE.setDDirProv("Dir. no especificada");
                gCamAE.setCCiuProv(BigInteger.ONE);
                gCamAE.setCDepProv(BigInteger.ONE);
                gCamAE.setDDesDepProv(TDesDepartamento.CAPITAL);
                gCamAE.setDDesCiuProv("ASUNCION (DISTRITO)");

                gDtipDE.setGCamAE(gCamAE);
            } else if ("5".equals(tipoDoc) || "6".equals(tipoDoc)) {
                // Nota de Crédito / Débito
                TgCamNCDE gCamNCDE = factory.createTgCamNCDE();
                int motId = 1;
                try {
                   if (dte.getMotivoEmision() != null && !dte.getMotivoEmision().isEmpty()) {
                       motId = Integer.parseInt(dte.getMotivoEmision());
                   }
                } catch (Exception e) {}
                gCamNCDE.setIMotEmi(String.valueOf(motId));
                gCamNCDE.setDDesMotEmi(mapearMotivoNcNd(motId, tipoDoc));
                gDtipDE.setGCamNCDE(gCamNCDE);
            } else if ("7".equals(tipoDoc)) {
                // Nota de Remisión Electrónica
                if (dte.getTransporte() != null) {
                    TgCamNRE gCamNRE = factory.createTgCamNRE();
                    Transporte t = dte.getTransporte();
                    int motId = t.getMotivoTraslado() != null ? t.getMotivoTraslado() : 1;
                    gCamNRE.setIMotEmiNR(BigInteger.valueOf(motId));
                    gCamNRE.setDDesMotEmiNR(mapearMotivoRemision(motId));
                    int respEmiNR = t.getResponsableEmision() != null ? t.getResponsableEmision() : 1;
                    gCamNRE.setIRespEmiNR(BigInteger.valueOf(respEmiNR));
                    gCamNRE.setDDesRespEmiNR(respEmiNR == 1 ? "Emisor" : "Receptor");
                    gCamNRE.setDKmR(t.getKmsRecorrido() != null ? t.getKmsRecorrido() : 10);
                    gCamNRE.setDFecEm(dte.getFechaCreacion().toLocalDate().toString());
                    if (t.getPrecioFlete() != null) {
                        gCamNRE.setCPreFle(BigDecimal.valueOf(t.getPrecioFlete()));
                    }
                    gDtipDE.setGCamNRE(gCamNRE);
                }
            }
            de.setGDtipDE(gDtipDE);
            
            // Si hay datos de transporte (gTransp)
            if (dte.getTransporte() != null) {
                Transporte t = dte.getTransporte();
                TgTransp gTranspObj = factory.createTgTransp();
                gTranspObj.setIModTrans(BigInteger.valueOf(1)); // Terrestre
                gTranspObj.setDDesModTrans(TdDesModTrans.TERRESTRE);
                gTranspObj.setIRespFlete(BigInteger.valueOf(1)); // Emisor
                
                TgCamTrans gTrans = factory.createTgCamTrans();
                gTrans.setINatTrans(BigInteger.valueOf(t.getNaturalezaTransportista() != null ? t.getNaturalezaTransportista() : 1));
                gTrans.setDNomTrans(t.getNombreTransportista() != null ? t.getNombreTransportista() : "Transportista Mock");
                gTrans.setDRucTrans(t.getRucTransportista());
                gTrans.setDDVTrans(t.getDvTransportista() != null ? new BigInteger(t.getDvTransportista()) : null);
                String numDocChof = (t.getNumeroDocumentoChofer() == null || t.getNumeroDocumentoChofer().trim().isEmpty() || "null".equals(t.getNumeroDocumentoChofer())) ? "1234567" : t.getNumeroDocumentoChofer().trim();
                gTrans.setDNumIDChof(numDocChof);
                gTrans.setDNomChof(t.getNombreChofer() != null && !t.getNombreChofer().trim().isEmpty() && !"null".equals(t.getNombreChofer()) ? t.getNombreChofer() : "Chofer Mock");
                gTrans.setDDomFisc("Domicilio transportista mock");
                gTrans.setDDirChof(t.getDireccionChofer() != null && !t.getDireccionChofer().trim().isEmpty() && !"null".equals(t.getDireccionChofer()) ? t.getDireccionChofer() : "Dir Chofer Mock");
                
                gTranspObj.setGCamTrans(gTrans);
                gDtipDE.setGTransp(gTranspObj);
            }

            // Condición de venta
            TgCamCond gCamCond = factory.createTgCamCond();
            Integer condOpe = dte.getCondicionOperacion() != null ? dte.getCondicionOperacion() : 1;
            gCamCond.setICondOpe(BigInteger.valueOf(condOpe));
            gCamCond.setDDCondOpe(condOpe == 1 ? TdDCondOpe.CONTADO : TdDCondOpe.CRÉDITO);
            
            if (condOpe == 1) {
                TgPagCont gPagCont = factory.createTgPagCont();
                gPagCont.setITiPago(BigInteger.valueOf(1)); // Efectivo
                gPagCont.setDDesTiPag("Efectivo");
                // En PYG los montos deben ser enteros sin decimales (norma SIFEN v150)
                gPagCont.setDMonTiPag(entero(dte.getTotalOperacion() != null ? dte.getTotalOperacion() : 0.0));
                gPagCont.setCMoneTiPag(CMondT.PYG);
                gPagCont.setDDMoneTiPag("Guarani");
                gCamCond.getGPaConEIni().add(gPagCont);
            } else {
                TgPagCred gPagCred = factory.createTgPagCred();
                gPagCred.setICondCred(BigInteger.valueOf(2)); // Cuotas
                gPagCred.setDDCondCred(TdDCondCred.CUOTA);
                gPagCred.setDCuotas(BigInteger.valueOf(dte.getCuotas().size()));
                
                for (Cuota c : dte.getCuotas()) {
                    TgCuotas gCuotaStruct = factory.createTgCuotas();
                    gCuotaStruct.setCMoneCuo(CMondT.PYG);
                    gCuotaStruct.setDDMoneCuo("Guarani");
                    gCuotaStruct.setDMonCuota(BigDecimal.valueOf(c.getMonto()));
                    gCuotaStruct.setDVencCuo(c.getFechaVencimiento().toString());
                    gPagCred.getGCuotas().add(gCuotaStruct);
                }
                gCamCond.setGPagCred(gPagCred);
            }
            gDtipDE.setGCamCond(gCamCond);

            // Mapeo dinámico de Ítems
            BigDecimal totalExenta = BigDecimal.ZERO;
            BigDecimal totalGravas5 = BigDecimal.ZERO;
            BigDecimal totalGravas10 = BigDecimal.ZERO;
            BigDecimal totalDescuentoItems = BigDecimal.ZERO;
            // Acumuladores de base gravada e IVA por ítem (para coherencia con gTotSub)
            BigDecimal acumBaseGrav5 = BigDecimal.ZERO;
            BigDecimal acumBaseGrav10 = BigDecimal.ZERO;
            BigDecimal acumIva5 = BigDecimal.ZERO;
            BigDecimal acumIva10 = BigDecimal.ZERO;

            if (dte.getItems() != null) {
                for (ItemDocumento it : dte.getItems()) {
                    TgCamItem gCamItem = mapearItem(it);
                    gDtipDE.getGCamItem().add(gCamItem);
                    
                    totalDescuentoItems = totalDescuentoItems.add(BigDecimal.valueOf(it.getMontoDescuento() != null ? it.getMontoDescuento() : 0.0));
                    
                    // Acumulación de totales por tipo de afectación IVA
                    double tasa = it.getTasaIva() != null ? it.getTasaIva() : 0.0;
                    double montoTotal = it.getMontoTotalItem() != null ? it.getMontoTotalItem() : 0.0;
                    BigDecimal montoTotalBD = BigDecimal.valueOf(montoTotal).setScale(0, RoundingMode.HALF_UP);
                    if (tasa == 10.0) {
                        totalGravas10 = totalGravas10.add(montoTotalBD);
                        // Calcular base y IVA por ítem con la misma fórmula que mapearItem
                        BigDecimal baseItem = montoTotalBD.divide(BigDecimal.valueOf(1.1), 0, RoundingMode.HALF_UP);
                        BigDecimal ivaItem = montoTotalBD.subtract(baseItem);
                        acumBaseGrav10 = acumBaseGrav10.add(baseItem);
                        acumIva10 = acumIva10.add(ivaItem);
                    } else if (tasa == 5.0) {
                        totalGravas5 = totalGravas5.add(montoTotalBD);
                        BigDecimal baseItem = montoTotalBD.divide(BigDecimal.valueOf(1.05), 0, RoundingMode.HALF_UP);
                        BigDecimal ivaItem = montoTotalBD.subtract(baseItem);
                        acumBaseGrav5 = acumBaseGrav5.add(baseItem);
                        acumIva5 = acumIva5.add(ivaItem);
                    } else {
                        // Exento: se suma al total exento, no al gravado
                        totalExenta = totalExenta.add(montoTotalBD);
                    }
                }
            }

            // NT13 F015/F016: dIVA5 y dIVA10 son la SUMA EXACTA de los dLiqIVAItem por tasa.
            // NT13 F036/F037: dLiqTotIVA5 y dLiqTotIVA10 son el IVA del redondeo (dRedon=0 → 0).
            // NT13 F017: dTotIVA = dIVA5 + dIVA10 - dLiqTotIVA5 - dLiqTotIVA10 + dIVAComi
            // → con dRedon=0 y dIVAComi=0: dTotIVA = acumIva5 + acumIva10
            // NT13 E735b (1911): dBasGravIVA = round(dTotOpeItem / 1.1) — validado ítem a ítem.

            de.setGDtipDE(gDtipDE);

            // gTotSub (Totales)
            TgTotSub gTotSub = factory.createTgTotSub();
            gTotSub.setDSubExe(entero(totalExenta));
            // CAMBIO v2-4: dSubExo requerido por InventivaFE (siempre 0 para ventas normales)
            gTotSub.setDSubExo(BigDecimal.ZERO);
            gTotSub.setDSub5(entero(totalGravas5));
            gTotSub.setDSub10(entero(totalGravas10));
            
            BigDecimal totOpeSinDescGlobal = totalExenta.add(totalGravas5).add(totalGravas10);
            BigDecimal descGlobal = BigDecimal.valueOf(dte.getDescuentoGlobal() != null ? dte.getDescuentoGlobal() : 0.0);
            BigDecimal porcDescGlobal = BigDecimal.valueOf(dte.getPorcentajeDescuentoGlobal() != null ? dte.getPorcentajeDescuentoGlobal() : 0.0);
            
            gTotSub.setDTotOpe(entero(totOpeSinDescGlobal));
            gTotSub.setDTotDesc(entero(totalDescuentoItems));
            gTotSub.setDTotDescGlotem(BigDecimal.ZERO);
            gTotSub.setDTotAntItem(BigDecimal.ZERO);
            gTotSub.setDTotAnt(BigDecimal.ZERO);
            gTotSub.setDPorcDescTotal(porcDescGlobal.setScale(0, RoundingMode.HALF_UP));
            gTotSub.setDDescTotal(entero(descGlobal));
            gTotSub.setDAnticipo(BigDecimal.ZERO);
            gTotSub.setDRedon(BigDecimal.ZERO);
            // CAMBIO v2-4: dComi requerido por InventivaFE (comisión = 0 para ventas directas)
            gTotSub.setDComi(BigDecimal.ZERO);
            
            BigDecimal totGralOpe = totOpeSinDescGlobal.subtract(descGlobal);
            gTotSub.setDTotGralOpe(entero(totGralOpe));
            
            // F015: Suma exacta de dLiqIVAItem al 5%
            // F016: Suma exacta de dLiqIVAItem al 10%
            // F036/F037: IVA del redondeo (dRedon=0 → 0)
            // F017: dTotIVA = F015 + F016 (con dRedon=0 y sin comisión)
            BigDecimal totIvaAcum = acumIva5.add(acumIva10);

            gTotSub.setDIVA5(entero(acumIva5));
            gTotSub.setDIVA10(entero(acumIva10));
            gTotSub.setDLiqTotIVA5(BigDecimal.ZERO);
            gTotSub.setDLiqTotIVA10(BigDecimal.ZERO);
            gTotSub.setDIVAComi(BigDecimal.ZERO);
            gTotSub.setDTotIVA(entero(totIvaAcum));

            gTotSub.setDBaseGrav5(acumBaseGrav5);
            gTotSub.setDBaseGrav10(acumBaseGrav10);
            gTotSub.setDTBasGraIVA(entero(acumBaseGrav5.add(acumBaseGrav10)));
            
            de.setGTotSub(gTotSub);

            // CAMBIO v2-5: gCamGen — nodo vacío requerido por InventivaFE aprobado en producción.
            // El XSD lo define como minOccurs="0" pero InventivaFE siempre lo incluye como <gCamGen/>.
            de.setGCamGen(factory.createTgCamGen());

            // Propagar los totales calculados al modelo DTE.
            // El firmador (XmlSignerService) los necesita para construir la URL del QR.
            dte.setTotalIva(totIvaAcum.doubleValue());
            dte.setTotalOperacion(totGralOpe.doubleValue());

            rde.setDE(de);

            // 3. Marshalling a ByteArrayOutputStream con encoding UTF-8 explícito
            // Se usa BAOS en lugar de StringWriter para garantizar que el XML
            // declare correctamente <?xml ... encoding="UTF-8"?> sin corrupción.
            Marshaller marshaller = jaxbContext.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.FALSE);
            marshaller.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");
            marshaller.setProperty(Marshaller.JAXB_SCHEMA_LOCATION,
                "http://ekuatia.set.gov.py/sifen/xsd siRecepDE_v150.xsd");
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            marshaller.marshal(factory.createRDE(rde), baos);
            String xmlGenerated = baos.toString(StandardCharsets.UTF_8);
            
            // 2. LIMPIEZA CRÍTICA PARA DATAPOWER/SIFEN v150
            // Eliminar cualquier namespace ns2 que JAXB pueda inyectar por la firma.
            xmlGenerated = xmlGenerated.replace(" xmlns:ns2=\"http://www.w3.org/2000/09/xmldsig#\"", "");

            // Reemplazar la etiqueta raíz para inyectar schemaLocation SIN perder el xmlns default de JAXB.
            // JAXB genera: <rDE xmlns="http://ekuatia.set.gov.py/sifen/xsd">
            xmlGenerated = xmlGenerated.replaceFirst("<rDE\\s+xmlns=\"http://ekuatia\\.set\\.gov\\.py/sifen/xsd\">",
                "<rDE xmlns=\"http://ekuatia.set.gov.py/sifen/xsd\" " +
                "xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" " +
                "xsi:schemaLocation=\"http://ekuatia.set.gov.py/sifen/xsd siRecepDE_v150.xsd\">");

            dte.setXmlGenerado(xmlGenerated);
            return xmlGenerated;

        } catch (Exception e) {
            throw new RuntimeException("Error en JAXB Mapping SIFEN: " + e.getMessage(), e);
        }
    }

    private TgEmis mapearEmisor(DocumentoElectronico dte) {
        Empresa emisor = dte.getEmisor();
        TgEmis gEmis = factory.createTgEmis();
        gEmis.setDRucEm(emisor.getRuc());
        gEmis.setDDVEmi(new BigInteger(emisor.getDv() != null ? emisor.getDv() : "0"));
        // SIFEN v150: iTipCont debe coincidir con el valor usado en el CDC.
        // Si el RUC es >= 80000000 es Persona Jurídica (2), de lo contrario
        // se respeta el valor configurado en la entidad Empresa.
        long rucNum = 0;
        try { rucNum = Long.parseLong(emisor.getRuc().replaceAll("[^0-9]", "0")); } catch (Exception ignored) {}
        int tipCont = (rucNum >= 80000000) ? 2 : (emisor.getTipoContribuyente() != null ? emisor.getTipoContribuyente() : 1);
        gEmis.setITipCont(BigInteger.valueOf(tipCont));
        gEmis.setDNomEmi(emisor.getRazonSocial());
        gEmis.setDDirEmi(emisor.getDireccion() != null ? emisor.getDireccion() : "Calle Mock 123");
        gEmis.setDNumCas(new BigInteger(emisor.getNumeroCasa() != null ? emisor.getNumeroCasa() : "0"));
        
        gEmis.setCDepEmi(BigInteger.valueOf(emisor.getCodDepartamento() != null ? emisor.getCodDepartamento() : 1));
        String depto = emisor.getDepartamento() != null ? emisor.getDepartamento() : "CAPITAL";
        try {
            gEmis.setDDesDepEmi(TDesDepartamento.fromValue(depto));
        } catch (Exception e) {
            logger.warning("Nombre de departamento no reconocido por SIFEN: " + depto + ". Usando CAPITAL.");
            gEmis.setDDesDepEmi(TDesDepartamento.CAPITAL);
        }
        // CAMBIO v2-3: cDisEmi y dDesDisEmi — obligatorios cuando cDepEmi está presente.
        // InventivaFE aprobado: <cDisEmi>1</cDisEmi><dDesDisEmi>ASUNCION (DISTRITO)</dDesDisEmi>
        int codDistrito = (emisor.getCodDistrito() != null && emisor.getCodDistrito() > 0)
            ? emisor.getCodDistrito() : 1;
        gEmis.setCDisEmi(BigInteger.valueOf(codDistrito));
        String desDistrito = (emisor.getDistrito() != null && !emisor.getDistrito().isBlank())
            ? emisor.getDistrito() : "ASUNCION (DISTRITO)";
            
        if (codDistrito == 1 && (desDistrito.equalsIgnoreCase("ASUNCION") || desDistrito.equalsIgnoreCase("ASUNCION (DISTRITO)"))) {
            desDistrito = "ASUNCION (DISTRITO)";
        }
        gEmis.setDDesDisEmi(desDistrito);
        
        int codCiu = emisor.getCodCiudad() != null ? emisor.getCodCiudad() : 1;
        gEmis.setCCiuEmi(codCiu);
        String desCiu = emisor.getCiudad() != null ? emisor.getCiudad() : "ASUNCION (DISTRITO)";
        
        if (codCiu == 1 && (desCiu.equalsIgnoreCase("ASUNCION") || desCiu.equalsIgnoreCase("ASUNCION (DISTRITO)") || desCiu.equalsIgnoreCase("ASUNCION (CAPITAL)"))) {
            desCiu = "ASUNCION (DISTRITO)";
        }
        gEmis.setDDesCiuEmi(desCiu);
        
        gEmis.setDTelEmi(emisor.getTelefono() != null ? emisor.getTelefono() : "021000000");
        gEmis.setDEmailE(emisor.getEmail() != null ? emisor.getEmail() : "emisor@example.com");

        // PLAN 4: Actividad Económica tomada del emisor (BD) en lugar de valor mock hardcodeado.
        // La BD tiene el código real registrado en Marangatu para RUC 80014603-4.
        // Actividad Económica: se prioriza el par código-descripción de la BD
        TgActEco gActEco = factory.createTgActEco();
        String codAct = emisor.getCodActividadEconomica();
        String desAct = emisor.getActividadEconomica();

        if (codAct != null && !codAct.isBlank() && codAct.matches("^\\d+$")) {
            // Caso ideal: tenemos el código numérico y la descripción por separado
            gActEco.setCActEco(codAct);
            gActEco.setDDesActEco(desAct != null && !desAct.isBlank() ? desAct : "Actividad económica");
        } else {
            // Fallback por compatibilidad con datos legacy o formatos combinados
            String actEcoRaw = (desAct != null && !desAct.isBlank()) ? desAct : "45301";
            if (actEcoRaw.contains("|")) {
                String[] partes = actEcoRaw.split("\\|", 2);
                gActEco.setCActEco(partes[0].trim());
                gActEco.setDDesActEco(partes[1].trim());
            } else {
                gActEco.setCActEco("45301"); // Genérico: Comercio
                gActEco.setDDesActEco(actEcoRaw);
            }
        }
        
        if (dte.getAmbiente() != null && "TEST".equals(dte.getAmbiente().name())) {
            gActEco.setCActEco("46699");
            gActEco.setDDesActEco("COMERCIO AL POR MAYOR DE OTROS PRODUCTOS N.C.P.");
        }
        
        gEmis.getGActEco().add(gActEco);

        return gEmis;
    }

    private TgDatRec mapearReceptor(DocumentoElectronico dte) {
        TgDatRec gDatRec = factory.createTgDatRec();
        String rucReceptor = dte.getRucReceptor();
        String razonSocial = dte.getReceptorRazonSocial() != null ? dte.getReceptorRazonSocial() : "Varios";

        // Lógica para iNatRec (Naturaleza del Receptor)
        if (rucReceptor != null && rucReceptor.length() > 0 && !rucReceptor.equals("Varios")) {
            // Si el RUC tiene DV (contiene guion o el dte.tipoReceptor dice que es contribuyente)
            if (rucReceptor.contains("-") || (dte.getTipoReceptor() != null && dte.getTipoReceptor() == 1)) {
                String rucSolo = rucReceptor.split("-")[0].trim();
                String dvRec = rucReceptor.contains("-") && rucReceptor.split("-").length > 1 ? rucReceptor.split("-")[1].trim() : "0";
                
                if (rucSolo.isEmpty() || rucSolo.equals("0")) {
                    gDatRec.setINatRec(BigInteger.valueOf(2)); // No Contribuyente
                    gDatRec.setITipIDRec(BigInteger.valueOf(5)); // Innominado
                    gDatRec.setDDTipIDRec("Innominado");
                    gDatRec.setDNumIDRec("0");
                    gDatRec.setDNomRec(razonSocial);
                } else {
                    gDatRec.setINatRec(BigInteger.valueOf(1)); // Contribuyente
                    gDatRec.setITiContRec(BigInteger.valueOf(2)); // Persona Jurídica (Generalmente se asume si es RUC)
                    gDatRec.setDRucRec(rucSolo);
                    gDatRec.setDDVRec(new BigInteger(dvRec));
                    gDatRec.setDNomRec(razonSocial);
                }
            } else {
                gDatRec.setINatRec(BigInteger.valueOf(2)); // No Contribuyente
                gDatRec.setITipIDRec(BigInteger.valueOf(1)); // Cédula de Identidad Parche
                gDatRec.setDDTipIDRec("Cédula paraguaya");
                
                String numId = (rucReceptor == null || rucReceptor.trim().isEmpty() || "null".equals(rucReceptor)) ? "0" : rucReceptor.trim();
                gDatRec.setDNumIDRec(numId);
                gDatRec.setDNomRec(razonSocial);
            }
        } else {
            // Consumidor Final
            gDatRec.setINatRec(BigInteger.valueOf(2));
            gDatRec.setITipIDRec(BigInteger.valueOf(1));
            gDatRec.setDDTipIDRec("Cédula paraguaya");
            gDatRec.setDNumIDRec("0");
            gDatRec.setDNomRec("Sin Nombre");
        }

        int opeType = dte.getTipoOperacion() != null ? dte.getTipoOperacion() : 1;
        gDatRec.setITiOpe(BigInteger.valueOf(opeType));
        
        String codPais = dte.getCPaisReceptor() != null ? dte.getCPaisReceptor() : "PRY";
        try {
            gDatRec.setCPaisRec(PaisType.fromValue(codPais));
        } catch (Exception e) {
            gDatRec.setCPaisRec(PaisType.PRY);
        }
        gDatRec.setDDesPaisRe(codPais.equals("PRY") ? "Paraguay" : codPais);
        
        return gDatRec;
    }

    private TgCamItem mapearItem(ItemDocumento it) {
        TgCamItem item = factory.createTgCamItem();
        item.setDCodInt(it.getCodigo());
        item.setDDesProSer(it.getDescripcion());
        item.setCUniMed(BigInteger.valueOf(77)); // UNIDAD (UNI)
        item.setDDesUniMed(it.getUnidadMedida());
        item.setDCantProSer(BigDecimal.valueOf(it.getCantidad()));
        
        TgValorItem v = factory.createTgValorItem();
        // Montos en PYG: sin decimales según norma SIFEN v150
        v.setDPUniProSer(entero(it.getPrecioUnitario()));
        v.setDTotBruOpeItem(entero(it.getPrecioUnitario() * it.getCantidad()));
        
        TgValorRestaItem r = factory.createTgValorRestaItem();
        BigDecimal descIt = BigDecimal.valueOf(it.getMontoDescuento() != null ? it.getMontoDescuento() : 0.0);
        r.setDDescItem(entero(descIt));
        r.setDTotOpeItem(entero(it.getMontoTotalItem()));
        v.setGValorRestaItem(r);
        item.setGValorItem(v);
        
        TgCamIVA iva = factory.createTgCamIVA();
        double tasa = it.getTasaIva() != null ? it.getTasaIva() : 10.0;
        
        if (tasa > 0) {
            iva.setIAfecIVA(BigInteger.valueOf(1)); // Gravado
            iva.setDDesAfecIVA(TdDesAfecIVA.GRAVADO_IVA);
            // Base gravada sin decimales: monto / (1 + tasa%)
            BigDecimal baseGrav = BigDecimal.valueOf(it.getMontoTotalItem()).divide(
                tasa == 10.0 ? BigDecimal.valueOf(1.1) : BigDecimal.valueOf(1.05),
                0, RoundingMode.HALF_UP);
            iva.setDPropIVA(BigDecimal.valueOf(100));
            iva.setDTasaIVA(BigInteger.valueOf((long)tasa));
            iva.setDBasGravIVA(baseGrav);
            // dLiqIVAItem = monto total - base neta (complemento exacto, evita diferencias de redondeo)
            BigDecimal liqIva = entero(it.getMontoTotalItem()).subtract(baseGrav);
            iva.setDLiqIVAItem(liqIva);
            iva.setDBasExe(BigDecimal.ZERO); // Obligatorio siempre en el XSD
        } else {
            iva.setIAfecIVA(BigInteger.valueOf(3)); // Exento/No grabado
            iva.setDDesAfecIVA(TdDesAfecIVA.EXENTO);
            iva.setDPropIVA(BigDecimal.ZERO);
            iva.setDTasaIVA(BigInteger.ZERO);            // Obligatorio siempre en el XSD
            iva.setDBasGravIVA(BigDecimal.ZERO);         // Obligatorio siempre en el XSD
            iva.setDLiqIVAItem(BigDecimal.ZERO);         // Obligatorio siempre en el XSD
            // NT13: para iAfecIVA=3 (Exento), dBasExe = 0. El monto exento va en gTotSub.dSubExe
            iva.setDBasExe(BigDecimal.ZERO);
        }
        item.setGCamIVA(iva);
        
        return item;
    }

    private String mapearDesPresencia(int cod) {
        return switch (cod) {
            case 1 -> "Operación presencial";
            case 2 -> "Operación electrónica";
            case 3 -> "Operación telemarketing";
            case 4 -> "Venta a domicilio";
            case 5 -> "Operación bancaria";
            case 6 -> "Operación cíclica";
            case 9 -> "Otros";
            default -> "Operación presencial";
        };
    }


    private TdDesTiDE mapearTipoDocEnum(String tipo) {
        return switch (tipo) {
            case "1" -> TdDesTiDE.FACTURA_ELECTRONICA;
            case "4" -> TdDesTiDE.AUTOFACTURA_ELECTRONICA;
            case "5" -> TdDesTiDE.NOTA_DE_CREDITO_ELECTRONICA;
            case "6" -> TdDesTiDE.NOTA_DE_DEBITO_ELECTRONICA;
            case "7" -> TdDesTiDE.NOTA_DE_REMISION_ELECTRONICA;
            default -> TdDesTiDE.FACTURA_ELECTRONICA;
        };
    }

    private TdDesMotEmi mapearMotivoNcNd(int cod, String tipoDoc) {
        if ("6".equals(tipoDoc)) { // Nota de Débito
            return switch (cod) {
                case 1 -> TdDesMotEmi.AJUSTE_DE_PRECIO; // SIFEN v150: Intereses -> Ajuste de precio
                case 2 -> TdDesMotEmi.RECUPERO_DE_GASTO; // Gastos -> Recupero gasto
                default -> TdDesMotEmi.AJUSTE_DE_PRECIO;
            };
        }
        // Nota de Crédito (Tipo 5)
        return switch (cod) {
            case 1 -> TdDesMotEmi.DEVOLUCION_Y_AJUSTE_DE_PRECIOS;
            case 2 -> TdDesMotEmi.DEVOLUCION;
            case 3 -> TdDesMotEmi.DESCUENTO;
            case 4 -> TdDesMotEmi.BONIFICACION;
            case 5 -> TdDesMotEmi.CREDITO_INCOBRABLE;
            default -> TdDesMotEmi.DEVOLUCION_Y_AJUSTE_DE_PRECIOS;
        };
    }

    private String mapearMotivoRemision(int cod) {
        return switch (cod) {
            case 1 -> "Traslado por venta";
            case 2 -> "Traslado por consignación";
            case 3 -> "Traslado por devolución";
            case 4 -> "Traslado por compra";
            case 5 -> "Traslado por exportación";
            default -> "Traslado";
        };
    }

    /**
     * Determina el tipo de transacción SIFEN según el DTE.
     * Si el DTE especifica el tipo explícitamente, se usa ese valor.
     * Si no, asume 1 (Venta de mercadería) por defecto.
     */
    private int resolverTipoTransaccion(DocumentoElectronico dte) {
        if (dte.getTipoTransaccion() != null) {
            return dte.getTipoTransaccion();
        }
        return 1; // Solo mercadería (default)
    }

    /**
     * Convierte un valor double a BigDecimal sin decimales (escala 0).
     * Obligatorio para montos en PYG según norma SIFEN v150.
     */
    private BigDecimal entero(double valor) {
        return BigDecimal.valueOf(Math.round(valor));
    }

    /**
     * Convierte un BigDecimal a escala 0 sin decimales.
     * Retorna ZERO si el valor es nulo.
     */
    private BigDecimal entero(BigDecimal valor) {
        return valor == null ? BigDecimal.ZERO : valor.setScale(0, RoundingMode.HALF_UP);
    }
}
