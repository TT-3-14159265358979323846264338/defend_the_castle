package defendthecastle.itemdispose;

import static javax.swing.JOptionPane.*;

import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;

import defendthecastle.commoninheritance.CommonJPanel;

//リサイクル数確定画面
class RecyclePanel extends CommonJPanel{
	private final JLabel commentLabel = new JLabel();
	private final JLabel resultLabel = new JLabel();
	private final JSpinner countSpinner = new JSpinner();
	private final JButton recycleButton = new JButton();
	private final JButton returnButton = new JButton();
	private final RecycleDialog recycleDialog;
	private final BufferedImage image;
	private final int rarity;
	private int quantity;
	private boolean canDispose;
	private final Font gothicFont = new Font("ＭＳ ゴシック", Font.BOLD, 15);
	private final Font arailFont = new Font("Arail", Font.BOLD, 15);
	
	RecyclePanel(BufferedImage image, int max, int rarity) {
		this.image = image;
		this.rarity = rarity;
		setLabel(commentLabel, "ガチャメダルへリサイクルする数量を入力してください", 120, 10, 400, 30, gothicFont);
		setLabel(resultLabel, medalComment(), 370, 50, 400, 30, gothicFont);
		addRecycleButton();
		addSpinner(max);
		addReturnButton();
		stillness(defaultWhite());
		recycleDialog = createRecycleDialog();
		recycleDialog.setDialog(this);
	}
	
	String medalComment() {
		return String.format("ガチャメダル: %d枚", getMedal());
	}
	
	RecycleDialog createRecycleDialog() {
		return new RecycleDialog();
	}
	
	private void addSpinner(int max) {
		countSpinner.setModel(new SpinnerNumberModel(1, 1, max, 1));
		countSpinner.addChangeListener(this::importQuantity);
		importQuantity(null);
		JSpinner.NumberEditor editor = new JSpinner.NumberEditor(countSpinner);
		JTextField field = editor.getTextField();
		field.setEditable(false);
		field.setFocusable(false);
		field.setHorizontalAlignment(JTextField.CENTER);
		countSpinner.setEditor(editor);
		setSpinner(countSpinner, 250, 50, 100, 30);
	}
	
	void importQuantity(ChangeEvent e) {
		quantity = (int) countSpinner.getValue();
		resultLabel.setText(medalComment());
	}
	
	private void setSpinner(JSpinner spinner, int x, int y, int width, int height) {
		spinner.setBounds(x, y, width, height);
		spinner.setPreferredSize(spinner.getSize());
		spinner.setFont(arailFont);
		add(spinner);
	}
	
	private void addRecycleButton() {
		setButton(recycleButton, "リサイクル", 170, 100, 120, 40, gothicFont);
		recycleButton.addActionListener(this::recycleButtonAction);
	}
	
	void recycleButtonAction(ActionEvent e) {
		if(YES_OPTION == showConfirmDialog(null, quantity + "個をリサイクルしますか","リサイクル確認",YES_NO_OPTION , QUESTION_MESSAGE)) {
			canDispose = true;
			recycleDialog.disposeDialog();
		}
	}
	
	private void addReturnButton() {
		setButton(returnButton, "戻る", 310, 100, 120, 40, gothicFont);
		returnButton.addActionListener(this::returnButtonAction);
	}
	
	void returnButtonAction(ActionEvent e) {
		recycleDialog.disposeDialog();
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