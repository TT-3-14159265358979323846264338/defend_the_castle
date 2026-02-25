package defendthecastle.battle;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import defaultdata.AtackPattern;
import defaultdata.Element;
import defaultdata.Enemy;
import defaultdata.Move;
import defaultdata.Type;
import defaultdata.enemy.EnemyData;
import defaultdata.stage.StageData;

//敵のバトル情報
public class BattleEnemy extends BattleData{
	//各難易度でのステータス補正倍率(difficultyCorrection)
	public static final double NORMAL_MODE = 1;
	public static final double HARD_MODE = 2;
	
	//基礎データ
	private Move move;
	private Type type;
	private List<List<Integer>> route;
	private int routeNumber;
	private int activateTime;
	private int resurrectionCount;
	private int interval;
	private final int SOTIE_MORALE = 5;
	private final int DEFEAT_MORALE = 3;
	private final int MOVE_DISTANCE = 2;
	
	//移動制御
	private int pauseCount;
	private int deactivateCount;
	private BattleData blockTarget;
	
	//システム関連
	private Object blockWait = new Object();
	private ScheduledFuture<?> moveFuture;
	private long beforeMoveTime;
	private ScheduledFuture<?> resuscitationFuture;
	private long beforeResuscitationTime;
	
	BattleEnemy(Battle Battle, StageData StageData, int number, double difficultyCorrection, ScheduledExecutorService scheduler) {
		this.Battle = Battle;
		EnemyData EnemyData = Enemy.getEnemyData(StageData.getEnemy().get(number).get(0));
		name = EnemyData.getName();
		explanation = EnemyData.getExplanation();
		rightActionImage = EnemyData.getActionImage(IMAGE_RATIO);
		bulletImage = EnemyData.getBulletImage(IMAGE_RATIO);
		generatedBuffInformation = EnemyData.getBuff();
		hitImage = EnemyData.getHitImage(IMAGE_RATIO);
		move = EnemyData.getMove();
		type = EnemyData.getType();
		route = StageData.getRoute().get(StageData.getEnemy().get(number).get(1));
		activateTime = StageData.getEnemy().get(number).get(2);
		resurrectionCount = StageData.getEnemy().get(number).get(3);
		interval = StageData.getEnemy().get(number).get(4);
		element = EnemyData.getElement().stream().toList();
		AtackPatternData = new AtackPattern().getAtackPattern(EnemyData.getAtackPattern());
		defaultWeaponStatus = weaponStatus(EnemyData, difficultyCorrection);
		defaultUnitStatus = unitStatus(EnemyData, difficultyCorrection);
		defaultCutStatus = EnemyData.getCutStatus().stream().toList();
		canActivate = false;
		super.initialize(scheduler);
	}
	
	List<Integer> weaponStatus(EnemyData EnemyData, double difficultyCorrection){
		final int ATACK = (int) Buff.ATACK;
		List<Integer> defaultStatus = EnemyData.getWeaponStatus();
		defaultStatus.set(ATACK, defaultStatus(defaultStatus.get(ATACK), difficultyCorrection));
		return defaultStatus.stream().toList();
	}
	
	List<Integer> unitStatus(EnemyData EnemyData, double difficultyCorrection){
		final int DEFENCE = (int) Buff.DEFENCE - 10;
		final int HEAL = (int) Buff.HEAL - 10;
		List<Integer> defaultStatus = EnemyData.getUnitStatus();
		defaultStatus.set(DEFENCE, defaultStatus(defaultStatus.get(DEFENCE), difficultyCorrection));
		defaultStatus.set(HEAL, defaultStatus(defaultStatus.get(HEAL), difficultyCorrection));
		return defaultStatus.stream().toList();
	}
	
	int defaultStatus(int status, double difficultyCorrection) {
		return (int) (status * difficultyCorrection);
	}
	
