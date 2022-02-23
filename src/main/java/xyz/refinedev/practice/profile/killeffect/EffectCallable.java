package xyz.refinedev.practice.profile.killeffect;

import org.bukkit.entity.Item;
import org.bukkit.entity.Player;

import java.util.List;

public interface EffectCallable {

    public void call(Player var1, List<Player> var2, List<Item> items);
}