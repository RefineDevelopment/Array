package xyz.refinedev.practice.util.tablist.entry;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import xyz.refinedev.practice.util.tablist.skin.Skin;

@Setter @Accessors(chain = true) 
@Getter
@AllArgsConstructor
public class TabEntry {

	private final int column;
    private final int row;
    
	private String text;
    
    private int ping = 0;
    private Skin skin = Skin.DEFAULT_SKIN;

    public TabEntry(int column, int row, String text) {
        this.column = column;
        this.row = row;
        this.text = text;
    }
}