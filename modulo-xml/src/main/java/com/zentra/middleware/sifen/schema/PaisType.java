//
// Este archivo ha sido generado por Eclipse Implementation of JAXB v3.0.0 
// Visite https://eclipse-ee4j.github.io/jaxb-ri 
// Todas las modificaciones realizadas en este archivo se perderán si se vuelve a compilar el esquema de origen. 
// Generado el: 2026.03.19 a las 11:22:40 PM PYT 
//


package com.zentra.middleware.sifen.schema;

import jakarta.xml.bind.annotation.XmlEnum;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Clase Java para paisType.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * <pre>
 * &lt;simpleType name="paisType"&gt;
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *     &lt;enumeration value="MKD"/&gt;
 *     &lt;enumeration value="TWN"/&gt;
 *     &lt;enumeration value="DZA"/&gt;
 *     &lt;enumeration value="EGY"/&gt;
 *     &lt;enumeration value="LBY"/&gt;
 *     &lt;enumeration value="MAR"/&gt;
 *     &lt;enumeration value="SDN"/&gt;
 *     &lt;enumeration value="TUN"/&gt;
 *     &lt;enumeration value="ESH"/&gt;
 *     &lt;enumeration value="IOT"/&gt;
 *     &lt;enumeration value="BDI"/&gt;
 *     &lt;enumeration value="COM"/&gt;
 *     &lt;enumeration value="DJI"/&gt;
 *     &lt;enumeration value="ERI"/&gt;
 *     &lt;enumeration value="ETH"/&gt;
 *     &lt;enumeration value="ATF"/&gt;
 *     &lt;enumeration value="KEN"/&gt;
 *     &lt;enumeration value="MDG"/&gt;
 *     &lt;enumeration value="MWI"/&gt;
 *     &lt;enumeration value="MUS"/&gt;
 *     &lt;enumeration value="MYT"/&gt;
 *     &lt;enumeration value="MOZ"/&gt;
 *     &lt;enumeration value="REU"/&gt;
 *     &lt;enumeration value="RWA"/&gt;
 *     &lt;enumeration value="SYC"/&gt;
 *     &lt;enumeration value="SOM"/&gt;
 *     &lt;enumeration value="SSD"/&gt;
 *     &lt;enumeration value="UGA"/&gt;
 *     &lt;enumeration value="TZA"/&gt;
 *     &lt;enumeration value="ZMB"/&gt;
 *     &lt;enumeration value="ZWE"/&gt;
 *     &lt;enumeration value="AGO"/&gt;
 *     &lt;enumeration value="CMR"/&gt;
 *     &lt;enumeration value="CAF"/&gt;
 *     &lt;enumeration value="TCD"/&gt;
 *     &lt;enumeration value="COG"/&gt;
 *     &lt;enumeration value="COD"/&gt;
 *     &lt;enumeration value="GNQ"/&gt;
 *     &lt;enumeration value="GAB"/&gt;
 *     &lt;enumeration value="STP"/&gt;
 *     &lt;enumeration value="BWA"/&gt;
 *     &lt;enumeration value="LSO"/&gt;
 *     &lt;enumeration value="NAM"/&gt;
 *     &lt;enumeration value="ZAF"/&gt;
 *     &lt;enumeration value="SWZ"/&gt;
 *     &lt;enumeration value="BEN"/&gt;
 *     &lt;enumeration value="BFA"/&gt;
 *     &lt;enumeration value="CPV"/&gt;
 *     &lt;enumeration value="CIV"/&gt;
 *     &lt;enumeration value="GMB"/&gt;
 *     &lt;enumeration value="GHA"/&gt;
 *     &lt;enumeration value="GIN"/&gt;
 *     &lt;enumeration value="GNB"/&gt;
 *     &lt;enumeration value="LBR"/&gt;
 *     &lt;enumeration value="MLI"/&gt;
 *     &lt;enumeration value="MRT"/&gt;
 *     &lt;enumeration value="NER"/&gt;
 *     &lt;enumeration value="NGA"/&gt;
 *     &lt;enumeration value="SHN"/&gt;
 *     &lt;enumeration value="SEN"/&gt;
 *     &lt;enumeration value="SLE"/&gt;
 *     &lt;enumeration value="TGO"/&gt;
 *     &lt;enumeration value="AIA"/&gt;
 *     &lt;enumeration value="ATG"/&gt;
 *     &lt;enumeration value="ABW"/&gt;
 *     &lt;enumeration value="BHS"/&gt;
 *     &lt;enumeration value="BRB"/&gt;
 *     &lt;enumeration value="BES"/&gt;
 *     &lt;enumeration value="VGB"/&gt;
 *     &lt;enumeration value="CYM"/&gt;
 *     &lt;enumeration value="CUB"/&gt;
 *     &lt;enumeration value="CUW"/&gt;
 *     &lt;enumeration value="DMA"/&gt;
 *     &lt;enumeration value="DOM"/&gt;
 *     &lt;enumeration value="GRD"/&gt;
 *     &lt;enumeration value="GLP"/&gt;
 *     &lt;enumeration value="HTI"/&gt;
 *     &lt;enumeration value="JAM"/&gt;
 *     &lt;enumeration value="MTQ"/&gt;
 *     &lt;enumeration value="MSR"/&gt;
 *     &lt;enumeration value="PRI"/&gt;
 *     &lt;enumeration value="BLM"/&gt;
 *     &lt;enumeration value="KNA"/&gt;
 *     &lt;enumeration value="LCA"/&gt;
 *     &lt;enumeration value="MAF"/&gt;
 *     &lt;enumeration value="VCT"/&gt;
 *     &lt;enumeration value="SXM"/&gt;
 *     &lt;enumeration value="TTO"/&gt;
 *     &lt;enumeration value="TCA"/&gt;
 *     &lt;enumeration value="VIR"/&gt;
 *     &lt;enumeration value="BLZ"/&gt;
 *     &lt;enumeration value="CRI"/&gt;
 *     &lt;enumeration value="SLV"/&gt;
 *     &lt;enumeration value="GTM"/&gt;
 *     &lt;enumeration value="HND"/&gt;
 *     &lt;enumeration value="MEX"/&gt;
 *     &lt;enumeration value="NIC"/&gt;
 *     &lt;enumeration value="PAN"/&gt;
 *     &lt;enumeration value="ARG"/&gt;
 *     &lt;enumeration value="BOL"/&gt;
 *     &lt;enumeration value="BRA"/&gt;
 *     &lt;enumeration value="CHL"/&gt;
 *     &lt;enumeration value="COL"/&gt;
 *     &lt;enumeration value="ECU"/&gt;
 *     &lt;enumeration value="FLK"/&gt;
 *     &lt;enumeration value="GUF"/&gt;
 *     &lt;enumeration value="GUY"/&gt;
 *     &lt;enumeration value="PRY"/&gt;
 *     &lt;enumeration value="PER"/&gt;
 *     &lt;enumeration value="SGS"/&gt;
 *     &lt;enumeration value="SUR"/&gt;
 *     &lt;enumeration value="URY"/&gt;
 *     &lt;enumeration value="VEN"/&gt;
 *     &lt;enumeration value="BMU"/&gt;
 *     &lt;enumeration value="CAN"/&gt;
 *     &lt;enumeration value="GRL"/&gt;
 *     &lt;enumeration value="SPM"/&gt;
 *     &lt;enumeration value="USA"/&gt;
 *     &lt;enumeration value="ATA"/&gt;
 *     &lt;enumeration value="KAZ"/&gt;
 *     &lt;enumeration value="KGZ"/&gt;
 *     &lt;enumeration value="TJK"/&gt;
 *     &lt;enumeration value="TKM"/&gt;
 *     &lt;enumeration value="UZB"/&gt;
 *     &lt;enumeration value="CHN"/&gt;
 *     &lt;enumeration value="HKG"/&gt;
 *     &lt;enumeration value="MAC"/&gt;
 *     &lt;enumeration value="PRK"/&gt;
 *     &lt;enumeration value="JPN"/&gt;
 *     &lt;enumeration value="MNG"/&gt;
 *     &lt;enumeration value="KOR"/&gt;
 *     &lt;enumeration value="BRN"/&gt;
 *     &lt;enumeration value="KHM"/&gt;
 *     &lt;enumeration value="IDN"/&gt;
 *     &lt;enumeration value="LAO"/&gt;
 *     &lt;enumeration value="MYS"/&gt;
 *     &lt;enumeration value="MMR"/&gt;
 *     &lt;enumeration value="PHL"/&gt;
 *     &lt;enumeration value="SGP"/&gt;
 *     &lt;enumeration value="THA"/&gt;
 *     &lt;enumeration value="TLS"/&gt;
 *     &lt;enumeration value="VNM"/&gt;
 *     &lt;enumeration value="AFG"/&gt;
 *     &lt;enumeration value="BGD"/&gt;
 *     &lt;enumeration value="BTN"/&gt;
 *     &lt;enumeration value="IND"/&gt;
 *     &lt;enumeration value="IRN"/&gt;
 *     &lt;enumeration value="MDV"/&gt;
 *     &lt;enumeration value="NPL"/&gt;
 *     &lt;enumeration value="PAK"/&gt;
 *     &lt;enumeration value="LKA"/&gt;
 *     &lt;enumeration value="ARM"/&gt;
 *     &lt;enumeration value="AZE"/&gt;
 *     &lt;enumeration value="BHR"/&gt;
 *     &lt;enumeration value="CYP"/&gt;
 *     &lt;enumeration value="GEO"/&gt;
 *     &lt;enumeration value="IRQ"/&gt;
 *     &lt;enumeration value="ISR"/&gt;
 *     &lt;enumeration value="JOR"/&gt;
 *     &lt;enumeration value="KWT"/&gt;
 *     &lt;enumeration value="LBN"/&gt;
 *     &lt;enumeration value="OMN"/&gt;
 *     &lt;enumeration value="QAT"/&gt;
 *     &lt;enumeration value="SAU"/&gt;
 *     &lt;enumeration value="PSE"/&gt;
 *     &lt;enumeration value="SYR"/&gt;
 *     &lt;enumeration value="TUR"/&gt;
 *     &lt;enumeration value="ARE"/&gt;
 *     &lt;enumeration value="YEM"/&gt;
 *     &lt;enumeration value="BLR"/&gt;
 *     &lt;enumeration value="BGR"/&gt;
 *     &lt;enumeration value="CZE"/&gt;
 *     &lt;enumeration value="HUN"/&gt;
 *     &lt;enumeration value="POL"/&gt;
 *     &lt;enumeration value="MDA"/&gt;
 *     &lt;enumeration value="ROU"/&gt;
 *     &lt;enumeration value="RUS"/&gt;
 *     &lt;enumeration value="SVK"/&gt;
 *     &lt;enumeration value="UKR"/&gt;
 *     &lt;enumeration value="ALA"/&gt;
 *     &lt;enumeration value="GGY"/&gt;
 *     &lt;enumeration value="JEY"/&gt;
 *     &lt;enumeration value="DNK"/&gt;
 *     &lt;enumeration value="EST"/&gt;
 *     &lt;enumeration value="FRO"/&gt;
 *     &lt;enumeration value="FIN"/&gt;
 *     &lt;enumeration value="ISL"/&gt;
 *     &lt;enumeration value="IRL"/&gt;
 *     &lt;enumeration value="IMN"/&gt;
 *     &lt;enumeration value="LVA"/&gt;
 *     &lt;enumeration value="LTU"/&gt;
 *     &lt;enumeration value="NOR"/&gt;
 *     &lt;enumeration value="SJM"/&gt;
 *     &lt;enumeration value="SWE"/&gt;
 *     &lt;enumeration value="GBR"/&gt;
 *     &lt;enumeration value="ALB"/&gt;
 *     &lt;enumeration value="AND"/&gt;
 *     &lt;enumeration value="BIH"/&gt;
 *     &lt;enumeration value="HRV"/&gt;
 *     &lt;enumeration value="GIB"/&gt;
 *     &lt;enumeration value="GRC"/&gt;
 *     &lt;enumeration value="VAT"/&gt;
 *     &lt;enumeration value="ITA"/&gt;
 *     &lt;enumeration value="MLT"/&gt;
 *     &lt;enumeration value="MNE"/&gt;
 *     &lt;enumeration value="PRT"/&gt;
 *     &lt;enumeration value="SMR"/&gt;
 *     &lt;enumeration value="SRB"/&gt;
 *     &lt;enumeration value="SVN"/&gt;
 *     &lt;enumeration value="ESP"/&gt;
 *     &lt;enumeration value="MKD"/&gt;
 *     &lt;enumeration value="AUT"/&gt;
 *     &lt;enumeration value="BEL"/&gt;
 *     &lt;enumeration value="FRA"/&gt;
 *     &lt;enumeration value="DEU"/&gt;
 *     &lt;enumeration value="LIE"/&gt;
 *     &lt;enumeration value="LUX"/&gt;
 *     &lt;enumeration value="MCO"/&gt;
 *     &lt;enumeration value="NLD"/&gt;
 *     &lt;enumeration value="CHE"/&gt;
 *     &lt;enumeration value="AUS"/&gt;
 *     &lt;enumeration value="CXR"/&gt;
 *     &lt;enumeration value="CCK"/&gt;
 *     &lt;enumeration value="HMD"/&gt;
 *     &lt;enumeration value="NZL"/&gt;
 *     &lt;enumeration value="NFK"/&gt;
 *     &lt;enumeration value="FJI"/&gt;
 *     &lt;enumeration value="NCL"/&gt;
 *     &lt;enumeration value="PNG"/&gt;
 *     &lt;enumeration value="SLB"/&gt;
 *     &lt;enumeration value="VUT"/&gt;
 *     &lt;enumeration value="GUM"/&gt;
 *     &lt;enumeration value="KIR"/&gt;
 *     &lt;enumeration value="MHL"/&gt;
 *     &lt;enumeration value="FSM"/&gt;
 *     &lt;enumeration value="NRU"/&gt;
 *     &lt;enumeration value="MNP"/&gt;
 *     &lt;enumeration value="PLW"/&gt;
 *     &lt;enumeration value="UMI"/&gt;
 *     &lt;enumeration value="ASM"/&gt;
 *     &lt;enumeration value="COK"/&gt;
 *     &lt;enumeration value="PYF"/&gt;
 *     &lt;enumeration value="NIU"/&gt;
 *     &lt;enumeration value="PCN"/&gt;
 *     &lt;enumeration value="WSM"/&gt;
 *     &lt;enumeration value="TKL"/&gt;
 *     &lt;enumeration value="TON"/&gt;
 *     &lt;enumeration value="TUV"/&gt;
 *     &lt;enumeration value="WLF"/&gt;
 *     &lt;enumeration value="NN"/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
 * </pre>
 * 
 */
