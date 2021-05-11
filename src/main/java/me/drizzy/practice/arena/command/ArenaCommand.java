package me.drizzy.practice.arena.command;

import me.drizzy.practice.util.chat.CC;
import me.drizzy.practice.util.command.command.CommandMeta;
import org.bukkit.command.CommandSender;

@CommandMeta(label = { "arena", "arenahelp" }, permission = "array.dev")
public class ArenaCommand {
    public void execute(final CommandSender player) {
        player.sendMessage(CC.CHAT_BAR);
        player.sendMessage(CC.translate( "&cArray &7» Arena Commands"));
        player.sendMessage(CC.CHAT_BAR);
        player.sendMessage(CC.translate(" &8• &c/arena create &8<&7name&8> &8<&7Shared|Standalone|TheBridge&8> &8(&7&oCreate an Arena&8)"));
        player.sendMessage(CC.translate(" &8• &c/arena remove &8<&7name&8> &8(&7&oDelete an Arena&8)"));
        player.sendMessage(CC.translate(" &8• &c/arena portalwand &8(&7&oReceive a wand to select the portal for TheBridge Arena&8)"));
        player.sendMessage(CC.translate(" &8• &c/arena kitlist &8<&7Arena&8> &8(&7&oLists all the kits of an arena&8)"));
        player.sendMessage(CC.translate(" &8• &c/arena seticon &8<&7Arena&8> &8(&7&oSets the item your holding as Arena Icon&8)"));
        player.sendMessage(CC.translate(" &8• &c/arena setcuboid &8<&7red|blue&8> &8(&7&oSets red/blue cuboid from your selection&8)"));
        player.sendMessage(CC.translate(" &8• &c/arena setportal &8<&7red|blue&8> &8(&7&oSets red/blue portal from your selection&8)"));
        player.sendMessage(CC.translate(" &8• &c/arena setbridgespawn &8<&7red|blue&8> &8(&7&oSet red/blue spawn of arena&8)"));
        player.sendMessage(CC.translate(" &8• &c/arena setspawn &8<&71/2&8> &8<&71/2&8> &8(&7&oSet 1/2 spawn of arena&8)"));
        player.sendMessage(CC.translate(" &8• &c/arena setmax &8(&7&oSet Max Location of a Standalone Arena&8)"));
        player.sendMessage(CC.translate(" &8• &c/arena setmin &8(&7&oSet Min Location of a Standalone Arena&8)"));
        player.sendMessage(CC.translate(" &8• &c/arena generate &8<&7Arena&8> &8<&7Amount&8> &8(&7&oCopy and paste Standalone Arenas automatically&8)"));
        player.sendMessage(CC.translate(" &8• &c/arena addkit &8<&7Arena&8> &8<&7Kit&8> &8(&7&oAdd a kit to the arena&8)"));
        player.sendMessage(CC.translate(" &8• &c/arena addnormalkits &8<&7Arena&8> &8<&7Kit&8> &8(&7&oAdd all the normal kits to the arena&8)"));
        player.sendMessage(CC.translate(" &8• &c/arena addbuildkits &8<&7Arena&8> &8<&7Kit&8> &8(&7&oAdd all the build kits to the arena&8)"));
        player.sendMessage(CC.translate(" &8• &c/arena removekit &8<&7Arena&8> <&7Kit&8> &8(&7&oRemove a kit from the arena&8)"));
        player.sendMessage(CC.translate(" &8• &c/arena disablepearls &8(&7&oEnable or Disable the ability for players to pearl on the arena&8)"));
        player.sendMessage(CC.translate(" &8• &c/arena save &8(&7&oSave Arenas&8)"));
        player.sendMessage(CC.CHAT_BAR);
    }

}
