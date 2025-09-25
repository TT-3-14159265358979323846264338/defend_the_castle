package defaultdata.enemy;

import java.util.Arrays;
import java.util.List;

import defaultdata.DefaultAtackPattern;
import defaultdata.DefaultEnemy;

public class No0001RedSlime extends EnemyData{
	@Override
	public String getName() {
		return "レッドスライム";
	}

	@Override
	public String getExplanation() {
		return "ブルースライムよりも少し攻撃力の高い敵。";
	}
	
	@Override
	public String getImageName() {
		return "image/enemy/red slime.png";
	}

	@Override
	public List<String> getActionImageName() {
		return Arrays.asList("image/enemy/red slime 0.png",
				"image/enemy/red slime 1.png",
				"image/enemy/red slime 2.png",
				"image/enemy/red slime 3.png",
				"image/enemy/red slime 4.png",
				"image/enemy/red slime 5.png");
	}

	@Override
	public String getBulletImageName() {
		return null;
	}

	@Override
	public List<String> getHitImageName() {
		return Arrays.asList();
	}

	@Override
	public int getMove() {
		return DefaultEnemy.GROUND;
	}

	@Override
	public int getType() {
		return DefaultEnemy.NORMAL;
	}

	@Override
	public List<Integer> getElement() {
		return Arrays.asList(DefaultEnemy.STRIKE);
	}

	@Override
	public int getAtackPattern() {
		return DefaultAtackPattern.NEAR;
	}

	@Override
	public List<Integer> getWeaponStatus() {
		return Arrays.asList(5000, 30, 1000, 1);
	}

	@Override
	public List<Integer> getUnitStatus() {
		return Arrays.asList(1000, 1000, 10, 0, 100, 1);
	}

	@Override
	public List<Integer> getCutStatus() {
		return Arrays.asList(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0);
	}
	
	@Override
	public List<List<Double>> getBuff(){
		return Arrays.asList();
	}
}
