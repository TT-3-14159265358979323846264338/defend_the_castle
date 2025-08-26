package defendthecastle.itemget;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.function.BiFunction;

import javax.swing.Timer;

//ガチャハンドルの調整
class HandleMotion implements MouseListener, MouseMotionListener, ActionListener{
	private MenuItemGet MenuItemGet;
	private HoldMedal HoldMedal;
	private BallMotion BallMotion;
	private Timer timer = new Timer(20, this);
	private int startPointX;
	private int startPointY;
	private int activePointX;
	private int activePointY;
	private double angle;
	
	protected HandleMotion(MenuItemGet MenuItemGet, HoldMedal HoldMedal, BallMotion BallMotion) {
		this.MenuItemGet = MenuItemGet;
		this.HoldMedal = HoldMedal;
		this.BallMotion = BallMotion;
		addListener();
	}
	
	protected void addListener() {
		MenuItemGet.addMouseListener(this);
		MenuItemGet.addMouseMotionListener(this);
	}
	
	private void removeListener() {
		MenuItemGet.removeMouseListener(this);
		MenuItemGet.removeMouseMotionListener(this);
	}
	
	protected double getAngle() {
		if(timer.isRunning()) {
			if(Math.PI * 2 < angle) {
				autoTurnStop();
			}
			return angle;
		}else {
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
	}
	
	private void autoTurnStart() {
		MenuItemGet.deactivatePanel();
		removeListener();
		timer.start();
	}
	
	private void autoTurnStop() {
		timer.stop();
		BallMotion.timerStart(this);
		reset();
	}
	
	private void reset() {
		startPointX = 0;
		startPointY = 0;
		activePointX = 0;
		activePointY = 0;
	}
	
	@Override
	public void mouseDragged(MouseEvent e) {
		if(HoldMedal.checkMedal()) {
			activePointX = e.getX();
			activePointY = e.getY();
			if(Math.PI / 2.0 < getAngle()) {
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
	
	@Override
	public void actionPerformed(ActionEvent e) {
		angle += 0.1;
	}
}