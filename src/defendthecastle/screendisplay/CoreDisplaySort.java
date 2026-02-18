package defendthecastle.screendisplay;

import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import java.util.stream.Stream;

import defaultdata.DefaultUnit;
import defaultdata.core.CoreData;

public class CoreDisplaySort extends SortPanel{
	private final CoreData[] coreData;
	
	public CoreDisplaySort(ScheduledExecutorService scheduler, List<Integer> defaultList) {
		coreData = DefaultUnit.CORE_DATA_MAP.values().toArray(CoreData[]::new);
		super(scheduler, defaultList);
	}
	
	@Override
	List<Integer> createRarityList() {
		return Stream.of(coreData).map(CoreData::getRarity).toList();
	}

	@Override
	List<List<Double>> createWeaponStatusList() {
		return Stream.of(coreData).map(CoreData::getWeaponStatus).toList();
	}

	@Override
	List<List<Double>> createUnitStatusList() {
		return Stream.of(coreData).map(CoreData::getUnitStatus).toList();
	}

	@Override
	List<List<Integer>> createCutList() {
		return Stream.of(coreData).map(CoreData::getCutStatus).toList();
	}

	@Override
	List<Integer> createDistanceList() {
		return null;
	}

	@Override
	List<Integer> createHandleList() {
		return null;
	}

	@Override
	List<List<Integer>> createElementList() {
		return null;
	}

	@Override
	List<Integer> createTargetList() {
		return null;
	}
}