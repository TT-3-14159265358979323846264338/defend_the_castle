package defendthecastle.itemget;

import javax.swing.JDialog;

//ガチャ結果画面
class GachaResult extends JDialog{
	GachaResult(GachaInformation gachaInformation, HoldMedal holdMedal, ItemGetImage itemGetImage) {
		setModalityType(ModalityType.APPLICATION_MODAL);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setResizable(false);
		setTitle("ガチャ結果");
		setSize(970, 300);
		setLocationRelativeTo(null);
		add(createDrawResult(gachaInformation, holdMedal, itemGetImage));
		setVisible(true);
	}
	
	DrawResult createDrawResult(GachaInformation gachaInformation, HoldMedal holdMedal, ItemGetImage itemGetImage) {
		return new DrawResult(gachaInformation, holdMedal, itemGetImage);
	}
}