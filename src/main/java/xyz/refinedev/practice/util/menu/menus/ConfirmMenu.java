package xyz.refinedev.practice.util.menu.menus;

import org.bukkit.entity.Player;
import xyz.refinedev.practice.util.menu.Button;
import xyz.refinedev.practice.util.menu.Menu;
import xyz.refinedev.practice.util.menu.TypeCallback;
import xyz.refinedev.practice.util.menu.button.ConfirmationButton;

import java.util.HashMap;
import java.util.Map;

public class ConfirmMenu extends Menu {

	private String title;
	private TypeCallback<Boolean> response;
	private boolean closeAfterResponse;
	private Button[] centerButtons;

	public ConfirmMenu(String title, TypeCallback<Boolean> response, boolean closeAfter, Button... centerButtons) {
		this.title = title;
		this.response = response;
		this.closeAfterResponse = closeAfter;
		this.centerButtons = centerButtons;
	}

	@Override
	public Map<Integer, Button> getButtons(Player player) {
		HashMap<Integer, Button> buttons = new HashMap<>();
		for (int x = 0; x < 3; x++) {
			for (int y = 0; y < 3; y++) {
				buttons.put(getSlot(x, y), new ConfirmationButton(true, response, closeAfterResponse));
				buttons.put(getSlot(8 - x, y), new ConfirmationButton(false, response, closeAfterResponse));
			}
		}
		if (centerButtons != null) {
			for (int i = 0; i < centerButtons.length; i++) {
				if (centerButtons[i] != null) buttons.put(getSlot(4, i), centerButtons[i]);
			}
		}
		return buttons;
	}

	@Override
	public String getTitle(Player player) {
		return title;
	}
}
