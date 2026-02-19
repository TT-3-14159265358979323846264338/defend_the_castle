package defendthecastle.itemdispose;

import java.awt.image.BufferedImage;
import java.util.List;

import commoninheritance.CommonImage;
import defaultdata.DefaultUnit;

class DefaultDisposeData extends CommonImage{
	private final int RATIO = 2;
	private final List<BufferedImage> coreImageList;
	private final List<BufferedImage> weaponImageList;
	private final List<Integer> coreRarityList;
	private final List<Integer> weaponRarityList;
	
	DefaultDisposeData(){
		coreImageList = createNormalCoreImage(RATIO);
		weaponImageList = createNormalWeaponImage(RATIO);
		coreRarityList = createCoreRarityList();
		weaponRarityList = createWeaponRarityList();
	}
	
	List<Integer> createCoreRarityList(){
		return DefaultUnit.CORE_DATA_MAP.values().stream().map(i -> i.getRarity()).toList();
	}
	
	List<Integer> createWeaponRarityList(){
		return DefaultUnit.WEAPON_DATA_MAP.values().stream().map(i -> i.getRarity()).toList();
	}

	List<BufferedImage> getCoreImageList() {
		return coreImageList;
	}

	List<BufferedImage> getWeaponImageList() {
		return weaponImageList;
	}

	List<Integer> getCoreRarityList() {
		return coreRarityList;
	}

	List<Integer> getWeaponRarityList() {
		return weaponRarityList;
	}
}