@XmlType(name = "paisType")
@XmlEnum
public enum PaisType {


    /**
     * Macedonia del Norte
     * 
     */
    MKD,

    /**
     * Taiwán (Provincia de China)
     * 
     */
    TWN,

    /**
     * Argelia
     * 
     */
    DZA,

    /**
     * Egipto
     * 
     */
    EGY,

    /**
     * Libia
     * 
     */
    LBY,

    /**
     * Marruecos
     * 
     */
    MAR,

    /**
     * Sudán
     * 
     */
    SDN,

    /**
     * Túnez
     * 
     */
    TUN,

    /**
     * Sáhara Occidental
     * 
     */
    ESH,

    /**
     * Territorio Británico del Océano Índico
     * 
     */
    IOT,

    /**
     * Burundi
     * 
     */
    BDI,

    /**
     * Comoras
     * 
     */
    COM,

    /**
     * Djibouti
     * 
     */
    DJI,

    /**
     * Eritrea
     * 
     */
    ERI,

    /**
     * Etiopía
     * 
     */
    ETH,

    /**
     * Territorio de las Tierras Australes Francesas
     * 
     */
    ATF,

    /**
     * Kenya
     * 
     */
    KEN,

    /**
     * Madagascar
     * 
     */
    MDG,

    /**
     * Malawi
     * 
     */
    MWI,

