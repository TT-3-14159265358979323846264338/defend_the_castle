package defaultdata.weapon;

import java.util.Arrays;
import java.util.List;

import defaultdata.DefaultAtackPattern;
import defaultdata.DefaultUnit;

public class No0003FirstAidKit extends WeaponData{
	@Override
	public String getName() {
		return "救急箱";
	}

	@Override
	public String getExplanation() {
		return "味方1体のHPを少し回復させる武器種。";
	}
	
	@Override
	public String getImageName() {
		return "image/weapon/No0003 first aid kit/first aid kit.png";
	}

	@Override
	public List<String> getRightActionImageName() {
		return Arrays.asList("image/weapon/No0003 first aid kit/first aid kit right 0.png",
				"image/weapon/No0003 first aid kit/first aid kit right 1.png",
				"image/weapon/No0003 first aid kit/first aid kit right 2.png",
				"image/weapon/No0003 first aid kit/first aid kit right 3.png",
				"image/weapon/No0003 first aid kit/first aid kit right 4.png",
				"image/weapon/No0003 first aid kit/first aid kit right 5.png");
	}

	@Override
	public List<String> getLeftActionImageName() {
		return Arrays.asList("image/weapon/No0003 first aid kit/first aid kit left 0.png",
				"image/weapon/No0003 first aid kit/first aid kit left 1.png",
				"image/weapon/No0003 first aid kit/first aid kit left 2.png",
				"image/weapon/No0003 first aid kit/first aid kit left 3.png",
				"image/weapon/No0003 first aid kit/first aid kit left 4.png",
				"image/weapon/No0003 first aid kit/first aid kit left 5.png");
	}

	@Override
	public String getBulletImageName() {
		return null;
	}

	@Override
	public List<String> getHitImageName() {
		return Arrays.asList("image/weapon/No0003 first aid kit/first aid kit hit 1.png",
				"image/weapon/No0003 first aid kit/first aid kit hit 2.png",
				"image/weapon/No0003 first aid kit/first aid kit hit 3.png");
	}

	@Override
	public int getRarity() {
		return 1;
	}

	@Override
	public int getDistance() {
		return DefaultUnit.ALL;
	}

	@Override
	public int getHandle() {
		return DefaultUnit.ONE;
	}

	@Override
	public List<Integer> getElement() {
		return Arrays.asList(DefaultUnit.SUPPORT);
	}

	@Override
	public int getAtackPattern() {
		return DefaultAtackPattern.LOW_HP;
	}

	@Override
	public List<Integer> getWeaponStatus() {
		return Arrays.asList(50, 100, 1000, 1);
	}

	@Override
	public List<Integer> getUnitStatus() {
		return Arrays.asList(200, 200, 10, 20, 0, 5);
	}

	@Override
	public List<Integer> getCutStatus() {
		return Arrays.asList(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 10);
	}
	
	@Override
	public List<List<Double>> getBuff(){
		return Arrays.asList();
	}
}