package defendthecastle;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

//最終画面の位置調整
class FinalMotion{
	private int number;
	private int x;
	private int y;
	private int count;
	
	protected FinalMotion(int number) {
		this.number = number;
		x = 100 * (number % 5);
		y = 300;
	}
	
	protected void finalTimerStart() {
		ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
		scheduler.scheduleWithFixedDelay(() -> {
			y -= 10 * (number / 5);
			count ++;
			if(10 < count) {
				scheduler.shutdown();
			}
		}, 0, 50, TimeUnit.MILLISECONDS);
	}
	
	protected int getX() {
		return x;
	}
	
	protected int getY() {
		return y;
	}
}