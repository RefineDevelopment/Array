package me.drizzy.practice.arena;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ArenaType {
    STANDALONE,
    SHARED,
    DUPLICATE
}
