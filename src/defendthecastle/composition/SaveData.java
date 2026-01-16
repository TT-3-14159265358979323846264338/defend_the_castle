package defendthecastle.composition;

import static javax.swing.JOptionPane.*;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;
import java.util.stream.IntStream;

import defaultdata.DefaultUnit;
import savedata.SaveComposition;
import savedata.SaveHoldItem;

//セーブデータ処理
class SaveData{
	private SaveHoldItem SaveHoldItem = new SaveHoldItem();
	private SaveComposition SaveComposition = new SaveComposition();
	private List<Integer> coreNumberList = new ArrayList<>();
	private List<Integer> weaponNumberList = new ArrayList<>();
	private List<List<List<Integer>>> allCompositionList = new ArrayList<>();
	private List<String> compositionNameList = new ArrayList<>();
	private int selectNumber;
	private List<Integer> nowCoreNumberList = new ArrayList<>();
	private List<Integer> nowWeaponNumberList = new ArrayList<>();
	private boolean existsChange;
	
	SaveData() {
		load();
	}
	
	void load() {
		SaveHoldItem.load();
		SaveComposition.load();
		input();
	}
	
	void input() {
		coreNumberList = SaveHoldItem.getCoreNumberList();
		weaponNumberList = SaveHoldItem.getWeaponNumberList();
		allCompositionList = SaveComposition.getAllCompositionList();
		compositionNameList = SaveComposition.getCompositionNameList();
		selectNumber = SaveComposition.getSelectNumber();
	}
	
	void save() {
		SaveComposition.save(allCompositionList, compositionNameList, selectNumber);
	}
	
	void countNumber() {
		int[] core = new int[coreNumberList.size()];
		int[] weapon = new int[weaponNumberList.size()];
		getActiveCompositionList().stream().forEach(i -> {
			core[i.get(1)]++;
			try {
				weapon[i.get(DefaultUnit.RIGHT_WEAPON)]++;
			}catch(Exception ignore) {
				//右武器を装備していないので、無視する
			}
			try {
				weapon[i.get(DefaultUnit.LEFT_WEAPON)]++;
			}catch(Exception ignore) {
				//左武器を装備していないので、無視する
			}
		});
		BiFunction<List<Integer>, int[], List<Integer>> getNowNumber = (list, count) -> {
			return IntStream.range(0, list.size()).mapToObj(i -> list.get(i) - count[i]).toList();
		};
		nowCoreNumberList.clear();
		nowCoreNumberList.addAll(getNowNumber.apply(coreNumberList, core));
		nowWeaponNumberList.clear();
		nowWeaponNumberList.addAll(getNowNumber.apply(weaponNumberList, weapon));
	}
	
	void addNewComposition() {
		SaveComposition.newComposition();
		input();
		existsChange = true;
	}
	
	void removeComposition(int[] number) {
		if(1 < allCompositionList.size()) {
			int select = showConfirmDialog(null, "選択中の編成を全て削除しますか", "編成削除確認", YES_NO_OPTION, QUESTION_MESSAGE);
			switch(select) {
			case 0:
				for(int i = number.length - 1; 0 <= i; i--) {
					SaveComposition.removeComposition(number[i]);
				}
				input();
				existsChange = true;
			default:
				break;
			}
		}else {
			showMessageDialog(null, "全ての編成を削除できません");
		}
	}
	
	void swapComposition(int max, int min) {
		if(max == min) {
			showMessageDialog(null, "入れ替える2つの編成を選択してください");
			return;
		}
		int select = showConfirmDialog(null, "選択中の編成を入れ替えますか", "入替確認", YES_NO_OPTION, QUESTION_MESSAGE);
		switch(select) {
		case 0:
			List<List<Integer>> maxList = allCompositionList.get(max);
			List<List<Integer>> minList = allCompositionList.get(min);
			String maxName = compositionNameList.get(max);
			String minName = compositionNameList.get(min);
			allCompositionList.set(max, minList);
			allCompositionList.set(min, maxList);
			compositionNameList.set(max, minName);
			compositionNameList.set(min, maxName);
			existsChange = true;
			break;
		default:
			break;
		}
	}
	
	String changeCompositionName() {
		String newName = showInputDialog(null, "変更後の編成名を入力してください", "名称変更", INFORMATION_MESSAGE);
		if(newName != null && !newName.isEmpty()) {
			if(!newName.startsWith(" ") && !newName.startsWith("　")) {
				compositionNameList.set(selectNumber, newName);
				existsChange = true;
				return newName;
			}
			showMessageDialog(null, "スペースで始まる名称は使用できません");
		}
		return null;
	}
	
