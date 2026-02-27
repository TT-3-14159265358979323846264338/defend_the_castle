package defendthecastle.battle;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ScheduledExecutorService;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.IntStream;

//各バフの管理
public class Buff {
	//発生タイミングコード
	public static final double BIGINNING = 0;
	public static final double SKILL = 1;
	public static final double HIT = 2;
	public static final double DAMAGE = 3;
	public static final double DEFEAT = 4;
	public static final double KILL = 5;
	
	//発生対象コード
	public static final double ALLY = 0;
	public static final double ENEMY = 1;
	public static final double GAME = 2;
	
	//効果範囲コード
	public static final double MYSELF = 0;
	public static final double ALL = 1;
	public static final double WITHIN_RANGE = 2;
	public static final double OUT_RANGE = 3;
	public static final double TARGET = 4;
	
	//対象ステータスコード
	public static final double ATACK = 0;
	public static final double RANGE = 1;
	public static final double ATACK_SPEED = 2;
	public static final double ATACK_NUMBER = 3;
	
	public static final double HP = 10;
	public static final double DEFENCE = 12;
	public static final double HEAL = 13;
	public static final double MOVE_SPEED_OR_BLOCK = 14;
	public static final double COST = 15;
	
	public static final double SLASH = 100;
	public static final double PIERCE = 101;
	public static final double STRIKE = 102;
	public static final double IMPACT = 103;
	public static final double FLAME = 104;
	public static final double WATER = 105;
	public static final double WIND = 106;
	public static final double SOIL = 107;
	public static final double THUNDER = 108;
	public static final double HOLY = 109;
	public static final double DARK = 110;
	public static final double SUPPORT = 111;
	
	public static final double MORALE = 1000;
	public static final double GAME_COST = 1001;
	
	//加減乗除コード
	public static final double ADDITION = 0;
	public static final double SUBTRACTION = 1;
	public static final double MULTIPLICATION = 2;
	public static final double DIVISION = 3;
	
	//コードなし
	public static final double NONE = 0;
	
	//バフ情報コード
	public static final int TIMING_CODE = 0;
	public static final int TARGET_CODE = 1;
	public static final int RANGE_CODE = 2;
	public static final int STATUS_CODE = 3;
	public static final int CALCULATION_CODE = 4;
	public static final int EFFECT = 5;
	public static final int INTERVAL = 6;
	public static final int MAX = 7;
	public static final int DURATION = 8;
	public static final int RECAST = 9;
	public static final int CONSUME_COST = 10;
	
	//その他定義
	public final static int DELEY = 50;
	
	//バフの管理
	private List<Double> buffInformation;
	private BattleData myself;
	private List<BattleData> candidate;
	private GameData gameData;
	private List<BattleData> target = new ArrayList<>();
	private List<Double> effect = new ArrayList<>();
	private int durationCount = 0;
	private int recastCount = 0;
	private boolean canRecast;
	private ScheduledExecutorService scheduler;
	private TimerOperation recastTimer;
	private TimerOperation targetTimer;
	private TimerOperation intervalTimer;
	private TimerOperation durationTimer;
	private final int NONE_DELAY = 0;
	private final int INITIALIZE = 0;
	
	Buff(List<Double> buffInformation, BattleData myself, List<BattleData> ally, List<BattleData> enemy, GameTimer gameTimer, GameData gameData, ScheduledExecutorService scheduler) {
		this.buffInformation = buffInformation;
		this.myself = myself;
		if(buffInformation.get(TARGET_CODE) == ALLY) {
			candidate = ally;
		}else if(buffInformation.get(TARGET_CODE) == ENEMY) {
			candidate = enemy;
		}else {
			this.gameData = gameData;
		}
		canRecast = (canPossessSkill())? true: false;
		this.scheduler = scheduler;
		recastTimer = createTimerOperation(gameTimer, scheduler);
		targetTimer = createTimerOperation(gameTimer, scheduler);
		intervalTimer = createTimerOperation(gameTimer, scheduler);
		durationTimer = createTimerOperation(gameTimer, scheduler);
	}
	
	TimerOperation createTimerOperation(GameTimer gameTimer, ScheduledExecutorService scheduler) {
		return new TimerOperation(gameTimer, scheduler);
	}
	
	void buffStart(BattleData BattleData) {
		buffEnd().join();
		recastBuff(NONE_DELAY);
		if(buffInformation.get(TARGET_CODE) == GAME) {
			gameBuff();
			return;
		}
		unitBuff(BattleData);
	}
	
	void timerPause() {
		recastTimer.timerPause(this::recastBuff);
		intervalTimer.timerPause(time -> {
			if(existsInterval()) {
				gameIntervalControl(time);
				return;
			}
			intervalControl(time);
		});
		durationTimer.timerPause(this::durationControl);
	}
	
