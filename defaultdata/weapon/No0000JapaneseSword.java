package defaultdata.weapon;

import java.util.Arrays;
import java.util.List;

import defaultdata.DefaultAtackPattern;
import defaultdata.DefaultUnit;

public class No0000JapaneseSword extends WeaponData{
	@Override
	public String getName() {
		return "日本刀";
	}

	@Override
	public String getExplanation() {
		return "一般的な近接武器。1体をブロックし、攻撃できる。";
	}
	
	@Override
	public String getImageName() {
		return "image/weapon/No0000 Japanese sword/Japanese sword.png";
	}

	@Override
	public List<String> getRightActionImageName() {
		return Arrays.asList("image/weapon/No0000 Japanese sword/Japanese sword right 0.png",
				"image/weapon/No0000 Japanese sword/Japanese sword right 1.png",
				"image/weapon/No0000 Japanese sword/Japanese sword right 2.png",
				"image/weapon/No0000 Japanese sword/Japanese sword right 3.png",
				"image/weapon/No0000 Japanese sword/Japanese sword right 4.png",
				"image/weapon/No0000 Japanese sword/Japanese sword right 5.png");
	}

	@Override
	public List<String> getLeftActionImageName() {
		return Arrays.asList("image/weapon/No0000 Japanese sword/Japanese sword left 0.png",
				"image/weapon/No0000 Japanese sword/Japanese sword left 1.png",
				"image/weapon/No0000 Japanese sword/Japanese sword left 2.png",
				"image/weapon/No0000 Japanese sword/Japanese sword left 3.png",
				"image/weapon/No0000 Japanese sword/Japanese sword left 4.png",
				"image/weapon/No0000 Japanese sword/Japanese sword left 5.png");
	}

	@Override
	public String getBulletImageName() {
		return null;
	}

	@Override
	public List<String> getHitImageName() {
		return Arrays.asList("image/weapon/No0000 Japanese sword/Japanese sword hit 1.png",
				"image/weapon/No0000 Japanese sword/Japanese sword hit 2.png",
				"image/weapon/No0000 Japanese sword/Japanese sword hit 3.png");
	}

	@Override
	public int getRarity() {
		return 1;
	}

	@Override
	public int getDistance() {
		return DefaultUnit.NEAR;
	}

	@Override
	public int getHandle() {
		return DefaultUnit.ONE;
	}

	@Override
	public List<Integer> getElement() {
		return Arrays.asList(DefaultUnit.SLASH);
	}

	@Override
	public int getAtackPattern() {
		return DefaultAtackPattern.NEAR;
	}

	@Override
	public List<Integer> getWeaponStatus() {
		return Arrays.asList(100, 30, 1000, 1);
	}

	@Override
	public List<Integer> getUnitStatus() {
		return Arrays.asList(1000, 1000, 100, 10, 1, 5);
	}

	@Override
	public List<Integer> getCutStatus() {
		return Arrays.asList(5, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0);
	}
	
	@Override
	public List<List<Double>> getBuff(){
		return Arrays.asList();
	}
}