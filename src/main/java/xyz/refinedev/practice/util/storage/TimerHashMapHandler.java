package xyz.refinedev.practice.util.storage;

/**
 * @author Elb1to
 */
public interface TimerHashMapHandler<E> {

    public void onExpire(E var1);

    public long getTimestamp(E var1);
}

