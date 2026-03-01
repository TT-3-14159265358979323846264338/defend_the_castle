package savedata;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import defaultdata.Stage;

public class OneStageMeritData {
	/**
	 * 戦功のクリア状況。
	 * ステージによって戦功数は異なるため、データベースでは多めに列が存在する。
	 * 有効な戦功数を超えてデータが保存されているため、リストを取り出す時は有効な戦功数で切り出す必要がある。
	 */
	private List<Boolean> meritClearList = new ArrayList<>();
	
	/**
	 * このステージの有効な戦功数。
	 */
	private final int activeMerit;
	
	OneStageMeritData(ResultSet result, int stageNo) throws Exception{
		activeMerit = Stage.getLabel(stageNo).getReward().size();
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