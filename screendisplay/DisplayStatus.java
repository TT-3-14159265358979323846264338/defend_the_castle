package screendisplay;

import java.awt.image.BufferedImage;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

import battle.BattleData;
import battle.BattleEnemy;
import battle.BattleFacility;
import battle.BattleUnit;
import defaultdata.DefaultAtackPattern;
import defaultdata.DefaultEnemy;
import defaultdata.DefaultUnit;
import defaultdata.EditImage;
import defaultdata.core.CoreData;
import defaultdata.enemy.EnemyData;
import defaultdata.weapon.WeaponData;

//ユニットデータ取込み
public class DisplayStatus extends StatusPanel{
	public void core(BufferedImage image, int number) {
		CoreData CoreData = DefaultUnit.CORE_DATA_MAP.get(number);
		setLabelName(getRarity(CoreData.getRarity()) + CoreData.getName());
		setWeapon(CoreData.getWeaponStatus());
		setUnit(CoreData.getUnitStatus(), "倍");
		setCut(CoreData.getCutStatus());
		super.setStatusPanel(image);
	}
	
	public void weapon(BufferedImage image, int number) {
		WeaponData WeaponData = DefaultUnit.WEAPON_DATA_MAP.get(number);
		setLabelName(getRarity(WeaponData.getRarity()) + WeaponData.getName());
		setWeapon(WeaponData);
		setUnit(WeaponData.getUnitStatus(), DefaultUnit.WEAPON_UNIT_MAP);
		setCut(WeaponData.getCutStatus());
		super.setStatusPanel(image);
	}
	
	public void unit(BufferedImage image, List<Integer> compositionList) {
		StatusCalculation StatusCalculation = new StatusCalculation(compositionList);
		setLabelName(getUnitName(compositionList));
		setWeapon(StatusCalculation, compositionList);
		setUnit(StatusCalculation.getUnitStatus(), DefaultUnit.WEAPON_UNIT_MAP);
		setCut(StatusCalculation.getCutStatus());
		super.setStatusPanel(image);
	}
	
	public void enemy(EnemyData EnemyData) {
		setLabelName(EnemyData.getName());
		setWeapon(EnemyData);
		setUnit(EnemyData.getUnitStatus(), DefaultEnemy.UNIT_MAP);
		setCut(EnemyData.getCutStatus());
		super.setStatusPanel(EnemyData.getImage(2));
	}
	
	public void unit(BattleUnit unitMainData, BattleUnit unitLeftData) {
		setLabelName(unitMainData.getName());
		setWeapon(unitMainData, unitLeftData);
		setUnit(unitMainData, DefaultUnit.WEAPON_UNIT_MAP);
		setCut(unitMainData.getCut());
		super.setStatusPanel(EditImage.compositeImage(Arrays.asList(unitMainData.getDefaultImage(), unitMainData.getDefaultCoreImage(), unitLeftData.getDefaultImage())));
	}
	
	public void facility(BattleFacility facilityData) {
		setLabelName(facilityData.getName());
		setWeapon(facilityData);
		setUnit(facilityData, DefaultUnit.WEAPON_UNIT_MAP);
		setCut(facilityData.getCut());
		super.setStatusPanel(facilityData.getDefaultImage());
	}
	
	public void enemy(BattleEnemy enemyData) {
		setLabelName(enemyData.getName());
		setWeapon(enemyData);
		setUnit(enemyData, DefaultEnemy.UNIT_MAP);
		setCut(enemyData.getCut());
		super.setStatusPanel(enemyData.getDefaultImage());
	}
	
	private void setLabelName(String unitName) {
		name[0].setText("【名称】");
		name[1].setText(unitName);
		name[2].setText("【武器ステータス】");
		name[3].setText("【ユニットステータス】");
	}
	
	private String getRarity(int rarity) {
		return "★" + rarity + " ";
	}
	
	public String getUnitName(List<Integer> compositionList) {
		String name = "";
		try {
			WeaponData WeaponData = DefaultUnit.WEAPON_DATA_MAP.get(compositionList.get(2));
			name += getRarity(WeaponData.getRarity()) + WeaponData.getName() + " - ";
		}catch(Exception ignore) {
			//左武器を装備していないので、無視する
		}
		CoreData CoreData = DefaultUnit.CORE_DATA_MAP.get(compositionList.get(1));
		name += getRarity(CoreData.getRarity()) + CoreData.getName() + " - ";
		try {
			WeaponData WeaponData = DefaultUnit.WEAPON_DATA_MAP.get(compositionList.get(0));
			name += getRarity(WeaponData.getRarity()) + WeaponData.getName() + " - ";
		}catch(Exception ignore) {
			//右武器を装備していないので、無視する
		}
		return name.substring(0, name.length() - 3);
	}
	
