package defendthecastle;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

import javax.swing.JFrame;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class MainFrameTest {
	private MainFrame MainFrame;
	
	@BeforeEach
	void setUp(){
		MainFrame = new MainFrame();
	}

	/**
	 * setDefaultCloseOperationとsetResizableが設定されているか確認。
	 */
	@Test
	void testMainFrame() {
		assertThat(MainFrame.getDefaultCloseOperation(), is(JFrame.EXIT_ON_CLOSE));
		assertThat(MainFrame.isResizable(), is(false));
	}
	
	/**
	 * 
	 */
	@Test
	void testMainMenuDraw() {
		
	}
}