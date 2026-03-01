package defendthecastle.selectstage;

import java.awt.image.BufferedImage;
import java.util.List;

import commonclass.EditImage;

class StageImage {
	private final List<BufferedImage> detailStageImage;
	private final List<BufferedImage> selectStageImage;
	
	StageImage(ProgressData progressData){
		detailStageImage = detailStageImage(progressData);
		selectStageImage = selectStageImage();
	}
	
	List<BufferedImage> detailStageImage(ProgressData progressData){
		return progressData.getActivateStage().stream().map(i -> EditImage.stageImage(i.getLabel(), 5)).toList();
	}
	
	List<BufferedImage> selectStageImage(){
		return detailStageImage.stream().map(i -> EditImage.scalingImage(i, 3.5)).toList();
	}

	BufferedImage getDetailImage(int index) {
		return detailStageImage.get(index);
	}

	BufferedImage getSelectImage(int index) {
		return selectStageImage.get(index);
	}
	
	int imageSize() {
		return selectStageImage.size();
	}
}