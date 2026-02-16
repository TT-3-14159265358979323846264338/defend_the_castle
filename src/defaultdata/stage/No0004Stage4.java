package defaultdata.stage;

import java.awt.Point;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

import defaultdata.DefaultEnemy;
import defaultdata.DefaultStage;
import defendthecastle.battle.Battle;
import defendthecastle.battle.BattleEnemy;
import defendthecastle.battle.BattleFacility;
import defendthecastle.battle.BattleUnit;
import defendthecastle.battle.GameData;
import savedata.SaveGameProgress;

public class No0004Stage4 extends StageData {
	@Override
	public String getName() {
		return "stage 4";
	}

	@Override
	public boolean canActivate(SaveGameProgress SaveGameProgress) {
		return hasClearedMerit(SaveGameProgress, 2, 2, 1);
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
	public List<List<Boolean>> canUsePlacement(Battle Battle, BattleEnemy[] EnemyData) {
		return Arrays.asList(
				Arrays.asList(true,
						true,
						true,
						true,
						
						true,
						true),
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
						true),
				Arrays.asList(true,
						true,
						
						true,
						true
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
				"総覚醒回数が5回以上(normal)",
				"ステージをクリアする(hard)",
				"ユニットが一度も倒されない(hard)");
	}

	@Override
	public List<Boolean> canClearMerit(BattleUnit[] UnitMainData, BattleUnit[] UnitLeftData, BattleFacility[] FacilityData, BattleEnemy[] EnemyData, GameData GameData, double nowDifficulty) {
		return Arrays.asList(canClearStage(BattleEnemy.NORMAL_MODE, nowDifficulty),
				existsOverAwakening(BattleEnemy.NORMAL_MODE, nowDifficulty, 5, UnitMainData),
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
				Arrays.asList(DefaultEnemy.HIGH_SLIME, 2, 500, 0, 0),
				Arrays.asList(DefaultEnemy.HIGH_SLIME, 6, 500, 0, 0),
				
				Arrays.asList(DefaultEnemy.BLUE_SLIME, 0, 1000, 0, 0),
				Arrays.asList(DefaultEnemy.BLUE_SLIME, 1, 1500, 0, 0),
				Arrays.asList(DefaultEnemy.BLUE_SLIME, 4, 6000, 0, 0),
				Arrays.asList(DefaultEnemy.BLUE_SLIME, 5, 6500, 0, 0),
				Arrays.asList(DefaultEnemy.BLUE_SLIME, 0, 11000, 0, 0),
				Arrays.asList(DefaultEnemy.BLUE_SLIME, 1, 11500, 0, 0),
				Arrays.asList(DefaultEnemy.BLUE_SLIME, 4, 16000, 0, 0),
				Arrays.asList(DefaultEnemy.BLUE_SLIME, 5, 16500, 0, 0),
				
				Arrays.asList(DefaultEnemy.GREEN_SLIME, 0, 35000, 0, 0),
				Arrays.asList(DefaultEnemy.GREEN_SLIME, 4, 35000, 0, 0),
				Arrays.asList(DefaultEnemy.GREEN_SLIME, 1, 40000, 0, 0),
				Arrays.asList(DefaultEnemy.GREEN_SLIME, 5, 40000, 0, 0),
				Arrays.asList(DefaultEnemy.YELLOW_SLIME, 0, 45000, 0, 0),
				Arrays.asList(DefaultEnemy.YELLOW_SLIME, 4, 45000, 0, 0),
				
				Arrays.asList(DefaultEnemy.HIGH_SLIME, 2, 55000, 0, 0),
				Arrays.asList(DefaultEnemy.HIGH_SLIME, 6, 55000, 0, 0),
				
				Arrays.asList(DefaultEnemy.RED_SLIME, 0, 55000, 0, 0),
				Arrays.asList(DefaultEnemy.RED_SLIME, 4, 55000, 0, 0),
				Arrays.asList(DefaultEnemy.YELLOW_SLIME, 0, 65000, 0, 0),
				Arrays.asList(DefaultEnemy.YELLOW_SLIME, 4, 65000, 0, 0),
				Arrays.asList(DefaultEnemy.YELLOW_SLIME, 0, 70000, 0, 0),
				Arrays.asList(DefaultEnemy.YELLOW_SLIME, 4, 70000, 0, 0),
				Arrays.asList(DefaultEnemy.YELLOW_SLIME, 0, 75000, 0, 0),
				Arrays.asList(DefaultEnemy.YELLOW_SLIME, 4, 75000, 0, 0),
				Arrays.asList(DefaultEnemy.YELLOW_SLIME, 0, 80000, 0, 0),
				Arrays.asList(DefaultEnemy.YELLOW_SLIME, 4, 80000, 0, 0),
				Arrays.asList(DefaultEnemy.YELLOW_SLIME, 0, 85000, 0, 0),
				Arrays.asList(DefaultEnemy.YELLOW_SLIME, 4, 85000, 0, 0),
				
				Arrays.asList(DefaultEnemy.HIGH_SLIME, 1, 90000, 0, 0),
				Arrays.asList(DefaultEnemy.HIGH_SLIME, 2, 90000, 0, 0),
				Arrays.asList(DefaultEnemy.HIGH_SLIME, 6, 90000, 0, 0)
				);
	}

	@Override
	public List<Integer> getDisplayOrder() {
		return Arrays.asList(DefaultEnemy.BLUE_SLIME, DefaultEnemy.RED_SLIME, DefaultEnemy.GREEN_SLIME, DefaultEnemy.YELLOW_SLIME, DefaultEnemy.HIGH_SLIME);
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