package savedata;

import static savedata.OperationSQL.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class SaveItem {
	/**
	 * データベース上で保有しているアイテムを格納したテーブル名
	 */
	public static final String ITEM_NAME = "item";
	
	/**
	 * ITEM_NAMEのテーブルの要素<br>
	 * どのアイテムか特定するidを格納したカラム名<br>
	 * PRIMARY KEY
	 */
	public static final String ID_COLUMN = "id";
	
	/**
	 * ITEM_NAMEのテーブルの要素<br>
	 * 現在選択されているアイテムの保有数を格納したカラム名
	 */
	public static final String ITEM_COLUMN = "number";
	
	/**
	 * メダル選択用項目<br>
	 * ID_COLUMNで定義
	 */
	private final int MEDAL = 0;
	
	/**
	 * 全てのアイテム数を保存
	 */
	private List<Integer> itemList = new ArrayList<>();
	
	public void load() {
		Connection mysql = connectMysql();
		executeSQL(mysql, () -> {
			itemList.clear();
			String itemLoad = "SELECT * FROM " + ITEM_NAME;
			try(PreparedStatement itemPrepared = mysql.prepareStatement(itemLoad);
					ResultSet itemTable = itemPrepared.executeQuery()) {
				while (itemTable.next()) {
					itemList.add(itemTable.getInt(ITEM_COLUMN));
				}
			}
		});
		closeConnection(mysql);
	}
	
	public void save() {
		Connection mysql = connectMysql();
		executeSQL(mysql, () -> {
			String itemSave = String.format("UPDATE %s SET %s = ? WHERE %s = ?", ITEM_NAME, ITEM_COLUMN, ID_COLUMN);
			try(PreparedStatement itemPrepared = mysql.prepareStatement(itemSave)) {
				for(int i = 0; i < itemList.size(); i++) {
					itemPrepared.setInt(1, itemList.get(i));
					itemPrepared.setInt(2, i + 1);
					itemPrepared.addBatch();
				}
				itemPrepared.executeBatch();
			}
		});
		closeConnection(mysql);
	}
	
	public int getMedalNumber() {
		return itemList.get(MEDAL);
	}
	
	public void addMedal(int number) {
		itemList.set(MEDAL, itemList.get(MEDAL) + number);
	}
	
	public void reduceMedal(int number) {
		itemList.set(MEDAL, itemList.get(MEDAL) - number);
	}
}