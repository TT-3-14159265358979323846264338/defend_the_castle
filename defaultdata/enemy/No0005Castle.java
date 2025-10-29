package defaultdata.enemy;

import java.util.Arrays;
import java.util.List;

import defaultdata.DefaultAtackPattern;
import defaultdata.DefaultEnemy;

public class No0005Castle extends EnemyData{
	@Override
	public String getName() {
		return "敵本丸";
	}

	@Override
	public String getExplanation() {
		return "敵の防衛拠点。高い耐久力を有し、周囲の敵に射撃を行う。";
	}
	
	@Override
	public String getImageName() {
		return "image/facility/castle.png";
	}

	@Override
	public List<String> getActionImageName() {
		return Arrays.asList("image/facility/castle.png");
	}

	@Override
	public String getBulletImageName() {
		return "image/weapon/No0001 bow/bow bullet.png";
	}

	@Override
	public List<String> getHitImageName() {
		return Arrays.asList("image/weapon/No0001 bow/bow hit 1.png",
				"image/weapon/No0001 bow/bow hit 2.png",
				"image/weapon/No0001 bow/bow hit 3.png");
	}

	@Override
	public int getMove() {
		return DefaultEnemy.NO_MOVE;
	}

	@Override
	public int getType() {
		return DefaultEnemy.BOSS;
	}

	@Override
	public List<Integer> getElement() {
		return Arrays.asList(DefaultEnemy.PIERCE);
	}

	@Override
	public int getAtackPattern() {
		return DefaultAtackPattern.NEAR;
	}

	@Override
	public List<Integer> getWeaponStatus() {
		return Arrays.asList(50, 250, 500, 5);
	}

	@Override
	public List<Integer> getUnitStatus() {
		return Arrays.asList(5000, 5000, 5, 0, 0, 5);
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