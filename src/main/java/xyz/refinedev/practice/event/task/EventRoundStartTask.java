package xyz.refinedev.practice.event.task;

import xyz.refinedev.practice.event.Event;
import xyz.refinedev.practice.event.EventState;
import xyz.refinedev.practice.event.meta.EventTask;

public class EventRoundStartTask extends EventTask {

	public EventRoundStartTask(Event event) {
		super(event, EventState.ROUND_STARTING);
	}

	@Override
	public void onRun() {

	}
}
