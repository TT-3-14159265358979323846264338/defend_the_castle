package defendthecastle.screendisplay;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import javax.swing.JLabel;

import commonclass.EditImage;
import defaultdata.Core;
import defaultdata.CoreUnit;
import defaultdata.CoreAtack;
import defaultdata.AtackPattern;
import defaultdata.Element;
import defaultdata.EnemyUnit;
import defaultdata.FacilityUnit;
import defaultdata.DefaultEnum;
import defaultdata.WeaponUnit;
import defaultdata.Weapon;
import defaultdata.Atack;
import defaultdata.core.CoreData;
import defaultdata.enemy.EnemyData;
import defaultdata.weapon.WeaponData;
import defendthecastle.battle.BattleData;
import defendthecastle.battle.BattleEnemy;
import defendthecastle.battle.BattleFacility;
import defendthecastle.battle.BattleUnit;
import savedata.OneUnitData;

//ユニットデータ取込み
public class DisplayStatus extends StatusPanel{
	public void core(BufferedImage image, int number) {
		CoreData coreData = createCoreData(number);
		setItem();
		setUnitName(getRarity(coreData.getRarity()) + coreData.getName());
		setExplanation(explanationComment(coreData.getExplanation()));
		setWeapon(coreData.getWeaponStatus());
		setUnit(coreData.getUnitStatus(), "倍");
		setCut(coreData.getCutStatus());
		super.setStatusPanel(image);
	}
	
	CoreData createCoreData(int number) {
		return Core.getLabel(number);
	}
	
	public void weapon(BufferedImage image, int number) {
		WeaponData weaponData = createWeaponData(number);
		setItem();
		setUnitName(getRarity(weaponData.getRarity()) + weaponData.getName());
		setExplanation(explanationComment(weaponData.getExplanation()));
		setWeapon(weaponData);
		setUnit(weaponData.getUnitStatus(), WeaponUnit.values());
		setCut(weaponData.getCutStatus());
		super.setStatusPanel(image);
	}
	
	WeaponData createWeaponData(int number) {
		return Weapon.getLabel(number);
	}
	
	public void unit(BufferedImage image, List<Integer> compositionList) {
		StatusCalculation statusCalculation = createStatusCalculation(compositionList);
		setItem();
		setUnitName(compositionList);
		setExplanation(compositionList);
		setWeapon(statusCalculation, compositionList);
		setUnit(statusCalculation.getUnitStatus(), WeaponUnit.values());
		setCut(statusCalculation.getCutStatus());
		super.setStatusPanel(image);
	}
	
	StatusCalculation createStatusCalculation(List<Integer> compositionList) {
		return new StatusCalculation(compositionList);
	}
	
	public void enemy(EnemyData enemyData) {
		setItem();
		setUnitName(enemyData.getName());
		setExplanation(explanationComment(enemyData.getExplanation()));
		setWeapon(enemyData);
		setUnit(enemyData.getUnitStatus(), EnemyUnit.values());
		setCut(enemyData.getCutStatus());
		super.setStatusPanel(enemyData.getImage(2));
	}
	
	public void unit(BattleUnit unitMainData, BattleUnit unitLeftData) {
		setItem();
		setUnitName(unitMainData.getComposition());
		setExplanation(unitMainData.getComposition());
		setWeapon(unitMainData, unitLeftData);
		setUnit(unitMainData, WeaponUnit.values());
		setCut(unitMainData.getCut());
		super.setStatusPanel(EditImage.compositeImage(Arrays.asList(unitMainData.getDefaultImage(), unitMainData.getDefaultCoreImage(), unitLeftData.getDefaultImage())));
	}
	
	public void facility(BattleFacility facilityData) {
		setItem();
		setUnitName(facilityData.getName());
		setExplanation(explanationComment(facilityData.getExplanation()));
		setWeapon(facilityData);
		setUnit(facilityData, FacilityUnit.values());
		setCut(facilityData.getCut());
		super.setStatusPanel(facilityData.getDefaultImage());
	}
	
