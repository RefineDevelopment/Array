package me.drizzy.practice.util.tab.utils.impl;

import com.mojang.authlib.*;
import com.mojang.authlib.properties.*;
import me.drizzy.practice.util.tab.*;
import me.drizzy.practice.util.tab.utils.*;
import me.drizzy.practice.util.tab.utils.playerversion.*;
import net.minecraft.server.v1_8_R3.*;
import org.bukkit.*;
import org.bukkit.craftbukkit.v1_8_R3.entity.*;
import org.bukkit.entity.*;
import org.bukkit.scoreboard.*;

import java.util.*;

public class v1_8TabImpl implements IZigguratHelper {

    private static final MinecraftServer server = MinecraftServer.getServer();
    private static final WorldServer world = server.getWorldServer(0);
    private static final PlayerInteractManager manager = new PlayerInteractManager(world);

    public v1_8TabImpl() {
    }

    @Override
    public TabEntry createFakePlayer(ZigguratTablist zigguratTablist, String string, TabColumn column, Integer slot, Integer rawSlot) {
        final OfflinePlayer offlinePlayer = new OfflinePlayer() {
            private final UUID uuid = UUID.randomUUID();

            @Override
            public boolean isOnline() {
                return true;
            }

            @Override
            public String getName() {
                return string;
            }

            @Override
            public UUID getUniqueId() {
                return uuid;
            }

            @Override
            public boolean isBanned() {
                return false;
            }

            @Override
            public void setBanned(boolean b) {

            }

            @Override
            public boolean isWhitelisted() {
                return false;
            }

            @Override
            public void setWhitelisted(boolean b) {

            }

            @Override
            public Player getPlayer() {
                return null;
            }

            @Override
            public long getFirstPlayed() {
                return 0;
            }

            @Override
            public long getLastPlayed() {
                return 0;
            }

            @Override
            public boolean hasPlayedBefore() {
                return false;
            }

            @Override
            public Location getBedSpawnLocation() {
                return null;
            }

            @Override
            public Map<String, Object> serialize() {
                return null;
            }

            @Override
            public boolean isOp() {
                return false;
            }

            @Override
            public void setOp(boolean b) {

            }
        };

        final Player player = zigguratTablist.getPlayer();

        final PlayerVersion playerVersion = PlayerUtility.getPlayerVersion(player);

        final GameProfile profile = new GameProfile(offlinePlayer.getUniqueId(), LegacyClientUtils.tabEntrys.get(rawSlot - 1) + "");

        final EntityPlayer entity = new EntityPlayer(server, world, profile, manager);

        if (playerVersion != PlayerVersion.v1_7) {
            profile.getProperties().put("textures", new Property("textures",ZigguratCommons.defaultTexture.SKIN_VALUE, ZigguratCommons.defaultTexture.SKIN_SIGNATURE));
        }

        entity.ping = 1;

        final PacketPlayOutPlayerInfo packet = new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.ADD_PLAYER, entity);

        this.sendPacket(zigguratTablist.getPlayer(), packet);

        return new TabEntry(string, offlinePlayer, "", zigguratTablist, ZigguratCommons.defaultTexture, column, slot, rawSlot, 0);
    }

    @Override
    public void updateFakeName(ZigguratTablist zigguratTablist, TabEntry tabEntry, String text) {

        if (tabEntry.getText().equals(text)) {
            return;
        }

        final Player player = zigguratTablist.getPlayer();

        final String[] newStrings = ZigguratTablist.splitStrings(text, tabEntry.getRawSlot());

        Team team = player.getScoreboard().getTeam(LegacyClientUtils.teamNames.get(tabEntry.getRawSlot()-1));
        if (team == null) {
            team = player.getScoreboard().registerNewTeam(LegacyClientUtils.teamNames.get(tabEntry.getRawSlot()-1));
        }
        team.setPrefix(ChatColor.translateAlternateColorCodes('&', newStrings[0]));
        if (newStrings.length > 1) {
            team.setSuffix(ChatColor.translateAlternateColorCodes('&', newStrings[1]));
        } else {
            team.setSuffix("");
        }

        tabEntry.setText(text);
    }

    @Override
    public void updateFakeLatency(ZigguratTablist zigguratTablist, TabEntry tabEntry, Integer latency) {
        if (tabEntry.getLatency() == latency) {
            return;
        }

        final GameProfile profile = new GameProfile(tabEntry.getOfflinePlayer().getUniqueId(), LegacyClientUtils.tabEntrys.get(tabEntry.getRawSlot() - 1) + "");
        final EntityPlayer entity = new EntityPlayer(server, world, profile, manager);

        entity.ping = latency;

        final PacketPlayOutPlayerInfo packet = new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.UPDATE_LATENCY, entity);

        sendPacket(zigguratTablist.getPlayer(), packet);

        tabEntry.setLatency(latency);
    }

    @Override
    public void updateFakeSkin(ZigguratTablist zigguratTablist, TabEntry tabEntry, SkinTexture skinTexture) {

        if (tabEntry.getTexture() == skinTexture){
            return;
        }

        final GameProfile profile = new GameProfile(tabEntry.getOfflinePlayer().getUniqueId(), LegacyClientUtils.tabEntrys.get(tabEntry.getRawSlot() - 1) + "");

        final EntityPlayer entity = new EntityPlayer(server, world, profile, manager);

        profile.getProperties().put("textures", new Property("textures", skinTexture.SKIN_VALUE, skinTexture.SKIN_SIGNATURE));

        PacketPlayOutPlayerInfo removePlayer = new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.REMOVE_PLAYER, entity);

        this.sendPacket(zigguratTablist.getPlayer(), removePlayer);

        PacketPlayOutPlayerInfo addPlayer = new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.ADD_PLAYER, entity);

        this.sendPacket(zigguratTablist.getPlayer(), addPlayer);

        tabEntry.setTexture(skinTexture);
    }

    @Override
    public void updateHeaderAndFooter(ZigguratTablist zigguratTablist, String header, String footer) {

    }

    private void sendPacket(Player player, Packet packet) {
        getEntity(player).playerConnection.sendPacket(packet);
    }

    private EntityPlayer getEntity(Player player) {
        return ((CraftPlayer)player).getHandle();
    }

}
