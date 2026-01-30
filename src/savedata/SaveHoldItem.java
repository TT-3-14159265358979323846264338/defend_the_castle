package savedata;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

//現在保有しているアイテムの保存用
public class SaveHoldItem{
	/**
	 * データベース上で武器保有数を格納したテーブル名
	 */
	private final String CORE_NAME = "core";
	
	/**
	 * データベース上で武器保有数を格納したテーブル名
	 */
	private final String WEAPON_NAME = "weapon";
	
	/**
	 * 両テーブルの要素<br>
	 * 番号を格納したカラム名<br>
	 * PRIMARY KEY
	 */
	private final String ID_COLUMN = "id";
	
	/**
	 * 両テーブルの要素<br>
	 * 保有数を格納したカラム名<br>
	 * PRIMARY KEY
	 */
	private final String NUMBER_COLUMN = "number";
	
	/**
	 * MySQLへの接続
	 */
	private Connection mysql;
	
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
	
	public SaveHoldItem() {
		mysql = FileCheck.connectMysql();
	}
	
	public void load() {
		coreNumberList.clear();
		loadData(CORE_NAME, coreNumberList);
		weaponNumberList.clear();
		loadData(WEAPON_NAME, weaponNumberList);
	}
	
	void loadData(String tableName, List<Integer> numberList) {
		String dataLoad = String.format("SELECT * FROM %s", tableName);
		try(PreparedStatement prepared = mysql.prepareStatement(dataLoad);
				ResultSet table = prepared.executeQuery()){
			while(table.next()) {
				numberList.add(table.getInt(NUMBER_COLUMN));
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void save() {
		saveData(CORE_NAME, coreNumberList);
		saveData(WEAPON_NAME, weaponNumberList);
	}
	
	void saveData(String tableName, List<Integer> numberList) {
		String dataSave = String.format("UPDATE %s SET %s = ? WHERE %s = ?", tableName, NUMBER_COLUMN, ID_COLUMN);
		try(PreparedStatement dataPrepared = mysql.prepareStatement(dataSave)) {
			IntStream.range(0, numberList.size()).forEach(i -> {
				try {
					dataPrepared.setInt(1, numberList.get(i));
					dataPrepared.setInt(2, i);
					dataPrepared.addBatch();
				}catch (Exception e) {
					e.printStackTrace();
				}
			});
			dataPrepared.executeBatch();
		}catch (Exception e) {
			e.printStackTrace();
		}
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