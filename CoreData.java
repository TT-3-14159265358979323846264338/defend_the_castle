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
	
	//スキルのアイコン画像ファイル名
	public abstract String getSkillImageName();
	
	//スキルのアイコン画像
	public BufferedImage getSkillImage(double ratio) {
		return EditImage.input(getSkillImageName(), ratio);
	}
}
