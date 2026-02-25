package defaultdata.gacha;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import defaultdata.Core;
import defaultdata.Weapon;
import savedata.SaveGameProgress;

public abstract class GachaData {
	/**
	 * コアのラインナップリスト。
	 * {@link defaultdata.Core Core}のコアコード変換番号を格納したリストを、{@link #addCore addCore}メソッドを用いて作成する。
	 */
	private List<Core> coreLineup = new ArrayList<>();
	
	/**
	 * コアの排出確率リスト。
	 * {@link #coreLineup coreLineup}の順番に排出確率を格納したリストを、{@link #addCore addCore}メソッドを用いて作成する。
	 */
	private List<Double> coreRatio = new ArrayList<>();
	
	/**
	 * 武器のラインナップリスト。
	 * {@link defaultdata.Weapon Weapon}の武器コード変換番号を格納したリストを、{@link #addWeapon addWeapon}メソッドを用いて作成する。
	 */
	private List<Weapon> weaponLineup = new ArrayList<>();
	
	/**
	 * 武器の排出確率リスト。
	 * {@link #weaponLineup weaponLineup}の順番に排出確率を格納したリストを、{@link #addWeapon addWeapon}メソッドを用いて作成する。
	 */
	private List<Double> weaponRatio = new ArrayList<>();
	
	/**
	 * このガチャで排出されるリストを作成する。
	 */
	public GachaData() {
		createLineup();
		aptitudeTest();
	}
	
	/**
	 * ガチャ名。
	 * @return ガチャ名を返却する。
	 */
	public abstract String getName();
	
	/**
	 * このガチャが有効であるか判定する。{@link GachaData#hasClearedMerit GachaData}の下部で定義したメソッドを使用する。
	 * @param saveGameProgress - 現在のゲームクリア状況。
	 * @return このガチャが有効であればtrueを返却する。
	 */
	public abstract boolean canActivate(SaveGameProgress saveGameProgress);
	
	/**
	 * このガチャで排出されるリストを作成する。<br>
	 * コアの排出リストと確率は{@link #addCore addCore}、武器の排出リストと確率は{@link #addWeapon addWeapon}メソッドを用いてリストを作成する。
	 * addCoreとaddWeaponに指定する確率の合計は100にならなければならない。
	 * このメソッドはコンストラクタにて1度のみしか呼び出してはならない。
	 */
	protected abstract void createLineup();
	
	/**
	 * コアのガチャリストを作成する。<br>
	 * lineupSetを{@link #coreLineup coreLineup}に追加し、totalRatioをlineupSetの要素数で割った値を{@link #coreRatio coreRatio}に追加する。
	 * @param lineupSet - {@link GachaData#CORE_SET_1 GachaData}の下部で定義したガチャで使用するコアラインナップのいずれかを指定する。
	 * @param totalRatio - lineupSetの合計排出確率を指定する。例) 合計確率が50%であれば50を入力する。
	 */
	protected void addCore(List<Core> lineupSet, double totalRatio) {
		coreLineup.addAll(lineupSet);
		coreRatio.addAll(getRatioList(lineupSet.size(), totalRatio));
	}
	
	/**
	 * 武器のガチャリストを作成する。<br>
	 * lineupSetを{@link #weaponLineup weaponLineup}に追加し、totalRatioをlineupSetの要素数で割った値を{@link #weaponRatio weaponRatio}に追加する。
	 * @param lineupSet - {@link GachaData#CORE_SET_1 GachaData}の下部で定義したガチャで使用する武器ラインナップのいずれかを指定する。
	 * @param totalRatio - lineupSetの合計排出確率を指定する。例) 合計確率が50%であれば50を入力する。
	 */
	protected void addWeapon(List<Weapon> lineupSet, double totalRatio) {
		weaponLineup.addAll(lineupSet);
		weaponRatio.addAll(getRatioList(lineupSet.size(), totalRatio));
	}
	
	/**
	 * coreRatioとweaponRatioに追加する確率リストを作成する。
	 * @param size - lineupSetの要素数。
	 * @param totalRatio - lineupSetの合計排出確率。
	 * @return 合計排出確率を要素数で割った値を格納した要素数sizeのリストを返却する。
	 */
	List<Double> getRatioList(int size, double totalRatio){
		return IntStream.range(0, size).mapToObj(_ -> (double) (totalRatio / size)).toList();
	}
	
