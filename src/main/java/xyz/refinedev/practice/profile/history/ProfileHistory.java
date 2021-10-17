package xyz.refinedev.practice.profile.history;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.bson.Document;
import xyz.refinedev.practice.Array;
import xyz.refinedev.practice.kit.Kit;
import xyz.refinedev.practice.match.MatchSnapshot;

import java.util.Date;

/**
 * This Project is property of Refine Development © 2021
 * Redistribution of this Project is not allowed
 *
 * @author Drizzy
 * Created: 9/25/2021
 * Project: Array
 */

@Getter @Setter
@RequiredArgsConstructor
public class ProfileHistory {

    private final Array plugin;

    private final long date;
    private final MatchSnapshot playerSnapshot;
    private final MatchSnapshot opponentSnapshot;

    private final Kit kit;
    private boolean won;
    private boolean ranked;

    private int winnerChangedELO;
    private int looserChangedELO;
}
