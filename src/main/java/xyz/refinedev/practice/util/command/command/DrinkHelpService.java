package xyz.refinedev.practice.util.command.command;

import lombok.Getter;
import lombok.Setter;
import xyz.refinedev.practice.util.chat.CC;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@Getter @Setter
public class DrinkHelpService {

    private final DrinkCommandService commandService;
    private HelpFormatter helpFormatter;

    public DrinkHelpService(DrinkCommandService commandService) {
        this.commandService = commandService;
        this.helpFormatter = (sender, container) -> {
            sender.sendMessage(CC.CHAT_BAR);
            sender.sendMessage(CC.translate( "&cArray &7» &c" + container.getName().toUpperCase() + "'s &7Commands"));
            sender.sendMessage(CC.CHAT_BAR);
            if (!(sender instanceof Player)) {
                for (DrinkCommand c : container.getCommands().values()) {
                    sender.sendMessage(CC.translate(" &7* &c/" + container.getName() + (c.getName().length() > 0 ? " &c" + c.getName() : "") + " &8<&7" + c.getMostApplicableUsage() + "&8> &8(&7&o" + c.getDescription() + "&8)"));
                }
                return;
            }
            for (DrinkCommand c : container.getCommands().values()) {
                TextComponent msg = new TextComponent(net.md_5.bungee.api.ChatColor.translateAlternateColorCodes('&',
                        " &7* &c/" + container.getName() + (c.getName().length() > 0 ? " &c" + c.getName() : "") + " &8<&7" + c.getMostApplicableUsage() + "&8> &8(&7&o" + c.getDescription() + "&8)"));
                msg.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, TextComponent.fromLegacyText(ChatColor.GRAY + "/" + container.getName() + " " + c.getName() + " - " + ChatColor.WHITE + c.getDescription())));
                msg.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/" + container.getName() + " " + c.getName()));

                Player player = (Player) sender;
                player.spigot().sendMessage(msg);
            }
            sender.sendMessage(CC.CHAT_BAR);
        };
    }

    public void sendHelpFor(CommandSender sender, DrinkCommandContainer container) {
        this.helpFormatter.sendHelpFor(sender, container);
    }

    public void sendUsageMessage(CommandSender sender, DrinkCommandContainer container, DrinkCommand command) {
        sender.sendMessage(getUsageMessage(container, command));
    }

    public String getUsageMessage(DrinkCommandContainer container, DrinkCommand command) {
        String usage = ChatColor.GRAY + "Usage: /" + container.getName() + " ";
        if (command.getName().length() > 0) {
            usage += command.getName() + " ";
        }
        if (command.getUsage() != null && command.getUsage().length() > 0) {
            usage += command.getUsage();
        } else {
            usage += command.getGeneratedUsage();
        }
        return usage;
    }

}
