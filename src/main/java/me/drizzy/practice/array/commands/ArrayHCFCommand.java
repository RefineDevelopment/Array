package me.drizzy.practice.array.commands;

import me.drizzy.practice.Array;
import me.drizzy.practice.util.chat.CC;
import me.drizzy.practice.util.command.command.CommandMeta;
import org.bukkit.entity.Player;

@CommandMeta(label = { "hcf", "array hcf", "hcf help"}, permission="array.dev")
public class ArrayHCFCommand {
    public void execute(final Player player) {
        if (Array.getInstance().getMainConfig().getBoolean("Array.HCF-Enabled")) {
            player.sendMessage(CC.CHAT_BAR);
            player.sendMessage(CC.translate("           &b&lHow to Setup HCF "));
            player.sendMessage("");
            player.sendMessage(CC.translate("&7In order to setup hcf, first of all create a Kit"));
            player.sendMessage(CC.translate("&7named &bHCFTeamFight &7, this should usually automatically"));
            player.sendMessage(CC.translate("&7create itself but if it doesn't make sure to use that correct"));
            player.sendMessage(CC.translate("&7spelling. To setup arenas for hcf, make a shared arena or standalone"));
            player.sendMessage(CC.translate("&7and add the kit HCFTeamFight to it. Now for the HCF Kits:"));
            player.sendMessage(CC.translate(""));
            player.sendMessage(CC.translate("&b&lBard Kit"));
            player.sendMessage(CC.translate("&7The kits are coded so that their name and Armor must be as the kit"));
            player.sendMessage(CC.translate("&7is intended to have in order to make them work, In this case"));
            player.sendMessage(CC.translate("&7Bard Kit MUST have Golden Armor and be named &bHCFBARD,"));
            player.sendMessage(CC.translate("&7rest you can add the normal bard items."));
            player.sendMessage(CC.translate(""));
            player.sendMessage(CC.translate("&b&lArcher Kit"));
            player.sendMessage(CC.translate("&7In order for archer to work, the Archer kit MUST be named"));
            player.sendMessage(CC.translate("&bHCFARCHER &7and must have Leather as its armor and a bow,"));
            player.sendMessage(CC.translate("&7rest you can add it as you wish."));
            player.sendMessage(CC.translate(""));
            player.sendMessage(CC.translate("&b&lRogue Kit"));
            player.sendMessage(CC.translate("&7In order for rogue to work, the Rogue kit MUST be named"));
            player.sendMessage(CC.translate("&bHCFROGUE &7and must have Chain-mail as its armor,"));
            player.sendMessage(CC.translate("&7rest you can add it as you wish."));
            player.sendMessage(CC.translate(""));
            player.sendMessage(CC.translate("&7The HCFDIAMOND kit must be named exactly like that and can have anything as its content."));
            player.sendMessage(CC.CHAT_BAR);
        } else {
            player.sendMessage(CC.translate("&cHCF has been disabled by an Admin."));
        }
    }
}
