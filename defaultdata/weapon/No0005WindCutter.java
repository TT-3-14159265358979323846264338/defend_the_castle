package defaultdata.weapon;

import java.util.Arrays;
import java.util.List;

import defaultdata.DefaultAtackPattern;
import defaultdata.DefaultUnit;

public class No0005WindCutter extends WeaponData{
	@Override
	public String getName() {
		return "ウィンドカッター";
	}

	@Override
	public String getExplanation() {
		return "風の刃で周囲の敵をまとめて攻撃できる近接武器種。攻撃力は低いが、速度は速い。";
	}
	
	@Override
	public String getImageName() {
		return "image/weapon/No0005 wind cutter/wind cutter.png";
	}

	@Override
	public List<String> getRightActionImageName() {
		return Arrays.asList();
	}

	@Override
	public List<String> getLeftActionImageName() {
		return Arrays.asList("image/weapon/No0005 wind cutter/wind cutter left 0.png",
				"image/weapon/No0005 wind cutter/wind cutter left 1.png",
				"image/weapon/No0005 wind cutter/wind cutter left 2.png",
				"image/weapon/No0005 wind cutter/wind cutter left 3.png",
				"image/weapon/No0005 wind cutter/wind cutter left 4.png",
				"image/weapon/No0005 wind cutter/wind cutter left 5.png");
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
	public int getDistance() {
		return DefaultUnit.NEAR;
	}

	@Override
	public int getHandle() {
		return DefaultUnit.BOTH;
	}

	@Override
	public List<Integer> getElement() {
		return Arrays.asList(DefaultUnit.WIND);
	}

	@Override
	public int getAtackPattern() {
		return DefaultAtackPattern.NEAR;
	}

	@Override
	public List<Integer> getWeaponStatus() {
		return Arrays.asList(30, 120, 700, 5);
	}

	@Override
	public List<Integer> getUnitStatus() {
		return Arrays.asList(1000, 1000, 30, 10, 0, 10);
	}

	@Override
	public List<Integer> getCutStatus() {
		return Arrays.asList(0, 0, 0, 0, 0, 0, 20, 0, 0, 0, 0, 0);
	}
	
	@Override
	public List<List<Double>> getBuff(){
		return Arrays.asList();
	}
}