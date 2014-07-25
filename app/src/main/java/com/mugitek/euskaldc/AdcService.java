package com.mugitek.euskaldc;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.mugitek.euskaldc.eventos.ConnectEvent;
import com.mugitek.euskaldc.eventos.ConnectedEvent;
import com.mugitek.euskaldc.eventos.KillServiceEvent;
import com.squareup.otto.Subscribe;

public class AdcService extends Service {
    static final String TAG = "AdcService";

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
        onDestroy();
    }

    @Subscribe
    public void buttonPress(ConnectEvent event) {
        Log.d(TAG, "conectando...\n" + event.getServer() + "\n" + event.getPuerto() + "\n" + event.getNick());
        BusProvider.getInstance().post(new ConnectedEvent());
    }
}
