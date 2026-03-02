package defendthecastle.battle.timer;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import defendthecastle.battle.InternalData.GameTimer;

public class ScheduleTimerOperation extends TimerOperation{
	private int dafualtDelay;
	
	public ScheduleTimerOperation(GameTimer gameTimer, ScheduledExecutorService scheduler) {
		super(gameTimer, scheduler);
	}
	
	@Override
	public void timerStrat(long stopTime, int dafualtDelay, Runnable task) {
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
	
	public int getDelay() {
		return dafualtDelay;
	}
}