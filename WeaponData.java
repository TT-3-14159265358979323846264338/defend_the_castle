package defaultdata.weapon;

import java.awt.image.BufferedImage;
import java.util.List;

import defaultdata.EditImage;

public abstract class WeaponData {
	/**
	 * 武器の名称。
	 * @return 武器の名称を返却する。
	 */
	public abstract String getName();
	
	/**
	 * 通常時の武器画像ファイル名。
	 * @return 武器を単独で表示する際の画像ファイル名を返却する。
	 */
	public abstract String getImageName();
	
	/**
	 * 通常時の武器画像。
	 * @param ratio - 元の画像を何倍の縮尺で取り込むか指定。
	 * @return 武器を単独で表示する際の画像を返却する。
	 */
	public BufferedImage getImage(double ratio) {
		return EditImage.input(getImageName(), ratio);
	}
	
	/**
	 * 攻撃時の武器画像ファイル名(右手ver)。
	 * @return コアと共に表示する際の武器画像ファイル名を返却する。片手武器の時は空のArrays.asList()を入れる
	 */
	public abstract List<String> getRightActionImageName();
	
	/**
	 * 攻撃時の武器画像ファイル名(左手ver)。
	 * @return コアと共に表示する際の武器画像ファイル名を返却する。
	 */
	public abstract List<String> getLeftActionImageName();
	
	/**
	 * 攻撃時の武器画像(右手ver)。
	 * @param ratio - 元の画像を何倍の縮尺で取り込むか指定。
	 * @return コアと共に表示する際の画像を返却する。
	 */
	public List<BufferedImage> getRightActionImage(double ratio) {
		return EditImage.input(getRightActionImageName(), ratio);
	}
	
	/**
	 * 攻撃時の武器画像(左手ver)。
	 * @param ratio - 元の画像を何倍の縮尺で取り込むか指定。
	 * @return コアと共に表示する際の画像を返却する。
	 */
	public List<BufferedImage> getLeftActionImage(double ratio) {
		return EditImage.input(getLeftActionImageName(), ratio);
	}
	
	/**
	 * 弾丸の画像ファイル名。
	 * @return 攻撃時に弾丸を飛ばすことがあれば、その画像ファイル名を返却する。なければnullを返却する。
	 */
	public abstract String getBulletImageName();
	
	/**
	 * 弾丸の画像。
	 * @param ratio - 元の画像を何倍の縮尺で取り込むか指定。
	 * @return 攻撃時に弾丸を飛ばすことがあれば、その画像を返却する。なければnullを返却する。
	 */
	public BufferedImage getBulletImage(double ratio) {
		return EditImage.input(getBulletImageName(), ratio);
	}
	
	/**
	 * ヒット画像ファイル名。
	 * @return 攻撃がヒットした時に表示する画像ファイル名を返却する。Listに入っている画像ファイルがヒットモーションとして表示される。なければ空のArrays.asList()を返却する。
	 */
	public abstract List<String> getHitImageName();
	
	/**
	 * ヒット画像。
	 * @param ratio - 元の画像を何倍の縮尺で取り込むか指定。
	 * @return 攻撃がヒットした時に表示する画像を返却する。Listに入っている画像がヒットモーションとして表示される。なければ空のListを返却する。
	 */
	public List<BufferedImage> getHitImage(double ratio){
		return EditImage.input(getHitImageName(), ratio);
	}
	
	/**
	 * 武器のレアリティ。
	 * @return 武器のレアリティを返却する。1以上の値をとり、一定の値まで到達したらDisplaySortの表示位置を再調整すること。
	 */
	public abstract int getRarity();
	
	/**
	 * 配置可能なマスコード。
	 * @return DefaultUnit.DISTANCE_MAPの距離コードを返却する。
	 */
	public abstract int getDistance();
	
	/**
	 * 装備位置コード。
	 * @return DefaultUnit.HANDLE_MAPの装備位置コードを返却する。
	 */
	public abstract int getHandle();
	
	/**
	 * 武器属性コード。
	 * @return 武器に付与される全ての属性コードを返却する。コードはDefaultUnit.ELEMENT_MAP参照。
	 */
	public abstract List<Integer> getElement();
	
	/**
	 * 使用するアタックパターンコード。
	 * @return DefaultAtackPatternのコード番号を返却する。
	 */
	public abstract int getAtackPattern();
	
	/**
	 * 装備した武器のステータス上昇量(加算上昇)。
	 * @return DefaultUnit.WEAPON_WEAPON_MAPの順にステータス上昇量をリスト化。
	 */
	public abstract List<Integer> getWeaponStatus();
	
	/**
	 * ユニットのステータス上昇量(加算上昇)。
	 * @return DefaultUnit.WEAPON_UNIT_MAPの順にステータス上昇量をリスト化。
	 */
	public abstract List<Integer> getUnitStatus();
	
	/**
	 * ダメージカット率上昇量(加算上昇)。
	 * @return DefaultUnit.ELEMENT_MAPの順にステータス上昇量をリスト化。
	 */
	public abstract List<Integer> getCutStatus();
	
	/**
	 * 発生させるバフ情報。<br>
	 * バフ情報を入力した複数のListを返却する。
	 * @return List(timing, target, range, status, culculate, effect, interval, max, duration, recast)<br>
	 * 			<br>
	 * 			timing - 発生させるタイミングコード。Buff.発生タイミングコードで指定。WeaponではSKILL使用不可。<br>
	 * 			target - 与える対象コード。Buff.発生対象コードで指定。指定は敵側から見た敵味方である。<br>
	 * 			range - 与える範囲コード。Buff.効果範囲コードで指定。<br>
	 * 			status - 効果のあるステータスコード。Buff.対象ステータスコードで指定。MORALE, GAME_COST を指定した場合、targetをGAMEに指定する必要がある。<br>
	 * 			culculate - 最終ステータスへの計算方法コード。Buff.加減乗除コードで指定。targetがGAMEであれば、MULTIPLICATION, DIVISION使用不可。<br>
	 * 			effect - 1回あたりの効果量。intervalを指定した際でも最大値ではないので注意。<br>
	 * 			interval - 効果の発生間隔[s]。未使用ならBuff.NONEを指定。<br>
	 * 			max - intervalを指定した時の最大値。未使用ならBuff.NONEを指定。<br>
	 * 			duration - 効果持続時間[s]。未使用ならBuff.NONEを指定。<br>
	 * 			recast - 必ずBuff.NONEを指定。<br>
	 * 			<br>
	 * 			バフを保有していない場合、空のArrays.asList()を返却する。
	 */
	public abstract List<List<Double>> getBuff();
}
