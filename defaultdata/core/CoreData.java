package defaultdata.core;

import java.awt.image.BufferedImage;
import java.util.List;

import defaultdata.EditImage;

public abstract class CoreData {
	/**
	 * コアの名称。
	 * @return コアの名称を返却する。
	 */
	public abstract String getName();
	
	/**
	 * コアの説明。<br>
	 * 所有するバフ・デバフを記載する。
	 * @return コアの説明を返却する。<br>
	 * 			最大字数は全角で39字。
	 */
	public abstract String getExplanation();
	
	/**
	 * 通常時のコア画像ファイル名。
	 * @return コアを単独で表示する際の画像ファイル名を返却する。
	 */
	public abstract String getImageName();
	
	/**
	 * 通常時のコア画像。
	 * @param ratio - 元の画像を何倍の縮尺で取り込むか指定。
	 * @return コアを単独で表示する際の画像を返却する。
	 */
	public BufferedImage getImage(double ratio) {
		return EditImage.input(getImageName(), ratio);
	}
	
	/**
	 * 攻撃時のコア画像ファイル名。
	 * @return 武器と共に表示する際のコア画像ファイル名を返却する。
	 */
	public abstract String getActionImageName();
	
	/**
	 * 攻撃時のコア画像。
	 * @param ratio - 元の画像を何倍の縮尺で取り込むか指定。
	 * @return 武器と共に表示する際の画像を返却する。
	 */
	public BufferedImage getActionImage(double ratio) {
		return EditImage.input(getActionImageName(), ratio);
	}
	
	/**
	 * コアのレアリティ。
	 * @return コアのレアリティを返却する。1以上の値をとり、一定の値まで到達したら{@link screendisplay.SortPanel SortPanel}の表示位置を再調整すること。
	 */
	public abstract int getRarity();
	
	/**
	 * 装備した武器のステータス上昇率(倍率上昇)。
	 * @return {@link defaultdata.DefaultUnit#CORE_WEAPON_MAP CORE_WEAPON_MAP}の順にステータス上昇率をリスト化。
	 */
	public abstract List<Double> getWeaponStatus();
	
	/**
	 * ユニットのステータス上昇率(倍率上昇)。
	 * @return {@link defaultdata.DefaultUnit#CORE_UNIT_MAP CORE_UNIT_MAP}の順にステータス上昇率をリスト化。
	 */
	public abstract List<Double> getUnitStatus();
	
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
	 * 			timing - 発生させるタイミングコード。{@link battle.Buff Buff}の発生タイミングコードで指定。Coreでは{@link battle.Buff#HIT HIT}, {@link battle.Buff#KILL KILL}使用不可。<br>
	 * 			target - 与える対象コード。{@link battle.Buff Buff}の発生対象コードで指定。<br>
	 * 			range - 与える範囲コード。{@link battle.Buff Buff}の効果範囲コードで指定。timingが{@link battle.Buff#DEFEAT DEFEAT}なら{@link battle.Buff#ALL ALL}, {@link battle.TARGET TARGET}のみ有効<br>
	 * 			status - 効果のあるステータスコード。{@link battle.Buff Buff}の対象ステータスコードで指定。{@link battle.Buff#MORALE MORALE}, {@link battle.Buff#GAME_COST GAME_COST} を指定した場合、targetを{@link battle.Buff#GAME GAME}に指定する必要がある。<br>
	 * 			culculate - 最終ステータスへの計算方法コード。{@link battle.Buff Buff}の加減乗除コードで指定。targetが{@link battle.Buff#GAME GAME}であれば、{@link battle.Buff#MULTIPLICATION MULTIPLICATION}, {@link battle.Buff#DIVISION DIVISION}使用不可。<br>
	 * 			effect - 1回あたりの効果量。intervalを指定した際でも最大値ではないので注意。<br>
	 * 			interval - 効果の発生間隔[s]。未使用なら{@link battle.Buff#NONE Buff.NONE}を指定。<br>
	 * 			max - intervalを指定した時の最大値。未使用なら{@link battle.Buff#NONE Buff.NONE}を指定。<br>
	 * 			duration - 効果持続時間[s]。未使用なら{@link battle.Buff#NONE Buff.NONE}を指定。<br>
	 * 			recast - スキルの再使用時間[s]。timingが{@link battle.Buff#SKILL SKILL}の時のみ指定することがある。未使用なら{@link battle.Buff#NONE Buff.NONE}を指定。<br>
	 * 			cost - スキルに使用するコスト。未使用なら{@link battle.Buff#NONE Buff.NONE}を指定。<br>
	 * 			<br>
	 * 			バフを保有していない場合、空のArrays.asList()を返却する。
	 */
	public abstract List<List<Double>> getBuff();
	
	/**
	 * スキルのアイコン画像ファイル名。
	 * @return スキルを保有する場合、そのアイコン画像名を返却する。スキルを保有しないときはnullを指定する。
	 */
	public abstract String getSkillImageName();
	
	/**
	 * スキルのアイコン画像。
	 * @param ratio - 元の画像を何倍の縮尺で取り込むか指定。
	 * @return スキルのアイコン画像を返却する。スキルを保有しないときはnullを返却する。
	 */
	public BufferedImage getSkillImage(double ratio) {
		return EditImage.input(getSkillImageName(), ratio);
	}
}
