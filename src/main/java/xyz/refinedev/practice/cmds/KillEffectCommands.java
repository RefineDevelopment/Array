package xyz.refinedev.practice.cmds;

import com.mongodb.client.model.Filters;
import lombok.RequiredArgsConstructor;
import org.bukkit.Effect;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import xyz.refinedev.practice.Array;
import xyz.refinedev.practice.profile.killeffect.KillEffect;
import xyz.refinedev.practice.util.chat.CC;
import xyz.refinedev.practice.util.command.annotation.Command;
import xyz.refinedev.practice.util.command.annotation.OptArg;
import xyz.refinedev.practice.util.command.annotation.Require;
import xyz.refinedev.practice.util.command.annotation.Sender;
import xyz.refinedev.practice.util.menu.Button;

import java.util.UUID;

/**
 * This Project is property of Refine Development © 2021
 * Redistribution of this Project is not allowed
 *
 * @author Drizzy
 * Created: 8/16/2021
 * Project: Array
 */

@RequiredArgsConstructor
public class KillEffectCommands {

    private final Array plugin;

    private final String[] HELP_MESSAGE = {
            CC.CHAT_BAR,
            CC.translate("&cArray &7» Kill Effect Commands"),
            CC.CHAT_BAR,
            CC.translate(" &7* &c/ke menu &8(&7&oOpen Kill Effects Menu&8)"),
            CC.translate(" &7* &c/ke create &8<&7name&8> &8<&7displayname&8> &8(&7&oCreate a Kill Effect with name&8)"),
            CC.translate(" &7* &c/ke remove &8<&7killeffect&8> &8(&7&oRemove a Kill Effect with name&8)"),
            CC.translate(" &7* &c/ke save &7[killeffect] &8(&7&oSave all or specified killeffect&8)"),
            CC.translate(" &7* &c/ke setdata &8<&7killeffect&8> &8<&7int&8> &8(&7&oSet your kill effect's data&8)"),
            CC.translate(" &7* &c/ke seteffect &8<&7killeffect&8> &8<&7effect&8> &8(&7&oSet your kill effect's effect&8)"),
            CC.translate(" &7* &c/ke seticon &8<&7killeffect&8> &8(&7&oSet your kill effect's icon to the item in your hand&8)"),
            CC.translate(" &7* &c/ke setpriority &8<&7killeffect&8> &8<&7int&8> &8(&7&oSet your kill effect's menu priority&8)"),
            CC.translate(" &7* &c/ke setpermission &8<&7killeffect&8> &8<&7perm&8> &8(&7&oSet your kill effect's permission string&8)"),
            CC.translate(" &7* &c/ke permission &8<&7killeffect&8> &8(&7&oToggle whether kill effect requires permission or not &8)"),
            CC.translate(" &7* &c/ke animatedeath &8<&7killeffect&8> &8(&7&oToggle Animate-Death mode for kill effect&8)"),
            CC.translate(" &7* &c/ke lightning &8<&7killeffect&8> &8(&7&oToggle Lightning mode for kill effect&8"),
            CC.translate(" &7* &c/ke dropsclear &8<&7killeffect&8> &8(&7&oToggle whether death drops are cleared instantly&8)"),
            CC.translate(" &7* &c/ke import &8(&7&oImport kill effects from config&8)"),
            CC.translate(" &7* &c/ke export &8(&7&oExport current kill effects to config&8)"),
            CC.CHAT_BAR
    };

    @Command(name = "", desc = "View kill effect commands or open the menu")
    public void help(@Sender CommandSender sender) {
        if (!sender.hasPermission("array.killeffect.admin") && sender instanceof Player) {
            this.menu((Player) sender);
        }
        sender.sendMessage(HELP_MESSAGE);
    }

    @Command(name = "menu", aliases = "openmenu", desc = "Open Kill Efects Menu")
    public void menu(@Sender Player sender) {
        plugin.getMenuManager().findMenu(sender,"profile_killeffects").openMenu(sender);
        Button.playSuccess(sender);
    }

    @Command(name = "create", usage = "<name> <displayname> (Color codes supported)", desc = "Create a Kill Effect with name")
    @Require("array.killeffect.admin")
    public void create(@Sender CommandSender sender, String name, String displayName) {
        KillEffect killEffect = new KillEffect(UUID.randomUUID(), name);
        killEffect.setDisplayName(CC.translate(displayName));

        plugin.getKillEffectManager().getKillEffects().add(killEffect);
        plugin.getKillEffectManager().save(killEffect);

        sender.sendMessage(CC.translate("&8[&c&lArray&8] &7Successfully created a kill effect with the name &c" + name + "&7."));
    }

