package savedata;

import static savedata.SaveComposition.*;
import static savedata.SaveGameProgress.*;
import static savedata.SaveHoldItem.*;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import defaultdata.DefaultStage;
import defaultdata.DefaultUnit;
import defaultdata.stage.StageData;

//データ保存用ファイルの確認
public class FileCheck{
	private SaveHoldItem SaveHoldItem = new SaveHoldItem();
	private SaveGameProgress SaveGameProgress = new SaveGameProgress();
	
	public FileCheck() {
		fileExistenceCheck();
		itemDataCheck();
		progressDataCheck();
	}
	
	//ファイルの存在確認
	private void fileExistenceCheck() {
		if(Files.notExists(Paths.get(HOLD_FILE)) || Files.notExists(Paths.get(COMPOSITION_FILE)) || Files.notExists(Paths.get(PROGRESS_FILE))) {
			SaveHoldItem.save();
			SaveGameProgress.save();
			new SaveComposition().save();
		}
	}
	
	//item dataのデータ数確認して、足りなければ追加
	private void itemDataCheck() {
		BiConsumer<Integer, List<Integer>> addList = (count, list) -> {
			IntStream.range(0, count).forEach(i -> list.add(0));
		};
		SaveHoldItem.load();
		List<Integer> coreNumberList = SaveHoldItem.getCoreNumberList();
		if(checkSize(DefaultUnit.CORE_DATA_MAP.size(), coreNumberList.size())) {
			addList.accept(DefaultUnit.CORE_DATA_MAP.size() - coreNumberList.size(), coreNumberList);
		}
		List<Integer> weaponNumberList = SaveHoldItem.getWeaponNumberList();
		if(checkSize(DefaultUnit.WEAPON_DATA_MAP.size(), weaponNumberList.size())) {
			addList.accept(DefaultUnit.WEAPON_DATA_MAP.size() - weaponNumberList.size(), weaponNumberList);
		}
		SaveHoldItem.save(coreNumberList, weaponNumberList);
	}
	
	//progress dataのデータ数確認して、足りなければ追加
	private void progressDataCheck() {
		SaveGameProgress.load();
		List<Boolean> clearStatus = SaveGameProgress.getClearStatus();
		if(checkSize(DefaultStage.STAGE_DATA_MAP.size(), clearStatus.size())) {
			IntStream.range(0, DefaultStage.STAGE_DATA_MAP.size() - clearStatus.size()).forEach(i -> clearStatus.add(false));
		}
		List<List<Boolean>> meritStatus = SaveGameProgress.getMeritStatus();
		IntStream.range(0, DefaultStage.STAGE_DATA_MAP.size()).forEach(i -> {
			StageData StageData = DefaultStage.STAGE_DATA_MAP.get(i);
			try {
				//meritStatusの内側のListのデータ数を確認し、足りなければ追加
				if(checkSize(StageData.getMerit().size(), meritStatus.get(i).size())) {
					IntStream.range(0, StageData.getMerit().size() - meritStatus.get(i).size()).forEach(j -> meritStatus.get(i).add(false));
				}
			}catch(Exception e) {
				//エラーが起こる時はList<Boolean>の数が足りない時である
				//その時はList<Boolean>を追加
				meritStatus.add(StageData.getMerit().stream().map(j -> false).collect(Collectors.toList()));
			}
		});
		SaveGameProgress.save(clearStatus, meritStatus, SaveGameProgress.getMedal(), SaveGameProgress.getSelectStage());
	}
	
	private boolean checkSize(int size1, int size2) {
		return size2 < size1;
	}
}