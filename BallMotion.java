package defendthecastle.itemget;

import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;
import java.util.List;

import javax.swing.Timer;

//排出ボールの調整
class BallMotion implements ActionListener{
	private OpenBallMotion OpenBallMotion;
	private HandleMotion HandleMotion;
	private Timer timer = new Timer(30, this);
	private double angle;
	private Point point;
	private List<Integer> moveList = Arrays.asList(296, 310, 360, 320, 340);
	private List<Integer> distanceList = Arrays.asList(3, 1, 2, -2, 1);
	private int moveNumber;
	
	protected BallMotion(OpenBallMotion OpenBallMotion) {
		this.OpenBallMotion = OpenBallMotion;
		reset();
	}
	
	protected void timerStart(HandleMotion HandleMotion) {
		this.HandleMotion = HandleMotion;
		timer.start();
	}
	
	private void timerStop() {
		timer.stop();
		reset();
	}
	
	protected double getBallAngel() {
		return angle;
	}
	
	protected Point getBallPosition() {
		return point;
	}
	
	private void reset() {
		angle = 0;
		point = new Point(159, 275);
		moveNumber = 0;
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		angle += 0.2;
		point.y += moveDistance();
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
}