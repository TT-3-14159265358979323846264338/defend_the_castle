package commonclass;

import java.awt.image.BufferedImage;
import java.util.List;
import java.util.stream.Stream;

import defaultdata.Core;
import defaultdata.Weapon;
import defaultdata.core.CoreData;
import defaultdata.weapon.WeaponData;

public abstract class CommonImage {
	/**
	 * 指定の縮尺倍率で通常時のコア画像を取り込む。
	 * @param ratio - 元の画像を何倍の縮尺で取り込むか指定。
	 * @return 通常時のコア画像リストを返却する。
	 */
	protected List<BufferedImage> createNormalCoreImage(double ratio){
		return allCoreDataStream().map(i -> i.getImage(ratio)).toList();
	}
	
	/**
	 * 全てのコアデータを取り込む。
	 * @return コアデータを格納したstreamを返却する。
	 */
	protected Stream<CoreData> allCoreDataStream(){
		return Stream.of(Core.values()).map(i -> i.getLabel());
	}
	
	/**
	 * 指定の縮尺倍率で通常時の武器画像を取り込む。
	 * @param ratio - 元の画像を何倍の縮尺で取り込むか指定。
	 * @return 通常時の武器画像リストを返却する。
	 */
	protected List<BufferedImage> createNormalWeaponImage(double ratio){
		return allWeaponDataStream().map(i -> i.getImage(ratio)).toList();
	}
	
	/**
	 * 全ての武器データを取り込む。
	 * @return 武器データを格納したstreamを返却する。
	 */
	protected Stream<WeaponData> allWeaponDataStream(){
		return Stream.of(Weapon.values()).map(i -> i.getLabel());
	}
	
	/**
	 * 指定の縮尺倍率で戦闘時の右武器初期画像を取り込む。
	 * @param ratio - 元の画像を何倍の縮尺で取り込むか指定。
	 * @return 戦闘時の右武器初期画像リストを返却する。両手武器の場合はnullとなる。
	 */
	protected List<BufferedImage> createBattleRightWeaponImage(double ratio){
		return allWeaponDataStream().map(i -> rightWeaponImage(i, ratio)).toList();
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
		return allCoreDataStream().map(i -> i.getActionImage(ratio)).toList();
	}
	
	/**
	 * 指定の縮尺倍率で戦闘時の左武器初期画像を取り込む。
	 * @param ratio - 元の画像を何倍の縮尺で取り込むか指定。
	 * @return 戦闘時の左武器初期画像リストを返却する。
	 */
	protected List<BufferedImage> createBattleLeftWeaponImage(double ratio){
		return allWeaponDataStream().map(i -> i.defaultLeftActionImage(ratio)).toList();
	}
}