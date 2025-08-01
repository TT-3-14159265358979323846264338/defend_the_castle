package screendisplay;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;

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
		setUnit(unitMainData.getUnit(), DefaultUnit.WEAPON_UNIT_MAP);
		setCut(unitMainData.getCut());
		super.setStatusPanel(EditImage.compositeImage(Arrays.asList(unitMainData.getDefaultImage(), unitMainData.getDefaultCoreImage(), unitLeftData.getDefaultImage())));
	}
	
	public void facility(BattleFacility facilityData) {
		setLabelName(facilityData.getName());
		setWeapon(facilityData);
		setUnit(facilityData.getUnit(), DefaultUnit.WEAPON_UNIT_MAP);
		setCut(facilityData.getCut());
		super.setStatusPanel(facilityData.getDefaultImage());
	}
	
	public void enemy(BattleEnemy enemyData) {
		setLabelName(enemyData.getName());
		setWeapon(enemyData);
		setUnit(enemyData.getUnit(), DefaultEnemy.UNIT_MAP);
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
	
	private void setCut(List<Integer> cutList) {
		IntStream.range(0, cutList.size()).forEach(i -> cut[i].setText(DefaultUnit.ELEMENT_MAP.get(i) + ((i == 11)? "倍率": "耐性")));
		IntStream.range(0, cutList.size()).forEach(i -> cut[i + 12].setText(cutList.get(i) + "%"));
	}
}

//ステータス表示用ダイアログ
class StstusDialog extends JDialog{
	protected StstusDialog(StatusPanel StatusPanel) {
		setModalityType(ModalityType.APPLICATION_MODAL);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setResizable(false);
		setTitle("ステータス");
		setSize(720, 700);
		setLocationRelativeTo(null);
		add(StatusPanel);
		setVisible(true);
	}
}

//ステータス表示
class StatusPanel extends JPanel{
	JLabel imageLabel;
	Function<Integer, JLabel[]> initialize = (size) -> {
		return IntStream.range(0, size).mapToObj(i -> new JLabel()).toArray(JLabel[]::new);
	};
	JLabel[] name = initialize.apply(4);
	JLabel[] weapon = initialize.apply(27);
	JLabel[] unit = initialize.apply(12);
	JLabel[] cut = initialize.apply(24);
	int startX = 20;
	int startY = 20;
	int sizeX = 110;
	int sizeY = 30;
	
