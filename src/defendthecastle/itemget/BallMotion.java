package defendthecastle.itemget;

import java.awt.Point;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

//排出ボールの調整
class BallMotion{
	private final ScheduledExecutorService scheduler;
	private ScheduledFuture<?> motionFuture;
	private final OpenBallMotion openBallMotion;
	private HandleMotion handleMotion;
	private double angle;
	private Point point;
	private final List<Integer> moveList = Arrays.asList(296, 310, 360, 320, 340);
	private final List<Integer> distanceList = Arrays.asList(3, 1, 2, -2, 1);
	private int moveNumber;
	
	BallMotion(OpenBallMotion openBallMotion, ScheduledExecutorService scheduler) {
		this.openBallMotion = openBallMotion;
		this.scheduler = scheduler;
		reset();
	}
	
	void reset() {
		angle = 0;
		point = new Point(159, 275);
		moveNumber = 0;
	}
	
	void timerStart(HandleMotion handleMotion) {
		this.handleMotion = handleMotion;
		motionTimer();
	}
	
	void motionTimer() {
		motionFuture = motionFuture();
	}
	
	ScheduledFuture<?> motionFuture(){
		return scheduler.scheduleAtFixedRate(this::motionFutureProcess, 0, 30, TimeUnit.MILLISECONDS);
	}
	
	void motionFutureProcess() {
		angle += 0.2;
		point.y += moveDistance();
	}
	
	int moveDistance() {
		try {
			if(point.y == moveList.get(moveNumber)) {
				moveNumber++;
			}
			return distanceList.get(moveNumber);
		}catch(Exception e) {
			timerStop();
			openBallMotion.timerStart(handleMotion);
			return 0;
		}
	}
	
	void timerStop() {
		reset();
		motionFuture.cancel(true);
	}
	
	double getBallAngel() {
		return angle;
	}
	
	Point getBallPosition() {
		return point;
	}
}