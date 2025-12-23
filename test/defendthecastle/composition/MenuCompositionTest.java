package defendthecastle.composition;

import static custommatcher.CustomMatcher.*;
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.stream.Stream;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JScrollPane;

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
	 * コアと左武器はnull禁止で全て取り込まれている必要がある。
	 * 右武器はnullの可能性があるので、Listの要素数が左武器と一致することを確認。
	 */
	@Test
	void testVariables() {
		assertThat(MenuComposition.getRightWeaponList().size(), is(MenuComposition.getLeftWeaponList().size()));
		assertThat(MenuComposition.getCeterCoreList(), everyItem(notNullValue()));
		assertThat(MenuComposition.getLeftWeaponList(), everyItem(notNullValue()));
	}
	
	/**
	 * MouseListenerが設定されていることを確認。<br>
	 * 全てのComponentが追加されているか確認。<br>
	 * ButtonにActionListenerが、ScrollにViewが設定されていることを確認。
	 */
	@Test
	void testMenuComposition() {
		JLabel[] allLabel = labelArray();
		JButton[] allButton = buttonArray();
		JScrollPane[] allScroll = scrollArray();
		assertThat(MenuComposition.getMouseListeners(), notNullValue());
		assertThat(MenuComposition.getComponents(), allOf(hasAllItemInArray(allLabel),
															hasAllItemInArray(allButton),
															hasAllItemInArray(allScroll)));
		Stream.of(allButton).forEach(this::assertActionListeners);
		Stream.of(allScroll).forEach(this::assertView);
	}
	
	JLabel[] labelArray() {
		return new JLabel[] {MenuComposition.getCompositionNameLabel(),
				MenuComposition.getCompositionLabel(),
				MenuComposition.getTypeLabel()};
	}
	
	JButton[] buttonArray() {
		return new JButton[] {MenuComposition.getNewButton(),
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
	
	JScrollPane[] scrollArray() {
		return new JScrollPane[] {MenuComposition.getCompositionScroll(),
				MenuComposition.getItemScroll()};
	}
	
	void assertActionListeners(JButton button) {
		assertThat(button.getActionListeners(), not(emptyArray()));
	}
	
	void assertView(JScrollPane scroll) {
		assertThat(scroll.getViewport().getView(), notNullValue());
	}
	
	/**
	 * 
	 */
	@Test
	void testPaintComponent() {
		JLabel[] allLabel = labelArray();
		
		
		
		MenuComposition.paintComponent(brankGraphics());
		
		
		
		
		
		
	}
	
	Graphics brankGraphics() {
		return new BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB).createGraphics();
	}
}