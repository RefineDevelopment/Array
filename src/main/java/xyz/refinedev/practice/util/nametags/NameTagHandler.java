package xyz.refinedev.practice.util.nametags;

import xyz.refinedev.practice.util.nametags.construct.NameTagComparator;
import xyz.refinedev.practice.util.nametags.construct.NameTagInfo;
import xyz.refinedev.practice.util.nametags.construct.NametagUpdate;
import lombok.Getter;
import lombok.Setter;
import xyz.refinedev.practice.util.nametags.packet.ScoreboardTeamPacketMod;
import xyz.refinedev.practice.util.nametags.provider.NameTagProvider;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class NameTagHandler {

    @Getter private static final Map<String, Map<String, NameTagInfo>> teamMap = new ConcurrentHashMap<>();
    private static final List<NameTagProvider> providers = new ArrayList<>();
    private static final List<NameTagInfo> registeredTeams = Collections.synchronizedList(new ArrayList<>());

    @Getter private static boolean initiated = false;
    @Getter @Setter private static boolean async = true;
    private static int teamCreateIndex = 1;

    public static void hook() {
        initiated = true;

        new NametagThread().start();
    }

    public static void registerProvider(NameTagProvider newProvider) {
        providers.add(newProvider);
        providers.sort(new NameTagComparator());
    }

    public static void reloadPlayer(Player toRefresh) {
        NametagUpdate update = new NametagUpdate(toRefresh);

        if(async) {
            NametagThread.getPendingUpdates().put(update, true);
        } else {
            applyUpdate(update);
        }
    }

    public static void reloadOthersFor(Player refreshFor) {
        Bukkit.getOnlinePlayers().forEach(toRefresh -> {
            if(refreshFor != toRefresh) {
                reloadPlayer(toRefresh, refreshFor);
            }
        });
    }

    public static void reloadPlayer(Player toRefresh, Player refreshFor) {
        NametagUpdate update = new NametagUpdate(toRefresh, refreshFor);

        if(async) {
            NametagThread.getPendingUpdates().put(update, true);
        } else {
            applyUpdate(update);
        }
    }

    public static void applyUpdate(NametagUpdate nametagUpdate) {
        if(nametagUpdate.getToRefresh() != null){
            Player toRefreshPlayer = Bukkit.getPlayerExact(nametagUpdate.getToRefresh());

            if(toRefreshPlayer == null) return;

            if(nametagUpdate.getRefreshFor() == null) {
                Bukkit.getOnlinePlayers().forEach(refreshFor -> reloadPlayerInternal(toRefreshPlayer, refreshFor));
            } else {
                Player refreshForPlayer = Bukkit.getPlayerExact(nametagUpdate.getRefreshFor());

                if(refreshForPlayer != null) {
                    reloadPlayerInternal(toRefreshPlayer, refreshForPlayer);
                }
            }
        }
    }

    public static void reloadPlayerInternal(Player toRefresh, Player refreshFor) {
        if(!refreshFor.hasMetadata("Test-LoggedIn")) return;

        NameTagInfo provided = null;

        for ( NameTagProvider nametagProvider : providers){
            provided =  nametagProvider.fetchNameTag(toRefresh, refreshFor);
            if (provided != null){
                break;
            }
        }

        if (provided == null){
            return;
        }

        Map<String, NameTagInfo> teamInfoMap = new HashMap<>();
        
        if(teamMap.containsKey(refreshFor.getName())) {
            teamInfoMap = teamMap.get(refreshFor.getName());
        }
        
        (new ScoreboardTeamPacketMod(provided.getName(), Collections.singletonList(toRefresh.getName()), 3)).sendToPlayer(refreshFor);
        teamInfoMap.put(toRefresh.getName(), provided);
        teamMap.put(refreshFor.getName(), teamInfoMap);        
    }

    public static void initiatePlayer(Player player) {
        registeredTeams.forEach(teamInfo -> teamInfo.getTeamAddPacket().sendToPlayer(player));
    }

    public static NameTagInfo getOrCreate(String prefix, String suffix) {
        for( NameTagInfo teamInfo : registeredTeams) {
            if(teamInfo.getPrefix().equals(prefix) && teamInfo.getSuffix().equals(suffix)) {
                return (teamInfo);
            }
        }

        NameTagInfo newTeam = new NameTagInfo(String.valueOf(teamCreateIndex++), prefix, suffix);
        registeredTeams.add(newTeam);

        ScoreboardTeamPacketMod addPacket = newTeam.getTeamAddPacket();

        Bukkit.getOnlinePlayers().forEach(addPacket::sendToPlayer);

        return (newTeam);
    }
}