package defaultdata.facility;

import java.awt.image.BufferedImage;
import java.util.List;

import defaultdata.EditImage;

public abstract class FacilityData {
	//名前
	public abstract String getName();
	
	
	//攻撃時の画像ファイル名
	public abstract List<String> getActionFrontImageName();
	public abstract List<String> getActionSideImageName();
	
	//攻撃時の画像
	public List<BufferedImage> getActionFrontImage(double ratio) {
		return EditImage.input(getActionFrontImageName(), ratio);
	}
	public List<BufferedImage> getActionSideImage(double ratio) {
		return EditImage.input(getActionSideImageName(), ratio);
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
	
	//破損時画像ファイル名
	public abstract String getBreakImageName();
	
	//破損時画像
	public BufferedImage getBreakImage(double ratio) {
		return EditImage.input(getBreakImageName(), ratio);
	}
	
	//武器属性はその武器の全ての属性(DefaultStage.ELEMENT_MAP)を登録　攻撃しない時は空のlist
	public abstract List<Integer> getElement();
	
	//DefaultAtackPatternのパターン番号
	public abstract int getAtackPattern();
	
	//DefaultStage.WEAPON_MAPの順にステータスをリスト化　攻撃しない時は空のlist
	public abstract List<Integer> getWeaponStatus();
	
	//DefaultStage.UNIT_MAPの順にステータスをリスト化　足止め数∞の時は-1
	public abstract List<Integer> getUnitStatus();
	
	//DefaultStage.ELEMENT_MAPの順にステータスをリスト化
	public abstract List<Integer> getCutStatus();
	
	/*
	発生タイミングコード, 
	発生対象コード, 
	効果範囲コード, 
	対象ステータスコード, 
	加減乗除コード, 
	効果量, 
	効果発生間隔[s](Buff.NONE: なし), 
	上限量(Buff.NONE: なし), 
	効果時間[s](Buff.NONE: 無限), 
	再使用時間[s](Buff.NONE: なし)
	
		の順にリスト化
	*/
	public abstract List<List<Double>> getBuff();
}
