package savedata;

import static javax.swing.JOptionPane.*;

import java.util.ArrayList;
import java.util.List;

//現在の編成状況の保存用
public class SaveComposition extends SQLOperation{
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
	 * 全ての編成情報を保存。
	 * */
	private List<OneCompositionData> allCompositionList = new ArrayList<>();
	
	public void load() {
		operateSQL(mysql -> {
			operateResultSet(mysql, COMPOSITION_NAME, result -> {
				allCompositionList.clear();
				while (result.next()) {
					OneCompositionData newCompositionData = new OneCompositionData(result.getInt(ID_COLUMN), result.getString(NAME_COLUMN));
					allCompositionList.add(newCompositionData);
					newCompositionData.load(mysql);
				}
			});
		});
	}
	
	public void save() {
		operateSQL(mysql -> {
			for(OneCompositionData i: allCompositionList) {
				i.save(mysql);
			}
		});
	}
	
	public void newComposition(String name) {
		operateSQL(mysql -> {
			try{
				OneCompositionData newComposition = new OneCompositionData(getNextNumber(), name);
				newComposition.canCreateComposition(mysql, name);
				String addComposition = String.format("INSERT INTO %s (%s) VALUES (?)", COMPOSITION_NAME, NAME_COLUMN);
				operatePrepared(mysql, addComposition, prepared -> {
					prepared.setString(1, name);
					prepared.executeUpdate();
				});
				allCompositionList.add(newComposition);
			}catch (Exception e) {
				showMessageDialog(null, "編成名は無効です。");
				throw e;
			}
		});
	}
	
	public void removeComposition(int index) {
		operateSQL(mysql -> {
			String dropTable = String.format("DROP TABLE %s", getCompositionName(index));
			operateStatement(mysql, dropTable);
			String remove = String.format("DELETE FROM %s WHERE %s = ?", COMPOSITION_NAME, ID_COLUMN);
			operatePrepared(mysql, remove, prepared -> {
				prepared.setInt(1, getNumber(index));
				prepared.executeUpdate();
			});
			allCompositionList.remove(index);
		});
	}
	
	public void rename(int index, String name) {
		operateSQL(mysql -> {
			try{
				String renameTable = String.format("RENAME TABLE %s TO %s", getCompositionName(index), name);
				operateStatement(mysql, renameTable);
			}catch (Exception e) {
				showMessageDialog(null, String.format("【%s】という編成名は既にあるか無効です。", name));
			}
			operatePrepared(mysql, createRenemaCore(), prepared -> {
				prepared.setString(1, name);
				prepared.setInt(2, getNumber(index));
				prepared.executeUpdate();
			});
			setCompositionName(index, name);
		});
	}
	
	public void swap(int selectIndex, int targetIndex) {
		operateSQL(mysql -> {
			operatePrepared(mysql, createRenemaCore(), prepared -> {
				String selectName = getCompositionName(selectIndex);
				String targetName = getCompositionName(targetIndex);
				prepared.setString(1, targetName);
				prepared.setInt(2, getNumber(selectIndex));
				prepared.addBatch();
				prepared.setString(1, selectName);
				prepared.setInt(2, getNumber(targetIndex));
				prepared.addBatch();
				prepared.executeBatch();
				setCompositionName(selectIndex, targetName);
				setCompositionName(targetIndex, selectName);
			});
		});
	}
	
	String createRenemaCore() {
		return String.format("UPDATE %s SET %s = ? WHERE %s = ?", COMPOSITION_NAME, NAME_COLUMN, ID_COLUMN);
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
		try {
			return allCompositionList.stream().mapToInt(i -> i.getID()).max().getAsInt() + 1;
		}catch(Exception e) {
			return 1;
		}
	}
}