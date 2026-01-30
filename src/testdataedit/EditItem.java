package testdataedit;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;

import defaultdata.DefaultUnit;
import savedata.SaveHoldItem;

//保有アイテム編集
class EditItem extends JPanel{
	private JLabel[] coreLabel;
	private JLabel[] weaponLabel;
	private JSpinner[] coreSpinner;
	private JSpinner[] weaponSpinner;
	private List<BufferedImage> coreImage = IntStream.range(0, DefaultUnit.CORE_DATA_MAP.size()).mapToObj(i -> DefaultUnit.CORE_DATA_MAP.get(i).getImage(4)).toList();
	private List<BufferedImage> weaponImage = IntStream.range(0, DefaultUnit.WEAPON_DATA_MAP.size()).mapToObj(i -> DefaultUnit.WEAPON_DATA_MAP.get(i).getImage(4)).toList();
	private SaveHoldItem SaveHoldItem = new SaveHoldItem();
	private int size = 50;
	
	protected EditItem() {
		SaveHoldItem.load();
		addLabel();
		addSpinner();
		setPreferredSize(new Dimension(100, size * (weaponImage.size() < coreImage.size()? coreImage.size(): weaponImage.size())));
	}
	
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		drawImage(g);
		setLabel();
		setSpinner();
	}
	
	protected void save() {
		Function<JSpinner[], List<Integer>> input = (spinner) -> {
			return Stream.of(spinner).map(i ->  (int) i.getValue()).collect(Collectors.toList());
		};
		SaveHoldItem.setCoreNumberList(input.apply(coreSpinner));
		SaveHoldItem.setWeaponNumberList(input.apply(weaponSpinner));
		SaveHoldItem.save();
	}
	
	private void addLabel() {
		Function<Integer, JLabel[]> initialize = count -> {
			return IntStream.range(0, count).mapToObj(i -> new JLabel()).toArray(JLabel[]::new);
		};
		BiConsumer<JLabel[], List<String>> set = (label, name) -> {
			IntStream.range(0, label.length).forEach(i -> {
				add(label[i]);
				label[i].setFont(new Font("ＭＳ ゴシック", Font.BOLD, 15));
				label[i].setText(name.get(i));
			});
		};
		coreLabel = initialize.apply(coreImage.size());
		weaponLabel = initialize.apply(weaponImage.size());
		set.accept(coreLabel, IntStream.range(0, coreLabel.length).mapToObj(i -> DefaultUnit.CORE_DATA_MAP.get(i).getName()).toList());
		set.accept(weaponLabel, IntStream.range(0, weaponLabel.length).mapToObj(i -> DefaultUnit.WEAPON_DATA_MAP.get(i).getName()).toList());
	}
	
	private void setLabel() {
		BiConsumer<JLabel[], Integer> draw = (label, position) -> {
			IntStream.range(0, label.length).forEach(i -> label[i].setBounds(size + position, i * size, 200, size));
		};
		draw.accept(coreLabel, 0);
		draw.accept(weaponLabel, 360);
	}
	
	private void addSpinner() {
		Function<Integer, JSpinner[]> initialize = count -> {
			return IntStream.range(0, count).mapToObj(i -> new JSpinner()).toArray(JSpinner[]::new);
		};
		BiConsumer<JSpinner[], List<Integer>> set = (spinner, number) -> {
			IntStream.range(0, spinner.length).forEach(i -> {
				add(spinner[i]);
				spinner[i].setModel(new SpinnerNumberModel((int) number.get(i), 0, 100, 1));
				JSpinner.NumberEditor editor = new JSpinner.NumberEditor(spinner[i]);
				editor.getTextField().setEditable(false);
				editor.getTextField().setHorizontalAlignment(JTextField.CENTER);
				spinner[i].setEditor(editor);
			});
		};
		coreSpinner = initialize.apply(coreImage.size());
		weaponSpinner = initialize.apply(weaponImage.size());
		set.accept(coreSpinner, SaveHoldItem.getCoreNumberList());
		set.accept(weaponSpinner, SaveHoldItem.getWeaponNumberList());
	}
	
	private void setSpinner() {
		BiConsumer<JSpinner[], Integer> draw = (spinner, position) -> {
			IntStream.range(0, spinner.length).forEach(i -> {
				spinner[i].setBounds(size + position + 200, i * size, 100, size);
				spinner[i].setPreferredSize(spinner[i].getSize());
				spinner[i].setFont(new Font("Arail", Font.BOLD, 15));
			});
		};
		draw.accept(coreSpinner, 0);
		draw.accept(weaponSpinner, 360);
	}
	
	private void drawImage(Graphics g) {
		BiConsumer<List<BufferedImage>, Integer> draw = (image, position) -> {
			IntStream.range(0, image.size()).forEach(i -> g.drawImage(image.get(i), position, i * size, this));
		};
		draw.accept(coreImage, 0);
		draw.accept(weaponImage, 360);
	}
}