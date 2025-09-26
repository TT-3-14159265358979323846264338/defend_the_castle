package defaultdata.stage;

import java.awt.Point;
import java.awt.image.BufferedImage;
import java.lang.reflect.Method;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Stream;

import battle.BattleEnemy;
import battle.BattleFacility;
import battle.BattleUnit;
import battle.GameData;
import defaultdata.EditImage;
import savedata.SaveGameProgress;

public abstract class StageData {
	/**
	 * ステージの名称。
	 * @return ステージの名称を返却する。<br>
	 * 			半角英数字のみ使用可能。最大字数は半角12字。
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
	 * @return 全ての設備コードを返却する。コードは{@link defaultdata.DefaultStage#FACILITY_DATA_MAP FACILITY_DATA_MAP}参照。
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
	 * @return 全ての戦功内容を返却する。<br>
	 * 			内容の記載は、内容+難易度で入力。
	 * 			実際の表示では"("で改行が入るため"("の前のスペース禁止。
	 * 			最大字数は全角で76字とし、最大で4行に改行して表示可能。
	 */
	public abstract List<String> getMerit();
	
	/**
	 * 各戦功のクリア状況の判定。
	 * @param UnitMainData - ゲーム終了後のユニットデータ。{@link battle.BattleUnit BattleUnit}
	 * @param UnitLeftData - ゲーム終了後のユニットデータ。{@link battle.BattleUnit BattleUnit}
	 * @param FacilityData - ゲーム終了後の設備データ。{@link battle.BattleFacility BattleFacility}
	 * @param EnemyData - ゲーム終了後の敵データ。{@link battle.BattleEnemy BattleEnemy}
	 * @param GameData - ゲーム終了後のゲームデータ。{@link battle.GameData GameData}
	 * @param difficultyCorrection - ゲームの難易度。{@link battle.BattleEnemy#NORMAL_MODE ステータス補正倍率}
	 * @return 各戦功の達成状況のListを返却する。<br>
	 * 			達成した場合をtrue, 未達成の場合をfalseとする。<br>
	 * 			達成判定は{@link StageData}の下部で定義したメソッドを使用する。
	 */
	public abstract List<Boolean> canClearMerit(BattleUnit[] UnitMainData, BattleUnit[] UnitLeftData, BattleFacility[] FacilityData, BattleEnemy[] EnemyData, GameData GameData, double difficultyCorrection);
	
	/**
	 * 各戦功で獲得可能な報酬。
	 * @return 全ての報酬内容を返却する。<br>
	 * 			最大字数は全角6字。
	 */
	public abstract List<String> getReward();
	
