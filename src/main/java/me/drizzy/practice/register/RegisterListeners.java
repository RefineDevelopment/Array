package me.drizzy.practice.register;

import me.drizzy.practice.Array;
import me.drizzy.practice.array.listener.GoldenHeads;
import me.drizzy.practice.array.listener.MOTDListener;
import me.drizzy.practice.array.listener.ToggleSprintFix;
import me.drizzy.practice.event.types.brackets.BracketsListener;
import me.drizzy.practice.event.types.gulag.GulagListener;
import me.drizzy.practice.event.types.lms.LMSListener;
import me.drizzy.practice.event.types.parkour.ParkourListener;
import me.drizzy.practice.event.types.spleef.SpleefListener;
import me.drizzy.practice.event.types.sumo.SumoListener;
import me.drizzy.practice.kit.KitEditorListener;
import me.drizzy.practice.match.MatchListener;
import me.drizzy.practice.party.PartyListener;
import me.drizzy.practice.profile.ProfileListener;
import me.drizzy.practice.hotbar.HotbarListener;
import me.drizzy.practice.queue.QueueListener;
import me.drizzy.practice.util.events.ArmorListener;
import me.drizzy.practice.util.events.WorldListener;
import me.drizzy.practice.util.external.menu.MenuListener;
import org.bukkit.event.Listener;

import java.util.Arrays;

public class RegisterListeners {

    public static void register() {
        for ( Listener listener : Arrays.asList(
                new ProfileListener(),
                new MenuListener(Array.getInstance()),
                new SumoListener(),
                new GulagListener(),
                new BracketsListener(),
                new LMSListener(),
                new ParkourListener(),
                new SpleefListener(),
                new KitEditorListener(),
                new MOTDListener(),
                new PartyListener(),
                new HotbarListener(),
                new MatchListener(),
                new WorldListener(),
                new GoldenHeads(),
                new ToggleSprintFix(),
                new QueueListener(),
                new ArmorListener()
        )) {
            Array.getInstance().getServer().getPluginManager().registerEvents(listener, Array.getInstance());
        }
    }
}
