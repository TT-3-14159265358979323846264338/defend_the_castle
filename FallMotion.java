package defendthecastle;

import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

//落下コアの位置調整
class FallMotion{
	private ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
	private double angle = new Random().nextInt((int) (Math.PI * 2 * 100)) / 100;
	private int x = new Random().nextInt(400);
	private int y = -100;
	private boolean canStart;
	
	protected void fallTimerStart() {
		canStart = true;
		scheduler.scheduleWithFixedDelay(() -> {
			angle += 0.1;
			y += 10;
			if(450 < y) {
				canStart = false;
				scheduler.shutdown();
			}
		}, 0, 20, TimeUnit.MILLISECONDS);
	}
	
	protected boolean getTimerStatus() {
		return canStart;
	}
	
	protected double getAngle() {
		return angle;
	}
	
	protected int getX() {
		return x;
	}
	
	protected int getY() {
		return y;
	}
}