    /**
     * Mauricio
     * 
     */
    MUS,

    /**
     * Mayotte
     * 
     */
    MYT,

    /**
     * Mozambique
     * 
     */
    MOZ,

    /**
     * Reunión
     * 
     */
    REU,

    /**
     * Rwanda
     * 
     */
    RWA,

    /**
     * Seychelles
     * 
     */
    SYC,

    /**
     * Somalia
     * 
     */
    SOM,

    /**
     * Sudán del Sur
     * 
     */
    SSD,

    /**
     * Uganda
     * 
     */
    UGA,

    /**
     * República Unida de Tanzanía
     * 
     */
    TZA,

    /**
     * Zambia
     * 
     */
    ZMB,

    /**
     * Zimbabwe
     * 
     */
    ZWE,

    /**
     * Angola
     * 
     */
    AGO,

    /**
     * Camerún
     * 
     */
    CMR,

    /**
     * República Centroafricana
     * 
     */
    CAF,

    /**
     * Chad
     * 
     */
    TCD,

    /**
     * Congo
     * 
     */
    COG,

    /**
     * República Democrática del Congo
     * 
     */
    COD,

    /**
     * Guinea Ecuatorial
     * 
     */
    GNQ,

    /**
     * Gabón
     * 
     */
    GAB,

