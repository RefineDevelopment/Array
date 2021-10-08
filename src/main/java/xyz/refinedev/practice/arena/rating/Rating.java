package xyz.refinedev.practice.arena.rating;

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
@AllArgsConstructor
public class Rating {

    private final Arena arena;
    private int terrible, average, decent, okay, good;

    /**
     * Increment the rating integer for the arena
     *
     * @param type {@link RatingType} the type of rating to be incremented
     */
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
