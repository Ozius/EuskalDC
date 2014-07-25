package com.mugitek.euskaldc;

import com.squareup.otto.Bus;

/**
 * Created by Arkaitz on 25/07/2014.
 */
public final class BusProvider {
    private static final Bus BUS = new Bus();

    public static Bus getInstance() {
        return BUS;
    }

    private BusProvider() {
        // No instances.
    }
}
