package xyz.refinedev.practice.tournament;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.bukkit.entity.Player;
import xyz.refinedev.practice.kit.Kit;

/**
 * This Project is property of Refine Development © 2021
 * Redistribution of this Project is not allowed
 *
 * @author Drizzy
 * Created: 10/31/2021
 * Project: Array
 */

@Getter @Setter
@RequiredArgsConstructor
public class TournamentProcedure {

    private final Player player;
    private final Kit kit;
    private int teamSize;
    private int playerSize;
    private int id;
}
