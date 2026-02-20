package testdataedit;

import static javax.swing.JOptionPane.*;

import java.awt.Font;
import java.awt.event.ActionEvent;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JScrollPane;

import commonclass.CommonJPanel;

//セーブデータ編集用メインパネル
class TestPanel extends CommonJPanel{
	private final TestDataEdit testDataEdit;
	private final EditItem editItem;
	private final EditProgress editProgress;
	private final JLabel typeLabel = new JLabel();
	private final JButton switchButton = new JButton();
	private final JButton saveButton = new JButton();
	private final JButton returnButton = new JButton();
	private final JScrollPane itemScroll = new JScrollPane();
	private final Font largeFont = new Font("ＭＳ ゴシック", Font.BOLD, 25);
	private final Font smallFont = new Font("ＭＳ ゴシック", Font.BOLD, 20);
	
	TestPanel(TestDataEdit testDataEdit) {
		this.testDataEdit = testDataEdit;
		TestEditImage testEditImage = createTestEditImage();
		editItem = createEditItem(testEditImage);
		editProgress = createEditProgress(testEditImage);
		setLabel(typeLabel, labelText(), 20, 10, 400, 30, largeFont);
		addSwitchButton();
		addSaveButton();
		addReturnButton();
		addScroll();
		stillness(brown());
	}
	
	TestEditImage createTestEditImage() {
		return new TestEditImage();
	}
	
	EditItem createEditItem(TestEditImage testEditImage) {
		return new EditItem(testEditImage);
	}
	
	EditProgress createEditProgress(TestEditImage testEditImage) {
		return new EditProgress(testEditImage);
	}
	
	String labelText() {
		return hasDisplayedItem()? "保有アイテム": "クリア状況";
	}
	
	boolean hasDisplayedItem() {
		return itemScroll.getViewport().getView() == editItem;
	}
	
	private void addSwitchButton() {
		setButton(switchButton, "表示切替", 145, 530, 150, 60, smallFont);
		switchButton.addActionListener(this::switchButtonAction);
	}
	
	void switchButtonAction(ActionEvent e) {
		itemScroll.getViewport().setView(hasDisplayedItem()? editProgress: editItem);
		typeLabel.setText(labelText());
	}
	
	private void addSaveButton() {
		setButton(saveButton, "セーブ", 315, 530, 150, 60, smallFont);
		saveButton.addActionListener(this::saveButtonAction);
	}
	
	void saveButtonAction(ActionEvent e) {
		if(hasDisplayedItem()) {
			editItem.save();
		}else {
			editProgress.save();
		}
		showMessageDialog(null, "セーブしました");
	}
	
	private void addReturnButton() {
		setButton(returnButton, "戻る", 485, 530, 150, 60, smallFont);
		returnButton.addActionListener(this::returnButtonAction);
	}
	
	void returnButtonAction(ActionEvent e) {
		testDataEdit.disposeDialog();
	}
	
	private void addScroll() {
		itemScroll.getViewport().setView(editItem);
		setScroll(itemScroll, 20, 50, 730, 470);
	}
}