	private void setWeapon(List<Double> statusList) {
		IntStream.range(0, DefaultUnit.CORE_WEAPON_MAP.size()).forEach(i -> {
			weapon[i + 1].setText(DefaultUnit.CORE_WEAPON_MAP.get(i));
			weapon[i + 10].setText(statusList.get(i) + "倍");
		});
		weapon[9].setText("武器性能");
	}
	
	private void setWeapon(WeaponData WeaponData) {
		IntStream.range(0, DefaultUnit.WEAPON_WEAPON_MAP.size()).forEach(i -> {
			weapon[i + 1].setText(DefaultUnit.WEAPON_WEAPON_MAP.get(i));
			weapon[i + 10].setText("" + WeaponData.getWeaponStatus().get(i));
		});
		weapon[5].setText("距離タイプ");
		weapon[6].setText("装備タイプ");
		weapon[7].setText("属性");
		weapon[8].setText("ターゲット");
		weapon[9].setText("武器性能");
		weapon[14].setText("" + DefaultUnit.DISTANCE_MAP.get(WeaponData.getDistance()));
		weapon[15].setText("" + DefaultUnit.HANDLE_MAP.get(WeaponData.getHandle()));
		weapon[16].setText("" + getElement(WeaponData.getElement()));
		weapon[17].setText("" + new DefaultAtackPattern().getAtackPattern(WeaponData.getAtackPattern()).getExplanation());
	}
	
	private void setWeapon(StatusCalculation StatusCalculation, List<Integer> compositionList) {
		IntStream.range(0, DefaultUnit.WEAPON_WEAPON_MAP.size()).forEach(i -> weapon[i + 1].setText(DefaultUnit.WEAPON_WEAPON_MAP.get(i)));
		weapon[5].setText("距離タイプ");
		weapon[6].setText("装備タイプ");
		weapon[7].setText("属性");
		weapon[8].setText("ターゲット");
		weapon[9].setText("左武器");
		if(0 <= compositionList.get(2)) {
			WeaponData WeaponData = DefaultUnit.WEAPON_DATA_MAP.get(compositionList.get(2));
			IntStream.range(0, DefaultUnit.WEAPON_WEAPON_MAP.size()).forEach(i -> weapon[i + 10].setText("" + StatusCalculation.getLeftWeaponStatus().get(i)));
			weapon[14].setText("" + DefaultUnit.DISTANCE_MAP.get(WeaponData.getDistance()));
			weapon[15].setText("" + DefaultUnit.HANDLE_MAP.get(WeaponData.getHandle()));
			weapon[16].setText("" + getElement(StatusCalculation.getLeftElement()));
			weapon[17].setText("" + new DefaultAtackPattern().getAtackPattern(WeaponData.getAtackPattern()).getExplanation());
		}
		weapon[18].setText("右武器");
		if(0 <= compositionList.get(0)) {
			WeaponData WeaponData = DefaultUnit.WEAPON_DATA_MAP.get(compositionList.get(0));
			IntStream.range(0, DefaultUnit.WEAPON_WEAPON_MAP.size()).forEach(i -> weapon[i + 19].setText("" + StatusCalculation.getRightWeaponStatus().get(i)));
			weapon[23].setText("" + DefaultUnit.DISTANCE_MAP.get(WeaponData.getDistance()));
			weapon[24].setText("" + DefaultUnit.HANDLE_MAP.get(WeaponData.getHandle()));
			weapon[25].setText("" + getElement(StatusCalculation.getRightElement()));
			weapon[26].setText("" + new DefaultAtackPattern().getAtackPattern(WeaponData.getAtackPattern()).getExplanation());
		}
	}
	
	private void setWeapon(EnemyData EnemyData) {
		IntStream.range(0, DefaultEnemy.WEAPON_MAP.size()).forEach(i -> {
			weapon[i + 1].setText(DefaultEnemy.WEAPON_MAP.get(i));
			weapon[i + 10].setText("" + EnemyData.getWeaponStatus().get(i));
		});
		weapon[5].setText("移動タイプ");
		weapon[6].setText("種別");
		weapon[7].setText("属性");
		weapon[8].setText("ターゲット");
		weapon[9].setText("攻撃性能");
		weapon[14].setText("" + DefaultEnemy.MOVE_MAP.get(EnemyData.getMove()));
		weapon[15].setText("" + DefaultEnemy.TYPE_MAP.get(EnemyData.getType()));
		weapon[16].setText("" + getElement(EnemyData.getElement()));
		weapon[17].setText("" + new DefaultAtackPattern().getAtackPattern(EnemyData.getAtackPattern()).getExplanation());
	}
	
