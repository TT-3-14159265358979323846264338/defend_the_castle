package defaultdata.facility;

import java.awt.image.BufferedImage;
import java.util.List;

import defaultdata.EditImage;

public abstract class FacilityData {
	/**
	 * 設備の名称。
	 * @return 設備の名称を返却する。
	 */
	public abstract String getName();
	
	/**
	 * 攻撃時の設備画像ファイル名(正面ver)。
	 * @return 正面からの設備画像ファイル名を返却する。Listの1つ目は待機時の画像ファイル名。それ以外は攻撃時のモーション画像ファイル名を登録する。
	 * 			攻撃しない設備の場合、Listには1つ目のみ登録する。
	 */
	public abstract List<String> getActionFrontImageName();
	
	/**
	 * 攻撃時の設備画像ファイル名(横側ver)。
	 * @return 横側からの設備画像ファイル名を返却する。Listの1つ目は待機時の画像ファイル名。それ以外は攻撃時のモーション画像ファイル名を登録する。
	 * 			攻撃しない設備の場合、Listには1つ目のみ登録する。
	 */
	public abstract List<String> getActionSideImageName();
	
	/**
	 * 攻撃時の設備画像(正面ver)。
	 * @param ratio - 元の画像を何倍の縮尺で取り込むか指定。
	 * @return 正面からの設備画像を返却する。Listの1つ目は待機時の画像。それ以外は攻撃時のモーション画像を登録されている。
	 * 			攻撃しない設備の場合、Listには1つ目のみ登録されている。
	 */
	public List<BufferedImage> getActionFrontImage(double ratio) {
		return EditImage.input(getActionFrontImageName(), ratio);
	}
	
	/**
	 * 攻撃時の設備画像(横側ver)。
	 * @param ratio - 元の画像を何倍の縮尺で取り込むか指定。
	 * @return 横側からの設備画像を返却する。Listの1つ目は待機時の画像。それ以外は攻撃時のモーション画像を登録されている。
	 * 			攻撃しない設備の場合、Listには1つ目のみ登録されている。
	 */
	public List<BufferedImage> getActionSideImage(double ratio) {
		return EditImage.input(getActionSideImageName(), ratio);
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
	 * 破損時画像ファイル名。
	 * @return 設備が破損した時の画像ファイル名を返却する。
	 */
	public abstract String getBreakImageName();
	
	/**
	 * 破損時画像。
	 * @param ratio - 元の画像を何倍の縮尺で取り込むか指定。
	 * @return 設備が破損した時の画像を返却する。
	 */
	public BufferedImage getBreakImage(double ratio) {
		return EditImage.input(getBreakImageName(), ratio);
	}
	
	/**
	 * 武器属性コード。
	 * @return 武器に付与される全ての属性コードを返却する。コードは{@link defaultdata.DefaultStage#ELEMENT_MAP ELEMENT_MAP}参照。攻撃しない時は空のArrays.asList()を返却する。
	 */
	public abstract List<Integer> getElement();
	
	/**
	 * 使用するアタックパターンコード。
	 * @return {@link defaultdata.DefaultAtackPattern#getAtackPattern DefaultAtackPattern}のコード番号を返却する。攻撃しない時は0を返却する。。
	 */
	public abstract int getAtackPattern();
	
	/**
	 * 武器のステータス。
	 * @return {@link defaultdata.DefaultStage#WEAPON_MAP WEAPON_MAP}の順にステータスをリスト化。攻撃しない時は空のArrays.asList()を返却する。
	 */
	public abstract List<Integer> getWeaponStatus();
	
	/**
	 * 設備のステータス。
	 * @return {@link defaultdata.DefaultStage#UNIT_MAP UNIT_MAP}の順にステータスをリスト化。足止め数∞の時は-1を指定する。
	 */
	public abstract List<Integer> getUnitStatus();
	
	/**
	 * ダメージカット率。
	 * @return {@link defaultdata.DefaultStage#ELEMENT_MAP ELEMENT_MAP}の順にステータスをリスト化。
	 */
	public abstract List<Integer> getCutStatus();
	
	/**
	 * 発生させるバフ情報。<br>
	 * バフ情報を入力した複数のListを返却する。
	 * @return List(timing, target, range, status, culculate, effect, interval, max, duration, recast, cost)<br>
	 * 			<br>
	 * 			timing - 発生させるタイミングコード。{@link battle.Buff Buff}の発生タイミングコードで指定。Facilityでは{@link battle.Buff#SKILL SKILL}使用不可。<br>
	 * 			target - 与える対象コード。{@link battle.Buff Buff}の発生対象コードで指定。<br>
	 * 			range - 与える範囲コード。{@link battle.Buff Buff}の効果範囲コードで指定。<br>
	 * 			status - 効果のあるステータスコード。{@link battle.Buff Buff}の対象ステータスコードで指定。{@link battle.Buff#MORALE MORALE}, {@link battle.Buff#GAME_COST GAME_COST} を指定した場合、targetを{@link battle.Buff#GAME GAME}に指定する必要がある。<br>
	 * 			culculate - 最終ステータスへの計算方法コード。{@link battle.Buff Buff}の加減乗除コードで指定。targetが{@link battle.Buff#GAME GAME}であれば、{@link battle.Buff#MULTIPLICATION MULTIPLICATION}, {@link battle.Buff#DIVISION DIVISION}使用不可。<br>
	 * 			effect - 1回あたりの効果量。intervalを指定した際でも最大値ではないので注意。<br>
	 * 			interval - 効果の発生間隔[s]。未使用なら{@link battle.Buff#NONE Buff.NONE}を指定。<br>
	 * 			max - intervalを指定した時の最大値。未使用なら{@link battle.Buff#NONE Buff.NONE}を指定。<br>
	 * 			duration - 効果持続時間[s]。未使用なら{@link battle.Buff#NONE Buff.NONE}を指定。<br>
	 * 			recast - 必ず{@link battle.Buff#NONE Buff.NONE}を指定。<br>
	 * 			cost - 必ず{@link battle.Buff#NONE Buff.NONE}を指定。<br>
	 * 			<br>
	 * 			バフを保有していない場合、空のArrays.asList()を返却する。
	 */
	public abstract List<List<Double>> getBuff();
}
