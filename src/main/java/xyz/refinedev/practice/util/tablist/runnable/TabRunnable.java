package xyz.refinedev.practice.util.tablist.runnable;

import lombok.AllArgsConstructor;
import xyz.refinedev.practice.util.tablist.adapter.TabAdapter;
import xyz.refinedev.practice.util.tablist.entry.TabEntry;
import xyz.refinedev.practice.util.tablist.layout.TabLayout;
import xyz.refinedev.practice.util.tablist.skin.Skin;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

@AllArgsConstructor
public class TabRunnable implements Runnable {

	private TabAdapter adapter;

	@Override
	public void run() {
		for(Player player : Bukkit.getOnlinePlayers()) {
			TabLayout layout = TabLayout.getLayoutMapping().get(player.getUniqueId());

			for(TabEntry entry : adapter.getLines(player)) {
				layout.update(entry.getColumn(), entry.getRow(), entry.getText(), entry.getPing(), entry.getSkin());
			}

			for (int row = 0; row < 20; row++) {
				for (int column = 0; column < 3; column++) {
					if(layout.getByLocation(adapter.getLines(player), column, row) == null) {
						layout.update(column, row, "", 0, Skin.DEFAULT_SKIN);
					}
				}
			}
		}
	}
}
