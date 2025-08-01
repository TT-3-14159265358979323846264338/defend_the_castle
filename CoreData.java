package defaultdata.core;

import java.awt.image.BufferedImage;
import java.util.List;

import defaultdata.EditImage;

public abstract class CoreData {
	//名前
	public abstract String getName();
	
	//通常時の画像ファイル名
	public abstract String getImageName();
	
	//通常時の画像
	public BufferedImage getImage(double ratio) {
		return EditImage.input(getImageName(), ratio);
	}
	
	//攻撃時の画像ファイル名
	public abstract String getActionImageName();
	
	//攻撃時の画像
	public BufferedImage getActionImage(double ratio) {
		return EditImage.input(getActionImageName(), ratio);
	}
	
	//レアリティ
	public abstract int getRarity();
	
	//DefaultUnit.CORE_WEAPON_MAPの順にステータスをリスト化
	public abstract List<Double> getWeaponStatus();
	
	//DefaultUnit.CORE_UNIT_MAPの順にステータスをリスト化
	public abstract List<Double> getUnitStatus();
	
	//DefaultUnit.ELEMENT_MAPの順にステータスをリスト化
	public abstract List<Integer> getCutStatus();
}
