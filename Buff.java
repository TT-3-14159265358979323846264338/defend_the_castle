package battle;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

//各バフの発生管理
public class Buff {
	//発生タイミングコード
	public final static double BIGINNING = 0;
	public final static double SKILL = 1;
	public final static double ATACK = 2;
	public final static double HIT = 3;
	public final static double DEFEAT = 4;
	
	//発生対象コード
	public final static double ALLY = 0;
	public final static double ENEMY = 1;
	public final static double GAME = 2;
	
	//効果範囲コード
	public final static double ALL = 0;
	public final static double WITHIN_RANGE = 1;
	public final static double OUT_RANGE = 2;
	public final static double MYSELF = 3;
	
	//対象ステータスコード
	public final static double POWER = 0;
	public final static double RANGE = 1;
	public final static double ATACK_SPEED = 2;
	public final static double ATACK_NUMBER = 3;
	
	public final static double HP = 10;
	public final static double DEFENCE = 12;
	public final static double HEAL = 13;
	public final static double MOVE_SPEED_OR_BLOCK = 14;
	public final static double COST = 15;
	
	public final static double SLASH = 100;
	public final static double PIERCE = 101;
	public final static double STRIKE = 102;
	public final static double IMPACT = 103;
	public final static double FLAME = 104;
	public final static double WATER = 105;
	public final static double WIND = 106;
	public final static double SOIL = 107;
	public final static double THUNDER = 108;
	public final static double HOLY = 109;
	public final static double DARK = 110;
	public final static double SUPPORT = 111;
	
	public final static double MORALE = 0;
	public final static double GAME_COST = 1;
	
	//加減乗除コード
	public final static double ADDITION = 0;
	public final static double SUBTRACTION = 1;
	public final static double MULTIPLICATION = 2;
	public final static double DIVISION = 3;
	
	//コードなし
	public final static double NONE = 0;
	
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
	
	//インスタンス変数
	List<Double> buffInformation;
	BattleData myself;
	BattleData[] candidate;
	Battle Battle;
	GameData GameData;
	List<BattleData> target = new ArrayList<>();
	List<Double> effect = new ArrayList<>();
	int durationCount = 0;
	int recastCount = 0;
	boolean canRecast = true;
	
