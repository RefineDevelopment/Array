package me.drizzy.practice.nametags.provider;

import me.drizzy.practice.nametags.NametagHandler;
import me.drizzy.practice.nametags.construct.NametagInfo;
import me.drizzy.practice.profile.Profile;
import lombok.AllArgsConstructor;
import lombok.Getter;
import me.drizzy.practice.util.chat.CC;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;

@Getter
@AllArgsConstructor
public abstract class NametagProvider {

    private final String name;
    private final int weight;

    public abstract NametagInfo fetchNametag(Player toRefresh, Player refreshFor);

    public static NametagInfo createNametag(String prefix, String suffix) {
        return (NametagHandler.getOrCreate(CC.translate(prefix), CC.translate(suffix)));
    }

    public static class DefaultNametagProvider extends NametagProvider {

        public DefaultNametagProvider() {
            super("Default Provider", 0);
        }

        @Override
        public NametagInfo fetchNametag(Player toRefresh, Player refreshFor) {
            for (PotionEffect potionEffect : toRefresh.getActivePotionEffects()) {
                if (potionEffect.getType().getName().equalsIgnoreCase("INVISIBILITY")) {
                    return null;
                }
            }
            return (createNametag(Profile.getByPlayer(toRefresh).getColor(), ""));
        }
    }
}