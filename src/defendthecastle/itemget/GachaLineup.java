package defendthecastle.itemget;

import java.util.List;
import java.util.stream.IntStream;

import javax.swing.DefaultListModel;
import javax.swing.JDialog;
import javax.swing.JList;
import javax.swing.JScrollPane;

import defaultdata.Core;
import defaultdata.Weapon;

//ガチャ詳細
class GachaLineup extends JDialog{
	GachaLineup(GachaInformation gachaInformation) {
		setModalityType(ModalityType.APPLICATION_MODAL);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setResizable(false);
		setTitle("ガチャラインナップ");
		setSize(300, 600);
		setLocationRelativeTo(null);
		add(createLineupScroll(gachaInformation));
		setVisible(true);
	}
	
	JScrollPane createLineupScroll(GachaInformation gachaInformation) {
		var lineupJList = new JList<String>(createModel(gachaInformation));
		lineupJList.setEnabled(false);
		return new JScrollPane(lineupJList);
	}
	
	DefaultListModel<String> createModel(GachaInformation gachaInformation){
		var lineup = new DefaultListModel<String>();
		var hasNotEnded = addCore(gachaInformation, lineup); 
		if(hasNotEnded) {
			lineup.addElement(" ");
		}
		addWeapon(gachaInformation, lineup);
		return lineup;
	}
	
	boolean addCore(GachaInformation gachaInformation, DefaultListModel<String> lineup) {
		List<Core> coreLineup = gachaInformation.getCoreLineup();
		List<Double> coreRatio = gachaInformation.getCoreRatio();
		lineup.addElement("【コア確率】 " + ratioName(getTotal(coreRatio)));
		lineup.addElement(" ");
		IntStream.range(0, coreLineup.size()).forEach(i -> {
			var coreData = coreLineup.get(i).getLabel();
			var coreName = String.format("%s%s", rarityName(coreData.getRarity()), coreData.getName());
			lineup.addElement(String.format("%s%s", unitName(coreName), ratioName(coreRatio.get(i))));
		});
		return getTotal(coreRatio) != 0;
	}
	
	void addWeapon(GachaInformation gachaInformation, DefaultListModel<String> lineup) {
		List<Weapon> weaponLineup = gachaInformation.getWeaponLineup();
		List<Double> weaponRatio = gachaInformation.getWeaponRatio();
		lineup.addElement("【武器確率】 " + ratioName(getTotal(weaponRatio)));
		lineup.addElement(" ");
		IntStream.range(0, weaponLineup.size()).forEach(i -> {
			var weaponData = weaponLineup.get(i).getLabel();
			var weaponName = String.format("%s%s", rarityName(weaponData.getRarity()), weaponData.getName());
			lineup.addElement(String.format("%s%s", unitName(weaponName), ratioName(weaponRatio.get(i))));
		});
	}
	
	String rarityName(int rarity) {
		return String.format("★%s ", rarity);
	}
	
	String unitName(String name) {
		return String.format("%-" + (60 - name.getBytes().length) + "s", name);
	}
	
	String ratioName(double value) {
		return String.format("%.1f", value) + "%";
	}
	
	double getTotal(List<Double> list) {
		return list.stream().mapToDouble(i -> i).sum();
	}
}