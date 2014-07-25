package com.mugitek.euskaldc;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.UnknownHostException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.Calendar;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import gnu.crypto.hash.HashFactory;
import gnu.crypto.hash.IMessageDigest;


public class MyActivity extends Activity {
    private TextView mText;
    private Button mButton;

    public static final String LOGTAG = MyActivity.class.getName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my);

        startService(new Intent(this, AdcService.class));

        //mText = (TextView) findViewById(R.id.textView);
        mButton = (Button) findViewById(R.id.button);

        // Register self with the only bus that we're using
        BusProvider.getInstance().register(this);

        //new DownloadFilesTask().execute();
    }

    @Override
    public void onResume() {

        super.onResume();
    }

    @Override
    public void onDestroy() {
        // Let's kill the service with a bus message when we kill the activity
        BusProvider.getInstance().post(new KillService());
        super.onDestroy();
    }

    private class DownloadFilesTask extends AsyncTask<Void, Void, Void> {
        protected Void doInBackground(Void... urls) {
            try
            {
                //Mo 1 client socket den server voi so cong va dia chi xac dinh
                TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {
                    @Override
                    public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType) throws java.security.cert.CertificateException {

                    }

                    @Override
                    public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType) throws java.security.cert.CertificateException {

                    }

                    public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                        return new java.security.cert.X509Certificate[] {};
                    }

                } };

                SSLContext sc = SSLContext.getInstance("TLS");
                sc.init(null, trustAllCerts, null);

                SSLSocket sslsocket = (SSLSocket) sc.getSocketFactory().createSocket("dc.p2plibre.es", 2780);
                //sslsocket.setUseClientMode(true);
                //sslsocket.setNeedClientAuth(false);
                //sslsocket.setWantClientAuth(false);

                //Tao luong nhan va gui du lieu len server
                DataOutputStream os=new DataOutputStream(sslsocket.getOutputStream());
                DataInputStream is=new DataInputStream(sslsocket.getInputStream());

                //Gui du lieu len server
                String str="HSUP ADBASE ADTIGR\n";
                os.writeBytes(str);

                //Nhan du lieu da qua xu li tu server ve
                String responseStr;
                String sid = "";

                //pid
                byte[] data = null;

                IMessageDigest md = HashFactory.getInstance("Tiger");
                Base32 base32 = new Base32();

                data = ("EuskalDC++ASDFGHJKLPOIUX").getBytes();
                Log.d(LOGTAG, "" + data.length);

                String pid = base32.encodeBytes(data).substring(0, 39);
                Log.d(LOGTAG, "PID: " + pid);

                //cid
                md.update(data, 0, data.length);

                String cid = base32.encodeBytes(md.digest()).substring(0, 39);
                Log.d(LOGTAG, "CID: " + cid);

                while((responseStr=is.readLine()) != null){
                    if(responseStr.indexOf("ISID") != -1){
                        sid = responseStr.substring(5);
                        break;
                    }

                    Log.d(LOGTAG, responseStr);
                }

                Log.d(LOGTAG, "SID: " + sid);

                str = "BINF " + sid + " ID" + cid + " PD" + pid + " NIOzius SL5\n";
                os.writeBytes(str);

                while((responseStr=is.readLine()) != null){
                    Log.d(LOGTAG, responseStr);
                }

                os.close();
                is.close();
                sslsocket.close();
            }
            catch(UnknownHostException e)
            {
                e.printStackTrace();
            }
            catch(IOException e)
            {
                e.printStackTrace();
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            } catch (KeyManagementException e) {
                e.printStackTrace();
            }
            return null;
        }

        protected void onProgressUpdate(Integer... progress) {

        }

        protected void onPostExecute() {

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.my, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void conectar(View v){
        BusProvider.getInstance().post(new ButtonEvent());
    }
}
