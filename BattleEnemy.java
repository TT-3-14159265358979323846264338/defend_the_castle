package battle;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import defaultdata.DefaultEnemy;
import defaultdata.EditImage;
import defaultdata.enemy.EnemyData;
import defaultdata.stage.StageData;

//敵のバトル情報
public class BattleEnemy extends BattleData{
	int move;
	int type;
	List<List<Integer>> route;
	int routeNumber;
	int actitateTime;
	int pauseCount;
	int deactivateCount;
	
	protected BattleEnemy(Battle Battle, StageData StageData, int number) {
		this.Battle = Battle;
		EnemyData EnemyData = new DefaultEnemy().getEnemyData(StageData.getEnemy().get(number).get(0));
		name = EnemyData.getName();
		actionImage = new EditImage().input(EnemyData.getActionImageName(), 4);
		move = EnemyData.getMove();
		type = EnemyData.getType();
		route = StageData.getRoute().get(StageData.getEnemy().get(number).get(1));
		actitateTime = StageData.getEnemy().get(number).get(2);
		positionX = route.get(0).get(0);
		positionY = route.get(0).get(1);
		element = EnemyData.getElement();
		defaultWeaponStatus = EnemyData.getWeaponStatus().stream().toList();
		defaultUnitStatus = EnemyData.getUnitStatus().stream().toList();
		defaultCutStatus = EnemyData.getCutStatus().stream().toList();
		canActivate = false;
		super.initialize();
		routeTimer();
	}
	
	public int getMove() {
		return move;
	}
	
	public int getType() {
		return type;
	}
	
	private void routeTimer() {
		ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
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
				scheduler.shutdown();
			}
		}, 0, 10, TimeUnit.MILLISECONDS);
	}
	
	private void constantMove(ScheduledExecutorService scheduler, int nowSpeed) {
		scheduler.scheduleWithFixedDelay(() -> {
			CompletableFuture.allOf(CompletableFuture.runAsync(() -> Battle.timerWait()), CompletableFuture.runAsync(() -> block())).join();
			if(nowHP <= 0) {
				crush(scheduler);
				return;
			}
			if(canActivate || 0 < deactivateCount) {
				move();
				routeChange();
				speedMonitoring(scheduler, nowSpeed);
				return;
			}
			if(actitateTime <= Battle.getMainTime()) {
				activateMode();
			}
		}, 0, 2000000 / nowSpeed, TimeUnit.MICROSECONDS);
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
				activateMode();
			}
		}
		//移動停止中の時は指定の描写回数になったら次のルートに入る
		//pauseCountは選択制御(停止中に移動はできない)なのでreturn
		if(0 < pauseCount) {
			if(pauseCount == route.get(routeNumber).get(3)) {
				routeNumber++;
				pauseCount = 0;
				activateMode();
				deactivateMode();
			}
			return;
		}
		try {
			//所定の位置に到達したら次のルートに入る
			if(Math.abs(route.get(routeNumber + 1).get(0) - positionX) <= 2
					|| Math.abs(route.get(routeNumber + 1).get(1) - positionY) <= 2) {
				routeNumber++;
				activateMode();
				deactivateMode();
			}
		}catch (Exception ignore) {
			//最後のrouteに入ったので、これ以上routeNumberは増えない
		}
	}
	
	private void speedMonitoring(ScheduledExecutorService scheduler, int nowSpeed) {
		if(nowSpeed != getMoveSpeedOrBlock()) {
			routeTimer();
			scheduler.shutdown();
		}
	}
	
	private void activateMode() {
		deactivateCount = 0;
		canActivate = true;
	}
	
	private void deactivateMode(){
		if(0 < route.get(routeNumber).get(4)) {
			deactivateCount++;
			canActivate = false;
		}
	}
	
	private void crush(ScheduledExecutorService scheduler){
		canActivate = false;
		scheduler.shutdown();
	}
	
	private void block() {
		
	}
	
	
	
	
}