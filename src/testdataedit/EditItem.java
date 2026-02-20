package testdataedit;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import javax.swing.JLabel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;

import commonclass.CommonJPanel;
import defaultdata.DefaultUnit;
import savedata.SaveHoldItem;

//保有アイテム編集
class EditItem extends CommonJPanel{
	private final TestEditImage testEditImage;
	private final SaveHoldItem saveHoldItem;
	private final JLabel[] coreLabel;
	private final JLabel[] weaponLabel;
	private final JSpinner[] coreSpinner;
	private final JSpinner[] weaponSpinner;
	private final Font gothicFont =  new Font("ＭＳ ゴシック", Font.BOLD, 15);
	private final Font arialFont = new Font("Arail", Font.BOLD, 15);
	private final int SIZE = 50;
	private final int LABEL_POSITION = 0;
	private final int SPINNER_POSITION = 360;
	
	EditItem(TestEditImage testEditImage) {
		this.testEditImage = testEditImage;
		saveHoldItem = createSaveHoldItem();
		saveHoldItem.load();
		List<String> coreName = IntStream.range(0, testEditImage.coreSize()).mapToObj(i -> DefaultUnit.CORE_DATA_MAP.get(i).getName()).toList();
		List<String> weaponName = IntStream.range(0, testEditImage.coreSize()).mapToObj(i -> DefaultUnit.WEAPON_DATA_MAP.get(i).getName()).toList();
		coreLabel = createLabel(testEditImage.coreSize(), LABEL_POSITION, coreName);
		weaponLabel = createLabel(testEditImage.weaponSize(), SPINNER_POSITION, weaponName);
		coreSpinner = createSpinner(coreLabel.length, LABEL_POSITION, saveHoldItem.getCoreNumberList());
		weaponSpinner = createSpinner(weaponLabel.length, SPINNER_POSITION, saveHoldItem.getWeaponNumberList());
		setPreferredSize(new Dimension(100, SIZE * maxSize()));
		stillness(defaultWhite());
	}
	
	SaveHoldItem createSaveHoldItem() {
		return new SaveHoldItem();
	}
	
	JLabel[] createLabel(int size, int position, List<String> nameList) {
		return IntStream.range(0, size).mapToObj(i -> initializeLabel(i, position, nameList.get(i))).toArray(JLabel[]::new);
	}
	
	JLabel initializeLabel(int number, int position, String name) {
		JLabel label = new JLabel();
		setLabel(label, name, SIZE + position, number * SIZE, 200, SIZE, gothicFont);
		return label;
	}
	
	JSpinner[] createSpinner(int size, int position, List<Integer> numberList) {
		return IntStream.range(0, size).mapToObj(i -> initializeSpinner(i, position, numberList.get(i))).toArray(JSpinner[]::new);
	}
	
	JSpinner initializeSpinner(int number, int position, int itemNumber) {
		JSpinner spinner = new JSpinner();
		spinner.setModel(new SpinnerNumberModel(itemNumber, 0, 100, 1));
		JSpinner.NumberEditor editor = new JSpinner.NumberEditor(spinner);
		JTextField field = editor.getTextField();
		field.setEditable(false);
		field.setFocusable(false);
		field.setHorizontalAlignment(JTextField.CENTER);
		spinner.setEditor(editor);
		spinner.setBounds(SIZE + position + 200, number * SIZE, 100, SIZE);
		spinner.setPreferredSize(spinner.getSize());
		spinner.setFont(arialFont);
		add(spinner);
		return spinner;
	}
	
	int maxSize() {
		return testEditImage.weaponSize() < testEditImage.coreSize()? testEditImage.coreSize(): testEditImage.weaponSize();
	}
	
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		BiConsumer<List<BufferedImage>, Integer> draw = (image, position) -> {
			IntStream.range(0, image.size()).forEach(i -> g.drawImage(image.get(i), position, i * SIZE, this));
		};
		draw.accept(testEditImage.getCoreImage(), 0);
		draw.accept(testEditImage.getWeaponImage(), 360);
	}
	
	void save() {
		saveHoldItem.setCoreNumberList(input(coreSpinner));
		saveHoldItem.setWeaponNumberList(input(weaponSpinner));
		saveHoldItem.save();
	}
	
	List<Integer> input(JSpinner[] spinner){
		return Stream.of(spinner).map(i ->  (int) i.getValue()).toList();
	}
}