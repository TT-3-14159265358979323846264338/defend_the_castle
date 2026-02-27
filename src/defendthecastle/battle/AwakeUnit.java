package defendthecastle.battle;

import java.util.Comparator;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

class AwakeUnit {
	private final Battle battle;
	private final GameData gameData;
	private final BattleUnit[] unitMainData;
	private final BattleUnit[] unitLeftData;
	private final ScheduledExecutorService scheduler;
	private ScheduledFuture<?> autoFuture;
	private ScheduledFuture<?> awakeFuture;
	private boolean[] canAwakeUnit;
	private boolean canAutoAwake;
	private boolean hasAwaked;
	private BattleUnit awakeUnit;
	private final Object awakeLock = new Object();
	private final int AWAKE_COST = 10;
	
	AwakeUnit(Battle battle, GameData gameData, BattleUnit[] unitMainData, BattleUnit[] unitLeftData, ScheduledExecutorService scheduler){
		this.battle = battle;
		this.gameData= gameData;
		this.unitMainData = unitMainData;
		this.unitLeftData = unitLeftData;
		this.scheduler= scheduler;
		canAwakeUnit = new boolean[unitMainData.length];
	}
	
	void changeAutoAwake() {
		if(canAutoAwake) {
			timerStop();
		}else {
			autoTimer();
		}
		canAutoAwake = canAutoAwake? false: true;
	}
	
	void timerStop() {
		autoFuture.cancel(true);
		autoFuture = null;
	}
	
	void timerRestart() {
		if(canAutoAwake) {
			autoTimer();
		}
	}
	
	void autoTimer() {
		autoFuture = createAutoTimer();
	}
	
	ScheduledFuture<?> createAutoTimer(){
		return scheduler.scheduleAtFixedRate(this::autoTimerProcess, 0, 100, TimeUnit.MILLISECONDS);
	}
	
	void autoTimerProcess() {
		IntStream.range(0, unitMainData.length).filter(this::canAwake).boxed().sorted(Comparator.comparing(i -> unitMainData[i].getAwakeningNumber())).forEach(this::awake);
	}
	
	boolean canAwake(int number) {
		return unitMainData[number].canAwake() && AWAKE_COST <= gameData.getCost();
	}
	
	void awake(int number) {
		synchronized(awakeLock) {
			if(canAwake(number)) {
				awakeUnit = unitMainData[number];
				hasAwaked = true;
				if(awakeFuture != null) {
					awakeFuture.cancel(true);
				}
				awakeFuture = createAwakeTimer();
				unitMainData[number].awakening();
				unitLeftData[number].awakening();
				gameData.consumeCost(AWAKE_COST);
			}
		}
	}
	
	ScheduledFuture<?> createAwakeTimer(){
		return scheduler.schedule(this::awakeTimerProcess, 2, TimeUnit.SECONDS);
	}
	
	void awakeTimerProcess() {
		hasAwaked = false;
		awakeFuture = null;
	}
	
	void awakeUnit() {
		IntStream.range(0, canAwakeUnit.length).forEach(i -> {
			if(canAwake(i) != canAwakeUnit[i]) {
				battle.setAwakeLabel(i);
				canAwakeUnit[i] = canAwakeUnit[i]? false: true;
			}
		});
	}
	
	boolean canAutoAwake() {
		return canAutoAwake;
	}
	
	boolean hasAwaked() {
		return hasAwaked;
	}
	
	int unitPositionX() {
		return awakeUnit.getPositionX();
	}
	
	int unitPositionY() {
		return awakeUnit.getPositionY();
	}
}
