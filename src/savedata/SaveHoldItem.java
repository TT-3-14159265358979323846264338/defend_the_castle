package savedata;

import static savedata.OperationSQL.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

//現在保有しているアイテムの保存用
public class SaveHoldItem{
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
		Connection mysql = connectMysql();
		executeSQL(mysql, () -> {
			coreNumberList.clear();
			loadData(mysql, CORE_NAME, coreNumberList);
			weaponNumberList.clear();
			loadData(mysql, WEAPON_NAME, weaponNumberList);
		});
		closeConnection(mysql);
	}
	
	void loadData(Connection mysql, String tableName, List<Integer> numberList) throws Exception {
		String dataLoad = String.format("SELECT * FROM %s", tableName);
		try(PreparedStatement prepared = mysql.prepareStatement(dataLoad);
				ResultSet table = prepared.executeQuery()){
			while(table.next()) {
				numberList.add(table.getInt(NUMBER_COLUMN));
			}
		}
	}
	
	public void save() {
		Connection mysql = connectMysql();
		executeSQL(mysql, () -> {
			dataSave(mysql, CORE_NAME, coreNumberList);
			dataSave(mysql, WEAPON_NAME, weaponNumberList);
		});
		closeConnection(mysql);
	}
	
	void dataSave(Connection mysql, String tableName, List<Integer> numberList) throws Exception{
		String dataSave = String.format("UPDATE %s SET %s = ? WHERE %s = ?", tableName, NUMBER_COLUMN, ID_COLUMN);
		try(PreparedStatement dataPrepared = mysql.prepareStatement(dataSave)) {
			for(int i = 0; i < numberList.size(); i++) {
				dataPrepared.setInt(1, numberList.get(i));
				dataPrepared.setInt(2, i + 1);
				dataPrepared.addBatch();
			}
			dataPrepared.executeBatch();
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