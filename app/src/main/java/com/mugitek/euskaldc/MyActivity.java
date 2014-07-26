package com.mugitek.euskaldc;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.Toast;

import com.dd.processbutton.iml.ActionProcessButton;
import com.iangclifton.android.floatlabel.FloatLabel;
import com.mugitek.euskaldc.eventos.ConnectEvent;
import com.mugitek.euskaldc.eventos.ConnectedEvent;
import com.mugitek.euskaldc.eventos.ConnectionErrorEvent;
import com.mugitek.euskaldc.eventos.KillServiceEvent;
import com.squareup.otto.Subscribe;


public class MyActivity extends Activity {
    private FloatLabel floatLabelServidor;
    private FloatLabel floatLabelPuerto;
    private FloatLabel floatLabelNick;
    private ActionProcessButton btnSignIn;
    private CheckBox mCheckSSL;

    private static final String SERVER_KEY = "server";
    private static final String PORT_KEY = "port";
    private static final String NICK_KEY = "nick";
    private static final String SSL_KEY = "ssl";

    public static final String LOGTAG = MyActivity.class.getName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my);

        startService(new Intent(this, AdcService.class));

        floatLabelServidor = (FloatLabel) findViewById(R.id.float_label_servidor);
        floatLabelPuerto = (FloatLabel) findViewById(R.id.float_label_puerto);
        floatLabelNick = (FloatLabel) findViewById(R.id.float_label_nick);
        mCheckSSL = (CheckBox) findViewById(R.id.checkSSL);

        floatLabelPuerto.getEditText().setInputType(InputType.TYPE_CLASS_NUMBER);

        btnSignIn = (ActionProcessButton) findViewById(R.id.button);
        btnSignIn.setMode(ActionProcessButton.Mode.ENDLESS);

        // Register self with the only bus that we're using
        BusProvider.getInstance().register(this);

        //Miramos si tenemos guardadas preferencias anteriores
        SharedPreferences pref = getSharedPreferences("EuskalDCPrefs", MODE_PRIVATE);
        floatLabelServidor.getEditText().setText(pref.getString(SERVER_KEY, ""));
        floatLabelPuerto.getEditText().setText(pref.getString(PORT_KEY, "2780"));
        floatLabelNick.getEditText().setText(pref.getString(NICK_KEY, ""));
        mCheckSSL.setChecked(pref.getBoolean(SSL_KEY, true));
    }

    @Override
    public void onResume() {

        super.onResume();
    }

    @Override
    public void onDestroy() {
        // Let's kill the service with a bus message when we kill the activity
        BusProvider.getInstance().post(new KillServiceEvent());
        super.onDestroy();
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
        btnSignIn.setProgress(1);

        floatLabelServidor.getEditText().setEnabled(false);
        floatLabelPuerto.getEditText().setEnabled(false);
        floatLabelNick.getEditText().setEnabled(false);

        BusProvider.getInstance().post(new ConnectEvent(floatLabelServidor.getEditText().getText().toString(), Integer.valueOf(floatLabelPuerto.getEditText().getText().toString()), floatLabelNick.getEditText().getText().toString(), mCheckSSL.isChecked()));
    }

    @Subscribe
    public void connectedToHub(ConnectedEvent event) {
        btnSignIn.setProgress(100);

        //Guardar el valor de los campos en las preferencias
        SharedPreferences pref = getSharedPreferences("EuskalDCPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();

        editor.putString(SERVER_KEY, floatLabelServidor.getEditText().getText().toString());
        editor.putString(PORT_KEY, floatLabelPuerto.getEditText().getText().toString());
        editor.putString(NICK_KEY, floatLabelNick.getEditText().getText().toString());
        editor.putBoolean(SSL_KEY, mCheckSSL.isChecked());

        editor.commit();

        //Lanzamos el activity del chat
        Intent i = new Intent(this, ChatActivity.class);
        //i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        i.putExtra("Titulo", "Nombre del HUB");

        startActivity(i);
        btnSignIn.setProgress(0);
        //finish();
    }

    @Subscribe
    public void errorConnecting(ConnectionErrorEvent event) {
        btnSignIn.setProgress(0);

        floatLabelServidor.getEditText().setEnabled(true);
        floatLabelPuerto.getEditText().setEnabled(true);
        floatLabelNick.getEditText().setEnabled(true);

        Toast.makeText(getApplicationContext(), event.getErrorMessage(), Toast.LENGTH_SHORT).show();
    }
}
