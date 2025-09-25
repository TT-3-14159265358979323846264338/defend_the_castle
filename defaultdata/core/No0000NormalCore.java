package defaultdata.core;

import java.util.Arrays;
import java.util.List;

import battle.Buff;

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
		return Arrays.asList(
				Arrays.asList(Buff.SKILL, Buff.ALLY, Buff.ALL, Buff.RANGE, Buff.MULTIPLICATION, 2.0, Buff.NONE, Buff.NONE, 5.0, 5.0, 10.0));
	}

	@Override
	public String getSkillImageName() {
		return "image/skill/atack buff.png";
	}
}