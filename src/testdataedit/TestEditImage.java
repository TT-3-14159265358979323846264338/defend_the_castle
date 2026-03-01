package testdataedit;

import java.awt.image.BufferedImage;
import java.util.List;
import java.util.stream.Stream;

import commonclass.CommonImage;
import defaultdata.Stage;

class TestEditImage extends CommonImage{
	private final List<BufferedImage> coreImage;
	private final List<BufferedImage> weaponImage;
	private final List<BufferedImage> stageImage;
	private final int UNIT_RATIO = 4;
	private final int STAGE_RATIO = 20;
	
	TestEditImage(){
		coreImage = createNormalCoreImage(UNIT_RATIO);
		weaponImage = createNormalWeaponImage(UNIT_RATIO);
		stageImage = createStageImage();
	}
	
	List<BufferedImage> createStageImage(){
		return Stream.of(Stage.values()).map(i -> i.getLabel().getImage(STAGE_RATIO)).toList();
	}

	List<BufferedImage> getCoreImage() {
		return coreImage;
	}
	
	int coreSize() {
		return coreImage.size();
	}

	List<BufferedImage> getWeaponImage() {
		return weaponImage;
	}
	
	int weaponSize() {
		return weaponImage.size();
	}

	List<BufferedImage> getStageImage() {
		return stageImage;
	}
	
	int stageSize() {
		return stageImage.size();
	}
}