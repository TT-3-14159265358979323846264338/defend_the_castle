package defendthecastle.itemget;

import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;
import java.util.List;

import javax.swing.Timer;

//ボール開封の調整
class OpenBallMotion implements ActionListener{
	private MenuItemGet MenuItemGet;
	private HoldMedal HoldMedal;
	private DefaultLineup DefaultLineup;
	private HandleMotion HandleMotion;
	private Timer timer = new Timer(40, this);
	private double bottomAngle;
	private double topAngle;
	private Point bottomPoint;
	private Point topPoint;
	private int color;
	private int expansion;
	
	protected OpenBallMotion(MenuItemGet MenuItemGet, HoldMedal HoldMedal, DefaultLineup DefaultLineup) {
		this.MenuItemGet = MenuItemGet;
		this.HoldMedal = HoldMedal;
		this.DefaultLineup = DefaultLineup;
		reset();
	}
	
	protected void timerStart(HandleMotion HandleMotion) {
		this.HandleMotion = HandleMotion;
		timer.start();
	}
	
	private void timerStop() {
		timer.stop();
		reset();
		HandleMotion.addListener();
		MenuItemGet.activatePanel();
		DefaultLineup.setLineup();
		if(DefaultLineup.aptitudeTest()) {
			HoldMedal.recountMedal();
			new GachaResult(DefaultLineup);
		}
	}
	
	protected boolean canRunTimer() {
		return timer.isRunning();
	}
	
	protected List<Double> getBallAngle(){
		return Arrays.asList(bottomAngle, topAngle);
	}
	
	protected List<Point> getBallPosition(){
		return Arrays.asList(bottomPoint, topPoint);
	}
	
	protected int getColor() {
		return color;
	}
	
	protected int getExpansion() {
		return expansion;
	}
	
	private void reset() {
		bottomAngle = 0;
		topAngle = 0;
		bottomPoint = new Point(160, 345);
		topPoint = new Point(160, 335);
		color = 0;
		expansion = -250;
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		bottomAngle += 0.04;
		topAngle += 0.1;
		bottomPoint.x -= 2;
		bottomPoint.y += 1;
		topPoint.x += 2;
		topPoint.y -= 2;
		color += 5;
		expansion += 20;
		if(1 < bottomAngle) {
			timerStop();
		}
	}
}