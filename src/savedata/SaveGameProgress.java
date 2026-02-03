package savedata;

import java.sql.PreparedStatement;
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
	 * この後ろに番号を振る
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
		operateSQL(() -> {
			String clearLoad = String.format("SELECT * FROM %s ", STAGE_NAME);
			try(PreparedStatement clearPrepared = mysql.prepareStatement(clearLoad);
					ResultSet clearResult = clearPrepared.executeQuery()){
				stageStatus.clear();
				meritStatus.clear();
				int index = 0;
				while(clearResult.next()) {
					OneStageMeritData OneStageMeritData = new OneStageMeritData(clearResult, index);
					stageStatus.add(clearResult.getBoolean(STAGE_COLUMN));
					meritStatus.add(OneStageMeritData);
					index++;
				}
			}
		});
	}
	
	public void save() {
		operateSQL(() -> {
			try(PreparedStatement savePrepared = mysql.prepareStatement(createSaveCode())){
				for(OneStageMeritData i: meritStatus){
					for(int j = 0; j < MERIT_MAX_NUMBER; j++) {
						savePrepared.setBoolean(j, i.getMeritClear(j));
					}
					savePrepared.addBatch();
				}
				savePrepared.executeBatch();
			}
		});
	}
	
	String createSaveCode() {
		StringBuilder builder = new StringBuilder();
		builder.append(String.format("UPDATE %s SET ", STAGE_NAME));
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