package xyz.refinedev.practice.event.task;

import xyz.refinedev.practice.event.Event;
import xyz.refinedev.practice.event.EventState;
import xyz.refinedev.practice.event.meta.EventTask;

public class EventStartTask extends EventTask {

	public EventStartTask(Event event) {
		super(event, EventState.WAITING);
	}

	@Override
	public void onRun() {
	}
}