	void install(GameData GameData, BattleData[] unitMainData, BattleData[] facilityData, BattleData[] enemyData) {
		this.GameData = GameData;
		allyData = Stream.of(enemyData).toList();
		this.enemyData = Stream.concat(Stream.of(facilityData), Stream.of(unitMainData)).toList();
		if(element.stream().anyMatch(i -> i == Element.SUPPORT)){
			AtackPatternData.install(this, allyData);
		}else {
			AtackPatternData.install(this, this.enemyData);
		}
		generatedBuff = IntStream.range(0, generatedBuffInformation.size()).mapToObj(i -> new Buff(generatedBuffInformation.get(i), this, allyData, this.enemyData, Battle, GameData, scheduler)).toList();
		reset();
	}
	
	void reset() {
		routeNumber = 0;
		pauseCount = 0;
		nowHP = defaultUnitStatus.get(1);
		positionX = route.get(0).get(0);
		positionY = route.get(0).get(1);
		moveTimer();
	}
	
	public Move getMove() {
		return move;
	}
	
	public Type getType() {
		return type;
	}
	
	void moveTimer() {
		if(getMoveSpeedOrBlock() <= 0) {
			eternalStop();
			return;
		}
		constantMove(NONE_DELAY);
	}
	
	void eternalStop() {
		moveFuture = scheduler.scheduleAtFixedRate(() -> {
			if(activateTime <= Battle.getMainTime()) {
				canActivate = true;
				GameData.moraleBoost(defendthecastle.battle.GameData.ENEMY, SOTIE_MORALE);
				atackTimer(NONE_DELAY);
				healTimer(NONE_DELAY);
				moveFuture.cancel(true);
			}
		}, 0, 10, TimeUnit.MILLISECONDS);
	}
	
	void constantMove(long stopTime) {
		int nowSpeed = getMoveSpeedOrBlock();
		double delay = MOVE_DISTANCE * 1000000.0 / nowSpeed;
		double initialDelay;
		if(stopTime == NONE_DELAY) {
			initialDelay = NONE_DELAY;
		}else {
			initialDelay = ((stopTime - beforeMoveTime) * 1000 < delay)? delay - (stopTime - beforeMoveTime) * 1000: NONE_DELAY;
			beforeMoveTime += System.currentTimeMillis() - stopTime;
		}
		moveFuture = scheduler.scheduleAtFixedRate(() -> {
			beforeMoveTime = System.currentTimeMillis();
			if(nowHP <= 0) {
				moveFuture.cancel(true);
				return;
			}
			if(canAtack) {
				CompletableFuture.runAsync(this::atackWait, scheduler).thenRun(() -> constantMove(NONE_DELAY));
				moveFuture.cancel(true);
				return;
			}
			blockTarget = blockTarget();
			if(Objects.nonNull(blockTarget)) {
				blockTarget.addBlock(this);
				CompletableFuture.runAsync(() -> blockWait(blockTarget), scheduler).thenRun(() -> constantMove(NONE_DELAY));
				moveFuture.cancel(true);
				return;
			}
			if(nowSpeed != getMoveSpeedOrBlock()) {
				CompletableFuture.runAsync(() -> moveFuture.cancel(true), scheduler).thenRun(() -> constantMove(NONE_DELAY));
				return;
			}
			if(canActivate || 0 < deactivateCount) {
				move();
				routeChange();
				return;
			}
			if(activateTime <= Battle.getMainTime()) {
				GameData.moraleBoost(defendthecastle.battle.GameData.ENEMY, SOTIE_MORALE);
				activate();
			}
		}, (int) initialDelay, (int) delay, TimeUnit.MICROSECONDS);
	}
	
	BattleData blockTarget() {
		List<BattleData> nearList = enemyData.stream().filter(i -> i.canActivate()).filter(this::existsInside).toList();
		if(nearList.isEmpty()) {
			return null;
		}
		for(int i = 0; i < nearList.size(); i++) {
			if(nearList.get(i).getMoveSpeedOrBlock() < 0) {
				return nearList.get(i);
			}
			if(nearList.get(i).block.size() < nearList.get(i).getMoveSpeedOrBlock()) {
				return nearList.get(i);
			}
		}
		return null;
	}
	
	boolean existsInside(BattleData BattleData) {
		return Math.sqrt(Math.pow(positionX - BattleData.getPositionX(), 2) + Math.pow(positionY - BattleData.getPositionY(), 2)) <= defendthecastle.battle.Battle.SIZE;
	}
	
