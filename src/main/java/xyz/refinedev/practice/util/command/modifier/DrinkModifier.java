package xyz.refinedev.practice.util.command.modifier;

import xyz.refinedev.practice.util.command.command.CommandExecution;
import xyz.refinedev.practice.util.command.exception.CommandExitMessage;
import xyz.refinedev.practice.util.command.parametric.CommandParameter;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Optional;

public interface DrinkModifier<T> {

    Optional<T> modify(@Nonnull CommandExecution execution, @Nonnull CommandParameter commandParameter, @Nullable T argument) throws CommandExitMessage;

}
