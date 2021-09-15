package xyz.refinedev.practice.event.task;

import xyz.refinedev.practice.event.Event;
import xyz.refinedev.practice.event.EventState;
import xyz.refinedev.practice.event.meta.EventTask;

public class EventRoundEndTask extends EventTask {

	public EventRoundEndTask(Event event) {
		super(event, EventState.ROUND_ENDING);
	}

	@Override
	public void onRun() {

	}
}
