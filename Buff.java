package battle;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
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
	public final static int TIMING_CODE = 0;
	public final static int TARGET_CODE = 1;
	public final static int RANGE_CODE = 2;
	public final static int STATUS_CODE = 3;
	public final static int CALCULATION_CODE = 4;
	public final static int EFFECT = 5;
	public final static int INTERVAL = 6;
	public final static int MAX = 7;
	public final static int DURATION = 8;
	public final static int RECAST = 9;
	
	//その他定義
	public final static int DELEY = 50;
	
	//バフの管理
	private List<Double> buffInformation;
	private BattleData myself;
	private List<BattleData> candidate;
	private Battle Battle;
	private GameData GameData;
	private List<BattleData> target = new ArrayList<>();
	private List<Double> effect = new ArrayList<>();
	private int durationCount = 0;
	private int recastCount = 0;
	private boolean canRecast;
	private ScheduledExecutorService recastScheduler = Executors.newSingleThreadScheduledExecutor();
	private ScheduledFuture<?> recastFuture;
	private ScheduledExecutorService targetScheduler = Executors.newSingleThreadScheduledExecutor();
	private ScheduledFuture<?> targetFuture;
	private ScheduledExecutorService intervalScheduler = Executors.newSingleThreadScheduledExecutor();
	private ScheduledFuture<?> intervalFuture;
	private ScheduledExecutorService durationScheduler = Executors.newSingleThreadScheduledExecutor();
	private ScheduledFuture<?> durationFuture;
	
	protected Buff(List<Double> buffInformation, BattleData myself, List<BattleData> ally, List<BattleData> enemy, Battle Battle, GameData GameData) {
		//テスト用
		//発生タイミングコード, 発生対象コード, 効果範囲コード, 対象ステータスコード, 加減乗除コード, 効果量, 効果発生間隔[s](Buff.NONE: なし), 上限量(Buff.NONE: なし), 効果時間[s](Buff.NONE: 無限), 再使用時間[s](Buff.NONE: なし)
		//this.buffInformation = Arrays.asList(Buff.BIGINNING, Buff.ALLY, Buff.ALL, Buff.ATACK, Buff.ADDITION, 10.0, Buff.NONE, Buff.NONE, Buff.NONE, Buff.NONE);
		
		this.buffInformation = buffInformation;
		this.myself = myself;
		this.Battle = Battle;
		if(buffInformation.get(TARGET_CODE) == ALLY) {
			candidate = ally;
		}else if(buffInformation.get(TARGET_CODE) == ENEMY) {
			candidate = enemy;
		}else {
			this.GameData = GameData;
		}
		canRecast = (canPossessSkill())? true: false;
	}
	
	protected void buffStart(BattleData BattleData) {
		buffEnd().join();
		recastBuff();
		if(buffInformation.get(TARGET_CODE) == GAME) {
			gameBuff();
			return;
		}
		unitBuff(BattleData);
	}
	
	protected void schedulerEnd() {
		recastScheduler.shutdown();
		targetScheduler.shutdown();
		intervalScheduler.shutdown();
		durationScheduler.shutdown();
	}
	
	//リキャスト
	private void recastBuff() {
		if(buffInformation.get(RECAST) == NONE) {
			return;
		}
		canRecast = false;
		recastFuture = recastScheduler.scheduleWithFixedDelay(() -> {
			Battle.timerWait();
			recastCount += DELEY;
			if(recastMax() <= recastCount) {
				recastCount = 0;
				canRecast = true;
				recastFuture.cancel(true);
			}
		}, 0, DELEY, TimeUnit.MILLISECONDS);
	}
	
	//ゲームバフ
	private void gameBuff() {
		effect.add(0.0);
		if(existsInterval()) {
			gameBuffSelect();
			return;
		}
		intervalFuture = intervalScheduler.scheduleWithFixedDelay(() -> {
			Battle.timerWait();
			if(canNotActivate()) {
				return;
			}
			gameBuffSelect();
			if(existsMax(0)) {
				intervalFuture.cancel(true);
			}
		}, 0, getInterval(), TimeUnit.SECONDS);
		durationControl();
	}
	
	private void gameBuffSelect() {
		if(getBuffStatusCode() == MORALE) {
			moraleBuff();
		}else {
			costBuff();
		}
	}
	
	private void moraleBuff() {
		setEffect(0);
		if(existsAddition()) {
			GameData.moraleBoost(battle.GameData.UNIT, (int) getEffect());
			return;
		}
		GameData.lowMorale(battle.GameData.UNIT, (int) getEffect());
	}
	
	private void costBuff() {
		setEffect(0);
		if(existsAddition()) {
			GameData.addCost((int) getEffect());
			return;
		}
		GameData.consumeCost((int) getEffect());
	}
	
	//ユニットバフ
	private void unitBuff(BattleData BattleData) {
		if(existsRangeCode(MYSELF)) {
			singleBuff(myself);
			return;
		}
		if(existsRangeCode(ALL)) {
			multipleBuff(i -> true);
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
	
	private void singleBuff(BattleData BattleData) {
		BattleData.receiveBuff(this);
		target = Arrays.asList(BattleData);
		effect = Arrays.asList(getEffect());
		if(existsInterval() && existsDuration()) {
			return;
		}
		intervalControl();
		durationControl();
	}
	
	private void multipleBuff(Predicate<? super BattleData> rangeFilter) {
		targetControl(rangeFilter);
		intervalControl();
		durationControl();
	}
	
	private boolean withinCheck(BattleData BattleData) {
		Function<BattleData, Double> distanceCalculate = (data) -> {
			return Math.sqrt(Math.pow(myself.getPositionX() - data.getPositionX(), 2) + Math.pow(myself.getPositionY() - data.getPositionY(), 2));
		};
		Predicate<BattleData> distanceCheck = (data) -> {
			return distanceCalculate.apply(data) <= myself.buffRange() + battle.Battle.SIZE / 2;
		};
		return distanceCheck.test(BattleData);
	}
	
	private void targetControl(Predicate<? super BattleData> rangeFilter) {
		targetFuture = targetScheduler.scheduleWithFixedDelay(() -> {
			Battle.timerWait();
			if(canNotActivate()) {
				return;
			}
			List<BattleData> newTarget = candidate.stream().filter(i -> i.canActivate()).filter(rangeFilter).toList();
			IntStream.range(0, target.size()).boxed().sorted(Comparator.reverseOrder()).forEach(i -> removeUpdate(i, newTarget));
			newTarget.forEach(this::addUpdate);
		}, 0, DELEY, TimeUnit.MILLISECONDS);
	}
	
	private void removeUpdate(int number, List<BattleData> newTarget) {
		if(newTarget.stream().noneMatch(i -> i.equals(target.get(number)))) {
			target.get(number).removeBuff(this);
			if(existsHP()) {
				target.get(number).HPIncrease(0);
			}
			target.remove(number);
			effect.remove(number);
		}
	}
	
	private void addUpdate(BattleData BattleData) {
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
	
	private void addBuff(BattleData BattleData) {
		BattleData.receiveBuff(this);
		target.add(BattleData);
		effect.add(getEffect());
	}
	
	private void intervalControl() {
		if(existsInterval()) {
			return;
		}
		intervalFuture = intervalScheduler.scheduleWithFixedDelay(() -> {
			Battle.timerWait();
			if(canNotActivate()) {
				return;
			}
			IntStream.range(0, effect.size()).filter(i -> !existsMax(i)).forEach(i -> setEffect(i));
		}, getInterval(), getInterval(), TimeUnit.SECONDS);
	}
	
	//共通メソッド
	private void durationControl() {
		if(existsDuration()) {
			return;
		}
		durationFuture = durationScheduler.scheduleWithFixedDelay(() -> {
			Battle.timerWait();
			durationCount += DELEY;
			if(canNotActivate()) {
				return;
			}
			if(buffInformation.get(DURATION) * 1000 <= durationCount) {
				durationCount = 0;
				buffEnd();
			}
		}, 0, DELEY, TimeUnit.MILLISECONDS);
	}
	
	private boolean canNotActivate() {
		if(!myself.canActivate()) {
			buffEnd();
			return true;
		}
		return false;
	}
	
	private CompletableFuture<Void> buffEnd() {
		return CompletableFuture.runAsync(this::futureCancel).thenRun(this::resetBuff);
	}
	
	private void futureCancel() {
		if(targetFuture != null) {
			targetFuture.cancel(true);
		}
		if(intervalFuture != null) {
			intervalFuture.cancel(true);
		}
		if(durationFuture != null) {
			durationFuture.cancel(true);
		}
	}
	
	private void resetBuff() {
		target.stream().forEach(i -> i.removeBuff(this));
		if(existsHP()) {
			target.stream().forEach(i -> i.HPIncrease(0));
		}
		target.clear();
		effect.clear();
		durationCount = 0;
	}
	
	private double recastMax() {
		return buffInformation.get(RECAST) * 1000;
	}
	
	private int getInterval() {
		return buffInformation.get(INTERVAL).intValue();
	}
	
	private boolean existsRangeCode(double code) {
		return buffInformation.get(RANGE_CODE) == code;
	}
	
	private boolean existsInterval() {
		return getInterval() == NONE;
	}
	
	private boolean existsDuration() {
		return buffInformation.get(DURATION) == NONE;
	}
	
	private boolean existsMax(int number) {
		if(buffInformation.get(MAX) == NONE) {
			return false;
		}
		return buffInformation.get(MAX) <= effect.get(number);
	}
	
	private boolean existsAddition() {
		return getCalculationCode() == ADDITION;
	}
	
	private boolean existsHP() {
		return buffInformation.get(STATUS_CODE) == HP;
	}
	
	private double getCalculationCode() {
		return buffInformation.get(CALCULATION_CODE);
	}
	
	private double getEffect() {
		return buffInformation.get(EFFECT);
	}
	
	private void setEffect(int number) {
		effect.set(number, effect.get(number) + getEffect());
	}
	
	//データ返却
	protected double getBuffStatusCode() {
		return buffInformation.get(STATUS_CODE);
	}
	
	protected double getBuffTiming() {
		return buffInformation.get(TIMING_CODE);
	}
	
	protected double additionalEffect(BattleData BattleData, double status){
		if(getCalculationCode() == ADDITION) {
			return status += getBuffValue(BattleData);
		}
		if(getCalculationCode() == SUBTRACTION) {
			return status -= getBuffValue(BattleData);
		}
		return status;
	}
	
	protected double ratioEffect(BattleData BattleData, double status) {
		if(getCalculationCode() == MULTIPLICATION) {
			return status *= getBuffValue(BattleData);
		}
		if(getCalculationCode() == DIVISION) {
			return status /= getBuffValue(BattleData);
		}
		return status;
	}
	
	private double getBuffValue(BattleData BattleData) {
		return effect.get(target.indexOf(BattleData));
	}
	
	protected boolean canPossessSkill() {
		return getBuffTiming() == SKILL;
	}
	
	protected boolean canRecast() {
		return canRecast;
	}
	
	protected double recastRatio() {
		return recastCount / recastMax();
	}
}