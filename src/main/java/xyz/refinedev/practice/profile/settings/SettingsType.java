package xyz.refinedev.practice.profile.settings;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.Material;

@AllArgsConstructor
@Getter
public enum SettingsType {

      TOGGLESCOREBOARD(), TOGGLEDUELREQUESTS(), TOGGLESPECTATORS(), TOGGLELIGHTNING(), TOGGLEPINGONSCOREBOARD(), TOGGLEDROPPROTECT(),
      TOGGLECPSONSCOREBOARD(), TOGGLEPINGFACTOR(), TOGGLETOURNAMENTMESSAGES(), TOGGLESHOWPLAYERS(), TOGGLETABSTYLE()
}
