package com.mugitek.euskaldc;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import com.mugitek.euskaldc.adc.AdcUtils;
import com.mugitek.euskaldc.eventos.ConnectEvent;
import com.mugitek.euskaldc.eventos.ConnectedEvent;
import com.mugitek.euskaldc.eventos.ConnectionErrorEvent;
import com.mugitek.euskaldc.eventos.KillServiceEvent;
import com.mugitek.euskaldc.adc.AdcCommands;
import com.mugitek.euskaldc.eventos.NewMessageEvent;
import com.mugitek.euskaldc.eventos.SendMessageEvent;
import com.mugitek.euskaldc.eventos.UserLoginEvent;
import com.mugitek.euskaldc.eventos.UserLogoutEvent;
import com.mugitek.euskaldc.socket.AcceptAllX509TrustManager;
import com.squareup.otto.Subscribe;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Calendar;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.TrustManager;

import gnu.crypto.Registry;
import gnu.crypto.hash.HashFactory;
import gnu.crypto.hash.IMessageDigest;

public class AdcService extends Service {
    static final String TAG = "AdcService";
    private boolean isRunning = false;
    private String sid = null;
    private BufferedReader dataInputStream = null;
    private PrintWriter dataOutputStream = null;
    private Socket socket = null;
    public AdcService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "create service");
        BusProvider.getInstance().register(this);
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "destroy service");
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    // Use bus to kill the service
    @Subscribe
    public void killService(KillServiceEvent event) {
        /*
        Thread infoExit = new Thread(new Runnable() {
            @Override
            public void run() {
                String logoutMessage =
                AdcService.this.dataOutputStream.writeBytes();
            }
        });
        */
        isRunning = false;
        onDestroy();
    }

    /**
     * Recive como parámetro el evento y se queda escuchando a
     * lo que llegue del servicio
     * @param event
     */
    @Subscribe
    public void startConnectionAndRead(ConnectEvent event) {
        isRunning = true;
        final ConnectEvent connectEvent = event;
        Log.d(TAG, "conectando...\n" + event.getServer() + "\n" + event.getPuerto() + "\n" + event.getNick());
        Thread socketInitializatorAndReader = new Thread(new Runnable() {
            @Override
            public void run() {
                byte[] data = null;
                boolean connected = false;
                Base32 base32 = new Base32();
                //TODO Esto es dependiente del dispositivo, habrá que generarlo una vez y guardarlo
                TelephonyManager telephonyManager = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
                String deviceId = telephonyManager.getDeviceId();
                String sampleMac = "11:11:11:11:11:ab";
                String currentTimeInMillis = "" + (Calendar.getInstance().getTimeInMillis());
                String dataString = sampleMac + currentTimeInMillis.substring(currentTimeInMillis.length() - (24 - sampleMac.length()));
//                (((+(Calendar.getInstance().getTimeInMillis()));
                data = ("NEIRU+DC++ASDFGHJKLPOIUX").getBytes(); //md.digest(); //("NEIRU+DC++ASDFGHJKLPOIUX").getBytes();//
                String pid = base32.encodeBytes(data).substring(0, 39);
                //cid
                IMessageDigest md = HashFactory.getInstance(Registry.TIGER_HASH);
                md.update(data, 0, data.length);
                String cid = base32.encodeBytes(md.digest()).substring(0, 39);
                //TODO Esto no siempre será ssl, hay que cambiar este boolean
                boolean isSSL = connectEvent.isSsl();
                try {
                    if(isSSL) {
                        TrustManager[] trustAllCerts = new TrustManager[]{new AcceptAllX509TrustManager()};
                        SSLContext sc = SSLContext.getInstance("TLS");
                        sc.init(null, trustAllCerts, null);
                        SSLSocket sslsocket = (SSLSocket) sc.getSocketFactory().createSocket(connectEvent.getServer(), connectEvent.getPuerto());
                        AdcService.this.socket = sslsocket;
                    } else {
                        // TODO Hay que crear un socket normal
                        AdcService.this.socket = new Socket(connectEvent.getServer(), connectEvent.getPuerto());
                    }
                    PrintWriter dataOutputStream = new PrintWriter(AdcService.this.socket.getOutputStream(),true);
                    AdcService.this.dataOutputStream = dataOutputStream;
                    BufferedReader dataInputStream= new BufferedReader(new InputStreamReader(AdcService.this.socket.getInputStream()));
                    AdcService.this.dataInputStream = dataInputStream;
                    AdcService.this.dataOutputStream.println(AdcCommands.ADC_WRITE_CONNECTION_START);
                    StringBuilder responseStringBuilder = new StringBuilder();
                    String responseString;
                    char responseChar;
                    sid = null;
                    while ((responseString = dataInputStream.readLine()) != null) {
                        if(responseString.indexOf(AdcCommands.ADC_READ_ISID) != -1) {
                            sid = responseString.substring(AdcCommands.ADC_READ_ISID.length()+1);
                            break;
                        }
                    }
                    // Si el servicio ha devuelto un identificador, le enviaremos nuestras credenciales
                    if(sid != null) {
                        long ss = (((long)1024)*((long)1024)*((long)1024)*((long)100));
                        String message = AdcCommands.ADC_WRITE_SEND_CLIENT_DATA;
                        message = message.replace("{0}", sid);
                        message = message.replace("{1}", cid);
                        message = message.replace("{2}",pid);
                        message = message.replace("{3}",connectEvent.getNick());
                        message = message.replace("{4}","5");
                        message = message.replace("{5}","" + ss);
                        Log.d(TAG, message);
                        dataOutputStream.println(message);
                        while ((responseString = dataInputStream.readLine()) != null && isRunning ) {
                            if(responseString.startsWith(AdcCommands.ADC_READ_BROADCAST_INFO)) {
                                if(!connected) {
                                    connected = true;
                                    BusProvider.getInstance().post(new ConnectedEvent());
                                }
                                int indexOfCid = responseString.indexOf(AdcCommands.ADC_READ_CLIENTID);
                                if(indexOfCid > -1) {
                                    // Vamos a suponer que se conecta un usuario
                                    String userSid = AdcUtils.getSidFromMessage(responseString);
                                    String userCid = AdcUtils.getCidFromMessage(responseString);
                                    String userNick = AdcUtils.getNickFromMessage(responseString);
                                    String userDescription = AdcUtils.getDescriptionFromMessage(responseString);
                                    UserLoginEvent userLoginEvent = new UserLoginEvent(userSid,userCid,userNick,userDescription);
                                    userLoginEvent.setShared(AdcUtils.getSharedSizeInBytesFromMessage(responseString));
                                    BusProvider.getInstance().post(userLoginEvent);
                                }
                            } else if(responseString.startsWith(AdcCommands.ADC_READ_HUB_MESSAGE)) {
                                String messageText = AdcUtils.getHubMessage(responseString);
                                BusProvider.getInstance().post(new SendMessageEvent(null,messageText));
                            } else if(responseString.startsWith(AdcCommands.ADC_READ_BROADCAST_MESSAGE)) {
                                String messageSid = AdcUtils.getSidFromMessage(responseString);
                                String messageText = AdcUtils.getUserMessageTextFromMessage(responseString);
                                BusProvider.getInstance().post(new SendMessageEvent(messageSid,messageText));
                            } else if (responseString.startsWith(AdcCommands.ADC_READ_STA)) {
                                int errorCode = AdcUtils.getErrorCodeFromMessage(responseString);
                                String errorMessage = AdcUtils.getErrorDescriptionFromMessage(responseString);
                                BusProvider.getInstance().post(new ConnectionErrorEvent(errorCode, errorMessage));
                            } else if(responseString.startsWith(AdcCommands.ADC_READ_LOGOUT)) {
                                //es una desconexión
                                String disconnectedSid = AdcUtils.getDisconnectedSidFromMessage(responseString);
                                if(disconnectedSid.equals(sid)) {
                                    BusProvider.getInstance().post(new ConnectionErrorEvent(280, "Username occupied"));
                                    String redirectUrl = AdcUtils.getRedirectUrlFromQuitMessage(responseString);
                                    if(redirectUrl != null) {
                                        boolean isSsl = redirectUrl.startsWith("adcs://");
                                        int protocolEndsIndex = redirectUrl.indexOf("://")+("://".length());
                                        String urlWithoutProtocol = redirectUrl.substring(protocolEndsIndex);
                                        int portPointsIndex = urlWithoutProtocol.indexOf(":");
                                        String host = urlWithoutProtocol.substring(0, portPointsIndex);
                                        int lastSlashIndex = urlWithoutProtocol.indexOf("/");
                                        String port = "";
                                        if(lastSlashIndex>-1) {
                                            port = urlWithoutProtocol.substring(portPointsIndex+1, lastSlashIndex);
                                        } else {
                                            port = urlWithoutProtocol.substring(portPointsIndex+1);
                                        }
                                        ConnectEvent connectEvent1 = new ConnectEvent(host, Integer.parseInt(port), connectEvent.getNick());
                                        connectEvent1.setIsSsl(isSsl);
                                        BusProvider.getInstance().post(connectEvent1);
                                    }
                                    isRunning = false;
                                } else {
                                    BusProvider.getInstance().post(new UserLogoutEvent(disconnectedSid));
                                }
                            }
                            Log.d(TAG, responseString);
                        }
                    }
                    dataInputStream.close();
                    dataOutputStream.close();
                    sid = null;
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        });
        socketInitializatorAndReader.start();

    }

    @Subscribe
    public void newMessage(final NewMessageEvent newMessageEvent) {
        Thread writeMessageThread = new Thread(new Runnable() {
            @Override
            public void run() {
                if(sid != null) {
                    try {
                        String toPublicText = newMessageEvent.getMensaje();//new String(newMessageEvent.getMensaje().getBytes(),"UTF-8");
                        toPublicText = toPublicText.replaceAll(" ","\\\\s");
                        toPublicText = toPublicText.replace("\n","\\n");
                        String message = AdcCommands.ADC_WRITE_SEND_PUBLIC_MESSAGE;
                        message = message.replace("{0}", sid);
                        message = message.replace("{1}", toPublicText);
                        AdcService.this.dataOutputStream.println(message);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        });
        writeMessageThread.start();
    }
}
