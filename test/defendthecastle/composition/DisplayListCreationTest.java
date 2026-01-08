package defendthecastle.composition;

import static custommatcher.CustomMatcher.*;
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class DisplayListCreationTest {
	@Mock
	private SaveData SaveData;
	
	@InjectMocks
	private DisplayListCreation DisplayListCreation;
	
	/**
	 * 
	 */
	@Test
	void testDisplayListCreation() {
		
	}
}
