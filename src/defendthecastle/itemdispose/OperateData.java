package defendthecastle.itemdispose;

import static javax.swing.JOptionPane.*;

import java.awt.image.BufferedImage;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import java.util.stream.IntStream;

import defaultdata.DefaultUnit;
import defendthecastle.commoninheritance.ImagePanel;
import savedata.SaveComposition;
import savedata.SaveGameProgress;
import savedata.SaveHoldItem;
import savedata.SaveItem;

//セーブデータの確認
class OperateData{
	private final ScheduledExecutorService scheduler;
	private final SaveHoldItem saveHoldItem;
	private final SaveComposition saveComposition;
	private final SaveGameProgress saveGameProgress;
	private final SaveItem saveItem;
	private int[] usedCoreNumber;
	private int[] usedWeaponNumber;
	
	OperateData(ScheduledExecutorService scheduler) {
		this.scheduler = scheduler;
		saveHoldItem = createSaveHoldItem();
		saveComposition = createSaveComposition();
		saveGameProgress = createSaveGameProgress();
		saveItem = createSaveItem();
		load();
		itemCount();
	}
	
	SaveHoldItem createSaveHoldItem() {
		return new SaveHoldItem();
	}
	
	SaveComposition createSaveComposition() {
		return new SaveComposition();
	}
	
	SaveGameProgress createSaveGameProgress() {
		return new SaveGameProgress();
	}
	
	SaveItem createSaveItem() {
		return new SaveItem();
	}
	
	void load() {
		saveHoldItem.load();
		saveComposition.load();
		saveGameProgress.load();
		saveItem.load();
	}
	
	void save() {
		saveHoldItem.save();
		saveItem.save();
	}
	
	void itemCount() {
		int[] coreMax = new int[getCoreNumberList().size()];
		int[] weaponMax = new int[getWeaponNumberList().size()];
		IntStream.range(0, saveComposition.getAllCompositionList().size()).forEach(i -> {
			int[] coreCount = new int[getCoreNumberList().size()];
			int[] weaponCount = new int[getWeaponNumberList().size()];
			countOneComposition(i, coreCount, weaponCount);
			maxNumberUpdate(coreMax, coreCount);
			maxNumberUpdate(weaponMax, weaponCount);
		});
		usedCoreNumber = coreMax;
		usedWeaponNumber = weaponMax;
		//初期武器は最低2本残さなければならない
		usedWeaponNumber[DefaultUnit.SWORD] = initialWeaponProtection(usedWeaponNumber[DefaultUnit.SWORD]);
		usedWeaponNumber[DefaultUnit.BOW] = initialWeaponProtection(usedWeaponNumber[DefaultUnit.BOW]);
	}
	
	void countOneComposition(int index, int[] coreCount, int[] weaponCount) {
		saveComposition.getOneCompositionData(index).getOneUnitDataList().stream().forEach(i -> {
			try {
				weaponCount[i.getUnit(DefaultUnit.RIGHT_WEAPON)]++;
			}catch(Exception ignore) {
				//右武器を装備していないので、無視する
			}
			coreCount[i.getUnit(DefaultUnit.CORE)]++;
			try {
				weaponCount[i.getUnit(DefaultUnit.LEFT_WEAPON)]++;
			}catch(Exception ignore) {
				//左武器を装備していないので、無視する
			}
		});
	}
	
	void maxNumberUpdate(int[] max, int[] count) {
		IntStream.range(0, max.length).forEach(i -> {
			if(max[i] < count[i]) {
				max[i] = count[i];
			}
		});
	}
	
	int initialWeaponProtection(int number){
		return (2 <= number)? number: 2;
	}
	
	void recycle(ImagePanel ImagePanel, List<Integer> numberList, int[] usedNumber, List<BufferedImage> imageList, List<Integer> rarityList) {
		int select = ImagePanel.getSelectNumber();
		if(hasSelected(select)) {
			int max = numberList.get(select) - usedNumber[select];
			if(canDispose(max)) {
				var RecyclePanel = createRecyclePanel(imageList, select, max, rarityList);
				if(RecyclePanel.canDispose()) {
					numberList.set(select, numberList.get(select) - RecyclePanel.getQuantity());
					saveItem.addMedal(RecyclePanel.getMedal());
					save();
				}
			}
		}
	}
	
	boolean hasSelected(int select) {
		if(select < 0) {
			showMessageDialog(null, "リサイクルする対象が選択されていません");
			return false;
		}
		return true;
	}
	
	boolean canDispose(int max) {
		if(max <= 0) {
			showMessageDialog(null, "最大所持数まで編成しているため、リサイクルできません");
			return false;
		}
		return true;
	}
	
	RecyclePanel createRecyclePanel(List<BufferedImage> imageList, int select, int max, List<Integer> rarityList) {
		return new RecyclePanel(scheduler, imageList.get(select), max, rarityList.get(select));
	}
	
	List<Integer> getCoreNumberList(){
		return saveHoldItem.getCoreNumberList();
	}
	
	List<Integer> getWeaponNumberList(){
		return saveHoldItem.getWeaponNumberList();
	}
	
	int[] getUsedCoreNumber() {
		return usedCoreNumber;
	}
	
	int[] getUsedWeaponNumber() {
		return usedWeaponNumber;
	}
}