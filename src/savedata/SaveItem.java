package savedata;

import java.util.ArrayList;
import java.util.List;

public class SaveItem extends SQLOperation{
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
		operateSQL(() -> {
			dataLoad(ITEM_NAME, ITEM_COLUMN, itemList);
		});
	}
	
	public void save() {
		operateSQL(() -> {
			dataSave(ITEM_NAME, ITEM_COLUMN, ID_COLUMN, itemList);
		});
	}
	
	public int getMedalNumber() {
		return itemList.get(MEDAL);
	}
	
	public void setMedalNumber(int number) {
		itemList.set(MEDAL, number);
	}
	
	public void addMedal(int number) {
		itemList.set(MEDAL, itemList.get(MEDAL) + number);
	}
	
	public void reduceMedal(int number) {
		itemList.set(MEDAL, itemList.get(MEDAL) - number);
	}
}