package xyz.refinedev.practice.event.task;

import xyz.refinedev.practice.Locale;
import xyz.refinedev.practice.event.Event;
import xyz.refinedev.practice.event.EventState;
import xyz.refinedev.practice.event.impl.sumo.solo.task.SumoSoloRoundStartTask;
import xyz.refinedev.practice.event.meta.EventTask;
import xyz.refinedev.practice.util.other.Cooldown;

public class EventStartTask extends EventTask {

	public EventStartTask(Event event) {
		super(event, EventState.WAITING);
	}

	@Override
	public void onRun() {
	}
}
