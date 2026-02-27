package defendthecastle.itemget;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.BiFunction;

//ガチャハンドルの調整
class HandleMotion implements MouseListener, MouseMotionListener{
	private final ScheduledExecutorService scheduler;
	private ScheduledFuture<?> handleFuture;
	private final MenuItemGet MenuItemGet;
	private final HoldMedal HoldMedal;
	private final BallMotion BallMotion;
	private int startPointX;
	private int startPointY;
	private int activePointX;
	private int activePointY;
	private double angle;
	
	HandleMotion(MenuItemGet menuItemGet, HoldMedal holdMedal, BallMotion ballMotion, ScheduledExecutorService scheduler) {
		this.MenuItemGet = menuItemGet;
		this.HoldMedal = holdMedal;
		this.BallMotion = ballMotion;
		this.scheduler = scheduler;
		addListener();
	}
	
	void addListener() {
		MenuItemGet.addMouseListener(this);
		MenuItemGet.addMouseMotionListener(this);
	}
	
	void removeListener() {
		MenuItemGet.removeMouseListener(this);
		MenuItemGet.removeMouseMotionListener(this);
	}
	
	double angle() {
		if(handleFuture != null) {
			return autoAngle();
		}
		return manualAngle();
	}
	
	double autoAngle() {
		if(Math.PI * 2 < angle) {
			autoTurnStop();
		}
		return angle;
	}
	
	void autoTurnStop() {
		handleFuture.cancel(true);
		handleFuture = null;
		BallMotion.timerStart(this);
		reset();
	}
	
	void reset() {
		startPointX = 0;
		startPointY = 0;
		activePointX = 0;
		activePointY = 0;
	}
	
	double manualAngle() {
		//x, y は 角度を求めたい点, 目標点1, 目標点2 の位置順
		//角度は余弦定理から算出
		double[] x = {175, activePointX, startPointX};
		double[] y = {250, activePointY, startPointY};
		double[] distance = new double[3];
		BiFunction<Double, Double, Double> pow = (x1, x2) -> {
			return Math.pow(x1 - x2, 2);
		};
		BiFunction<Integer, Integer, Double> sqrt = (i, j) -> {
			return Math.sqrt(pow.apply(x[i], x[j]) + pow.apply(y[i], y[j]));
		};
		distance[0] = sqrt.apply(1, 2);
		distance[1] = sqrt.apply(0, 2);
		distance[2] = sqrt.apply(0, 1);
		angle = Math.acos((Math.pow(distance[0], 2) - Math.pow(distance[1], 2) - Math.pow(distance[2], 2)) / (-2 * distance[1] * distance[2]));
		return angle;
	}
	
	@Override
	public void mouseDragged(MouseEvent e) {
		if(HoldMedal.canPossessMedal()) {
			activePointX = e.getX();
			activePointY = e.getY();
			if(Math.PI / 2.0 < angle()) {
				autoTurnStart();
			}
		}
	}
	@Override
	public void mouseMoved(MouseEvent e) {
	}
	@Override
	public void mouseClicked(MouseEvent e) {
	}
	@Override
	public void mousePressed(MouseEvent e) {
		startPointX = e.getX();
		startPointY = e.getY();
		activePointX = e.getX();
		activePointY = e.getY();
	}
	@Override
	public void mouseReleased(MouseEvent e) {
		reset();
	}
	@Override
	public void mouseEntered(MouseEvent e) {
	}
	@Override
	public void mouseExited(MouseEvent e) {
	}
	
	void autoTurnStart() {
		MenuItemGet.deactivatePanel();
		removeListener();
		handleTimer();
	}
	
	void handleTimer() {
		handleFuture = handleFuture();
	}
	
	ScheduledFuture<?> handleFuture(){
		return scheduler.scheduleAtFixedRate(this::handleFutureProcess, 0, 20, TimeUnit.MILLISECONDS);
	}
	
	void handleFutureProcess() {
		angle += 0.1;
	}
}