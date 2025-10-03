package defaultdata.core;

import java.util.Arrays;
import java.util.List;

public class No0004NormalHealCore extends CoreData{
	@Override
	public String getName() {
		return "ノーマルグリーンコア";
	}
	
	@Override
	public String getExplanation() {
		return "回復力が少し高い通常コア。";
	}
	
	@Override
	public String getImageName() {
		return "image/core/No0004 normal heal core/normal heal core.png";
	}
	
	@Override
	public String getActionImageName() {
		return "image/core/No0004 normal heal core/normal heal core center.png";
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
		return Arrays.asList(1.0, 1.0, 1.0, 1.1, 1.0, 1.0);
	}
	
	@Override
	public List<Integer> getCutStatus(){
		return Arrays.asList(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 5);
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