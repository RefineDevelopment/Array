package xyz.refinedev.practice.match.types.kit;

import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import xyz.refinedev.practice.Array;
import xyz.refinedev.practice.arena.Arena;
import xyz.refinedev.practice.match.team.Team;
import xyz.refinedev.practice.match.team.TeamPlayer;
import xyz.refinedev.practice.match.types.TeamMatch;
import xyz.refinedev.practice.party.Party;
import xyz.refinedev.practice.profile.Profile;
import xyz.refinedev.practice.util.inventory.TeamFightUtil;
import xyz.refinedev.practice.util.other.PlayerUtil;

@Getter
public class TeamFightMatch extends TeamMatch {

    private final Array plugin = this.getPlugin();

    private final Team teamA;
    private final Team teamB;

    public TeamFightMatch(Team teamA, Team teamB, Arena arena) {
        super(teamA, teamB, Array.getInstance().getKitManager().getTeamFight(), arena);

        this.teamA = teamA;
        this.teamB = teamB;
    }

    @Override
    public void setupPlayer(Player player) {
        TeamPlayer teamPlayer = getTeamPlayer(player);

        if (teamPlayer.isDisconnected()) return;

        teamPlayer.setAlive(true);

        PlayerUtil.reset(player);

        player.setNoDamageTicks(this.getKit().getGameRules().getHitDelay());

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
                TeamFightUtil.giveBardKit(player);
                break;
            case "archer":
            case "Archer":
                TeamFightUtil.giveArcherKit(player);
                break;
            case "rogue":
            case "Rogue":
                TeamFightUtil.giveRogueKit(player);
                break;
            default:
                TeamFightUtil.giveDiamondKit(player);
                break;
        }

        plugin.getNameTagHandler().reloadPlayer(player);
        plugin.getNameTagHandler().reloadOthersFor(player);
    }

    @Override
    public void onStart() {
        this.getPlayers().forEach(player -> plugin.getSpigotHandler().kitKnockback(player, this.getKit()));
    }

}
