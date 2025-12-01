package defendthecastle;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

//最終画面の位置調整
class FinalMotion{
	private ScheduledFuture<?> finalFuture;
	private int number;
	private int x;
	private int y;
	private int count;
	
	FinalMotion(int number) {
		this.number = number;
		x = 100 * (number % 5);
		y = 300;
	}
	
	void finalTimerStart(ScheduledExecutorService scheduler) {
		finalFuture = scheduler.scheduleAtFixedRate(() -> {
			y -= 10 * (number / 5);
			count ++;
			if(10 < count) {
				finalFuture.cancel(true);
			}
		}, 0, 50, TimeUnit.MILLISECONDS);
	}
	
	int getX() {
		return x;
	}
	
	int getY() {
		return y;
	}
	
	boolean canEnd() {
		return finalFuture.isCancelled();
	}
}