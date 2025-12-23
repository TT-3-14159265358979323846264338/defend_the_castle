package custommatcher;

import java.awt.Insets;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.junit.platform.commons.util.StringUtils;

class MatcherOfDisplayAllText extends BaseMatcher<JComponent>{
	private int displayWidth;
	private int displayHeight;
	private int actualWidth;
	private int actualHeight;
	
	@Override
	public boolean matches(Object obj) {
		String text = text(obj);
		if(StringUtils.isBlank(text)) {
			return false;
		}
		install(obj, text);
		return canClearTest();
	}
	
	String text(Object comp) {
		if(comp instanceof JButton) {
			JButton button = (JButton) comp;
			return button.getText();
		}
		if(comp instanceof JLabel) {
			JLabel label = (JLabel) comp;
			return label.getText();
		}
		return "";
	}
	
	void install(Object obj, String text) {
		JComponent comp = (JComponent) obj;
		Insets margin = comp.getBorder().getBorderInsets(comp);
		displayWidth = comp.getFontMetrics(comp.getFont()).stringWidth(text) + margin.left + margin.right;
		actualWidth = comp.getWidth() ;
		displayHeight = comp.getFontMetrics(comp.getFont()).getHeight() + margin.top + margin.bottom;
		actualHeight = comp.getHeight();
	}
	
	boolean canClearTest() {
		if(displayWidth == 0 || displayHeight == 0 || actualWidth == 0 || actualHeight == 0) {
			return false;
		}
		if(actualWidth <= displayWidth) {
			return false;
		}
		if(actualHeight <= displayHeight) {
			return false;
		}
		return true;
	}

	@Override
	public void describeTo(Description desc) {
		if(displayWidth == 0) {
			desc.appendValue("このインスタンスは使用できないか、表示できるテキストがありません。");
			return;
		}
		desc.appendText("このテキストを表示するには最低限");
		desc.appendValue(String.format("幅%d, 高さ%d", displayWidth, displayHeight));
		desc.appendText("が必要です。しかし、このJButtonの設定値は");
		desc.appendValue(String.format("幅%d, 高さ%d", actualWidth, actualHeight));
		desc.appendText("です。");
	}
}