package xyz.refinedev.practice.managers;

import lombok.RequiredArgsConstructor;
import org.bukkit.event.Listener;
import xyz.refinedev.practice.Array;
import xyz.refinedev.practice.events.impl.sumo.solo.SumoSoloListener;
import xyz.refinedev.practice.events.impl.sumo.team.SumoTeamListener;
import xyz.refinedev.practice.kit.kiteditor.KitEditorListener;
import xyz.refinedev.practice.listeners.*;
import xyz.refinedev.practice.listeners.HotbarListener;
import xyz.refinedev.practice.util.events.WorldListener;
import xyz.refinedev.practice.util.menu.MenuListener;
import xyz.refinedev.practice.util.nametags.listener.NameTagListener;

import java.util.Arrays;

@RequiredArgsConstructor
public class ListenersManager {

    private final Array plugin;

    public void init() {
        for ( Listener listener : Arrays.asList(
                new ProfileListener(),
                new MenuListener(),
                new ChatListener(),
                new SumoSoloListener(),
                new SumoTeamListener(),
                new ArenaSelectionListener(),
                new KitEditorListener(),
                new NameTagListener(),
                new JoinMessageListener(),
                new OutdatedListener(),
                new PartyListener(),
                new HotbarListener(),
                new MatchListener(),
                new WorldListener(),
                new GHeadListener(),
                new ToggleSprintListener(),
                new TournamentListener(),
                new QueueListener()
        )) {
            this.plugin.getServer().getPluginManager().registerEvents(listener, plugin);
        }
    }
}
