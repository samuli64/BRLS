package library.entities;

import static org.junit.jupiter.api.Assertions.*;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import library.entities.IPatron.PatronState;

@ExtendWith(MockitoExtension.class)
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
		MockitoAnnotations.initMocks(this);
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
		Mockito.when(loan1.isOverDue()).thenReturn(false);
		Mockito.when(loan2.isOverDue()).thenReturn(true);
		boolean expected = true;
		// act
		boolean actual = patron.hasOverDueLoans();
		// assert
		assertEquals(expected, actual);
	}

	@Test
	void hasOverDueLoans_WhenLoansCurrent_ReturnsFalse() {
		// arrange
		Mockito.when(loan1.isOverDue()).thenReturn(false);
		Mockito.when(loan2.isOverDue()).thenReturn(false);
		boolean expected = false;
		// act
		boolean actual = patron.hasOverDueLoans();
		// assert
		assertEquals(expected, actual);
	}

	@Test
	void hasOverDueLoans_WhenLoansCurrent_CallsEachLoanIsOverdue() {
		// arrange
		Mockito.when(loan1.isOverDue()).thenReturn(false);
		Mockito.when(loan2.isOverDue()).thenReturn(false);
		// act
		patron.hasOverDueLoans();
		// assert
		Mockito.verify(loan1).isOverDue();
		Mockito.verify(loan2).isOverDue();
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
		Mockito.when(loan1.getId()).thenReturn(loanId);
		Mockito.when(mockLoans.containsKey(loanId)).thenReturn(false);
		// act
		validPatron.takeOutLoan(loan1);
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

}
