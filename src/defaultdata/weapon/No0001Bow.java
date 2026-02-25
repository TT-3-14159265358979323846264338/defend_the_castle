package defaultdata.weapon;

import java.util.Arrays;
import java.util.List;

import defaultdata.AtackPattern;
import defaultdata.Distance;
import defaultdata.Element;
import defaultdata.Handle;

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
	public Distance getDistance() {
		return Distance.FAR;
	}

	@Override
	public Handle getHandle() {
		return Handle.BOTH;
	}

	@Override
	public List<Element> getElement() {
		return Arrays.asList(Element.PIERCE);
	}

	@Override
	public int getAtackPattern() {
		return AtackPattern.NEAR;
	}

	@Override
	public List<Integer> getWeaponStatus() {
		return Arrays.asList(100, 120, 1000, 1);
	}

	@Override
	public List<Integer> getUnitStatus() {
		return Arrays.asList(500, 500, 20, 0, 0, 10);
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