package defendthecastle.screendisplay;

import javax.swing.JDialog;

//ソート画面表示用ダイアログ
class SortDialog extends JDialog{
	protected void setSortDialog(SortPanel SortPanel) {
		setModalityType(ModalityType.APPLICATION_MODAL);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setResizable(false);
		setTitle("ソート/絞り込み");
		setSize(835, 565);
		setLocationRelativeTo(null);
		add(SortPanel);
		setVisible(true);
	}
	
	protected void disposeDialog() {
		dispose();
	}
}