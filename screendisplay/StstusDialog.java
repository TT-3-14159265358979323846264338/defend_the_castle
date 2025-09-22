package screendisplay;

import javax.swing.JDialog;

//ステータス表示用ダイアログ
class StstusDialog extends JDialog{
	protected StstusDialog(StatusPanel StatusPanel) {
		setModalityType(ModalityType.APPLICATION_MODAL);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setResizable(false);
		setTitle("ステータス");
		setSize(720, 700);
		setLocationRelativeTo(null);
		add(StatusPanel);
		setVisible(true);
	}
}