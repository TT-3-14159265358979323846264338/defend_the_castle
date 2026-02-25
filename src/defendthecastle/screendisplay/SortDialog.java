package defendthecastle.screendisplay;

import javax.swing.JDialog;

//ソート画面表示用ダイアログ
class SortDialog extends JDialog{
	void setSortDialog(SortPanel sortPanel) {
		setTitle("ソート/絞り込み");
		setSize(835, 565);
		setModalityType(ModalityType.APPLICATION_MODAL);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setResizable(false);
		setLocationRelativeTo(null);
		add(sortPanel);
		setVisible(true);
	}
	
	void disposeDialog() {
		dispose();
	}
}