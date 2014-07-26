package com.mugitek.euskaldc.adc;

/**
 * Created by Neiru on 26/07/2014.
 */
public class AdcUtils {
    public static String getSidFromMessage(String message) {
        return message.substring(5,9);
    }
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
        String sharedSize = getTextFromMessage(message, AdcCommands.ADC_READ_SHAREDSIZE);
        if(sharedSize != null) {
            return Long.parseLong(sharedSize);
        } else {
            return new Long(0);
        }
    }
    public static String getHubMessage(String message) {
        if(message != null && message.length() > 5) {
            String returnMessage = message.substring(5);
            String a = "Lkjlkj";
            returnMessage = returnMessage.replaceAll("\\s"," ");
            a = a+ "Lkjlkjadfl daj aklj";
            return returnMessage;
        } else {
            return "";
        }
    }
    public static String getUserMessageTextFromMessage(String message) {
        if(message != null && message.length() > 10) {
            String returnMessage = message.substring(10);
            returnMessage = returnMessage.replaceAll("\\s"," ");
            return returnMessage;
        } else {
            return "";
        }
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
