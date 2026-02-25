package defaultdata;

import java.awt.image.BufferedImage;
import java.util.List;

import commonclass.EditImage;

public class Placement {
	public static final List<String> PLACEMENT_NAME_LIST = List.of(
			"image/field/near placement.png",
			"image/field/far placement.png",
			"image/field/all placement.png"
			);
	
	public List<BufferedImage> getPlacementImage(double ratio){
		return EditImage.input(PLACEMENT_NAME_LIST, ratio);
	}
}