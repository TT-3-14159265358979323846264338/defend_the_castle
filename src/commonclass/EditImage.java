package commonclass;

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

import defaultdata.Placement;
import defaultdata.Facility;
import defaultdata.stage.StageData;

//画像処理
public class EditImage{
	/**
	 * 画像の取り込み(単独ファイル)。
	 * @param imageName - 画像のファイル名。nullを受け付ける。
	 * @param ratio - 元の画像を何倍の縮尺で取り込むか指定。
	 * @return 取り込んだ画像を返却する。imageNameがnullであればnullを返却する。ファイルが存在しなければエラーメッセージのみ表示して処理を継続する。<br>
	 * 			画像中の白部分(new Color(255, 255, 255))は全て透過色として扱われる。
	 */
	public static BufferedImage input(String imageName, double ratio) {
		if(imageName == null) {
			return null;
		}
		BufferedImage image = null;
		try{
			image = getImage(ImageIO.read(new File(imageName)), ratio);
		}catch(Exception e) {
			System.out.println("取り込み不可ファイル: " + imageName);
			e.printStackTrace();
		}
		return image;
	}
	
	/**
	 * 画像の取り込み(複数ファイル)。
	 * @param imageNameList - 画像のファイル名List。nullを受け付ける。
	 * @param ratio - 元の画像を何倍の縮尺で取り込むか指定。
	 * @return 取り込んだ画像を返却する。imageNameList中にnullであればnullを返却する。空のListであれば空のListを返却する。ファイルが存在しなければエラーメッセージのみ表示して処理を継続する。<br>
	 * 			画像中の白部分(new Color(255, 255, 255))は全て透過色として扱われる。
	 */
	public static List<BufferedImage> input(List<String> imageNameList, double ratio){
		return imageNameList.stream().map(i -> (i == null)? null: input(i, ratio)).toList();
	}
	
	static BufferedImage getImage(BufferedImage originalImage, double ratio) {
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
	
	/**
	 * 画像の縮尺変更。
	 * @param originalImage - 縮尺を変更する元の画像。
	 * @param ratio - 元の画像を何倍の縮尺で取り込むか指定。
	 * @return 倍率変更された画像を返却する。
	 */
	public static BufferedImage scalingImage(BufferedImage originalImage, double ratio) {
		int resizeWidth = (int) (originalImage.getWidth() / ratio);
		int resizeHeight = (int) (originalImage.getHeight() / ratio);
		BufferedImage resizeImage = getBlankImage(resizeWidth, resizeHeight);
		resizeImage.createGraphics().drawImage(
				originalImage.getScaledInstance(resizeWidth, resizeHeight, Image.SCALE_AREA_AVERAGING),
	        0, 0, resizeWidth, resizeHeight, null);
		return resizeImage;
	}
	
	/**
	 * 画像の重ね掛け。
	 * @param originalImage - 重ね掛けを行う画像List。
	 * @return 重ね掛け画像を返却する。画像は左上を起点に重ね掛けされる。
	 */
	public static BufferedImage compositeImage(List<BufferedImage> originalImage) {
		int width = originalImage.get(1).getWidth();
		int height = originalImage.get(1).getHeight();
		BufferedImage image = getBlankImage(width, height);
		Graphics g = image.getGraphics();
		originalImage.stream().forEach(i -> g.drawImage(i, 0, 0, null));
		g.dispose();
		return image;
	}
	
	/**
	 * 画像の回転。
	 * @param originalImage - 回転させる画像。
	 * @param angle - 回転させる角度[rad]。
	 * @return 回転させた画像を返却する。
	 */
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
	
	/**
	 * 画像の反転。
	 * @param originalImage - 反転させる画像。
	 * @return 左右対称で反転させた画像を返却する。
	 */
	public static BufferedImage mirrorImage(BufferedImage originalImage) {
		AffineTransform mirror = AffineTransform.getScaleInstance(-1.0, 1.0);
		mirror.translate(- originalImage.getWidth(), 0);
		return new AffineTransformOp(mirror, null).filter(originalImage, null);
	}
	
	/**
	 * ガチャ排出時のエフェクト作成。<br>
	 * このメソッドはガチャ排出時専用である。
	 * @param originalImage - エフェクト画像。
	 * @param expansion - 拡大させる大きさ。
	 * @param color - 変更後の色指定。
	 * @return 大きさ, 色, ぼかしエフェクトを展開した画像を返却する。
	 */
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
	
	/**
	 * 完全なステージ画像を作成。
	 * @param stageData - 対象のステージ情報。
	 * @param ratio - 元の画像を何倍の縮尺で取り込むか指定。
	 * @return ステージ画像に損傷のない設備を加えた完全なステージ画像を返却する。
	 */
	public static BufferedImage stageImage(StageData stageData, double ratio) {
		BufferedImage image = stageData.getImage(2);
		Graphics g = image.getGraphics();
		BiConsumer<BufferedImage, Point> drawImage = (displayImage, point) -> {
			g.drawImage(displayImage, point.x, point.y, null);
		};
		//配置マスの表示
		List<BufferedImage> placementImage = new Placement().getPlacementImage(4);
		IntStream.range(0, placementImage.size()).forEach(i -> stageData.getPlacementPoint().get(i).stream().forEach(j -> g.drawImage(placementImage.get(i), j.get(0).intValue(), j.get(1).intValue(), null)));
		//設備の表示
		List<BufferedImage> frontImage = Stream.of(Facility.values()).map(i -> i.getLabel().getActionFrontImage(4).get(0)).toList();
		List<BufferedImage> sideImage = Stream.of(Facility.values()).map(i -> i.getLabel().getActionSideImage(4).get(0)).toList();
		IntStream.range(0, stageData.getFacility().size()).forEach(i -> drawImage.accept(facilityImage(frontImage, sideImage, stageData, i), stageData.getFacilityPoint().get(i)));
		g.dispose();
		return scalingImage(image, ratio / 2);
	}
	
	static BufferedImage facilityImage(List<BufferedImage> frontImage, List<BufferedImage> sideImage, StageData stageData, int number) {
		return stageData.getFacilityDirection().get(number)? selectImage(frontImage, stageData, number): selectImage(sideImage, stageData, number);
	}
	
	static BufferedImage selectImage(List<BufferedImage> imageList, StageData stageData, int number) {
		return imageList.get(stageData.getFacility().get(number).getId());
	}
	
	static BufferedImage getBlankImage(int width, int height) {
		BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		IntStream.range(0, height).forEach(y -> IntStream.range(0, width).forEach(x -> image.setRGB(x, y, 0)));
		return image;
	}
}