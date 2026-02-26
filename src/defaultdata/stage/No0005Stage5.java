package defaultdata.stage;

import java.awt.Point;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

import defaultdata.Enemy;
import defaultdata.Facility;
import defendthecastle.battle.BattleEnemy;
import defendthecastle.battle.BattleFacility;
import defendthecastle.battle.BattleUnit;
import defendthecastle.battle.GameData;
import defendthecastle.battle.GameTimer;
import savedata.SaveGameProgress;

public class No0005Stage5 extends StageData {
	@Override
	public String getName() {
		return "stage 5";
	}

	@Override
	public boolean canActivate(SaveGameProgress saveGameProgress) {
		return hasClearedMerit(saveGameProgress, 3, 3, 1) && hasClearedMerit(saveGameProgress, 0, 3, 10);
	}

	@Override
	public String getImageName() {
		return "image/field/stage 5.png";
	}

	@Override
	public List<Facility> getFacility() {
		return Arrays.asList(Facility.STRONGHOLD);
	}

	@Override
	public List<Boolean> getFacilityDirection() {
		return Arrays.asList(true);
	}

	@Override
	public List<Point> getFacilityPoint() {
		return Arrays.asList(new Point(828, 383));
	}

	@Override
	public List<List<List<Double>>> getPlacementPoint() {
		double size = 29.5;
		double centerX = 483;
		double centerY = 265;
		return Arrays.asList(
				Arrays.asList(Arrays.asList(centerX - size * 13, centerY - size * 7),
						Arrays.asList(centerX - size * 11, centerY - size * 7),
						Arrays.asList(centerX - size * 9, centerY - size * 7),
						Arrays.asList(centerX - size * 15, centerY - size * 5),
						Arrays.asList(centerX - size * 15, centerY - size * 3),
						
						Arrays.asList(centerX - size * 15, centerY + size),
						Arrays.asList(centerX - size * 13, centerY + size),
						Arrays.asList(centerX - size * 11, centerY + size),
						Arrays.asList(centerX - size * 9, centerY + size),
						Arrays.asList(centerX - size * 9, centerY - size),
						Arrays.asList(centerX - size * 7, centerY - size),
						
						Arrays.asList(centerX - size * 11, centerY + size * 5),
						Arrays.asList(centerX - size * 11, centerY + size * 7),
						Arrays.asList(centerX - size * 9, centerY + size * 5),
						Arrays.asList(centerX - size * 7, centerY + size * 5),
						
						Arrays.asList(centerX, centerY - size),
						Arrays.asList(centerX + size * 3, centerY - size),
						Arrays.asList(centerX + size * 6, centerY - size),
						Arrays.asList(centerX + size * 6, centerY + size * 2),
						Arrays.asList(centerX, centerY + size * 5),
						Arrays.asList(centerX + size * 3, centerY + size * 5),
						Arrays.asList(centerX + size * 6, centerY + size * 5),
						Arrays.asList(centerX + size * 9, centerY + size * 5),
						Arrays.asList(centerX + size * 16, centerY - size),
						Arrays.asList(centerX + size * 16, centerY + size * 2),
						Arrays.asList(centerX + size * 16, centerY + size * 5)
						),
				Arrays.asList(Arrays.asList(centerX - size * 13, centerY - size * 5),
						Arrays.asList(centerX - size * 13, centerY - size * 3),
						
						Arrays.asList(centerX - size * 15, centerY + size * 3),
						Arrays.asList(centerX - size * 15, centerY + size * 5),
						Arrays.asList(centerX - size * 15, centerY + size * 7),
						
						Arrays.asList(centerX - size * 7, centerY + size),
						
						Arrays.asList(centerX - size * 9, centerY + size * 7),
						Arrays.asList(centerX - size * 7, centerY + size * 7),
						
						Arrays.asList(centerX, centerY + size * 2),
						Arrays.asList(centerX + size * 3, centerY + size * 2),
						Arrays.asList(centerX + size * 9, centerY - size),
						Arrays.asList(centerX + size * 9, centerY + size * 2),
						Arrays.asList(centerX, centerY + size * 8),
						Arrays.asList(centerX + size * 3, centerY + size * 8),
						Arrays.asList(centerX + size * 6, centerY + size * 8),
						Arrays.asList(centerX + size * 9, centerY + size * 8)
						),
				Arrays.asList());
	}

