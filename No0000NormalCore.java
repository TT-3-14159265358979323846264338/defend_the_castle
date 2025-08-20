package defaultdata.core;

import java.util.Arrays;
import java.util.List;

public class No0000NormalCore extends CoreData{
	@Override
	public String getName() {
		return "ノーマルコア";
	}
	
	@Override
	public String getImageName() {
		return "image/soldier/normal core.png";
	}
	
	@Override
	public String getActionImageName() {
		return "image/soldier/normal core center.png";
	}
	
	@Override
	public int getRarity() {
		return 1;
	}
	
	@Override
	public List<Double> getWeaponStatus(){
		return Arrays.asList(1.0, 1.0, 1.0, 1.0);
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
}