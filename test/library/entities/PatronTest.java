package library.entities;

import static org.junit.jupiter.api.Assertions.*;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import static org.mockito.Mockito.*;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.junit.jupiter.MockitoExtension;

import library.entities.IPatron.PatronState;

@ExtendWith(MockitoExtension.class)
@RunWith(MockitoJUnitRunner.class)
class PatronTest {
	
	IPatron patron;
	Map<Integer, ILoan> loans;
	String firstName = "Max";
	String lastName = "Mustermann";
	String emailAddress = "max.mustermann@example.com";
	long phoneNumber = 198765432;
	int id	= 1;
	
	@Mock ILoan loan1;
	@Mock ILoan loan2;
	@Mock ILoan loan3;
	@Mock Map<Integer, ILoan> mockLoans;

	@BeforeEach
	void setUp() throws Exception {
		loans = new HashMap<Integer, ILoan>();
		loans.put(1, loan1);
		loans.put(2, loan2);
		
		patron = new Patron(firstName, lastName, emailAddress, phoneNumber, id, PatronState.CAN_BORROW, loans);
	}

	@AfterEach
	void tearDown() throws Exception {
	}

	@Test
	void hasOverDueLoans_WhenLoanOverDue_ReturnsTrue() {
		// arrange
		when(loan1.isOverDue()).thenReturn(false);
		when(loan2.isOverDue()).thenReturn(true);
		boolean expected = true;
		// act
		boolean actual = patron.hasOverDueLoans();
		// assert
		assertEquals(expected, actual);
	}

	@Test
	void hasOverDueLoans_WhenLoansCurrent_ReturnsFalse() {
		// arrange
		when(loan1.isOverDue()).thenReturn(false);
		when(loan2.isOverDue()).thenReturn(false);
		boolean expected = false;
		// act
		boolean actual = patron.hasOverDueLoans();
		// assert
		assertEquals(expected, actual);
	}

	@Test
	void hasOverDueLoans_WhenLoansCurrent_CallsEachLoanIsOverdue() {
		// arrange
		when(loan1.isOverDue()).thenReturn(false);
		when(loan2.isOverDue()).thenReturn(false);
		// act
		patron.hasOverDueLoans();
		// assert
		verify(loan1).isOverDue();
		verify(loan2).isOverDue();
	}

	@Test
	void hasOverDueLoans_WhenNoLoans_ReturnsFalse() {
		// arrange
		Map<Integer, ILoan> emptyLoans = new HashMap<Integer, ILoan>();
		IPatron patronWithNoLoans = new Patron(firstName, lastName, 
				emailAddress, phoneNumber, id, PatronState.CAN_BORROW, emptyLoans);
		boolean expected = false;
		// act
		boolean actual = patronWithNoLoans.hasOverDueLoans();
		// assert
		assertEquals(expected, actual);
	}

	@Test
	void takeOutLoan_PatronBorrowsValidLoan_AddsLoanToLoans() {
		// arrange
		int loanId = 1;
		IPatron validPatron = new Patron(firstName, lastName, 
				emailAddress, phoneNumber, id, PatronState.CAN_BORROW, mockLoans);
		when(loan1.getId()).thenReturn(loanId);
		when(mockLoans.containsKey(loanId)).thenReturn(false);
		// act
		validPatron.takeOutLoan(loan1);
		// assert
		verify(mockLoans).put(loanId, loan1);
	}

	@Test
	void takeOutLoan_LoanBorrowedTwice_ThrowsException() {
		// arrange
		int loanId = 1;
		IPatron greedyPatron = new Patron(firstName, lastName, 
				emailAddress, phoneNumber, id, PatronState.CAN_BORROW, mockLoans);
		when(loan1.getId()).thenReturn(loanId);
		when(mockLoans.containsKey(loanId)).thenReturn(true);
		// act
		RuntimeException thrown = assertThrows(RuntimeException.class, 
				() -> {greedyPatron.takeOutLoan(loan1);});
		// assert
		assertTrue(thrown.getClass().equals(RuntimeException.class));
	}

	@Test
	void takeOutLoan_PatronRestricted_ThrowsException() {
		// arrange
		IPatron restrictedPatron = new Patron(firstName, lastName, 
				emailAddress, phoneNumber, id, PatronState.RESTRICTED, loans);
		// act
		RuntimeException thrown = assertThrows(RuntimeException.class, 
				() -> {restrictedPatron.takeOutLoan(loan3);});
		// assert
		assertTrue(thrown.getClass().equals(RuntimeException.class));
	}

	/**
	 * Test for valid loan state. Test uses isCurrent() which is not part of specification
	 * due to the lack of an appropriate method in ILoan.
	 */
	@Test
	void takeOutLoan_LoanStateNotCurrent_ThrowsException() {
		// arrange
		int loanId = 3;
		when(loan3.getId()).thenReturn(loanId);
		when(loan3.isCurrent()).thenReturn(false);
		// act
		RuntimeException thrown = assertThrows(RuntimeException.class, 
				() -> {patron.takeOutLoan(loan3);});
		// assert
		assertTrue(thrown.getClass().equals(RuntimeException.class));
	}

}
