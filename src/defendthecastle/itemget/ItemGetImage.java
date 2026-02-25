package defendthecastle.itemget;

import java.awt.image.BufferedImage;
import java.util.List;

import commonclass.CommonImage;
import defaultdata.OtherData;

class ItemGetImage extends CommonImage{
	private final List<BufferedImage> coreImageList;
	private final List<BufferedImage> weaponImageList;
	private final BufferedImage ballImage;
	private final List<BufferedImage> halfBallImage;
	private final BufferedImage handleImage;
	private final List<BufferedImage> machineImage;
	private final BufferedImage turnImage;
	private final BufferedImage effectImage;
	private final int RATIO = 2;
	private final int EFFECT_RATIO = 1;
	
	ItemGetImage(){
		coreImageList = createNormalCoreImage(RATIO);
		weaponImageList = createNormalWeaponImage(RATIO);
		var defaultOther = createDefaultOther();
		ballImage = defaultOther.getBallImage(RATIO);
		halfBallImage = defaultOther.getHalfBallImage(RATIO);
		handleImage = defaultOther.getHandleImage(RATIO);
		machineImage = defaultOther.getMachineImage(RATIO);
		turnImage = defaultOther.getTurnImage(RATIO);
		effectImage = defaultOther.getEffectImage(EFFECT_RATIO);
	}

	List<BufferedImage> getCoreImageList() {
		return coreImageList;
	}

	List<BufferedImage> getWeaponImageList() {
		return weaponImageList;
	}
	
	OtherData createDefaultOther() {
		return new OtherData();
	}

	BufferedImage getBallImage() {
		return ballImage;
	}

	List<BufferedImage> getHalfBallImage() {
		return halfBallImage;
	}

	BufferedImage getHandleImage() {
		return handleImage;
	}

	List<BufferedImage> getMachineImage() {
		return machineImage;
	}

	BufferedImage getTurnImage() {
		return turnImage;
	}

	BufferedImage getEffectImage() {
		return effectImage;
	}
}