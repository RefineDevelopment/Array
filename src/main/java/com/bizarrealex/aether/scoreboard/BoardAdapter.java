package com.bizarrealex.aether.scoreboard;

import org.bukkit.entity.*;
import com.bizarrealex.aether.scoreboard.cooldown.*;
import java.util.*;

public interface BoardAdapter
{
    String getTitle(final Player p0);
    
    List<String> getScoreboard(final Player p0, final Board p1, final Set<BoardCooldown> p2);
}
