package uk.jamieisgeek;

import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.EventListener;
import org.jetbrains.annotations.NotNull;

public class PrivateVCs implements EventListener {
    public void load() {
        System.out.println("PrivateVSs loaded!");
    }

    public void unload() {
        System.out.println("PrivateVSs unloaded!");
    }

    @Override
    public void onEvent(@NotNull GenericEvent event) {
        if(event instanceof MessageReceivedEvent receivedEvent) {

        }
    }
}