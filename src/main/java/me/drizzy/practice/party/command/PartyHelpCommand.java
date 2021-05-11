package me.drizzy.practice.party.command;

import me.drizzy.practice.util.chat.CC;
import me.drizzy.practice.util.command.command.CommandMeta;
import org.bukkit.entity.Player;

@CommandMeta(label = { "party", "party help", "p", "p help" })
public class PartyHelpCommand {
    public void execute(final Player player) {
        player.sendMessage(CC.CHAT_BAR);
        player.sendMessage(CC.translate("&cParty &8(&7All Commands For Parties&8)"));
        player.sendMessage(CC.CHAT_BAR);
        player.sendMessage(CC.translate(" &8• &c/party help &8(&7&oDisplays the help message&8)"));
        player.sendMessage(CC.translate(" &8• &c/party create &8(&7&oCreates a party instance&8)"));
        player.sendMessage(CC.translate(" &8• &c/party leave &8(&7&oLeave your current party&8)"));
        player.sendMessage(CC.translate(" &8• &c/party info &8(&7&oDisplays your party information&8)"));
        player.sendMessage(CC.translate(" &8• &c/party join &8<&7party&8> - &8&o(&7&oJoin a party&8)"));
        player.sendMessage(CC.translate(" &8• &c/party chat - &8&o(&7&oToggle party chat&8)"));
        player.sendMessage(CC.translate(" &8• &c/party open &8(&7&oOpen your party for others to join&8)"));
        player.sendMessage(CC.translate(" &8• &c/party close &8(&7&oClose your party for others to join&8)"));
        player.sendMessage(CC.translate(" &8• &c/party invite &8<&7player&8> - &8&o(&7&oInvites a profile to your party&8)"));
        player.sendMessage(CC.translate(" &8• &c/party kick &8<&7player&8> - &8&o(&7&oKicks a profile from your party&8)"));
        player.sendMessage(CC.translate(" &8• &c/party ban &8<&7player&8> - &8&o(&7&oBans a profile from your party&8)"));
        player.sendMessage(CC.translate(" &8• &c/party unban &8<&7player&8> - &8&o(&7&oUnbans a profile from your party&8)"));
        player.sendMessage(CC.translate(" &8• &c/party promote &8<&7player&8> - &8&o(&7&oTransfers Ownership of your party&8)"));
        player.sendMessage(CC.CHAT_BAR);
    }
}