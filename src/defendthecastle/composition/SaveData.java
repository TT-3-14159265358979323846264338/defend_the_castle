package defendthecastle.composition;

import static javax.swing.JOptionPane.*;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;
import java.util.stream.IntStream;

import defaultdata.DefaultUnit;
import savedata.OneUnitData;
import savedata.SaveComposition;
import savedata.SaveHoldItem;
import savedata.SaveSelect;

//セーブデータ処理
class SaveData{
	private SaveHoldItem SaveHoldItem = new SaveHoldItem();
	private SaveComposition SaveComposition = new SaveComposition();
	private SaveSelect SaveSelect = new SaveSelect();
	private List<Integer> nowCoreNumberList = new ArrayList<>();
	private List<Integer> nowWeaponNumberList = new ArrayList<>();
	private boolean existsChange;
	
	SaveData() {
		load();
	}
	
	void load() {
		SaveHoldItem.load();
		SaveComposition.load();
		SaveSelect.load();
	}
	
	void save() {
		SaveComposition.save();
		SaveSelect.save();
	}
	
	void countNumber() {
		int[] core = new int[getCoreNumberList().size()];
		int[] weapon = new int[getWeaponNumberList().size()];
		getActiveCompositionList().stream().forEach(i -> {
			core[i.getUnit(DefaultUnit.CORE)]++;
			try {
				weapon[i.getUnit(DefaultUnit.RIGHT_WEAPON)]++;
			}catch(Exception ignore) {
				//右武器を装備していないので、無視する
			}
			try {
				weapon[i.getUnit(DefaultUnit.LEFT_WEAPON)]++;
			}catch(Exception ignore) {
				//左武器を装備していないので、無視する
			}
		});
		BiFunction<List<Integer>, int[], List<Integer>> getNowNumber = (list, count) -> {
			return IntStream.range(0, list.size()).mapToObj(i -> list.get(i) - count[i]).toList();
		};
		nowCoreNumberList.clear();
		nowCoreNumberList.addAll(getNowNumber.apply(getCoreNumberList(), core));
		nowWeaponNumberList.clear();
		nowWeaponNumberList.addAll(getNowNumber.apply(getWeaponNumberList(), weapon));
	}
	
	void swapComposition(int selectIndex, int targetIndex) {
		if(selectIndex == targetIndex) {
			showMessageDialog(null, "入れ替える2つの編成を選択してください");
			return;
		}
		int select = showConfirmDialog(null, "選択中の編成を入れ替えますか", "入替確認", YES_NO_OPTION, QUESTION_MESSAGE);
		switch(select) {
		case 0:
			SaveComposition.swap(selectIndex, targetIndex);
			existsChange = true;
			break;
		default:
			break;
		}
	}
	
	void changeCompositionName() {
		String newName = showInputDialog(null, "変更後の編成名を入力してください", "名称変更", INFORMATION_MESSAGE);
		if(SaveComposition.rename(getSelectNumber(), newName)){
			existsChange = true;
		}
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
			getActiveCompositionList().stream().forEach(i -> i.reset());
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
		SaveSelect.setCompositionSelectNumber(indexNumber);
	}
	
	void changeCore(int number, int selectCore) {
		getUnitData(number).setUnitData(DefaultUnit.CORE, selectCore);
		existsChange = true;
	}
	
	void changeWeapon(int number, int selectWeapon) {
		if(DefaultUnit.WEAPON_DATA_MAP.get(selectWeapon).getHandle() == DefaultUnit.BOTH) {
			getUnitData(number).setUnitData(DefaultUnit.LEFT_WEAPON, selectWeapon);
			getUnitData(number).setUnitData(DefaultUnit.RIGHT_WEAPON, DefaultUnit.NO_WEAPON);
		}else if(getUnitData(number).getUnitDataList().get(DefaultUnit.LEFT_WEAPON) == DefaultUnit.NO_WEAPON) {
			change(number, selectWeapon);
		}else {
			switch(DefaultUnit.WEAPON_DATA_MAP.get(getUnitData(number).getUnitDataList().get(DefaultUnit.LEFT_WEAPON)).getHandle()) {
			case DefaultUnit.ONE:
				change(number, selectWeapon);
				break;
			case DefaultUnit.BOTH:
				if(change(number, selectWeapon) == 1) {
					getUnitData(number).setUnitData(DefaultUnit.LEFT_WEAPON, DefaultUnit.NO_WEAPON);
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
			getUnitData(number).setUnitData(DefaultUnit.LEFT_WEAPON, selectWeapon);
			break;
		case 1:
			getUnitData(number).setUnitData(DefaultUnit.RIGHT_WEAPON, selectWeapon);
			break;
		default:
			break;
		}
		return select;
	}
	
	List<Integer> getCoreNumberList(){
		return SaveHoldItem.getCoreNumberList();
	}
	
	List<Integer> getWeaponNumberList(){
		return SaveHoldItem.getWeaponNumberList();
	}
	
	List<String> getCompositionNameList(){
		return SaveComposition.getCompositionNameList();
	}
	
	int getSelectNumber() {
		return SaveSelect.getCompositionSelectNumber();
	}
	
	List<OneUnitData> getActiveCompositionList(){
		return SaveComposition.getOneCompositionData(getSelectNumber()).getOneUnitDataList();
	}
	
	OneUnitData getUnitData(int index) {
		return getActiveCompositionList().get(index);
	}
	
	List<Integer> getNowCoreNumberList(){
		return nowCoreNumberList;
	}
	
	List<Integer> getNowWeaponNumberList(){
		return nowWeaponNumberList;
	}
	
	/*
	 * ここからテスト用ゲッターセッター
	 */
	SaveHoldItem getSaveHoldItem() {
		return SaveHoldItem;
	}

	public void setSaveHoldItem(SaveHoldItem saveHoldItem) {
		SaveHoldItem = saveHoldItem;
	}

	SaveComposition getSaveComposition() {
		return SaveComposition;
	}

	public void setSaveComposition(SaveComposition saveComposition) {
		SaveComposition = saveComposition;
	}

	public SaveSelect getSaveSelect() {
		return SaveSelect;
	}

	public void setSaveSelect(SaveSelect saveSelect) {
		SaveSelect = saveSelect;
	}

	boolean isExistsChange() {
		return existsChange;
	}

	void setExistsChange(boolean existsChange) {
		this.existsChange = existsChange;
	}
}