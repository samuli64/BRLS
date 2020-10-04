package library.entities;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

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
class LoanPatronIntegrationTest {

	ILoan loan;
	@Mock IBook book;
	IPatron patron;

	String firstName = "Max";
	String lastName = "Mustermann";
	String emailAddress = "max.mustermann@example.com";
	long phoneNumber = 198765432;
	int patronId = 1;
	int bookId = 1;
	int loanId = 1;
	Date earlierDate;
	Date laterDate;
	
	@BeforeEach
	void setUp() throws Exception {
		patron = new Patron(lastName, firstName, emailAddress, phoneNumber, patronId);
		loan = new Loan(book, patron);
		earlierDate = TestUtilities.dateOf(2020, 2, 23);
		laterDate = TestUtilities.dateOf(2020, 2, 24);
	}

	@AfterEach
	void tearDown() throws Exception {
	}


	@Test
	void commit_PatronCanBorrow_AddsLoanToPatronCurrentLoanRecord() {
		// arrange
		assertEquals(PatronState.CAN_BORROW, ((Patron) patron).getState());
		assertFalse(patron.getLoans().contains(loan));
		boolean expected = true;
		
		// act
		loan.commit(loanId, earlierDate);
		boolean actual = patron.getLoans().contains(loan);
		
		// assert
		assertEquals(expected, actual);
		assertTrue(loan.isCurrent());
	}
	
	@Test
	void commit_PatronRestricted_ThrowsException() {
		// arrange
		patron.restrictBorrowing();
		assertEquals(PatronState.RESTRICTED, ((Patron) patron).getState());
		
		// act
		RuntimeException thrown = assertThrows(RuntimeException.class, 
				() -> { loan.commit(loanId, earlierDate); });
		
		// assert
		assertTrue(thrown.getClass().equals(RuntimeException.class));
	}
	@Test
	
	void commit_PatronRestricted_LeavesLoanStateUnchanged() {
		// arrange
		patron.restrictBorrowing();
		assertEquals(PatronState.RESTRICTED, ((Patron) patron).getState());
		assertTrue(book.isOnLoan());
		
		// act
		assertThrows(RuntimeException.class, 
				() -> { loan.commit(loanId, earlierDate); });
		
		// assert
		assertTrue(book.isOnLoan());
	}

	@Test
	void hasOverDueLoans_WhenLoanOverDue_ReturnsTrue() {
		// arrange
		loan.commit(loanId, earlierDate);
		loan.checkOverDue(laterDate);
		assertTrue(loan.isOverDue());
		assertTrue(patron.getLoans().contains(loan));
		boolean expected = true;
		// act
		boolean actual = patron.hasOverDueLoans();
		// assert
		assertEquals(expected, actual);
	}

	@Test
	void hasOverDueLoans_WhenLoansCurrent_ReturnsFalse() {
		// arrange
		loan.commit(loanId, laterDate);
		loan.checkOverDue(earlierDate);
		assertFalse(loan.isOverDue());
		assertTrue(patron.getLoans().contains(loan));
		boolean expected = false;
		// act
		boolean actual = patron.hasOverDueLoans();
		// assert
		assertEquals(expected, actual);
	}

}
