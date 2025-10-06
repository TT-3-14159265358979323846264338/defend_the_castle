package defaultdata.enemy;

import java.util.Arrays;
import java.util.List;

import defaultdata.DefaultAtackPattern;
import defaultdata.DefaultEnemy;

public class No0004HighSlime extends EnemyData{
	@Override
	public String getName() {
		return "ハイスライム";
	}

	@Override
	public String getExplanation() {
		return "通常よりも能力の高いスライム。物理武器が有効。";
	}
	
	@Override
	public String getImageName() {
		return "image/enemy/No0004 high slime/high slime.png";
	}

	@Override
	public List<String> getActionImageName() {
		return Arrays.asList("image/enemy/No0004 high slime/high slime 0.png",
				"image/enemy/No0004 high slime/high slime 1.png",
				"image/enemy/No0004 high slime/high slime 2.png",
				"image/enemy/No0004 high slime/high slime 3.png",
				"image/enemy/No0004 high slime/high slime 4.png",
				"image/enemy/No0004 high slime/high slime 5.png");
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
		return Arrays.asList(30, 40, 1000, 1);
	}

	@Override
	public List<Integer> getUnitStatus() {
		return Arrays.asList(1250, 1250, 12, 10, 100, 1);
	}

	@Override
	public List<Integer> getCutStatus() {
		return Arrays.asList(0, 0, 0, 0, 20, 20, 20, 20, 20, 20, 20, 0);
	}
	
	@Override
	public List<List<Double>> getBuff(){
		return Arrays.asList();
	}
}