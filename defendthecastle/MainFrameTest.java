package defendthecastle;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;

import java.awt.Component;
import java.awt.Container;

import javax.swing.JFrame;
import javax.swing.JPanel;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InOrder;
import org.mockito.Mockito;

import battle.Battle;
import defaultdata.stage.StageData;

class MainFrameTest {
	private MainFrame MainFrame;
	private Container mockContentPane;
	private Component mockComponent;
	
	@BeforeEach
	void setUp(){
		MainFrame = spy(new MainFrame());
		mockContentPane = mock(Container.class);
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
		InOrder inOrder = inOrder(mockContentPane, MainFrame);
		MainFrame.mainMenuDraw();
		inOrder.verify(mockContentPane).removeAll();
		inOrder.verify(MainFrame).add(Mockito.any(Component.class));
	}
	
	/**
	 * Componentが取り除かれた後に新たなComponentが追加されているか確認。
	 */
	@Test
	void testItemGetMenuDraw() {
		InOrder inOrder = inOrder(mockContentPane, MainFrame);
		MainFrame.itemGetMenuDraw();
		inOrder.verify(mockContentPane).removeAll();
		inOrder.verify(MainFrame).add(Mockito.any(Component.class));
	}
	
	/**
	 * Componentが取り除かれた後に新たなComponentが追加されているか確認。
	 */
	@Test
	void testItemDisposeMenuDraw() {
		InOrder inOrder = inOrder(mockContentPane, MainFrame);
		MainFrame.itemDisposeMenuDraw();
		inOrder.verify(mockContentPane).removeAll();
		inOrder.verify(MainFrame).add(Mockito.any(Component.class));
	}
	
	/**
	 * Componentが取り除かれた後に新たなComponentが追加されているか確認。
	 */
	@Test
	void testCompositionDraw() {
		InOrder inOrder = inOrder(mockContentPane, MainFrame);
		MainFrame.compositionDraw();
		inOrder.verify(mockContentPane).removeAll();
		inOrder.verify(MainFrame).add(Mockito.any(Component.class));
	}
	
	/**
	 * Componentが取り除かれた後に新たなComponentが追加されているか確認。
	 */
	@Test
	void testSelectStageDraw() {
		InOrder inOrder = inOrder(mockContentPane, MainFrame);
		MainFrame.selectStageDraw();
		inOrder.verify(mockContentPane).removeAll();
		inOrder.verify(MainFrame).add(Mockito.any(Component.class));
	}
}