    /**
     * Santo Tomé y Príncipe
     * 
     */
    STP,

    /**
     * Botswana
     * 
     */
    BWA,

    /**
     * Lesotho
     * 
     */
    LSO,

    /**
     * Namibia
     * 
     */
    NAM,

    /**
     * Sudáfrica
     * 
     */
    ZAF,

    /**
     * Swazilandia
     * 
     */
    SWZ,

    /**
     * Benin
     * 
     */
    BEN,

    /**
     * Burkina Faso
     * 
     */
    BFA,

    /**
     * Cabo Verde
     * 
     */
    CPV,

    /**
     * Côte d'Ivoire
     * 
     */
    CIV,

    /**
     * Gambia
     * 
     */
    GMB,

    /**
     * Ghana
     * 
     */
    GHA,

    /**
     * Guinea
     * 
     */
    GIN,

    /**
     * Guinea-Bissau
     * 
     */
    GNB,

    /**
     * Liberia
     * 
     */
    LBR,

    /**
     * Malí
     * 
     */
    MLI,

    /**
     * Mauritania
     * 
     */
    MRT,

    /**
     * Níger
     * 
     */
    NER,

    /**
     * Nigeria
     * 
     */
    NGA,

    /**
     * Santa Elena
     * 
     */
    SHN,

