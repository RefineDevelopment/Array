package xyz.refinedev.practice.managers;

import org.bukkit.Bukkit;
import xyz.refinedev.practice.Array;
import xyz.refinedev.practice.arena.selection.ArenaSelectionListener;
import xyz.refinedev.practice.clan.listener.ClanListener;
import xyz.refinedev.practice.essentials.listener.GoldenHeads;
import xyz.refinedev.practice.essentials.listener.MOTDListener;
import xyz.refinedev.practice.essentials.listener.ToggleSprintFix;
import xyz.refinedev.practice.events.impl.sumo.solo.SumoSoloListener;
import xyz.refinedev.practice.events.types.brackets.BracketsListener;
import xyz.refinedev.practice.events.types.gulag.GulagListener;
import xyz.refinedev.practice.events.types.lms.LMSListener;
import xyz.refinedev.practice.events.types.parkour.ParkourListener;
import xyz.refinedev.practice.events.types.spleef.SpleefListener;
import xyz.refinedev.practice.kit.kiteditor.KitEditorListener;
import xyz.refinedev.practice.match.MatchListener;
import xyz.refinedev.practice.party.PartyListener;
import xyz.refinedev.practice.profile.ProfileListener;
import xyz.refinedev.practice.profile.hotbar.HotbarListener;
import xyz.refinedev.practice.queue.QueueListener;
import xyz.refinedev.practice.util.events.WorldListener;
import xyz.refinedev.practice.util.menu.MenuListener;
import xyz.refinedev.practice.util.nametags.listener.NameTagListener;
import org.bukkit.event.Listener;

import java.util.Arrays;

public class ListenersManager {

    private final Array plugin = Array.getInstance();

    public void registerListeners() {
        for ( Listener listener : Arrays.asList(
                new ProfileListener(),
                new MenuListener(),
                new ClanListener(),
                new SumoSoloListener(),
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
            Bukkit.getPluginManager().registerEvents(listener, plugin);
        }
    }
}
