package defaultdata.facility;

import java.util.Arrays;
import java.util.List;

import defaultdata.DefaultAtackPattern;

public class No0000Castle extends FacilityData{
	@Override
	public String getName() {
		return "本丸";
	}

	@Override
	public List<String> getActionFrontImageName() {
		return Arrays.asList("image/field/castle.png");
	}

	@Override
	public List<String> getActionSideImageName() {
		return Arrays.asList("image/field/castle.png");
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
	public String getBreakImageName() {
		return "image/field/castle.png";
	}

	@Override
	public List<Integer> getElement() {
		return Arrays.asList();
	}

	@Override
	public int getAtackPattern() {
		return DefaultAtackPattern.NEAR;
	}

	@Override
	public List<Integer> getWeaponStatus() {
		return Arrays.asList();
	}

	@Override
	public List<Integer> getUnitStatus() {
		return Arrays.asList(10000, 10000, 0, 0, -1, 0);
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