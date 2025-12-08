package defendthecastle.itemget;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import defaultdata.DefaultGacha;
import defaultdata.gacha.GachaData;
import savedata.SaveGameProgress;

class GachaInformation{
	private List<GachaData> gachaList;
	private int repeatCode = 0;
	private Map<Integer, Integer> repeatMap = new HashMap<>();{
		repeatMap.put(0, 1);
		repeatMap.put(1, 5);
		repeatMap.put(2, 10);
	}
	private int gachaModeCode = 0;
	
	protected GachaInformation(SaveGameProgress SaveGameProgress) {
		gachaList = DefaultGacha.GACHA_DATA.stream().filter(i -> i.canActivate(SaveGameProgress)).toList();
	}
	
	protected String[] getGachaName(){
		return gachaList.stream().map(i -> i.getName()).toArray(String[]::new);
	}
	
	protected int getRepeatNumber() {
		return repeatMap.get(repeatCode);
	}
	
	protected void changeRepeatNumber() {
		repeatCode = (repeatCode < repeatMap.size() - 1)? repeatCode + 1: 0;
	}
	
	protected void changeGachaMode(int mode) {
		gachaModeCode = mode;
	}
	
	protected List<Integer> getCoreLineup(){
		return gachaList.get(gachaModeCode).getCoreLineup();
	}
	
	protected List<Double> getCoreRatio(){
		return gachaList.get(gachaModeCode).getCoreRatio();
	}
	
	protected List<Integer> getWeaponLineup(){
		return gachaList.get(gachaModeCode).getWeaponLineup();
	}
	
	protected List<Double> getWeaponRatio(){
		return gachaList.get(gachaModeCode).getWeaponRatio();
	}
}