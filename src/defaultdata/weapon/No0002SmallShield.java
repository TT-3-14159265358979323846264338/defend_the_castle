package defaultdata.weapon;

import java.util.Arrays;
import java.util.List;

import defaultdata.AtackPattern;
import defaultdata.Distance;
import defaultdata.Element;
import defaultdata.Handle;

public class No0002SmallShield extends WeaponData{
	@Override
	public String getName() {
		return "スモールシールド";
	}

	@Override
	public String getExplanation() {
		return "攻撃を行わない代わりに防御力とブロックが高い。";
	}
	
	@Override
	public String getImageName() {
		return "image/weapon/No0002 small shield/small shield.png";
	}

	@Override
	public List<String> getRightActionImageName() {
		return Arrays.asList("image/weapon/No0002 small shield/small shield right 0.png");
	}

	@Override
	public List<String> getLeftActionImageName() {
		return Arrays.asList("image/weapon/No0002 small shield/small shield left 0.png");
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
	public int getRarity() {
		return 1;
	}

	@Override
	public Distance getDistance() {
		return Distance.NEAR;
	}

	@Override
	public Handle getHandle() {
		return Handle.ONE;
	}

	@Override
	public List<Element> getElement() {
		return Arrays.asList();
	}

	@Override
	public int getAtackPattern() {
		return AtackPattern.NO_ATACK;
	}

	@Override
	public List<Integer> getWeaponStatus() {
		return Arrays.asList(0, 0, 0, 0);
	}

	@Override
	public List<Integer> getUnitStatus() {
		return Arrays.asList(1000, 1000, 45, 30, 3, 5);
	}

	@Override
	public List<Integer> getCutStatus() {
		return Arrays.asList(10, 10, 10, 10, 5, 5, 5, 5, 5, 5, 5, 0);
	}
	
	@Override
	public List<List<Double>> getBuff(){
		return Arrays.asList();
	}
}