package battle;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import defaultdata.DefaultAtackPattern;
import defaultdata.DefaultEnemy;
import defaultdata.enemy.EnemyData;
import defaultdata.stage.StageData;

//敵のバトル情報
public class BattleEnemy extends BattleData{
	//各難易度でのステータス補正倍率(difficultyCorrection)
	public static final double NORMAL_MODE = 1;
	public static final double HARD_MODE = 2;
	
	//基礎データ
	private int move;
	private int type;
	private List<List<Integer>> route;
	private int routeNumber;
	private int activateTime;
	
	//移動制御
	private int pauseCount;
	private int deactivateCount;
	private BattleData blockTarget;
	
	//システム関連
	private Object blockWait = new Object();
	private ScheduledExecutorService moveScheduler = Executors.newSingleThreadScheduledExecutor();
	private ScheduledFuture<?> moveFuture;
	private long beforeMoveTime;
	
	protected BattleEnemy(Battle Battle, StageData StageData, int number, double difficultyCorrection) {
		this.Battle = Battle;
		EnemyData EnemyData = DefaultEnemy.DATA_MAP.get(StageData.getEnemy().get(number).get(0));
		name = EnemyData.getName();
		explanation = EnemyData.getExplanation();
		rightActionImage = EnemyData.getActionImage(4);
		bulletImage = EnemyData.getBulletImage(4);
		generatedBuffInformation = EnemyData.getBuff();
		hitImage = EnemyData.getHitImage(4);
		move = EnemyData.getMove();
		type = EnemyData.getType();
		route = StageData.getRoute().get(StageData.getEnemy().get(number).get(1));
		activateTime = StageData.getEnemy().get(number).get(2);
		positionX = route.get(0).get(0);
		positionY = route.get(0).get(1);
		element = EnemyData.getElement().stream().toList();
		AtackPattern = new DefaultAtackPattern().getAtackPattern(EnemyData.getAtackPattern());
		defaultWeaponStatus = weaponStatus(EnemyData, difficultyCorrection);
		defaultUnitStatus = unitStatus(EnemyData, difficultyCorrection);
		defaultCutStatus = EnemyData.getCutStatus().stream().toList();
		canActivate = false;
		super.initialize();
	}
	
	private List<Integer> weaponStatus(EnemyData EnemyData, double difficultyCorrection){
		final int ATACK = (int) Buff.ATACK;
		List<Integer> defaultStatus = EnemyData.getWeaponStatus();
		defaultStatus.set(ATACK, defaultStatus(defaultStatus.get(ATACK), difficultyCorrection));
		return defaultStatus.stream().toList();
	}
	
	private List<Integer> unitStatus(EnemyData EnemyData, double difficultyCorrection){
		final int MAX_HP = (int) Buff.HP - 10;
		final int HP = MAX_HP + 1;
		final int DEFENCE = (int) Buff.DEFENCE - 10;
		final int HEAL = (int) Buff.HEAL - 10;
		List<Integer> defaultStatus = EnemyData.getUnitStatus();
		defaultStatus.set(MAX_HP, defaultStatus(defaultStatus.get(MAX_HP), difficultyCorrection));
		defaultStatus.set(HP, defaultStatus(defaultStatus.get(HP), difficultyCorrection));
		defaultStatus.set(DEFENCE, defaultStatus(defaultStatus.get(DEFENCE), difficultyCorrection));
		defaultStatus.set(HEAL, defaultStatus(defaultStatus.get(HEAL), difficultyCorrection));
		return defaultStatus.stream().toList();
	}
	
	private int defaultStatus(int status, double difficultyCorrection) {
		return (int) (status * difficultyCorrection);
	}
	
	protected void install(GameData GameData, BattleData[] unitMainData, BattleData[] facilityData, BattleData[] enemyData) {
		this.GameData = GameData;
		allyData = Stream.of(enemyData).toList();
		this.enemyData = Stream.concat(Stream.of(facilityData), Stream.of(unitMainData)).toList();
		if(element.stream().anyMatch(i -> i == DefaultEnemy.SUPPORT)){
			AtackPattern.install(this, allyData);
		}else {
			AtackPattern.install(this, this.enemyData);
		}
		generatedBuff = IntStream.range(0, generatedBuffInformation.size()).mapToObj(i -> new Buff(generatedBuffInformation.get(i), this, allyData, this.enemyData, Battle, GameData)).toList();
		moveTimer();
	}
	
	public int getMove() {
		return move;
	}
	
	public int getType() {
		return type;
	}
	
	private void moveTimer() {
		if(getMoveSpeedOrBlock() <= 0) {
			eternalStop();
			return;
		}
		constantMove(0);
	}
	
