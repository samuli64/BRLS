package library.entities;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Date;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import library.test.*;

@ExtendWith(MockitoExtension.class)
class LoanTest {
	ILoan loan;
	@Mock IBook book;
	@Mock IPatron patron;
	Date earlierDate;
	Date laterDate;

	@BeforeEach
	void setUp() throws Exception {
		//MockitoAnnotations.initMocks(this);
		earlierDate = TestUtilities.dateOf(2020, 2, 23);
		laterDate = TestUtilities.dateOf(2020, 2, 24);
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
	

	@Test
	void testIsOverDue_WhenStateOverdue_ReturnsTrue() {
		// arrange
		boolean expected = true;
		// act
		boolean actual = defaultBook.isAvailable();
		// assert
		assertEquals(expected, actual);
	}

	/*@Test
	void isAvailable_WhenStateDamaged_ReturnsFalse() {
		// arrange
		IBook damagedBook = new Book(author, title, callNumber, id, IBook.BookState.DAMAGED);
		boolean expected = false;
		// act
		boolean actual = damagedBook.isAvailable();
		// assert
		assertEquals(expected, actual);
	}

	@Test
	void isAvailable_WhenStateOnLoan_ReturnsFalse() {
		// arrange
		IBook onLoanBook = new Book(author, title, callNumber, id, IBook.BookState.ON_LOAN);
		boolean expected = false;
		// act
		boolean actual = onLoanBook.isAvailable();
		// assert
		assertEquals(expected, actual);*/
	}

}
