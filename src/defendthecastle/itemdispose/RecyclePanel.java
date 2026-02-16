package defendthecastle.itemdispose;

import static javax.swing.JOptionPane.*;

import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;

//リサイクル数確定画面
class RecyclePanel extends JPanel{
	private final JLabel commentLabel = new JLabel();
	private final JLabel resultLabel = new JLabel();
	private final JSpinner countSpinner = new JSpinner();
	private final JButton recycleButton = new JButton();
	private final JButton returnButton = new JButton();
	private final RecycleDialog RecycleDialog;
	private final BufferedImage image;
	private final int rarity;
	private int quantity;
	private boolean canDispose;
	private final Font gothicFont = new Font("ＭＳ ゴシック", Font.BOLD, 15);
	private final Font arailFont = new Font("Arail", Font.BOLD, 15);
	
	RecyclePanel(BufferedImage image, int max, int rarity) {
		this.image = image;
		this.rarity = rarity;
		setLabel(commentLabel, "ガチャメダルへリサイクルする数量を入力してください", 120, 10, 400, 30);
		setLabel(resultLabel, "ガチャメダル: " + getMedal() +  "枚", 370, 50, 400, 30);
		addRecycleButton();
		addSpinner(max);
		addReturnButton();
		RecycleDialog = createRecycleDialog();
		RecycleDialog.setDialog(this);
	}
	
	RecycleDialog createRecycleDialog() {
		return new RecycleDialog();
	}
	
	private void setLabel(JLabel label, String name, int x, int y, int width, int height) {
		label.setText(name);
		label.setBounds(x, y, width, height);
		label.setFont(gothicFont);
		add(label);
	}
	
	private void addSpinner(int max) {
		importQuantity(null);
		countSpinner.setModel(new SpinnerNumberModel(1, 1, max, 1));
		countSpinner.addChangeListener(this::importQuantity);
		JSpinner.NumberEditor editor = new JSpinner.NumberEditor(countSpinner);
		editor.getTextField().setEditable(false);
		editor.getTextField().setHorizontalAlignment(JTextField.CENTER);
		countSpinner.setEditor(editor);
		setSpinner(countSpinner, 250, 50, 100, 30);
	}
	
	void importQuantity(ChangeEvent e) {
		quantity = (int) countSpinner.getValue();
	}
	
	private void setSpinner(JSpinner spinner, int x, int y, int width, int height) {
		spinner.setBounds(x, y, width, height);
		spinner.setPreferredSize(spinner.getSize());
		spinner.setFont(arailFont);
		add(spinner);
	}
	
	private void addRecycleButton() {
		setButton(recycleButton, "リサイクル", 170, 100, 120, 40);
		recycleButton.addActionListener(this::recycleButtonAction);
	}
	
	void recycleButtonAction(ActionEvent e) {
		if(YES_OPTION == showConfirmDialog(null, quantity + "個をリサイクルしますか","リサイクル確認",YES_NO_OPTION , QUESTION_MESSAGE)) {
			canDispose = true;
			RecycleDialog.disposeDialog();
		}
	}
	
	private void addReturnButton() {
		setButton(returnButton, "戻る", 310, 100, 120, 40);
		returnButton.addActionListener(this::returnButtonAction);
	}
	
	void returnButtonAction(ActionEvent e) {
		RecycleDialog.disposeDialog();
	}
	
	private void setButton(JButton button, String name, int x, int y, int width, int height) {
		button.setText(name);
		button.setBounds(x, y, width, height);
		button.setFont(gothicFont);
		add(button);
	}
	
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		g.drawImage(image, 10, 10, null);
	}
	
	boolean canDispose() {
		return canDispose;
	}
	
	int getQuantity() {
		return quantity;
	}
	
	int getMedal() {
		return quantity * rarity * rarity * 3;
	}
}