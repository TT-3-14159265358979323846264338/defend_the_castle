package savedata;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class SaveGameProgress extends SQLOperation{
	/**
	 * データベース上でゲームのクリア状況を格納したテーブル名
	 */
	public static final String STAGE_NAME = "stage";
	
	/**
	 * STAGE_NAMEのテーブルの要素<br>
	 * ステージIDを格納したカラム名<br>
	 * PRIMARY KEY
	 */
	public static final String ID_COLUMN = "id";
	
	/**
	 * STAGE_NAMEのテーブルの要素<br>
	 * ステージのクリア状況を格納したカラム名
	 */
	public static final String STAGE_COLUMN = "stage_clear";
	
	/**
	 * STAGE_NAMEのテーブルの要素<br>
	 * 戦功のクリア状況を格納したカラム名<br>
	 * この後ろにMERIT_MAX_NUMBER番号を振る
	 */
	public static final String MERIT_COLUMN = "merit_clear_";
	
	/**
	 * STAGE_NAMEのテーブルの要素<br>
	 * 戦功の最大数を表しており、0からこの数字までのカラムを作成する(0～9)
	 */
	public static final int MERIT_MAX_NUMBER = 10;
	
	/**
	 * 各ステージのクリア状況。<br>
	 * {@link defaultdata.DefaultStage#STAGE_DATA ステージ順}にクリア状況を保存。<br>
	 * 各ステージの戦功を全てクリアするとtrueになる。<br>
	 * このListのsizeは、{@link defaultdata.DefaultStage#STAGE_DATA STAGE_DATA}に新規追加されると、{@link savedata.FileCheck FileCheck}で自動的に追加される。
	 */
	private List<Boolean> stageStatus = new ArrayList<>();
	
	/**
	 * 各ステージの戦功取得状況。<br>
	 * {@link defaultdata.DefaultStage#STAGE_DATA ステージ順}で{@link defaultdata.stage.StageData#getMerit 戦功}クリア状況を保存。<br>
	 * このListのsizeは、{@link defaultdata.DefaultStage#STAGE_DATA STAGE_DATA}に新規追加されると、{@link savedata.FileCheck FileCheck}で自動的に追加される。
	 */
	private List<OneStageMeritData> meritStatus = new ArrayList<>();
	
	public void load() {
		operateSQL(mysql -> {
			operateResultSet(mysql, STAGE_NAME, result -> {
				List<Boolean> loadStage = new ArrayList<>();
				List<OneStageMeritData> loadMerit = new ArrayList<>();
				int stageNo = 0;
				while(result.next()) {
					addStageStatus(loadStage, result);
					var oneStageMeritData = createOneStageMeritData(result, stageNo);
					addMeritStatus(loadMerit, oneStageMeritData);
					stageNo++;
				}
				stageStatus.clear();
				stageStatus.addAll(loadStage);
				meritStatus.clear();
				meritStatus.addAll(loadMerit);
			});
		});
	}
	
	void addStageStatus(List<Boolean> loadData, ResultSet result) throws Exception{
		loadData.add(result.getBoolean(STAGE_COLUMN));
	}
	
	OneStageMeritData createOneStageMeritData(ResultSet result, int stageNo) throws Exception{
		return new OneStageMeritData(result, stageNo);
	}
	
	void addMeritStatus(List<OneStageMeritData> loadData, OneStageMeritData OneStageMeritData) {
		loadData.add(OneStageMeritData);
	}
	
	public void save() {
		operateSQL(mysql -> {
			operatePrepared(mysql, createSaveCode(), prepared -> {
				for(int i = 0; i < meritStatus.size(); i++){
					prepared.setBoolean(1, stageStatus.get(i));
					for(int j = 0; j < MERIT_MAX_NUMBER; j++) {
						prepared.setBoolean(j + 2, meritStatus.get(i).getMeritClear(j));
					}
					prepared.setInt(12, i + 1);
					prepared.addBatch();
				}
				prepared.executeBatch();
			});
		});
	}
	
	String createSaveCode() {
		StringBuilder builder = new StringBuilder();
		builder.append(String.format("UPDATE %s SET %s = ?,", STAGE_NAME, STAGE_COLUMN));
		for(int i = 0; i < MERIT_MAX_NUMBER; i++){
			builder.append(String.format(" %s%d = ?,", MERIT_COLUMN, i));
		}
		builder.delete(builder.length() - 1, builder.length());
		builder.append(String.format(" WHERE %s = ?", ID_COLUMN));
		return builder.toString();
	}

	public List<Boolean> getStageStatus() {
		return stageStatus;
	}
	
	public void setStage(int index, boolean exists) {
		stageStatus.set(index, exists);
	}

	public List<OneStageMeritData> getMeritStatus() {
		return meritStatus;
	}
	
	public OneStageMeritData getMeritData(int index) {
		return meritStatus.get(index);
	}
}