	protected Buff(List<Double> buffInformation, BattleData myself, BattleData[] ally, BattleData[] enemy, Battle Battle, GameData GameData) {
		//テスト用
		//発生タイミングコード, 発生対象コード, 効果範囲コード, 対象ステータスコード, 加減乗除コード, 効果量, 効果発生間隔[s](Buff.NONE: なし), 上限量(Buff.NONE: なし), 効果時間[s](Buff.NONE: 無限), 再使用時間[s](Buff.NONE: なし)
		this.buffInformation = Arrays.asList(Buff.BIGINNING, Buff.ALLY, Buff.ALL, Buff.POWER, Buff.ADDITION, 10.0, Buff.NONE, Buff.NONE, Buff.NONE, Buff.NONE);
		
		//this.buffInformation = buffInformation;
		this.myself = myself;
		this.Battle = Battle;
		if(buffInformation.get(TARGET_CODE) == ALLY) {
			candidate = ally;
		}else if(buffInformation.get(TARGET_CODE) == ENEMY) {
			candidate = enemy;
		}else {
			this.GameData = GameData;
		}
		if(buffInformation.get(TIMING_CODE) == BIGINNING) {
			buffStart();
		}
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
			if(buffInformation.get(RECAST) * 1000 / 20 <= recastCount) {
				recastCount = 0;
				canRecast = true;
				scheduler.shutdown();
			}
		}, 0, 20, TimeUnit.MILLISECONDS);
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
			gameBuffSelect();
			if(maxCheck(0)) {
				scheduler.shutdown();
			}
		}, 0, buffInformation.get(INTERVAL).intValue(), TimeUnit.SECONDS);
		durationControl(scheduler);
	}
	
	private void durationControl(ScheduledExecutorService scheduler) {
		if(buffInformation.get(DURATION) == NONE) {
			return;
		}
		scheduler.scheduleWithFixedDelay(() -> {
			Battle.timerWait();
			durationCount++;
			if(buffInformation.get(DURATION) * 1000 / 20 <= durationCount) {
				effect.clear();
				durationCount = 0;
				scheduler.shutdown();
			}
		}, 0, 20, TimeUnit.MILLISECONDS);
	}
	
	private void gameBuffSelect() {
		if(buffInformation.get(STATUS_CODE) == MORALE) {
			moraleBuff();
		}else {
			costBuff();
		}
	}
	
	private void moraleBuff() {
		addEffect(0);
		if(additionCheck()) {
			
			GameData.moraleBoost(allyCheck(), effect());
			return;
		}
		GameData.lowMorale(allyCheck(), effect());
	}
	
	private void costBuff() {
		addEffect(0);
		if(additionCheck()) {
			GameData.addCost(effect());
			return;
		}
		GameData.consumeCost(effect());
	}
	
	//ユニットバフ
	private void unitBuff() {
		if(buffInformation.get(RANGE_CODE) == ALL) {
			allBuff();
			return;
		}
		if(buffInformation.get(RANGE_CODE) == MYSELF) {
			myselfBuff();
			return;
		}
		if(buffInformation.get(RANGE_CODE) == WITHIN_RANGE) {
			
			
			
			
			
			return;
		}
		if(buffInformation.get(RANGE_CODE) == OUT_RANGE) {
			
			
			
			
			
			return;
		}
	}
	
	private void allBuff() {
		target = Stream.of(candidate).collect(Collectors.toList());
		effect = Stream.of(candidate).map(i -> 0.0).collect(Collectors.toList());
		if(intervalCheck()) {
			IntStream.range(0, effect.size()).forEach(i -> addEffect(i));
			return;
		}
		ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
		scheduler.scheduleWithFixedDelay(() -> {
			Battle.timerWait();
			IntStream.range(0, effect.size()).filter(i -> !maxCheck(i)).forEach(i -> addEffect(i));
		}, 0, buffInformation.get(INTERVAL).intValue(), TimeUnit.SECONDS);
		durationControl(scheduler);
	}
	
	private void allBuffControl() {
		Stream.of(candidate).filter(i -> i.getActivate()).forEach(this::updateBuff);
		
		
		
		
		
		
		
		
		
	}
	
	private void updateBuff(BattleData BattleData) {
		if(target.stream().noneMatch(i -> i.equals(BattleData))) {
			
			
			
		}
	}
	
	
	
	
	private void myselfBuff() {
		target = Arrays.asList(myself);
		effect = Arrays.asList(buffInformation.get(EFFECT));
		
		
		
		
	}
	
	
	
	
	
	
	
	//共通メソッド
	private boolean intervalCheck() {
		return buffInformation.get(INTERVAL) == NONE;
	}
	
	private boolean maxCheck(int number) {
		if(buffInformation.get(MAX) == NONE) {
			return false;
		}
		return buffInformation.get(MAX) <= effect.get(number);
	}
	
	private boolean additionCheck() {
		return buffInformation.get(CALCULATION_CODE) == ADDITION;
	}
	
	private boolean allyCheck() {
		return buffInformation.get(TARGET_CODE) == ALLY;
	}
	
	private int effect() {
		return buffInformation.get(EFFECT).intValue();
	}
	
	private void addEffect(int number) {
		effect.set(number, effect.get(number) + effect());
	}
	
	
	
	
	
	
	
	
	
	//データ返却
	protected List<BattleData> getTarget(){
		return target;
	}
	
	protected List<Double> getEffect(){
		return effect;
	}
	
	protected boolean recast() {
		return canRecast;
	}
}