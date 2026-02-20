package defaultdata.weapon;

import java.awt.image.BufferedImage;
import java.util.List;

import commonclass.EditImage;

public abstract class WeaponData {
	/**
	 * 武器の名称。
	 * @return 武器の名称を返却する。<br>
	 * 			最大字数は全角で11字。
	 */
	public abstract String getName();
	
	/**
	 * 武器の説明。<br>
	 * 所有するバフ・デバフを記載する。
	 * @return 武器の説明を返却する。<br>
	 * 			最大字数は全角で39字。
	 */
	public abstract String getExplanation();
	
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
	 * 初期状態の武器画像(右手ver)。
	 * @param ratio - 元の画像を何倍の縮尺で取り込むか指定。
	 * @return 戦闘時の初期状態の武器画像を返却する。
	 */
	public BufferedImage defaultRightActionImage(double ratio) {
		return EditImage.input(getRightActionImageName().get(0), ratio);
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
	 * 初期状態の武器画像(左手ver)。
	 * @param ratio - 元の画像を何倍の縮尺で取り込むか指定。
	 * @return 戦闘時の初期状態の武器画像を返却する。
	 */
	public BufferedImage defaultLeftActionImage(double ratio) {
		return EditImage.input(getLeftActionImageName().get(0), ratio);
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
	 * @return 武器のレアリティを返却する。1以上の値をとり、一定の値まで到達したら{@link defendthecastle.screendisplay.SortPanel SortPanel}の表示位置を再調整すること。
	 */
	public abstract int getRarity();
	
	/**
	 * 配置可能なマスコード。
	 * @return {@link defaultdata.DefaultUnit#DISTANCE_MAP DISTANCE_MAP}の距離コードを返却する。
	 */
	public abstract int getDistance();
	
	/**
	 * 装備位置コード。
	 * @return {@link defaultdata.DefaultUnit#HANDLE_MAP HANDLE_MAP}の装備位置コードを返却する。
	 */
	public abstract int getHandle();
	
	/**
	 * 武器属性コード。
	 * @return 武器に付与される全ての属性コードを返却する。コードは{@link defaultdata.DefaultUnit#ELEMENT_MAP ELEMENT_MAP}参照。攻撃しない時は空のArrays.asList()を返却する。
	 */
	public abstract List<Integer> getElement();
	
	/**
	 * 使用するアタックパターンコード。
	 * @return {@link defaultdata.DefaultAtackPattern#getAtackPattern DefaultAtackPattern}のコード番号を返却する。
	 */
	public abstract int getAtackPattern();
	
	/**
	 * 装備した武器のステータス上昇量(加算上昇)。
	 * @return {@link defaultdata.DefaultUnit#WEAPON_WEAPON_MAP WEAPON_WEAPON_MAP}の順にステータス上昇量をリスト化。
	 */
	public abstract List<Integer> getWeaponStatus();
	
	/**
	 * ユニットのステータス上昇量(加算上昇)。
	 * @return {@link defaultdata.DefaultUnit#WEAPON_UNIT_MAP WEAPON_UNIT_MAP}の順にステータス上昇量をリスト化。
	 */
	public abstract List<Integer> getUnitStatus();
	
	/**
	 * ダメージカット率上昇量(加算上昇)。
	 * @return {@link defaultdata.DefaultUnit#ELEMENT_MAP ELEMENT_MAP}の順にステータス上昇量をリスト化。
	 */
	public abstract List<Integer> getCutStatus();
	
	/**
	 * 発生させるバフ情報。<br>
	 * バフ情報を入力した複数のListを返却する。
	 * @return List(timing, target, range, status, culculate, effect, interval, max, duration, recast, cost)<br>
	 * 			<br>
	 * 			timing - 発生させるタイミングコード。{@link defendthecastle.battle.Buff Buff}の発生タイミングコードで指定。Weaponでは{@link defendthecastle.battle.Buff#SKILL SKILL}, {@link defendthecastle.battle.Buff#DAMAGE DAMAGE}使用不可。<br>
	 * 			target - 与える対象コード。{@link defendthecastle.battle.Buff Buff}の発生対象コードで指定。<br>
	 * 			range - 与える範囲コード。{@link defendthecastle.battle.Buff Buff}の効果範囲コードで指定。timingが{@link defendthecastle.battle.Buff#DEFEAT DEFEAT}なら{@link defendthecastle.battle.Buff#ALL ALL}, {@link battle.TARGET TARGET}のみ有効<br>
	 * 			status - 効果のあるステータスコード。{@link defendthecastle.battle.Buff Buff}の対象ステータスコードで指定。{@link defendthecastle.battle.Buff#MORALE MORALE}, {@link defendthecastle.battle.Buff#GAME_COST GAME_COST} を指定した場合、targetを{@link defendthecastle.battle.Buff#GAME GAME}に指定する必要がある。<br>
	 * 			culculate - 最終ステータスへの計算方法コード。{@link defendthecastle.battle.Buff Buff}の加減乗除コードで指定。targetが{@link defendthecastle.battle.Buff#GAME GAME}であれば、{@link defendthecastle.battle.Buff#MULTIPLICATION MULTIPLICATION}, {@link defendthecastle.battle.Buff#DIVISION DIVISION}使用不可。<br>
	 * 			effect - 1回あたりの効果量。intervalを指定した際でも最大値ではないので注意。<br>
	 * 			interval - 効果の発生間隔[s]。未使用なら{@link defendthecastle.battle.Buff#NONE Buff.NONE}を指定。<br>
	 * 			max - intervalを指定した時の最大値。未使用なら{@link defendthecastle.battle.Buff#NONE Buff.NONE}を指定。<br>
	 * 			duration - 効果持続時間[s]。未使用なら{@link defendthecastle.battle.Buff#NONE Buff.NONE}を指定。<br>
	 * 			recast - 必ず{@link defendthecastle.battle.Buff#NONE Buff.NONE}を指定。<br>
	 * 			cost - 必ず{@link defendthecastle.battle.Buff#NONE Buff.NONE}を指定。<br>
	 * 			<br>
	 * 			バフを保有していない場合、空のArrays.asList()を返却する。
	 */
	public abstract List<List<Double>> getBuff();
}
