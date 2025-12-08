package defendthecastle.itemget;

import java.awt.Point;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

//ボール開封の調整
class OpenBallMotion{
	private MenuItemGet MenuItemGet;
	private HoldMedal HoldMedal;
	private GachaInformation GachaInformation;
	private HandleMotion HandleMotion;
	private double bottomAngle;
	private double topAngle;
	private Point bottomPoint;
	private Point topPoint;
	private int color;
	private int expansion;
	private ScheduledExecutorService scheduler;
	private ScheduledFuture<?> openFuture;
	
	protected OpenBallMotion(MenuItemGet MenuItemGet, HoldMedal HoldMedal, GachaInformation GachaInformation, ScheduledExecutorService scheduler) {
		this.MenuItemGet = MenuItemGet;
		this.HoldMedal = HoldMedal;
		this.GachaInformation = GachaInformation;
		this.scheduler = scheduler;
		openFuture = scheduler.schedule(() -> null, 0, TimeUnit.MILLISECONDS);
		reset();
	}
	
	protected void timerStart(HandleMotion HandleMotion) {
		this.HandleMotion = HandleMotion;
		openTimer();
	}
	
	private void openTimer() {
		openFuture = scheduler.scheduleAtFixedRate(() -> {
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
		}, 0, 40, TimeUnit.MILLISECONDS);
	}
	
	private void timerStop() {
		reset();
		HandleMotion.addListener();
		MenuItemGet.activatePanel();
		new GachaResult(GachaInformation, HoldMedal);
		openFuture.cancel(true);
	}
	
	protected boolean canRunTimer() {
		return !openFuture.isDone();
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
}