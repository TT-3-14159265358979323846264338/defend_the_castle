package defaultdata.stage;

import java.awt.Point;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

import battle.BattleEnemy;
import battle.BattleFacility;
import battle.BattleUnit;
import battle.GameData;
import defaultdata.DefaultEnemy;
import defaultdata.DefaultStage;

public class No0002Stage3 extends StageData {
	@Override
	public String getName() {
		return "stage 3";
	}

	@Override
	public String getImageName() {
		return "image/field/stage 3.png";
	}

	@Override
	public List<Integer> getFacility() {
		return Arrays.asList(DefaultStage.CASTLE, DefaultStage.GATE, DefaultStage.GATE, DefaultStage.GATE);
	}

	@Override
	public List<Boolean> getFacilityDirection() {
		return Arrays.asList(true, true, false, true);
	}

	@Override
	public List<Point> getFacilityPoint() {
		return Arrays.asList(new Point(875, 20), new Point(73, 205), new Point(367, 30), new Point(783, 143));
	}

	@Override
	public List<List<List<Double>>> getPlacementPoint() {
		double size = 29.5;
		double centerX = 483;
		double centerY = 265;
		return Arrays.asList(
				Arrays.asList(Arrays.asList(centerX - size * 13, centerY + size),
						Arrays.asList(centerX - size * 13, centerY + size * 4),
						Arrays.asList(centerX - size * 13, centerY + size * 7),
						Arrays.asList(centerX - size, centerY - size * 7),
						Arrays.asList(centerX + size, centerY - size * 7),
						Arrays.asList(centerX + size, centerY - size * 5),
						Arrays.asList(centerX + size, centerY - size * 3)
						),
				Arrays.asList(Arrays.asList(centerX - size * 15, centerY + size),
						Arrays.asList(centerX - size * 15, centerY + size * 4),
						Arrays.asList(centerX - size * 15, centerY + size * 7),
						Arrays.asList(centerX - size * 11, centerY + size),
						Arrays.asList(centerX - size * 11, centerY + size * 4),
						Arrays.asList(centerX - size, centerY - size * 5),
						Arrays.asList(centerX - size, centerY - size * 3),
						Arrays.asList(centerX + size * 9, centerY - size * 7),
						Arrays.asList(centerX + size * 9, centerY - size * 5),
						Arrays.asList(centerX + size * 13, centerY - size * 3)
						),
				Arrays.asList(Arrays.asList(centerX + size * 11, centerY - size * 7),
						Arrays.asList(centerX + size * 11, centerY - size * 5),
						Arrays.asList(centerX + size * 15, centerY - size * 5),
						Arrays.asList(centerX + size * 15, centerY - size * 3)
						));
	}
	
	@Override
	public int getCost() {
		return 50;
	}
	
	@Override
	public List<Integer> getMorale(){
		return Arrays.asList(0, 0);
	}

	@Override
	public String getClearCondition() {
		return "全ての敵を撃破する";
	}

	@Override
	public boolean canClear(BattleUnit[] UnitMainData, BattleUnit[] UnitLeftData, BattleFacility[] FacilityData, BattleEnemy[] EnemyData, GameData GameData) {
		return canAllDefeat(EnemyData);
	}

	@Override
	public String getGameOverCondition() {
		return "本丸を制圧される";
	}

	@Override
	public boolean existsGameOver(BattleUnit[] UnitMainData, BattleUnit[] UnitLeftData, BattleFacility[] FacilityData, BattleEnemy[] EnemyData, GameData GameData) {
		return canAllBreak(FacilityData[0]);
	}

	@Override
	public List<String> getMerit() {
		return Arrays.asList("ステージをクリアする(normal)",
				"本丸が1度も攻撃を受けない(normal)",
				"ステージをクリアする(hard)",
				"ユニットが一度も倒されずクリアする(hard)");
	}