	void blockWait(BattleData blockTarget) {
		synchronized (blockWait) {
			try {
				if(blockTarget.canActivate()) {
					blockWait.wait();
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	void releaseBlock() {
		synchronized (blockWait) {
			blockWait.notifyAll();
		}
	}
	
	void move() {
		if(0 < route.get(routeNumber).get(3)) {
			pauseCount++;
			return;
		}
		double radian = route.get(routeNumber).get(2) * Math.PI / 180;
		positionX += MOVE_DISTANCE * Math.cos(radian);
		positionY += MOVE_DISTANCE * Math.sin(radian);
	}
	
	void routeChange() {
		//描写停止中の時は指定の描写回数になったら描写を開始する
		//deactivateCountは独立制御(描写停止中に、移動も停止もできる)
		if(0 < deactivateCount) {
			deactivateCount++;
			if(deactivateCount == route.get(routeNumber).get(4)) {
				activate();
			}
		}
		//移動停止中の時は指定の描写回数になったら次のルートに入る
		//pauseCountは選択制御(停止中に移動はできない)なのでreturn
		if(0 < pauseCount) {
			if(pauseCount == route.get(routeNumber).get(3)) {
				routeNumber++;
				pauseCount = 0;
				activate();
				deactivate();
			}
			return;
		}
		try {
			//所定の位置に到達したら次のルートに入る
			if(Math.abs(route.get(routeNumber + 1).get(0) - positionX) <= MOVE_DISTANCE
					|| Math.abs(route.get(routeNumber + 1).get(1) - positionY) <= MOVE_DISTANCE) {
				routeNumber++;
				activate();
				deactivate();
			}
		}catch (Exception ignore) {
			//最後のrouteに入ったので、これ以上routeNumberは増えない
		}
	}
	
	void activate() {
		if(!canActivate) {
			deactivateCount = 0;
			canActivate = true;
			atackTimer(NONE_DELAY);
			healTimer(NONE_DELAY);
			activateBuff(Buff.BIGINNING, null);
		}
	}
	
	void deactivate(){
		if(0 < route.get(routeNumber).get(4)) {
			deactivateCount++;
			canActivate = false;
		}
	}
	
	@Override
	protected void individualFutureStop() {
		if(resuscitationFuture != null && !resuscitationFuture.isDone()) {
			resuscitationFuture.cancel(true);
			long resuscitationTime = System.currentTimeMillis();
			CompletableFuture.runAsync(Battle::timerWait, scheduler).thenRun(() -> resurrection(resuscitationTime));
			return;
		}
		if(moveFuture == null) {
			return;
		}
		if(moveFuture.isCancelled()) {
			return;
		}
		if(canAtack) {
			return;
		}
		if(blockTarget != null) {
			return;
		}
		if(getMoveSpeedOrBlock() <= 0) {
			return;
		}
		moveFuture.cancel(true);
		long moveTime = System.currentTimeMillis();
		CompletableFuture.runAsync(Battle::timerWait, scheduler).thenRun(() -> constantMove(moveTime));
	}
	
	@Override
	protected int moraleCorrection() {
		return (0 <= GameData.getMoraleDifference())? GameData.getMoraleDifference(): 0;
	}
	
	@Override
	protected void defeat(BattleData target) {
		canActivate = false;
		GameData.addCost(getCost());
		releaseBlock(this);
		GameData.lowMorale(defendthecastle.battle.GameData.ENEMY, DEFEAT_MORALE);
		activateBuff(Buff.DEFEAT, target);
		moveFuture.cancel(true);
		beforeResuscitationTime = System.currentTimeMillis();
		resurrection(NONE_DELAY);
	}
	
	void resurrection(long stopTime) {
		if(resurrectionCount == 0) {
			return;
		}
		long delay;
		if(stopTime == NONE_DELAY) {
			delay = interval;
		}else {
			delay = (stopTime - beforeResuscitationTime < interval)? interval - (stopTime - beforeResuscitationTime): NONE_DELAY;
			beforeResuscitationTime += System.currentTimeMillis() - stopTime;
		}
		resuscitationFuture = scheduler.schedule(() -> {
			resurrectionCount--;
			reset();
		}, delay, TimeUnit.MILLISECONDS);
	}
}