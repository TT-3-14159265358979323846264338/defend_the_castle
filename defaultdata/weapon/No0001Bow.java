package defaultdata.weapon;

import java.util.Arrays;
import java.util.List;

import defaultdata.DefaultAtackPattern;
import defaultdata.DefaultUnit;

public class No0001Bow extends WeaponData{
	@Override
	public String getName() {
		return "弓";
	}

	@Override
	public String getExplanation() {
		return "一般的な遠距離武器。長い射程で1体を攻撃する。";
	}
	
	@Override
	public String getImageName() {
		return "image/weapon/No0001 bow/bow.png";
	}

	@Override
	public List<String> getRightActionImageName() {
		return Arrays.asList();
	}

	@Override
	public List<String> getLeftActionImageName() {
		return Arrays.asList("image/weapon/No0001 bow/bow left 0.png",
				"image/weapon/No0001 bow/bow left 1.png",
				"image/weapon/No0001 bow/bow left 2.png",
				"image/weapon/No0001 bow/bow left 3.png",
				"image/weapon/No0001 bow/bow left 4.png",
				"image/weapon/No0001 bow/bow left 5.png");
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
	public int getRarity() {
		return 1;
	}

	@Override
	public int getDistance() {
		return DefaultUnit.FAR;
	}

	@Override
	public int getHandle() {
		return DefaultUnit.BOTH;
	}

	@Override
	public List<Integer> getElement() {
		return Arrays.asList(DefaultUnit.PIERCE);
	}

	@Override
	public int getAtackPattern() {
		return DefaultAtackPattern.NEAR;
	}

	@Override
	public List<Integer> getWeaponStatus() {
		return Arrays.asList(100, 150, 1000, 1);
	}

	@Override
	public List<Integer> getUnitStatus() {
		return Arrays.asList(500, 500, 20, 10, 0, 10);
	}

	@Override
	public List<Integer> getCutStatus() {
		return Arrays.asList(0, 10, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0);
	}
	
	@Override
	public List<List<Double>> getBuff(){
		return Arrays.asList();
	}
}