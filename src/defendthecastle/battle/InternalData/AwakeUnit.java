package defendthecastle.battle.InternalData;

import java.util.Comparator;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

import defendthecastle.battle.Battle;

public class AwakeUnit {
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
	
	public AwakeUnit(Battle battle, GameData gameData, BattleUnit[] unitMainData, BattleUnit[] unitLeftData, ScheduledExecutorService scheduler){
		this.battle = battle;
		this.gameData= gameData;
		this.unitMainData = unitMainData;
		this.unitLeftData = unitLeftData;
		this.scheduler= scheduler;
		canAwakeUnit = new boolean[unitMainData.length];
	}
	
	public void changeAutoAwake() {
		if(canAutoAwake) {
			timerStop();
		}else {
			autoTimer();
		}
		canAutoAwake = canAutoAwake? false: true;
	}
	
	void timerStop() {
		if(autoFuture != null) {
			autoFuture.cancel(true);
			autoFuture = null;
		}
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
	
	public boolean canAwake(int number) {
		return unitMainData[number].canAwake() && AWAKE_COST <= gameData.getCost();
	}
	
	public void awake(int number) {
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
	
	public boolean canAutoAwake() {
		return canAutoAwake;
	}
	
	public boolean hasAwaked() {
		return hasAwaked;
	}
	
	public int unitPositionX() {
		return awakeUnit.getPositionX();
	}
	
	public int unitPositionY() {
		return awakeUnit.getPositionY();
	}
}
