package defendthecastle;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;

import java.util.stream.Stream;

import javax.swing.JButton;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class MenuMainTest {
	private MenuMain MenuMain;

	@BeforeEach
	void setUp() throws Exception {
		
	}

	/**
	 * 
	 */
	@Test
	void testMenuMain() {
		MainFrame MainFrame = mock(MainFrame.class);
		MenuMain = new MenuMain(MainFrame);
		JButton[] button = {MenuMain.getItemGetButton(),
				MenuMain.getItemDisposeButton(),
				MenuMain.getCompositionButton(),
				MenuMain.getSelectStageButton(),
				MenuMain.getTestButton()
		};
		assertThat(MenuMain.getMainFrame(), is(MainFrame));
		assertThat(MenuMain.getComponents(), arrayContainingInAnyOrder(button));
		Stream.of(button).forEach(this::assertActionListeners);
	}
	
	void assertActionListeners(JButton button) {
		assertThat(button.getActionListeners(), not(emptyArray()));
	}
}