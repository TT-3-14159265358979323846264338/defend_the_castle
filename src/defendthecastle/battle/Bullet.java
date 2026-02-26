package defendthecastle.battle;

import java.awt.image.BufferedImage;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.BiFunction;

import commonclass.EditImage;

public class Bullet {
	public static final int CORRECTION = 25;
	public static final int COUNT = 5;
	private ScheduledExecutorService scheduler;
	private ScheduledFuture<?> bulletFuture;
	private long beforeBulletTime;
	private ScheduledFuture<?> hitFuture;
	private long beforeHitTime;
	private GameTimer gameTimer;
	private BattleData myself;
	private BattleData target;
	private BufferedImage bulletImage;
	private List<BufferedImage> hitImage;
	private double oneTimeMoveX;
	private double oneTimeMoveY;
	private double positionX;
	private double positionY;
	private int bulletNumber = 0;
	private int hitNumber = -1;
	private boolean canRestart;
	private final int NONE_DELAY = 0;
	
	Bullet(GameTimer gameTimer, BattleData myself, BattleData target, BufferedImage bulletImage, List<BufferedImage> hitImage, ScheduledExecutorService scheduler) {
		this.gameTimer = gameTimer;
		this.myself = myself;
		this.target = target;
		this.bulletImage = bulletImage;
		this.hitImage = hitImage;
		this.scheduler = scheduler;
		bulletTimer();
	}
	
	void bulletTimer() {
		if(Objects.isNull(bulletImage)) {
			hit();
			return;
		}
		positionX = (int) myself.getPositionX() + CORRECTION;
		positionY = (int) myself.getPositionY() + CORRECTION;
		oneTimeMoveX = (myself.getPositionX() - target.getPositionX()) / COUNT;
		oneTimeMoveY = (myself.getPositionY() - target.getPositionY()) / COUNT;
		bulletImage = EditImage.rotateImage(bulletImage, angle());
		bullet(NONE_DELAY);
	}
	
	double angle() {
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
	
	void bullet(long stopTime) {
		int delay = 30;
		long initialDelay;
		if(stopTime == NONE_DELAY) {
			initialDelay = NONE_DELAY;
		}else {
			initialDelay = (stopTime - beforeBulletTime < delay)? delay - (stopTime - beforeBulletTime): NONE_DELAY;
			beforeBulletTime += System.currentTimeMillis() - stopTime;
		}
		bulletFuture = scheduler.scheduleAtFixedRate(() -> {
			beforeBulletTime = System.currentTimeMillis();
			if(COUNT <= bulletNumber) {
				hit();
				bulletFuture.cancel(true);
				return;
			}
			moveBullet();
		}, initialDelay, delay, TimeUnit.MILLISECONDS);
	}
	
	void moveBullet() {
		bulletNumber++;
		positionX -= oneTimeMoveX;
		positionY -= oneTimeMoveY;
	}
	
	void hit() {
		if(Objects.isNull(hitImage)) {
			completion();
			return;
		}
		positionX = (int) target.getPositionX() + CORRECTION;
		positionY = (int) target.getPositionY() + CORRECTION;
		hitTimer(NONE_DELAY);
	}
	
	void hitTimer(long stopTime) {
		int delay = 50;
		long initialDelay;
		if(stopTime == NONE_DELAY) {
			initialDelay = NONE_DELAY;
		}else {
			initialDelay = (stopTime - beforeHitTime < delay)? delay - (stopTime - beforeHitTime): NONE_DELAY;
			beforeHitTime += System.currentTimeMillis() - stopTime;
		}
		hitFuture = scheduler.scheduleAtFixedRate(() -> {
			beforeHitTime = System.currentTimeMillis();
			if(hitImage.size() - 1 <= hitNumber) {
				completion();
				hitFuture.cancel(true);
				return;
			}
			hitNumber++;
		}, initialDelay, delay, TimeUnit.MILLISECONDS);
	}
	
	synchronized void waitCompletion() {
		if(canRestart) {
			return;
		}
		try {
			wait();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return;
	}
	
	synchronized void completion() {
		canRestart = true;
		notifyAll();
	}
	
	void futureStop() {
		if(bulletFuture != null && !bulletFuture.isCancelled()) {
			bulletFuture.cancel(true);
			long bulletTime = System.currentTimeMillis();
			CompletableFuture.runAsync(gameTimer::timerWait, scheduler).thenRun(() -> bullet(bulletTime));
		}
		if(hitFuture != null && !hitFuture.isCancelled()) {
			hitFuture.cancel(true);
			long hitTime = System.currentTimeMillis();
			CompletableFuture.runAsync(gameTimer::timerWait, scheduler).thenRun(() -> hitTimer(hitTime));
		}
	}
	
	BufferedImage getImage() {
		return (0 <= hitNumber)? hitImage.get(hitNumber): bulletImage;
	}
	
	int getPsitionX() {
		return (int) positionX;
	}
	
	int getPsitionY() {
		return (int) positionY;
	}
}