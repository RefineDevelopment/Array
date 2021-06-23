package club.hydrogenpvp.core.util.menu.button;

import club.hydrogenpvp.core.util.InventoryUtil;
import club.hydrogenpvp.core.util.callback.TypeCallback;
import club.hydrogenpvp.core.util.menu.Button;
import club.hydrogenpvp.core.util.menu.Menu;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class ConfirmationButton extends Button {

	private boolean confirm;
	private TypeCallback<Boolean> callback;
	private boolean closeAfterResponse;

	@Override
	public ItemStack getButtonItem(Player player) {
		return InventoryUtil.makeItem(Material.WOOL, (this.confirm ? "&aConfirm" : "&cCancel"), null, (short) (this.confirm ? 5 : 14));
	}

	@Override
	public void clicked(Player player, ClickType clickType) {
		if (this.confirm) player.playSound(player.getLocation(), Sound.NOTE_PIANO, 20f, 0.1f);
		else player.playSound(player.getLocation(), Sound.DIG_GRAVEL, 20f, 0.1F);
		if (this.closeAfterResponse) {
			Menu menu = Menu.currentlyOpenedMenus.get(player.getName());
			if (menu != null) menu.setClosedByMenu(true);
			player.closeInventory();
		}
		this.callback.callback(this.confirm);
	}
}
