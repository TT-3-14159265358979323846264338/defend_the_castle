package defendthecastle.screendisplay;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.IntStream;

import javax.swing.JLabel;

import defaultdata.DefaultAtackPattern;
import defaultdata.DefaultEnemy;
import defaultdata.DefaultUnit;
import defaultdata.EditImage;
import defaultdata.core.CoreData;
import defaultdata.enemy.EnemyData;
import defaultdata.weapon.WeaponData;
import defendthecastle.battle.BattleData;
import defendthecastle.battle.BattleEnemy;
import defendthecastle.battle.BattleFacility;
import defendthecastle.battle.BattleUnit;

//ユニットデータ取込み
public class DisplayStatus extends StatusPanel{
	public void core(BufferedImage image, int number) {
		CoreData CoreData = DefaultUnit.CORE_DATA_MAP.get(number);
		setItem();
		setUnitName(getRarity(CoreData.getRarity()) + CoreData.getName());
		setExplanation(explanationComment(CoreData.getExplanation()));
		setWeapon(CoreData.getWeaponStatus());
		setUnit(CoreData.getUnitStatus(), "倍");
		setCut(CoreData.getCutStatus());
		super.setStatusPanel(image);
	}
	
	public void weapon(BufferedImage image, int number) {
		WeaponData WeaponData = DefaultUnit.WEAPON_DATA_MAP.get(number);
		setItem();
		setUnitName(getRarity(WeaponData.getRarity()) + WeaponData.getName());
		setExplanation(explanationComment(WeaponData.getExplanation()));
		setWeapon(WeaponData);
		setUnit(WeaponData.getUnitStatus(), DefaultUnit.WEAPON_UNIT_MAP);
		setCut(WeaponData.getCutStatus());
		super.setStatusPanel(image);
	}
	
	public void unit(BufferedImage image, List<Integer> compositionList) {
		StatusCalculation StatusCalculation = new StatusCalculation(compositionList);
		setItem();
		setUnitName(compositionList);
		setExplanation(compositionList);
		setWeapon(StatusCalculation, compositionList);
		setUnit(StatusCalculation.getUnitStatus(), DefaultUnit.WEAPON_UNIT_MAP);
		setCut(StatusCalculation.getCutStatus());
		super.setStatusPanel(image);
	}
	
	public void enemy(EnemyData EnemyData) {
		setItem();
		setUnitName(EnemyData.getName());
		setExplanation(explanationComment(EnemyData.getExplanation()));
		setWeapon(EnemyData);
		setUnit(EnemyData.getUnitStatus(), DefaultEnemy.UNIT_MAP);
		setCut(EnemyData.getCutStatus());
		super.setStatusPanel(EnemyData.getImage(2));
	}
	
	public void unit(BattleUnit unitMainData, BattleUnit unitLeftData) {
		setItem();
		setUnitName(unitMainData.getComposition());
		setExplanation(unitMainData.getComposition());
		setWeapon(unitMainData, unitLeftData);
		setUnit(unitMainData, DefaultUnit.WEAPON_UNIT_MAP);
		setCut(unitMainData.getCut());
		super.setStatusPanel(EditImage.compositeImage(Arrays.asList(unitMainData.getDefaultImage(), unitMainData.getDefaultCoreImage(), unitLeftData.getDefaultImage())));
	}
	
	public void facility(BattleFacility facilityData) {
		setItem();
		setUnitName(facilityData.getName());
		setExplanation(explanationComment(facilityData.getExplanation()));
		setWeapon(facilityData);
		setUnit(facilityData, DefaultUnit.WEAPON_UNIT_MAP);
		setCut(facilityData.getCut());
		super.setStatusPanel(facilityData.getDefaultImage());
	}
	
	public void enemy(BattleEnemy enemyData) {
		setItem();
		setUnitName(enemyData.getName());
		setExplanation(explanationComment(enemyData.getExplanation()));
		setWeapon(enemyData);
		setUnit(enemyData, DefaultEnemy.UNIT_MAP);
		setCut(enemyData.getCut());
		super.setStatusPanel(enemyData.getDefaultImage());
	}
	
	private void setItem() {
		item[0].setText("【名称/説明】");
		item[1].setText("【武器ステータス】");
		item[2].setText("【ユニットステータス】");
	}
	
