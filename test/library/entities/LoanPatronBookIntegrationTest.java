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

import library.entities.IPatron.PatronState;
import library.test.TestUtilities;

@ExtendWith(MockitoExtension.class)
@RunWith(MockitoJUnitRunner.class)
class LoanPatronBookIntegrationTest {

	ILoan loan;
	IBook book;
	IPatron patron;

	String firstName = "Max";
	String lastName = "Mustermann";
	String emailAddress = "max.mustermann@example.com";
	long phoneNumber = 198765432;
	int patronId = 1;
	String author = "Stephen King";
	String title = "The Shining";
	String callNumber = "call123";
	int bookId = 1;
	int loanId = 1;
	Date earlierDate;
	Date laterDate;
	
	
	@BeforeEach
	void setUp() throws Exception {
		book = new Book(author, title, callNumber, bookId);
		patron = new Patron(lastName, firstName, emailAddress, phoneNumber, patronId);
		loan = new Loan(book, patron);
		earlierDate = TestUtilities.dateOf(2020, 2, 23);
		laterDate = TestUtilities.dateOf(2020, 2, 24);
	}

	@AfterEach
	void tearDown() throws Exception {
	}


	@Test
	void commit_PatronCanBorrowBookIsAvailable_UpdatesObjectStates() {
		// arrange
		assertEquals(PatronState.CAN_BORROW, ((Patron) patron).getState());
		assertFalse(patron.getLoans().contains(loan));
		assertTrue(book.isAvailable());
		
		// act
		loan.commit(loanId, earlierDate);
		boolean patronContainsLoan = patron.getLoans().contains(loan);
		boolean bookOnLoan = book.isOnLoan();
		
		// assert
		assertTrue(patronContainsLoan);
		assertTrue(bookOnLoan);
		assertTrue(loan.isCurrent());
	}

	@Test
	void commit_PatronCanBorrowBookOnLoan_LeavesStateUnchanged() {
		// arrange
		assertEquals(PatronState.CAN_BORROW, ((Patron) patron).getState());
		assertFalse(patron.getLoans().contains(loan));

		book.borrowFromLibrary();
		assertTrue(book.isOnLoan());
		assertFalse(loan.isCurrent());
		
		// act
		assertThrows(RuntimeException.class, 
				() -> { loan.commit(loanId, earlierDate); });
		boolean patronContainsLoan = patron.getLoans().contains(loan);
		boolean bookOnLoan = book.isOnLoan();
		
		// assert
		assertFalse(patronContainsLoan);
		assertFalse(bookOnLoan);
		assertFalse(loan.isCurrent());
	}

	@Test
	void commit_PatronRestrictedBookIsAvailable_LeavesStateUnchanged() {
		// arrange
		patron.restrictBorrowing();
		assertEquals(PatronState.RESTRICTED, ((Patron) patron).getState());
		assertFalse(patron.getLoans().contains(loan));

		assertTrue(book.isAvailable());
		assertFalse(loan.isCurrent());
		
		// act
		assertThrows(RuntimeException.class, 
				() -> { loan.commit(loanId, earlierDate); });
		boolean patronContainsLoan = patron.getLoans().contains(loan);
		boolean bookOnLoan = book.isOnLoan();
		
		// assert
		assertFalse(patronContainsLoan);
		assertFalse(bookOnLoan);
		assertFalse(loan.isCurrent());
	}

}
