package library.entities;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Date;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.junit.jupiter.MockitoExtension;

import library.test.TestUtilities;


@ExtendWith(MockitoExtension.class)
@RunWith(MockitoJUnitRunner.class)
class LoanBookIntegrationTest {
	
	ILoan loan;
	IBook book;
	@Mock IPatron patron;
	
	private String author = "Stephen King";
	private String title = "The Shining";
	private String callNumber = "call123";
	private int bookId = 1;
	private int loanId = 1;
	private Date date;

	@BeforeEach
	void setUp() throws Exception {
		book = new Book(author, title, callNumber, bookId);
		loan = new Loan(book, patron);
		date = TestUtilities.dateOf(2020, 2, 24);
	}

	@AfterEach
	void tearDown() throws Exception {
	}

	@Test
	void commit_BookAvailable_SetsBookStateOnLoan() {
		// arrange
		assertTrue(book.isAvailable());
		boolean expected = true;
		
		// act
		loan.commit(loanId, date);
		boolean actual = book.isOnLoan();
		
		// assert
		assertEquals(expected, actual);
	}

	@Test
	void commit_BookOnLoan_ThrowsException() {
		// arrange
		book.borrowFromLibrary();
		assertTrue(book.isOnLoan());
		
		
		// act
		RuntimeException actual = assertThrows(RuntimeException.class, 
				() -> { loan.commit(loanId, date); });
		
		// assert
		assertEquals(RuntimeException.class, actual);
	}

}
