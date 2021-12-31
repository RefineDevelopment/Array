package xyz.refinedev.practice.event.task;

import xyz.refinedev.practice.Array;
import xyz.refinedev.practice.event.Event;
import xyz.refinedev.practice.event.EventState;
import xyz.refinedev.practice.event.meta.EventTask;

public class EventRoundEndTask extends EventTask {

	private final Event event;
	
	public EventRoundEndTask(Array plugin, Event event) {
		super(plugin, event, EventState.ROUND_ENDING);
		
		this.event = event;
	}

	@Override
	public void onRun() {
		if (this.event.canEnd()) {
			this.event.handleEnd();
		} else {
			if (this.getTicks() >= 3) {
				this.event.onRound();
			}
		}
	}
}
