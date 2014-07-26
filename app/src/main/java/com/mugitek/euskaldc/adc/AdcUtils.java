package com.mugitek.euskaldc.adc;

/**
 * Created by Neiru on 26/07/2014.
 */
public class AdcUtils {
    public static String getCidFromMessage(String message) {
        return getTextFromMessage(message, AdcCommands.ADC_READ_CLIENTID);
    }
    public static String getNickFromMessage(String message) {
        return getTextFromMessage(message, AdcCommands.ADC_READ_NICK);
    }
    public static String getDescriptionFromMessage(String message) {
        return getTextFromMessage(message, AdcCommands.ADC_READ_DESCRIPTION);
    }

    public static long getSharedSizeInBytesFromMessage(String message) {
        return Long.parseLong(getTextFromMessage(message, AdcCommands.ADC_READ_SHAREDSIZE));
    }

    private static String getTextFromMessage(String message, String textKey) {
        int indexOfKey = message.indexOf(textKey);
        if(indexOfKey == -1) return null;
        String messageFromKey = message.substring(indexOfKey + textKey.length());
        int firstSpaceIndex = messageFromKey.indexOf(" ");
        String text;
        if(firstSpaceIndex == -1) {
            text = messageFromKey;
        } else {
            text = messageFromKey.substring(0,firstSpaceIndex);
        }
        return text.replaceAll("\\s"," ");
    }

}
