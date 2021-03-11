package me.drizzy.practice.party.command;

import me.drizzy.practice.util.chat.CC;
import me.drizzy.practice.util.command.command.CommandMeta;
import org.bukkit.entity.Player;

@CommandMeta(label = { "party", "party help", "p", "p help" })
public class PartyHelpCommand {
    public void execute(final Player player) {
        player.sendMessage(CC.CHAT_BAR);
        player.sendMessage(CC.translate("&bParty &8(&7All Commands For Parties&8)"));
        player.sendMessage(CC.CHAT_BAR);
        player.sendMessage(CC.translate(" &8• &b/party help &8- &8&o(&7&oDisplays the help message&8&o)"));
        player.sendMessage(CC.translate(" &8• &b/party create &8- &8&o(&7&oCreates a party instance&8&o)"));
        player.sendMessage(CC.translate(" &8• &b/party leave &8- &8&o(&7&oLeave your current party&8&o)"));
        player.sendMessage(CC.translate(" &8• &b/party info &8- &8&o(&7&oDisplays your party information&8&o)"));
        player.sendMessage(CC.translate(" &8• &b/party join &8<&7party&8> - &8&o(&7&oJoin a party&8&o)"));
        player.sendMessage(CC.translate(" &8• &b/party chat - &8&o(&7&oToggle party chat&8&o)"));
        player.sendMessage(CC.translate(" &8• &b/party open &8- &8&o(&7&oOpen your party for others to join&8&o)"));
        player.sendMessage(CC.translate(" &8• &b/party close &8- &8&o(&7&oClose your party for others to join&8&o)"));
        player.sendMessage(CC.translate(" &8• &b/party invite &8<&7player&8> - &8&o(&7&oInvites a profile to your party&8&o)"));
        player.sendMessage(CC.translate(" &8• &b/party kick &8<&7player&8> - &8&o(&7&oKicks a profile from your party&8&o)"));
        player.sendMessage(CC.translate(" &8• &b/party ban &8<&7player&8> - &8&o(&7&oBans a profile from your party&8&o)"));
        player.sendMessage(CC.translate(" &8• &b/party unban &8<&7player&8> - &8&o(&7&oUnbans a profile from your party&8&o)"));
        player.sendMessage(CC.translate(" &8• &b/party promote &8<&7player&8> - &8&o(&7&oTransfers Ownership of your party&8&o)"));
        player.sendMessage(CC.CHAT_BAR);
    }
}