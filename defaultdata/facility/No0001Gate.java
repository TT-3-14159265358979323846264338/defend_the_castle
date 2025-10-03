package defaultdata.facility;

import java.util.Arrays;
import java.util.List;

import defaultdata.DefaultAtackPattern;

public class No0001Gate extends FacilityData{
	@Override
	public String getName() {
		return "城門";
	}

	@Override
	public String getExplanation() {
		return "敵の進軍を防ぐ門。破壊されるまで全ての敵をブロックする。";
	}
	
	@Override
	public List<String> getActionFrontImageName() {
		return Arrays.asList("image/facility/front gate.png");
	}

	@Override
	public List<String> getActionSideImageName() {
		return Arrays.asList("image/facility/side gate.png");
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
		return "image/facility/break gate.png";
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
		return Arrays.asList(2000, 2000, 0, 0, -1, 0);
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