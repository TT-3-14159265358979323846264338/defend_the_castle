package savedata;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

public class SaveSelect {
	/**
	 * データベース上で選択している編成番号を格納したテーブル名
	 */
	private final String SELECT_NAME = "select_target";
	
	/**
	 * SELECT_NAMEのテーブルの要素<br>
	 * どの項目に対する選択なのかを格納したカラム名<br>
	 * PRIMARY KEY
	 */
	private final String TARGET_COLUMN = "target";
	
	/**
	 * SELECT_NAMEのテーブルの要素<br>
	 * 現在選択されている編成番号を格納したカラム名
	 */
	private final String SELECT_COLUMN = "select";
	
	/**
	 * 編成選択用項目<br>
	 * TARGET_COLUMNで定義
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
	
	public SaveSelect() {
		mysql = FileCheck.connectMysql();
	}
	
	public void load(){
		selectList.clear();
		try {
			String selectLoad = "SELECT * FROM " + SELECT_NAME;
			ResultSet selectTable = mysql.prepareStatement(selectLoad).executeQuery();
			while (selectTable.next()) {
				selectList.add(selectTable.getInt(SELECT_COLUMN));
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void save() {
		try {
			String selectSave = String.format("UPDATE %s SET %s = ? WHERE %s = ?", SELECT_NAME, SELECT_COLUMN, TARGET_COLUMN);
			PreparedStatement selectPrepared = mysql.prepareStatement(selectSave);
			IntStream.range(0, selectList.size()).forEach(i -> {
				try {
					selectPrepared.setInt(1, selectList.get(i));
					selectPrepared.setInt(2, i + 1);
					selectPrepared.addBatch();
				}catch (Exception e) {
					e.printStackTrace();
				}
			});
			selectPrepared.executeBatch();
		}catch (Exception e) {
			e.printStackTrace();
		}
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