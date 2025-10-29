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

public class No0005Stage5 extends StageData {
	@Override
	public String getName() {
		return "stage 5";
	}

	@Override
	public String getImageName() {
		return "image/field/stage 5.png";
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
	public List<List<Boolean>> canUsePlacement(Battle Battle, BattleEnemy[] EnemyData) {
		boolean hasBrokenFrontGate = canAllDefeat(EnemyData[1]);
		boolean hasBrokenSubGate = canAllDefeat(EnemyData[2]);
		boolean hasBrokenUpperGate = canAllDefeat(EnemyData[3]);
		boolean hasBrokenLowerGate = canAllDefeat(EnemyData[4]);
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
	public boolean canClear(BattleUnit[] UnitMainData, BattleUnit[] UnitLeftData, BattleFacility[] FacilityData, BattleEnemy[] EnemyData, GameData GameData) {
		return canAllDefeat(EnemyData[0]);
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
				"ユニットが一度も倒されない(hard)");
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
				Arrays.asList(DefaultEnemy.CASTLE, 0, 0),
				Arrays.asList(DefaultEnemy.FRONT_GATE, 4, 0),
				Arrays.asList(DefaultEnemy.SIDE_GATE, 5, 0),
				Arrays.asList(DefaultEnemy.SIDE_GATE, 6, 0),
				Arrays.asList(DefaultEnemy.SIDE_GATE, 7, 0),
				Arrays.asList(DefaultEnemy.FRONT_GATE, 8, 0),
				
				Arrays.asList(DefaultEnemy.BLUE_SLIME, 1, 500)
				);
	}

	@Override
	public List<Integer> getDisplayOrder() {
		return Arrays.asList(DefaultEnemy.BLUE_SLIME, DefaultEnemy.RED_SLIME, DefaultEnemy.GREEN_SLIME, DefaultEnemy.YELLOW_SLIME, DefaultEnemy.HIGH_SLIME);
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