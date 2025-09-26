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

public class No0000Stage1 extends StageData{
	@Override
	public String getName() {
		return "stage 1";
	}

	@Override
	public String getImageName() {
		return "image/field/stage 1.png";
	}

	@Override
	public List<Integer> getFacility() {
		return Arrays.asList(DefaultStage.CASTLE, DefaultStage.GATE, DefaultStage.GATE, DefaultStage.GATE);
	}

	@Override
	public List<Boolean> getFacilityDirection() {
		return Arrays.asList(true, false, false, true);
	}

	@Override
	public List<Point> getFacilityPoint() {
		return Arrays.asList(new Point(550, 50), new Point(308, 320), new Point(662, 425), new Point(910, 170));
	}

	@Override
	public List<List<List<Double>>> getPlacementPoint() {
		double size = 29.5;
		double centerX = 483;
		double centerY = 265;
		return Arrays.asList(
				Arrays.asList(Arrays.asList(centerX - size * 3, centerY + size * 3),
						Arrays.asList(centerX + size, centerY + size * 3),
						Arrays.asList(centerX + size * 5, centerY + size * 3),
						Arrays.asList(centerX + size * 9, centerY + size * 3),
						
						Arrays.asList(centerX - size * 3, centerY + size * 7),
						Arrays.asList(centerX + size, centerY + size * 7),
						Arrays.asList(centerX + size * 5, centerY + size * 7),
						Arrays.asList(centerX + size * 9, centerY + size * 7),
						
						Arrays.asList(centerX + size * 11, centerY + size),
						Arrays.asList(centerX + size * 15, centerY + size)),
				Arrays.asList(
						Arrays.asList(centerX - size * 3, (double) centerY),
						Arrays.asList(centerX + size, (double) centerY),
						Arrays.asList(centerX + size * 5, (double) centerY),
						
						Arrays.asList(centerX + size * 7, centerY + size * 3),
						Arrays.asList(centerX + size * 11, centerY + size * 3),
						Arrays.asList(centerX + size * 15, centerY + size * 3),
						
						Arrays.asList(centerX - size * 5, centerY + size * 7),
						Arrays.asList(centerX + size * 11, centerY + size * 7),
						
						Arrays.asList(centerX + size * 7, centerY - size * 2),
						Arrays.asList(centerX + size * 11, centerY - size * 2)),
				Arrays.asList(
						Arrays.asList(centerX + size * 7, centerY - size * 7),
						Arrays.asList(centerX + size * 11, centerY - size * 7),
						Arrays.asList(centerX + size * 15, centerY - size * 7),
						
						Arrays.asList(centerX + size * 7, centerY - size * 5),
						Arrays.asList(centerX + size * 11, centerY - size * 5),
						Arrays.asList(centerX + size * 15, centerY - size * 5)));
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
				Arrays.asList(DefaultEnemy.RED_SLIME, 3, 2000)
				);
	}

	@Override
	public List<Integer> getDisplayOrder() {
		return Arrays.asList(DefaultEnemy.BLUE_SLIME, DefaultEnemy.RED_SLIME);
	}
	
	@Override
	public List<List<List<Integer>>> getRoute() {
		return Arrays.asList(
				//route0: 通常ルート1
				Arrays.asList(
						Arrays.asList(30, 500, 270, 0, 0),
						Arrays.asList(0, 310, 0, 100, 0),
						Arrays.asList(0, 0, 0, 0, 0),
						Arrays.asList(600, 0, 90, 0, 0),
						Arrays.asList(0, 440, 0, 0, 0),
						Arrays.asList(725, 0, 270, 0, 0),
						Arrays.asList(0, 260, 0, 0, 0),
						Arrays.asList(920, 0, 270, 0, 0),
						Arrays.asList(0, 90, 180, 0, 0)
						),
				//route1: 通常ルート2
				Arrays.asList(
						Arrays.asList(30, 510, 270, 0, 0),
						Arrays.asList(0, 320, 0, 100, 0),
						Arrays.asList(0, 0, 0, 0, 0),
						Arrays.asList(370, 0, 90, 0, 0),
						Arrays.asList(0, 450, 0, 0, 0),
						Arrays.asList(725, 0, 270, 0, 0),
						Arrays.asList(0, 260, 0, 0, 0),
						Arrays.asList(910, 0, 270, 0, 0),
						Arrays.asList(0, 25, 180, 0, 0)
						),
				//route2: N字飛行
				Arrays.asList(
						Arrays.asList(10, 510, 270, 0, 0),
						Arrays.asList(0, 290, 315, 0, 100),
						Arrays.asList(100, 0, 0, 100, 100),
						Arrays.asList(0, 0, 290, 0, 0),
						Arrays.asList(125, 0, 315, 0, 0),
						Arrays.asList(175, 0, 340, 0, 0),
						Arrays.asList(225, 0, 0, 0, 0),
						Arrays.asList(275, 0, 20, 0, 0),
						Arrays.asList(325, 0, 45, 0, 0),
						Arrays.asList(475, 0, 20, 0, 0),
						Arrays.asList(595, 0, 0, 0, 0),
						Arrays.asList(650, 0, 340, 0, 0),
						Arrays.asList(700, 0, 315, 0, 0),
						Arrays.asList(750, 0, 290, 0, 0),
						Arrays.asList(775, 0, 270, 0, 0),
						Arrays.asList(0, 70, 245, 0, 0),
						Arrays.asList(0, 50, 225, 0, 0),
						Arrays.asList(0, 30, 200, 0, 0),
						Arrays.asList(0, 10, 180, 0, 0),
						Arrays.asList(635, 0, 155, 0, 0),
						Arrays.asList(610, 0, 135, 0, 0)
						),
				//route3: 右下から直行
				Arrays.asList(
						Arrays.asList(900, 510, 270, 0, 0),
						Arrays.asList(0, 440, 0, 200, 0),
						Arrays.asList(0, 0, 225, 0, 0),
						Arrays.asList(660, 0, 0, 200, 0),
						Arrays.asList(0, 0, 225, 0, 0),
						Arrays.asList(550, 0, 270, 0, 0)
						)
				);
	}
}

/*
テスト用enemy
		return Arrays.asList(
				Arrays.asList(0, 0, 1000),
				Arrays.asList(0, 1, 1000),
				Arrays.asList(1, 0, 2000),
				Arrays.asList(1, 1, 2000)
				);
テスト用route
		return Arrays.asList(
				Arrays.asList(
						Arrays.asList(10, 10, 90, 0, 0),
						Arrays.asList(0, 100, 0, 0, 0),
						Arrays.asList(100, 0, 0, 100, 0),
						Arrays.asList(0, 0, 0, 0, 0),
						Arrays.asList(200, 0, 0, 200, 100),
						Arrays.asList(0, 0, 270, 0, 0),
						Arrays.asList(0, 10, 180, 0, 100),
						Arrays.asList(100, 0, 90, 0, 0),
						Arrays.asList(0, 100, 315, 0, 0)),
				Arrays.asList(
						Arrays.asList(10, 10, 0, 0, 0),
						Arrays.asList(100, 0, 90, 0, 0),
						Arrays.asList(0, 100, 0, 0, 100),
						Arrays.asList(200, 0, 90, 0, 100),
						Arrays.asList(0, 200, 90, 0, 0),
						Arrays.asList(0, 300, 225, 0, 0)
						)
				);
*/