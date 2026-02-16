package savedata;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import defaultdata.DefaultStage;

public class OneStageMeritData {
	/**
	 * 戦功のクリア状況
	 */
	private List<Boolean> meritClearList = new ArrayList<>();
	
	private int activeMerit;
	
	OneStageMeritData(ResultSet result, int index) throws Exception{
		activeMerit = DefaultStage.STAGE_DATA.get(index).getReward().size();
		for(int i = 0; i < SaveGameProgress.MERIT_MAX_NUMBER; i++) {
			meritClearList.add(result.getBoolean(String.format("%s%d", SaveGameProgress.MERIT_COLUMN, i)));
		}
	}
	
	public List<Boolean> getMeritClearList(){
		return meritClearList.stream().limit(activeMerit).toList();
	}
	
	public boolean getMeritClear(int index) {
		return meritClearList.get(index);
	}
	
	public void setMeritClear(int index, boolean exists) {
		meritClearList.set(index, exists);
	}
}