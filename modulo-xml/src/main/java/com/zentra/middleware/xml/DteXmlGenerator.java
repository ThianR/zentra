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

@Service
public class DteXmlGenerator {

    private final ObjectFactory factory = new ObjectFactory();

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

            int tipoContribuyente = 2; // Por defecto Juridica
            if (dte.getEmisor().getTipoContribuyente() != null) {
                tipoContribuyente = dte.getEmisor().getTipoContribuyente();
            }

            int ambiente = dte.getAmbiente() != null ? dte.getAmbiente() : 1;
            String cdc = SifenUtil.generarCdc(tipoDoc, ruc, dv, estab, punto, nro, tipoContribuyente, fechaStr, tipoEmis, codSeg);
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
            gTimb.setDDesTiDE(TdDesTiDE.FACTURA_ELECTRÓNICA); 
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
            
            // Mapeo detallado del Receptor
            gDatGralOpe.setGDatRec(mapearReceptor(dte));
            
            de.setGDatGralOpe(gDatGralOpe);
            
            // gDtipDE (Datos específicos según tipoDoc)
            TgDtipDE gDtipDE = factory.createTgDtipDE();
            
            // Si es Factura Electrónica (tipo 1)
            if ("1".equals(tipoDoc)) {
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
            } else if ("4".equals(tipoDoc) || "5".equals(tipoDoc)) {
                // Nota de Crédito / Débito
                TgCamNCDE gCamNCDE = factory.createTgCamNCDE();
                int motId = 1;
                try {
                   if (dte.getMotivoEmision() != null && !dte.getMotivoEmision().isEmpty()) {
                       motId = Integer.parseInt(dte.getMotivoEmision());
                   }
                } catch (Exception e) {}
                gCamNCDE.setIMotEmi(String.valueOf(motId));
                gCamNCDE.setDDesMotEmi(mapearMotivoNC(motId));
                gDtipDE.setGCamNCDE(gCamNCDE);
            } else if ("7".equals(tipoDoc)) {
                // Nota de Remisión Electrónica
                if (dte.getTransporte() != null) {
                    TgCamNRE gCamNRE = factory.createTgCamNRE();
                    Transporte t = dte.getTransporte();
                    int motId = t.getMotivoTraslado() != null ? t.getMotivoTraslado() : 1;
                    gCamNRE.setIMotEmiNR(BigInteger.valueOf(motId));
                    gCamNRE.setDDesMotEmiNR(mapearMotivoRemision(motId));
                    gCamNRE.setIRespEmiNR(BigInteger.valueOf(t.getResponsableEmision() != null ? t.getResponsableEmision() : 1));
                    gCamNRE.setDDesRespEmiNR(t.getResponsableEmision() == 1 ? "Emisor" : "Receptor");
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

            // 3. Marshalling a String
            JAXBContext context = JAXBContext.newInstance("com.zentra.middleware.sifen.schema");
            Marshaller marshaller = context.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
            
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
        String depto = emisor.getDepartamendo() != null ? emisor.getDepartamendo() : "CAPITAL";
        gEmis.setDDesDepEmi(TDesDepartamento.fromValue(depto));
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
        
        // Lógica básica para el MVP: Si contiene guion, es RUC (Contribuyente)
        if (rucReceptor != null && rucReceptor.contains("-")) {
            String[] partes = rucReceptor.split("-");
            gDatRec.setINatRec(BigInteger.valueOf(1)); // Contribuyente
            gDatRec.setITiContRec(BigInteger.valueOf(2)); // Persona Jurídica (Mock)
            gDatRec.setDRucRec(partes[0]);
            gDatRec.setDDVRec(new BigInteger(partes[1]));
            gDatRec.setDNomRec(razonSocial);
        } else {
            gDatRec.setINatRec(BigInteger.valueOf(2)); // No Contribuyente
            gDatRec.setITipIDRec(BigInteger.valueOf(1)); // Cédula de Identidad
            gDatRec.setDDTipIDRec("Cédula de identidad");
            gDatRec.setDNumIDRec(rucReceptor != null ? rucReceptor : "4444444");
            gDatRec.setDNomRec(razonSocial);
        }

        gDatRec.setITiOpe(BigInteger.valueOf(1)); // B2B
        gDatRec.setCPaisRec(PaisType.PRY);
        gDatRec.setDDesPaisRe("Paraguay");
        
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
            default -> "Otro";
        };
    }

    private TdDesMotEmi mapearMotivoNC(int cod) {
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
