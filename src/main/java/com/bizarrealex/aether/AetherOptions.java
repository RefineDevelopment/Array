package com.bizarrealex.aether;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true, fluent = true)
public class AetherOptions {

    static AetherOptions defaultOptions() {
        return new AetherOptions()
                .hook(false)
                .scoreDirectionDown(false);
    }

    private boolean hook;
    private boolean scoreDirectionDown;

}
