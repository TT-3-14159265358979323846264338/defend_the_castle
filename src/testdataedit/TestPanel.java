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
		setButton(switchButton, "表示切替", 145, 530, 150, 60, smallFont, this::switchButtonAction);
		setButton(saveButton, "セーブ", 315, 530, 150, 60, smallFont, this::saveButtonAction);
		setButton(returnButton, "戻る", 485, 530, 150, 60, smallFont, this::returnButtonAction);
		setScroll(itemScroll, 20, 50, 730, 470, editItem);
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
	
	void switchButtonAction(ActionEvent e) {
		itemScroll.getViewport().setView(hasDisplayedItem()? editProgress: editItem);
		typeLabel.setText(labelText());
	}
	
	void saveButtonAction(ActionEvent e) {
		if(hasDisplayedItem()) {
			editItem.save();
		}else {
			editProgress.save();
		}
		showMessageDialog(null, "セーブしました");
	}
	
	void returnButtonAction(ActionEvent e) {
		testDataEdit.disposeDialog();
	}
}