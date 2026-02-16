package defendthecastle.itemget;

import java.util.concurrent.ScheduledExecutorService;

import javax.swing.JDialog;

//ガチャ結果画面
class GachaResult extends JDialog{
	GachaResult(ScheduledExecutorService scheduler, GachaInformation gachaInformation, HoldMedal holdMedal, ItemGetImage itemGetImage) {
		setModalityType(ModalityType.APPLICATION_MODAL);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setResizable(false);
		setTitle("ガチャ結果");
		setSize(970, 300);
		setLocationRelativeTo(null);
		add(createDrawResult(scheduler, gachaInformation, holdMedal, itemGetImage));
		setVisible(true);
	}
	
	DrawResult createDrawResult(ScheduledExecutorService scheduler, GachaInformation gachaInformation, HoldMedal holdMedal, ItemGetImage itemGetImage) {
		return new DrawResult(scheduler, gachaInformation, holdMedal, itemGetImage);
	}
}