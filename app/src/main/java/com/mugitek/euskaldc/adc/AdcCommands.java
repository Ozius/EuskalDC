package com.mugitek.euskaldc.adc;

/**
 * Created by Neiru on 26/07/2014.
 */
public class AdcCommands {
    public static final String ADC_WRITE_CONNECTION_START = "HSUP ADBASE ADTIGR";//\n";
    public static final String ADC_WRITE_SEND_CLIENT_DATA = "BINF {0} ID{1} PD{2} NI{3} SL{4} SS{5}";//\n";
    public static final String ADC_WRITE_SEND_PUBLIC_MESSAGE = "BMSG {0} {1}";//\n";
    public static final String ADC_READ_ISID = "ISID";
    public static final String ADC_READ_STA = "ISTA";
    public static final String ADC_READ_LOGOUT = "IQUI";
    public static final String ADC_READ_BROADCAST_INFO = "BINF";
    public static final String ADC_READ_BROADCAST_MESSAGE = "BMSG";
    public static final String ADC_READ_HUB_MESSAGE = "IMSG";
    public static final String ADC_READ_CLIENTID =" ID";
    public static final String ADC_READ_NICK = " NI";
    public static final String ADC_READ_REDIRECT = " RD";
    public static final String ADC_READ_DESCRIPTION = " DE";
    public static final String ADC_READ_SHAREDSIZE = " SS";

}