	public void enemy(BattleEnemy enemyData) {
		setItem();
		setUnitName(enemyData.getName());
		setExplanation(explanationComment(enemyData.getExplanation()));
		setWeapon(enemyData);
		setUnit(enemyData, EnemyUnit.values());
		setCut(enemyData.getCut());
		super.setStatusPanel(enemyData.getDefaultImage());
	}
	
	void setItem() {
		item[0].setText("【名称/説明】");
		item[1].setText("【武器ステータス】");
		item[2].setText("【ユニットステータス】");
	}
	
	void setUnitName(String name) {
		unitName[2].setText(name);
	}
	
	void setUnitName(List<Integer> composition) {
		Consumer<JLabel> noWeapon = (label) -> {
			label.setText("no weapon");
		};
		try {
			WeaponData rightWeapon = createWeaponData(composition.get(OneUnitData.RIGHT_WEAPON));
			unitName[0].setText(getRarity(rightWeapon.getRarity()) + explanationComment(rightWeapon.getName()));
		}catch(Exception e) {
			noWeapon.accept(unitName[0]);
		}
		CoreData coreData = createCoreData(composition.get(OneUnitData.CORE));
		unitName[1].setText(getRarity(coreData.getRarity()) + explanationComment(coreData.getName()));
		try {
			WeaponData leftWeapon = createWeaponData(composition.get(OneUnitData.LEFT_WEAPON));
			unitName[2].setText(getRarity(leftWeapon.getRarity()) + explanationComment(leftWeapon.getName()));
		}catch(Exception e) {
			noWeapon.accept(unitName[2]);
		}
	}
	
	String getRarity(int rarity) {
		return "★" + rarity + " ";
	}
	
	void setExplanation(String comment) {
		explanation[0].setText("");
		explanation[1].setText("");
		explanation[2].setText(comment);
	}
	
	void setExplanation(List<Integer> composition) {
		try {
			explanation[0].setText(explanationComment(createWeaponData(composition.get(OneUnitData.RIGHT_WEAPON)).getExplanation()));
		}catch(Exception ignore) {
			//右武器を装備していないので、無視する
		}
		explanation[1].setText(explanationComment(createCoreData(composition.get(OneUnitData.CORE)).getExplanation()));
		try {
			explanation[2].setText(explanationComment(createWeaponData(composition.get(OneUnitData.LEFT_WEAPON)).getExplanation()));
		}catch(Exception ignore) {
			//左武器を装備していないので、無視する
		}
	}
	
	String explanationComment(String comment) {
		int lastPosition = 0;
		List<Integer> wrapPosition = new ArrayList<>();
		for(int i = 0; i < comment.length(); i++) {
			if(SIZE_X * 2- 10 < getFontMetrics(defaultFont).stringWidth(comment.substring(lastPosition, i))) {
				wrapPosition.add(i - 1);
				lastPosition = i - 1;
			}
		}
		if(wrapPosition.isEmpty()) {
			return comment;
		}
		StringBuilder wrapComment = new StringBuilder(comment);
		wrapPosition.stream().sorted(Comparator.reverseOrder()).forEach(i -> wrapComment.insert(i, "<br>"));
		return wrapComment.insert(0, "<html>").toString();
	}
	
	void setWeapon(List<Double> statusList) {
		Stream.of(CoreAtack.values()).forEach(i -> {
			weapon[i.getId() + 1].setText(i.getLabel());
			weapon[i.getId() + 10].setText(statusList.get(i.getId()) + "倍");
		});
		weapon[9].setText("武器性能");
	}
	
