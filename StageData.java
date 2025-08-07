package defaultdata.stage;

import java.awt.Point;
import java.awt.image.BufferedImage;
import java.util.List;

import defaultdata.EditImage;

public abstract class StageData {
	//ステージの名前
	public abstract String getName();
	
	//ステージ画像ファイル名
	public abstract String getImageName();
	
	//ステージ画像
	public BufferedImage getImage(double ratio) {
		return EditImage.input(getImageName(), ratio);
	}
	
	//設備の種類番号
	public abstract List<Integer> getFacility();
	
	//設備の向き　前向きをtrue, 横向きをfalse
	public abstract List<Boolean> getFacilityDirection();
	
	//設備の位置
	public abstract List<Point> getFacilityPoint();
	
	//配置位置　near, far, allの順にList<Point>を追加
	public abstract List<List<List<Double>>> getPlacementPoint();
	
	//初期コスト
	public abstract int getCost();
	
	//初期士気(味方, 敵 の順にリスト化)
	public abstract List<Integer> getMorale();
	
	//戦功内容　実際の表示では"("で改行が入るため"("の前のスペース禁止
	public abstract List<String> getMerit();
	
	//敵情報　各敵番号, 移動番号, 出撃タイミング(1000 = 1 seceond) の順にリスト化
	public abstract List<List<Integer>> getEnemy();
	
	//敵情報表示順
	public abstract List<Integer> getDisplayOrder();
	
	/*移動情報　①List: 移動番号, 
				②List: 移動経路(初期位置x (x, yのいずれかが次の設定値になると次のrouteへ),
								初期位置y (x, yのいずれかが次の設定値になると次のrouteへ),
								移動方向角 (右を0°, 下を90°とする),
								停止時間 (停止中の描写回数),
								描写中止時間 (停止中の描写回数))
																	の順にリスト化
	*/
	public abstract List<List<List<Integer>>> getRoute();
}
