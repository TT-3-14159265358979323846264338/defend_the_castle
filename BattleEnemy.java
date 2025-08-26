package battle;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import defaultdata.DefaultAtackPattern;
import defaultdata.DefaultEnemy;
import defaultdata.enemy.EnemyData;
import defaultdata.stage.StageData;

//敵のバトル情報
public class BattleEnemy extends BattleData{
	private int move;
	private int type;
	private List<List<Integer>> route;
	private int routeNumber;
	private int actitateTime;
	private int pauseCount;
	private int deactivateCount;
	private Object blockWait = new Object();
	
	protected BattleEnemy(Battle Battle, StageData StageData, int number) {
		this.Battle = Battle;
		EnemyData EnemyData = DefaultEnemy.DATA_MAP.get(StageData.getEnemy().get(number).get(0));
		name = EnemyData.getName();
		rightActionImage = EnemyData.getActionImage(4);
		bulletImage = EnemyData.getBulletImage(4);
		generatedBuffInformation = EnemyData.getBuff();
		hitImage = EnemyData.getHitImage(4);
		move = EnemyData.getMove();
		type = EnemyData.getType();
		route = StageData.getRoute().get(StageData.getEnemy().get(number).get(1));
		actitateTime = StageData.getEnemy().get(number).get(2);
		positionX = route.get(0).get(0);
		positionY = route.get(0).get(1);
		element = EnemyData.getElement().stream().toList();
		AtackPattern = new DefaultAtackPattern().getAtackPattern(EnemyData.getAtackPattern());
		defaultWeaponStatus = EnemyData.getWeaponStatus().stream().toList();
		defaultUnitStatus = EnemyData.getUnitStatus().stream().toList();
		defaultCutStatus = EnemyData.getCutStatus().stream().toList();
		canActivate = false;
		super.initialize();
	}
	
	protected void install(GameData GameData, BattleData[] unitMainData, BattleData[] facilityData, BattleData[] enemyData) {
		this.GameData = GameData;
		allyData = Stream.of(enemyData).toList();
		this.enemyData = Stream.concat(Stream.of(facilityData), Stream.of(unitMainData)).toList();
		if(element.stream().anyMatch(i -> i == 11)){
			AtackPattern.install(this, allyData);
		}else {
			AtackPattern.install(this, this.enemyData);
		}
		generatedBuff = IntStream.range(0, generatedBuffInformation.size()).mapToObj(i -> new Buff(generatedBuffInformation.get(i), this, allyData, this.enemyData, Battle, GameData)).toList();
		routeTimer();
	}
	
	public int getMove() {
		return move;
	}
	
	public int getType() {
		return type;
	}
	
	private void routeTimer() {
		ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
		int nowSpeed = getMoveSpeedOrBlock();
		if(nowSpeed <= 0) {
			eternalStop(scheduler);
			return;
		}
		constantMove(scheduler, nowSpeed);
	}
	
	private void eternalStop(ScheduledExecutorService scheduler) {
		scheduler.scheduleWithFixedDelay(() -> {
			if(actitateTime <= Battle.getMainTime()) {
				canActivate = true;
				GameData.moraleBoost(battle.GameData.ENEMY, 10);
				atackTimer();
				healTimer();
				scheduler.shutdown();
			}
		}, 0, 10, TimeUnit.MILLISECONDS);
	}
	
	private void constantMove(ScheduledExecutorService scheduler, int nowSpeed) {
		scheduler.scheduleWithFixedDelay(() -> {
			Battle.timerWait();
			timerWait();
			BattleData blockTarget = blockTarget();
			if(Objects.nonNull(blockTarget)) {
				blockTarget.addBlock(this);
				blockWait();
			}
			if(nowHP <= 0) {
				scheduler.shutdown();
				return;
			}
			if(nowSpeed != getMoveSpeedOrBlock()) {
				routeTimer();
				scheduler.shutdown();
				return;
			}
			if(canActivate || 0 < deactivateCount) {
				move();
				routeChange();
				return;
			}
			if(actitateTime <= Battle.getMainTime()) {
				GameData.moraleBoost(battle.GameData.ENEMY, 5);
				activate();
			}
		}, 0, 2000000 / nowSpeed, TimeUnit.MICROSECONDS);
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
			healTimer();
			activateBuff(Buff.BIGINNING);
		}
	}
	
	private void deactivate(){
		if(0 < route.get(routeNumber).get(4)) {
			deactivateCount++;
			canActivate = false;
		}
	}
	
	@Override
	protected int moraleRatio() {
		return (0 <= GameData.getMoraleDifference())? GameData.getMoraleDifference(): 0;
	}
	
	@Override
	protected void defeat() {
		canActivate = false;
		GameData.addCost(getCost());
		removeBlock(this);
		GameData.lowMorale(battle.GameData.ENEMY, 3);
		activateBuff(Buff.DEFEAT);
	}
}