package com.zentra.middleware.xml;

import com.zentra.middleware.core.model.DocumentoElectronico;
import com.zentra.middleware.core.util.SifenUtil;
import java.math.BigDecimal;
import java.math.RoundingMode;
import com.zentra.middleware.sifen.schema.*;
import com.zentra.middleware.core.model.*;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.Marshaller;
import org.springframework.stereotype.Service;

import java.io.StringWriter;
import java.math.BigInteger;
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
            
            String tipoEmis = "1";
            String fechaStr = dte.getFechaCreacion().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
            String codSeg = String.format("%09d", (int)(Math.random() * 1000000000));

            int ambiente = dte.getAmbiente() != null ? dte.getAmbiente() : 1;
            String cdc = SifenUtil.generarCdc(tipoDoc, ruc, dv, estab, punto, nro, tipoEmis, fechaStr, codSeg, ambiente);
            dte.setCdc(cdc);

            // 2. Poblar objetos JAXB (Mapeo Exhaustivo)
            RDE rde = factory.createRDE();
            rde.setDVerFor(new BigInteger("150"));

            TDE de = factory.createTDE();
            de.setId(cdc);
            de.setDDVId(new BigInteger(cdc.substring(43)));
            
            // Fecha Firma (Conversion de LocalDateTime a XMLGregorianCalendar via ZonedDateTime)
            ZonedDateTime zdt = ZonedDateTime.of(dte.getFechaCreacion(), ZoneId.systemDefault());
            GregorianCalendar gcal = GregorianCalendar.from(zdt);
            XMLGregorianCalendar xmlCal = DatatypeFactory.newInstance().newXMLGregorianCalendar(gcal);
            de.setDFecFirma(xmlCal);
            de.setDSisFact(1); // Auto-impresor / Propio

            // gOpeDE (Operación)
            TgCOpeDE gOpeDE = factory.createTgCOpeDE();
            gOpeDE.setIAmb(BigInteger.valueOf(ambiente));
            gOpeDE.setDDesAmb(ambiente == 1 ? "Test" : "Produccion");
            gOpeDE.setITipEmi(new BigInteger("1")); // Normal
            gOpeDE.setDDesTipEmi(TdDesTipEmi.NORMAL);
            gOpeDE.setDCodSeg(new BigInteger(codSeg));
            de.setGOpeDE(gOpeDE);

            // gTimb (Timbrado)
            TgDTim gTimb = factory.createTgDTim();
            gTimb.setITiDE(new BigInteger(tipoDoc));
            // Descripcion segun tipo oficial de SIFEN
            gTimb.setDDesTiDE(mapearTipoDocEnum(tipoDoc)); 
            gTimb.setDNumTim(dte.getTimbrado() != null && !dte.getTimbrado().isEmpty() ? dte.getTimbrado() : "12345678");
            gTimb.setDEst(estab);
            gTimb.setDPunExp(punto);
            gTimb.setDNumDoc(nro);
            gTimb.setDFeIniT(xmlCal); // Fecha inicio timbrado
            de.setGTimb(gTimb);

            // gDatGralOpe (Datos Generales)
            TgDaGOC gDatGralOpe = factory.createTgDaGOC();
            gDatGralOpe.setDFeEmiDE(xmlCal);
            
            // Documento Asociado if present
            if (dte.getCdcDocumentoAsociado() != null) {
                TgCamDEAsoc gCamDEAsoc = factory.createTgCamDEAsoc();
                gCamDEAsoc.setITipDocAso(BigInteger.valueOf(dte.getTipoDocumentoAsociado() != null ? dte.getTipoDocumentoAsociado() : 1));
                gCamDEAsoc.setDDesTipDocAso(gCamDEAsoc.getITipDocAso().equals(BigInteger.valueOf(1)) ? TdDesTipDocAso.ELECTRÓNICO : TdDesTipDocAso.IMPRESO);
                gCamDEAsoc.setDCdCDERef(dte.getCdcDocumentoAsociado());
                de.getGCamDEAsoc().add(gCamDEAsoc);
            }
            
            // Mapeo detallado del Emisor
            gDatGralOpe.setGEmis(mapearEmisor(dte.getEmisor()));
            
            // gOpeCom (Operación Comercial - OBLIGATORIO en v150 para Factura/Autofactura)
            TgOpeCom gOpeCom = factory.createTgOpeCom();
            // 1 = IVA, 2 = ISC, 3 = Renta, 4 = Ninguno, 5 = IVA-Renta
            gOpeCom.setITImp(BigInteger.valueOf(1)); 
            gOpeCom.setDDesTImp(TdDesTImp.IVA);
            gOpeCom.setCMoneOpe(CMondT.PYG);
            gOpeCom.setDDesMoneOpe("Guaraní");
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
                gCamAE.setDDesCiuVen("Asunción");
                // Dirección del proveedor (misma que vendedor para simplificar)
                gCamAE.setDDirProv("Dir. no especificada");
                gCamAE.setCCiuProv(BigInteger.ONE);
                gCamAE.setCDepProv(BigInteger.ONE);
                gCamAE.setDDesDepProv(TDesDepartamento.CAPITAL);
                gCamAE.setDDesCiuProv("Asunción");

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
                gTrans.setDNumIDChof(t.getNumeroDocumentoChofer() != null ? t.getNumeroDocumentoChofer() : "1234567");
                gTrans.setDNomChof(t.getNombreChofer() != null ? t.getNombreChofer() : "Chofer Mock");
                gTrans.setDDomFisc("Domicilio transportista mock");
                gTrans.setDDirChof(t.getDireccionChofer() != null ? t.getDireccionChofer() : "Dir Chofer Mock");
                
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
                gPagCont.setDMonTiPag(BigDecimal.valueOf(dte.getTotalOperacion() != null ? dte.getTotalOperacion() : 0.0));
                gPagCont.setCMoneTiPag(CMondT.PYG);
                gPagCont.setDDMoneTiPag("Guaraní");
                gCamCond.getGPaConEIni().add(gPagCont);
            } else {
                TgPagCred gPagCred = factory.createTgPagCred();
                gPagCred.setICondCred(BigInteger.valueOf(2)); // Cuotas
                gPagCred.setDDCondCred(TdDCondCred.CUOTA);
                gPagCred.setDCuotas(BigInteger.valueOf(dte.getCuotas().size()));
                
                for (Cuota c : dte.getCuotas()) {
                    TgCuotas gCuotaStruct = factory.createTgCuotas();
                    gCuotaStruct.setCMoneCuo(CMondT.PYG);
                    gCuotaStruct.setDDMoneCuo("Guaraní");
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
            BigDecimal totalIva5 = BigDecimal.ZERO;
            BigDecimal totalIva10 = BigDecimal.ZERO;
            BigDecimal totalDescuentoItems = BigDecimal.ZERO;

            if (dte.getItems() != null) {
                for (ItemDocumento it : dte.getItems()) {
                    TgCamItem gCamItem = mapearItem(it);
                    gDtipDE.getGCamItem().add(gCamItem);
                    
                    totalDescuentoItems = totalDescuentoItems.add(BigDecimal.valueOf(it.getMontoDescuento() != null ? it.getMontoDescuento() : 0.0));
                    
                    // Acumuladores básicos para el MVP
                    if (it.getTasaIva() == 10.0) {
                        totalGravas10 = totalGravas10.add(BigDecimal.valueOf(it.getMontoTotalItem()));
                        totalIva10 = totalIva10.add(BigDecimal.valueOf(it.getMontoIvaItem()));
                    } else if (it.getTasaIva() == 5.0) {
                        totalGravas5 = totalGravas5.add(BigDecimal.valueOf(it.getMontoTotalItem()));
                        totalIva5 = totalIva5.add(BigDecimal.valueOf(it.getMontoIvaItem()));
                    } else {
                        totalExenta = totalExenta.add(BigDecimal.valueOf(it.getMontoTotalItem()));
                    }
                }
            }
            de.setGDtipDE(gDtipDE);

            // gTotSub (Totales)
            TgTotSub gTotSub = factory.createTgTotSub();
            gTotSub.setDSubExe(totalExenta);
            gTotSub.setDSub5(totalGravas5);
            gTotSub.setDSub10(totalGravas10);
            
            BigDecimal totOpeSinDescGlobal = totalExenta.add(totalGravas5).add(totalGravas10);
            BigDecimal descGlobal = BigDecimal.valueOf(dte.getDescuentoGlobal() != null ? dte.getDescuentoGlobal() : 0.0);
            BigDecimal porcDescGlobal = BigDecimal.valueOf(dte.getPorcentajeDescuentoGlobal() != null ? dte.getPorcentajeDescuentoGlobal() : 0.0);
            
            gTotSub.setDTotOpe(totOpeSinDescGlobal);
            gTotSub.setDTotDesc(totalDescuentoItems); // Descuentos por ítem
            gTotSub.setDTotDescGlotem(BigDecimal.ZERO);
            gTotSub.setDTotAntItem(BigDecimal.ZERO);
            gTotSub.setDTotAnt(BigDecimal.ZERO);
            gTotSub.setDPorcDescTotal(porcDescGlobal);
            gTotSub.setDDescTotal(descGlobal);
            gTotSub.setDAnticipo(BigDecimal.ZERO);
            gTotSub.setDRedon(BigDecimal.ZERO);
            
            BigDecimal totGralOpe = totOpeSinDescGlobal.subtract(descGlobal);
            gTotSub.setDTotGralOpe(totGralOpe);
            
            BigDecimal totIva = totalIva10.add(totalIva5);

            gTotSub.setDIVA5(totalIva5);
            gTotSub.setDIVA10(totalIva10);
            gTotSub.setDLiqTotIVA5(totalIva5);
            gTotSub.setDLiqTotIVA10(totalIva10);
            gTotSub.setDTotIVA(totIva);
            
            // Cálculos oficiales de bases imponibles según SIFEN (monto / 1.1 o 1.05)
            BigDecimal base5 = totalGravas5.compareTo(BigDecimal.ZERO) > 0 ? 
                totalGravas5.divide(BigDecimal.valueOf(1.05), 0, RoundingMode.HALF_UP) : BigDecimal.ZERO;
            BigDecimal base10 = totalGravas10.compareTo(BigDecimal.ZERO) > 0 ? 
                totalGravas10.divide(BigDecimal.valueOf(1.1), 0, RoundingMode.HALF_UP) : BigDecimal.ZERO;
            
            gTotSub.setDBaseGrav5(base5);
            gTotSub.setDBaseGrav10(base10);
            gTotSub.setDTBasGraIVA(base5.add(base10));
            gTotSub.setDTotalGs(totGralOpe);
            
            de.setGTotSub(gTotSub);

            rde.setDE(de);
            rde.setGCamFuFD(factory.createTgCamFuFD());

            // 3. Marshalling a String con configuración para evitar prefijos ns2
            Marshaller marshaller = jaxbContext.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
            marshaller.setProperty(Marshaller.JAXB_SCHEMA_LOCATION, "http://ekuatia.set.gov.py/sifen/xsd siRecepDE_v150.xsd");
            
            StringWriter sw = new StringWriter();
            marshaller.marshal(factory.createRDE(rde), sw);
            String xmlGenerated = sw.toString();
            dte.setXmlGenerado(xmlGenerated);
            return xmlGenerated;

        } catch (Exception e) {
            throw new RuntimeException("Error en JAXB Mapping SIFEN: " + e.getMessage(), e);
        }
    }

    private TgEmis mapearEmisor(Empresa emisor) {
        TgEmis gEmis = factory.createTgEmis();
        gEmis.setDRucEm(emisor.getRuc());
        gEmis.setDDVEmi(new BigInteger(emisor.getDv() != null ? emisor.getDv() : "0"));
        gEmis.setITipCont(BigInteger.valueOf(emisor.getTipoContribuyente() != null ? emisor.getTipoContribuyente() : 2));
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
        gEmis.setCDisEmi(null); // Opcional
        
        gEmis.setCCiuEmi(emisor.getCodCiudad() != null ? emisor.getCodCiudad() : 1);
        gEmis.setDDesCiuEmi(emisor.getCiudad() != null ? emisor.getCiudad() : "ASUNCION");
        
        gEmis.setDTelEmi(emisor.getTelefono() != null ? emisor.getTelefono() : "021000000");
        gEmis.setDEmailE(emisor.getEmail() != null ? emisor.getEmail() : "emisor@example.com");

        // Actividad Económica (Requerido al menos 1 por SIFEN)
        TgActEco gActEco = factory.createTgActEco();
        gActEco.setCActEco("62010"); // Consultoría informática (String en el esquema)
        gActEco.setDDesActEco("Consultoría informática");
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
                String rucSolo = rucReceptor.split("-")[0];
                String dvRec = rucReceptor.contains("-") ? rucReceptor.split("-")[1] : "0";
                
                gDatRec.setINatRec(BigInteger.valueOf(1)); // Contribuyente
                gDatRec.setITiContRec(BigInteger.valueOf(2)); // Persona Jurídica (Generalmente se asume si es RUC)
                gDatRec.setDRucRec(rucSolo);
                gDatRec.setDDVRec(new BigInteger(dvRec));
                gDatRec.setDNomRec(razonSocial);
            } else {
                gDatRec.setINatRec(BigInteger.valueOf(2)); // No Contribuyente
                gDatRec.setITipIDRec(BigInteger.valueOf(1)); // Cédula de Identidad Parche
                gDatRec.setDDTipIDRec("Cédula de identidad");
                gDatRec.setDNumIDRec(rucReceptor);
                gDatRec.setDNomRec(razonSocial);
            }
        } else {
            // Consumidor Final
            gDatRec.setINatRec(BigInteger.valueOf(2));
            gDatRec.setITipIDRec(BigInteger.valueOf(1));
            gDatRec.setDDTipIDRec("Cédula de identidad");
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
        v.setDPUniProSer(BigDecimal.valueOf(it.getPrecioUnitario()));
        v.setDTotBruOpeItem(BigDecimal.valueOf(it.getPrecioUnitario() * it.getCantidad()));
        
        TgValorRestaItem r = factory.createTgValorRestaItem();
        BigDecimal descIt = BigDecimal.valueOf(it.getMontoDescuento() != null ? it.getMontoDescuento() : 0.0);
        r.setDDescItem(descIt);
        r.setDTotOpeItem(BigDecimal.valueOf(it.getMontoTotalItem()));
        v.setGValorRestaItem(r);
        item.setGValorItem(v);
        
        TgCamIVA iva = factory.createTgCamIVA();
        double tasa = it.getTasaIva() != null ? it.getTasaIva() : 10.0;
        
        if (tasa > 0) {
            iva.setIAfecIVA(BigInteger.valueOf(1)); // Gravado
            iva.setDDesAfecIVA(TdDesAfecIVA.GRAVADO_IVA);
            iva.setDBasGravIVA(BigDecimal.valueOf(it.getMontoTotalItem()).divide(
                tasa == 10.0 ? BigDecimal.valueOf(1.1) : BigDecimal.valueOf(1.05), 
                2, RoundingMode.HALF_UP));
            iva.setDBasExe(BigDecimal.ZERO);
        } else {
            iva.setIAfecIVA(BigInteger.valueOf(3)); // Exento/No grabado
            iva.setDDesAfecIVA(TdDesAfecIVA.EXENTO);
            iva.setDBasGravIVA(BigDecimal.ZERO);
            iva.setDBasExe(BigDecimal.valueOf(it.getMontoTotalItem()));
        }
        iva.setDPropIVA(BigDecimal.valueOf(100));
        iva.setDTasaIVA(BigInteger.valueOf((long)tasa));
        iva.setDLiqIVAItem(BigDecimal.valueOf(it.getMontoIvaItem() != null ? it.getMontoIvaItem() : 0.0));
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
            case "1" -> TdDesTiDE.FACTURA_ELECTRÓNICA;
            case "4" -> TdDesTiDE.AUTOFACTURA_ELECTRÓNICA;
            case "5" -> TdDesTiDE.NOTA_DE_CRÉDITO_ELECTRÓNICA;
            case "6" -> TdDesTiDE.NOTA_DE_DÉBITO_ELECTRÓNICA;
            case "7" -> TdDesTiDE.NOTA_DE_REMISIÓN_ELECTRÓNICA;
            default -> TdDesTiDE.FACTURA_ELECTRÓNICA;
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
            case 1 -> TdDesMotEmi.DEVOLUCIÓN_Y_AJUSTE_DE_PRECIOS;
            case 2 -> TdDesMotEmi.DEVOLUCIÓN;
            case 3 -> TdDesMotEmi.DESCUENTO;
            case 4 -> TdDesMotEmi.BONIFICACIÓN;
            case 5 -> TdDesMotEmi.CRÉDITO_INCOBRABLE;
            default -> TdDesMotEmi.DEVOLUCIÓN_Y_AJUSTE_DE_PRECIOS;
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
}
