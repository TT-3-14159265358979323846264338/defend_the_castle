package testdataedit;

import static javax.swing.JOptionPane.*;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

//セーブデータ編集用メインパネル
class TestPanel extends JPanel{
	private JLabel typeLabel = new JLabel();
	private JButton switchButton = new JButton();
	private JButton saveButton = new JButton();
	private JButton returnButton = new JButton();
	private JScrollPane itemScroll = new JScrollPane();
	private EditItem EditItem = new EditItem();
	private EditProgress EditProgress = new EditProgress();
	private TestDataEdit TestDataEdit;
	
	protected TestPanel(TestDataEdit TestDataEdit) {
		this.TestDataEdit = TestDataEdit;
		setBackground(new Color(240, 170, 80));
		add(typeLabel);
		addSwitchButton();
		addSaveButton();
		addReturnButton();
		addScroll();
	}
	
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		setTypeLabel();
		setSwitchButton();
		setSaveButton();
		setReturnButton();
		setScroll();
		requestFocus();
	}
	
	private void setTypeLabel() {
		typeLabel.setText((itemScroll.getViewport().getView() == EditItem)? "保有アイテム": "クリア状況");
		typeLabel.setBounds(20, 10, 400, 30);
		typeLabel.setFont(new Font("ＭＳ ゴシック", Font.BOLD, 25));
	}
	
	private void addSwitchButton() {
		add(switchButton);
		switchButton.addActionListener(e->{
			itemScroll.getViewport().setView((itemScroll.getViewport().getView() == EditItem)? EditProgress: EditItem);
		});
	}
	
	private void setSwitchButton() {
		switchButton.setText("表示切替");
		switchButton.setBounds(145, 530, 150, 60);
		setButton(switchButton);
	}
	
	private void addSaveButton() {
		add(saveButton);
		saveButton.addActionListener(e->{
			if(itemScroll.getViewport().getView() == EditItem) {
				EditItem.save();
			}else {
				EditProgress.save();
			}
			showMessageDialog(null, "セーブしました");
		});
	}
	
	private void setSaveButton() {
		saveButton.setText("セーブ");
		saveButton.setBounds(315, 530, 150, 60);
		setButton(saveButton);
	}
	
	private void addReturnButton() {
		add(returnButton);
		returnButton.addActionListener(e->{
			TestDataEdit.disposeDialog();
		});
	}
	
	private void setReturnButton() {
		returnButton.setText("戻る");
		returnButton.setBounds(485, 530, 150, 60);
		setButton(returnButton);
	}
	
	private void setButton(JButton button) {
		button.setFont(new Font("ＭＳ ゴシック", Font.BOLD, 20));
	}
	
	private void addScroll() {
		itemScroll.getViewport().setView(EditItem);
  	add(itemScroll);
	}
	
	private void setScroll() {
		itemScroll.setBounds(20, 50, 730, 470);
		itemScroll.setPreferredSize(itemScroll.getSize());
	}
}