    @Command(name = "remove", usage = "<killeffect>", desc = "Create a Kill Effect with name")
    @Require("array.killeffect.admin")
    public void remove(@Sender CommandSender sender, KillEffect killEffect) {
        sender.sendMessage(CC.translate("&8[&c&lArray&8] &7Successfully removed a kill effect with the name &c" + killEffect.getName() + "&7."));
        plugin.getKillEffectManager().getKillEffects().remove(killEffect);
        plugin.getKillEffectManager().getCollection().deleteOne(Filters.eq("uuid", killEffect.getUniqueId().toString()));
    }

    @Command(name = "list", desc = "List all Kill Effects")
    @Require("array.killeffect.admin")
    public void list(@Sender CommandSender sender) {
        sender.sendMessage(CC.CHAT_BAR);
        sender.sendMessage(CC.translate("&cArray &7» Kill Effects"));
        sender.sendMessage(CC.CHAT_BAR);

        if (plugin.getKillEffectManager().getKillEffects().isEmpty()) {
            sender.sendMessage(CC.translate("&7&oThere are no kill effects"));
            return;
        } else {
            for ( KillEffect killEffect : plugin.getKillEffectManager().getKillEffects() ) {
                sender.sendMessage(CC.translate(" &7* &c" + killEffect.getName() + " &8(&7" + killEffect.getPriority() + "&8)"));
            }
        }
        sender.sendMessage(CC.CHAT_BAR);
    }

    @Command(name = "save", usage = "[killeffect]", desc = "Save all or specified killeffect")
    @Require("array.killeffect.admin")
    public void save(@Sender CommandSender sender, @OptArg KillEffect killEffect) {
        if (killEffect == null) {
            plugin.getKillEffectManager().getKillEffects().forEach(k -> plugin.getKillEffectManager().save(k));
        } else {
            plugin.getKillEffectManager().save(killEffect);
        }
        sender.sendMessage(CC.translate("&8[&c&lArray&8] &7Successfully saved &c" + killEffect == null ? "all" : killEffect.getName() + " &7killeffect to our database."));
    }

    @Command(name = "setdata", aliases = "data", desc = "Set your kill effect's data")
    @Require("array.killeffect.admin")
    public void setData(@Sender CommandSender sender, KillEffect killEffect, int data) {
        killEffect.setData(data);
        plugin.getKillEffectManager().save(killEffect);
        sender.sendMessage(CC.translate("&8[&c&lArray&8] &7Successfully set &c" + killEffect.getName() + "'s &7Effect Data to &c" + data));
    }

    @Command(name = "seteffect", aliases = "effect", usage = "<killeffect> <effect>", desc = "Set your kill effect's effect")
    @Require("array.killeffect.admin")
    public void setEffect(@Sender CommandSender sender, KillEffect killEffect, String effect) {
        if (effect.equalsIgnoreCase("none")) {
            killEffect.setEffect(null);
            plugin.getKillEffectManager().save(killEffect);
            sender.sendMessage(CC.translate("&8[&c&lArray&8] &7Successfully removed the effect from &c" + killEffect.getName() + "&7."));
            return;
        }

        Effect bukkitEffect = null;
        try {
             bukkitEffect = Effect.valueOf(effect.toUpperCase());
        } catch (Exception e) {
            sender.sendMessage(CC.translate("&8[&c&lArray&8] &7Invalid Effect!"));
            return;
        }

        killEffect.setEffect(bukkitEffect);
        plugin.getKillEffectManager().save(killEffect);
        sender.sendMessage(CC.translate("&8[&c&lArray&8] &7Successfully updated the effect for &c" + killEffect.getName() + "&7."));
    }

    @Command(name = "seticon", aliases = "icon", usage = "<>", desc = "Set your kill effect's icon to the item in your hand")
    @Require("array.killeffect.admin")
    public void setIcon(@Sender Player sender, KillEffect killEffect) {
        ItemStack itemStack = sender.getItemInHand();
        if (itemStack == null) {
            sender.sendMessage(CC.translate("&8[&c&lArray&8] &7Please hold a valid item in your hand&7."));
            return;
        }
        killEffect.setItemStack(itemStack);
        plugin.getKillEffectManager().save(killEffect);
        sender.sendMessage(CC.translate("&8[&c&lArray&8] &7Successfully updated &c" + killEffect.getName() + "'s &7icon to the item you're holding."));
    }

    @Command(name = "setpriority", aliases = "priority", usage = "<killeffect> <priority>", desc = "Set your kill effect's menu priorty")
    public void setPriority(@Sender CommandSender sender, KillEffect killEffect, int priority) {
        killEffect.setPriority(priority);
        plugin.getKillEffectManager().save(killEffect);
        sender.sendMessage(CC.translate("&8[&c&lArray&8] &7Successfully updated &c" + killEffect.getName() + "&7's priority."));
    }
}
