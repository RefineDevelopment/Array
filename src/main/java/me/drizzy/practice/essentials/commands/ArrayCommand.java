package me.drizzy.practice.essentials.commands;

import me.drizzy.practice.util.chat.CC;
import me.drizzy.practice.util.command.command.CommandMeta;
import org.bukkit.entity.Player;

@CommandMeta(label={"array", "array help", "practice"})
public class ArrayCommand {
    public void execute(Player p) {
        if (p.hasPermission("array.staff")) {
            p.sendMessage(CC.CHAT_BAR);
            p.sendMessage(CC.translate("&cArray &7» Array Commands"));
            p.sendMessage(CC.CHAT_BAR);
            p.sendMessage(CC.translate(" &8• &c/array setlobby &8(&7&oSets the lobby to player's location&8)"));
            p.sendMessage(CC.translate(" &8• &c/array savekits &8(&7&oSave all Kits&8)"));
            p.sendMessage(CC.translate(" &8• &c/array savearenas &8(&7&oSave all Arenas&8)"));
            p.sendMessage(CC.translate(" &8• &c/array savedata &8(&7&oSave all Profiles&8)"));
            p.sendMessage(CC.translate(" &8• &c/array goldenhead &8(&7&oReceive a pre-made G-Head&8)"));
            p.sendMessage(CC.translate(" &8• &c/array refill &8(&7&oRefill your Inventory with potions or soup&8)"));
            p.sendMessage(CC.translate(" &8• &c/array update &8(&7&oUpdate all leaderboards&8)"));
            p.sendMessage(CC.translate(" &8• &c/array savedata &8(&7&oSave all Profiles&8)"));
            p.sendMessage(CC.translate(" &8• &c/array savearenas &8(&7&oSave all Arenas&8)"));
            p.sendMessage(CC.translate(" &8• &c/array savekits &8(&7&oSave all Kits&8)"));
            p.sendMessage(CC.translate(" &8• &c/array hcf &8(&7&oHelp on how to setup HCF&8)"));
            p.sendMessage(CC.translate(" &8• &c/array worlds &8(&7&oShow a Worlds Menu&8)"));
            p.sendMessage(CC.translate(" &8• &c/array resetstats &8<&7name&8> &8(&7&oResets a profile&8)"));
            p.sendMessage(CC.translate(" &8• &c/array clearloadouts &8<&7kit|all&8> &8<&7global|name&8> &8(&7&oResets a profile&8)"));
            p.sendMessage(CC.translate(" &8• &c/array rename &8<&7name&8> &8(&7&oRenames item in hand&8)"));
            p.sendMessage(CC.translate(" &8• &c/array spawn &8(&7&oRefresh Profile & Teleport to spawn&8)"));
            p.sendMessage(CC.translate(" &8• &c/kit help &8(&7&oView kit commands&8)"));
            p.sendMessage(CC.translate(" &8• &c/arena help &8(&7&oView arena commands&8)"));
            p.sendMessage(CC.CHAT_BAR);
        } else {
            p.sendMessage(CC.CHAT_BAR);
            p.sendMessage(CC.translate("&7This server is running &cArray &8[&71.0&8]"));
            p.sendMessage(CC.translate("&7Array is made By &c&lDrizzy &7and &cVeltus"));
            p.sendMessage(CC.translate("&7Base for &cArray &7provided by &cNick & Joeleoli"));
            p.sendMessage(CC.CHAT_BAR);

        }
    }
}