	/**
	 * 戦功報酬を獲得する。<br>
	 * ステージのクリア報酬は一律でメダル200枚を獲得する。<br>
	 * これに加えて新規で獲得できた戦功報酬を獲得する。
	 * @param newClearList - 新規で獲得できた戦功をtrueとするList。<br>
	 * 						trueとなっている戦功のみ{@link #giveReward}を呼び出して報酬を受け取る。
	 */
	public void giveClearReward(List<Boolean> newClearList) {
		give200Medal();
		List<Method> methodList = giveReward();
		for(int i = 0; i < newClearList.size(); i++) {
			if(newClearList.get(i)) {
				try {
					methodList.get(i).invoke(this);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}
	/**
	 * 報酬を獲得するメソッド。<br>
	 * メソッドの引数設定及びスロー宣言してはならない。<br>
	 * 呼び出しは{@link #giveClearReward}からのみ行われる。
	 * @return 各戦功での報酬を獲得するメソッドを返却する。<br>
	 * 			メソッドリストに登録するメソッドは{@link StageData}の下部で定義し、getClass().getMethod()をリスト内に記載する。
	 */
	protected abstract List<Method> giveReward();
	
	/**
	 * 敵情報。<br>
	 * 全ての敵情報を入力した複数のListを返却する。
	 * @return List(enemyCode, moveCode, timing)<br>
	 * 			<br>
	 * 			enemyCode - {@link defaultdata.DefaultEnemy#DATA_MAP DATA_MAP}の敵コード。<br>
	 * 			moveCode - {@link #getRoute}の順番。<br>
	 * 			timing - 出撃タイミング(1000 = 1 seceond)。
	 */
	public abstract List<List<Integer>> getEnemy();
	
	/**
	 * 敵表示順。
	 * @return ステージ選択画面での敵表示順を返却する。{@link defaultdata.DefaultEnemy#DATA_MAP DATA_MAP}の敵コードで記載。同じコードの敵を重複して記載しないこと。
	 */
	public abstract List<Integer> getDisplayOrder();
	
	/**
	 * 移動情報。<br>
	 * 全ての移動情報を入力した複数のListを返却する。Listの順番が{@link #getEnemy}のmoveCodeに該当する。<br>
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
	
	/*
	 * 
	 * 
	 * 
	 * 
	 * 戦功クリア判定で使用するメソッド
	 * 
	 * 
	 * 
	 * 
	 */
	
	/**
	 * 難易度に関わらずゲームをクリアしたかどうか判定。
	 * @return 常にtrueを返却する。
	 */
	protected boolean canClearStage() {
		return true;
	}
	
	/**
	 * 指定の難易度でクリアしたかどうか判定。
	 * @param base - 基準となる指定の難易度。
	 * @param difficultyCorrection - 今回のゲームの難易度。
	 * @return 引数が一致すればtrueを返却する。
	 * @see {@link battle.BattleEnemy#NORMAL_MODE ステータス補正倍率}
	 */
	protected boolean canClearStage(double base, double difficultyCorrection) {
		return base == difficultyCorrection;
	}
	
	/**
	 * 難易度に関わらずユニットが一度も倒されずクリアしたかどうか判定。
	 * @param UnitMainData - ゲーム終了後のユニットデータ。
	 * @param UnitLeftData - ゲーム終了後のユニットデータ。
	 * @return {@link battle.BattleUnit#defeatNumber 被撃破数}が全て0であるならばtrueを返却する。
	 * @see {@link battle.BattleUnit BattleUnit}
	 */
	protected boolean canNotDefeat(BattleUnit[] UnitMainData, BattleUnit[] UnitLeftData) {
		Predicate<BattleUnit[]> canNotDefeatCheack = (data) -> {
			return Stream.of(data).noneMatch(i -> 0 < i.getDefeatNumber());
		};
		if(canNotDefeatCheack.test(UnitMainData)) {
			if(canNotDefeatCheack.test(UnitLeftData)) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * 指定の難易度でユニットが一度も倒されずクリアしたかどうか判定。
	 * @param base - 基準となる指定の難易度。
	 * @param difficultyCorrection - 今回のゲームの難易度。
	 * @param UnitMainData - ゲーム終了後のユニットデータ。
	 * @param UnitLeftData - ゲーム終了後のユニットデータ。
	 * @return 指定の難易度で{@link battle.BattleUnit#defeatNumber 被撃破数}が全て0であるならばtrueを返却する。
	 * @see {@link battle.BattleEnemy#NORMAL_MODE ステータス補正倍率}<br>
	 * 		{@link battle.BattleUnit BattleUnit}
	 */
	protected boolean canNotDefeat(double base, double difficultyCorrection, BattleUnit[] UnitMainData, BattleUnit[] UnitLeftData) {
		if(canClearStage(base, difficultyCorrection)) {
			return canNotDefeat(UnitMainData, UnitLeftData);
		}
		return false;
	}
	
	/**
	 * 難易度に関わらず味方全てが一度も倒されずクリアしたかどうか判定。
	 * @param UnitMainData - ゲーム終了後のユニットデータ。
	 * @param UnitLeftData - ゲーム終了後のユニットデータ。
	 * @param FacilityData - ゲーム終了後の設備データ。
	 * @return {@link battle.BattleData#canActivate 設備が生存}し、{@link battle.BattleUnit#defeatNumber ユニットの被撃破数}が全て0であるならばtrueを返却する。
	 * @see {@link battle.BattleUnit BattleUnit}<br>
	 * 		{@link battle.BattleFacility BattleFacility}
	 */
	protected boolean canNotDefeat(BattleUnit[] UnitMainData, BattleUnit[] UnitLeftData, BattleFacility[] FacilityData) {
		if(Stream.of(FacilityData).noneMatch(i -> !i.canActivate())) {
			return canNotDefeat(UnitMainData, UnitLeftData);
		}
		return false;
	}
	
	/**
	 * 指定の難易度で味方全てが一度も倒されずクリアしたかどうか判定。
	 * @param base - 基準となる指定の難易度。
	 * @param difficultyCorrection - 今回のゲームの難易度。
	 * @param UnitMainData - ゲーム終了後のユニットデータ。
	 * @param UnitLeftData - ゲーム終了後のユニットデータ。
	 * @param FacilityData - ゲーム終了後の設備データ。
	 * @return 指定の難易度で{@link battle.BattleData#canActivate 設備が生存}し、{@link battle.BattleUnit#defeatNumber ユニットの被撃破数}が全て0であるならばtrueを返却する。
	 * @see {@link battle.BattleEnemy#NORMAL_MODE ステータス補正倍率}<br>
	 * 		{@link battle.BattleUnit BattleUnit}<br>
	 * 		{@link battle.BattleFacility BattleFacility}
	 */
	protected boolean canNotDefeat(double base, double difficultyCorrection, BattleUnit[] UnitMainData, BattleUnit[] UnitLeftData, BattleFacility[] FacilityData) {
		if(canClearStage(base, difficultyCorrection)) {
			return canNotDefeat(UnitMainData, UnitLeftData, FacilityData);
		}
		return false;
	}
	
	/*
	 * 
	 * 
	 * 
	 * 
	 * 報酬獲得で使用するメソッド
	 * 
	 * 
	 * 
	 * 
	 */
	
	/**
	 * メダル100枚を獲得する。
	 */
	public void give100Medal() {
		giveMedal(100);
	}
	
	/**
	 * メダル200枚を獲得する。
	 */
	public void give200Medal() {
		giveMedal(200);
	}
	
	/**
	 * メダル300枚を獲得する。
	 */
	public void give300Medal() {
		giveMedal(300);
	}
	
	/**
	 * メダル500枚を獲得する。
	 */
	public void give500Medal() {
		giveMedal(500);
	}
	
	/**
	 * メダル1000枚を獲得する。
	 */
	public void give1000Medal() {
		giveMedal(1000);
	}
	
	/**
	 * メダルを獲得する。
	 * @param number - 獲得するメダル枚数。
	 */
	private void giveMedal(int number) {
		SaveGameProgress SaveGameProgress = new SaveGameProgress();
		SaveGameProgress.load();
		SaveGameProgress.save(SaveGameProgress.getClearStatus(), SaveGameProgress.getMeritStatus(), SaveGameProgress.getMedal() + number, SaveGameProgress.getSelectStage());
	}
}
