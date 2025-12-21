package defendthecastle;

import static custommatcher.CustomMatcher.*;
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.util.Arrays;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

import javax.swing.JButton;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedConstruction;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import testdataedit.TestDataEdit;

class MenuMainTest {
	private MainFrame MainFrame;
	private MenuMain MenuMain;
	private FallMotion mockFallMotion;
	private FinalMotion mockFinalMotion;

	@BeforeEach
	void setUp() throws Exception {
		MainFrame = mock(MainFrame.class);
		MenuMain = new MenuMain(MainFrame);
	}
	
	/**
	 * タイトル画像が取り込まれていることを確認。<br>
	 * コア画像が全て取り込まれていることを確認。<br>
	 * コア画像番号がランダムに格納されたリストであるか確認。
	 */
	@Test
	void testVariables() {
		assertThat(MenuMain.getTitleImage(), notNullValue());
		assertThat(MenuMain.getCoreImage(), everyItem(notNullValue()));
		assertThat(MenuMain.getRandamList(), everyItem(allOf(lessThan(MenuMain.getCoreImage().size()), greaterThanOrEqualTo(0))));
	}

	/**
	 * MainFrameが更新されていることを確認。<br>
	 * JButtonがJPanelに追加されているか確認。<br>
	 * 全てのJButtonにActionListenersが追加されているか確認。<br>
	 * scheduleAtFixedRateでのタイマーがセットされているか確認。
	 */
	@Test
	void testMenuMain() {
		ScheduledExecutorService mockScheduler = mock(ScheduledExecutorService.class);
		MockedStatic<Executors> mockExecutor = mockStatic(Executors.class);
		mockExecutor.when(() -> Executors.newScheduledThreadPool(anyInt())).thenReturn(mockScheduler);
		MenuMain = new MenuMain(MainFrame);
		JButton[] allButton = buttonArray();
		assertThat(MenuMain.getMainFrame(), is(MainFrame));
		assertThat(MenuMain.getComponents(), hasAllItemInArray(allButton));
		Stream.of(allButton).forEach(this::assertActionListeners);
		verify(mockScheduler).scheduleAtFixedRate(Mockito.any(Runnable.class), anyLong(), anyLong(), Mockito.any(TimeUnit.class));
		mockExecutor.close();
	}
	
	JButton[] buttonArray() {
		return new JButton[]{MenuMain.getItemGetButton(),
			MenuMain.getItemDisposeButton(),
			MenuMain.getCompositionButton(),
			MenuMain.getSelectStageButton(),
			MenuMain.getTestButton()
		};
	}
	
	void assertActionListeners(JButton button) {
		assertThat(button.getActionListeners(), not(emptyArray()));
	}
	
	/**
	 * JButtonにテキストが設定されており、テキストの全文が表示可能であるか確認。
	 */
	@Test
	void testPaintComponent() {
		MenuMain.paintComponent(brankGraphics());
		JButton[] allButton = buttonArray();
		Stream.of(allButton).forEach(this::assertButton);
	}
	
