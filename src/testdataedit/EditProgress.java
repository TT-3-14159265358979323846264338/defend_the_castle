package testdataedit;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import javax.swing.JLabel;
import javax.swing.JRadioButton;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;

import commonclass.CommonJPanel;
import defaultdata.Stage;
import savedata.SaveGameProgress;
import savedata.SaveItem;

//クリア状況編集
class EditProgress extends CommonJPanel{
	private final TestEditImage testEditImage;
	private final SaveGameProgress saveGameProgress;
	private final SaveItem saveItem;
	private final JLabel medalLabel = new JLabel();
	//JUnitでゲッターを追加する可能性あり
	@SuppressWarnings("unused")
	private final JLabel[] nameLabel;
	private final JSpinner medalSpinner = new JSpinner();
	private final JRadioButton[] stageRadio;
	private final List<JRadioButton[]> merit;
	private final Font largeGothicFont = new Font("ＭＳ ゴシック", Font.BOLD, 15);
	private final Font smallGothicFont = new Font("ＭＳ ゴシック", Font.BOLD, 10);
	private final Font arailFont = new Font("Arail", Font.BOLD, 15);
	private final int SIZE_X = 110;
	private final int SIZE_Y = 70;
	
	EditProgress(TestEditImage testEditImage) {
		this.testEditImage = testEditImage;
		saveGameProgress = createSaveGameProgress();
		saveItem = createSaveItem();
		saveGameProgress.load();
		saveItem.load();
		setLabel(medalLabel, "保有メダル", SIZE_X, 0, SIZE_X, SIZE_Y, largeGothicFont);
		nameLabel = createNameLabel();
		setSpinner();
		stageRadio = createRadioArray(testEditImage.stageSize(), saveGameProgress.getStageStatus());
		merit = createRadioList();
		setRadio();
		setPreferredSize(new Dimension(500, SIZE_Y * testEditImage.stageSize()));
		stillness(defaultWhite());
	}
	
	SaveGameProgress createSaveGameProgress() {
		return new SaveGameProgress();
	}
	
	SaveItem createSaveItem() {
		return new SaveItem();
	}
	
	JLabel[] createNameLabel() {
		return Stream.of(Stage.values()).map(i -> initializeLabel(i)).toArray(JLabel[]::new);
	}
	
	JLabel initializeLabel(Stage stage) {
		JLabel label = new JLabel();
		setLabel(label, stage.getLabel().getName(), SIZE_X, (stage.getId() + 1) * SIZE_Y, SIZE_X, SIZE_Y, largeGothicFont);
		return label;
	}
	
	private void setSpinner() {
		medalSpinner.setModel(new SpinnerNumberModel(saveItem.getMedalNumber(), 0, 100000, 100));
		JSpinner.NumberEditor editor = new JSpinner.NumberEditor(medalSpinner);
		JTextField field = editor.getTextField();
		field.setEditable(false);
		field.setFocusable(false);
		field.setHorizontalAlignment(JTextField.CENTER);
		medalSpinner.setEditor(editor);
		medalSpinner.setBounds(SIZE_X * 2, 0, SIZE_X, SIZE_Y);
		medalSpinner.setPreferredSize(medalSpinner.getSize());
		medalSpinner.setFont(arailFont);
		add(medalSpinner);
	}
	
	JRadioButton[] createRadioArray(int size, List<Boolean> clearList){
		return IntStream.range(0, size).mapToObj(i -> initializeRadio(i, clearList.get(i))).toArray(JRadioButton[]::new);
	}
	
	JRadioButton initializeRadio(int number, boolean hasCleared) {
		JRadioButton radio = new JRadioButton();
		radio.setFont(smallGothicFont);
		radio.setOpaque(false);
		if(hasCleared) {
			radio.setSelected(true);
		}
		add(radio);
		return radio;
	}
	
	List<JRadioButton[]> createRadioList(){
		return Stream.of(Stage.values()).map(i -> createRadioArray(i.getLabel().getMerit().size(), saveGameProgress.getMeritData(i.getId()).getMeritClearList())).toList();
	}
	
	private void setRadio(){
		IntStream.range(0, stageRadio.length).forEach(i -> {
				stageRadio[i].setText("ステージクリア");
				stageRadio[i].setBounds(SIZE_X + 100, (i + 1) * SIZE_Y, SIZE_X, SIZE_Y);
			});
		IntStream.range(0, merit.size()).forEach(i -> IntStream.range(0, merit.get(i).length).forEach(j -> {
				merit.get(i)[j].setText("戦功" + (j + 1) + "クリア");
				merit.get(i)[j].setBounds(100 + SIZE_X * (2 + j / 2), (i + 1) * SIZE_Y + j % 2 * SIZE_Y / 2, SIZE_X, SIZE_Y / 2);
			}));
	}
	
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		IntStream.range(0, testEditImage.stageSize()).forEach(i -> g.drawImage(testEditImage.getStageImage().get(i), 0, (i + 1) * SIZE_Y, this));
	}
	
	void save() {
		IntStream.range(0, stageRadio.length).forEach(i -> {
			saveGameProgress.setStage(i, stageRadio[i].isSelected());
			IntStream.range(0, merit.get(i).length).forEach(j -> {
				saveGameProgress.getMeritData(i).setMeritClear(j, merit.get(i)[j].isSelected());
			});
		});
		saveItem.setMedalNumber((int) medalSpinner.getValue());
		saveGameProgress.save();
		saveItem.save();
	}
}