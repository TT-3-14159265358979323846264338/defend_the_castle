package defendthecastle.itemget;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import defaultdata.Core;
import defaultdata.Gacha;
import defaultdata.Weapon;
import defaultdata.gacha.GachaData;
import savedata.SaveGameProgress;

class GachaInformation{
	private final List<GachaData> gachaList;
	private int repeatCode = 0;
	private final Map<Integer, Integer> repeatMap = new HashMap<>();{
		repeatMap.put(0, 1);
		repeatMap.put(1, 5);
		repeatMap.put(2, 10);
	}
	private int gachaModeCode = 0;
	
	GachaInformation(SaveGameProgress saveGameProgress) {
		gachaList = createGachaData(saveGameProgress);
	}
	
	List<GachaData> createGachaData(SaveGameProgress saveGameProgress){
		return Stream.of(Gacha.values()).map(i -> i.getGachaData()).filter(i -> i.canActivate(saveGameProgress)).toList();
	}
	
	String[] getGachaName(){
		return gachaList.stream().map(i -> i.getName()).toArray(String[]::new);
	}
	
	int getRepeatNumber() {
		return repeatMap.get(repeatCode);
	}
	
	void changeRepeatNumber() {
		repeatCode = repeatCode();
	}
	
	int repeatCode() {
		return (repeatCode < repeatMap.size() - 1)? repeatCode + 1: 0;
	}
	
	void changeGachaMode(int mode) {
		gachaModeCode = mode;
	}
	
	List<Core> getCoreLineup(){
		return gachaList.get(gachaModeCode).getCoreLineup();
	}
	
	List<Double> getCoreRatio(){
		return gachaList.get(gachaModeCode).getCoreRatio();
	}
	
	List<Weapon> getWeaponLineup(){
		return gachaList.get(gachaModeCode).getWeaponLineup();
	}
	
	List<Double> getWeaponRatio(){
		return gachaList.get(gachaModeCode).getWeaponRatio();
	}
}