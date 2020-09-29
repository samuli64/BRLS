package library.entities;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;



@ExtendWith(MockitoExtension.class)
class LoanTest {
	ILoan loan;
	@Mock IBook book;
	@Mock IPatron patron;

	@BeforeEach
	void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
	}

	@AfterEach
	void tearDown() throws Exception {
	}

	@Test
	void testCommit() {
		fail("Not yet implemented");
	}

	@Test
	void testCheckOverDue() {
		fail("Not yet implemented");
	}

	@Test
	void testIsOverDue() {
		fail("Not yet implemented");
	}

}
