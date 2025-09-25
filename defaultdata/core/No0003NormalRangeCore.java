package defaultdata.core;

import java.util.Arrays;
import java.util.List;

public class No0003NormalRangeCore extends CoreData{
	@Override
	public String getName() {
		return "ノーマルパープルコア";
	}
	
	@Override
	public String getExplanation() {
		return "射程が少し長い通常コア。";
	}
	
	@Override
	public String getImageName() {
		return "image/soldier/normal range core.png";
	}
	
	@Override
	public String getActionImageName() {
		return "image/soldier/normal range core center.png";
	}
	
	@Override
	public int getRarity() {
		return 1;
	}
	
	@Override
	public List<Double> getWeaponStatus(){
		return Arrays.asList(1.0, 1.1, 1.0, 1.0);
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