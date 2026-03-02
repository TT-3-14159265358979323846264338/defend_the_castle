package defendthecastle.battle.timer;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import defendthecastle.battle.InternalData.GameTimer;

public class MicroSecondsTimerOperation extends TimerOperation{
	public MicroSecondsTimerOperation(GameTimer gameTimer, ScheduledExecutorService scheduler) {
		super(gameTimer, scheduler);
	}
	
	@Override
	ScheduledFuture<?> createTimer(long stopTime, int dafaultDelay, Runnable task){
		return getScheduler().scheduleAtFixedRate(task, initialDelay(stopTime, dafaultDelay), dafaultDelay, TimeUnit.MICROSECONDS);
	}
	
	@Override
	long remainingDelay(long stopTime, int dafaultDelay) {
		return ((stopTime - getBeforeTime()) * 1000 < dafaultDelay)? dafaultDelay - (stopTime - getBeforeTime()) * 1000: getNoneDelay();
	}
}