	@Override
	public List<List<Boolean>> canUsePlacement(GameTimer gameTimer, BattleEnemy[] enemyData) {
		boolean hasBrokenFrontGate = canAllDefeat(enemyData[1]);
		boolean hasBrokenSubGate = canAllDefeat(enemyData[2]);
		boolean hasBrokenUpperGate = hasBrokenFrontGate? true: canAllDefeat(enemyData[3]);
		boolean hasBrokenLowerGate = hasBrokenFrontGate? true: canAllDefeat(enemyData[4]);
		return Arrays.asList(
				Arrays.asList(hasBrokenFrontGate,
						hasBrokenFrontGate,
						hasBrokenFrontGate,
						hasBrokenFrontGate,
						hasBrokenFrontGate,
						
						hasBrokenUpperGate,
						hasBrokenUpperGate,
						hasBrokenUpperGate,
						hasBrokenUpperGate,
						hasBrokenUpperGate,
						hasBrokenUpperGate,
						
						hasBrokenLowerGate,
						hasBrokenLowerGate,
						hasBrokenLowerGate,
						hasBrokenLowerGate,
						
						true,
						true,
						true,
						true,
						true,
						true,
						true,
						true,
						true,
						true,
						true
						),
				Arrays.asList(hasBrokenFrontGate,
						hasBrokenFrontGate,
						
						hasBrokenSubGate,
						hasBrokenSubGate,
						hasBrokenSubGate,
						
						hasBrokenUpperGate,
						
						hasBrokenLowerGate,
						hasBrokenLowerGate,
						
						true,
						true,
						true,
						true,
						true,
						true,
						true,
						true
						),
				Arrays.asList());
	}
	
	@Override
	public int getCost() {
		return 80;
	}
	
	@Override
	public List<Integer> getMorale(){
		return Arrays.asList(0, 0);
	}

	@Override
	public String getClearCondition() {
		return "敵の本丸を制圧する";
	}

	@Override
	public boolean canClear(BattleUnit[] unitMainData, BattleUnit[] unitLeftData, BattleFacility[] facilityData, BattleEnemy[] enemyData, GameData gameData) {
		return canAllDefeat(enemyData[0]);
	}

	@Override
	public String getGameOverCondition() {
		return "本陣を制圧される";
	}

	@Override
	public boolean existsGameOver(BattleUnit[] unitMainData, BattleUnit[] unitLeftData, BattleFacility[] facilityData, BattleEnemy[] enemyData, GameData gameData) {
		return canAllBreak(facilityData[0]);
	}

	@Override
	public List<String> getMerit() {
		return Arrays.asList("ステージをクリアする(normal)",
				"本陣が1度も攻撃を受けない(normal)",
				"ステージをクリアする(hard)",
				"ユニットが一度も倒されない(hard)");
	}

	@Override
	public List<Boolean> canClearMerit(BattleUnit[] unitMainData, BattleUnit[] unitLeftData, BattleFacility[] facilityData, BattleEnemy[] enemyData, GameData gameData, double nowDifficulty) {
		return Arrays.asList(canClearStage(BattleEnemy.NORMAL_MODE, nowDifficulty),
				hasNotHited(BattleEnemy.NORMAL_MODE, nowDifficulty, facilityData[0]),
				canClearStage(BattleEnemy.HARD_MODE, nowDifficulty),
				canNotDefeat(BattleEnemy.HARD_MODE, nowDifficulty, unitMainData, unitLeftData));
	}

	@Override
	public List<String> getReward() {
		return Arrays.asList("メダル100",
				"メダル200",
				"メダル300",
				"メダル500");
	}

	@Override
	protected List<Method> giveReward() {
		try {
			return Arrays.asList(getClass().getMethod("give100Medal"),
					getClass().getMethod("give200Medal"),
					getClass().getMethod("give300Medal"),
					getClass().getMethod("give500Medal"));
		} catch (Exception e) {
			e.printStackTrace();
			return Arrays.asList();
		}
	}
	
