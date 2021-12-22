package xyz.refinedev.practice.event.task;

import xyz.refinedev.practice.Array;
import xyz.refinedev.practice.event.Event;
import xyz.refinedev.practice.event.EventState;
import xyz.refinedev.practice.event.meta.EventTask;

public class EventRoundEndTask extends EventTask {

	public EventRoundEndTask(Array plugin, Event event) {
		super(plugin, event, EventState.ROUND_ENDING);
	}

	@Override
	public void onRun() {
		if (this.getEvent().canEnd()) {
			this.getEvent().handleEnd();
		} else {
			if (getTicks() >= 3) {
				this.getEvent().onRound();
			}
		}
	}
}