    /**
     * Senegal
     * 
     */
    SEN,

    /**
     * Sierra Leona
     * 
     */
    SLE,

    /**
     * Togo
     * 
     */
    TGO,

    /**
     * Anguila
     * 
     */
    AIA,

    /**
     * Antigua y Barbuda
     * 
     */
    ATG,

    /**
     * Aruba
     * 
     */
    ABW,

    /**
     * Bahamas
     * 
     */
    BHS,

    /**
     * Barbados
     * 
     */
    BRB,

    /**
     * Bonaire, San Eustaquio y Saba
     * 
     */
    BES,

    /**
     * Islas Vírgenes Británicas
     * 
     */
    VGB,

    /**
     * Islas Caimán
     * 
     */
    CYM,

    /**
     * CUBA
     * 
     */
    CUB,

    /**
     * Curaçao
     * 
     */
    CUW,

    /**
     * Dominica
     * 
     */
    DMA,

    /**
     * República Dominicana
     * 
     */
    DOM,

    /**
     * Granada
     * 
     */
    GRD,

    /**
     * Guadalupe
     * 
     */
    GLP,

    /**
     * Haití
     * 
     */
    HTI,

    /**
     * Jamaica
     * 
     */
    JAM,

    /**
     * Martinica
     * 
     */
    MTQ,

