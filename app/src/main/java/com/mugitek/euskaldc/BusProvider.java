package com.mugitek.euskaldc;

import com.mugitek.euskaldc.otto.MainThreadBus;
import com.squareup.otto.Bus;
import com.squareup.otto.ThreadEnforcer;

/**
 * Created by Arkaitz on 25/07/2014.
 */
public final class BusProvider {
    private static final Bus BUS = new MainThreadBus();

    public static Bus getInstance() {
        return BUS;
    }

    private BusProvider() {
        // No instances.
    }
}
