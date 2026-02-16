package defendthecastle.commoninheritance;

import java.awt.image.BufferedImage;
import java.util.List;

import defaultdata.DefaultUnit;
import defaultdata.weapon.WeaponData;

public abstract class CommonImage {
	/**
	 * 指定の縮尺倍率で通常時のコア画像を取り込む。
	 * @param ratio - 元の画像を何倍の縮尺で取り込むか指定。
	 * @return 通常時のコア画像リストを返却する。
	 */
	protected List<BufferedImage> createNormalCoreImage(double ratio){
		return DefaultUnit.CORE_DATA_MAP.values().stream().map(i -> i.getImage(ratio)).toList();
	}
	
	/**
	 * 指定の縮尺倍率で通常時の武器画像を取り込む。
	 * @param ratio - 元の画像を何倍の縮尺で取り込むか指定。
	 * @return 通常時の武器画像リストを返却する。
	 */
	protected List<BufferedImage> createNormalWeaponImage(double ratio){
		return DefaultUnit.WEAPON_DATA_MAP.values().stream().map(i -> i.getImage(ratio)).toList();
	}
	
	/**
	 * 指定の縮尺倍率で戦闘時の右武器初期画像を取り込む。
	 * @param ratio - 元の画像を何倍の縮尺で取り込むか指定。
	 * @return 戦闘時の右武器初期画像リストを返却する。
	 */
	protected List<BufferedImage> createBattleRightWeaponImage(double ratio){
		return DefaultUnit.WEAPON_DATA_MAP.values().stream().map(i -> rightWeaponImage(i, ratio)).toList();
	}
	
	BufferedImage rightWeaponImage(WeaponData data, double ratio) {
		return data.getRightActionImageName().isEmpty()? null: data.defaultRightActionImage(ratio);
	}
	
	/**
	 * 指定の縮尺倍率で戦闘時のコア画像を取り込む。
	 * @param ratio - 元の画像を何倍の縮尺で取り込むか指定。
	 * @return 戦闘時のコア画像リストを返却する。
	 */
	protected List<BufferedImage> createBattleCoreImage(double ratio){
		return DefaultUnit.CORE_DATA_MAP.values().stream().map(i -> i.getActionImage(ratio)).toList();
	}
	
	/**
	 * 指定の縮尺倍率で戦闘時の左武器初期画像を取り込む。
	 * @param ratio - 元の画像を何倍の縮尺で取り込むか指定。
	 * @return 戦闘時の左武器初期画像リストを返却する。
	 */
	protected List<BufferedImage> createBattleLeftWeaponImage(double ratio){
		return DefaultUnit.WEAPON_DATA_MAP.values().stream().map(i -> i.defaultLeftActionImage(ratio)).toList();
	}
}