    /**
     * Montserrat
     * 
     */
    MSR,

    /**
     * Puerto Rico
     * 
     */
    PRI,

    /**
     * San Bartolomé
     * 
     */
    BLM,

    /**
     * Saint Kitts y Nevis
     * 
     */
    KNA,

    /**
     * Santa Lucía
     * 
     */
    LCA,

    /**
     * San Martín (parte francesa)
     * 
     */
    MAF,

    /**
     * San Vicente y las Granadinas
     * 
     */
    VCT,

    /**
     * San Martín (parte holandés)
     * 
     */
    SXM,

    /**
     * Trinidad y Tabago
     * 
     */
    TTO,

    /**
     * Islas Turcas y Caicos
     * 
     */
    TCA,

    /**
     * Islas Vírgenes de los Estados Unidos
     * 
     */
    VIR,

    /**
     * Belice
     * 
     */
    BLZ,

    /**
     * Costa Rica
     * 
     */
    CRI,

    /**
     * El Salvador
     * 
     */
    SLV,

    /**
     * Guatemala
     * 
     */
    GTM,

    /**
     * Honduras
     * 
     */
    HND,

    /**
     * México
     * 
     */
    MEX,

    /**
     * Nicaragua
     * 
     */
    NIC,

    /**
     * Panamá
     * 
     */
    PAN,

    /**
     * Argentina
     * 
     */
    ARG,

    /**
     * Bolivia (Estado Plurinacional de)
     * 
     */
    BOL,

    /**
     * Brasil
     * 
     */
    BRA,

    /**
     * Chile
     * 
     */
    CHL,

    /**
     * Colombia
     * 
     */
    COL,

    /**
     * Ecuador
     * 
     */
    ECU,

    /**
     * Islas Malvinas (Falkland)
     * 
     */
    FLK,

    /**
     * Guayana Francesa
     * 
     */
    GUF,

    /**
     * Guyana
     * 
     */
    GUY,

    /**
     * Paraguay
     * 
     */
    PRY,

    /**
     * Perú
     * 
     */
    PER,

    /**
     * Georgia del Sur y las Islas Sandwich del Sur
     * 
     */
    SGS,

    /**
     * Suriname
     * 
     */
    SUR,

    /**
     * Uruguay
     * 
     */
    URY,

    /**
     * Venezuela (República Bolivariana de)
     * 
     */
    VEN,

    /**
     * Bermuda
     * 
     */
    BMU,

    /**
     * Canadá
     * 
     */
    CAN,

    /**
     * Groenlandia
     * 
     */
    GRL,

    /**
     * Saint Pierre y Miquelon
     * 
     */
    SPM,

    /**
     * Estados Unidos de América
     * 
     */
    USA,

    /**
     * Antártida
     * 
     */
    ATA,

    /**
     * Kazajstán
     * 
     */
    KAZ,

    /**
     * Kirguistán
     * 
     */
    KGZ,

    /**
     * Tayikistán
     * 
     */
    TJK,

    /**
     * Turkmenistán
     * 
     */
    TKM,

    /**
     * Uzbekistán
     * 
     */
    UZB,

    /**
     * China
     * 
     */
    CHN,

    /**
     * Hong Kong
     * 
     */
    HKG,

    /**
     * Macao
     * 
     */
    MAC,

    /**
     * República Popular Democrática de Corea
     * 
     */
    PRK,

    /**
     * Japón
     * 
     */
    JPN,

