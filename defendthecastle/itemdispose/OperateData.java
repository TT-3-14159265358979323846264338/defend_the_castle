package defendthecastle.itemdispose;

import static javax.swing.JOptionPane.*;

import java.awt.image.BufferedImage;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Predicate;
import java.util.stream.IntStream;

import savedata.SaveComposition;
import savedata.SaveGameProgress;
import savedata.SaveHoldItem;

//セーブデータの確認
class OperateData{
	private SaveHoldItem SaveHoldItem = new SaveHoldItem();
	private SaveComposition SaveComposition = new SaveComposition();
	private SaveGameProgress SaveGameProgress = new SaveGameProgress();
	private List<Integer> coreNumberList;
	private List<Integer> weaponNumberList;
	private List<List<List<Integer>>> allCompositionList;
	private int medal;
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
		coreNumberList = SaveHoldItem.getCoreNumberList();
		weaponNumberList = SaveHoldItem.getWeaponNumberList();
		allCompositionList = SaveComposition.getAllCompositionList();
		medal = SaveGameProgress.getMedal();
	}
	
	private void save() {
		SaveHoldItem.save(coreNumberList, weaponNumberList);
		SaveGameProgress.save(SaveGameProgress.getClearStatus(), SaveGameProgress.getMeritStatus(), medal, SaveGameProgress.getSelectStage());
	}
	
	private void itemCount() {
		BiConsumer<int[], int[]> maxNumberUpdate = (max, count) -> {
			IntStream.range(0, max.length).forEach(i -> {
				if(max[i] < count[i]) {
					max[i] = count[i];
				}
			});
		};
		int[] coreMax = new int[coreNumberList.size()];
		int[] weaponMax = new int[weaponNumberList.size()];
		IntStream.range(0, allCompositionList.size()).forEach(i -> {
			int[] coreCount = new int[coreNumberList.size()];
			int[] weaponCount = new int[weaponNumberList.size()];
			IntStream.range(0, allCompositionList.get(i).size()).forEach(j -> {
				try {
					weaponCount[allCompositionList.get(i).get(j).get(0)]++;
	    		}catch(Exception ignore) {
					//右武器を装備していないので、無視する
				}
				coreCount[allCompositionList.get(i).get(j).get(1)]++;
	    		try {
	    			weaponCount[allCompositionList.get(i).get(j).get(2)]++;
	    		}catch(Exception ignore) {
					//左武器を装備していないので、無視する
				}
			});
			maxNumberUpdate.accept(coreMax, coreCount);
			maxNumberUpdate.accept(weaponMax, weaponCount);
		});
		usedCoreNumber = coreMax;
		usedWeaponNumber = weaponMax;
		//初期武器は2本以外リサイクル禁止
		usedWeaponNumber[0] += 2;
		usedWeaponNumber[1] += 2;
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
					medal += RecyclePanel.getMedal();
					save();
				}
			}
		}
	}
	
	protected List<Integer> getCoreNumberList(){
		return coreNumberList;
	}
	
	protected List<Integer> getWeaponNumberList(){
		return weaponNumberList;
	}
	
	protected int[] getUsedCoreNumber() {
		return usedCoreNumber;
	}
	
	protected int[] getUsedWeaponNumber() {
		return usedWeaponNumber;
	}
}