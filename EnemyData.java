package defaultdata.enemy;

import java.awt.image.BufferedImage;
import java.util.List;

import defaultdata.EditImage;

public abstract class EnemyData {
	/**
	 * 敵の名称。
	 * @return 敵の名称を返却する。
	 */
	public abstract String getName();
	
	/**
	 * 通常時の敵画像ファイル名。
	 * @return 敵を単独で表示する際の画像ファイル名を返却する。
	 */
	public abstract String getImageName();
	
	/**
	 * 通常時の敵画像。
	 * @param ratio - 元の画像を何倍の縮尺で取り込むか指定。
	 * @return 敵を単独で表示する際の画像を返却する。
	 */
	public BufferedImage getImage(double ratio) {
		return EditImage.input(getImageName(), ratio);
	}
	
	/**
	 * 攻撃時の敵画像ファイル名。
	 * @return 戦闘時の敵画像ファイル名を返却する。Listの1つ目は移動時の画像ファイル名。それ以外は攻撃時のモーション画像ファイル名を登録する。
	 */
	public abstract List<String> getActionImageName();
	
	/**
	 * 攻撃時の敵画像ファイル。
	 * @param ratio - 元の画像を何倍の縮尺で取り込むか指定。
	 * @return 戦闘時の敵画像を返却する。Listの1つ目は移動時の画像。それ以外は攻撃時のモーション画像が登録されている。
	 */
	public List<BufferedImage> getActionImage(double ratio) {
		return EditImage.input(getActionImageName(), ratio);
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
	 * 移動経路コード。
	 * @return DefaultEnemy.MOVE_MAPにある移動経路コードを返却する。
	 */
	public abstract int getMove();
	
	/**
	 * 種別コード。
	 * @return DefaultEnemy.TYPE_MAPにある種別コードを返却する。
	 */
	public abstract int getType();
	
	/**
	 * 武器属性コード。
	 * @return 武器に付与される全ての属性コードを返却する。コードはDefaultEnemy.ELEMENT_MAP参照。攻撃しない時は空のArrays.asList()を返却する。
	 */
	public abstract List<Integer> getElement();
	
	/**
	 * 使用するアタックパターンコード。
	 * @return DefaultAtackPatternのコード番号を返却する。
	 */
	public abstract int getAtackPattern();
	
	/**
	 * 武器のステータス。
	 * @return DefaultEnemy.WEAPON_MAPの順にステータスをリスト化。
	 */
	public abstract List<Integer> getWeaponStatus();
	
	/**
	 * 敵のステータス。
	 * @return DefaultEnemy.UNIT_MAPの順にステータスをリスト化。
	 */
	public abstract List<Integer> getUnitStatus();
	
	/**
	 * ダメージカット率。
	 * @return DefaultEnemy.ELEMENT_MAPの順にステータスをリスト化。
	 */
	public abstract List<Integer> getCutStatus();
	
	/**
	 * 発生させるバフ情報。<br>
	 * バフ情報を入力した複数のListを返却する。
	 * @return List(timing, target, range, status, culculate, effect, interval, max, duration, recast)<br>
	 * 			<br>
	 * 			timing - 発生させるタイミングコード。Buff.発生タイミングコードで指定。EnemyではSKILL使用不可。<br>
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