    /**
     * Mongolia
     * 
     */
    MNG,

    /**
     * República de Corea
     * 
     */
    KOR,

    /**
     * Brunei Darussalam
     * 
     */
    BRN,

    /**
     * Camboya
     * 
     */
    KHM,

    /**
     * Indonesia
     * 
     */
    IDN,

    /**
     * República Democrática Popular Lao
     * 
     */
    LAO,

    /**
     * Malasia
     * 
     */
    MYS,

    /**
     * Myanmar
     * 
     */
    MMR,

    /**
     * Filipinas
     * 
     */
    PHL,

    /**
     * Singapur
     * 
     */
    SGP,

    /**
     * Tailandia
     * 
     */
    THA,

    /**
     * Timor-Leste
     * 
     */
    TLS,

    /**
     * Viet Nam
     * 
     */
    VNM,

    /**
     * Afganistán
     * 
     */
    AFG,

    /**
     * Bangladesh
     * 
     */
    BGD,

    /**
     * Bhután
     * 
     */
    BTN,

    /**
     * India
     * 
     */
    IND,

    /**
     * Irán (República Islámica del)
     * 
     */
    IRN,

    /**
     * Maldivas
     * 
     */
    MDV,

    /**
     * Nepal
     * 
     */
    NPL,

    /**
     * Pakistán
     * 
     */
    PAK,

    /**
     * Sri Lanka
     * 
     */
    LKA,

    /**
     * Armenia
     * 
     */
    ARM,

    /**
     * Azerbaiyán
     * 
     */
    AZE,

    /**
     * Bahrein
     * 
     */
    BHR,

    /**
     * Chipre
     * 
     */
    CYP,

    /**
     * Georgia
     * 
     */
    GEO,

    /**
     * Iraq
     * 
     */
    IRQ,

    /**
     * Israel
     * 
     */
    ISR,

    /**
     * Jordania
     * 
     */
    JOR,

    /**
     * Kuwait
     * 
     */
    KWT,

    /**
     * Líbano
     * 
     */
    LBN,

    /**
     * Omán
     * 
     */
    OMN,

    /**
     * Qatar
     * 
     */
    QAT,

    /**
     * Arabia Saudita
     * 
     */
    SAU,

    /**
     * Estado de Palestina
     * 
     */
    PSE,

    /**
     * República Árabe Siria
     * 
     */
    SYR,

    /**
     * Turquía
     * 
     */
    TUR,

    /**
     * Emiratos Árabes Unidos
     * 
     */
    ARE,

    /**
     * Yemen
     * 
     */
    YEM,

    /**
     * Belarús
     * 
     */
    BLR,

    /**
     * Bulgaria
     * 
     */
    BGR,

    /**
     * Chequia
     * 
     */
    CZE,

    /**
     * Hungría
     * 
     */
    HUN,

    /**
     * Polonia
     * 
     */
    POL,

    /**
     * República de Moldova
     * 
     */
    MDA,

    /**
     * Rumania
     * 
     */
    ROU,

    /**
     * Federación de Rusia
     * 
     */
    RUS,

    /**
     * Eslovaquia
     * 
     */
    SVK,

    /**
     * Ucrania
     * 
     */
    UKR,

    /**
     * Islas Åland
     * 
     */
    ALA,

    /**
     * Guernsey
     * 
     */
    GGY,

    /**
     * Jersey
     * 
     */
    JEY,

    /**
     * Dinamarca
     * 
     */
    DNK,

    /**
     * Estonia
     * 
     */
    EST,

    /**
     * Islas Feroe
     * 
     */
    FRO,

    /**
     * Finlandia
     * 
     */
    FIN,

    /**
     * Islandia
     * 
     */
    ISL,

    /**
     * Irlanda
     * 
     */
    IRL,

    /**
     * Isla de Man
     * 
     */
    IMN,

    /**
     * Letonia
     * 
     */
    LVA,

    /**
     * Lituania
     * 
     */
    LTU,

    /**
     * Noruega
     * 
     */
    NOR,

