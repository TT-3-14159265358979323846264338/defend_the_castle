package savedata;

import static javax.swing.JOptionPane.*;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.stream.IntStream;

import defaultdata.DefaultUnit;

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
				OneCompositionData newCompositionData = new OneCompositionData(mysql, compositionTable.getString(NAME_COLUMN));
				allCompositionList.add(newCompositionData);
				newCompositionData.load();
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void save() {
		String compositionSave = String.format("UPDATE %s SET %s = ? WHERE %s = ?", COMPOSITION_NAME, NAME_COLUMN, NUMBER_COLUMN);
		try(PreparedStatement compositionPrepared = mysql.prepareStatement(compositionSave)) {
			IntStream.range(0, allCompositionList.size()).forEach(i -> {
				try {
					compositionPrepared.setString(1, allCompositionList.get(i).getNewComposionName());
					compositionPrepared.setInt(2, i + 1);
					compositionPrepared.addBatch();
					allCompositionList.get(i).save();
				}catch (Exception e) {
					e.printStackTrace();
				}
			});
			compositionPrepared.executeBatch();
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void newComposition(String name) {
		OneCompositionData newComposition = new OneCompositionData(mysql, name);
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
		allCompositionList.remove(number);
		compositionNameList.remove(number);
	}
	
	public List<OneCompositionData> getAllCompositionList(){
		return allCompositionList;
	}
	
	public List<String> getCompositionNameList(){
		return allCompositionList.stream().map(i -> i.getNewComposionName()).toList();
	}
}