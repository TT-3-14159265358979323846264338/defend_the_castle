package defendthecastle.composition;

import static custommatcher.CustomMatcher.*;
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;

import java.util.stream.Stream;

import javax.swing.JButton;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import defendthecastle.MainFrame;

class MenuCompositionTest {
	private MainFrame MainFrame;
	private MenuComposition MenuComposition;

	@BeforeEach
	void setUp() throws Exception {
		MainFrame = mock(MainFrame.class);
		MenuComposition = new MenuComposition(MainFrame);
	}

	/**
	 * ユニット画像が全て取り込まれていることを確認。<br>
	 * コアと左武器はnull禁止で全て取り込まれている必要がある。<br>
	 * 右武器はnullの可能性があるので、Listの要素数が左武器と一致することを確認。
	 */
	@Test
	void testVariables() {
		assertThat(MenuComposition.getRightWeaponList().size(), is(MenuComposition.getLeftWeaponList().size()));
		assertThat(MenuComposition.getCeterCoreList(), everyItem(notNullValue()));
		assertThat(MenuComposition.getLeftWeaponList(), everyItem(notNullValue()));
	}
	
	/**
	 * 
	 */
	@Test
	void testMenuComposition() {
		JButton[] allButton = buttonArray();
		assertThat(MenuComposition.getComponents(), hasAllItemInArray(allButton));
		Stream.of(allButton).forEach(this::assertActionListeners);
		
		
		
		
		
	}
	
	JButton[] buttonArray() {
		return new JButton[]{MenuComposition.getNewButton(),
				MenuComposition.getRemoveButton(),
				MenuComposition.getSwapButton(),
				MenuComposition.getNameChangeButton(),
				MenuComposition.getSaveButton(),
				MenuComposition.getLoadButton(),
				MenuComposition.getResetButton(),
				MenuComposition.getReturnButton(),
				MenuComposition.getSwitchButton(),
				MenuComposition.getSortButton()
		};
	}
	
	void assertActionListeners(JButton button) {
		assertThat(button.getActionListeners(), not(emptyArray()));
	}
}