    /**
     * Islas Svalbard y Jan Mayen
     * 
     */
    SJM,

    /**
     * Suecia
     * 
     */
    SWE,

    /**
     * Reino Unido de Gran Bretaña e Irlanda del Norte
     * 
     */
    GBR,

    /**
     * Albania
     * 
     */
    ALB,

    /**
     * Andorra
     * 
     */
    AND,

    /**
     * Bosnia y Herzegovina
     * 
     */
    BIH,

    /**
     * Croacia
     * 
     */
    HRV,

    /**
     * Gibraltar
     * 
     */
    GIB,

    /**
     * Grecia
     * 
     */
    GRC,

    /**
     * Santa Sede
     * 
     */
    VAT,

    /**
     * Italia
     * 
     */
    ITA,

    /**
     * Malta
     * 
     */
    MLT,

    /**
     * Montenegro
     * 
     */
    MNE,

    /**
     * Portugal
     * 
     */
    PRT,

    /**
     * San Marino
     * 
     */
    SMR,

    /**
     * Serbia
     * 
     */
    SRB,

    /**
     * Eslovenia
     * 
     */
    SVN,

    /**
     * España
     * 
     */
    ESP,

    /**
     * Austria
     * 
     */
    AUT,

    /**
     * Bélgica
     * 
     */
    BEL,

    /**
     * Francia
     * 
     */
    FRA,

    /**
     * Alemania
     * 
     */
    DEU,

    /**
     * Liechtenstein
     * 
     */
    LIE,

    /**
     * Luxemburgo
     * 
     */
    LUX,

    /**
     * Mónaco
     * 
     */
    MCO,

    /**
     * Países Bajos
     * 
     */
    NLD,

    /**
     * Suiza
     * 
     */
    CHE,

    /**
     * Australia
     * 
     */
    AUS,

    /**
     * Isla de Navidad
     * 
     */
    CXR,

    /**
     * Islas Cocos (Keeling)
     * 
     */
    CCK,

    /**
     * Islas Heard y McDonald
     * 
     */
    HMD,

    /**
     * Nueva Zelandia
     * 
     */
    NZL,

    /**
     * Islas Norfolk
     * 
     */
    NFK,

    /**
     * Fiji
     * 
     */
    FJI,

    /**
     * Nueva Caledonia
     * 
     */
    NCL,

    /**
     * Papua Nueva Guinea
     * 
     */
    PNG,

    /**
     * Islas Salomón
     * 
     */
    SLB,

    /**
     * Vanuatu
     * 
     */
    VUT,

    /**
     * Guam
     * 
     */
    GUM,

    /**
     * Kiribati
     * 
     */
    KIR,

    /**
     * Islas Marshall
     * 
     */
    MHL,

    /**
     * Micronesia (Estados Federados de)
     * 
     */
    FSM,

    /**
     * Nauru
     * 
     */
    NRU,

    /**
     * Islas Marianas Septentrionales
     * 
     */
    MNP,

    /**
     * Palau
     * 
     */
    PLW,

    /**
     * Islas menores alejadas de Estados Unidos
     * 
     */
    UMI,

    /**
     * Samoa Americana
     * 
     */
    ASM,

    /**
     * Islas Cook
     * 
     */
    COK,

    /**
     * Polinesia Francesa
     * 
     */
    PYF,

    /**
     * Niue
     * 
     */
    NIU,

    /**
     * Pitcairn
     * 
     */
    PCN,

    /**
     * Samoa
     * 
     */
    WSM,

    /**
     * Tokelau
     * 
     */
    TKL,

    /**
     * Tonga
     * 
     */
    TON,

    /**
     * Tuvalu
     * 
     */
    TUV,

    /**
     * Islas Wallis y Futuna
     * 
     */
    WLF,

    /**
     * NO EXISTE
     * 
     */
    NN;

    public String value() {
        return name();
    }

    public static PaisType fromValue(String v) {
        return valueOf(v);
    }

}
