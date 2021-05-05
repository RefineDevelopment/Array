package me.drizzy.practice.managers;

import me.drizzy.practice.Array;
import me.drizzy.practice.arena.selection.ArenaSelectionListener;
import me.drizzy.practice.essentials.listener.GoldenHeads;
import me.drizzy.practice.essentials.listener.MOTDListener;
import me.drizzy.practice.essentials.listener.ToggleSprintFix;
import me.drizzy.practice.events.types.brackets.BracketsListener;
import me.drizzy.practice.events.types.gulag.GulagListener;
import me.drizzy.practice.events.types.lms.LMSListener;
import me.drizzy.practice.events.types.parkour.ParkourListener;
import me.drizzy.practice.events.types.spleef.SpleefListener;
import me.drizzy.practice.events.types.sumo.SumoListener;
import me.drizzy.practice.kiteditor.KitEditorListener;
import me.drizzy.practice.match.MatchListener;
import me.drizzy.practice.party.PartyListener;
import me.drizzy.practice.profile.ProfileListener;
import me.drizzy.practice.hotbar.HotbarListener;
import me.drizzy.practice.queue.QueueListener;
import me.drizzy.practice.robot.listener.RobotHitDelay;
import me.drizzy.practice.util.events.WorldListener;
import me.drizzy.practice.util.menu.MenuListener;
import org.bukkit.event.Listener;

import java.util.Arrays;

public class ListenersManager {

    public static void register() {
        Array.logger("&7Registering Listeners....");
        for ( Listener listener : Arrays.asList(
                new ProfileListener(),
                new MenuListener(Array.getInstance()),
                new SumoListener(),
                new GulagListener(),
                new BracketsListener(),
                new RobotHitDelay(),
                new LMSListener(),
                new ParkourListener(),
                new SpleefListener(),
                new ArenaSelectionListener(),
                new KitEditorListener(),
                new MOTDListener(),
                new PartyListener(),
                new HotbarListener(),
                new MatchListener(),
                new WorldListener(),
                new GoldenHeads(),
                new ToggleSprintFix(),
                new QueueListener()
        )) {
            Array.getInstance().getServer().getPluginManager().registerEvents(listener, Array.getInstance());
        }
    }
}
