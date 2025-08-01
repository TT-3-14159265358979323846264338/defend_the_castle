package defaultdata;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.awt.image.ConvolveOp;
import java.awt.image.Kernel;
import java.io.File;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import javax.imageio.ImageIO;

import defaultdata.facility.FacilityData;
import defaultdata.stage.StageData;

//画像処理
public class EditImage{
	public static BufferedImage input(String imageName, double ratio) {
		if(imageName == null) {
			return null;
		}
		BufferedImage image = null;
		try{
			image = getImage(ImageIO.read(new File(imageName)), ratio);
		}catch(Exception e) {
			e.printStackTrace();
		}
		return image;
	}
	
	public static List<BufferedImage> input(List<String> imageNameList, double ratio){
		return imageNameList.stream().map(i -> (i == null)? null: input(i, ratio)).toList();
	}
	
	private static BufferedImage getImage(BufferedImage originalImage, double ratio) {
		int width = originalImage.getWidth();
		int height = originalImage.getHeight();
		BufferedImage image = getBlankImage(width, height);
		IntStream.range(0, height).forEach(y -> IntStream.range(0, width).forEach(x -> {
			if(originalImage.getRGB(x, y) != new Color(255, 255, 255).getRGB()) {
				image.setRGB(x, y, originalImage.getRGB(x, y));
			}
		}));
		return scalingImage(image, ratio);
	}
	
	public static BufferedImage scalingImage(BufferedImage originalImage, double ratio) {
		int resizeWidth = (int) (originalImage.getWidth() / ratio);
		int resizeHeight = (int) (originalImage.getHeight() / ratio);
		BufferedImage resizeImage = getBlankImage(resizeWidth, resizeHeight);
		resizeImage.createGraphics().drawImage(
				originalImage.getScaledInstance(resizeWidth, resizeHeight, Image.SCALE_AREA_AVERAGING),
	        0, 0, resizeWidth, resizeHeight, null);
		return resizeImage;
	}
	
	public static BufferedImage compositeImage(List<BufferedImage> originalImage) {
		int width = originalImage.get(1).getWidth();
		int height = originalImage.get(1).getHeight();
		BufferedImage image = getBlankImage(width, height);
		Graphics g = image.getGraphics();
		originalImage.stream().forEach(i -> g.drawImage(i, 0, 0, null));
		g.dispose();
		return image;
	}
	
	public static BufferedImage rotateImage(BufferedImage originalImage, double angle) {
		int width = originalImage.getWidth();
		int height = originalImage.getHeight();
		BufferedImage image = getBlankImage(width, height);
		Graphics2D g2 =  (Graphics2D) image.getGraphics();
		AffineTransform rotate = new AffineTransform();
		rotate.setToRotation(angle, width / 2, height / 2);
		g2.setTransform(rotate);
		g2.drawImage(originalImage, 0, 0, null);
		g2.dispose();
		return image;
	}
	
	public static BufferedImage mirrorImage(BufferedImage originalImage) {
		AffineTransform mirror = AffineTransform.getScaleInstance(-1.0, 1.0);
		mirror.translate(- originalImage.getWidth(), 0);
		return new AffineTransformOp(mirror, null).filter(originalImage, null);
	}
	
	public static BufferedImage effectImage(BufferedImage originalImage, int expansion, int color) {
		int width = originalImage.getWidth();
		int height = originalImage.getHeight();
		BufferedImage image = getBlankImage(width, height);
		IntStream.range(0, height).forEach(y -> IntStream.range(0, width).forEach(x -> {
			if(originalImage.getRGB(x, y) == new Color(0, 0, 0).getRGB()) {
				image.setRGB(x, y, color);
			}
		}));
		int resizeWidth = width + expansion;
		int resizeHeight = height + expansion;
		BufferedImage resizeImage = getBlankImage(resizeWidth, resizeHeight);
		resizeImage.createGraphics().drawImage(
	    	image.getScaledInstance(resizeWidth, resizeHeight, Image.SCALE_AREA_AVERAGING),
	        0, 0, resizeWidth, resizeHeight, null);
		int pixel = 9;
		float[] matrix = new float[pixel * pixel];
		IntStream.range(0, matrix.length).forEach(i -> matrix[i] = 1.5f / (pixel * pixel));//透過度あるため、色濃いめの1.5f
		ConvolveOp blur = new ConvolveOp(new Kernel(pixel, pixel, matrix), ConvolveOp.EDGE_NO_OP, null);
		return blur.filter(resizeImage, null);
	}
	
	public static BufferedImage stageImage(StageData StageData, double ratio) {
		BufferedImage image = StageData.getImage(2);
		Graphics g = image.getGraphics();
		BiConsumer<BufferedImage, Point> drawImage = (displayImage, point) -> {
			g.drawImage(displayImage, point.x, point.y, null);
		};
		//配置マスの表示
		List<BufferedImage> placementImage = new DefaultStage().getPlacementImage(4);
		IntStream.range(0, placementImage.size()).forEach(i -> StageData.getPlacementPoint().get(i).stream().forEach(j -> g.drawImage(placementImage.get(i), j.get(0).intValue(), j.get(1).intValue(), null)));
		//設備の表示
		FacilityData[] FacilityData = IntStream.range(0, DefaultStage.FACILITY_DATA_MAP.size()).mapToObj(i -> DefaultStage.FACILITY_DATA_MAP.get(i)).toArray(FacilityData[]::new);
		List<BufferedImage> frontFacilityImage = Stream.of(FacilityData).map(i -> i.getActionFrontImage(4).get(0)).toList();
		List<BufferedImage> sideFacilityImage = Stream.of(FacilityData).map(i -> i.getActionSideImage(4).get(0)).toList();
		IntStream.range(0, StageData.getFacility().size()).forEach(i -> {
			drawImage.accept(StageData.getFacilityDirection().get(i)? frontFacilityImage.get(StageData.getFacility().get(i)): sideFacilityImage.get(StageData.getFacility().get(i)), StageData.getFacilityPoint().get(i));
		});
		g.dispose();
		return scalingImage(image, ratio / 2);
	}
	
	private static BufferedImage getBlankImage(int width, int height) {
		BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		IntStream.range(0, height).forEach(y -> IntStream.range(0, width).forEach(x -> image.setRGB(x, y, 0)));
		return image;
	}
}