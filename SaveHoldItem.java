package savedata;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

//現在保有しているアイテムの保存用
public class SaveHoldItem implements Serializable{
	public transient final static String HOLD_FILE = "hold data.dat";
	private List<Integer> coreNumberList = new ArrayList<>();
	private List<Integer> weaponNumberList = new ArrayList<>();
	
	/*
	非セーブデータの構造
	HOLD_FILE: セーブデータ保存ファイルの名称
	
	セーブデータの構造
	coreNumberList: 各コアの所持数
	weaponNumberList: 各武器の所持数
	これらのListのsize()は、DefaultUnitに新規追加されると、FileCheckで自動的に追加される
	*/
	
	public SaveHoldItem() {
		coreNumberList = new ArrayList<>(Arrays.asList(8));
		weaponNumberList = new ArrayList<>(Arrays.asList(2, 2));
	}
	
	public void load() {
		try {
			ObjectInputStream itemData = new ObjectInputStream(new FileInputStream(HOLD_FILE));
			SaveHoldItem SaveHoldItem = (SaveHoldItem) itemData.readObject();
			itemData.close();
			coreNumberList = SaveHoldItem.getCoreNumberList();
			weaponNumberList = SaveHoldItem.getWeaponNumberList();
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void save() {
		try {
			ObjectOutputStream saveItemData = new ObjectOutputStream(new FileOutputStream(HOLD_FILE));
			saveItemData.writeObject(this);
			saveItemData.close();
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void save(List<Integer> coreNumberList, List<Integer> weaponNumberList) {
		this.coreNumberList = coreNumberList;
		this.weaponNumberList = weaponNumberList;
		try {
			ObjectOutputStream saveItemData = new ObjectOutputStream(new FileOutputStream(HOLD_FILE));
			saveItemData.writeObject(this);
			saveItemData.close();
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public List<Integer> getCoreNumberList(){
		return coreNumberList;
	}
	
	public List<Integer> getWeaponNumberList(){
		return weaponNumberList;
	}
}