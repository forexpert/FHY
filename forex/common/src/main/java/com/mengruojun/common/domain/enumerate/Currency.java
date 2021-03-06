package com.mengruojun.common.domain.enumerate;

/**
 *  Enumerates the currencies we deal with. When embedding this enum's values in other objects
 *  use @Enumerated(EnumType.STRING) so they go into the db as strings and we can read them
 * more easily.
 */
public enum Currency
{
    AED (200),
    AFN (199),
    ALL (198),
    AMD (197),
    ANG (196),
    AOA (195),
    ARS (194),
    AUD (900),
    AWG (193),
    AZN (192),
    BAM (191),
    BBD (190),
    BCO (201), //Brent Crude Oil - precedence should be higher if we were to ever construct it
    BDT (189),
    BGN (188),
    BHD (187),
    BIF (186),
    BMD (185),
    BND (184),
    BOB (183),
    BOV (182),
    BRL (181),
    BSD (180),
    BTN (179),
    BWP (178),
    BYR (177),
    BZD (176),
    CAD (750),
    CDF (175),
    CHE (174),
    CHF (700),
    CHW (173),
    CLF (172),
    CLP (171),
    CNY (170),
    COP (169),
    COU (168),
    CRC (167),
    CUP (166),
    CVE (165),
    CZK (164),
    DJF (163),
    DKK (162),
    DOP (161),
    DZD (160),
    EEK (159),
    EGP (158),
    ERN (157),
    ETB (156),
    EUR (1000),
    FJD (155),
    FKP (154),
    FRX (204), //Some composite? - precedence should be higher if we were to ever construct it
    GBP (950),
    GEL (153),
    GHS (152),
    GIP (151),
    GMD (150),
    GNF (149),
    GTQ (148),
    GYD (147),
    HKD (146),
    HNL (145),
    HRK (144),
    HTG (143),
    HUF (142),
    IDR (141),
    ILS (140),
    INR (139),
    IQD (138),
    IRR (137),
    ISK (136),
    JMD (135),
    JOD (134),
    JPY (650),
    KES (133),
    KGS (132),
    KHR (131),
    KMF (130),
    KPW (129),
    KRW (128),
    KWD (127),
    KYD (126),
    KZT (125),
    LAK (124),
    LBP (123),
    LKR (122),
    LRD (121),
    LSL (120),
    LTL (119),
    LVL (118),
    LYD (117),
    MAD (116),
    MDL (115),
    MGA (114),
    MKD (113),
    MMK (112),
    MNT (111),
    MOP (110),
    MRO (109),
    MUR (108),
    MVR (107),
    MWK (106),
    MXN (105),
    MXV (104),
    MYR (103),
    MZN (102),
    NAD (101),
    NGN (100),
    NIO (99),
    NOK (98),
    NPR (97),
    NZD (850),
    OMR (96),
    PAB (95),
    PEN (94),
    PGK (93),
    PHP (92),
    PKR (91),
    PLN (90),
    PYG (89),
    QAR (88),
    RON (87),
    RSD (86),
    RUB (85),
    RWF (84),
    SAR (83),
    SBD (82),
    SCR (81),
    SDG (80),
    SEK (79),
    SGD (78),
    SHP (77),
    SLL (76),
    SOS (75),
    SPX (203), //S & P 500 Index - precedence should be higher if we were to ever construct it
    SRD (74),
    STD (73),
    SYP (72),
    SZL (71),
    THB (70),
    TJS (69),
    TMT (68),
    TND (67),
    TOP (66),
    TRY (65),
    TTD (64),
    TWD (63),
    TZS (62),
    UAH (61),
    UGX (60),
    USD (800),
    USN (59),
    USS (58),
    UYU (57),
    UZS (56),
    VEF (55),
    VND (54),
    VUV (53),
    WST (52),
    WTI (202), // West Texas Intermediate - another oil symbol, precedence should be higher if we were to ever construct it 
    XAF (51),
    XAG (50),
    XAU (49),
    XCD (48),
    XOF (47),
    XPD (46),
    XPF (45),
    XPT (44),
    YER (43),
    ZAR (42),
    ZMK (41),
    ZWL (40),
    ALL_CURRENCIES (0);

    private final int precedence;

    Currency(int precedence)
    {
        this.precedence = precedence;
    }

    public int precedence()    { return precedence; }

    public static Currency fromJDKCurrency(java.util.Currency jdkCurrency){
        for(Currency c1 : Currency.values()){
            if(jdkCurrency.getCurrencyCode().equals(c1.toString())) {
                return c1;
            }
        }
        return null;
    }

    public java.util.Currency toJDKCurrency(){
      return java.util.Currency.getInstance(this.toString());

    }
}
