package com.zentra.middleware.xml;

import com.zentra.middleware.core.model.DocumentoElectronico;
import com.zentra.middleware.core.model.PagoContado;
import com.zentra.middleware.core.model.Transporte;
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
            
            // gOpeCom (Operación Comercial - OBLIGATORIO en v150 para Factura, Autofactura, N.Crédito y N.Débito)
            // No debe informarse para Notas de Remisión (7)
            int tDoc = Integer.parseInt(tipoDoc != null ? tipoDoc : "1");
            if (tDoc == 1 || tDoc == 4 || tDoc == 5 || tDoc == 6) {
                TgOpeCom gOpeCom = factory.createTgOpeCom();
                // 1 = IVA, 2 = ISC, 3 = Renta, 4 = Ninguno, 5 = IVA-Renta
                gOpeCom.setITImp(BigInteger.valueOf(1));
                gOpeCom.setDDesTImp(TdDesTImp.IVA);
                gOpeCom.setCMoneOpe(CMondT.PYG);
                gOpeCom.setDDesMoneOpe("Guarani");
                // Tipo de transacción: Obligatorio si C002 = 1, 2, 3 o 4. No informar si C002 = 5 o 6.
                if (tDoc <= 4) {
                    int tipTra = resolverTipoTransaccion(dte);
                    gOpeCom.setITipTra(tipTra);
                    gOpeCom.setDDesTipTra(TdDesTiTran.fromValue(DescripcionTipoTransaccion.getDescripcion(tipTra)));
                }
                gDatGralOpe.setGOpeCom(gOpeCom);
            }

            
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
                    // Valores exactos según XSD tdDesRespEmiNR (minLen=20, maxLen=36)
                    gCamNRE.setDDesRespEmiNR(mapearResponsableEmisionNR(respEmiNR));
                    gCamNRE.setDKmR(t.getKmsRecorrido() != null ? t.getKmsRecorrido() : 10);
                    gCamNRE.setDFecEm(dte.getFechaCreacion().toLocalDate().toString());
                    if (t.getPrecioFlete() != null) {
                        gCamNRE.setCPreFle(entero(t.getPrecioFlete()));
                    }
                    gDtipDE.setGCamNRE(gCamNRE);
                }
            }
            de.setGDtipDE(gDtipDE);
            
            // Si hay datos de transporte (gTransp)
            if (dte.getTransporte() != null) {
                Transporte t = dte.getTransporte();
                TgTransp gTranspObj = factory.createTgTransp();
                // Tipo de transporte (1=Propio, 2=Tercero) — seleccionable por el usuario
                int tipTrans = t.getTipoTransporte() != null ? t.getTipoTransporte() : 1;
                gTranspObj.setITipTrans(BigInteger.valueOf(tipTrans));
                gTranspObj.setDDesTipTrans(tipTrans == 2 ? TdDesTTrans.TERCERO : TdDesTTrans.PROPIO);
                // Modalidad de transporte (1=Terrestre por defecto — extensible)
                gTranspObj.setIModTrans(BigInteger.valueOf(1));
                gTranspObj.setDDesModTrans(TdDesModTrans.TERRESTRE);
                gTranspObj.setIRespFlete(BigInteger.valueOf(1));
                
                TgCamTrans gTrans = factory.createTgCamTrans();
                // Naturaleza del transportista (1=Contribuyente, 2=No Contribuyente)
                int natTrans = t.getNaturalezaTransportista() != null ? t.getNaturalezaTransportista() : 1;
                gTrans.setINatTrans(BigInteger.valueOf(natTrans));
                gTrans.setDNomTrans(t.getNombreTransportista() != null && !t.getNombreTransportista().isBlank() && !"null".equals(t.getNombreTransportista())
                        ? t.getNombreTransportista() : "Sin nombre");
                gTrans.setDRucTrans(t.getRucTransportista());
                gTrans.setDDVTrans(t.getDvTransportista() != null && !t.getDvTransportista().isBlank() && !"null".equals(t.getDvTransportista())
                        ? new BigInteger(t.getDvTransportista()) : null);
                String numDocChof = (t.getNumeroDocumentoChofer() == null || t.getNumeroDocumentoChofer().trim().isEmpty() || "null".equals(t.getNumeroDocumentoChofer()))
                        ? "0" : t.getNumeroDocumentoChofer().trim();
                gTrans.setDNumIDChof(numDocChof);
                gTrans.setDNomChof(t.getNombreChofer() != null && !t.getNombreChofer().trim().isEmpty() && !"null".equals(t.getNombreChofer())
                        ? t.getNombreChofer() : "Sin nombre");
                gTrans.setDDomFisc(t.getNombreTransportista() != null && !t.getNombreTransportista().isBlank() ? t.getNombreTransportista() : "Sin domicilio");
                gTrans.setDDirChof(t.getDireccionChofer() != null && !t.getDireccionChofer().trim().isEmpty() && !"null".equals(t.getDireccionChofer())
                        ? t.getDireccionChofer() : "Sin dirección");
                
                // Fecha estimada de inicio de traslado (obligatoria según SIFEN 2107)
                try {
                    String fechaIni = t.getFechaInicioTraslado();
                    if (fechaIni == null || fechaIni.isBlank() || "null".equals(fechaIni)) {
                        fechaIni = java.time.LocalDate.now().toString();
                    }
                    javax.xml.datatype.DatatypeFactory dtf = javax.xml.datatype.DatatypeFactory.newInstance();
                    gTranspObj.setDIniTras(dtf.newXMLGregorianCalendar(fechaIni));

                    String fechaFin = t.getFechaFinTraslado();
                    if (fechaFin != null && !fechaFin.isBlank() && !"null".equals(fechaFin)) {
                        gTranspObj.setDFinTras(fechaFin);
                    }
                } catch (Exception e) {
                    logger.warning("No se pudo asignar fecha de traslado: " + e.getMessage());
                }
                
                // Local de salida (gCamSal) — obligatorio según SIFEN error 2150
                TgCamSal gCamSal = factory.createTgCamSal();
                // Dirección: tomar del transporte o del emisor como fallback
                String dirSal = t.getLocalSalidaDireccion();
                if (dirSal == null || dirSal.isBlank() || "null".equals(dirSal)) {
                    dirSal = dte.getDireccionEmisor() != null ? dte.getDireccionEmisor() : "Sin dirección";
                }
                gCamSal.setDDirLocSal(dirSal);
                int numCasSal = t.getLocalSalidaNumeroCasa() != null ? t.getLocalSalidaNumeroCasa() : 0;
                gCamSal.setDNumCasSal(BigInteger.valueOf(numCasSal));
                // Departamento: tomar del transporte o del emisor como fallback
                int codDepSal = t.getLocalSalidaCodigoDepartamento() != null
                        ? t.getLocalSalidaCodigoDepartamento()
                        : (dte.getEmisor().getCodDepartamento() != null ? dte.getEmisor().getCodDepartamento() : 1);
                gCamSal.setCDepSal(BigInteger.valueOf(codDepSal));
                String desDepSal = t.getLocalSalidaDescripcionDepartamento();
                if (desDepSal == null || desDepSal.isBlank()) {
                    desDepSal = dte.getEmisor().getDepartamento() != null ? dte.getEmisor().getDepartamento() : "CAPITAL";
                }
                gCamSal.setDDesDepSal(mapearDepartamento(desDepSal));
                // Ciudad: tomar del transporte o del emisor como fallback
                int codCiuSal = t.getLocalSalidaCodigoCiudad() != null
                        ? t.getLocalSalidaCodigoCiudad()
                        : (dte.getEmisor().getCodCiudad() != null ? dte.getEmisor().getCodCiudad() : 1);
                gCamSal.setCCiuSal(BigInteger.valueOf(codCiuSal));
                String desCiuSal = t.getLocalSalidaDescripcionCiudad();
                if (desCiuSal == null || desCiuSal.isBlank()) {
                    desCiuSal = dte.getEmisor().getCiudad() != null ? dte.getEmisor().getCiudad() : "ASUNCION (DISTRITO)";
                }
                if (codCiuSal == 1 && (desCiuSal.equalsIgnoreCase("ASUNCION") || desCiuSal.equalsIgnoreCase("ASUNCION (CAPITAL)"))) {
                    desCiuSal = "ASUNCION (DISTRITO)";
                }
                gCamSal.setDDesCiuSal(desCiuSal);
                gTranspObj.setGCamSal(gCamSal);
                
                // Local de entrega (gCamEnt) — obligatorio según SIFEN error 2200
                TgCamEnt gCamEnt = factory.createTgCamEnt();
                // Dirección: tomar del transporte o del receptor como fallback
                String dirEnt = t.getLocalEntregaDireccion();
                if (dirEnt == null || dirEnt.isBlank() || "null".equals(dirEnt)) {
                    dirEnt = dte.getReceptorDireccion() != null ? dte.getReceptorDireccion() : "Sin dirección de entrega";
                }
                gCamEnt.setDDirLocEnt(dirEnt);
                
                int numCasEnt = t.getLocalEntregaNumeroCasa() != null ? t.getLocalEntregaNumeroCasa() : 0;
                if (numCasEnt == 0) {
                    try {
                        String numCasRecStr = dte.getReceptorNumeroCasa() != null ? dte.getReceptorNumeroCasa().replaceAll("\\D", "") : "0";
                        numCasEnt = numCasRecStr.isEmpty() ? 0 : Integer.parseInt(numCasRecStr);
                    } catch (Exception ignored) {}
                }
                gCamEnt.setDNumCasEnt(BigInteger.valueOf(numCasEnt));
                
                // Departamento: tomar del transporte o del receptor como fallback
                int codDepEnt = t.getLocalEntregaCodigoDepartamento() != null
                        ? t.getLocalEntregaCodigoDepartamento()
                        : (dte.getReceptorCodigoDepartamento() != null ? dte.getReceptorCodigoDepartamento() : 1);
                gCamEnt.setCDepEnt(BigInteger.valueOf(codDepEnt));
                
                String desDepEnt = t.getLocalEntregaDescripcionDepartamento();
                if (desDepEnt == null || desDepEnt.isBlank()) {
                    desDepEnt = dte.getReceptorDescripcionDepartamento() != null ? dte.getReceptorDescripcionDepartamento() : "CAPITAL";
                }
                gCamEnt.setDDesDepEnt(mapearDepartamento(desDepEnt));
                
                // Ciudad: tomar del transporte o del receptor como fallback
                int codCiuEnt = t.getLocalEntregaCodigoCiudad() != null
                        ? t.getLocalEntregaCodigoCiudad()
                        : (dte.getReceptorCodigoCiudad() != null ? dte.getReceptorCodigoCiudad() : 1);
                gCamEnt.setCCiuEnt(BigInteger.valueOf(codCiuEnt));
                
                String desCiuEnt = t.getLocalEntregaDescripcionCiudad();
                if (desCiuEnt == null || desCiuEnt.isBlank()) {
                    desCiuEnt = dte.getReceptorDescripcionCiudad() != null ? dte.getReceptorDescripcionCiudad() : "ASUNCION (DISTRITO)";
                }
                if (codCiuEnt == 1 && (desCiuEnt.equalsIgnoreCase("ASUNCION") || desCiuEnt.equalsIgnoreCase("ASUNCION (CAPITAL)"))) {
                    desCiuEnt = "ASUNCION (DISTRITO)";
                }
                gCamEnt.setDDesCiuEnt(desCiuEnt);
                gTranspObj.getGCamEnt().add(gCamEnt);

                // Datos del Vehículo de Traslado (gVehTras) — obligatorio según SIFEN error 2250
                TgVehTras gVehTras = factory.createTgVehTras();

                // Tipo de Vehículo: tomar de Transporte o "CAMION" como fallback
                String tiVeh = t.getTipoVehiculo();
                if (tiVeh == null || tiVeh.isBlank() || "null".equals(tiVeh)) {
                    tiVeh = "CAMION";
                }
                // Limitar longitud entre 4 y 10 según XSD
                if (tiVeh.length() > 10) tiVeh = tiVeh.substring(0, 10);
                if (tiVeh.length() < 4) tiVeh = String.format("%-4s", tiVeh).replace(' ', 'A');
                gVehTras.setDTiVehTras(tiVeh.toUpperCase());

                // Marca del Vehículo: tomar de Transporte o "SIN MARCA" como fallback
                String marVeh = t.getMarcaVehiculo();
                if (marVeh == null || marVeh.isBlank() || "null".equals(marVeh)) {
                    marVeh = "SIN MARCA";
                }
                if (marVeh.length() > 10) marVeh = marVeh.substring(0, 10);
                gVehTras.setDMarVeh(marVeh.toUpperCase());

                // Tipo de Identificación del Vehículo:
                // Usemos siempre 2 (Número de Matrícula) como estándar, y si hay chasis lo agregamos.
                String nroMat = t.getMatriculaVehiculo();
                if (nroMat == null || nroMat.isBlank() || "null".equals(nroMat)) {
                    nroMat = "TEST999";
                }
                nroMat = nroMat.replaceAll("[^a-zA-Z0-9]", ""); // limpiar caracteres especiales
                if (nroMat.length() > 7) nroMat = nroMat.substring(0, 7);

                gVehTras.setDTipIdenVeh(BigInteger.valueOf(2)); // 2 = Número de matrícula
                gVehTras.setDNroIDVeh(nroMat);
                gVehTras.setDNroMatVeh(nroMat);

                // Si hay un número de chasis provisto, lo agregamos en dAdicVeh (opcional)
                String chasis = t.getChasisVehiculo();
                if (chasis != null && !chasis.isBlank() && !"null".equals(chasis)) {
                    if (chasis.length() > 20) chasis = chasis.substring(0, 20);
                    gVehTras.setDAdicVeh(chasis);
                }

                gTranspObj.getGVehTras().add(gVehTras);

                gTranspObj.setGCamTrans(gTrans);
                gDtipDE.setGTransp(gTranspObj);
            }

            // Condición de venta (Solo para Facturas y Autofacturas: Tipos 1 al 4)
            if (tDoc <= 4) {
                TgCamCond gCamCond = factory.createTgCamCond();
                Integer condOpe = dte.getCondicionOperacion() != null ? dte.getCondicionOperacion() : 1;
                gCamCond.setICondOpe(BigInteger.valueOf(condOpe));
                gCamCond.setDDCondOpe(condOpe == 1 ? TdDCondOpe.CONTADO : TdDCondOpe.CRÉDITO);
                
                if (condOpe == 1) {
                    if (dte.getPagos() != null && !dte.getPagos().isEmpty()) {
                        for (PagoContado p : dte.getPagos()) {
                            TgPagCont gPagCont = factory.createTgPagCont();
                            int tipoPagoId = p.getTipoPago() != null ? p.getTipoPago() : 1;
                            gPagCont.setITiPago(BigInteger.valueOf(tipoPagoId));
                            gPagCont.setDDesTiPag(getDescripcionTipoPago(tipoPagoId));
                            gPagCont.setDMonTiPag(entero(p.getMonto() != null ? p.getMonto() : 0.0));
                            gPagCont.setCMoneTiPag(CMondT.PYG);
                            gPagCont.setDDMoneTiPag("Guarani");

                            // Indicador de modo seguro: si es null se interpreta como true
                            boolean safe = p.getSafeSecure() == null || p.getSafeSecure();

                            // Si el pago es con tarjeta (crédito=3, débito=4)
                            if (tipoPagoId == 3 || tipoPagoId == 4) {
                                TgPagTarCD gPagTarCD = factory.createTgPagTarCD();

                                // Denominación: usar dato real o fallback genérico si safeSecure
                                int den = (p.getTarjetaDenominacion() != null) ? p.getTarjetaDenominacion() : (safe ? 99 : 99);
                                gPagTarCD.setIDenTarj(BigInteger.valueOf(den));

                                // Descripción: calcular según denominación o usar dato libre del usuario
                                String desc = resolverDescripcionDenominacion(den, p.getTarjetaDescripcion(), safe);
                                gPagTarCD.setDDesDenTarj(desc);

                                // Forma de procesamiento: usar dato real o fallback POS
                                int forma = (p.getTarjetaFormaProcesamiento() != null) ? p.getTarjetaFormaProcesamiento() : (safe ? 1 : 1);
                                gPagTarCD.setIForProPa((short) forma);

                                gPagCont.setGPagTarCD(gPagTarCD);
                            }

                            // Si el pago es con cheque (tipo 2)
                            if (tipoPagoId == 2) {
                                TgPagCheq gPagCheq = factory.createTgPagCheq();

                                // Número de cheque: usar dato real o fallback genérico si safeSecure
                                String numCheq = (p.getChequeNumero() != null && !p.getChequeNumero().isBlank())
                                        ? p.getChequeNumero() : (safe ? "00000000" : "00000000");
                                gPagCheq.setDNumCheq(numCheq);

                                // Banco emisor: usar dato real o fallback genérico si safeSecure
                                String banco = (p.getChequeBanco() != null && !p.getChequeBanco().isBlank())
                                        ? p.getChequeBanco() : (safe ? "OTRO" : "OTRO");
                                gPagCheq.setDBcoEmi(banco);

                                gPagCont.setGPagCheq(gPagCheq);
                            }

                            gCamCond.getGPaConEIni().add(gPagCont);
                        }

                    } else {
                        // Fallback por si la UI no envió nada, asumimos 100% efectivo
                        TgPagCont gPagCont = factory.createTgPagCont();
                        gPagCont.setITiPago(BigInteger.valueOf(1));
                        gPagCont.setDDesTiPag("Efectivo");
                        gPagCont.setDMonTiPag(entero(dte.getTotalOperacion() != null ? dte.getTotalOperacion() : 0.0));
                        gPagCont.setCMoneTiPag(CMondT.PYG);
                        gPagCont.setDDMoneTiPag("Guarani");
                        gCamCond.getGPaConEIni().add(gPagCont);
                    }
                } else {
                    TgPagCred gPagCred = factory.createTgPagCred();
                    gPagCred.setICondCred(BigInteger.valueOf(2)); // Cuotas
                    gPagCred.setDDCondCred(TdDCondCred.CUOTA);
                    gPagCred.setDCuotas(BigInteger.valueOf(dte.getCuotas().size()));
                    
                    for (Cuota c : dte.getCuotas()) {
                        TgCuotas gCuotaStruct = factory.createTgCuotas();
                        gCuotaStruct.setCMoneCuo(CMondT.PYG);
                        gCuotaStruct.setDDMoneCuo("Guarani");
                        gCuotaStruct.setDMonCuota(entero(c.getMonto()));
                        gCuotaStruct.setDVencCuo(c.getFechaVencimiento().toString());
                        gPagCred.getGCuotas().add(gCuotaStruct);
                    }
                    gCamCond.setGPagCred(gPagCred);
                }
                gDtipDE.setGCamCond(gCamCond);
            }

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
                    TgCamItem gCamItem = mapearItem(it, tipoDoc);
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
            
            // gTotSub no es requerido/permitido en Notas de Remisión (7)
            if (!"7".equals(tipoDoc)) {
                de.setGTotSub(gTotSub);
            }

            // CAMBIO v2-5: gCamGen — nodo vacío requerido por InventivaFE aprobado en producción.
            // El XSD lo define como minOccurs="0" pero InventivaFE siempre lo incluye como <gCamGen/>.
            de.setGCamGen(factory.createTgCamGen());

            // Propagar los totales calculados al modelo DTE.
            // El firmador (XmlSignerService) los necesita para construir la URL del QR.
            if ("7".equals(tipoDoc)) {
                dte.setTotalIva(0.0);
                dte.setTotalOperacion(0.0);
            } else {
                dte.setTotalIva(totIvaAcum.doubleValue());
                dte.setTotalOperacion(totGralOpe.doubleValue());
            }

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

            // Limpiar de forma robusta cualquier decimal .0 en campos monetarios o de monto (que inician con d o c)
            // Esto evita incompatibilidades de JAXB con tipos decimales en guaraníes (PYG) ante SIFEN.
            xmlGenerated = xmlGenerated.replaceAll("<([dc][A-Za-z0-9]+)>([0-9]+)\\.0+</\\1>", "<$1>$2</$1>");

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

        // --- Dirección y Contacto del Receptor ---
        // XSD D013: dDirRec es obligatorio si iTiOpe = 4 (B2F) o si tDoc = 7 (Nota de Remisión)
        String direccion = dte.getReceptorDireccion();
        String tipoDoc = dte.getTipoDocumento();
        if ("7".equals(tipoDoc) || (direccion != null && !direccion.isBlank())) {
            gDatRec.setDDirRec(direccion != null && !direccion.isBlank() ? direccion : "Sin Direccion");
            
            // Número de Casa Dinámico
            String numCas = dte.getReceptorNumeroCasa() != null && !dte.getReceptorNumeroCasa().isBlank() 
                            ? dte.getReceptorNumeroCasa() : "0";
            try {
                // SIFEN: dNumCasRec es BigInteger (max 6 dígitos)
                gDatRec.setDNumCasRec(new BigInteger(numCas.replaceAll("\\D", ""))); 
            } catch (Exception e) {
                gDatRec.setDNumCasRec(BigInteger.ZERO);
            }
            
            // Departamento Dinámico (Fallback: 1 - CAPITAL)
            Integer codDepto = dte.getReceptorCodigoDepartamento();
            gDatRec.setCDepRec(BigInteger.valueOf(codDepto != null ? codDepto : 1));
            
            String descDepto = dte.getReceptorDescripcionDepartamento() != null 
                               ? dte.getReceptorDescripcionDepartamento() : "CAPITAL";
            try {
                // SIFEN: dDesDepRec es enum TDesDepartamento
                gDatRec.setDDesDepRec(TDesDepartamento.fromValue(descDepto.toUpperCase().trim()));
            } catch (Exception e) {
                gDatRec.setDDesDepRec(TDesDepartamento.CAPITAL);
            }
            
            // Ciudad Dinámica (Fallback: 1 - ASUNCION (DISTRITO))
            Integer codCiud = dte.getReceptorCodigoCiudad();
            int finalCodCiud = codCiud != null ? codCiud : 1;
            gDatRec.setCCiuRec(BigInteger.valueOf(finalCodCiud));
            
            String desCiuRec = dte.getReceptorDescripcionCiudad() != null ? dte.getReceptorDescripcionCiudad() : "ASUNCION (DISTRITO)";
            if (finalCodCiud == 1 && (desCiuRec.equalsIgnoreCase("ASUNCION") || desCiuRec.equalsIgnoreCase("ASUNCION (CAPITAL)"))) {
                desCiuRec = "ASUNCION (DISTRITO)";
            }
            gDatRec.setDDesCiuRec(desCiuRec);
        }

        if (dte.getReceptorTelefono() != null && !dte.getReceptorTelefono().isBlank()) {
            gDatRec.setDTelRec(dte.getReceptorTelefono());
        }
        if (dte.getReceptorEmail() != null && !dte.getReceptorEmail().isBlank()) {
            gDatRec.setDEmailRec(dte.getReceptorEmail());
        }
        
        return gDatRec;
    }

    private TgCamItem mapearItem(ItemDocumento it, String tipoDoc) {
        TgCamItem item = factory.createTgCamItem();
        item.setDCodInt(it.getCodigo());
        item.setDDesProSer(it.getDescripcion());
        item.setCUniMed(BigInteger.valueOf(77)); // UNIDAD (UNI)
        item.setDDesUniMed(it.getUnidadMedida());
        item.setDCantProSer(BigDecimal.valueOf(it.getCantidad()));
        
        // En Nota de Remisión (7) no deben ir montos ni IVA
        if (!"7".equals(tipoDoc)) {
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
        }
        
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

    /**
     * Mapea el código de responsable de emisión (iRespEmiNR) al texto exacto
     * definido en la enumeración XSD tdDesRespEmiNR (minLen=20, maxLen=36).
     * Se usan nombres genéricos ("Comprobante") en lugar de "factura"
     * para reflejar que aplica a cualquier tipo de documento.
     */
    private String mapearResponsableEmisionNR(int cod) {
        return switch (cod) {
            case 1 -> "Emisor de la factura";           // XSD: minLen=20 ✓ (22 chars)
            case 2 -> "Poseedor de la factura y bienes"; // XSD ✓ (31 chars)
            case 3 -> "Empresa transportista";           // XSD ✓ (21 chars)
            case 4 -> "Despachante de Aduanas";          // XSD ✓ (22 chars)
            case 5 -> "Agente de transporte o intermediario"; // XSD ✓ (36 chars)
            default -> "Emisor de la factura";
        };
    }

    private String mapearMotivoRemision(int cod) {
        return switch (cod) {
            // Valores exactos según XSD tdDMotivTras
            case 1  -> "Traslado por ventas";
            case 2  -> "Traslado por consignación";
            case 3  -> "Exportación";
            case 4  -> "Traslado por compra";
            case 5  -> "Importación";
            case 6  -> "Traslado por devolución";
            case 7  -> "Traslado entre locales de la empresa";
            case 8  -> "Traslado de bienes por transformación";
            case 9  -> "Traslado de bienes para reparación";
            case 10 -> "Traslado por emisor móvil";
            case 11 -> "Exhibición o Demostración";
            case 12 -> "Participación en ferias";
            case 13 -> "Traslado de encomienda";
            case 14 -> "Decomiso";
            default -> "Traslado por ventas"; // Fallback al código 1
        };
    }

    private TDesDepartamento mapearDepartamento(String dDesDep) {
        if (dDesDep == null || dDesDep.trim().isEmpty()) {
            return TDesDepartamento.CAPITAL;
        }
        String deptoClean = dDesDep.trim().toUpperCase()
                .replace("Á", "A")
                .replace("É", "E")
                .replace("Í", "I")
                .replace("Ó", "O")
                .replace("Ú", "U")
                .replace("Ñ", "N");
        try {
            return TDesDepartamento.fromValue(deptoClean);
        } catch (Exception e) {
            logger.warning("Nombre de departamento no reconocido por SIFEN: " + dDesDep + ". Usando CAPITAL.");
            if (deptoClean.contains("CENTRAL")) return TDesDepartamento.CENTRAL;
            if (deptoClean.contains("ALTO PARANA")) return TDesDepartamento.ALTO_PARANA;
            if (deptoClean.contains("PTE") || deptoClean.contains("HAYES")) return TDesDepartamento.PTE_HAYES;
            if (deptoClean.contains("SAN PEDRO")) return TDesDepartamento.SAN_PEDRO;
            return TDesDepartamento.CAPITAL;
        }
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

    private String getDescripcionTipoPago(int codigo) {
        switch (codigo) {
            case 1: return "Efectivo";
            case 2: return "Cheque";
            case 3: return "Tarjeta de crédito";
            case 4: return "Tarjeta de débito";
            case 5: return "Transferencia";
            case 6: return "Giro";
            case 7: return "Billetera electrónica";
            case 8: return "Tarjeta empresarial";
            case 9: return "Vale";
            case 10: return "Retención";
            case 11: return "Pago por anticipo";
            case 12: return "Valor fiscal";
            case 13: return "Valor comercial";
            case 14: return "Compensación";
            case 15: return "Permuta";
            case 16: return "Pago bancario";
            case 17: return "Pago Móvil";
            case 18: return "Donación";
            case 19: return "Promoción";
            case 20: return "Consumo Interno";
            case 21: return "Pago Electrónico";
            default: return "Otro";
        }
    }

    /**
     * Convierte un BigDecimal a escala 0 sin decimales.
     * Retorna ZERO si el valor es nulo.
     */
    private BigDecimal entero(BigDecimal valor) {
        return valor == null ? BigDecimal.ZERO : valor.setScale(0, RoundingMode.HALF_UP);
    }

    /**
     * Resuelve la descripción de la denominación de la tarjeta según el código SIFEN.
     * Para códigos conocidos (1-6) retorna el nombre oficial.
     * Para código 99 (Otro) usa la descripción libre del usuario o "OTRO" como fallback.
     *
     * @param codigoDenominacion  código de denominación SIFEN (1=Visa, ..., 99=Otro)
     * @param descripcionLibre    texto libre del usuario (solo aplicable cuando es 99)
     * @param safe                indica si está en modo seguro (usa fallback si está vacío)
     * @return descripción final a enviar en el XML
     */
    private String resolverDescripcionDenominacion(int codigoDenominacion, String descripcionLibre, boolean safe) {
        switch (codigoDenominacion) {
            case 1: return "VISA";
            case 2: return "MASTERCARD";
            case 3: return "AMERICAN EXPRESS";
            case 4: return "MAESTRO";
            case 5: return "PANAL";
            case 6: return "CABALL";
            default:
                // Para código 99 (Otro) o cualquier otro valor, usar la descripción libre
                if (descripcionLibre != null && !descripcionLibre.isBlank()) {
                    return descripcionLibre.trim().toUpperCase();
                }
                return safe ? "OTRO" : "OTRO";
        }
    }
}
