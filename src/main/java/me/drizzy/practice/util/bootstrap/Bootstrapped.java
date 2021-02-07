package me.drizzy.practice.util.bootstrap;

import me.drizzy.practice.Array;
import lombok.Getter;

@Getter
public class Bootstrapped {

    protected final me.drizzy.practice.Array Array;

    public Bootstrapped(Array Array) {
        this.Array=Array;
    }

}
