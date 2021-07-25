package xyz.refinedev.practice.managers;

import org.bukkit.Bukkit;
import xyz.refinedev.practice.Array;
import xyz.refinedev.practice.arena.selection.ArenaSelectionListener;
import xyz.refinedev.practice.clan.listener.ClanListener;
import xyz.refinedev.practice.essentials.listener.GoldenHeads;
import xyz.refinedev.practice.essentials.listener.MOTDListener;
import xyz.refinedev.practice.essentials.listener.OutdatedListener;
import xyz.refinedev.practice.essentials.listener.ToggleSprintFix;
import xyz.refinedev.practice.events.impl.sumo.solo.SumoSoloListener;
import xyz.refinedev.practice.events.impl.sumo.team.SumoTeamListener;
import xyz.refinedev.practice.kit.kiteditor.KitEditorListener;
import xyz.refinedev.practice.match.MatchListener;
import xyz.refinedev.practice.party.PartyListener;
import xyz.refinedev.practice.profile.ProfileListener;
import xyz.refinedev.practice.profile.hotbar.HotbarListener;
import xyz.refinedev.practice.queue.QueueListener;
import xyz.refinedev.practice.util.events.WorldListener;
import xyz.refinedev.practice.util.menu.MenuListener;
import org.bukkit.event.Listener;
import xyz.refinedev.practice.util.nametags.listener.NameTagListener;

import java.util.Arrays;

public class ListenersManager {

    private final Array plugin = Array.getInstance();

    public void init() {
        Array.logger("&7Registering Listeners....");
        for ( Listener listener : Arrays.asList(
                new ProfileListener(),
                new MenuListener(),
                new ClanListener(),
                new SumoSoloListener(),
                new SumoTeamListener(),
                new ArenaSelectionListener(),
                new KitEditorListener(),
                new NameTagListener(),
                new MOTDListener(),
                new OutdatedListener(),
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
