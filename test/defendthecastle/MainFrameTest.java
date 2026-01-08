package defendthecastle;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;

import java.awt.Component;
import java.awt.Container;

import javax.swing.JFrame;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import battle.Battle;
import defaultdata.stage.No0001Stage1;
import defendthecastle.composition.MenuComposition;
import defendthecastle.itemdispose.MenuItemDispose;
import defendthecastle.itemget.MenuItemGet;
import defendthecastle.selectstage.MenuSelectStage;

@ExtendWith(MockitoExtension.class)
class MainFrameTest {
	@Spy
	private MainFrame MainFrame;
	
	@Mock
	private Container mockContentPane;

	@Mock
	private Component mockComponent;

	/**
	 * setDefaultCloseOperationとsetResizableが設定されているか確認。
	 */
	@Test
	void testMainFrame() {
		assertThat(MainFrame.getDefaultCloseOperation(), is(JFrame.EXIT_ON_CLOSE));
		assertThat(MainFrame.isResizable(), is(false));
	}
	
	/**
	 * Componentが取り除かれた後に新たなComponentが追加されているか確認。
	 */
	@Test
	void testMainMenuDraw() {
		InOrder InOrder = createInOrder();
		doNothing().when(MainFrame).setVisible(anyBoolean());
		MainFrame.mainMenuDraw();
		InOrder.verify(mockContentPane).removeAll();
		InOrder.verify(MainFrame).add(Mockito.any(MenuMain.class));
	}
	
	/**
	 * Componentが取り除かれた後に新たなComponentが追加されているか確認。
	 */
	@Test
	void testItemGetMenuDraw() {
		InOrder InOrder = createInOrder();
		MainFrame.itemGetMenuDraw();
		InOrder.verify(mockContentPane).removeAll();
		InOrder.verify(MainFrame).add(Mockito.any(MenuItemGet.class));
	}
	
	/**
	 * Componentが取り除かれた後に新たなComponentが追加されているか確認。
	 */
	@Test
	void testItemDisposeMenuDraw() {
		InOrder InOrder = createInOrder();
		MainFrame.itemDisposeMenuDraw();
		InOrder.verify(mockContentPane).removeAll();
		InOrder.verify(MainFrame).add(Mockito.any(MenuItemDispose.class));
	}
	
	/**
	 * Componentが取り除かれた後に新たなComponentが追加されているか確認。
	 */
	@Test
	void testCompositionDraw() {
		InOrder InOrder = createInOrder();
		MainFrame.compositionDraw();
		InOrder.verify(mockContentPane).removeAll();
		InOrder.verify(MainFrame).add(Mockito.any(MenuComposition.class));
	}
	
	/**
	 * Componentが取り除かれた後に新たなComponentが追加されているか確認。
	 */
	@Test
	void testSelectStageDraw() {
		InOrder InOrder = createInOrder();
		MainFrame.selectStageDraw();
		InOrder.verify(mockContentPane).removeAll();
		InOrder.verify(MainFrame).add(Mockito.any(MenuSelectStage.class));
	}
	
	/**
	 * Componentが取り除かれた後に新たなComponentが追加されているか確認。
	 */
	@Test
	void testBattleDraw() {
		InOrder InOrder = createInOrder();
		MainFrame.battleDraw(new No0001Stage1(), 0);
		InOrder.verify(mockContentPane).removeAll();
		InOrder.verify(MainFrame).add(Mockito.any(Battle.class));
	}
	
	InOrder createInOrder() {
		InOrder InOrder = inOrder(mockContentPane, MainFrame);
		doReturn(mockContentPane).when(MainFrame).getContentPane();
		doReturn(mockComponent).when(MainFrame).add(Mockito.any(Component.class));
		doNothing().when(MainFrame).setTitle(anyString());
		doNothing().when(MainFrame).setSize(anyInt(), anyInt());
		doNothing().when(MainFrame).setLocationRelativeTo(isNull());
		return InOrder;
	}
}