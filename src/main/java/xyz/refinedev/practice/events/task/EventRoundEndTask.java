package xyz.refinedev.practice.events.task;

import xyz.refinedev.practice.events.Event;
import xyz.refinedev.practice.events.EventState;
import xyz.refinedev.practice.events.meta.EventTask;

public class EventRoundEndTask extends EventTask {

	public EventRoundEndTask(Event event) {
		super(event, EventState.ROUND_ENDING);
	}

	@Override
	public void onRun() {

	}
}
