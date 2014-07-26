package com.mugitek.euskaldc.otto;

import android.os.Handler;
import android.os.Looper;

import com.squareup.otto.Bus;

import java.util.logging.LogRecord;

/**
 * Created by Neiru on 26/07/2014.
 */
public class MainThreadBus extends Bus {
    private final android.os.Handler mainThread = new Handler(Looper.getMainLooper());
    @Override
    public void post(final Object event) {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            super.post(event);
        } else {
            mainThread.post(new Runnable() {
                @Override
                public void run() {
                    post(event);
                }
            });
        }
    }

}
