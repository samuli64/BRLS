package library.entities;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Map;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import library.entities.helpers.IBookHelper;
import library.entities.helpers.ILoanHelper;
import library.entities.helpers.IPatronHelper;

@ExtendWith(MockitoExtension.class)
class LibraryTest {
	/* This class uses some lenient stubbing to avoid issues if a future 
	 * code refactor tests conditions in a different order
	 */
	
	ILibrary library;
	@Mock IBookHelper bookHelper;
	@Mock IPatronHelper patronHelper;
	@Mock ILoanHelper loanHelper;
	@Mock Map<Integer, IBook> catalog;
	@Mock Map<Integer, IPatron> patrons;
	@Mock Map<Integer, ILoan> loans;
	@Mock Map<Integer, ILoan> currentLoans;
	@Mock Map<Integer, IBook> damagedBooks;
	
	@Mock IPatron patron;
	

	@BeforeEach
	void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		library = new Library(bookHelper, patronHelper, loanHelper, catalog,
				patrons, loans, currentLoans, damagedBooks);
	}

	@AfterEach
	void tearDown() throws Exception {
	}

	@Test
	void patronCanBorrow_NoRestrictions_ReturnsTrue() {
		// arrange
		Mockito.when(patron.getNumberOfCurrentLoans()).thenReturn(0);
		Mockito.when(patron.getFinesPayable()).thenReturn(0.0);
		Mockito.when(patron.hasOverDueLoans()).thenReturn(false);
		boolean expected = true;
		// act
		boolean actual = library.patronCanBorrow(patron);
		// assert
		assertEquals(expected, actual);
	}
	
	@Test
	void patronCanBorrow_LoanLimitReached_ReturnsFalse() {
		// arrange
		Mockito.when(patron.getNumberOfCurrentLoans()).thenReturn(ILibrary.LOAN_LIMIT);
		
		Mockito.lenient().when(patron.getFinesPayable()).thenReturn(0.0);
		Mockito.lenient().when(patron.hasOverDueLoans()).thenReturn(false);
		boolean expected = false;
		// act
		boolean actual = library.patronCanBorrow(patron);
		// assert
		assertEquals(expected, actual);
	}
	
	@Test
	void patronCanBorrow_MaxFinesOwedReached_ReturnsFalse() {
		// arrange
		Mockito.when(patron.getNumberOfCurrentLoans()).thenReturn(0);
		Mockito.when(patron.getFinesPayable()).thenReturn(ILibrary.MAX_FINES_OWED);
		Mockito.lenient().when(patron.hasOverDueLoans()).thenReturn(false);
		boolean expected = false;
		// act
		boolean actual = library.patronCanBorrow(patron);
		// assert
		assertEquals(expected, actual);
	}
	
	@Test
	void patronCanBorrow_PatronHasOverDueLoans_ReturnsFalse() {
		// arrange
		Mockito.when(patron.getNumberOfCurrentLoans()).thenReturn(0);
		Mockito.when(patron.getFinesPayable()).thenReturn(0.0);
		Mockito.when(patron.hasOverDueLoans()).thenReturn(true);
		boolean expected = false;
		// act
		boolean actual = library.patronCanBorrow(patron);
		// assert
		assertEquals(expected, actual);
	}

	@Test
	void testIssueLoan() {
		// arrange
		
		// act
		
		// assert
		fail("Not yet implemented");
	}

	@Test
	void testCommitLoan() {
		// arrange
		
		// act
		
		// assert
		fail("Not yet implemented");
	}

}
