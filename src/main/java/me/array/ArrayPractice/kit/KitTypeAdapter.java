package me.array.ArrayPractice.kit;

import com.qrakn.honcho.command.adapter.CommandTypeAdapter;

public class KitTypeAdapter implements CommandTypeAdapter {

	@Override
	public <T> T convert(String string, Class<T> type) {
		return type.cast(Kit.getByName(string));
	}

}
