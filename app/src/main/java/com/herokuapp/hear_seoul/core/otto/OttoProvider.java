/*
 * Copyright (c) 2018. YeonJung Kim
 *
 *  GitHub : @duswnd25
 *  Site   : https://yeonjung.herokuapp.com/
 */

package com.herokuapp.hear_seoul.core.otto;

import com.squareup.otto.Bus;

public final class OttoProvider {
    private static final Bus BUS = new Bus();

    private OttoProvider() {
        // No instances.
    }

    public static Bus getInstance() {
        return BUS;
    }
}