	void saveProcessing() {
		int select = showConfirmDialog(null, "現在の編成を保存しますか?", "保存確認", YES_NO_OPTION, QUESTION_MESSAGE);
		switch(select) {
		case 0:
			save();
			existsChange = false;
		default:
			break;
		}
	}
	
	void loadProcessing() {
		int select = showConfirmDialog(null, "保存せずに元のデータをロードしますか?", "ロード確認", YES_NO_OPTION, QUESTION_MESSAGE);
		switch(select) {
		case 0:
			load();
			existsChange = false;
		default:
			break;
		}
	}
	
	void resetComposition() {
		int select = showConfirmDialog(null, "現在の編成をリセットしますか", "リセット確認", YES_NO_OPTION, QUESTION_MESSAGE);
		switch(select) {
		case 0:
			allCompositionList.set(selectNumber, new ArrayList<>(IntStream.range(0, 8).mapToObj(i -> new ArrayList<>(savedata.SaveComposition.DEFAULT)).toList()));
			existsChange = true;
		default:
			break;
		}
	}
	
	boolean returnProcessing() {
		if(!existsChange) {
			return true;
		}
		int select = showConfirmDialog(null, "保存して戻りますか?", "実行確認", YES_NO_CANCEL_OPTION, QUESTION_MESSAGE);
		switch(select) {
		case 0:
			save();
			return true;
		case 1:
			return true;
		default:
			break;
		}
		return false;
	}
	
	void selectNumberUpdate(int indexNumber) {
		selectNumber = indexNumber;
	}
	
	void changeCore(int number, int selectCore) {
		getActiveUnit(number).set(1, selectCore);
		existsChange = true;
	}
	
	void changeWeapon(int number, int selectWeapon) {
		if(DefaultUnit.WEAPON_DATA_MAP.get(selectWeapon).getHandle() == DefaultUnit.BOTH) {
			getActiveUnit(number).set(DefaultUnit.LEFT_WEAPON, selectWeapon);
			getActiveUnit(number).set(DefaultUnit.RIGHT_WEAPON, DefaultUnit.NO_WEAPON);
		}else if(getActiveUnit(number).get(DefaultUnit.LEFT_WEAPON) == DefaultUnit.NO_WEAPON) {
			change(number, selectWeapon);
		}else {
			switch(DefaultUnit.WEAPON_DATA_MAP.get(getActiveUnit(number).get(DefaultUnit.LEFT_WEAPON)).getHandle()) {
			case DefaultUnit.ONE:
				change(number, selectWeapon);
				break;
			case DefaultUnit.BOTH:
				if(change(number, selectWeapon) == 1) {
					getActiveUnit(number).set(DefaultUnit.LEFT_WEAPON, DefaultUnit.NO_WEAPON);
				}
				break;
			default:
				break;
			}
		}
		existsChange = true;
	}
	
	int change(int number, int selectWeapon) {
		String[] menu = {"左", "右", "戻る"};
		int select = showOptionDialog(null, "左右どちらの武器を変更しますか", "武器変更", OK_CANCEL_OPTION, PLAIN_MESSAGE, null, menu, menu[0]);
		switch(select) {
		case 0:
			getActiveUnit(number).set(DefaultUnit.LEFT_WEAPON, selectWeapon);
			break;
		case 1:
			getActiveUnit(number).set(DefaultUnit.RIGHT_WEAPON, selectWeapon);
			break;
		default:
			break;
		}
		return select;
	}
	
	List<Integer> getCoreNumberList(){
		return coreNumberList;
	}
	
	List<Integer> getWeaponNumberList(){
		return weaponNumberList;
	}
	
	List<String> getCompositionNameList(){
		return compositionNameList;
	}
	
	int getSelectNumber() {
		return selectNumber;
	}
	
	List<List<Integer>> getActiveCompositionList(){
		return allCompositionList.get(selectNumber);
	}
	
	List<Integer> getActiveUnit(int number){
		return allCompositionList.get(selectNumber).get(number);
	}
	
	List<Integer> getNowCoreNumberList(){
		return nowCoreNumberList;
	}
	
	List<Integer> getNowWeaponNumberList(){
		return nowWeaponNumberList;
	}
}