	//リキャスト
	void recastBuff(long stopTime) {
		if(buffInformation.get(RECAST) == NONE) {
			return;
		}
		canRecast = false;
		recastTimer.timerStrat(stopTime, DELEY, this::recastTimerProcess);
	}
	
	void recastTimerProcess() {
		recastTimer.updateBeforeTime();
		recastCount += DELEY;
		if(recastMax() <= recastCount) {
			recastCount = INITIALIZE;
			canRecast = true;
			recastTimer.timerStop();
		}
	}
	
	//ゲームバフ
	void gameBuff() {
		effect.add((double) INITIALIZE);
		if(existsInterval()) {
			gameBuffSelect();
			return;
		}
		gameIntervalControl(NONE_DELAY);
		durationControl(NONE_DELAY);
	}
	
	void gameIntervalControl(long stopTime) {
		int delay = getInterval() * 1000;
		intervalTimer.timerStrat(stopTime, delay, this::intervalTimerProcessGameBuff);
	}
	
	void intervalTimerProcessGameBuff() {
		intervalTimer.updateBeforeTime();
		if(canNotActivateBuff()) {
			return;
		}
		gameBuffSelect();
		if(existsMax(INITIALIZE)) {
			intervalTimer.timerStop();
		}
	}
	
	void gameBuffSelect() {
		if(getBuffStatusCode() == MORALE) {
			moraleBuff();
		}else {
			costBuff();
		}
	}
	
	void moraleBuff() {
		setEffect(INITIALIZE);
		if(existsAddition()) {
			gameData.moraleBoost(GameData.UNIT, (int) getEffect());
			return;
		}
		gameData.lowMorale(GameData.UNIT, (int) getEffect());
	}
	
	void costBuff() {
		setEffect(INITIALIZE);
		if(existsAddition()) {
			gameData.addCost((int) getEffect());
			return;
		}
		gameData.consumeCost((int) getEffect());
	}
	
	//ユニットバフ
	void unitBuff(BattleData BattleData) {
		if(existsRangeCode(MYSELF)) {
			singleBuff(myself);
			return;
		}
		if(existsRangeCode(ALL)) {
			multipleBuff(_ -> true);
			return;
		}
		if(existsRangeCode(WITHIN_RANGE)) {
			multipleBuff(i -> withinCheck(i));
			return;
		}
		if(existsRangeCode(OUT_RANGE)) {
			multipleBuff(i -> !withinCheck(i));
			return;
		}
		if(existsRangeCode(TARGET)) {
			singleBuff(BattleData);
		}
	}
	
	void singleBuff(BattleData BattleData) {
		BattleData.receiveBuff(this);
		target = Arrays.asList(BattleData);
		effect = Arrays.asList(getEffect());
		if(existsInterval() && existsDuration()) {
			return;
		}
		intervalControl(NONE_DELAY);
		durationControl(NONE_DELAY);
	}
	
	void multipleBuff(Predicate<? super BattleData> rangeFilter) {
		targetControl(rangeFilter);
		intervalControl(NONE_DELAY);
		durationControl(NONE_DELAY);
	}
	
	boolean withinCheck(BattleData BattleData) {
		Function<BattleData, Double> distanceCalculate = (data) -> {
			return Math.sqrt(Math.pow(myself.getPositionX() - data.getPositionX(), 2) + Math.pow(myself.getPositionY() - data.getPositionY(), 2));
		};
		Predicate<BattleData> distanceCheck = (data) -> {
			return distanceCalculate.apply(data) <= myself.buffRange() + defendthecastle.battle.Battle.SIZE / 2;
		};
		return distanceCheck.test(BattleData);
	}
	
	void targetControl(Predicate<? super BattleData> rangeFilter) {
		targetTimer.timerStrat(0, DELEY, () -> targetTimerProcess(rangeFilter));
	}
	
	void targetTimerProcess(Predicate<? super BattleData> rangeFilter) {
		if(canNotActivateBuff()) {
			return;
		}
		List<BattleData> newTarget = candidate.stream().filter(i -> i.canActivate()).filter(rangeFilter).toList();
		IntStream.range(0, target.size()).boxed().sorted(Comparator.reverseOrder()).forEach(i -> removeUpdate(i, newTarget));
		newTarget.forEach(this::addUpdate);
	}
	
	void removeUpdate(int number, List<BattleData> newTarget) {
		if(newTarget.stream().noneMatch(i -> i.equals(target.get(number)))) {
			target.get(number).removeBuff(this);
			if(existsHP()) {
				target.get(number).HPIncrease(INITIALIZE);
			}
			target.remove(number);
			effect.remove(number);
		}
	}
	
