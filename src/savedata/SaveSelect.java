package savedata;

import java.util.ArrayList;
import java.util.List;

public class SaveSelect extends SQLOperation{
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
	 * 全ての選択情報を保存
	 */
	private List<Integer> selectList = new ArrayList<>();
	
	public void load(){
		operateSQL(mysql -> {
			dataLoad(mysql, SELECT_NAME, SELECT_COLUMN, selectList);
		});
	}
	
	public void save() {
		operateSQL(mysql -> {
			dataSave(mysql, SELECT_NAME, SELECT_COLUMN, ID_COLUMN, selectList);
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