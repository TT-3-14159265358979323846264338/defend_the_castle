package battle;

import java.awt.image.BufferedImage;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Bullet {
	ScheduledExecutorService bulletScheduler = Executors.newSingleThreadScheduledExecutor();
	ScheduledExecutorService hitScheduler = Executors.newSingleThreadScheduledExecutor();
	Battle Battle;
	BattleData myself;
	BattleData target;
	BufferedImage bulletImage;
	List<BufferedImage> hitImage;
	int bulletNumber = 0;
	int hitNumber = -1;
	int positionX;
	int positionY;
	
	protected Bullet(Battle Battle, BattleData myself, BattleData target, BufferedImage bulletImage, List<BufferedImage> hitImage) {
		this.Battle = Battle;
		this.myself = myself;
		this.target = target;
		this.bulletImage = bulletImage;
		this.hitImage = hitImage;
		positionX = (int) myself.getPositionX();
		positionY = (int) myself.getPositionY();
		bulletTimer();
	}
	
	private void bulletTimer() {
		if(Objects.isNull(bulletImage)) {
			hitTimer();
			bulletScheduler.shutdown();
			return;
		}
		bulletScheduler.scheduleWithFixedDelay(() -> {
			Battle.timerWait();
			if(5 < bulletNumber) {
				hitTimer();
				bulletScheduler.shutdown();
				return;
			}
			bulletNumber++;
			changePosition();
		}, 0, 20, TimeUnit.MILLISECONDS);
	}
	
	private void changePosition() {
		
		
		
		
	}
	
	private void hitTimer() {
		if(Objects.isNull(hitImage)) {
			completion();
			hitScheduler.shutdown();
			return;
		}
		hitScheduler.scheduleWithFixedDelay(() -> {
			Battle.timerWait();
			if(hitImage.size() - 1 <= hitNumber) {
				completion();
				hitScheduler.shutdown();
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
	
	protected int getPsitionX() {
		return positionX;
	}
	
	protected int getPsitionY() {
		return positionY;
	}
}