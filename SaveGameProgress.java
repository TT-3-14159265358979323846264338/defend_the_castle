package savedata;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class SaveGameProgress implements Serializable{
	/**
	 * セーブデータ保存ファイルの名称。<br>
	 * (非セーブデータ)
	 */
	public transient static final String PROGRESS_FILE = "progress data.dat";
	
	/**
	 * 各ステージのクリア状況。<br>
	 * {@link defaultdata.DefaultStage#STAGE_DATA ステージ順}にクリア状況を保存。<br>
	 * このListのsizeは、{@link defaultdata.DefaultStage#STAGE_DATA STAGE_DATA}に新規追加されると、{@link savedata.FileCheck FileCheck}で自動的に追加される。
	 */
	private List<Boolean> clearStatus = new ArrayList<>();
	
	/**
	 * 各ステージの戦功取得状況。<br>
	 * {@link defaultdata.DefaultStage#STAGE_DATA ステージ順}で{@link defaultdata.stage.StageData#getMerit 戦功}クリア状況を保存。<br>
	 * このListのsizeは、{@link defaultdata.DefaultStage#STAGE_DATA STAGE_DATA}に新規追加されると、{@link savedata.FileCheck FileCheck}で自動的に追加される。
	 */
	private List<List<Boolean>> meritStatus = new ArrayList<>();
	
	/**
	 * 現在保有しているガチャメダル数。
	 */
	private int medal;
	
	/**
	 * 最後に出撃したステージ番号。
	 */
	private int selectStage;
	
	public SaveGameProgress() {
		medal = 1000;
		selectStage = 0;
	}
	
	public void load() {
		try {
			ObjectInputStream loadProgressData = new ObjectInputStream(new FileInputStream(PROGRESS_FILE));
			SaveGameProgress SaveGameProgress = (SaveGameProgress) loadProgressData.readObject();
			loadProgressData.close();
			clearStatus = SaveGameProgress.getClearStatus();
			meritStatus = SaveGameProgress.getMeritStatus();
			medal = SaveGameProgress.getMedal();
			selectStage = SaveGameProgress.getSelectStage();
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void save() {
		try {
			ObjectOutputStream saveProgressData = new ObjectOutputStream(new FileOutputStream(PROGRESS_FILE));
			saveProgressData.writeObject(this);
			saveProgressData.close();
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void save(List<Boolean> clearStatus, List<List<Boolean>> meritStatus, int medal, int selectStage) {
		this.clearStatus = clearStatus;
		this.meritStatus = meritStatus;
		this.medal = medal;
		this.selectStage = selectStage;
		try {
			ObjectOutputStream saveProgressData = new ObjectOutputStream(new FileOutputStream(PROGRESS_FILE));
			saveProgressData.writeObject(this);
			saveProgressData.close();
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public List<Boolean> getClearStatus(){
		return clearStatus;
	}
	
	public List<List<Boolean>> getMeritStatus(){
		return meritStatus;
	}
	
	public int getMedal() {
		return medal;
	}
	
	public int getSelectStage() {
		return selectStage;
	}
}