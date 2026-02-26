package defendthecastle.battle;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

import defaultdata.Stage;
import defendthecastle.MainFrame;
import defendthecastle.battle.battledialog.PauseDialog;

public class GameTimer {
	private final MainFrame mainFrame;
	private final Stage stage;
	private final double difficultyCorrection;
	private final ScheduledExecutorService scheduler;
	private GameData gameData;
	private AwakeUnit awakeUnit;
	private StageImage stageImage;
	private BattleUnit[] unitMainData;//右武器/コア用　攻撃・被弾などの判定はこちらで行う
	private BattleUnit[] unitLeftData;//左武器用
	private BattleFacility[] facilityData;
	private BattleEnemy[] enemyData;
	private ScheduledFuture<?> mainFuture;
	private long beforeMainTime;
	private ScheduledFuture<?> clearFuture;
	private ScheduledFuture<?> monitorFuture;
	private boolean canStop;
	private int time;
	private final int NONE_DELAY = 0;
	private final int NATURAL_RECOVERY = 1;
	private final int DELAY = 10;
	
	GameTimer(MainFrame mainFrame, Stage stage, double difficultyCorrection, ScheduledExecutorService scheduler){
		this.mainFrame = mainFrame;
		this.stage = stage;
		this.difficultyCorrection = difficultyCorrection;
		this.scheduler = scheduler;
	}
	
	void timerStart(GameData gameData, AwakeUnit awakeUnit, StageImage stageImage, BattleUnit[] unitMainData, BattleUnit[] unitLeftData, BattleFacility[] facilityData, BattleEnemy[] enemyData){
		this.gameData = gameData;
		this.awakeUnit = awakeUnit;
		this.stageImage = stageImage;
		this.unitMainData = unitMainData;
		this.unitLeftData = unitLeftData;
		this.facilityData = facilityData;
		this.enemyData = enemyData;
		mainTimer(NONE_DELAY);
		clearTimer();
		monitorTimer();
	}
	
	void mainTimer(long stopTime) {
		mainFuture = createMainTimer(stopTime);
	}
	
	ScheduledFuture<?> createMainTimer(long stopTime){
		return scheduler.scheduleAtFixedRate(this::mainTimerProcess, initialDelay(stopTime), DELAY, TimeUnit.MILLISECONDS);
	}
	
	long initialDelay(long stopTime) {
		long initialDelay;
		if(stopTime == NONE_DELAY) {
			initialDelay = NONE_DELAY;
		}else {
			initialDelay = (stopTime - beforeMainTime < DELAY)? DELAY - (stopTime - beforeMainTime): NONE_DELAY;
			beforeMainTime += System.currentTimeMillis() - stopTime;
		}
		return initialDelay;
	}
	
	void mainTimerProcess() {
		beforeMainTime = System.currentTimeMillis();
		time += DELAY;
		if(time % 1000 == 0) {
			gameData.addCost(NATURAL_RECOVERY);
		}
	}
	
	int getMilliTime() {
		return time;
	}
	
	int getSecondsTime() {
		return time / 1000;
	}
	
	void clearTimer() {
		clearFuture = createClearTimer();
	}
	
	ScheduledFuture<?> createClearTimer(){
		return scheduler.scheduleAtFixedRate(this::clearTimerProcess, 0, 1, TimeUnit.SECONDS);
	}
	
	void clearTimerProcess() {
		if(stage.getStageData().canClear(unitMainData, unitLeftData, facilityData, enemyData, gameData)) {
			new PauseDialog(stage, unitMainData, unitLeftData, facilityData, enemyData, gameData, difficultyCorrection);
			mainFrame.selectStageDraw();
			return;
		}
		if(stage.getStageData().existsGameOver(unitMainData, unitLeftData, facilityData, enemyData, gameData)) {
			new PauseDialog();
			mainFrame.selectStageDraw();
		}
	}
	
	void monitorTimer() {
		monitorFuture = createMonitorTimer();
	}
	
	ScheduledFuture<?> createMonitorTimer(){
		return scheduler.scheduleAtFixedRate(this::monitorTimerProcess, 0, 100, TimeUnit.MILLISECONDS);
	}
	
	void monitorTimerProcess() {
		awakeUnit.awakeUnit();
		stageImage.updatePlacement();
	}
	
	void timerStop() {
		canStop = true;
		clearFuture.cancel(true);
		monitorFuture.cancel(true);
		awakeUnit.timerStop();
		mainFuture.cancel(true);
		long mainTime = System.currentTimeMillis();
		CompletableFuture.runAsync(this::timerWait, scheduler).thenRun(() -> mainTimer(mainTime));
		unitStop(unitMainData);
		unitStop(unitLeftData);
		unitStop(facilityData);
		unitStop(enemyData);
	}
	
	void unitStop(BattleData[] data) {
		CompletableFuture.runAsync(() -> futureStop(data), scheduler);
	}
	
	void futureStop(BattleData[] data) {
		Stream.of(data).forEach(BattleData::futureStop);
	}
	
	synchronized void timerWait() {
		if(!canStop) {
			return;
		}
		try {
			wait();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public synchronized void timerRestart() {
		canStop = false;
		notifyAll();
		clearTimer();
		monitorTimer();
		awakeUnit.timerRestart();
	}
}