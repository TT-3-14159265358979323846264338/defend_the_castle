package defaultdata;

import java.awt.image.BufferedImage;
import java.util.List;

import commonclass.EditImage;

//その他のデータ
public class OtherData {
	//タイトル画像ファイル
	public static final String TITLE = "image/gacha/title.png";

	//ガチャ画像ファイル
	public static final String BALL = "image/gacha/ball full.png";
	public static final List<String> HALF_BALL = List.of("image/gacha/ball bottom.png", "image/gacha/ball top.png");
	public static final String HANDLE = "image/gacha/machine handle.png";
	public static final List<String> MACHINE = List.of("image/gacha/machine bottom.png", "image/gacha/machine top.png");
	public static final String TURN = "image/gacha/turn.png";
	public static final String EFFECT = "image/gacha/effect.png";
	
	//画像取込み
	public BufferedImage getTitleImage(double ratio) {
		return EditImage.input(TITLE, ratio);
	}

	public BufferedImage getBallImage(double ratio) {
		return EditImage.input(BALL, ratio);
	}

	public List<BufferedImage> getHalfBallImage(double ratio) {
		return EditImage.input(HALF_BALL, ratio);
	}

	public BufferedImage getHandleImage(double ratio) {
		return EditImage.input(HANDLE, ratio);
	}

	public List<BufferedImage> getMachineImage(double ratio) {
		return EditImage.input(MACHINE, ratio);
	}

	public BufferedImage getTurnImage(double ratio) {
		return EditImage.input(TURN, ratio);
	}

	public BufferedImage getEffectImage(double ratio) {
		return EditImage.input(EFFECT, ratio);
	}
}