	@Override
	public List<Boolean> canClearMerit(BattleUnit[] UnitMainData, BattleUnit[] UnitLeftData, BattleFacility[] FacilityData, BattleEnemy[] EnemyData, GameData GameData, double nowDifficulty) {
		return Arrays.asList(canClearStage(BattleEnemy.NORMAL_MODE, nowDifficulty),
				hasNotHited(BattleEnemy.NORMAL_MODE, nowDifficulty, FacilityData[0]),
				canClearStage(BattleEnemy.HARD_MODE, nowDifficulty),
				canNotDefeat(BattleEnemy.HARD_MODE, nowDifficulty, UnitMainData, UnitLeftData));
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
				Arrays.asList(DefaultEnemy.BLUE_SLIME, 0, 500),
				Arrays.asList(DefaultEnemy.BLUE_SLIME, 1, 1000),
				Arrays.asList(DefaultEnemy.BLUE_SLIME, 0, 6500),
				Arrays.asList(DefaultEnemy.BLUE_SLIME, 1, 7000),
				Arrays.asList(DefaultEnemy.BLUE_SLIME, 0, 12500),
				Arrays.asList(DefaultEnemy.BLUE_SLIME, 1, 13000),
				Arrays.asList(DefaultEnemy.GREEN_SLIME, 0, 25000),
				Arrays.asList(DefaultEnemy.GREEN_SLIME, 1, 25000),
				Arrays.asList(DefaultEnemy.RED_SLIME, 0, 30000),
				Arrays.asList(DefaultEnemy.RED_SLIME, 1, 30000),
				
				Arrays.asList(DefaultEnemy.BLUE_SLIME, 2, 32500),
				
				Arrays.asList(DefaultEnemy.YELLOW_SLIME, 0, 35000),
				Arrays.asList(DefaultEnemy.YELLOW_SLIME, 1, 35000),
				Arrays.asList(DefaultEnemy.GREEN_SLIME, 0, 40000),
				Arrays.asList(DefaultEnemy.GREEN_SLIME, 0, 45000),
				Arrays.asList(DefaultEnemy.RED_SLIME, 1, 50000),
				Arrays.asList(DefaultEnemy.RED_SLIME, 1, 55000),
				
				Arrays.asList(DefaultEnemy.BLUE_SLIME, 2, 57000),
				Arrays.asList(DefaultEnemy.BLUE_SLIME, 3, 57500),
				
				Arrays.asList(DefaultEnemy.HIGH_SLIME, 0, 60000),
				Arrays.asList(DefaultEnemy.HIGH_SLIME, 1, 60000),
				Arrays.asList(DefaultEnemy.YELLOW_SLIME, 0, 65000),
				Arrays.asList(DefaultEnemy.YELLOW_SLIME, 1, 65000),
				
				Arrays.asList(DefaultEnemy.HIGH_SLIME, 2, 67000),
				Arrays.asList(DefaultEnemy.HIGH_SLIME, 3, 67500),
				
				Arrays.asList(DefaultEnemy.YELLOW_SLIME, 0, 70000),
				Arrays.asList(DefaultEnemy.YELLOW_SLIME, 1, 70000),
				Arrays.asList(DefaultEnemy.YELLOW_SLIME, 0, 75000),
				Arrays.asList(DefaultEnemy.YELLOW_SLIME, 1, 75000)
				);
	}

	@Override
	public List<Integer> getDisplayOrder() {
		return Arrays.asList(DefaultEnemy.BLUE_SLIME, DefaultEnemy.RED_SLIME, DefaultEnemy.GREEN_SLIME, DefaultEnemy.YELLOW_SLIME, DefaultEnemy.HIGH_SLIME);
	}

	@Override
	public List<List<List<Integer>>> getRoute() {
		return Arrays.asList(
				//route0: 左城門から右城門へ
				Arrays.asList(
						Arrays.asList(-50, 28, 0, 0, 0),
						Arrays.asList(73, 0, 90, 0, 0),
						Arrays.asList(0, 441, 0, 0, 0),
						Arrays.asList(247, 0, 270, 0, 0),
						Arrays.asList(0, 324, 0, 0, 0),
						Arrays.asList(483, 0, 90, 0, 0),
						Arrays.asList(0, 441, 0, 0, 0),
						Arrays.asList(660, 0, 270, 0, 0),
						Arrays.asList(0, 206, 0, 0, 0),
						Arrays.asList(780, 0, 270, 0, 0),
						Arrays.asList(0, 20, 0, 0, 0)
						),
				//route1: 上城門から右城門へ
				Arrays.asList(
						Arrays.asList(247, -50, 90, 0, 0),
						Arrays.asList(0, 29, 0, 0, 0),
						Arrays.asList(485, 0, 90, 0, 0),
						Arrays.asList(0, 441, 0, 0, 0),
						Arrays.asList(660, 0, 270, 0, 0),
						Arrays.asList(0, 206, 0, 0, 0),
						Arrays.asList(780, 0, 270, 0, 0),
						Arrays.asList(0, 20, 0, 0, 0)
						),
				//route2: 裏道から右城門へ
				Arrays.asList(
						Arrays.asList(542, 590, 270, 0, 0),
						Arrays.asList(0, 441, 0, 300, 0),
						Arrays.asList(0, 0, 0, 0, 0),
						Arrays.asList(660, 0, 270, 0, 0),
						Arrays.asList(0, 206, 0, 0, 0),
						Arrays.asList(780, 0, 270, 0, 0),
						Arrays.asList(0, 20, 0, 0, 0)
						),
				//route3: 裏道から井戸へ
				Arrays.asList(
						Arrays.asList(542, 590, 270, 0, 0),
						Arrays.asList(0, 441, 0, 300, 0),
						Arrays.asList(0, 0, 0, 0, 0),
						Arrays.asList(660, 0, 270, 0, 0),
						Arrays.asList(0, 354, 0, 100, 0),
						Arrays.asList(0, 0, 0, 0, 0),
						Arrays.asList(749, 0, 45, 0, 0),
						Arrays.asList(808, 0, 0, 0, 0),
						Arrays.asList(926, 0, 0, 100, 0),
						Arrays.asList(0, 0, 270, 0, 0),
						Arrays.asList(0, 383, 180, 0, 100),
						Arrays.asList(896, 0, 270, 0, 300),
						Arrays.asList(0, 206, 0, 300, 300),
						Arrays.asList(0, 0, 270, 0, 0),
						Arrays.asList(0, 20, 180, 0, 0)
						)
				);
	}
}