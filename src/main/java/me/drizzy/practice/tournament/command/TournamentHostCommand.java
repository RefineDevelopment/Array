package me.drizzy.practice.tournament.command;

import me.drizzy.practice.util.command.command.CPL;
import me.drizzy.practice.util.command.command.CommandMeta;
import me.drizzy.practice.Array;
import me.drizzy.practice.kit.Kit;
import me.drizzy.practice.tournament.Tournament;
import me.drizzy.practice.util.chat.CC;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

@CommandMeta(label = "tournament host", permission = "array.host.tournament")
public class TournamentHostCommand {

    private static void broadcastMessage(String message) {
        BaseComponent[] component = TextComponent.fromLegacyText(message);
        for (BaseComponent baseComponent : component) {
            baseComponent.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/tournament join"));
            baseComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, TextComponent.fromLegacyText(ChatColor.GREEN + "Click to join the Tournament")));
        }
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.spigot().sendMessage(component);
        }
    }

    public void execute(CommandSender commandSender, @CPL("team size") String s) {
        Player player = (Player) commandSender;
        final String ladder = "NoDebuff";
        try {
            Integer.parseInt(s);
        } catch (Exception e) {
            commandSender.sendMessage(CC.translate("Please put a valid number."));
            return;
        }
        int size = Integer.parseInt(s);
        if (Tournament.CURRENT_TOURNAMENT != null) {
            commandSender.sendMessage(ChatColor.RED + "The Tournament has already started");
            return;
        }
        Tournament.CURRENT_TOURNAMENT = new Tournament();
        if (size == 1 || size == 2) {
            if (size == 1) Tournament.CURRENT_TOURNAMENT.setTeamCount(1);
            else Tournament.CURRENT_TOURNAMENT.setTeamCount(2);
        } else {
            commandSender.sendMessage(ChatColor.RED + "Please choose 1 or 2");
            Tournament.CURRENT_TOURNAMENT.cancel();
            Tournament.CURRENT_TOURNAMENT = null;
            return;
        }
        if (Kit.getByName(ladder) != null) {
            Tournament.CURRENT_TOURNAMENT.setLadder(Kit.getByName("NoDebuff"));
        } else {
            commandSender.sendMessage(ChatColor.RED + "Please Contact an Admin, The Kit NoDebuff Doesn't Exist!");
            Tournament.CURRENT_TOURNAMENT.cancel();
            Tournament.CURRENT_TOURNAMENT = null;
            return;
        }

        Tournament.CURRENT_TOURNAMENT.setLadder(Kit.getByName("NoDebuff"));

        Bukkit.broadcastMessage(CC.translate("&8[&b&lTournament&8] &b" + player.getPlayer().getDisplayName() + CC.WHITE + " is hosting a Tournament!"));
        broadcastMessage(CC.translate("&8[&b&lTournament&8] &b" + ChatColor.AQUA + "(Click to accept)"));

        Tournament.RUNNABLE = new BukkitRunnable() {
            private int countdown = 60;

            @Override
            public void run() {
                countdown--;
                if (countdown == 30 || countdown == 10 || countdown <= 3) {
                    if (countdown > 0) {
                        Bukkit.broadcastMessage(CC.translate("&8[&b&lTournament&8] &f" + "Tournament is starting in &b" + countdown + " &fseconds!"));
                        broadcastMessage(CC.translate("&8[&b&lTournament&8] &b" + ChatColor.AQUA + "(Click to accept)"));
                    }
                }
                if(countdown <= 0){
                    Tournament.RUNNABLE = null;
                    cancel();
                    if(Tournament.CURRENT_TOURNAMENT.getParticipatingCount() < 2){
                        Bukkit.broadcastMessage(CC.translate("&8[&b&lTournament&8] " + CC.RED + "The Tournament has been cancelled."));
                        Tournament.CURRENT_TOURNAMENT.cancel();
                        Tournament.CURRENT_TOURNAMENT = null;
                    } else {
                        Tournament.CURRENT_TOURNAMENT.tournamentstart();
					}
                }
            }
        };
        Tournament.RUNNABLE.runTaskTimer(Array.getInstance(), 20, 20);
    }
}


