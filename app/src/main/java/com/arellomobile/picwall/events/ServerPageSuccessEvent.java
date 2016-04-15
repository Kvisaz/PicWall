package com.arellomobile.picwall.events;

import com.arellomobile.picwall.network.model.DesktopprResponse;

public class ServerPageSuccessEvent {
    public final DesktopprResponse page;

    public ServerPageSuccessEvent(DesktopprResponse page) {
        this.page = page;
    }
}
