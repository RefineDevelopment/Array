package me.array.ArrayPractice.util.bootstrap;

import me.array.ArrayPractice.Array;
import lombok.Getter;

@Getter
public class Bootstrapped {

    protected final Array Array;

    public Bootstrapped(Array Array) {
        this.Array=Array;
    }

}
