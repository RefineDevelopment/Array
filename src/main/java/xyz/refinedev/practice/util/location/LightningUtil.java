package xyz.refinedev.practice.util.location;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketContainer;
import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.util.concurrent.ThreadLocalRandom;

/**
 * This Project is property of Refine Development © 2021
 * Redistribution of this Project is not allowed
 *
 * @author Drizzy
 * Created: 9/27/2021
 * Project: Array
 */

@UtilityClass
public class LightningUtil {

    /**
     * Lightning through Protocol Lib cuz we care about the
     * environment
     *
     * @param location {@link Location} where the lightning should spawn
     */
    @SneakyThrows
    public void spawnLightning(Player player, Location location) {
        PacketContainer lightningPacket = new PacketContainer(PacketType.Play.Server.SPAWN_ENTITY_WEATHER);

        lightningPacket.getModifier().writeDefaults();
        lightningPacket.getIntegers().write(0, 128);
        lightningPacket.getIntegers().write(4, 1);
        lightningPacket.getIntegers().write(1, (int) (location.getX() * 32.0));
        lightningPacket.getIntegers().write(2, (int) (location.getY() * 32.0));
        lightningPacket.getIntegers().write(3, (int) (location.getZ() * 32.0));

        ProtocolLibrary.getProtocolManager().sendServerPacket(player, lightningPacket);

        float thunderSoundPitch = 0.8f + ThreadLocalRandom.current().nextFloat() * 0.2f;
        float explodeSoundPitch = 0.5f + ThreadLocalRandom.current().nextFloat() * 0.2f;

        player.playSound(location, Sound.AMBIENCE_THUNDER, 10000.0f, thunderSoundPitch);
        player.playSound(location, Sound.EXPLODE, 2.0f, explodeSoundPitch);
    }
}
