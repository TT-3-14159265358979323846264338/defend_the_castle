package defendthecastle;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.Test;
import org.mockito.MockedConstruction;

import savedata.FileCheck;

class DefendTheCastleTest {

	/**
	 * ファイルチェックとメイン画面が1回ずつ呼び出されることを確認。<br>
	 * メイン画面では初期画面表示されることも確認。
	 */
	@Test
	void testDefendTheCastle() {
		try(MockedConstruction<FileCheck> mockFileCheck = mockConstruction(FileCheck.class);
				MockedConstruction<MainFrame> mockMainFrame = mockConstruction(MainFrame.class)){
			new DefendTheCastle();
			assertThat(mockFileCheck.constructed(), hasSize(1));
			assertThat(mockMainFrame.constructed(), hasSize(1));
			verify(mockMainFrame.constructed().get(0), times(1)).mainMenuDraw();
		}
	}
}