	private void setUnitName(String name) {
		unitName[2].setText(name);
	}
	
	private void setUnitName(List<Integer> composition) {
		Consumer<JLabel> noWeapon = (label) -> {
			label.setText("no weapon");
		};
		try {
			WeaponData rightWeapon = DefaultUnit.WEAPON_DATA_MAP.get(composition.get(DefaultUnit.RIGHT_WEAPON));
			unitName[0].setText(getRarity(rightWeapon.getRarity()) + explanationComment(rightWeapon.getName()));
		}catch(Exception e) {
			noWeapon.accept(unitName[0]);
		}
		CoreData CoreData = DefaultUnit.CORE_DATA_MAP.get(composition.get(DefaultUnit.CORE));
		unitName[1].setText(getRarity(CoreData.getRarity()) + explanationComment(CoreData.getName()));
		try {
			WeaponData leftWeapon = DefaultUnit.WEAPON_DATA_MAP.get(composition.get(DefaultUnit.LEFT_WEAPON));
			unitName[2].setText(getRarity(leftWeapon.getRarity()) + explanationComment(leftWeapon.getName()));
		}catch(Exception e) {
			noWeapon.accept(unitName[2]);
		}
	}
	
	private String getRarity(int rarity) {
		return "★" + rarity + " ";
	}
	
	private void setExplanation(String comment) {
		explanation[0].setText("");
		explanation[1].setText("");
		explanation[2].setText(comment);
	}
	
	private void setExplanation(List<Integer> composition) {
		try {
			explanation[0].setText(explanationComment(DefaultUnit.WEAPON_DATA_MAP.get(composition.get(DefaultUnit.RIGHT_WEAPON)).getExplanation()));
		}catch(Exception ignore) {
			//右武器を装備していないので、無視する
		}
		explanation[1].setText(explanationComment(DefaultUnit.CORE_DATA_MAP.get(composition.get(DefaultUnit.CORE)).getExplanation()));
		try {
			explanation[2].setText(explanationComment(DefaultUnit.WEAPON_DATA_MAP.get(composition.get(DefaultUnit.LEFT_WEAPON)).getExplanation()));
		}catch(Exception ignore) {
			//左武器を装備していないので、無視する
		}
	}
	
	private String explanationComment(String comment) {
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
		if(0 <= compositionList.get(DefaultUnit.LEFT_WEAPON)) {
			WeaponData WeaponData = DefaultUnit.WEAPON_DATA_MAP.get(compositionList.get(DefaultUnit.LEFT_WEAPON));
			IntStream.range(0, DefaultUnit.WEAPON_WEAPON_MAP.size()).forEach(i -> weapon[i + 10].setText("" + StatusCalculation.getLeftWeaponStatus().get(i)));
			weapon[14].setText("" + DefaultUnit.DISTANCE_MAP.get(WeaponData.getDistance()));
			weapon[15].setText("" + DefaultUnit.HANDLE_MAP.get(WeaponData.getHandle()));
			weapon[16].setText("" + getElement(StatusCalculation.getLeftElement()));
			weapon[17].setText("" + new DefaultAtackPattern().getAtackPattern(WeaponData.getAtackPattern()).getExplanation());
		}
		weapon[18].setText("右武器");
		if(0 <= compositionList.get(DefaultUnit.RIGHT_WEAPON)) {
			WeaponData WeaponData = DefaultUnit.WEAPON_DATA_MAP.get(compositionList.get(DefaultUnit.RIGHT_WEAPON));
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
		if(!unitLeftData.getElement().isEmpty()) {
			IntStream.range(0, DefaultUnit.WEAPON_WEAPON_MAP.size()).forEach(i -> weapon[i + 10].setText("" + unitLeftData.getWeapon().get(i)));
			weapon[14].setText("" + DefaultUnit.DISTANCE_MAP.get(unitMainData.getType()));
			weapon[15].setText("" + getElement(unitLeftData.getElement()));
			weapon[16].setText("" + unitLeftData.getAtackPattern().getExplanation());
		}
		weapon[18].setText("右武器");
		if(!unitMainData.getElement().isEmpty()) {
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
		if(elementList.isEmpty()) {
			return "なし";
		}
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