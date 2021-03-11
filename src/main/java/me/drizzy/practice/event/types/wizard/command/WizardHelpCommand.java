package me.drizzy.practice.event.types.wizard.command;

import me.drizzy.practice.util.chat.CC;
import me.drizzy.practice.util.chat.Color;
import me.drizzy.practice.util.command.command.CommandMeta;
import org.bukkit.entity.Player;

@CommandMeta(label = {"wizard", "wizard help"}, permission = "array.dev")
public class WizardHelpCommand {

    public void execute(Player player) {
        player.sendMessage(CC.CHAT_BAR);
        player.sendMessage(Color.translate("&b&lWizard &8- &8&o(&7&o&7Wizard Commands&8&o)"));
        player.sendMessage(CC.CHAT_BAR);
        player.sendMessage(Color.translate(" &8• &b/wizard cancel &8- &8&o(&7&o&7Cancel current Wizard Event&8&o)"));
        player.sendMessage(Color.translate(" &8• &b/wizard cooldown &8- &8&o(&7&o&7Reset the Wizard Event cooldown&8&o)"));
        player.sendMessage(Color.translate(" &8• &b/wizard host &8- &8&o(&7&o&7Host a Wizard Event&8&o)"));
        player.sendMessage(Color.translate(" &8• &b/wizard forcestart &8- &8&o(&7&o&7Force start a Wizard Event&8&o)"));
        player.sendMessage(Color.translate(" &8• &b/wizard join &8- &8&o(&7&o&7Join ongoing Wizard Event&8&o)"));
        player.sendMessage(Color.translate(" &8• &b/wizard leave &8- &8&o(&7&o&7Leave ongoing Wizard Event&8&o)"));
        player.sendMessage(Color.translate(" &8• &b/wizard tp &8- &8&o(&7&o&7Teleport to the Wizard Event Arena&8&o)"));
        player.sendMessage(Color.translate(" &8• &b/wizard setspawn  &8- &8&o(&7&o&7Set the spawns for Wizard Event&8&o)"));
        player.sendMessage(CC.CHAT_BAR);
    }
}
