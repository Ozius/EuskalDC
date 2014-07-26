package com.mugitek.euskaldc;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.mugitek.euskaldc.adc.AdcUtils;
import com.mugitek.euskaldc.eventos.ConnectEvent;
import com.mugitek.euskaldc.eventos.ConnectedEvent;
import com.mugitek.euskaldc.eventos.KillServiceEvent;
import com.mugitek.euskaldc.adc.AdcCommands;
import com.mugitek.euskaldc.eventos.SendMessageEvent;
import com.mugitek.euskaldc.eventos.UserLoginEvent;
import com.mugitek.euskaldc.socket.AcceptAllX509TrustManager;
import com.squareup.otto.Subscribe;

import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.net.Socket;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.TrustManager;

import gnu.crypto.Registry;
import gnu.crypto.hash.HashFactory;
import gnu.crypto.hash.IMessageDigest;

public class AdcService extends Service {
    static final String TAG = "AdcService";
    private boolean isRunning = false;
    private DataInputStream dataInputStream = null;
    private DataOutputStream dataOutputStream = null;
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
                data = ("NEIRU+DC++ASDFGHJKLPOIUX").getBytes();
                String pid = base32.encodeBytes(data).substring(0, 39);
                //cid
                IMessageDigest md = HashFactory.getInstance(Registry.TIGER_HASH);
                md.update(data, 0, data.length);
                String cid = base32.encodeBytes(md.digest()).substring(0, 39);
                //TODO Esto no siempre será ssl, hay que cambiar este boolean
                boolean isSSL = true;
                try {
                    if(isSSL) {
                        TrustManager[] trustAllCerts = new TrustManager[]{new AcceptAllX509TrustManager()};
                        SSLContext sc = SSLContext.getInstance("TLS");
                        sc.init(null, trustAllCerts, null);
                        SSLSocket sslsocket = (SSLSocket) sc.getSocketFactory().createSocket(connectEvent.getServer(), connectEvent.getPuerto());
                        AdcService.this.socket = sslsocket;
                    } else {
                        // TODO Hay que crear un socket normal
                    }
                    DataOutputStream dataOutputStream = new DataOutputStream(AdcService.this.socket.getOutputStream());
                    AdcService.this.dataOutputStream = dataOutputStream;
                    DataInputStream dataInputStream= new DataInputStream(AdcService.this.socket.getInputStream());
                    AdcService.this.dataInputStream = dataInputStream;
                    AdcService.this.dataOutputStream.writeBytes(AdcCommands.ADC_WRITE_CONNECTION_START);
                    String responseString;
                    String sid = null;
                    while ((responseString = dataInputStream.readLine()) != null) {
                        if(responseString.indexOf(AdcCommands.ADC_READ_ISID) != -1) {
                            sid = responseString.substring(AdcCommands.ADC_READ_ISID.length()+1);
                            break;
                        }
                    }
                    // Si el servicio ha devuelto un identificador, le enviaremos nuestras credenciales
                    if(sid != null) {
                        String message = AdcCommands.ADC_WRITE_SEND_CLIENT_DATA;
                        message = message.replace("{0}", sid);
                        message = message.replace("{1}", cid);
                        message = message.replace("{2}",pid);
                        message = message.replace("{3}",connectEvent.getNick());
                        message = message.replace("{4}","5");
                        Log.d(TAG, message);
                        dataOutputStream.writeBytes(message);
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
                            }
                            Log.d(TAG, responseString);
                        }
                    }
                    dataInputStream.close();
                    dataOutputStream.close();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        });
        socketInitializatorAndReader.start();

    }
}
