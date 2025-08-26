package savedata;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class SaveGameProgress implements Serializable{
	public transient final static String PROGRESS_FILE = "progress data.dat";
	private List<Boolean> clearStatus = new ArrayList<>();
	private List<List<Boolean>> meritStatus = new ArrayList<>();
	private int medal;
	private int selectStage;
	
	/*
	非セーブデータの構造
	PROGRESS_FILE: セーブデータ保存ファイルの名称
	
	セーブデータの構造
	clearStatus: 各ステージのクリア状況
	meritStatus: 各ステージの戦功取得状況
	これらのListのsize()は、DefaultStageに新規追加されると、FileCheckで自動的に追加される
	medal: 現在保有しているガチャメダル数
	selectStage: 最後に出撃したステージ番号
	*/
	
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