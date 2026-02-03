package savedata;

import java.util.ArrayList;
import java.util.List;

//現在保有しているアイテムの保存用
public class SaveHoldItem extends SQLOperation{
	/**
	 * データベース上で武器保有数を格納したテーブル名
	 */
	public static final String CORE_NAME = "core";
	
	/**
	 * データベース上で武器保有数を格納したテーブル名
	 */
	public static final String WEAPON_NAME = "weapon";
	
	/**
	 * 両テーブルの要素<br>
	 * 番号を格納したカラム名<br>
	 * PRIMARY KEY
	 */
	public static final String ID_COLUMN = "id";
	
	/**
	 * 両テーブルの要素<br>
	 * 保有数を格納したカラム名<br>
	 * PRIMARY KEY
	 */
	public static final String NUMBER_COLUMN = "number";
	
	/**
	 * 各コアの所持数。<br>
	 * {@link defaultdata.DefaultUnit#NORMAL_CORE コアコード変換}の順にリスト化。<br>
	 * このListのsizeは、{@link defaultdata.DefaultUnit#CORE_DATA_MAP CORE_DATA_MAP}に新規追加されると、{@link savedata.FileCheck FileCheck}で自動的に追加される。
	 */
	private List<Integer> coreNumberList = new ArrayList<>();
	
	/**
	 * 各武器の所持数。<br>
	 * {@link defaultdata.DefaultUnit#NO_WEAPON 武器コード変換}の順にリスト化。<br>
	 * このListのsizeは、{@link defaultdata.DefaultUnit#WEAPON_DATA_MAP WEAPON_DATA_MAP}に新規追加されると、{@link savedata.FileCheck FileCheck}で自動的に追加される。
	 */
	private List<Integer> weaponNumberList = new ArrayList<>();
	
	public void load() {
		operateSQL(() -> {
			dataLoad(CORE_NAME, NUMBER_COLUMN, coreNumberList);
			dataLoad(WEAPON_NAME, NUMBER_COLUMN, weaponNumberList);
		});
	}
	
	public void save() {
		operateSQL(() -> {
			dataSave(CORE_NAME, NUMBER_COLUMN, ID_COLUMN, coreNumberList);
			dataSave(WEAPON_NAME, NUMBER_COLUMN, ID_COLUMN, weaponNumberList);
		});
	}
	
	public List<Integer> getCoreNumberList(){
		return coreNumberList;
	}
	
	public void setCoreNumberList(List<Integer> coreNumberList) {
		this.coreNumberList = coreNumberList;
	}

	public List<Integer> getWeaponNumberList(){
		return weaponNumberList;
	}

	public void setWeaponNumberList(List<Integer> weaponNumberList) {
		this.weaponNumberList = weaponNumberList;
	}
}