	private void eternalStop() {
		moveFuture = moveScheduler.scheduleAtFixedRate(() -> {
			if(activateTime <= Battle.getMainTime()) {
				canActivate = true;
				GameData.moraleBoost(battle.GameData.ENEMY, 10);
				atackTimer();
				healTimer(0);
				moveFuture.cancel(true);
			}
		}, 0, 10, TimeUnit.MILLISECONDS);
	}
	
	private void constantMove(long stopTime) {
		int nowSpeed = getMoveSpeedOrBlock();
		double delay = 2000000.0 / nowSpeed;
		double initialDelay;
		if(stopTime == 0) {
			initialDelay = 0;
		}else {
			initialDelay = ((stopTime - beforeMoveTime) * 1000 < delay)? delay - (stopTime - beforeMoveTime) * 1000: 0;
			beforeMoveTime += System.currentTimeMillis() - stopTime;
		}
		moveFuture = moveScheduler.scheduleAtFixedRate(() -> {
			beforeMoveTime = System.currentTimeMillis();
			if(canAtack) {
				CompletableFuture.runAsync(this::timerWait).thenRun(() -> constantMove(0));
				moveFuture.cancel(true);
				return;
			}
			blockTarget = blockTarget();
			if(Objects.nonNull(blockTarget)) {
				blockTarget.addBlock(this);
				CompletableFuture.runAsync(this::blockWait).thenRun(() -> constantMove(0));
				moveFuture.cancel(true);
				return;
			}
			if(nowHP <= 0) {
				moveFuture.cancel(true);
				return;
			}
			if(nowSpeed != getMoveSpeedOrBlock()) {
				CompletableFuture.runAsync(() -> moveFuture.cancel(true)).thenRun(() -> constantMove(0));
				return;
			}
			if(canActivate || 0 < deactivateCount) {
				move();
				routeChange();
				return;
			}
			if(activateTime <= Battle.getMainTime()) {
				GameData.moraleBoost(battle.GameData.ENEMY, 5);
				activate();
			}
		}, (int) initialDelay, (int) delay, TimeUnit.MICROSECONDS);
	}
	
	private BattleData blockTarget() {
		List<BattleData> nearList = enemyData.stream().filter(i -> i.canActivate()).filter(this::existsInside).toList();
		if(nearList.isEmpty()) {
			return null;
		}
		for(int i = 0; i < nearList.size(); i++) {
			if(nearList.get(i).getMoveSpeedOrBlock() < 0) {
				return nearList.get(i);
			}
			if(nearList.get(i).getBlock().size() < nearList.get(i).getMoveSpeedOrBlock()) {
				return nearList.get(i);
			}
		}
		return null;
	}
	
	private boolean existsInside(BattleData BattleData) {
		return Math.sqrt(Math.pow(positionX - BattleData.getPositionX(), 2) + Math.pow(positionY - BattleData.getPositionY(), 2)) <= battle.Battle.SIZE;
	}
	
	private void blockWait() {
		synchronized (blockWait) {
			try {
				blockWait.wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	protected void releaseBlock() {
		synchronized (blockWait) {
			blockWait.notifyAll();
		}
	}
	
	private void move() {
		if(0 < route.get(routeNumber).get(3)) {
			pauseCount++;
			return;
		}
		double radian = route.get(routeNumber).get(2) * Math.PI / 180;
		positionX += 2 * Math.cos(radian);
		positionY += 2 * Math.sin(radian);
	}
	
	private void routeChange() {
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
			if(Math.abs(route.get(routeNumber + 1).get(0) - positionX) <= 2
					|| Math.abs(route.get(routeNumber + 1).get(1) - positionY) <= 2) {
				routeNumber++;
				activate();
				deactivate();
			}
		}catch (Exception ignore) {
			//最後のrouteに入ったので、これ以上routeNumberは増えない
		}
	}
	
	private void activate() {
		if(!canActivate) {
			deactivateCount = 0;
			canActivate = true;
			atackTimer();
			healTimer(0);
			activateBuff(Buff.BIGINNING, null);
		}
	}
	
	private void deactivate(){
		if(0 < route.get(routeNumber).get(4)) {
			deactivateCount++;
			canActivate = false;
		}
	}
	
	@Override
	protected void individualSchedulerEnd() {
		moveScheduler.shutdown();
	}
	
	@Override
	protected void individualFutureStop() {
		if(moveFuture == null && moveFuture.isCancelled()) {
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
		CompletableFuture.runAsync(Battle::timerWait).thenRun(() -> constantMove(moveTime));
	}
	
	@Override
	protected int moraleCorrection() {
		return (0 <= GameData.getMoraleDifference())? GameData.getMoraleDifference(): 0;
	}
	
	@Override
	protected void defeat(BattleData target) {
		canActivate = false;
		GameData.addCost(getCost());
		removeBlock(this);
		GameData.lowMorale(battle.GameData.ENEMY, 3);
		activateBuff(Buff.DEFEAT, target);
	}
}