package savedata;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;

import defaultdata.DefaultUnit;

//現在の編成状況の保存用
public class SaveComposition implements Serializable{
	/**
	 * セーブデータ保存ファイルの名称。<br>
	 * (非セーブデータ)
	 */
	public transient static final String COMPOSITION_FILE = "composition data.dat";
	
	/**
	 * 新規に作成された編成のデフォルト設定。<br>
	 * (非セーブデータ)
	 */
	public transient static final List<Integer> DEFAULT = Arrays.asList(DefaultUnit.NO_WEAPON, DefaultUnit.NORMAL_CORE, DefaultUnit.NO_WEAPON);
	
	/**
	 * 編成情報を保存。<br>
	 * ①List: 1つの編成情報。Listの順番が編成番号。<br>
	 * ②List: パーティ8体のデータ。<br>
	 * ③List: {@link defaultdata.DefaultUnit#WEAPON_DATA_MAP 右武器番号}, {@link defaultdata.DefaultUnit#CORE_DATA_MAP コア番号}, {@link defaultdata.DefaultUnit#WEAPON_DATA_MAP 左武器番号} の順でリスト化。
	 */
	private List<List<List<Integer>>> allCompositionList = new ArrayList<>();
	
	/**
	 * 各編成番号の名称。<br>
	 * 編成順に登録。
	 */
	private List<String> compositionNameList = new ArrayList<>();
	
	/**
	 * 現在使用可能な編成番号。
	 */
	private int selectNumber;
	
	public SaveComposition() {
		newComposition();
	}
	
	public void newComposition() {
		allCompositionList.add(new ArrayList<>(IntStream.range(0, 8).mapToObj(i -> new ArrayList<>(DEFAULT)).toList()));
		compositionNameList.add("編成 " + allCompositionList.size());
		selectNumber = allCompositionList.size() - 1;
	}
	
	public void removeComposition(int number) {
		allCompositionList.remove(number);
		compositionNameList.remove(number);
		selectNumber = (number == 0)? 0: number - 1;
	}
	
	public void load() {
		try {
			ObjectInputStream compositionData = new ObjectInputStream(new FileInputStream(COMPOSITION_FILE));
			SaveComposition SaveComposition = (SaveComposition) compositionData.readObject();
			compositionData.close();
			allCompositionList = SaveComposition.getAllCompositionList();
			compositionNameList = SaveComposition.getCompositionNameList();
			selectNumber = SaveComposition.getSelectNumber();
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void save() {
		try {
			ObjectOutputStream saveItemData = new ObjectOutputStream(new FileOutputStream(COMPOSITION_FILE));
			saveItemData.writeObject(this);
			saveItemData.close();
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void save(List<List<List<Integer>>> allCompositionList, List<String> compositionNameList, int selectNumber) {
		this.allCompositionList = allCompositionList;
		this.compositionNameList = compositionNameList;
		this.selectNumber = selectNumber;
		try {
			ObjectOutputStream saveItemData = new ObjectOutputStream(new FileOutputStream(COMPOSITION_FILE));
			saveItemData.writeObject(this);
			saveItemData.close();
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public List<List<List<Integer>>> getAllCompositionList(){
		return allCompositionList;
	}
	
	public List<String> getCompositionNameList(){
		return compositionNameList;
	}
	
	public int getSelectNumber() {
		return selectNumber;
	}
}