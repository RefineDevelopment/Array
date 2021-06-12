package xyz.refinedev.practice.arena;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ArenaType {
    STANDALONE,
    SHARED,
    THEBRIDGE,
    FFA,
    DUPLICATE
}
