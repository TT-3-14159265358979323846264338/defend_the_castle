package defaultdata.weapon;

import java.awt.image.BufferedImage;
import java.util.List;

import defaultdata.EditImage;

public abstract class WeaponData {
	//名前
	public abstract String getName();
	
	//通常時の画像ファイル名
	public abstract String getImageName();
	
	//通常時の画像
	public BufferedImage getImage(double ratio) {
		return EditImage.input(getImageName(), ratio);
	}
	
	//攻撃時の画像ファイル名
	//片手武器の時は右武器のlistにEmptyのlistを入れる
	public abstract List<String> getRightActionImageName();
	public abstract List<String> getLeftActionImageName();
	
	//攻撃時の画像
	public List<BufferedImage> getRightActionImage(double ratio) {
		return EditImage.input(getRightActionImageName(), ratio);
	}
	public List<BufferedImage> getLeftActionImage(double ratio) {
		return EditImage.input(getLeftActionImageName(), ratio);
	}
	
	//弾丸の画像ファイル名
	public abstract String getBulletImageName();
	
	//弾丸の画像
	public BufferedImage getBulletImage(double ratio) {
		return EditImage.input(getBulletImageName(), ratio);
	}
	
	//ヒット画像ファイル名
	public abstract List<String> getHitImageName();
	
	//ヒット画像
	public List<BufferedImage> getHitImage(double ratio){
		return EditImage.input(getHitImageName(), ratio);
	}
	
	//レアリティ
	public abstract int getRarity();
	
	//距離(DefaultUnit.DISTANCE_MAP)で登録
	public abstract int getDistance();
	
	//装備位置(DefaultUnit.HANDLE_MAP)で登録
	public abstract int getHandle();
	
	//武器属性はその武器の全ての属性(DefaultUnit.ELEMENT_MAP)を登録
	public abstract List<Integer> getElement();
	
	//DefaultAtackPatternのパターン番号
	public abstract int getAtackPattern();
	
	//DefaultUnit.WEAPON_WEAPON_MAPの順にステータスをリスト化
	public abstract List<Integer> getWeaponStatus();
	
	//DefaultUnit.WEAPON_UNIT_MAPの順にステータスをリスト化
	public abstract List<Integer> getUnitStatus();
	
	//DefaultUnit.ELEMENT_MAPの順にステータスをリスト化
	public abstract List<Integer> getCutStatus();
}
