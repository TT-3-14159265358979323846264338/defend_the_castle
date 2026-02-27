package defendthecastle.battle;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

class TimerOperation {
	private final GameTimer gameTimer;
	private final ScheduledExecutorService scheduler;
	private ScheduledFuture<?> future;
	private long beforeProcessingTime;
	private final int NONE_DELAY = 0;
	
	TimerOperation(GameTimer gameTimer, ScheduledExecutorService scheduler) {
		this.gameTimer = gameTimer;
		this.scheduler = scheduler;
	}
	
	ScheduledExecutorService getScheduler() {
		return scheduler;
	}
	
	void timerStrat(long stopTime, int dafualtDelay, Runnable task) {
		future = createTimer(stopTime, dafualtDelay, task);
	}
	
	ScheduledFuture<?> createTimer(long stopTime, int dafaultDelay, Runnable task){
		return scheduler.scheduleAtFixedRate(task, initialDelay(stopTime, dafaultDelay), dafaultDelay, TimeUnit.MILLISECONDS);
	}
	
	long initialDelay(long stopTime, int dafaultDelay) {
		long initialDelay;
		if(stopTime == NONE_DELAY) {
			initialDelay = defaultDelay();
		}else {
			initialDelay = remainingDelay(stopTime, dafaultDelay);
			beforeProcessingTime += System.currentTimeMillis() - stopTime;
		}
		return initialDelay;
	}
	
	int defaultDelay() {
		return NONE_DELAY;
	}
	
	long remainingDelay(long stopTime, int dafaultDelay) {
		return (stopTime - beforeProcessingTime < dafaultDelay)? dafaultDelay - (stopTime - beforeProcessingTime): NONE_DELAY;
	}
	
	long getBeforeTime() {
		return beforeProcessingTime;
	}
	
	int getNoneDelay() {
		return NONE_DELAY;
	}

	void updateBeforeTime() {
		beforeProcessingTime = System.currentTimeMillis();
	}
	
	boolean isRunningFuture() {
		if(future == null) {
			return false;
		}
		return !future.isDone();
	}
	
	void timerStop() {
		if(isRunningFuture()) {
			future.cancel(true);
			future = null;
		}
	}
	
	boolean timerPause(Consumer<Long> task) {
		if(future != null && !future.isCancelled()) {
			future.cancel(true);
			future = null;
			long time = System.currentTimeMillis();
			CompletableFuture.runAsync(gameTimer::timerWait, scheduler).thenRun(() -> task.accept(time));
			return true;
		}
		return false;
	}
}