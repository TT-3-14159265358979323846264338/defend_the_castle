package defendthecastle.screendisplay;

import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import java.util.stream.Stream;

import defaultdata.DefaultUnit;
import defaultdata.weapon.WeaponData;

public class WeaponDisplaySort extends SortPanel{
	private final WeaponData[] weaponData;
	
	public WeaponDisplaySort(ScheduledExecutorService scheduler, List<Integer> defaultList) {
		weaponData = DefaultUnit.WEAPON_DATA_MAP.values().toArray(WeaponData[]::new);
		super(scheduler, defaultList);
	}
	
	@Override
	List<Integer> createRarityList() {
		return Stream.of(weaponData).map(WeaponData::getRarity).toList();
	}

	@Override
	List<List<Double>> createWeaponStatusList() {
		return Stream.of(weaponData).map(this::weaponStatus).toList();
	}
	
	List<Double> weaponStatus(WeaponData weaponData) {
		return weaponData.getWeaponStatus().stream().map(Integer::doubleValue).toList();
	}

	@Override
	List<List<Double>> createUnitStatusList() {
		return Stream.of(weaponData).map(this::unitStatus).toList();
	}
	
	List<Double> unitStatus(WeaponData weaponData) {
		return weaponData.getUnitStatus().stream().map(Integer::doubleValue).toList();
	}

	@Override
	List<List<Integer>> createCutList() {
		return Stream.of(weaponData).map(WeaponData::getCutStatus).toList();
	}

	@Override
	List<Integer> createDistanceList() {
		return Stream.of(weaponData).map(WeaponData::getDistance).toList();
	}

	@Override
	List<Integer> createHandleList() {
		return Stream.of(weaponData).map(WeaponData::getHandle).toList();
	}

	@Override
	List<List<Integer>> createElementList() {
		return Stream.of(weaponData).map(WeaponData::getElement).toList();
	}

	@Override
	List<Integer> createTargetList() {
		return Stream.of(weaponData).map(WeaponData::getAtackPattern).toList();
	}
}