	void setWeapon(WeaponData WeaponData) {
		Stream.of(Atack.values()).forEach(i -> {
			weapon[i.getId() + 1].setText(i.getLabel());
			weapon[i.getId() + 10].setText("" + WeaponData.getWeaponStatus().get(i.getId()));
		});
		weapon[5].setText("距離タイプ");
		weapon[6].setText("装備タイプ");
		weapon[7].setText("属性");
		weapon[8].setText("ターゲット");
		weapon[9].setText("武器性能");
		weapon[14].setText("" + WeaponData.getDistance().getLabel());
		weapon[15].setText("" + WeaponData.getHandle().getLabel());
		weapon[16].setText("" + getElement(WeaponData.getElement()));
		weapon[17].setText("" + new AtackPattern().getAtackPattern(WeaponData.getAtackPattern()).getExplanation());
	}
	
	void setWeapon(StatusCalculation statusCalculation, List<Integer> compositionList) {
		Stream.of(Atack.values()).forEach(i -> weapon[i.getId() + 1].setText(i.getLabel()));
		weapon[5].setText("距離タイプ");
		weapon[6].setText("装備タイプ");
		weapon[7].setText("属性");
		weapon[8].setText("ターゲット");
		weapon[9].setText("左武器");
		if(0 <= compositionList.get(OneUnitData.LEFT_WEAPON)) {
			WeaponData weaponData = createWeaponData(compositionList.get(OneUnitData.LEFT_WEAPON));
			Stream.of(Atack.values()).forEach(i -> weapon[i.getId() + 10].setText("" + statusCalculation.getLeftWeaponStatus().get(i.getId())));
			weapon[14].setText("" + weaponData.getDistance().getLabel());
			weapon[15].setText("" + weaponData.getHandle().getLabel());
			weapon[16].setText("" + getElement(statusCalculation.getLeftElement()));
			weapon[17].setText("" + new AtackPattern().getAtackPattern(weaponData.getAtackPattern()).getExplanation());
		}
		weapon[18].setText("右武器");
		if(0 <= compositionList.get(OneUnitData.RIGHT_WEAPON)) {
			WeaponData weaponData = createWeaponData(compositionList.get(OneUnitData.RIGHT_WEAPON));
			Stream.of(Atack.values()).forEach(i -> weapon[i.getId() + 19].setText("" + statusCalculation.getRightWeaponStatus().get(i.getId())));
			weapon[23].setText("" + weaponData.getDistance().getLabel());
			weapon[24].setText("" + weaponData.getHandle().getLabel());
			weapon[25].setText("" + getElement(statusCalculation.getRightElement()));
			weapon[26].setText("" + new AtackPattern().getAtackPattern(weaponData.getAtackPattern()).getExplanation());
		}
	}
	
	void setWeapon(EnemyData enemyData) {
		Stream.of(Atack.values()).forEach(i -> {
			weapon[i.getId() + 1].setText(i.getLabel());
			weapon[i.getId() + 10].setText("" + enemyData.getWeaponStatus().get(i.getId()));
		});
		weapon[5].setText("移動タイプ");
		weapon[6].setText("種別");
		weapon[7].setText("属性");
		weapon[8].setText("ターゲット");
		weapon[9].setText("攻撃性能");
		weapon[14].setText("" + enemyData.getMove().getLabel());
		weapon[15].setText("" + enemyData.getType().getLabel());
		weapon[16].setText("" + getElement(enemyData.getElement()));
		weapon[17].setText("" + new AtackPattern().getAtackPattern(enemyData.getAtackPattern()).getExplanation());
	}
	
