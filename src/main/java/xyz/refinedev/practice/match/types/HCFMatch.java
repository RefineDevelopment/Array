package xyz.refinedev.practice.match.types;

import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import xyz.refinedev.practice.Array;
import xyz.refinedev.practice.arena.Arena;
import xyz.refinedev.practice.kit.Kit;
import xyz.refinedev.practice.managers.PvPClassManager;
import xyz.refinedev.practice.match.team.Team;
import xyz.refinedev.practice.match.team.TeamPlayer;
import xyz.refinedev.practice.party.Party;
import xyz.refinedev.practice.profile.Profile;
import xyz.refinedev.practice.util.other.PlayerUtil;

@Getter
public class HCFMatch extends TeamMatch {

    private final Array plugin = Array.getInstance();

    private final Team teamA;
    private final Team teamB;

    public HCFMatch(Team teamA, Team teamB, Arena arena) {
        super(teamA, teamB, Kit.getHCFTeamFight(), arena);

        this.teamA = teamA;
        this.teamB = teamB;
    }

    @Override
    public void setupPlayer(Player player) {
        TeamPlayer teamPlayer = getTeamPlayer(player);

        if (teamPlayer.isDisconnected()) return;

        teamPlayer.setAlive(true);

        PlayerUtil.reset(player);

        player.setNoDamageTicks(Kit.getHCFTeamFight().getGameRules().getHitDelay());

        Team team = getTeam(player);

        Location spawn = team.equals(teamA) ? getArena().getSpawn1() : getArena().getSpawn2();
        player.teleport(spawn.add(0, plugin.getConfigHandler().getMATCH_SPAWN_YLEVEL(), 0));

        teamPlayer.setPlayerSpawn(spawn);

        Profile profile = plugin.getProfileManager().getByUUID(player.getUniqueId());
        Party party = profile.getParty();
        String kit = party.getKits().get(player.getUniqueId());

        switch (kit) {
            case "bard":
            case "Bard":
                PvPClassManager.giveBardKit(player);
                break;
            case "archer":
            case "Archer":
                PvPClassManager.giveArcherKit(player);
                break;
            case "rogue":
            case "Rogue":
                PvPClassManager.giveRogueKit(player);
                break;
            default:
                PvPClassManager.giveDiamondKit(player);
                break;
        }

        plugin.getNameTagHandler().reloadPlayer(player);
        plugin.getNameTagHandler().reloadOthersFor(player);
    }

    @Override
    public void onStart() {
        this.getPlayers().forEach(player -> plugin.getSpigotHandler().kitKnockback(player, Kit.getHCFTeamFight()));
    }

}
