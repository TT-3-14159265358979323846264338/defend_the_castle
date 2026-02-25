package defendthecastle;

import java.awt.image.BufferedImage;
import java.util.List;
import java.util.Random;
import java.util.stream.IntStream;

import commonclass.CommonImage;
import defaultdata.OtherData;

class MainImage extends CommonImage{
	private final BufferedImage titleImage;
	private final List<BufferedImage> coreImageList;
	private final List<Integer> randamList;
	
	MainImage(int number){
		titleImage = createTitleImage();
		coreImageList = createNormalCoreImage(1);
		randamList = createRandamList(number);
	}
	
	BufferedImage createTitleImage() {
		return createDefaultOther().getTitleImage(2);
	}
	
	OtherData createDefaultOther() {
		return new OtherData();
	}
	
	List<Integer> createRandamList(int number){
		var random = createRandom();
		return IntStream.range(0, number).mapToObj(_ -> random.nextInt(coreImageList.size())).toList();
	}
	
	Random createRandom() {
		return new Random();
	}
	
	BufferedImage getTitleImage() {
		return titleImage;
	}

	BufferedImage getCoreImage(int index) {
		return coreImageList.get(randamList.get(index));
	}
}