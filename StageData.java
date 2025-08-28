package defaultdata.stage;

import java.awt.Point;
import java.awt.image.BufferedImage;
import java.util.List;

import defaultdata.EditImage;

public abstract class StageData {
	/**
	 * ステージの名称。
	 * @return ステージの名称を返却する。
	 */
	public abstract String getName();
	
	/**
	 * ステージ画像ファイル名。
	 * @return ステージ画像ファイル名を返却する。
	 */
	public abstract String getImageName();
	
	/**
	 * ステージ画像。
	 * @param ratio - 元の画像を何倍の縮尺で取り込むか指定。
	 * @return ステージ画像を返却する。
	 */
	public BufferedImage getImage(double ratio) {
		return EditImage.input(getImageName(), ratio);
	}
	
	/**
	 * 設備の種類コード
	 * @return 全ての設備コードを返却する。コードはDefaultStage参照。
	 */
	public abstract List<Integer> getFacility();
	
	/**
	 * 設備の方向コード。
	 * @return 各設備の方向コードを返却する。正面をtrue, 横側をfalseとする。
	 */
	public abstract List<Boolean> getFacilityDirection();
	
	/**
	 * 設備の位置。
	 * @return 各設備の位置を返却する。
	 */
	public abstract List<Point> getFacilityPoint();
	
	/**
	 * ユニットの配可能置位置。
	 * @return ユニットの配可能置位置を返却する。Listはnear, far, allの順番に、x座標, y座標を入れたList(Double)を登録する。
	 * 			ステージ中心点は
	 * 			double centerX = 483;
	 * 			double centerY = 265; であり、1マスの大きさを
	 * 			double size = 29.5; として計算すると位置調整が容易となる。
	 */
	public abstract List<List<List<Double>>> getPlacementPoint();
	
	/**
	 * 初期コスト。
	 * @return 初期コストを返却する。
	 */
	public abstract int getCost();
	
	/**
	 * 初期士気。
	 * @return 味方, 敵の順に初期士気をリスト化。
	 */
	public abstract List<Integer> getMorale();
	
	/**
	 * 戦功内容。
	 * @return 全ての戦功内容を返却する。内容の記載は、内容+難易度で入力。実際の表示では"("で改行が入るため"("の前のスペース禁止。
	 */
	public abstract List<String> getMerit();
	
	/**
	 * 敵情報。<br>
	 * 全ての敵情報を入力した複数のListを返却する。
	 * @return List(enemyCode, moveCode, timing)<br>
	 * 			<br>
	 * 			enemyCode - DefaultEnemyの敵コード。<br>
	 * 			moveCode - getRoute()の順番。<br>
	 * 			timing - 出撃タイミング(1000 = 1 seceond)。
	 */
	public abstract List<List<Integer>> getEnemy();
	
	/**
	 * 敵表示順。
	 * @return ステージ選択画面での敵表示順を返却する。DefaultEnemyの敵コードで記載。同じコードの敵を重複して記載しないこと。
	 */
	public abstract List<Integer> getDisplayOrder();
	
	/**
	 * 移動情報。<br>
	 * 全ての移動情報を入力した複数のListを返却する。Listの順番がgetEnemy()のmoveCodeに該当する。<br>
	 * 各moveCodeには移動方法の順番にListを入力する。
	 * @return List(initialX, initialY, angle, stopTime, noDisplayTime)<br>
	 * 			<br>
	 * 			initialX - 初期x座標。initialX, initialYのいずれかが次のListの初期座標に到達すると、その移動方法に移る。<br>
	 * 			initialY - 初期y座標。initialX, initialYのいずれかが次のListの初期座標に到達すると、その移動方法に移る。<br>
	 * 			angle - 移動方向角[°]。右を0°, 下を90°とする。<br>
	 * 			stopTime - 停止時間(停止中の描写回数)。<br>
	 * 			noDisplayTime - 描写中止時間 (停止中の描写回数)。
	 */
	public abstract List<List<List<Integer>>> getRoute();
}
