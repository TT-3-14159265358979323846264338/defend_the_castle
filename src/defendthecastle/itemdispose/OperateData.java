package defendthecastle.itemdispose;

import static javax.swing.JOptionPane.*;

import java.awt.image.BufferedImage;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.IntStream;

import defaultdata.DefaultUnit;
import savedata.SaveComposition;
import savedata.SaveGameProgress;
import savedata.SaveHoldItem;
import savedata.SaveItem;

//セーブデータの確認
class OperateData{
	private SaveHoldItem SaveHoldItem = new SaveHoldItem();
	private SaveComposition SaveComposition = new SaveComposition();
	private SaveGameProgress SaveGameProgress = new SaveGameProgress();
	private SaveItem SaveItem = new SaveItem();;
	private int[] usedCoreNumber;
	private int[] usedWeaponNumber;
	
	protected OperateData() {
		load();
		itemCount();
	}
	
	private void load() {
		SaveHoldItem.load();
		SaveComposition.load();
		SaveGameProgress.load();
		SaveItem.load();
	}
	
	private void save() {
		SaveHoldItem.save();
		SaveItem.save();
	}
	
	private void itemCount() {
		BiConsumer<int[], int[]> maxNumberUpdate = (max, count) -> {
			IntStream.range(0, max.length).forEach(i -> {
				if(max[i] < count[i]) {
					max[i] = count[i];
				}
			});
		};
		Function<Integer, Integer> initialWeaponProtection = (number) -> {
			return (2 <= number)? number: 2;
		};
		int[] coreMax = new int[getCoreNumberList().size()];
		int[] weaponMax = new int[getWeaponNumberList().size()];
		IntStream.range(0, SaveComposition.getAllCompositionList().size()).forEach(i -> {
			int[] coreCount = new int[getCoreNumberList().size()];
			int[] weaponCount = new int[getWeaponNumberList().size()];
			SaveComposition.getOneCompositionData(i).getOneUnitDataList().stream().forEach(j -> {
				try {
					weaponCount[j.getUnit(DefaultUnit.RIGHT_WEAPON)]++;
				}catch(Exception ignore) {
					//右武器を装備していないので、無視する
				}
				coreCount[j.getUnit(DefaultUnit.CORE)]++;
				try {
					weaponCount[j.getUnit(DefaultUnit.LEFT_WEAPON)]++;
				}catch(Exception ignore) {
					//左武器を装備していないので、無視する
				}
			});
			maxNumberUpdate.accept(coreMax, coreCount);
			maxNumberUpdate.accept(weaponMax, weaponCount);
		});
		usedCoreNumber = coreMax;
		usedWeaponNumber = weaponMax;
		//初期武器は最低2本残さなければならない
		usedWeaponNumber[DefaultUnit.SWORD] = initialWeaponProtection.apply(usedWeaponNumber[DefaultUnit.SWORD]);
		usedWeaponNumber[DefaultUnit.BOW] = initialWeaponProtection.apply(usedWeaponNumber[DefaultUnit.BOW]);
	}
	
	protected void recycle(ItemImagePanel ImagePanel, List<Integer> numberList, int[] usedNumber, List<BufferedImage> imageList, List<Integer> rarityList) {
		Predicate<Integer> selectCheck = (select) -> {
			if(select < 0) {
				showMessageDialog(null, "リサイクルする対象が選択されていません");
				return false;
			}
			return true;
		};
		Predicate<Integer> numberCheck = (max) -> {
			if(max <= 0) {
				showMessageDialog(null, "最大所持数まで編成しているため、リサイクルできません");
				return false;
			}
			return true;
		};
		int select = ImagePanel.getSelectNumber();
		if(selectCheck.test(select)) {
			int max = numberList.get(select) - usedNumber[select];
			if(numberCheck.test(max)) {
				RecyclePanel RecyclePanel = new RecyclePanel(imageList.get(select), max, rarityList.get(select));
				if(RecyclePanel.canDispose()) {
					numberList.set(select, numberList.get(select) - RecyclePanel.getQuantity());
					SaveItem.addMedal(RecyclePanel.getMedal());
					save();
				}
			}
		}
	}
	
	protected List<Integer> getCoreNumberList(){
		return SaveHoldItem.getCoreNumberList();
	}
	
	protected List<Integer> getWeaponNumberList(){
		return SaveHoldItem.getWeaponNumberList();
	}
	
	protected int[] getUsedCoreNumber() {
		return usedCoreNumber;
	}
	
	protected int[] getUsedWeaponNumber() {
		return usedWeaponNumber;
	}
}