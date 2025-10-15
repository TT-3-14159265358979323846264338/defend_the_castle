package defaultdata.weapon;

import java.util.Arrays;
import java.util.List;

import defaultdata.DefaultAtackPattern;
import defaultdata.DefaultUnit;

public class No0004FlameRod extends WeaponData{
	@Override
	public String getName() {
		return "炎ロッド";
	}

	@Override
	public String getExplanation() {
		return "生み出した炎で敵を攻撃する遠隔武器。弱った敵を優先的に狙う。";
	}
	
	@Override
	public String getImageName() {
		return "image/weapon/No0004 flame rod/flame rod.png";
	}

	@Override
	public List<String> getRightActionImageName() {
		return Arrays.asList("image/weapon/No0004 flame rod/flame rod right 0.png",
				"image/weapon/No0004 flame rod/flame rod right 1.png",
				"image/weapon/No0004 flame rod/flame rod right 2.png",
				"image/weapon/No0004 flame rod/flame rod right 3.png",
				"image/weapon/No0004 flame rod/flame rod right 4.png",
				"image/weapon/No0004 flame rod/flame rod right 5.png");
	}

	@Override
	public List<String> getLeftActionImageName() {
		return Arrays.asList("image/weapon/No0004 flame rod/flame rod left 0.png",
				"image/weapon/No0004 flame rod/flame rod left 1.png",
				"image/weapon/No0004 flame rod/flame rod left 2.png",
				"image/weapon/No0004 flame rod/flame rod left 3.png",
				"image/weapon/No0004 flame rod/flame rod left 4.png",
				"image/weapon/No0004 flame rod/flame rod left 5.png");
	}

	@Override
	public String getBulletImageName() {
		return "image/weapon/No0004 flame rod/flame rod bullet.png";
	}

	@Override
	public List<String> getHitImageName() {
		return Arrays.asList("image/weapon/No0004 flame rod/flame rod hit 1.png",
				"image/weapon/No0004 flame rod/flame rod hit 2.png",
				"image/weapon/No0004 flame rod/flame rod hit 3.png");
	}
	
	@Override
	public int getRarity() {
		return 1;
	}

	@Override
	public int getDistance() {
		return DefaultUnit.FAR;
	}

	@Override
	public int getHandle() {
		return DefaultUnit.ONE;
	}

	@Override
	public List<Integer> getElement() {
		return Arrays.asList(DefaultUnit.FLAME);
	}

	@Override
	public int getAtackPattern() {
		return DefaultAtackPattern.LOW_HP;
	}

	@Override
	public List<Integer> getWeaponStatus() {
		return Arrays.asList(100, 100, 1000, 1);
	}

	@Override
	public List<Integer> getUnitStatus() {
		return Arrays.asList(1000, 1000, 50, 5, 0, 5);
	}

	@Override
	public List<Integer> getCutStatus() {
		return Arrays.asList(0, 0, 0, 0, 10, 0, 0, 0, 0, 0, 0, 0);
	}
	
	@Override
	public List<List<Double>> getBuff(){
		return Arrays.asList();
	}
}