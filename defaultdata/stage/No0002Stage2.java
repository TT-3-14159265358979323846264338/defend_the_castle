package defaultdata.stage;

import java.awt.Point;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

import battle.Battle;
import battle.BattleEnemy;
import battle.BattleFacility;
import battle.BattleUnit;
import battle.GameData;
import defaultdata.DefaultEnemy;
import defaultdata.DefaultStage;

public class No0002Stage2 extends StageData{
	@Override
	public String getName() {
		return "stage 2";
	}

	@Override
	public String getImageName() {
		return "image/field/stage 4.png";
	}

	@Override
	public List<Integer> getFacility() {
		return Arrays.asList(DefaultStage.STRONGHOLD);
	}

	@Override
	public List<Boolean> getFacilityDirection() {
		return Arrays.asList(true);
	}

	@Override
	public List<Point> getFacilityPoint() {
		return Arrays.asList(new Point(866, 383));
	}

	@Override
	public List<List<List<Double>>> getPlacementPoint() {
		double size = 29.5;
		double centerX = 483;
		double centerY = 265;
		return Arrays.asList(
				Arrays.asList(Arrays.asList(centerX - size * 12, centerY - size * 7),
						Arrays.asList(centerX - size * 9, centerY - size * 7),
						Arrays.asList(centerX - size * 6, centerY - size * 7),
						Arrays.asList(centerX - size * 3, centerY - size * 6),
						Arrays.asList(centerX - size, centerY - size * 4),
						Arrays.asList(centerX + size, centerY - size * 2),
						Arrays.asList(centerX + size, centerY + size),
						Arrays.asList(centerX - size * 15, centerY + size * 3),
						Arrays.asList(centerX - size * 13, centerY + size * 5),
						Arrays.asList(centerX - size * 11, centerY + size * 7),
						Arrays.asList(centerX - size * 8, centerY + size * 7),
						Arrays.asList(centerX - size * 6, centerY + size * 5),
						Arrays.asList(centerX - size * 4, centerY + size * 3),
						Arrays.asList(centerX - size, centerY + size * 3),
						Arrays.asList(centerX + size * 14, centerY + size * 3),
						Arrays.asList(centerX + size * 12, centerY + size),
						Arrays.asList(centerX + size * 10, centerY - size),
						Arrays.asList(centerX + size * 7, centerY - size),
						Arrays.asList(centerX + size * 5, centerY + size),
						Arrays.asList(centerX + size * 3, centerY + size * 3)
						),
				Arrays.asList(Arrays.asList(centerX + size * 2, centerY - size * 5),
						Arrays.asList(centerX - size * 4, centerY + size),
						Arrays.asList(centerX + size * 7, centerY - size * 3),
						Arrays.asList(centerX + size, centerY + size * 5)
						),
				Arrays.asList(
						));
	}

