package com.mugitek.euskaldc.adc;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.nio.charset.UnsupportedCharsetException;

/**
 * Created by Neiru on 26/07/2014.
 */
public class AdcUtils {

    public static int getErrorCodeFromMessage(String message) {
        String errorCode = getTextFromMessage(message, AdcCommands.ADC_READ_STA+" ");
        if(errorCode != null && errorCode!="") {
            return Integer.parseInt(errorCode);
        }
        return -1;
    }

    public static String getErrorDescriptionFromMessage(String message) {
        String text = message.substring(8).trim();
        text = text.replaceAll("\\\\s"," ");
        text = text.replace("\\\\n","\n");
        return text;
    }
    public static String getDisconnectedSidFromMessage(String message) {
        return getTextFromMessage(message, AdcCommands.ADC_READ_LOGOUT+" ");
    }
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
    public static String getHubMessage(final String message) {
        if(message != null && message.length() > 5) {
            try {
                String returnMessage = message.substring(5);
                returnMessage = returnMessage.replaceAll("\\\\s", " ");
                returnMessage = returnMessage.replaceAll("\\\\n", "\n");
                return returnMessage;
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        } else {
            return "";
        }
    }
    public static String getUserMessageTextFromMessage(String message) {
        if(message != null && message.length() > 10) {
            try {
                String returnMessage = message.substring(10);
                returnMessage = returnMessage.replaceAll("\\\\s", " ");
                returnMessage = returnMessage.replaceAll("\\\\n", "\n");
                return returnMessage;
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
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
