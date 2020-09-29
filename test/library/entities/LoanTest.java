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

import library.entities.ILoan.LoanState;
import library.test.*;

@ExtendWith(MockitoExtension.class)
class LoanTest {
	ILoan loan;
	@Mock IBook book;
	@Mock IPatron patron;
	int loanId = 1;
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
	void testCheckOverDue() {
		fail("Not yet implemented");
	}
	
	@Test
	void commit_WhenStatePending_SetsStateCurrent() {
		// arrange
		ILoan pendingLoan = new Loan(book, patron, loanId, laterDate, LoanState.PENDING);
		boolean expected = true;
		// act
		pendingLoan.commit(loanId, laterDate);
		boolean actual = pendingLoan.isCurrent();
		// assert
		assertEquals(expected, actual);
	}

	
	@Test
	void commit_WhenStateOverdue_ThrowsException() {
		// arrange
		ILoan overdueLoan = new Loan(book, patron, loanId, laterDate, LoanState.OVER_DUE);
		// act
		RuntimeException thrown = assertThrows(RuntimeException.class, () -> {overdueLoan.commit(loanId, laterDate);});
		// assert
		assertTrue(thrown.getClass().equals(RuntimeException.class));
	}
	
	@Test
	void commit_WhenStateCurrent_ThrowsException() {
		// arrange
		ILoan currentLoan = new Loan(book, patron, loanId, laterDate, LoanState.CURRENT);
		// act
		RuntimeException thrown = assertThrows(RuntimeException.class, () -> {currentLoan.commit(loanId, laterDate);});
		// assert
		assertTrue(thrown.getClass().equals(RuntimeException.class));
	}
	
	@Test
	void commit_WhenStateDischarged_ThrowsException() {
		// arrange
		ILoan dischargedLoan = new Loan(book, patron, loanId, laterDate, LoanState.DISCHARGED);
		// act
		RuntimeException thrown = assertThrows(RuntimeException.class, () -> {dischargedLoan.commit(loanId, laterDate);});
		// assert
		assertTrue(thrown.getClass().equals(RuntimeException.class));
	}
	

	@Test
	void isOverDue_WhenStateOverdue_ReturnsTrue() {
		// arrange
		ILoan overdueLoan = new Loan(book, patron, loanId, laterDate, LoanState.OVER_DUE);
		boolean expected = true;
		// act
		boolean actual = overdueLoan.isOverDue();
		// assert
		assertEquals(expected, actual);
	}
	
	@Test
	void isOverDue_WhenStatePending_ReturnsFalse() {
		// arrange
		ILoan pendingLoan = new Loan(book, patron, loanId, laterDate, LoanState.PENDING);
		boolean expected = false;
		// act
		boolean actual = pendingLoan.isOverDue();
		// assert
		assertEquals(expected, actual);
	}
	
	@Test
	void isOverDue_WhenStateCurrent_ReturnsFalse() {
		// arrange
		ILoan currentLoan = new Loan(book, patron, loanId, laterDate, LoanState.CURRENT);
		boolean expected = false;
		// act
		boolean actual = currentLoan.isOverDue();
		// assert
		assertEquals(expected, actual);
	}
	
	@Test
	void isOverDue_WhenStateDischarged_ReturnsFalse() {
		// arrange
		ILoan dischargedLoan = new Loan(book, patron, loanId, laterDate, LoanState.DISCHARGED);
		boolean expected = false;
		// act
		boolean actual = dischargedLoan.isOverDue();
		// assert
		assertEquals(expected, actual);
	}


}
