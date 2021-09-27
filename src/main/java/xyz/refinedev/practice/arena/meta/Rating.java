package xyz.refinedev.practice.arena.meta;

import lombok.*;
import xyz.refinedev.practice.arena.Arena;

/**
 * This Project is property of Refine Development © 2021
 * Redistribution of this Project is not allowed
 *
 * @author Drizzy
 * Created at 7/12/2021
 * Project: Array
 */

@Getter @Setter
@RequiredArgsConstructor
public class Rating {

    private final Arena arena;
    protected int terrible;
    protected int average;
    protected int decent;
    protected int okay;
    protected int good;

    public void recordVote(RatingType type) {
        switch (type) {
            case TERRIBLE: {
                terrible++;
                break;
            }
            case AVERAGE: {
                average++;
                break;
            }
            case DECENT: {
                decent++;
                break;
            }
            case OKAY: {
                okay++;
                break;
            }
            case GOOD: {
                good++;
                break;
            }
        }
    }
}
