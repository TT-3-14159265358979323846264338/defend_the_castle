package defendthecastle.itemget;

import java.awt.Point;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

//排出ボールの調整
class BallMotion{
	private OpenBallMotion OpenBallMotion;
	private HandleMotion HandleMotion;
	private double angle;
	private Point point;
	private List<Integer> moveList = Arrays.asList(296, 310, 360, 320, 340);
	private List<Integer> distanceList = Arrays.asList(3, 1, 2, -2, 1);
	private int moveNumber;
	private ScheduledExecutorService scheduler;
	private ScheduledFuture<?> motionFuture;
	
	protected BallMotion(OpenBallMotion OpenBallMotion, ScheduledExecutorService scheduler) {
		this.OpenBallMotion = OpenBallMotion;
		this.scheduler = scheduler;
		reset();
	}
	
	private void reset() {
		angle = 0;
		point = new Point(159, 275);
		moveNumber = 0;
	}
	
	protected void timerStart(HandleMotion HandleMotion) {
		this.HandleMotion = HandleMotion;
		motionTimer();
	}
	
	private void motionTimer() {
		motionFuture = scheduler.scheduleAtFixedRate(() -> {
			angle += 0.2;
			point.y += moveDistance();
		}, 0, 30, TimeUnit.MILLISECONDS);
	}
	
	private int moveDistance() {
		try {
			if(point.y == moveList.get(moveNumber)) {
				moveNumber++;
			}
			return distanceList.get(moveNumber);
		}catch(Exception e) {
			timerStop();
			OpenBallMotion.timerStart(HandleMotion);
			return 0;
		}
	}
	
	private void timerStop() {
		motionFuture.cancel(true);
		reset();
	}
	
	protected double getBallAngel() {
		return angle;
	}
	
	protected Point getBallPosition() {
		return point;
	}
}