	Graphics brankGraphics() {
		return new BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB).createGraphics();
	}
	
	void assertButton(JButton button) {
		assertThat(button.getText(), not(emptyOrNullString()));
		assertThat(button, displayAllText());
	}
	
	/**
	 * ガチャ画面が1回のみ呼び出されることを確認。
	 */
	@Test
	void testItemGetButtonAction() {
		ActionEvent e = mock(ActionEvent.class);
		MenuMain.itemGetButtonAction(e);
		verify(MainFrame, times(1)).itemGetMenuDraw();
	}
	
	/**
	 * リサイクル画面が1回のみ呼び出されることを確認。
	 */
	@Test
	void testItemDisposeButtonAction() {
		ActionEvent e = mock(ActionEvent.class);
		MenuMain.itemDisposeButtonAction(e);
		verify(MainFrame, times(1)).itemDisposeMenuDraw();
	}
	
	/**
	 * 編成画面が1回のみ呼び出されることを確認。
	 */
	@Test
	void testCompositionButtonAction() {
		ActionEvent e = mock(ActionEvent.class);
		MenuMain.compositionButtonAction(e);
		verify(MainFrame, times(1)).compositionDraw();
	}
	
	/**
	 * ステージ選択画面が1回のみ呼び出されることを確認。
	 */
	@Test
	void testBattleButtonAction() {
		ActionEvent e = mock(ActionEvent.class);
		MenuMain.battleButtonAction(e);
		verify(MainFrame, times(1)).selectStageDraw();
	}
	
	/**
	 * データ編集画面が1回のみ呼び出されることを確認。
	 */
	@Test
	void testTestButtonAction() {
		MockedConstruction<TestDataEdit> mockTestDataEdit = mockConstruction(TestDataEdit.class);
		ActionEvent e = mock(ActionEvent.class);
		MenuMain.testButtonAction(e);
		assertThat(mockTestDataEdit.constructed(), hasSize(1));
		mockTestDataEdit.close();
	}
	
	/**
	 * futureがキャンセルされていれば、全てのコアとタイトルを描写したか確認。
	 */
	@Test
	void testDrawImageFutureCancelled() {
		Graphics mockGraphics = setMockGraphics();
		setMockFuture(true);
		MenuMain.drawImage(mockGraphics);
		verify(mockGraphics, times(MenuMain.getFinalMotion().length + 1)).drawImage(Mockito.any(Image.class), anyInt(), anyInt(), Mockito.any(ImageObserver.class));
	}
	
	Graphics setMockGraphics() {
		Graphics mockGraphics = mock(Graphics.class);
		doReturn(true).when(mockGraphics).drawImage(Mockito.any(Image.class), anyInt(), anyInt(), Mockito.any(ImageObserver.class));
		return mockGraphics;
	}
	
	void setMockFuture(boolean exists) {
		ScheduledFuture<?> mockFuture = mock(ScheduledFuture.class);
		MenuMain.setMainFuture(mockFuture);
		doReturn(exists).when(mockFuture).isCancelled();
	}
	
	/**
	 * futureが実行中であれば、全ての実行中のコアを描写したか確認。
	 */
	@Test
	void testDrawImageFutureOperation() {
		Graphics mockGraphics = setMockGraphics();
		setMockFuture(false);
		setMockFallMotion();
		MenuMain.drawImage(mockGraphics);
		verify(mockGraphics, times(MenuMain.getFallMotion().length)).drawImage(Mockito.any(Image.class), anyInt(), anyInt(), Mockito.any(ImageObserver.class));
	}
	
	void setMockFallMotion() {
		mockFallMotion = mock(FallMotion.class);
		FallMotion[] mockFallMotionArray = new FallMotion[MenuMain.getFallMotion().length];
		Arrays.fill(mockFallMotionArray, mockFallMotion);
		MenuMain.setFallMotion(mockFallMotionArray);
		doReturn(true).when(mockFallMotion).canStart();
		doNothing().when(mockFallMotion).fallTimerStart(Mockito.any(ScheduledExecutorService.class));
	}
	
	/**
	 * タイマーのカウントが1増加していることを確認。<br>
	 * 落下用タイマーが1つ以上呼ばれたことを確認。<br>
	 * 落下用タイマーが稼働中はメインのタイマーが停止しないことを確認。
	 */
	@Test
	void testEffectTimerProcessNotCancel() {
		setMockFallMotion();
		doReturn(true).when(mockFallMotion).canStart();
		int oldCount = MenuMain.getCount();
		MenuMain.effectTimerProcess();
		assertThat(MenuMain.getCount(), is(oldCount + 1));
		verify(mockFallMotion, atLeastOnce()).fallTimerStart(Mockito.any(ScheduledExecutorService.class));
		assertThat(MenuMain.getMainFuture().isCancelled(), is(false));
	}
	
	/**
	 * カウントがユニット数以上になったら落下用タイマーが呼ばれないことを確認。<br>
	 * 落下用タイマーが停止すればメインのタイマーが停止し、最終段階タイマーが稼働することを確認。
	 */
	@Test
	void testEffectTimerProcessCanCancel() {
		MenuMain.setCount(MenuMain.getFallMotion().length + 1);
		setMockFallMotion();
		setMockFinalMotion();
		doReturn(false).when(mockFallMotion).canStart();
		MenuMain.effectTimerProcess();
		verify(mockFallMotion, never()).fallTimerStart(Mockito.any(ScheduledExecutorService.class));
		verify(mockFinalMotion, atLeastOnce()).finalTimerStart(Mockito.any(ScheduledExecutorService.class));
		assertThat(MenuMain.getMainFuture().isCancelled(), is(true));
	}
	
	void setMockFinalMotion() {
		mockFinalMotion = mock(FinalMotion.class);
		FinalMotion[] mockFinalMotionArray = new FinalMotion[MenuMain.getFinalMotion().length];
		Arrays.fill(mockFinalMotionArray, mockFinalMotion);
		MenuMain.setFinalMotion(mockFinalMotionArray);
		doNothing().when(mockFinalMotion).finalTimerStart(Mockito.any(ScheduledExecutorService.class));
	}
	
	/**
	 * 最終段階タイマーが稼働中ならschedulerも稼働していることを確認。
	 */
	@Test
	void testsSchedulerEndProcessNotEnd() {
		setMockFinalMotion();
		doReturn(false).when(mockFinalMotion).canEnd();
		MenuMain.schedulerEndProcess();
		assertThat(MenuMain.getScheduler().isShutdown(), is(false));
	}
	
	/**
	 * 最終段階タイマーが停止したならschedulerも停止することを確認。
	 */
	@Test
	void testsSchedulerEndProcessCanEnd() {
		setMockFinalMotion();
		doReturn(true).when(mockFinalMotion).canEnd();
		MenuMain.schedulerEndProcess();
		assertThat(MenuMain.getScheduler().isShutdown(), is(true));
	}
}