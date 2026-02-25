package defendthecastle.screendisplay;

import java.util.List;
import java.util.stream.Stream;

import defaultdata.Core;
import defaultdata.Distance;
import defaultdata.Element;
import defaultdata.Handle;
import defaultdata.core.CoreData;

public class CoreDisplaySort extends SortPanel{
	private final CoreData[] coreData;
	
	public CoreDisplaySort(List<Integer> defaultList) {
		coreData = Stream.of(Core.values()).map(i -> i.getCoreData()).toArray(CoreData[]::new);
		super(defaultList);
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
	List<Distance> createDistanceList() {
		return null;
	}

	@Override
	List<Handle> createHandleList() {
		return null;
	}

	@Override
	List<List<Element>> createElementList() {
		return null;
	}

	@Override
	List<Integer> createTargetList() {
		return null;
	}
}