	private void setWeapon(BattleUnit unitMainData, BattleUnit unitLeftData) {
		IntStream.range(0, DefaultUnit.WEAPON_WEAPON_MAP.size()).forEach(i -> weapon[i + 1].setText(DefaultUnit.WEAPON_WEAPON_MAP.get(i)));
		weapon[5].setText("距離タイプ");
		weapon[6].setText("属性");
		weapon[7].setText("ターゲット");
		weapon[9].setText("左武器");
		if(0 <= unitLeftData.getElement().get(0)) {
			IntStream.range(0, DefaultUnit.WEAPON_WEAPON_MAP.size()).forEach(i -> weapon[i + 10].setText("" + unitLeftData.getWeapon().get(i)));
			weapon[14].setText("" + DefaultUnit.DISTANCE_MAP.get(unitMainData.getType()));
			weapon[15].setText("" + getElement(unitLeftData.getElement()));
			weapon[16].setText("" + unitLeftData.getAtackPattern().getExplanation());
		}
		weapon[18].setText("右武器");
		if(0 <= unitMainData.getElement().get(0)) {
			IntStream.range(0, DefaultUnit.WEAPON_WEAPON_MAP.size()).forEach(i -> weapon[i + 19].setText("" + unitMainData.getWeapon().get(i)));
			weapon[23].setText("" + DefaultUnit.DISTANCE_MAP.get(unitMainData.getType()));
			weapon[24].setText("" + getElement(unitMainData.getElement()));
			weapon[25].setText("" + unitMainData.getAtackPattern().getExplanation());
		}
	}
	
	private void setWeapon(BattleFacility facilityData) {
		IntStream.range(0, DefaultUnit.WEAPON_WEAPON_MAP.size()).forEach(i -> weapon[i + 1].setText(DefaultUnit.WEAPON_WEAPON_MAP.get(i)));
		weapon[5].setText("属性");
		weapon[6].setText("ターゲット");
		weapon[9].setText("攻撃性能");
		if(!facilityData.getElement().isEmpty()) {
			IntStream.range(0, DefaultUnit.WEAPON_WEAPON_MAP.size()).forEach(i -> weapon[i + 10].setText("" + facilityData.getWeapon().get(i)));
			weapon[14].setText("" + getElement(facilityData.getElement()));
		}
	}
	
	private void setWeapon(BattleEnemy enemyData) {
		IntStream.range(0, DefaultEnemy.WEAPON_MAP.size()).forEach(i -> {
			weapon[i + 1].setText(DefaultEnemy.WEAPON_MAP.get(i));
			weapon[i + 10].setText("" + enemyData.getWeapon().get(i));
		});
		weapon[5].setText("移動タイプ");
		weapon[6].setText("種別");
		weapon[7].setText("属性");
		weapon[8].setText("ターゲット");
		weapon[9].setText("攻撃性能");
		weapon[14].setText("" + DefaultEnemy.MOVE_MAP.get(enemyData.getMove()));
		weapon[15].setText("" + DefaultEnemy.TYPE_MAP.get(enemyData.getType()));
		weapon[16].setText("" + getElement(enemyData.getElement()));
		weapon[17].setText(enemyData.getAtackPattern().getExplanation());
	}
	
	private String getElement(List<Integer> elementList) {
		String element = "";
		for(int i: elementList) {
			element += DefaultUnit.ELEMENT_MAP.get(i) + ", ";
		}
		return element.substring(0, element.length() - 2);
	}
	
	private void setUnit(List<Double> statusList, String comment) {
		IntStream.range(0, statusList.size()).forEach(i -> {
			unit[i].setText(DefaultUnit.CORE_UNIT_MAP.get(i));
			unit[i + 6].setText(statusList.get(i) + comment);
		});
	}
	
	private void setUnit(List<Integer> statusList, Map<Integer, String> map) {
		IntStream.range(0, statusList.size()).forEach(i -> {
			unit[i].setText(map.get(i));
			if(statusList.get(i) < 0) {
				unit[i + 6].setText("∞");
			}else {
				unit[i + 6].setText(statusList.get(i) + "");
			}
		});
	}
	
	private void setUnit(BattleData data, Map<Integer, String> map) {
		List<Integer> statusList = data.getUnit();
		IntStream.range(0, statusList.size()).forEach(i -> {
			unit[i].setText(map.get(i));
			if(statusList.get(i) < 0) {
				unit[i + 6].setText("∞");
			}else if(i == 1) {
				unit[i + 6].setText(data.getNowHP() + "");
			}else {
				unit[i + 6].setText(statusList.get(i) + "");
			}
		});
	}
	
	private void setCut(List<Integer> cutList) {
		IntStream.range(0, cutList.size()).forEach(i -> cut[i].setText(DefaultUnit.ELEMENT_MAP.get(i) + ((i == 11)? "倍率": "耐性")));
		IntStream.range(0, cutList.size()).forEach(i -> cut[i + 12].setText(cutList.get(i) + "%"));
	}
}