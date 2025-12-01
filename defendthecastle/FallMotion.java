package defendthecastle;

import java.util.Random;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

//落下コアの位置調整
class FallMotion{
	private ScheduledFuture<?> fallFuture;
	private double angle = randomAngle();
	private int x = randomX();
	private int y = -100;
	private boolean canStart;
	private final double ANGLE_CHANGE = 0.1;
	private final int COODINATE_CHANGE = 10;
	private final int FINAL_COODINATE = 450;
	
	void fallTimerStart(ScheduledExecutorService scheduler) {
		setStart(true);
		setFallFuture(scheduler.scheduleAtFixedRate(this::fallTimerProcess, 0, 20, TimeUnit.MILLISECONDS));
	}
	
	void fallTimerProcess() {
		angle += ANGLE_CHANGE;
		y += COODINATE_CHANGE;
		timerStop();
	}
	
	void timerStop() {
		if(FINAL_COODINATE < y) {
			setStart(false);
			fallFuture.cancel(true);
		}
	}
	
	void setFallFuture(ScheduledFuture<?> future){
		fallFuture = future;
	}
	
	double randomAngle() {
		return new Random().nextInt((int) (Math.PI * 2 * 100)) / 100.0;
	}
	
	int randomX() {
		return new Random().nextInt(400);
	}
	
	void setStart(boolean exists) {
		canStart = exists;
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
	
	void setY(int value) {
		y = value;
	}
	
	int getY() {
		return y;
	}
}