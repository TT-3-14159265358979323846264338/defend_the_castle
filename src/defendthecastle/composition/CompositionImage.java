package defendthecastle.composition;

import java.awt.image.BufferedImage;
import java.util.List;

import defendthecastle.commoninheritance.CommonImage;

class CompositionImage extends CommonImage{
	private final int RATIO = 2;
	private final List<BufferedImage> normalCoreList;
	private final List<BufferedImage> normalWeaponList;
	private final List<BufferedImage> rightWeaponList;
	private final List<BufferedImage> ceterCoreList;
	private final List<BufferedImage> leftWeaponList;
	
	CompositionImage(){
		normalCoreList = createNormalCoreImage(RATIO);
		normalWeaponList = createNormalWeaponImage(RATIO);
		rightWeaponList = createBattleRightWeaponImage(RATIO);
		ceterCoreList = createBattleCoreImage(RATIO);
		leftWeaponList = createBattleLeftWeaponImage(RATIO);
	}

	List<BufferedImage> getNormalCoreList() {
		return normalCoreList;
	}

	List<BufferedImage> getNormalWeaponList() {
		return normalWeaponList;
	}
	
	BufferedImage getRightWeapon(int index) {
		return rightWeaponList.get(index);
	}
	
	BufferedImage getCore(int index) {
		return ceterCoreList.get(index);
	}
	
	BufferedImage getLeftWeapon(int index) {
		return leftWeaponList.get(index);
	}
}