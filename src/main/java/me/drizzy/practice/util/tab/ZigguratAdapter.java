package me.drizzy.practice.util.tab;

import me.drizzy.practice.util.tab.utils.*;
import org.bukkit.entity.*;

import java.util.*;

public interface ZigguratAdapter {

    Set<BufferedTabObject> getSlots(Player player);

    String getFooter();

    String getHeader();

}
