package me.drizzy.practice.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author Drizzy
 * Created at 2/3/2021
 */
@Getter
@AllArgsConstructor
public enum ClanProfileType {

    LEADER(0),
    CAPTAIN(1),
    MEMBER(2);

    private final int weight;
}
