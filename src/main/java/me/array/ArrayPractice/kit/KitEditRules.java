package me.array.ArrayPractice.kit;

import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.inventory.ItemStack;

public class KitEditRules {

	@Getter @Setter private boolean allowPotionFill;
	@Getter private final List<ItemStack> editorItems = new ArrayList<>();

}
