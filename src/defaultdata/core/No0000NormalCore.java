package defaultdata.core;

import java.util.Arrays;
import java.util.List;

public class No0000NormalCore extends CoreData{
	@Override
	public String getName() {
		return "ノーマルコア";
	}

	@Override
	public String getExplanation() {
		return "初期コア。ゲーム開始時に8体獲得可能。リサイクル不可。";
	}
	
	@Override
	public String getImageName() {
		return "image/core/No0000 normal core/normal core.png";
	}
	
	@Override
	public String getActionImageName() {
		return "image/core/No0000 normal core/normal core center.png";
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

	@Override
	public String getSkillImageName() {
		return null;
	}
}