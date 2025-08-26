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

//現在の編成状況の保存用
public class SaveComposition implements Serializable{
	public transient final static String COMPOSITION_FILE = "composition data.dat";
	public transient final static List<Integer> DEFAULT = Arrays.asList(-1, 0, -1);
	private List<List<List<Integer>>> allCompositionList = new ArrayList<>();
	private List<String> compositionNameList = new ArrayList<>();
	private int selectNumber;
	
	/*
	非セーブデータの構造
	COMPOSITION_FILE: セーブデータ保存ファイルの名称
	DEFAULT: 新規に作成された編成のデフォルト設定 (-1: 装備無し, 0: 初期コア の番号)
	
	セーブデータの構造
	allCompositionList: ①List 編成番号 ②List パーティ8体のデータ ③List 右武器番号, コア番号, 左武器番号 の順でリスト化
	compositionNameList: 各編成番号の名称
	selectNumber: 現在使用可能な編成番号
	*/
	
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