	void setWeapon(BattleUnit unitMainData, BattleUnit unitLeftData) {
		Stream.of(Atack.values()).forEach(i -> weapon[i.getId() + 1].setText(i.getLabel()));
		weapon[5].setText("距離タイプ");
		weapon[6].setText("属性");
		weapon[7].setText("ターゲット");
		weapon[9].setText("左武器");
		if(!unitLeftData.getElement().isEmpty()) {
			Stream.of(Atack.values()).forEach(i -> weapon[i.getId() + 10].setText("" + unitLeftData.getWeapon().get(i.getId())));
			weapon[14].setText("" + unitMainData.getType().getLabel());
			weapon[15].setText("" + getElement(unitLeftData.getElement()));
			weapon[16].setText("" + unitLeftData.getAtackPattern().getExplanation());
		}
		weapon[18].setText("右武器");
		if(!unitMainData.getElement().isEmpty()) {
			Stream.of(Atack.values()).forEach(i -> weapon[i.getId() + 19].setText("" + unitMainData.getWeapon().get(i.getId())));
			weapon[23].setText("" + unitMainData.getType().getLabel());
			weapon[24].setText("" + getElement(unitMainData.getElement()));
			weapon[25].setText("" + unitMainData.getAtackPattern().getExplanation());
		}
	}
	
	void setWeapon(BattleFacility facilityData) {
		Stream.of(Atack.values()).forEach(i -> weapon[i.getId() + 1].setText(i.getLabel()));
		weapon[5].setText("属性");
		weapon[6].setText("ターゲット");
		weapon[9].setText("攻撃性能");
		if(!facilityData.getElement().isEmpty()) {
			Stream.of(Atack.values()).forEach(i -> weapon[i.getId() + 10].setText("" + facilityData.getWeapon().get(i.getId())));
			weapon[14].setText("" + getElement(facilityData.getElement()));
		}
	}
	
	void setWeapon(BattleEnemy enemyData) {
		Stream.of(Atack.values()).forEach(i -> {
			weapon[i.getId() + 1].setText(i.getLabel());
			weapon[i.getId() + 10].setText("" + enemyData.getWeapon().get(i.getId()));
		});
		weapon[5].setText("移動タイプ");
		weapon[6].setText("種別");
		weapon[7].setText("属性");
		weapon[8].setText("ターゲット");
		weapon[9].setText("攻撃性能");
		weapon[14].setText("" + enemyData.getMove().getLabel());
		weapon[15].setText("" + enemyData.getType().getLabel());
		weapon[16].setText("" + getElement(enemyData.getElement()));
		weapon[17].setText(enemyData.getAtackPattern().getExplanation());
	}
	
	String getElement(List<Element> elementList) {
		if(elementList.isEmpty()) {
			return "なし";
		}
		String element = "";
		for(Element i: elementList) {
			element += i.getLabel() + ", ";
		}
		return element.substring(0, element.length() - 2);
	}
	
	void setUnit(List<Double> statusList, String comment) {
		Stream.of(CoreUnit.values()).forEach(i -> {
			unit[i.getId()].setText(i.getLabel());
			unit[i.getId() + 6].setText(statusList.get(i.getId()) + comment);
		});
	}
	
	<T extends Enum<T> & DefaultEnum<String>>void setUnit(List<Integer> statusList, T[] data) {
		IntStream.range(0, statusList.size()).forEach(i -> {
			unit[i].setText(data[i].getLabel());
			if(statusList.get(i) < 0) {
				unit[i + 6].setText("∞");
			}else {
				unit[i + 6].setText(statusList.get(i) + "");
			}
		});
	}
	
	<T extends Enum<T> & DefaultEnum<String>>void setUnit(BattleData battle, T[] data) {
		List<Integer> statusList = battle.getUnit();
		IntStream.range(0, statusList.size()).forEach(i -> {
			unit[i].setText(data[i].getLabel());
			if(statusList.get(i) < 0) {
				unit[i + 6].setText("∞");
			}else if(i == 1) {
				unit[i + 6].setText(battle.getNowHP() + "");
			}else {
				unit[i + 6].setText(statusList.get(i) + "");
			}
		});
	}
	
	void setCut(List<Integer> cutList) {
		Stream.of(Element.values()).forEach(i -> cut[i.getId()].setText(i.getLabel() + ((i == Element.SUPPORT)? "倍率": "耐性")));
		IntStream.range(0, cutList.size()).forEach(i -> cut[i + 12].setText(cutList.get(i) + "%"));
	}
}