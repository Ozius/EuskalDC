package com.mugitek.euskaldc.adc;

/**
 * Created by Neiru on 26/07/2014.
 */
public class AdcCommands {
    public static final String ADC_WRITE_CONNECTION_START = "HSUP ADBASE ADTIGR\n";
    public static final String ADC_WRITE_SEND_CLIENT_DATA = "BINF {0} ID{1} PD{2} NI{3} SL{4}\n";
    public static final String ADC_READ_ISID = "ISID";

}
