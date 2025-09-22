package defaultdata.core;

import java.util.Arrays;
import java.util.List;

public class No0001NormalAtackCore extends CoreData{
	@Override
	public String getName() {
		return "ノーマルレッドコア";
	}
	
	@Override
	public String getImageName() {
		return "image/soldier/normal atack core.png";
	}
	
	@Override
	public String getActionImageName() {
		return "image/soldier/normal atack core center.png";
	}
	
	@Override
	public int getRarity() {
		return 1;
	}
	
	@Override
	public List<Double> getWeaponStatus(){
		return Arrays.asList(1.1, 1.0, 1.0, 1.0);
	}
	
	@Override
	public List<Double> getUnitStatus(){
		return Arrays.asList(1.0, 1.0, 1.0, 1.0, 1.0, 1.0);
	}
	
	@Override
	public List<Integer> getCutStatus(){
		return Arrays.asList(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0);
	}
	
	@Override
	public List<List<Double>> getBuff(){
		return Arrays.asList();
	}

	@Override
	public String getSkillImageName() {
		return null;
	}
}
