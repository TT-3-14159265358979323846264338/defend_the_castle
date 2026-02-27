package defendthecastle.battle;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

class ScheduleTimerOperation extends TimerOperation{
	private int dafualtDelay;
	
	ScheduleTimerOperation(GameTimer gameTimer, ScheduledExecutorService scheduler) {
		super(gameTimer, scheduler);
	}
	
	@Override
	void timerStrat(long stopTime, int dafualtDelay, Runnable task) {
		this.dafualtDelay = dafualtDelay;
		super.timerStrat(stopTime, dafualtDelay, task);
	}
	
	@Override
	ScheduledFuture<?> createTimer(long stopTime, int dafaultDelay, Runnable task){
		return getScheduler().schedule(task, initialDelay(stopTime, dafaultDelay), TimeUnit.MILLISECONDS);
	}
	
	@Override
	int defaultDelay() {
		return dafualtDelay;
	}
	
	int getDelay() {
		return dafualtDelay;
	}
}