	@Override
	public List<List<Boolean>> canUsePlacement(Battle Battle, BattleEnemy[] EnemyData) {
		return Arrays.asList(
				Arrays.asList(true,
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
				Arrays.asList(true,
						true,
						true,
						true
						),
				Arrays.asList(
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
		return "本陣を制圧される";
	}

	@Override
	public boolean existsGameOver(BattleUnit[] UnitMainData, BattleUnit[] UnitLeftData, BattleFacility[] FacilityData, BattleEnemy[] EnemyData, GameData GameData) {
		return canAllBreak(FacilityData[0]);
	}

	@Override
	public List<String> getMerit() {
		return Arrays.asList("ステージをクリアする(normal)",
				"本陣が1度も攻撃を受けない(normal)",
				"ステージをクリアする(hard)",
				"本陣が1度も攻撃を受けない(hard)",
				"ユニットが一度も倒されない(hard)");
	}

	@Override
	public List<Boolean> canClearMerit(BattleUnit[] UnitMainData, BattleUnit[] UnitLeftData, BattleFacility[] FacilityData, BattleEnemy[] EnemyData, GameData GameData, double nowDifficulty) {
		return Arrays.asList(canClearStage(BattleEnemy.NORMAL_MODE, nowDifficulty),
				hasNotHited(BattleEnemy.NORMAL_MODE, nowDifficulty, FacilityData[0]),
				canClearStage(BattleEnemy.HARD_MODE, nowDifficulty),
				hasNotHited(BattleEnemy.HARD_MODE, nowDifficulty, FacilityData[0]),
				canNotDefeat(BattleEnemy.HARD_MODE, nowDifficulty, UnitMainData, UnitLeftData));
	}

	@Override
	public List<String> getReward() {
		return Arrays.asList("メダル100",
				"メダル200",
				"メダル300",
				"メダル300",
				"メダル500");
	}

	@Override
	protected List<Method> giveReward() {
		try {
			return Arrays.asList(getClass().getMethod("give100Medal"),
					getClass().getMethod("give200Medal"),
					getClass().getMethod("give300Medal"),
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
				Arrays.asList(DefaultEnemy.BLUE_SLIME, 0, 1000, 0, 0),
				Arrays.asList(DefaultEnemy.BLUE_SLIME, 0, 1500, 0, 0),
				Arrays.asList(DefaultEnemy.BLUE_SLIME, 0, 5000, 0, 0),
				Arrays.asList(DefaultEnemy.BLUE_SLIME, 0, 5500, 0, 0),
				Arrays.asList(DefaultEnemy.BLUE_SLIME, 1, 13000, 0, 0),
				Arrays.asList(DefaultEnemy.BLUE_SLIME, 1, 13500, 0, 0),
				Arrays.asList(DefaultEnemy.BLUE_SLIME, 1, 18000, 0, 0),
				Arrays.asList(DefaultEnemy.BLUE_SLIME, 1, 18500, 0, 0),
				Arrays.asList(DefaultEnemy.RED_SLIME, 0, 25000, 0, 0),
				Arrays.asList(DefaultEnemy.RED_SLIME, 1, 25000, 0, 0),
				Arrays.asList(DefaultEnemy.RED_SLIME, 0, 28000, 0, 0),
				Arrays.asList(DefaultEnemy.RED_SLIME, 1, 28000, 0, 0),
				Arrays.asList(DefaultEnemy.GREEN_SLIME, 0, 33000, 0, 0),
				Arrays.asList(DefaultEnemy.GREEN_SLIME, 0, 34000, 0, 0),
				Arrays.asList(DefaultEnemy.YELLOW_SLIME, 1, 33000, 0, 0),
				Arrays.asList(DefaultEnemy.YELLOW_SLIME, 1, 34000, 0, 0),
				
				Arrays.asList(DefaultEnemy.RED_SLIME, 0, 45000, 0, 0),
				Arrays.asList(DefaultEnemy.RED_SLIME, 1, 46000, 0, 0),
				Arrays.asList(DefaultEnemy.RED_SLIME, 0, 47000, 0, 0),
				Arrays.asList(DefaultEnemy.RED_SLIME, 1, 48000, 0, 0),
				Arrays.asList(DefaultEnemy.YELLOW_SLIME, 0, 51000, 0, 0),
				Arrays.asList(DefaultEnemy.YELLOW_SLIME, 1, 52000, 0, 0),
				Arrays.asList(DefaultEnemy.GREEN_SLIME, 0, 55000, 0, 0),
				Arrays.asList(DefaultEnemy.GREEN_SLIME, 1, 56000, 0, 0),
				Arrays.asList(DefaultEnemy.HIGH_SLIME, 0, 61000, 0, 0),
				Arrays.asList(DefaultEnemy.HIGH_SLIME, 1, 61000, 0, 0),
				Arrays.asList(DefaultEnemy.HIGH_SLIME, 0, 65000, 0, 0),
				Arrays.asList(DefaultEnemy.HIGH_SLIME, 1, 65000, 0, 0)
				);
	}

	@Override
	public List<Integer> getDisplayOrder() {
		return Arrays.asList(DefaultEnemy.BLUE_SLIME, DefaultEnemy.RED_SLIME, DefaultEnemy.GREEN_SLIME, DefaultEnemy.YELLOW_SLIME, DefaultEnemy.HIGH_SLIME);
	}
	
	@Override
	public List<List<List<Integer>>> getRoute() {
		return Arrays.asList(
				//route0: 左上から
				Arrays.asList(
						Arrays.asList(70, -50, 90, 0, 0),
						Arrays.asList(0, 29, 0, 0, 0),
						Arrays.asList(335, 0, 45, 0, 0),
						Arrays.asList(483, 0, 90, 0, 0),
						Arrays.asList(0, 324, 0, 0, 0),
						Arrays.asList(542, 0, 315, 0, 0),
						Arrays.asList(660, 0, 0, 0, 0),
						Arrays.asList(748, 0, 45, 0, 0),
						Arrays.asList(866, 0, 90, 0, 0),
						Arrays.asList(0, 383, 00, 0, 0)
						),
				//route1: 左下から
				Arrays.asList(
						Arrays.asList(-50, 324, 0, 0, 0),
						Arrays.asList(11, 0, 45, 0, 0),
						Arrays.asList(129, 0, 0, 0, 0),
						Arrays.asList(218, 0, 315, 0, 0),
						Arrays.asList(336, 0, 0, 0, 0),
						Arrays.asList(542, 0, 315, 0, 0),
						Arrays.asList(660, 0, 0, 0, 0),
						Arrays.asList(748, 0, 45, 0, 0),
						Arrays.asList(866, 0, 90, 0, 0),
						Arrays.asList(0, 383, 00, 0, 0)
						),
				//route2: 池から
				Arrays.asList(
						Arrays.asList(749, 147, 90, 0, 0),
						Arrays.asList(0, 177, 0, 300, 0),
						Arrays.asList(0, 0, 90, 0, 0),
						Arrays.asList(0, 206, 90, 0, 0),
						Arrays.asList(748, 0, 45, 0, 0),
						Arrays.asList(866, 0, 90, 0, 0),
						Arrays.asList(0, 383, 0, 0, 0)
						)
				);
	}
}