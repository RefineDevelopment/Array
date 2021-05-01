package me.drizzy.practice.nametags;

import lombok.Getter;
import me.drizzy.practice.nametags.construct.NametagUpdate;

import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class NametagThread extends Thread {

    @Getter private static final Map<NametagUpdate, Boolean> pendingUpdates = new ConcurrentHashMap<>();

    public NametagThread() {
        super("Array - Nametags Thread");
        setDaemon(false);
    }

    public void run() {
        while (true) {
            Iterator<NametagUpdate> pendingUpdatesIterator = pendingUpdates.keySet().iterator();

            while(pendingUpdatesIterator.hasNext()) {
                NametagUpdate pendingUpdate = pendingUpdatesIterator.next();

                try {
                    NametagHandler.applyUpdate(pendingUpdate);
                    pendingUpdatesIterator.remove();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            try {
                Thread.sleep(2 * 50L);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

}