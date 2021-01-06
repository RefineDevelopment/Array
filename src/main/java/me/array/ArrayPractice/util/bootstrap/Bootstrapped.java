package me.array.ArrayPractice.util.bootstrap;

import me.array.ArrayPractice.Practice;
import lombok.Getter;

@Getter
public class Bootstrapped {

    protected final Practice Practice;

    public Bootstrapped(Practice Practice) {
        this.Practice = Practice;
    }

}
