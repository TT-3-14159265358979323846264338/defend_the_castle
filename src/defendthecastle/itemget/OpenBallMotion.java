package defendthecastle.itemget;

import java.awt.Point;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

//ボール開封の調整
class OpenBallMotion{
	private final ScheduledExecutorService scheduler;
	private ScheduledFuture<?> openFuture;
	private final MenuItemGet menuItemGet;
	private final HoldMedal holdMedal;
	private final GachaInformation gachaInformation;
	private final ItemGetImage itemGetImage;
	private HandleMotion handleMotion;
	private double bottomAngle;
	private double topAngle;
	private Point bottomPoint;
	private Point topPoint;
	private int color;
	private int expansion;
	
	OpenBallMotion(MenuItemGet menuItemGet, HoldMedal holdMedal, GachaInformation gachaInformation, ItemGetImage itemGetImage, ScheduledExecutorService scheduler) {
		this.menuItemGet = menuItemGet;
		this.holdMedal = holdMedal;
		this.gachaInformation = gachaInformation;
		this.scheduler = scheduler;
		this.itemGetImage = itemGetImage;
		openFuture = scheduler.schedule(() -> null, 0, TimeUnit.MILLISECONDS);
		reset();
	}
	
	void timerStart(HandleMotion handleMotion) {
		this.handleMotion = handleMotion;
		openTimer();
	}
	
	private void openTimer() {
		openFuture = openFuture();
	}
	
	ScheduledFuture<?> openFuture(){
		return scheduler.scheduleAtFixedRate(this::openFutureProcess, 0, 40, TimeUnit.MILLISECONDS);
	}
	
	void openFutureProcess(){
		bottomAngle += 0.04;
		topAngle += 0.1;
		bottomPoint.x -= 2;
		bottomPoint.y += 1;
		topPoint.x += 2;
		topPoint.y -= 2;
		color += 5;
		expansion += 20;
		IO.println(true);
		if(1 < bottomAngle) {
			timerStop();
		}
	}
	
	void timerStop() {
		gacha();
		reset();
		handleMotion.addListener();
		menuItemGet.activatePanel();
		openFuture.cancel(true);
	}
	
	void gacha() {
		new GachaResult(scheduler, gachaInformation, holdMedal, itemGetImage);
	}
	
	boolean canRunTimer() {
		return !openFuture.isDone();
	}
	
	List<Double> getBallAngle(){
		return Arrays.asList(bottomAngle, topAngle);
	}
	
	List<Point> getBallPosition(){
		return Arrays.asList(bottomPoint, topPoint);
	}
	
	int getColor() {
		return color;
	}
	
	int getExpansion() {
		return expansion;
	}
	
	void reset() {
		bottomAngle = 0;
		topAngle = 0;
		bottomPoint = new Point(160, 345);
		topPoint = new Point(160, 335);
		color = 0;
		expansion = -250;
	}
}