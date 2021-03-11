package me.drizzy.practice.kiteditor;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

@Getter
public class KitEditRules {

    private final List<ItemStack> editorItems = new ArrayList<>();
    @Setter private boolean allowPotionFill;

}
