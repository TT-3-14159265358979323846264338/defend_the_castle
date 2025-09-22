package defendthecastle.itemdispose;

import static javax.swing.JOptionPane.*;

import java.awt.Font;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;

//リサイクル数確定画面
class RecyclePanel extends JPanel{
	private JLabel commentLabel = new JLabel();
	private JLabel resultLabel = new JLabel();
	private JSpinner countSpinner = new JSpinner();
	private JButton recycleButton = new JButton();
	private JButton returnButton = new JButton();
	private RecycleDialog RecycleDialog = new RecycleDialog();
	private BufferedImage image;
	private int rarity;
	private int quantity;
	private boolean canDispose;
	
	protected RecyclePanel(BufferedImage image, int max, int rarity) {
		this.image = image;
		this.rarity = rarity;
		add(commentLabel);
		add(resultLabel);
		addSpinner(max);
		addRecycleButton();
		addReturnButton();
		RecycleDialog.setDialog(this);
	}
	
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		g.drawImage(image, 10, 10, null);
		setLabel(commentLabel, "ガチャメダルへリサイクルする数量を入力してください", 120, 10, 400, 30);
		setLabel(resultLabel, "ガチャメダル: " + getMedal() +  "枚", 370, 50, 400, 30);
		setSpinner(countSpinner, 250, 50, 100, 30);
		setButton(recycleButton, "リサイクル", 170, 100, 120, 40);
		setButton(returnButton, "戻る", 310, 100, 120, 40);
		requestFocus();
	}
	
	private void addSpinner(int max) {
		add(countSpinner);
		countSpinner.addChangeListener(e->{
			importQuantity();
		});
		countSpinner.setModel(new SpinnerNumberModel(1, 1, max, 1));
		importQuantity();
		JSpinner.NumberEditor editor = new JSpinner.NumberEditor(countSpinner);
		editor.getTextField().setEditable(false);
		editor.getTextField().setHorizontalAlignment(JTextField.CENTER);
		countSpinner.setEditor(editor);
	}
	
	private void addRecycleButton() {
		add(recycleButton);
		recycleButton.addActionListener(e->{
			if(YES_OPTION == showConfirmDialog(null, quantity + "個をリサイクルしますか","リサイクル確認",YES_NO_OPTION , QUESTION_MESSAGE)) {
				canDispose = true;
				RecycleDialog.disposeDialog();
			}
		});
	}
	
	private void addReturnButton() {
		add(returnButton);
		returnButton.addActionListener(e->{
			RecycleDialog.disposeDialog();
		});
	}
	
	private void setLabel(JLabel label, String name, int x, int y, int width, int height) {
		label.setText(name);
		label.setBounds(x, y, width, height);
		label.setFont(new Font("ＭＳ ゴシック", Font.BOLD, 15));
	}
	
	private void setSpinner(JSpinner spinner, int x, int y, int width, int height) {
		spinner.setBounds(x, y, width, height);
		spinner.setPreferredSize(spinner.getSize());
		spinner.setFont(new Font("Arail", Font.BOLD, 15));
	}
	
	private void setButton(JButton button, String name, int x, int y, int width, int height) {
		button.setText(name);
		button.setBounds(x, y, width, height);
		button.setFont(new Font("ＭＳ ゴシック", Font.BOLD, 15));
	}
	
	private void importQuantity() {
		quantity = (int) countSpinner.getValue();
	}
	
	protected boolean canDispose() {
		return canDispose;
	}
	
	protected int getQuantity() {
		return quantity;
	}
	
	protected int getMedal() {
		return quantity * rarity * rarity * 3;
	}
}