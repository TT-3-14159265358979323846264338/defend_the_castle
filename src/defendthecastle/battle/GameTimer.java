package defendthecastle.battle;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ScheduledExecutorService;
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
	private final TimerOperation mainTimer;
	private final TimerOperation clearTimer;
	private final TimerOperation monitorTimer;
	private boolean hasStoped;
	private boolean hasEndedGame;
	private int time;
	private final int NONE_DELAY = 0;
	private final int NATURAL_RECOVERY = 1;
	private final int MAIN_DELAY = 10;
	private final int CLEAR_DELAY = 1000;
	private final int MONITOR_DELAY = 100;
	
	GameTimer(MainFrame mainFrame, Stage stage, double difficultyCorrection, ScheduledExecutorService scheduler){
		this.mainFrame = mainFrame;
		this.stage = stage;
		this.difficultyCorrection = difficultyCorrection;
		this.scheduler = scheduler;
		mainTimer = createTimerOperation(scheduler);
		clearTimer = createTimerOperation(scheduler);
		monitorTimer = createTimerOperation(scheduler);
	}
	
	TimerOperation createTimerOperation(ScheduledExecutorService scheduler) {
		return new TimerOperation(this, scheduler);
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
		clearTimer(NONE_DELAY);
		monitorTimer(NONE_DELAY);
	}
	
	void mainTimer(long stopTime) {
		mainTimer.timerStrat(stopTime, MAIN_DELAY, this::mainTimerProcess);
	}
	
	void mainTimerProcess() {
		mainTimer.updateBeforeTime();
		time += MAIN_DELAY;
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
	
	void clearTimer(long stopTime) {
		clearTimer.timerStrat(0, CLEAR_DELAY, this::clearTimerProcess);
	}
	
	void clearTimerProcess() {
		clearTimer.updateBeforeTime();
		if(stage.getStageData().canClear(unitMainData, unitLeftData, facilityData, enemyData, gameData)) {
			new PauseDialog(stage, unitMainData, unitLeftData, facilityData, enemyData, gameData, difficultyCorrection);
			mainFrame.selectStageDraw();
			gameEnd();
			return;
		}
		if(stage.getStageData().existsGameOver(unitMainData, unitLeftData, facilityData, enemyData, gameData)) {
			new PauseDialog();
			mainFrame.selectStageDraw();
			gameEnd();
		}
	}
	
	void monitorTimer(long stopTime) {
		monitorTimer.timerStrat(0, MONITOR_DELAY, this::monitorTimerProcess);
	}
	
	void monitorTimerProcess() {
		monitorTimer.updateBeforeTime();
		awakeUnit.awakeUnit();
		stageImage.updatePlacement();
	}
	
	void timerPause() {
		hasStoped = true;
		mainTimer.timerPause(this::mainTimer);
		clearTimer.timerPause(this::clearTimer);
		monitorTimer.timerPause(this::monitorTimer);
		allTimerPause(unitMainData);
		allTimerPause(unitLeftData);
		allTimerPause(facilityData);
		allTimerPause(enemyData);
	}
	
	void allTimerPause(BattleData[] data) {
		CompletableFuture.runAsync(() -> battleDataTimerPause(data), scheduler);
	}
	
	void battleDataTimerPause(BattleData[] data) {
		Stream.of(data).forEach(BattleData::timerPause);
	}
	
	synchronized void timerWait() {
		if(!hasStoped) {
			return;
		}
		try {
			wait();
		} catch (Exception e) {
			throw new CompletionException(e);
		}
	}
	
	public synchronized void timerRestart() {
		if(!hasStoped || hasEndedGame) {
			return;
		}
		hasStoped = false;
		notifyAll();
		awakeUnit.timerRestart();
	}
	
	public void gameEnd() {
		hasEndedGame = true;
		mainTimer.timerStop();
		clearTimer.timerStop();
		monitorTimer.timerStop();
		awakeUnit.timerStop();
		battleDataTimerEnd(unitMainData);
		battleDataTimerEnd(unitLeftData);
		battleDataTimerEnd(facilityData);
		battleDataTimerEnd(enemyData);
	}
	
	void battleDataTimerEnd(BattleData[] data) {
		Stream.of(data).forEach(BattleData::timerEnd);
	}
}