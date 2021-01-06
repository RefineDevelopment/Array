package me.array.ArrayPractice.util.profile.option.event;

import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.array.ArrayPractice.util.external.BaseEvent;
import me.array.ArrayPractice.util.profile.option.menu.ProfileOptionButton;
import org.bukkit.entity.Player;

@RequiredArgsConstructor
@Getter
public class OptionsOpenedEvent extends BaseEvent {

	private final Player player;
	private List<ProfileOptionButton> buttons = new ArrayList<>();

}