	/**
	 * このガチャリストの合計確率が100%でない場合、メッセージを表示する。
	 */
	void aptitudeTest() {
		double sum = coreRatio.stream().mapToDouble(Double::doubleValue).sum() + weaponRatio.stream().mapToDouble(Double::doubleValue).sum();
		if(Math.round(sum) != 100) {
			System.out.println("ガチャモード「" + getName() + "」は使用できません");
		}
	}
	
	public List<Core> getCoreLineup(){
		return coreLineup;
	}
	
	public List<Double> getCoreRatio(){
		return coreRatio;
	}
	
	public List<Weapon> getWeaponLineup(){
		return weaponLineup;
	}
	
	public List<Double> getWeaponRatio(){
		return weaponRatio;
	}
	
	/*
	 * 
	 * 
	 * ガチャのラインナップリスト
	 * 
	 * 
	 */
	protected List<Core> CORE_SET_1(){
		return List.of(Core.ATACK_CORE, 
				Core.DEFENCE_CORE, 
				Core.RANGE_CORE, 
				Core.HEAL_CORE, 
				Core.SPEED_CORE);
	}
	
	protected List<Weapon> WEAPON_SET_1(){
		return List.of(Weapon.SWORD, 
				Weapon.BOW, 
				Weapon.SMALL_SHIELD, 
				Weapon.FIRST_AID_KIT, 
				Weapon.FLAME_ROD, 
				Weapon.WIND_CUTTER);
	}
	
	/*
	 * 
	 * 
	 * ガチャの有効判定に使用するメソッド
	 * 
	 * 
	 */
	
	/**
	 * 指定の範囲内のステージで戦功クリア数が一定以上であるか判定する。
	 * @param saveGameProgress - 現在のゲームクリア状況。
	 * @param minStageNumber - 検索する最小ステージ番号。ステージ番号は{@link defaultdata.Stage Stage}のリスト順である。このステージ番号を含む。
	 * @param maxStageNumber - 検索する最大ステージ番号。ステージ番号は{@link defaultdata.Stage Stage}のリスト順である。このステージ番号を含む。
	 * @param clearCount - このガチャが有効になる最低戦功クリア数。-1を指定すると半数以上を表す。
	 * @return 戦功クリア数が一定以上であればtrueを返却する。
	 */
	boolean hasClearedMerit(SaveGameProgress saveGameProgress, int minStageNumber, int maxStageNumber, int clearCount) {
		List<List<Boolean>> meritStatus = IntStream.range(0, saveGameProgress.getMeritStatus().size())
											.filter(i -> minStageNumber <= i && i <= maxStageNumber)
											.mapToObj(i -> saveGameProgress.getMeritData(i).getMeritClearList())
											.toList();
		if(clearCount == -1) {
			clearCount = (1 + meritStatus.stream().mapToInt(i -> i.size()).sum()) / 2;
		}
		return clearCount <= meritStatus.stream().mapToLong(i -> i.stream().filter(j -> j).count()).sum();
	}
	
	/**
	 * 指定の範囲内の全てのステージをクリアしているか判定する。
	 * @param saveGameProgress - 現在のゲームクリア状況。
	 * @param minStageNumber - 検索する最小ステージ番号。ステージ番号は{@link defaultdata.Stage Stage}のリスト順である。このステージ番号を含む。
	 * @param maxStageNumber - 検索する最大ステージ番号。ステージ番号は{@link defaultdata.Stage Stage}のリスト順である。このステージ番号を含む。
	 * @return 範囲内の全てのステージがクリア済みならtrueを返却する。
	 */
	boolean hasClearedStage(SaveGameProgress saveGameProgress, int minStageNumber, int maxStageNumber) {
		return 0 == IntStream.range(0, saveGameProgress.getMeritStatus().size())
						.filter(i -> minStageNumber <= i && i <= maxStageNumber)
						.mapToObj(i -> saveGameProgress.getStageStatus().get(i))
						.filter(i -> !i)
						.count();
	}
}