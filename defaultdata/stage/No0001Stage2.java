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

public class No0001Stage2 extends StageData {
	@Override
	public String getName() {
		return "stage 2";
	}

	@Override
	public String getImageName() {
		return "image/field/stage 2.png";
	}

	@Override
	public List<Integer> getFacility() {
		return Arrays.asList(DefaultStage.CASTLE, DefaultStage.GATE, DefaultStage.GATE, DefaultStage.GATE);
	}

	@Override
	public List<Boolean> getFacilityDirection() {
		return Arrays.asList(true, true, false, false);
	}

	@Override
	public List<Point> getFacilityPoint() {
		return Arrays.asList(new Point(457, 70), new Point(457, 260), new Point(192, 76), new Point(723, 76));
	}

	@Override
	public List<List<List<Double>>> getPlacementPoint() {
		double size = 29.5;
		double centerX = 483;
		double centerY = 265;
		return Arrays.asList(
				Arrays.asList(Arrays.asList(centerX - size * 7, centerY - size * 5),
						Arrays.asList(centerX - size * 5, centerY - size * 5),
						Arrays.asList(centerX + size * 5, centerY - size * 5),
						Arrays.asList(centerX + size * 7, centerY - size * 5),
						
						Arrays.asList(centerX - size, centerY - size),
						Arrays.asList(centerX + size, centerY - size)),
				Arrays.asList(Arrays.asList(centerX - size * 9, centerY - size * 8),
						Arrays.asList(centerX + size * 9, centerY - size * 8),
						
						Arrays.asList(centerX - size * 6, centerY - size * 7),
						Arrays.asList(centerX - size * 4, centerY - size * 7),
						Arrays.asList(centerX + size * 6, centerY - size * 7),
						Arrays.asList(centerX + size * 4, centerY - size * 7),
						
						Arrays.asList(centerX - size * 9, centerY - size * 3),
						Arrays.asList(centerX - size * 5, centerY - size * 3),
						Arrays.asList(centerX - size * 3, centerY - size * 3),
						Arrays.asList(centerX + size * 9, centerY - size * 3),
						Arrays.asList(centerX + size * 5, centerY - size * 3),
						Arrays.asList(centerX + size * 3, centerY - size * 3),
						
						Arrays.asList(centerX - size * 9, centerY - size),
						Arrays.asList(centerX + size * 9, centerY - size),
						
						Arrays.asList(centerX - size * 7, centerY + size),
						Arrays.asList(centerX - size * 5, centerY + size),
						Arrays.asList(centerX - size * 3, centerY + size),
						Arrays.asList(centerX + size * 7, centerY + size),
						Arrays.asList(centerX + size * 5, centerY + size),
						Arrays.asList(centerX + size * 3, centerY + size)),
				Arrays.asList(Arrays.asList(centerX - size * 3, centerY - size * 5),
						Arrays.asList(centerX + size * 3, centerY - size * 5),
						
						Arrays.asList(centerX - size, centerY - size * 3),
						Arrays.asList(centerX + size, centerY - size * 3)
						));
	}
	
	@Override
	public int getCost() {
		return 50;
	}
	
	@Override
	public List<Integer> getMorale(){
		return Arrays.asList(0, 10);
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
				"ユニットが一度も倒されずクリアする(normal)",
				"ステージをクリアする(hard)",
				"ユニットも設備も破壊されずクリアする(hard)");
	}

	@Override
	public List<Boolean> canClearMerit(BattleUnit[] UnitMainData, BattleUnit[] UnitLeftData, BattleFacility[] FacilityData, BattleEnemy[] EnemyData, GameData GameData, double difficultyCorrection) {
		return Arrays.asList(canClearStage(BattleEnemy.NORMAL_MODE, difficultyCorrection),
				canNotDefeat(BattleEnemy.NORMAL_MODE, difficultyCorrection, UnitMainData, UnitLeftData),
				canClearStage(BattleEnemy.HARD_MODE, difficultyCorrection),
				canNotDefeat(BattleEnemy.HARD_MODE, difficultyCorrection, UnitMainData, UnitLeftData, FacilityData));
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
				Arrays.asList(DefaultEnemy.BLUE_SLIME, 0, 1000),
				Arrays.asList(DefaultEnemy.RED_SLIME, 1, 1000),
				Arrays.asList(DefaultEnemy.BLUE_SLIME, 2, 2000),
				Arrays.asList(DefaultEnemy.RED_SLIME, 3, 2000),
				Arrays.asList(DefaultEnemy.BLUE_SLIME, 4, 3000),
				Arrays.asList(DefaultEnemy.RED_SLIME, 5, 3000),
				Arrays.asList(DefaultEnemy.BLUE_SLIME, 6, 4000),
				Arrays.asList(DefaultEnemy.RED_SLIME, 7, 4000)
				);
	}

	@Override
	public List<Integer> getDisplayOrder() {
		return Arrays.asList(DefaultEnemy.BLUE_SLIME, DefaultEnemy.RED_SLIME);
	}

	@Override
	public List<List<List<Integer>>> getRoute() {
		return Arrays.asList(
				//route0: 左下から中央城門へ1
				Arrays.asList(
						Arrays.asList(-50, 400, 0, 0, 0),
						Arrays.asList(460, 0, 270, 0, 0)
						),
				//route1: 左下から中央城門へ2
				Arrays.asList(
						Arrays.asList(-50, 420, 0, 0, 0),
						Arrays.asList(450, 0, 270, 0, 0)
						),
				//route2: 左下から左城門へ1
				Arrays.asList(
						Arrays.asList(-50, 400, 0, 0, 0),
						Arrays.asList(80, 0, 270, 0, 0),
						Arrays.asList(0, 70, 0, 0, 0)
						),
				//route3: 左下から左城門へ2
				Arrays.asList(
						Arrays.asList(-50, 420, 0, 0, 0),
						Arrays.asList(60, 0, 270, 0, 0),
						Arrays.asList(0, 80, 0, 0, 0)
						),
				//route4: 右下から中央城門へ1
				Arrays.asList(
						Arrays.asList(835, 510, 270, 0, 0),
						Arrays.asList(0, 400, 180, 0, 0),
						Arrays.asList(450, 0, 270, 0, 0)
						),
				//route5: 右下から中央城門へ2
				Arrays.asList(
						Arrays.asList(855, 510, 270, 0, 0),
						Arrays.asList(0, 420, 180, 0, 0),
						Arrays.asList(460, 0, 270, 0, 0)
						),
				//route6: 右下から右城門へ1
				Arrays.asList(
						Arrays.asList(835, 510, 270, 0, 0),
						Arrays.asList(0, 70, 180, 0, 0)
						),
				//route7: 右下から右城門へ2
				Arrays.asList(
						Arrays.asList(855, 510, 270, 0, 0),
						Arrays.asList(0, 80, 180, 0, 0)
						)
				);
	}
}