	void addUpdate(BattleData BattleData) {
		if(target.stream().noneMatch(i -> i.equals(BattleData))) {
			if(existsHP()) {
				int defaultHP = BattleData.getMaxHP();
				addBuff(BattleData);
				BattleData.HPIncrease(BattleData.getMaxHP() - defaultHP);
				return;
			}
			addBuff(BattleData);
		}
	}
	
	void addBuff(BattleData BattleData) {
		BattleData.receiveBuff(this);
		target.add(BattleData);
		effect.add(getEffect());
	}
	
	void intervalControl(long stopTime) {
		if(existsInterval()) {
			return;
		}
		int delay = getInterval() * 1000;
		intervalTimer.timerStrat(stopTime, delay, this::intervalTimerProcessUnitBuff);
	}
	
	void intervalTimerProcessUnitBuff() {
		intervalTimer.updateBeforeTime();
		if(canNotActivateBuff()) {
			return;
		}
		IntStream.range(0, effect.size()).filter(i -> !existsMax(i)).forEach(i -> setEffect(i));
	}
	
	//共通メソッド
	void durationControl(long stopTime) {
		if(existsDuration()) {
			return;
		}
		durationTimer.timerStrat(stopTime, DELEY, this::durationTimerProcess);
	}
	
	void durationTimerProcess() {
		durationTimer.updateBeforeTime();
		durationCount += DELEY;
		if(canNotActivateBuff()) {
			return;
		}
		if(buffInformation.get(DURATION) * 1000 <= durationCount) {
			durationCount = INITIALIZE;
			buffEnd();
		}
	}
	
	boolean canNotActivateBuff() {
		if(myself.canActivate()) {
			return false;
		}
		if(getBuffTiming() == DEFEAT) {
			return false;
		}
		buffEnd();
		return true;
	}
	
	CompletableFuture<Void> buffEnd() {
		return CompletableFuture.runAsync(this::timerEnd, scheduler).thenRun(this::resetBuff);
	}
	
	void timerEnd() {
		recastTimer.timerStop();
		targetTimer.timerStop();
		intervalTimer.timerStop();
		durationTimer.timerStop();
	}
	
	void resetBuff() {
		target.stream().forEach(i -> i.removeBuff(this));
		if(existsHP()) {
			target.stream().forEach(i -> i.HPIncrease(0));
		}
		target.clear();
		effect.clear();
		durationCount = INITIALIZE;
	}
	
	double recastMax() {
		return buffInformation.get(RECAST) * 1000;
	}
	
	int getInterval() {
		return buffInformation.get(INTERVAL).intValue();
	}
	
	boolean existsRangeCode(double code) {
		return buffInformation.get(RANGE_CODE) == code;
	}
	
	boolean existsInterval() {
		return getInterval() == NONE;
	}
	
	boolean existsDuration() {
		return buffInformation.get(DURATION) == NONE;
	}
	
	boolean existsMax(int number) {
		if(buffInformation.get(MAX) == NONE) {
			return false;
		}
		return buffInformation.get(MAX) <= effect.get(number);
	}
	
	boolean existsAddition() {
		return getCalculationCode() == ADDITION;
	}
	
	boolean existsHP() {
		return buffInformation.get(STATUS_CODE) == HP;
	}
	
	double getCalculationCode() {
		return buffInformation.get(CALCULATION_CODE);
	}
	
	double getEffect() {
		return buffInformation.get(EFFECT);
	}
	
	void setEffect(int number) {
		effect.set(number, effect.get(number) + getEffect());
	}
	
	//データ返却
	double getBuffStatusCode() {
		return buffInformation.get(STATUS_CODE);
	}
	
	double getBuffTiming() {
		return buffInformation.get(TIMING_CODE);
	}
	
	double additionalEffect(BattleData BattleData, double status){
		if(getCalculationCode() == ADDITION) {
			return status += getBuffValue(BattleData);
		}
		if(getCalculationCode() == SUBTRACTION) {
			return status -= getBuffValue(BattleData);
		}
		return status;
	}
	
	double ratioEffect(BattleData BattleData, double status) {
		if(getCalculationCode() == MULTIPLICATION) {
			return status *= getBuffValue(BattleData);
		}
		if(getCalculationCode() == DIVISION) {
			return status /= getBuffValue(BattleData);
		}
		return status;
	}
	
	double getBuffValue(BattleData BattleData) {
		return effect.get(target.indexOf(BattleData));
	}
	
	int getCost() {
		return buffInformation.get(CONSUME_COST).intValue();
	}
	
	boolean canPossessSkill() {
		return getBuffTiming() == SKILL;
	}
	
	boolean canRecast() {
		return canRecast;
	}
	
	double recastRatio() {
		return recastCount / recastMax();
	}
}