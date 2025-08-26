package battle;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
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
	public final static int DELEY = 20;
	
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
		canRecast = (possessSkill())? true: false;
	}
	
	protected void buffStart() {
		recastBuff();
		if(buffInformation.get(TARGET_CODE) == GAME) {
			gameBuff();
			return;
		}
		unitBuff();
	}
	
	//リキャスト
	private void recastBuff() {
		if(buffInformation.get(RECAST) == NONE) {
			return;
		}
		canRecast = false;
		ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
		scheduler.scheduleWithFixedDelay(() -> {
			Battle.timerWait();
			recastCount++;
			if(recastMax() <= recastCount) {
				recastCount = 0;
				canRecast = true;
				scheduler.shutdown();
			}
		}, 0, DELEY, TimeUnit.MILLISECONDS);
	}
	
	//ゲームバフ
	private void gameBuff() {
		effect.add(0.0);
		if(intervalCheck()) {
			gameBuffSelect();
			return;
		}
		ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
		scheduler.scheduleWithFixedDelay(() -> {
			Battle.timerWait();
			if(activateCheck(scheduler)) {
				return;
			}
			gameBuffSelect();
			if(maxCheck(0)) {
				scheduler.shutdown();
			}
		}, 0, getInterval(), TimeUnit.SECONDS);
		durationControl(scheduler);
	}
	
	private void gameBuffSelect() {
		if(buffStatusCode() == MORALE) {
			moraleBuff();
		}else {
			costBuff();
		}
	}
	
	private void moraleBuff() {
		addEffect(0);
		if(additionCheck()) {
			GameData.moraleBoost(battle.GameData.UNIT, (int) effect());
			return;
		}
		GameData.lowMorale(battle.GameData.UNIT, (int) effect());
	}
	
	private void costBuff() {
		addEffect(0);
		if(additionCheck()) {
			GameData.addCost((int) effect());
			return;
		}
		GameData.consumeCost((int) effect());
	}
	
	//ユニットバフ
	private void unitBuff() {
		if(buffInformation.get(RANGE_CODE) == MYSELF) {
			myselfBuff();
			return;
		}
		if(buffInformation.get(RANGE_CODE) == ALL) {
			multipleBuff(i -> true);
			return;
		}
		if(buffInformation.get(RANGE_CODE) == WITHIN_RANGE) {
			multipleBuff(i -> withinCheck(i));
			return;
		}
		multipleBuff(i -> !withinCheck(i));
	}
	
	private void myselfBuff() {
		myself.receiveBuff(this);
		target = Arrays.asList(myself);
		effect = Arrays.asList(effect());
		if(intervalCheck() && durationCheck()) {
			return;
		}
		ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
		intervalControl(scheduler);
		durationControl(scheduler);
	}
	
	private void multipleBuff(Predicate<? super BattleData> rangeFilter) {
		ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
		targetControl(scheduler, rangeFilter);
		intervalControl(scheduler);
		durationControl(scheduler);
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
	
	private void targetControl(ScheduledExecutorService scheduler, Predicate<? super BattleData> rangeFilter) {
		scheduler.scheduleWithFixedDelay(() -> {
			Battle.timerWait();
			if(activateCheck(scheduler)) {
				return;
			}
			List<BattleData> newTarget = candidate.stream().filter(i -> i.getActivate()).filter(rangeFilter).toList();
			IntStream.range(0, target.size()).boxed().sorted(Comparator.reverseOrder()).forEach(i -> targetUpdate(i, newTarget));
			newTarget.forEach(this::targetUpdate);
		}, 0, DELEY, TimeUnit.MILLISECONDS);
	}
	
	private void targetUpdate(int number, List<BattleData> newTarget) {
		if(newTarget.stream().noneMatch(i -> i.equals(target.get(number)))) {
			target.get(number).removeBuff(this);
			if(HPCheck()) {
				target.get(number).HPBuff(0);
			}
			target.remove(number);
			effect.remove(number);
		}
	}
	
	private void targetUpdate(BattleData BattleData) {
		if(target.stream().noneMatch(i -> i.equals(BattleData))) {
			if(HPCheck()) {
				int defaultHP = BattleData.getMaxHP();
				addBuff(BattleData);
				BattleData.HPBuff(BattleData.getMaxHP() - defaultHP);
				return;
			}
			addBuff(BattleData);
		}
	}
	
	private void addBuff(BattleData BattleData) {
		BattleData.receiveBuff(this);
		target.add(BattleData);
		effect.add(effect());
	}
	
	private void intervalControl(ScheduledExecutorService scheduler) {
		if(intervalCheck()) {
			return;
		}
		scheduler.scheduleWithFixedDelay(() -> {
			Battle.timerWait();
			if(activateCheck(scheduler)) {
				return;
			}
			IntStream.range(0, effect.size()).filter(i -> !maxCheck(i)).forEach(i -> addEffect(i));
		}, getInterval(), getInterval(), TimeUnit.SECONDS);
	}
	
	//共通メソッド
	private void durationControl(ScheduledExecutorService scheduler) {
		if(durationCheck()) {
			return;
		}
		scheduler.scheduleWithFixedDelay(() -> {
			Battle.timerWait();
			durationCount++;
			if(buffInformation.get(DURATION) * 1000 / DELEY <= durationCount) {
				durationCount = 0;
				resetBuff();
				scheduler.shutdown();
			}
		}, 0, DELEY, TimeUnit.MILLISECONDS);
	}
	
	private boolean activateCheck(ScheduledExecutorService scheduler) {
		if(!myself.getActivate()) {
			resetBuff();
			scheduler.shutdown();
			return true;
		}
		return false;
	}
	
	private void resetBuff() {
		target.stream().forEach(i -> i.removeBuff(this));
		if(HPCheck()) {
			target.stream().forEach(i -> i.HPBuff(0));
		}
		target.clear();
		effect.clear();
	}
	
	private double recastMax() {
		return buffInformation.get(RECAST) * 1000 / DELEY;
	}
	
	private int getInterval() {
		return buffInformation.get(INTERVAL).intValue();
	}
	
	private boolean intervalCheck() {
		return getInterval() == NONE;
	}
	
	private boolean durationCheck() {
		return buffInformation.get(DURATION) == NONE;
	}
	
	private boolean maxCheck(int number) {
		if(buffInformation.get(MAX) == NONE) {
			return false;
		}
		return buffInformation.get(MAX) <= effect.get(number);
	}
	
	private boolean additionCheck() {
		return calculationCode() == ADDITION;
	}
	
	private boolean HPCheck() {
		return buffInformation.get(STATUS_CODE) == HP;
	}
	
	private double calculationCode() {
		return buffInformation.get(CALCULATION_CODE);
	}
	
	private double effect() {
		return buffInformation.get(EFFECT);
	}
	
	private void addEffect(int number) {
		effect.set(number, effect.get(number) + effect());
	}
	
	//データ返却
	protected double buffStatusCode() {
		return buffInformation.get(STATUS_CODE);
	}
	
	protected double buffTiming() {
		return buffInformation.get(TIMING_CODE);
	}
	
	protected double additionalEffect(BattleData BattleData, double status){
		if(calculationCode() == ADDITION) {
			return status += buffValue(BattleData);
		}
		if(calculationCode() == SUBTRACTION) {
			return status -= buffValue(BattleData);
		}
		return status;
	}
	
	protected double ratioEffect(BattleData BattleData, double status) {
		if(calculationCode() == MULTIPLICATION) {
			return status *= buffValue(BattleData);
		}
		if(calculationCode() == DIVISION) {
			return status /= buffValue(BattleData);
		}
		return status;
	}
	
	private double buffValue(BattleData BattleData) {
		return effect.get(target.indexOf(BattleData));
	}
	
	protected boolean possessSkill() {
		return buffTiming() == SKILL;
	}
	
	protected boolean getRecast() {
		return canRecast;
	}
	
	protected double recastRatio() {
		return recastCount / recastMax();
	}
}