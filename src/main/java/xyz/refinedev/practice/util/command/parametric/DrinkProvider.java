package xyz.refinedev.practice.util.command.parametric;

import xyz.refinedev.practice.util.command.argument.CommandArg;
import xyz.refinedev.practice.util.command.exception.CommandExitMessage;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.annotation.Annotation;
import java.util.List;

public abstract class DrinkProvider<T> {

    public abstract boolean doesConsumeArgument();

    public abstract boolean isAsync();

    public boolean allowNullArgument() {
        return true;
    }

    @Nullable
    public T defaultNullValue() {
        return null;
    }

    @Nullable
    public abstract T provide(@Nonnull CommandArg arg, @Nonnull List<? extends Annotation> annotations) throws CommandExitMessage;

    public abstract String argumentDescription();

    public abstract List<String> getSuggestions(@Nonnull String prefix);

    protected boolean hasAnnotation(List<? extends Annotation> list, Class<? extends Annotation> a) {
        return list.stream().anyMatch(annotation -> annotation.annotationType().equals(a));
    }

}