	@Override
	public List<List<Integer>> getEnemy() {
		return Arrays.asList(
				Arrays.asList(Enemy.CASTLE.getId(), 0, 0, 0, 0),
				Arrays.asList(Enemy.FRONT_GATE.getId(), 4, 0, 0, 0),
				Arrays.asList(Enemy.SIDE_GATE.getId(), 5, 0, 0, 0),
				Arrays.asList(Enemy.SIDE_GATE.getId(), 6, 0, 0, 0),
				Arrays.asList(Enemy.SIDE_GATE.getId(), 7, 0, 0, 0),
				Arrays.asList(Enemy.FRONT_GATE.getId(), 8, 0, 0, 0),
				
				Arrays.asList(Enemy.BLUE_SLIME.getId(), 0, 1000, 5, 3000),
				Arrays.asList(Enemy.BLUE_SLIME.getId(), 3, 1000, 5, 3000),
				Arrays.asList(Enemy.BLUE_SLIME.getId(), 0, 5000, 5, 3000),
				Arrays.asList(Enemy.BLUE_SLIME.getId(), 2, 10000, 5, 3000),
				
				Arrays.asList(Enemy.GREEN_SLIME.getId(), 3, 60000, 5, 2000),
				Arrays.asList(Enemy.YELLOW_SLIME.getId(), 2, 70000, 5, 2000),
				Arrays.asList(Enemy.RED_SLIME.getId(), 0, 80000, 5, 2000),
				
				Arrays.asList(Enemy.RED_SLIME.getId(), 0, 100000, 5, 2000),
				Arrays.asList(Enemy.YELLOW_SLIME.getId(), 1, 100000, 5, 2000),
				Arrays.asList(Enemy.RED_SLIME.getId(), 0, 110000, 5, 2000),
				
				Arrays.asList(Enemy.HIGH_SLIME.getId(), 2, 110000, -1, 2000),
				Arrays.asList(Enemy.GREEN_SLIME.getId(), 0, 110000, 5, 2000),
				Arrays.asList(Enemy.GREEN_SLIME.getId(), 0, 120000, 5, 2000),
				
				Arrays.asList(Enemy.HIGH_SLIME.getId(), 0, 150000, -1, 3000),
				Arrays.asList(Enemy.HIGH_SLIME.getId(), 1, 150500, -1, 3000),
				
				Arrays.asList(Enemy.HIGH_SLIME.getId(), 0, 200000, -1, 3000),
				Arrays.asList(Enemy.HIGH_SLIME.getId(), 2, 200000, -1, 3000),
				
				Arrays.asList(Enemy.HIGH_SLIME.getId(), 0, 300000, -1, 2000),
				Arrays.asList(Enemy.HIGH_SLIME.getId(), 1, 300500, -1, 2000),
				Arrays.asList(Enemy.HIGH_SLIME.getId(), 2, 300000, -1, 2000),
				
				Arrays.asList(Enemy.HIGH_SLIME.getId(), 0, 400000, -1, 1500),
				Arrays.asList(Enemy.HIGH_SLIME.getId(), 1, 400500, -1, 1500),
				Arrays.asList(Enemy.HIGH_SLIME.getId(), 2, 400000, -1, 1500)
				);
	}

	@Override
	public List<Integer> getDisplayOrder() {
		return Arrays.asList(Enemy.BLUE_SLIME.getId(), Enemy.RED_SLIME.getId(), Enemy.GREEN_SLIME.getId(), Enemy.YELLOW_SLIME.getId(), Enemy.HIGH_SLIME.getId());
	}

	@Override
	public List<List<List<Integer>>> getRoute() {
		return Arrays.asList(
				//route0: 本丸から上門へ
				Arrays.asList(
						Arrays.asList(11, 29, 90, 0, 0),
						Arrays.asList(0, 265, 0, 0, 0),
						Arrays.asList(188, 0, 270, 0, 0),
						Arrays.asList(0, 206, 0, 0, 0),
						Arrays.asList(631, 0, 90, 0, 0),
						Arrays.asList(0, 383, 0, 0, 0)
						),
				//route1: 本丸から下門へ
				Arrays.asList(
						Arrays.asList(11, 29, 90, 0, 0),
						Arrays.asList(0, 265, 0, 0, 0),
						Arrays.asList(129, 0, 90, 0, 0),
						Arrays.asList(0, 383, 0, 0, 0)
						),
				//route2: 本丸から井戸へ
				Arrays.asList(
						Arrays.asList(11, 29, 0, 0, 0),
						Arrays.asList(247, 0, 0, 0, 1000),
						Arrays.asList(926, 0, 0, 200, 200),
						Arrays.asList(0, 0, 90, 0, 0),
						Arrays.asList(0, 383, 180, 0, 0)
						),
				//route3: 出丸から出撃
				Arrays.asList(
						Arrays.asList(631, -50, 90, 0, 0),
						Arrays.asList(0, 29, 0, 200, 0),
						Arrays.asList(0, 0, 90, 0, 0),
						Arrays.asList(0, 383, 0, 0, 0)
						),
				//route4: 正面門
				Arrays.asList(
						Arrays.asList(11, 206, 0, 0, 0)
						),
				//route5: 左下門
				Arrays.asList(
						Arrays.asList(70, 415, 0, 0, 0)
						),
				//route6: 中央上門
				Arrays.asList(
						Arrays.asList(306, 206, 0, 0, 0)
						),
				//route7: 中央下門
				Arrays.asList(
						Arrays.asList(306, 383, 0, 0, 0)
						),
				//route8: 出丸門
				Arrays.asList(
						Arrays.asList(631, 138, 0, 0, 0)
						)
				);
	}
}