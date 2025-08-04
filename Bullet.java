package battle;

import java.awt.image.BufferedImage;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.BiFunction;

import defaultdata.EditImage;

public class Bullet {
	ScheduledExecutorService bulletScheduler = Executors.newSingleThreadScheduledExecutor();
	ScheduledExecutorService hitScheduler = Executors.newSingleThreadScheduledExecutor();
	Battle Battle;
	BattleData myself;
	BattleData target;
	BufferedImage bulletImage;
	List<BufferedImage> hitImage;
	double positionX;
	double positionY;
	int bulletNumber = 0;
	int hitNumber = -1;
	final static int CORRECTION = 25;
	final static int COUNT = 5;
	
	protected Bullet(Battle Battle, BattleData myself, BattleData target, BufferedImage bulletImage, List<BufferedImage> hitImage) {
		this.Battle = Battle;
		this.myself = myself;
		this.target = target;
		this.bulletImage = bulletImage;
		this.hitImage = hitImage;
		positionX = (int) myself.getPositionX() + CORRECTION;
		positionY = (int) myself.getPositionY() + CORRECTION;
		bulletTimer();
	}
	
	private void bulletTimer() {
		if(Objects.isNull(bulletImage)) {
			hitTimer();
			bulletScheduler.shutdown();
			return;
		}
		double oneTimeMoveX = (myself.getPositionX() - target.getPositionX()) / COUNT;
		double oneTimeMoveY = (myself.getPositionY() - target.getPositionY()) / COUNT;
		bulletImage = EditImage.rotateImage(bulletImage, getAngle());
		bulletScheduler.scheduleWithFixedDelay(() -> {
			Battle.timerWait();
			if(COUNT <= bulletNumber) {
				hitTimer();
				bulletScheduler.shutdown();
				return;
			}
			moveBullet(oneTimeMoveX, oneTimeMoveY);
		}, 0, 20, TimeUnit.MILLISECONDS);
	}
	
	private double getAngle() {
		//x, y は 角度を求めたい点, 起点, 目標点 の位置順
		//角度は余弦定理から、回転方向は外積から算出
		double[] x = {myself.getPositionX(), myself.getPositionX() - 10, target.getPositionX()};
		double[] y = {myself.getPositionY(), myself.getPositionY(), target.getPositionY()};
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
		double cosineTheoremAngle = Math.acos((Math.pow(distance[0], 2) - Math.pow(distance[1], 2) - Math.pow(distance[2], 2)) / (-2 * distance[1] * distance[2]));
		double outerProductDirection = (x[2] - x[0]) * (y[1] - y[0]) - (y[2] - y[0]) * (x[1] - x[0]);
		return (0 < outerProductDirection)? Math.PI * 2 - cosineTheoremAngle: cosineTheoremAngle;
	}
	
	private void moveBullet(double oneTimeMoveX, double oneTimeMoveY) {
		bulletNumber++;
		positionX -= oneTimeMoveX;
		positionY -= oneTimeMoveY;
	}
	
	private void hitTimer() {
		if(Objects.isNull(hitImage)) {
			hitScheduler.shutdown();
			completion();
			return;
		}
		hitScheduler.scheduleWithFixedDelay(() -> {
			Battle.timerWait();
			if(hitImage.size() - 1 <= hitNumber) {
				hitScheduler.shutdown();
				completion();
				return;
			}
			hitNumber++;
		}, 0, 20, TimeUnit.MILLISECONDS);
	}
	
	protected synchronized BattleData waitCompletion() {
		if(bulletScheduler.isShutdown() && hitScheduler.isShutdown()) {
			return target;
		}
		try {
			wait();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return target;
	}
	
	private synchronized void completion() {
		notifyAll();
	}
	
	protected BufferedImage getImage() {
		return (0 <= hitNumber)? hitImage.get(hitNumber): bulletImage;
	}
	
	protected double getPsitionX() {
		return positionX;
	}
	
	protected double getPsitionY() {
		return positionY;
	}
}