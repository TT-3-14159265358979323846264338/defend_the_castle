package defendthecastle.itemget;

import java.util.List;
import java.util.function.Function;
import java.util.stream.IntStream;

import javax.swing.DefaultListModel;
import javax.swing.JDialog;
import javax.swing.JList;
import javax.swing.JScrollPane;

import defaultdata.DefaultUnit;
import defaultdata.core.CoreData;
import defaultdata.weapon.WeaponData;

//ガチャ詳細
class GachaLineup extends JDialog{
	protected GachaLineup(GachaInformation GachaInformation) {
		setModalityType(ModalityType.APPLICATION_MODAL);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setResizable(false);
		setTitle("ガチャラインナップ");
		setSize(300, 600);
		setLocationRelativeTo(null);
		add(getLineupScrollPane(GachaInformation));
		setVisible(true);
	}
	
	private JScrollPane getLineupScrollPane(GachaInformation GachaInformation) {
		Function<Integer, String> getRarity = (rarity) -> {
			return "★" + rarity + " ";
		};
		Function<String, String> getName = (name) -> {
			return String.format("%-" + (60 - name.getBytes().length) + "s", name);
		};
		Function<Double, String> getRatio = (value) -> {
			return String.format("%.1f", value) + "%";
		};
		List<Integer> coreLineup = GachaInformation.getCoreLineup();
		List<Double> coreRatio = GachaInformation.getCoreRatio();
		List<Integer> weaponLineup = GachaInformation.getWeaponLineup();
		List<Double> weaponRatio = GachaInformation.getWeaponRatio();
		DefaultListModel<String> lineup = new DefaultListModel<String>();
		lineup.addElement("【コア確率】 " + getRatio.apply(getTotal(coreRatio)));
		lineup.addElement(" ");
		IntStream.range(0, coreLineup.size()).forEach(i -> {
			CoreData CoreData = DefaultUnit.CORE_DATA_MAP.get(coreLineup.get(i));
			String coreName = getRarity.apply(CoreData.getRarity()) + CoreData.getName();
			lineup.addElement(getName.apply(coreName) + getRatio.apply(coreRatio.get(i)));
		});
		if(getTotal(coreRatio) != 0) {
			lineup.addElement(" ");
		}
		lineup.addElement("【武器確率】 " + getRatio.apply(getTotal(weaponRatio)));
		lineup.addElement(" ");
		IntStream.range(0, weaponLineup.size()).forEach(i -> {
			WeaponData WeaponData = DefaultUnit.WEAPON_DATA_MAP.get(weaponLineup.get(i));
			String weaponName = getRarity.apply(WeaponData.getRarity()) + WeaponData.getName();
			lineup.addElement(getName.apply(weaponName) + getRatio.apply(weaponRatio.get(i)));
		});
		JList<String> lineupJList = new JList<String>(lineup);
		lineupJList.setEnabled(false);
		return new JScrollPane(lineupJList);
	}
	
	private double getTotal(List<Double> list) {
		return list.stream().mapToDouble(i -> i).sum();
	}
}