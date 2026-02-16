package defendthecastle.itemdispose;

import javax.swing.JDialog;

//リサイクル画面表示用ダイアログ
class RecycleDialog extends JDialog{
	void setDialog(RecyclePanel RecyclePanel) {
		setModalityType(ModalityType.APPLICATION_MODAL);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setResizable(false);
		setTitle("ステータス");
		setSize(600, 195);
		setLocationRelativeTo(null);
		add(RecyclePanel);
		setVisible(true);
	}
	
	void disposeDialog() {
		dispose();
	}
}