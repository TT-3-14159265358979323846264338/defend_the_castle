package savedata;

import static javax.swing.JOptionPane.*;
import static savedata.OperationSQL.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

//現在の編成状況の保存用
public class SaveComposition{
	/**
	 * データベース上で全編成を格納したテーブル名
	 */
	public static final String COMPOSITION_NAME = "all_composition";
	
	/**
	 * COMPOSITION_NAMEのテーブルの要素<br>
	 * 編成番号を格納したカラム名<br>
	 * PRIMARY KEY
	 */
	public static final String ID_COLUMN = "id";
	
	/**
	 * COMPOSITION_NAMEのテーブルの要素<br>
	 * 編成名とその編成を格納したテーブル名を表す文字列を格納したカラム名
	 */
	public static final String NAME_COLUMN = "name";
	
	/**
	 * MySQLへの接続
	 */
	private Connection mysql;
	
	/**
	 * 全ての編成情報を保存。
	 * */
	private List<OneCompositionData> allCompositionList = new ArrayList<>();
	
	/**
	 * このインスタンスを作成した場合、終了時に必ず{@link #close}を呼び出すこと。
	 */
	public SaveComposition() {
		mysql = connectMysql();
	}
	
	public void close() {
		closeConnection(mysql);
	}
	
	public void load() {
		executeSQL(mysql, () -> {
			allCompositionList.clear();
			String compositionLoad = String.format("SELECT * FROM %s", COMPOSITION_NAME);
			try(PreparedStatement compositionPrepared = mysql.prepareStatement(compositionLoad);
					ResultSet compositionTable = compositionPrepared.executeQuery()) {
				while (compositionTable.next()) {
					OneCompositionData newCompositionData = new OneCompositionData(mysql, compositionTable.getInt(ID_COLUMN), compositionTable.getString(NAME_COLUMN));
					allCompositionList.add(newCompositionData);
					newCompositionData.load();
				}
			}
		});
	}
	
	public void save() {
		executeSQL(mysql, () -> {
			for(OneCompositionData i: allCompositionList) {
				i.save();
			}
		});
	}
	
	public void newComposition(String name) {
		executeSQL(mysql, () -> {
			try{
				OneCompositionData newComposition = new OneCompositionData(mysql, getNextNumber(), name);
				newComposition.canCreateComposition(name);
				String addComposition = String.format("INSERT INTO %s (%s) VALUES (?)", COMPOSITION_NAME, NAME_COLUMN);
				try(PreparedStatement addPrepareed = mysql.prepareStatement(addComposition)) {
					addPrepareed.setString(1, name);
					addPrepareed.executeUpdate();
				}
				allCompositionList.add(newComposition);
			}catch (Exception e) {
				showMessageDialog(null, "編成名は無効です。");
				throw e;
			}
		});
	}
	
	public void removeComposition(int index) {
		executeSQL(mysql, () -> {
			String dropTable = String.format("DROP TABLE %s", getCompositionName(index));
			try(Statement dropStatement = mysql.createStatement()){
				dropStatement.executeUpdate(dropTable);
			}
			String remove = String.format("DELETE FROM %s WHERE %s = ?", COMPOSITION_NAME, ID_COLUMN);
			try(PreparedStatement removePrepared = mysql.prepareStatement(remove)){
				removePrepared.setInt(1, getNumber(index));
				removePrepared.executeUpdate();
			}
			allCompositionList.remove(index);
		});
	}
	
	public void rename(int index, String name) {
		String rename = String.format("RENAME TABLE %s TO %s", getCompositionName(index), name);
		try(Statement renameStatement = mysql.createStatement()) {
			renameStatement.executeUpdate(rename);
		}catch (Exception e) {
			showMessageDialog(null, "編成名は無効です。");
			return;
		}
		setCompositionName(index, name);
	}
	
	public void swap(int selectIndex, int targetIndex) {
		executeSQL(mysql, () -> {
			String swap = String.format("UPDATE %s SET %s = ? WHERE %s = ?", COMPOSITION_NAME, NAME_COLUMN, ID_COLUMN);
			try(PreparedStatement swapPrepared = mysql.prepareStatement(swap)) {
				String selectName = getCompositionName(selectIndex);
				String targetName = getCompositionName(targetIndex);
				swapPrepared.setString(1, targetName);
				swapPrepared.setInt(2, getNumber(selectIndex));
				swapPrepared.addBatch();
				swapPrepared.setString(1, selectName);
				swapPrepared.setInt(2, getNumber(targetIndex));
				swapPrepared.addBatch();
				swapPrepared.executeBatch();
				setCompositionName(getNumber(selectIndex), targetName);
				setCompositionName(getNumber(targetIndex), selectName);
			}
		});
	}
	
	public List<String> getCompositionNameList(){
		return allCompositionList.stream().map(i -> i.getComposionName()).toList();
	}

	public List<OneCompositionData> getAllCompositionList() {
		return allCompositionList;
	}
	
	public OneCompositionData getOneCompositionData(int index) {
		return allCompositionList.get(index);
	}
	
	String getCompositionName(int index) {
		return getOneCompositionData(index).getComposionName();
	}
	
	void setCompositionName(int index, String name) {
		getOneCompositionData(index).setComposionName(name);
	}
	
	int getNumber(int index) {
		return getOneCompositionData(index).getID();
	}
	
	int getNextNumber() {
		return allCompositionList.stream().mapToInt(i -> i.getID()).max().getAsInt() + 1;
	}
}