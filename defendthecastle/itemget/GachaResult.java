package defendthecastle.itemget;

import javax.swing.JDialog;

//ガチャ結果画面
class GachaResult extends JDialog{
	protected GachaResult(GachaInformation GachaInformation, HoldMedal HoldMedal) {
		setModalityType(ModalityType.APPLICATION_MODAL);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setResizable(false);
		setTitle("ガチャ結果");
		setSize(970, 300);
		setLocationRelativeTo(null);
		add(new DrawResult(GachaInformation, HoldMedal));
		setVisible(true);
	}
}