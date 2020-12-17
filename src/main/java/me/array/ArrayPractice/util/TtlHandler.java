package me.array.ArrayPractice.util;

public interface TtlHandler<E> {
    void onExpire(final E p0);

    long getTimestamp(final E p0);
}
