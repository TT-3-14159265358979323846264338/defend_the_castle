package commoninheritance;

import java.awt.image.BufferedImage;
import java.util.List;
import java.util.stream.IntStream;

import defaultdata.DefaultUnit;

public abstract class CommonImage {
	/**
	 * 指定の縮尺倍率で通常時のコア画像を取り込む。
	 * @param ratio - 元の画像を何倍の縮尺で取り込むか指定。
	 * @return 通常時のコア画像リストを返却する。
	 */
	protected List<BufferedImage> createNormalCoreImage(double ratio){
		return IntStream.range(0, DefaultUnit.CORE_DATA_MAP.size()).mapToObj(i -> DefaultUnit.CORE_DATA_MAP.get(i).getImage(ratio)).toList();
	}
	
	/**
	 * 指定の縮尺倍率で通常時の武器画像を取り込む。
	 * @param ratio - 元の画像を何倍の縮尺で取り込むか指定。
	 * @return 通常時の武器画像リストを返却する。
	 */
	protected List<BufferedImage> createNormalWeaponImage(double ratio){
		return IntStream.range(0, DefaultUnit.WEAPON_DATA_MAP.size()).mapToObj(i -> DefaultUnit.WEAPON_DATA_MAP.get(i).getImage(ratio)).toList();
	}
	
	/**
	 * 指定の縮尺倍率で戦闘時の右武器初期画像を取り込む。
	 * @param ratio - 元の画像を何倍の縮尺で取り込むか指定。
	 * @return 戦闘時の右武器初期画像リストを返却する。
	 */
	protected List<BufferedImage> createBattleRightWeaponImage(double ratio){
		return IntStream.range(0, DefaultUnit.WEAPON_DATA_MAP.size()).mapToObj(i -> rightWeaponImage(i, ratio)).toList();
	}
	
	BufferedImage rightWeaponImage(int index, double ratio) {
		return DefaultUnit.WEAPON_DATA_MAP.get(index).getRightActionImageName().isEmpty()? null: DefaultUnit.WEAPON_DATA_MAP.get(index).defaultRightActionImage(ratio);
	}
	
	/**
	 * 指定の縮尺倍率で戦闘時のコア画像を取り込む。
	 * @param ratio - 元の画像を何倍の縮尺で取り込むか指定。
	 * @return 戦闘時のコア画像リストを返却する。
	 */
	protected List<BufferedImage> createBattleCoreImage(double ratio){
		return IntStream.range(0, DefaultUnit.CORE_DATA_MAP.size()).mapToObj(i -> DefaultUnit.CORE_DATA_MAP.get(i).getActionImage(ratio)).toList();
	}
	
	/**
	 * 指定の縮尺倍率で戦闘時の左武器初期画像を取り込む。
	 * @param ratio - 元の画像を何倍の縮尺で取り込むか指定。
	 * @return 戦闘時の左武器初期画像リストを返却する。
	 */
	protected List<BufferedImage> createBattleLeftWeaponImage(double ratio){
		return IntStream.range(0, DefaultUnit.WEAPON_DATA_MAP.size()).mapToObj(i -> DefaultUnit.WEAPON_DATA_MAP.get(i).defaultLeftActionImage(ratio)).toList();
	}
}