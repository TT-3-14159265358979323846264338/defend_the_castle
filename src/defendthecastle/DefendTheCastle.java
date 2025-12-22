package defendthecastle;

import savedata.FileCheck;

public class DefendTheCastle {
	public static void main(String[] args) {
		new DefendTheCastle();
	}
	
	DefendTheCastle(){
		new FileCheck();
		new MainFrame().mainMenuDraw();
	}
}