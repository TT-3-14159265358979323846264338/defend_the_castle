package savedata;

import static savedata.OperationSQL.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class SaveSelect {
	/**
	 * データベース上で選択している編成番号を格納したテーブル名
	 */
	public static final String SELECT_NAME = "select_target";
	
	/**
	 * SELECT_NAMEのテーブルの要素<br>
	 * どの項目に対する選択なのかを格納したカラム名<br>
	 * PRIMARY KEY
	 */
	public static final String ID_COLUMN = "id";
	
	/**
	 * SELECT_NAMEのテーブルの要素<br>
	 * 現在選択されている編成番号を格納したカラム名
	 */
	public static final String SELECT_COLUMN = "select_code";
	
	/**
	 * 編成選択用項目<br>
	 * ID_COLUMNで定義
	 */
	private final int COMPOSITION_SELECT = 0;
	
	/**
	 * ステージ選択用項目<br>
	 * TARGET_COLUMNで定義
	 */
	private final int STAGE_SELECT = 1;
	
	/**
	 * MySQLへの接続
	 */
	private Connection mysql;
	
	/**
	 * 全ての選択情報を保存
	 */
	private List<Integer> selectList = new ArrayList<>();
	
	/**
	 * このインスタンスを作成した場合、終了時に必ず{@link #close}を呼び出すこと。
	 */
	public SaveSelect() {
		mysql = OperationSQL.connectMysql();
	}
	
	public void close() {
		closeConnection(mysql);
	}
	
	public void load(){
		executeSQL(mysql, () -> {
			selectList.clear();
			String selectLoad = "SELECT * FROM " + SELECT_NAME;
			try(PreparedStatement selectPrepared = mysql.prepareStatement(selectLoad);
					ResultSet selectTable = selectPrepared.executeQuery()) {
				while (selectTable.next()) {
					selectList.add(selectTable.getInt(SELECT_COLUMN));
				}
			}
		});
	}
	
	public void save() {
		executeSQL(mysql, () -> {
			String selectSave = String.format("UPDATE %s SET %s = ? WHERE %s = ?", SELECT_NAME, SELECT_COLUMN, ID_COLUMN);
			try(PreparedStatement selectPrepared = mysql.prepareStatement(selectSave)) {
				for(int i = 0; i < selectList.size(); i++) {
					selectPrepared.setInt(1, selectList.get(i));
					selectPrepared.setInt(2, i + 1);
					selectPrepared.addBatch();
				}
				selectPrepared.executeBatch();
			}
		});
	}
	
	public int getCompositionSelectNumber() {
		return selectList.get(COMPOSITION_SELECT);
	}
	
	public void setCompositionSelectNumber(int number) {
		selectList.set(COMPOSITION_SELECT, number);
	}
	
	public int getStageSelectNumber() {
		return selectList.get(STAGE_SELECT);
	}
	
	public void setStageSelectNumber(int number) {
		selectList.set(STAGE_SELECT, number);
	}
}