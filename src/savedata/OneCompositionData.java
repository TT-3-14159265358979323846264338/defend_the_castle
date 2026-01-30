package savedata;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

public class OneCompositionData {
	/**
	 * composionNameのテーブルの要素<br>
	 * 編成番号を格納したカラム名<br>
	 * PRIMARY KEY
	 */
	private final String NUMBER_COLUMN = "number";
	
	/**
	 * composionNameのテーブルの要素<br>
	 * 右武器番号を格納したカラム名
	 */
	private final String RIGHT_COLUMN = "right";
	
	/**
	 * composionNameのテーブルの要素<br>
	 * コア番号を格納したカラム名
	 */
	private final String CENTER_COLUMN = "center";
	
	/**
	 * composionNameのテーブルの要素<br>
	 * 左武器番号を格納したカラム名
	 */
	private final String LEFT_COLUMN = "left";
	
	/**
	 * MySQLへの接続
	 */
	private Connection mysql;
	
	/**
	 * テーブルの番号
	 */
	private int number;
	
	/**
	 * テーブル名
	 */
	private String compositionName;
	
	/**
	 * 各ユニットのカスタマイズ情報
	 */
	private List<OneUnitData> unitData = new ArrayList<>();
	
	OneCompositionData(Connection mysql, int number, String compositionName) {
		this.mysql = mysql;
		this.number = number;
		this.compositionName = compositionName;
	}
	
	void load() {
		unitData.clear();
		String compositionLoad = "SELECT * FROM " + compositionName;
		try(PreparedStatement compositionPrepared = mysql.prepareStatement(compositionLoad);
				ResultSet compositionTable = compositionPrepared.executeQuery()) {
			while (compositionTable.next()) {
				unitData.add(new OneUnitData(compositionTable.getInt(RIGHT_COLUMN), compositionTable.getInt(CENTER_COLUMN), compositionTable.getInt(LEFT_COLUMN)));
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	void save() {
		String compositionSave = String.format("UPDATE %s SET %s = ?, %s = ?, %s = ? WHERE %s = ?", compositionName, RIGHT_COLUMN, CENTER_COLUMN, LEFT_COLUMN, NUMBER_COLUMN);
		try(PreparedStatement compositionPrepared = mysql.prepareStatement(compositionSave)) {
			IntStream.range(0, unitData.size()).forEach(i -> {
				IntStream.range(0, unitData.get(i).getUnitData().size()).forEach(j -> {
					try {
						compositionPrepared.setInt(j + 1, unitData.get(i).getUnitData().get(j));
					}catch (Exception e) {
						e.printStackTrace();
					}
				});
				try {
					compositionPrepared.setInt(4, i + 1);
					compositionPrepared.addBatch();
				}catch (Exception e) {
					e.printStackTrace();
				}
			});
			compositionPrepared.executeBatch();
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	boolean canCreateComposition(String name) {
		try(Statement newTableStatement = mysql.createStatement()) {
			newTableStatement.executeUpdate(createTableCode(name));
		}catch (Exception e) {
			return false;
		}
		String addDefault = String.format("INSERT INTO %s (%s, %s, %s) VALUES (?, ?, ?)", name, RIGHT_COLUMN, CENTER_COLUMN, LEFT_COLUMN);
		try(PreparedStatement newPrepared = mysql.prepareStatement(addDefault)) {
			IntStream.range(0, 8).forEach(i -> {
				try {
					OneUnitData newUnitData = new OneUnitData();
					unitData.add(newUnitData);
					IntStream.range(0, newUnitData.getUnitData().size()).forEach(j -> {
						try {
							newPrepared.setInt(j + 1, newUnitData.getUnitData().get(j));
						}catch (Exception e) {
							e.printStackTrace();
						}
					});
					newPrepared.addBatch();
				}catch (Exception e) {
					e.printStackTrace();
				}
			});
			newPrepared.executeBatch();
		}catch (Exception e) {
			return false;
		}
		return true;
	}
	
	String createTableCode(String name) {
		return String.format("CREATE TABLE %s ("
				+ "%s TINYINT AUTO_INCREMENT NOT NULL PRIMARY KEY,"
				+ "%s TINYINT NOT NULL,"
				+ "%s TINYINT NOT NULL,"
				+ "%s TINYINT NOT NULL"
				+ ")",
				name,
				NUMBER_COLUMN,
				RIGHT_COLUMN,
				CENTER_COLUMN,
				LEFT_COLUMN);
	}
	
	int getNumber() {
		return number;
	}
	
	String getComposionName() {
		return compositionName;
	}

	void setComposionName(String newComposionName) {
		this.compositionName = newComposionName;
	}
}