package me.drizzy.practice.util.command.modifier;

import me.drizzy.practice.util.command.command.CommandExecution;
import me.drizzy.practice.util.command.exception.CommandExitMessage;
import me.drizzy.practice.util.command.parametric.CommandParameter;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.annotation.Annotation;
import java.util.Optional;

public interface DrinkModifier<T> {

    Optional<T> modify(@Nonnull CommandExecution execution, @Nonnull CommandParameter commandParameter, @Nullable T argument) throws CommandExitMessage;

}
