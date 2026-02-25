package defendthecastle;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

//最終画面の位置調整
class FinalMotion{
	private ScheduledFuture<?> finalFuture;
	private final int number;
	private final int x;
	private int y;
	private int count;
	
	FinalMotion(int number) {
		this.number = number;
		x = 100 * (number % 5);
		y = 300;
	}
	
	void finalTimerStart(ScheduledExecutorService scheduler) {
		finalFuture = createFinalFuture(scheduler);
	}
	
	ScheduledFuture<?> createFinalFuture(ScheduledExecutorService scheduler){
		return scheduler.scheduleAtFixedRate(this::finalTimerProcess, 0, 50, TimeUnit.MILLISECONDS);
	}
	
	void finalTimerProcess() {
		y -= 10 * (number / 5);
		count++;
		timerStop();
	}
	
	boolean canEnd() {
		return finalFuture.isCancelled();
	}
	
	void timerStop() {
		if(10 < count) {
			finalFuture.cancel(true);
		}
	}
	
	int getX() {
		return x;
	}
	
	int getY() {
		return y;
	}
}