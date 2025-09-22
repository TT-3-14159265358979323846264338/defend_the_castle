package screendisplay;

import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import defaultdata.DefaultUnit;
import defaultdata.core.CoreData;
import defaultdata.weapon.WeaponData;

//ソート画面振り分け
public class DisplaySort extends SortPanel{
	public void core(List<Integer> defaultList) {
		CoreData[] CoreData = IntStream.range(0, DefaultUnit.CORE_DATA_MAP.size()).mapToObj(i -> DefaultUnit.CORE_DATA_MAP.get(i)).toArray(CoreData[]::new);
		rarityList = Stream.of(CoreData).map(i -> i.getRarity()).toList();
		weaponStatusList = Stream.of(CoreData).map(i -> i.getWeaponStatus()).toList();
		unitStatusList = Stream.of(CoreData).map(i -> i.getUnitStatus()).toList();
		cutList = Stream.of(CoreData).map(i -> i.getCutStatus()).toList();
		super.setSortPanel(defaultList);
	}
	
	public void weapon(List<Integer> defaultList) {
		WeaponData[] WeaponData = IntStream.range(0, DefaultUnit.WEAPON_DATA_MAP.size()).mapToObj(i -> DefaultUnit.WEAPON_DATA_MAP.get(i)).toArray(WeaponData[]::new);
		rarityList = Stream.of(WeaponData).map(i -> i.getRarity()).toList();
		weaponStatusList = Stream.of(WeaponData).map(i -> i.getWeaponStatus().stream().map(j -> (double) j).toList()).toList();
		unitStatusList = Stream.of(WeaponData).map(i -> i.getUnitStatus().stream().map(j -> (double) j).toList()).toList();
		cutList = Stream.of(WeaponData).map(i -> i.getCutStatus()).toList();
		distanceList = Stream.of(WeaponData).map(i -> i.getDistance()).toList();
		handleList = Stream.of(WeaponData).map(i -> i.getHandle()).toList();
		elementList = Stream.of(WeaponData).map(i -> i.getElement()).toList();
		targetList = Stream.of(WeaponData).map(i -> i.getAtackPattern()).toList();
		super.setSortPanel(defaultList);
	}
}