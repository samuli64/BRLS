package library.entities;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Date;
import java.util.Map;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.junit.jupiter.MockitoExtension;

import library.entities.helpers.IBookHelper;
import library.entities.helpers.ILoanHelper;
import library.entities.helpers.IPatronHelper;

@ExtendWith(MockitoExtension.class)
@RunWith(MockitoJUnitRunner.class)
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
	@Mock ILoan loan;
	@Mock IBook book;
	

	@BeforeEach
	void setUp() throws Exception {
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
	void issueLoan_PreconditionsMet_ReturnsValidLoan() {
		// arrange
		ILibrary spyLibrary = Mockito.spy(library);
		Mockito.lenient().doReturn(true).when(spyLibrary).patronCanBorrow(patron);
		Mockito.lenient().when(book.isAvailable()).thenReturn(true);
		Mockito.when(loanHelper.makeLoan(book, patron)).thenReturn(loan);
		ILoan expected = loan;
		// act
		ILoan actual = spyLibrary.issueLoan(book, patron);
		// assert
		assertEquals(expected, actual);
	}

	@Test
	void issueLoan_BookUnavailable_ThrowsException() {
		// arrange
		ILibrary spyLibrary = Mockito.spy(library);
		Mockito.lenient().doReturn(true).when(spyLibrary).patronCanBorrow(patron);
		Mockito.lenient().when(book.isAvailable()).thenReturn(false);
		// act
		RuntimeException thrown = assertThrows(RuntimeException.class, 
				() -> {spyLibrary.issueLoan(book, patron);});
		// assert
		assertTrue(thrown.getClass().equals(RuntimeException.class));
	}

	@Test
	void issueLoan_PatronCantBorrow_ThrowsException() {
		// arrange
		ILibrary spyLibrary = Mockito.spy(library);
		Mockito.lenient().doReturn(false).when(spyLibrary).patronCanBorrow(patron);
		Mockito.lenient().when(book.isAvailable()).thenReturn(true);
		// act
		RuntimeException thrown = assertThrows(RuntimeException.class, 
				() -> {spyLibrary.issueLoan(book, patron);});
		// assert
		assertTrue(thrown.getClass().equals(RuntimeException.class));
	}

	@Test
	void commitLoan_validLoan_CallsLoanCommit() {
		// arrange
		ILibrary spyLibrary = Mockito.spy(library);
		Mockito.lenient().doReturn(true).when(spyLibrary).patronCanBorrow(patron);
		Mockito.when(loan.getBook()).thenReturn(book);
		Mockito.when(loan.getPatron()).thenReturn(patron);
		Mockito.when(book.getId()).thenReturn(1);
		// act
		spyLibrary.commitLoan(loan);
		// assert
		Mockito.verify(loan).commit(ArgumentMatchers.anyInt(), ArgumentMatchers.any(Date.class));
	}

	@Test
	void commitLoan_validLoan_PutsLoanIntoLoans() {
		// arrange
		ILibrary spyLibrary = Mockito.spy(library);
		Mockito.lenient().doReturn(true).when(spyLibrary).patronCanBorrow(patron);
		Mockito.when(loan.getBook()).thenReturn(book);
		Mockito.when(loan.getPatron()).thenReturn(patron);
		Mockito.when(book.getId()).thenReturn(1);
		// act
		spyLibrary.commitLoan(loan);
		// assert
		Mockito.verify(loans).put(ArgumentMatchers.anyInt(), ArgumentMatchers.eq(loan));
	}

	@Test
	void commitLoan_validLoan_PutsLoanIntoCurrentLoans() {
		// arrange
		int bookId = 1;
		ILibrary spyLibrary = Mockito.spy(library);
		Mockito.lenient().doReturn(true).when(spyLibrary).patronCanBorrow(patron);
		Mockito.when(loan.getBook()).thenReturn(book);
		Mockito.when(loan.getPatron()).thenReturn(patron);
		Mockito.when(book.getId()).thenReturn(bookId);
		// act
		spyLibrary.commitLoan(loan);
		// assert
		Mockito.verify(currentLoans).put(bookId, loan);
	}

}
