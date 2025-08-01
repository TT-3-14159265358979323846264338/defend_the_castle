package defaultdata.enemy;

import java.awt.image.BufferedImage;
import java.util.List;

import defaultdata.EditImage;

public abstract class EnemyData {
	//名前
	public abstract String getName();
	
	//通常時の画像ファイル名
	public abstract String getImageName();
	
	//通常時の画像
	public BufferedImage getImage(double ratio) {
		return EditImage.input(getImageName(), ratio);
	}
	
	//攻撃時の画像ファイル名
	public abstract List<String> getActionImageName();
	
	//攻撃時の画像
	public List<BufferedImage> getActionImage(double ratio) {
		return EditImage.input(getActionImageName(), ratio);
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
	
	//移動経路(DefaultEnemy.MOVE_MAP)で登録
	public abstract int getMove();
	
	//種別(DefaultEnemy.TYPE_MAP)で登録
	public abstract int getType();
	
	//武器属性はその武器の全ての属性(DefaultStage.ELEMENT_MAP)を登録
	public abstract List<Integer> getElement();
	
	//DefaultAtackPatternのパターン番号
	public abstract int getAtackPattern();
	
	//DefaultStage.WEAPON_MAPの順にステータスをリスト化
	public abstract List<Integer> getWeaponStatus();
	
	//DefaultStage.UNIT_MAPの順にステータスをリスト化
	public abstract List<Integer> getUnitStatus();
	
	//DefaultStage.ELEMENT_MAPの順にステータスをリスト化
	public abstract List<Integer> getCutStatus();
}