	protected void setStatusPanel(BufferedImage image) {
		setBackground(new Color(240, 170, 80));
		this.imageLabel = new JLabel(new ImageIcon(image));
		addLabel();
		setLabelFont();
		setLabelHorizontal();
		new StstusDialog(this);
	}
	
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		setLabelPosition();
		drawBackground(g);
	}
	
	private void addLabel() {
		Consumer<JLabel[]> addLabel = (label) -> {
			Stream.of(label).forEach(i -> add(i));
		};
		add(imageLabel);
		addLabel.accept(name);
		addLabel.accept(weapon);
		addLabel.accept(unit);
		addLabel.accept(cut);
	}
	
	private void setLabelFont() {
		BiConsumer<JLabel[], Integer> setLabel = (label, size) -> {
			String fontName = "ＭＳ ゴシック";
			int bold = Font.BOLD;
			Stream.of(label).forEach(i -> {
				int fontSize = 15;
				int width = getFontMetrics(new Font(fontName, bold, fontSize)).stringWidth(i.getText());
				while(size < width) {
					fontSize--;
					width = getFontMetrics(new Font(fontName, bold, fontSize)).stringWidth(i.getText());
				}
				i.setFont(new Font(fontName, bold, fontSize));
			});
		};
		setLabel.accept(name, sizeX * 5);
		setLabel.accept(weapon, sizeX);
		setLabel.accept(unit, sizeX);
		setLabel.accept(cut, sizeX);
	}
	
	private void setLabelHorizontal() {
		Consumer<JLabel[]> setLabel = (label) -> {
			Stream.of(label).forEach(i -> i.setHorizontalAlignment(JLabel.CENTER));
		};
		imageLabel.setHorizontalAlignment(JLabel.CENTER);
		setLabel.accept(weapon);
		setLabel.accept(unit);
		setLabel.accept(cut);
	}
	
	private void setLabelPosition() {
		name[0].setBounds(startX, startY, sizeX, sizeY);
		name[1].setBounds(startX + 20, startY + sizeY, sizeX * 5, sizeY);
		name[2].setBounds(startX + sizeX * 3, startY + sizeY * 3, sizeX * 3, sizeY);
		name[3].setBounds(startX, startY + sizeY * 14, sizeX * 3, sizeY);
		imageLabel.setBounds(startX, startY + sizeY * 3, sizeX * 3, sizeY * 10);
		IntStream.range(0, weapon.length).forEach(i -> weapon[i].setBounds(startX + (i / 9 + 3) * sizeX, startY + (i % 9 + 4) * sizeY, sizeX, sizeY));
		IntStream.range(0, unit.length).forEach(i -> unit[i].setBounds(startX + (i / 6) * sizeX, startY + (i % 6 + 15) * sizeY, sizeX, sizeY));
		IntStream.range(0, cut.length / 2).forEach(i -> {
			cut[i].setBounds(startX + (i / 6 * 2 + 2) * sizeX, startY + (i % 6 + 15) * sizeY, sizeX, sizeY);
			cut[i + cut.length / 2].setBounds(startX + (i / 6 * 2 + 3) * sizeX, startY + (i % 6 + 15) * sizeY, sizeX, sizeY);
		});
	}
	
	private void drawBackground(Graphics g) {
		g.setColor(Color.WHITE);	
		g.fillRect(startX, startY, sizeX * 6, sizeY * 2);
		g.fillRect(startX, startY + sizeY * 3, sizeX * 6, sizeY * 10);
		g.fillRect(startX, startY + sizeY * 14, sizeX * 6, sizeY * 7);
		g.setColor(Color.LIGHT_GRAY);
		g.fillRect(startX + sizeX * 3, startY + sizeY * 4, sizeX * 3, sizeY);
		g.fillRect(startX + sizeX * 3, startY + sizeY * 5, sizeX, sizeY * 8);
		IntStream.range(0, 3).forEach(i -> g.fillRect(startX + sizeX * i * 2, startY + sizeY * 15, sizeX, sizeY * 6));
		g.setColor(Color.YELLOW);
		g.fillRect(startX + sizeX * 4, startY + sizeY * 5, sizeX * 2, sizeY * 8);
		IntStream.range(0, 3).forEach(i -> g.fillRect(startX + sizeX * (i * 2 + 1), startY + sizeY * 15, sizeX, sizeY * 6));
		g.setColor(Color.BLACK);
		Graphics2D g2 = (Graphics2D)g;
		g2.setStroke(new BasicStroke(2));
		g.drawRect(startX, startY, sizeX * 6, sizeY * 2);
		g.drawRect(startX, startY + sizeY * 3, sizeX * 3, sizeY * 10);
		g.drawRect(startX + sizeX * 3, startY + sizeY * 3, sizeX * 3, sizeY * 10);
		g.drawRect(startX, startY + sizeY * 14, sizeX * 6, sizeY * 7);
		g2.setStroke(new BasicStroke(1));
		g.drawLine(startX + sizeX * 3, startY + sizeY * 4, startX + sizeX * 4, startY + sizeY * 5);
		IntStream.range(0, 9).forEach(i -> g.drawLine(startX + sizeX * 3, startY + sizeY * (4 + i), startX + sizeX * 6, startY + sizeY * (4 + i)));
		IntStream.range(0, 2).forEach(i -> g.drawLine(startX + sizeX * (4 + i), startY + sizeY * 4, startX + sizeX * (4 + i), startY + sizeY * 13));
		IntStream.range(0, 6).forEach(i -> g.drawLine(startX, startY + sizeY * (15 + i), startX + sizeX * 6, startY + sizeY * (15 + i)));
		IntStream.range(0, 5).forEach(i -> g.drawLine(startX + sizeX * (1 + i), startY + sizeY * 15, startX + sizeX * (1 + i), startY + sizeY * 21));
	}
}