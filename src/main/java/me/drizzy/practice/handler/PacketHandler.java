package me.drizzy.practice.handler;

import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import me.drizzy.practice.match.Match;
import me.drizzy.practice.match.MatchState;
import me.drizzy.practice.profile.Profile;
import net.minecraft.server.v1_8_R3.Packet;
import net.minecraft.server.v1_8_R3.PacketPlayInFlying;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

@RequiredArgsConstructor
public class PacketHandler extends ChannelDuplexHandler {

    private final Player player;
    private Location previousLocation;

    @Override
    public void write(ChannelHandlerContext context, Object object, ChannelPromise channelPromise) throws Exception {
        super.write(context, object, channelPromise);
    }

    @Override
    public void channelRead(ChannelHandlerContext context, Object object) throws Exception {

        Packet packet = (Packet) object;

        if (packet instanceof PacketPlayInFlying) {

            if (player.getLocation() != previousLocation) {
                // Here is where u can do the move events stuff
                final Profile profile = Profile.getByUuid(player.getUniqueId());
                if (profile.getMatch() != null) {
                    Match match=profile.getMatch();
                    if (profile.getMatch().getKit() != null) {
                        if (profile.getMatch().isSumoMatch() || profile.getMatch().isSumoTeamMatch() || profile.getMatch().getKit().getGameRules().isStickspawn()
                            || profile.getMatch().getKit().getGameRules().isSumo() || profile.getMatch().isTheBridgeMatch()) {
                            if (match.getState() == MatchState.STARTING) {
                                player.teleport(previousLocation);
                            }
                        }
                    }
                }

            }

            previousLocation = player.getLocation();
        }

        super.channelRead(context, object);
    }
}
