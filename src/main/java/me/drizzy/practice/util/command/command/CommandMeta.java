package me.drizzy.practice.util.command.command;

import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Retention;
import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface CommandMeta {
    String[] label();
    
    String[] options() default {};
    
    String permission() default "";
    
    String description() default "";
    
    boolean autoAddSubCommands() default true;
    
    boolean async() default false;
}
