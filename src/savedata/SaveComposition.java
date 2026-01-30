package savedata;

import static javax.swing.JOptionPane.*;

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
	private final String COMPOSITION_NAME = "all_composition";
	
	/**
	 * COMPOSITION_NAMEのテーブルの要素<br>
	 * 編成番号を格納したカラム名<br>
	 * PRIMARY KEY
	 */
	private final String NUMBER_COLUMN = "number";
	
	/**
	 * COMPOSITION_NAMEのテーブルの要素<br>
	 * 編成名とその編成を格納したテーブル名を表す文字列を格納したカラム名
	 */
	private final String NAME_COLUMN = "name";
	
	/**
	 * MySQLへの接続
	 */
	private Connection mysql;
	
	/**
	 * 全ての編成情報を保存。
	 * */
	private List<OneCompositionData> allCompositionList = new ArrayList<>();
	
	public SaveComposition() {
		mysql = FileCheck.connectMysql();
	}
	
	public void load() {
		allCompositionList.clear();
		String compositionLoad = "SELECT * FROM " + COMPOSITION_NAME;
		try(PreparedStatement compositionPrepared = mysql.prepareStatement(compositionLoad);
				ResultSet compositionTable = compositionPrepared.executeQuery()) {
			while (compositionTable.next()) {
				OneCompositionData newCompositionData = new OneCompositionData(mysql, compositionTable.getInt(NUMBER_COLUMN), compositionTable.getString(NAME_COLUMN));
				allCompositionList.add(newCompositionData);
				newCompositionData.load();
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void save() {
		allCompositionList.stream().forEach(i -> i.save());
	}
	
	public void newComposition(String name) {
		OneCompositionData newComposition = new OneCompositionData(mysql, getNextNumber(), name);
		if(newComposition.canCreateComposition(name)) {
			allCompositionList.add(newComposition);
			String addComposition = String.format("INSERT INTO %s (%s) VALUES (?)", COMPOSITION_NAME, NAME_COLUMN);
			try(PreparedStatement addPrepareed = mysql.prepareStatement(addComposition)) {
				addPrepareed.setString(1, name);
				addPrepareed.executeUpdate();
			}catch (Exception e) {
				e.printStackTrace();
			}
			return;
		}
		showMessageDialog(null, "編成名は無効です。");
	}
	
	public void removeComposition(int number) {
		String dropTable = String.format("DROP TABLE %s", getCompositionName(number));
		try(Statement dropStatement = mysql.createStatement()){
			dropStatement.executeUpdate(dropTable);
		}catch (Exception e) {
			e.printStackTrace();
			return;
		}
		String remove = String.format("DELETE FROM %s WHERE %s = ?", COMPOSITION_NAME, NUMBER_COLUMN);
		try(PreparedStatement removePrepared = mysql.prepareStatement(remove)){
			removePrepared.setInt(1, getNumber(number));
			removePrepared.executeUpdate();
		}catch (Exception e) {
			e.printStackTrace();
		}
		allCompositionList.remove(number);
	}
	
	public void rename(int number, String name) {
		String rename = String.format("RENAME TABLE %s TO %s", getCompositionName(number), name);
		try(Statement renameStatement = mysql.createStatement()) {
			renameStatement.executeUpdate(rename);
		}catch (Exception e) {
			e.printStackTrace();
			return;
		}
		setCompositionName(number, name);
	}
	
	public void swap(int select, int target) {
		String swap = String.format("UPDATE %s SET %s = ? WHERE %s = ?", COMPOSITION_NAME, NAME_COLUMN, NUMBER_COLUMN);
		try(PreparedStatement swapPrepared = mysql.prepareStatement(swap)) {
			String selectName = getCompositionName(select);
			String targetName = getCompositionName(target);
			swapPrepared.setString(1, targetName);
			swapPrepared.setInt(2, getNumber(select));
			swapPrepared.addBatch();
			swapPrepared.setString(1, selectName);
			swapPrepared.setInt(2, getNumber(target));
			swapPrepared.addBatch();
			swapPrepared.executeBatch();
			setCompositionName(getNumber(select), targetName);
			setCompositionName(getNumber(target), selectName);
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public List<OneCompositionData> getAllCompositionList(){
		return allCompositionList;
	}
	
	public List<String> getCompositionNameList(){
		return allCompositionList.stream().map(i -> i.getComposionName()).toList();
	}
	
	OneCompositionData getOneCompositionData(int number) {
		return allCompositionList.get(number);
	}
	
	String getCompositionName(int number) {
		return getOneCompositionData(number).getComposionName();
	}
	
	void setCompositionName(int number, String name) {
		getOneCompositionData(number).setComposionName(name);
	}
	
	int getNumber(int number) {
		return getOneCompositionData(number).getNumber();
	}
	
	int getNextNumber() {
		return allCompositionList.stream().mapToInt(i -> i.getNumber()).max().getAsInt() + 1;
	}
}