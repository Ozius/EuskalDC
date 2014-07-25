package com.mugitek.euskaldc;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.dd.processbutton.iml.ActionProcessButton;
import com.iangclifton.android.floatlabel.FloatLabel;
import com.mugitek.euskaldc.eventos.ConnectEvent;
import com.mugitek.euskaldc.eventos.Connected;
import com.mugitek.euskaldc.eventos.KillService;
import com.squareup.otto.Subscribe;


public class MyActivity extends Activity {
    private FloatLabel floatLabelServidor;
    private FloatLabel floatLabelPuerto;
    private FloatLabel floatLabelNick;
    private ActionProcessButton btnSignIn;

    public static final String LOGTAG = MyActivity.class.getName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my);

        startService(new Intent(this, AdcService.class));

        floatLabelServidor = (FloatLabel) findViewById(R.id.float_label_servidor);
        floatLabelPuerto = (FloatLabel) findViewById(R.id.float_label_puerto);
        floatLabelNick = (FloatLabel) findViewById(R.id.float_label_nick);

        floatLabelPuerto.getEditText().setInputType(InputType.TYPE_CLASS_NUMBER);

        btnSignIn = (ActionProcessButton) findViewById(R.id.button);
        btnSignIn.setMode(ActionProcessButton.Mode.ENDLESS);

        // Register self with the only bus that we're using
        BusProvider.getInstance().register(this);
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
        BusProvider.getInstance().post(new ConnectEvent(floatLabelServidor.getEditText().getText().toString(), Integer.valueOf(floatLabelPuerto.getEditText().getText().toString()), floatLabelNick.getEditText().getText().toString()));
    }

    @Subscribe
    public void connectedToHub(Connected event) {
        //btnSignIn.set

    }
}
