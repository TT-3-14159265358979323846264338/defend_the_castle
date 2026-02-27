package defendthecastle;

import java.util.Random;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

//落下コアの位置調整
class FallMotion{
	private ScheduledFuture<?> fallFuture;
	private double angle;
	private final int x;
	private int y = -100;
	private boolean canStart;
	private final double ANGLE_CHANGE = 0.1;
	private final int COODINATE_CHANGE = 10;
	private final int FINAL_COODINATE = 450;
	
	FallMotion(){
		angle = randomAngle();
		x = randomX();
	}
	
	double randomAngle() {
		return createRandom().nextInt((int) (Math.PI * 2 * 100)) / 100.0;
	}
	
	int randomX() {
		return createRandom().nextInt(400);
	}
	
	Random createRandom() {
		return new Random();
	}
	
	void fallTimerStart(ScheduledExecutorService scheduler) {
		fallFuture = createFallFuture(scheduler);
		canStart = true;
	}
	
	ScheduledFuture<?> createFallFuture(ScheduledExecutorService scheduler){
		return scheduler.scheduleAtFixedRate(this::fallTimerProcess, 0, 20, TimeUnit.MILLISECONDS);
	}
	
	void fallTimerProcess() {
		angle += ANGLE_CHANGE;
		y += COODINATE_CHANGE;
		timerStop();
	}
	
	void timerStop() {
		if(FINAL_COODINATE < y) {
			canStart = false;
			fallFuture.cancel(true);
			fallFuture = null;
		}
	}
	
	boolean canStart() {
		return canStart;
	}
	
	double getAngle() {
		return angle;
	}
	
	int getX() {
		return x;
	}
	
	int getY() {
		return y;
	}
}