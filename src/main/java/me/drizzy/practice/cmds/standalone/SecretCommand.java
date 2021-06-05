package me.drizzy.practice.cmds.standalone;

import me.drizzy.practice.Array;
import me.drizzy.practice.arena.Arena;
import me.drizzy.practice.kit.Kit;
import me.drizzy.practice.profile.Profile;
import me.drizzy.practice.profile.ProfileState;
import me.drizzy.practice.robot.Robot;
import me.drizzy.practice.robot.RobotType;
import me.drizzy.practice.util.chat.CC;
import me.drizzy.practice.util.command.annotation.Command;
import me.drizzy.practice.util.command.annotation.Require;
import me.drizzy.practice.util.command.annotation.Sender;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.ChatColor;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;

import java.util.Collections;
import java.util.HashMap;
import java.util.UUID;

/**
 * This Project is the property of Purge Community © 2021
 * Redistribution of this Project is not allowed
 *
 * @author Drizzy
 * Created at 5/31/2021
 * Project: Array
 */

public class SecretCommand {

    private HashMap<UUID, Robot> npcRegistry = new HashMap();

    @Command(name = "", desc = "A secret command to player botfight for Array")
    @Require("array.essentials.admin")
    public void secret(@Sender Player player) {
        this.initMatch(player, Kit.getByName("NoDebuff"));
    }

    @Command(name = "end", aliases = "die", desc = "Match End Command For the Bot Fight")
    public void end(@Sender Player player) {
        this.removeMatch(player, true);
    }


    public void initMatch(Player player, Kit kit) {
        Profile profile = Profile.getByPlayer(player);
        Arena arena = Arena.getRandom(kit);

        if (arena == null) return;

        player.sendMessage(ChatColor.YELLOW + "Starting training match. " + ChatColor.GREEN + "(" + player.getName() + " vs Robot)");

        NPC npc = CitizensAPI.getNPCRegistry().createNPC(EntityType.PLAYER, "Robot");
        npc.spawn(arena.getSpawn1());

        player.teleport(arena.getSpawn2());

        Robot robot = new Robot(npc, RobotType.MEDIUM);
        robot.setKit(kit);
        robot.setSkin("Intel_i7");
        robot.setSpawnLocation(arena.getSpawn1());
        npcRegistry.put(player.getUniqueId(), robot);

        robot.init(Collections.singletonList(player.getUniqueId()));

        npc.setProtected(false);
        profile.setState(ProfileState.IN_FIGHT);
        profile.handleVisibility();
        kit.applyToPlayer(player);
        player.showPlayer(robot.getPlayer());
        robot.getPlayer().showPlayer(player);
    }

    public void removeMatch(Player player, boolean won) {
        if (this.npcRegistry.containsKey(player.getUniqueId())) {
            Robot bot = this.npcRegistry.get(player.getUniqueId());

            if (bot.getLogic() != null) {
                bot.getLogic().cancel();
            }

            bot.stop();
            npcRegistry.remove(player.getUniqueId());

            Profile profile = Profile.getByUuid(player.getUniqueId());
            profile.setState(ProfileState.IN_LOBBY);
            profile.refreshHotbar();
            profile.handleVisibility();
            profile.teleportToSpawn();

            player.sendMessage(ChatColor.YELLOW + "You " + (won ? ChatColor.GREEN.toString() + ChatColor.BOLD + "WON" : ChatColor.RED.toString() + ChatColor.BOLD + "LOST") + ChatColor.YELLOW.toString() + " against the training bot.");
        }
    }



}
