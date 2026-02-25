package defaultdata.enemy;

import java.awt.image.BufferedImage;
import java.util.List;

import commonclass.EditImage;
import defaultdata.Element;
import defaultdata.Move;
import defaultdata.Type;

public abstract class EnemyData {
	/**
	 * 敵の名称。
	 * @return 敵の名称を返却する。<br>
	 * 			最大字数は全角で14字。
	 */
	public abstract String getName();
	
	/**
	 * 敵の説明。<br>
	 * 所有するバフ・デバフを記載する。
	 * @return 敵の説明を返却する。<br>
	 * 			最大字数は全角で39字。
	 */
	public abstract String getExplanation();
	
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
	 * @return {@link defaultdata.Move Move}にある移動経路コードを返却する。
	 */
	public abstract Move getMove();
	
	/**
	 * 種別コード。
	 * @return {@link defaultdata.Type Type}にある種別コードを返却する。
	 */
	public abstract Type getType();
	
	/**
	 * 武器属性コード。
	 * @return 武器に付与される全ての属性コードを返却する。コードは{@link defaultdata.Element Element}参照。
	 */
	public abstract List<Element> getElement();
	
	/**
	 * 使用するアタックパターンコード。
	 * @return {@link defaultdata.AtackPattern#getAtackPattern AtackPattern}のコード番号を返却する。
	 */
	public abstract int getAtackPattern();
	
	/**
	 * 武器のステータス。
	 * @return {@link defaultdata.Atack Atack}の順にステータスをリスト化。
	 */
	public abstract List<Integer> getWeaponStatus();
	
	/**
	 * 敵のステータス。
	 * @return {@link defaultdata.EnemyUnit EnemyUnit}の順にステータスをリスト化。
	 */
	public abstract List<Integer> getUnitStatus();
	
	/**
	 * ダメージカット率。
	 * @return {@link defaultdata.Element Element}の順にステータスをリスト化。
	 */
	public abstract List<Integer> getCutStatus();
	
	/**
	 * 発生させるバフ情報。<br>
	 * バフ情報を入力した複数のListを返却する。
	 * @return List(timing, target, range, status, culculate, effect, interval, max, duration, recast, cost)<br>
	 * 			<br>
	 * 			timing - 発生させるタイミングコード。{@link defendthecastle.battle.Buff Buff}の発生タイミングコードで指定。Enemyでは{@link defendthecastle.battle.Buff#SKILL SKILL}使用不可。<br>
	 * 			target - 与える対象コード。{@link defendthecastle.battle.Buff Buff}の発生対象コードで指定。指定は敵側から見た敵味方である。<br>
	 * 			range - 与える範囲コード。{@link defendthecastle.battle.Buff Buff}の効果範囲コードで指定。<br>
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
