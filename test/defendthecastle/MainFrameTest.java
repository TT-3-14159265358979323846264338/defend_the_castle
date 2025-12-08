package defendthecastle;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;

import java.awt.Component;
import java.awt.Container;

import javax.swing.JFrame;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InOrder;
import org.mockito.Mockito;

import battle.Battle;
import defaultdata.stage.No0001Stage1;
import defendthecastle.composition.MenuComposition;
import defendthecastle.itemdispose.MenuItemDispose;
import defendthecastle.itemget.MenuItemGet;
import defendthecastle.selectstage.MenuSelectStage;

class MainFrameTest {
	private MainFrame MainFrame;
	private Container mockContentPane;
	private Component mockComponent;
	private InOrder InOrder;
	
	@BeforeEach
	void setUp(){
		MainFrame = spy(new MainFrame());
		mockContentPane = mock(Container.class);
		mockComponent = mock(Component.class);
		InOrder = inOrder(mockContentPane, MainFrame);
		doReturn(mockContentPane).when(MainFrame).getContentPane();
		doReturn(mockComponent).when(MainFrame).add(Mockito.any(Component.class));
		doNothing().when(MainFrame).setTitle(anyString());
		doNothing().when(MainFrame).setSize(anyInt(), anyInt());
		doNothing().when(MainFrame).setLocationRelativeTo(isNull());
		doNothing().when(MainFrame).setVisible(anyBoolean());
	}

	/**
	 * setDefaultCloseOperationとsetResizableが設定されているか確認。
	 */
	@Test
	void testMainFrame() {
		MainFrame = new MainFrame();
		assertThat(MainFrame.getDefaultCloseOperation(), is(JFrame.EXIT_ON_CLOSE));
		assertThat(MainFrame.isResizable(), is(false));
	}
	
	/**
	 * Componentが取り除かれた後に新たなComponentが追加されているか確認。
	 */
	@Test
	void testMainMenuDraw() {
		MainFrame.mainMenuDraw();
		InOrder.verify(mockContentPane).removeAll();
		InOrder.verify(MainFrame).add(Mockito.any(MenuMain.class));
	}
	
	/**
	 * Componentが取り除かれた後に新たなComponentが追加されているか確認。
	 */
	@Test
	void testItemGetMenuDraw() {
		MainFrame.itemGetMenuDraw();
		InOrder.verify(mockContentPane).removeAll();
		InOrder.verify(MainFrame).add(Mockito.any(MenuItemGet.class));
	}
	
	/**
	 * Componentが取り除かれた後に新たなComponentが追加されているか確認。
	 */
	@Test
	void testItemDisposeMenuDraw() {
		MainFrame.itemDisposeMenuDraw();
		InOrder.verify(mockContentPane).removeAll();
		InOrder.verify(MainFrame).add(Mockito.any(MenuItemDispose.class));
	}
	
	/**
	 * Componentが取り除かれた後に新たなComponentが追加されているか確認。
	 */
	@Test
	void testCompositionDraw() {
		MainFrame.compositionDraw();
		InOrder.verify(mockContentPane).removeAll();
		InOrder.verify(MainFrame).add(Mockito.any(MenuComposition.class));
	}
	
	/**
	 * Componentが取り除かれた後に新たなComponentが追加されているか確認。
	 */
	@Test
	void testSelectStageDraw() {
		MainFrame.selectStageDraw();
		InOrder.verify(mockContentPane).removeAll();
		InOrder.verify(MainFrame).add(Mockito.any(MenuSelectStage.class));
	}
	
	/**
	 * Componentが取り除かれた後に新たなComponentが追加されているか確認。
	 */
	@Test
	void testBattleDraw() {
		MainFrame.battleDraw(new No0001Stage1(), 0);
		InOrder.verify(mockContentPane).removeAll();
		InOrder.verify(MainFrame).add(Mockito.any(Battle.class));
	}
}