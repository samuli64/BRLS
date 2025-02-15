package library.entities;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Date;
import java.util.Map;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import static org.mockito.ArgumentMatchers.*;
import org.mockito.Mock;
import static org.mockito.Mockito.*;
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
		when(patron.getNumberOfCurrentLoans()).thenReturn(0);
		when(patron.getFinesPayable()).thenReturn(0.0);
		when(patron.hasOverDueLoans()).thenReturn(false);
		boolean expected = true;
		// act
		boolean actual = library.patronCanBorrow(patron);
		// assert
		assertEquals(expected, actual);
	}
	
	@Test
	void patronCanBorrow_LoanLimitReached_ReturnsFalse() {
		// arrange
		when(patron.getNumberOfCurrentLoans()).thenReturn(ILibrary.LOAN_LIMIT);
		
		lenient().when(patron.getFinesPayable()).thenReturn(0.0);
		lenient().when(patron.hasOverDueLoans()).thenReturn(false);
		boolean expected = false;
		// act
		boolean actual = library.patronCanBorrow(patron);
		// assert
		assertEquals(expected, actual);
	}
	
	@Test
	void patronCanBorrow_MaxFinesOwedReached_ReturnsFalse() {
		// arrange
		when(patron.getNumberOfCurrentLoans()).thenReturn(0);
		when(patron.getFinesPayable()).thenReturn(ILibrary.MAX_FINES_OWED);
		lenient().when(patron.hasOverDueLoans()).thenReturn(false);
		boolean expected = false;
		// act
		boolean actual = library.patronCanBorrow(patron);
		// assert
		assertEquals(expected, actual);
	}
	
	@Test
	void patronCanBorrow_PatronHasOverDueLoans_ReturnsFalse() {
		// arrange
		when(patron.getNumberOfCurrentLoans()).thenReturn(0);
		when(patron.getFinesPayable()).thenReturn(0.0);
		when(patron.hasOverDueLoans()).thenReturn(true);
		boolean expected = false;
		// act
		boolean actual = library.patronCanBorrow(patron);
		// assert
		assertEquals(expected, actual);
	}

	@Test
	void issueLoan_PreconditionsMet_ReturnsValidLoan() {
		// arrange
		ILibrary spyLibrary = spy(library);
		lenient().doReturn(true).when(spyLibrary).patronCanBorrow(patron);
		lenient().when(book.isAvailable()).thenReturn(true);
		when(loanHelper.makeLoan(book, patron)).thenReturn(loan);
		ILoan expected = loan;
		// act
		ILoan actual = spyLibrary.issueLoan(book, patron);
		// assert
		assertEquals(expected, actual);
	}

	@Test
	void issueLoan_BookUnavailable_ThrowsException() {
		// arrange
		ILibrary spyLibrary = spy(library);
		lenient().doReturn(true).when(spyLibrary).patronCanBorrow(patron);
		lenient().when(book.isAvailable()).thenReturn(false);
		// act
		RuntimeException thrown = assertThrows(RuntimeException.class, 
				() -> {spyLibrary.issueLoan(book, patron);});
		// assert
		assertTrue(thrown.getClass().equals(RuntimeException.class));
	}

	@Test
	void issueLoan_PatronCantBorrow_ThrowsException() {
		// arrange
		ILibrary spyLibrary = spy(library);
		lenient().doReturn(false).when(spyLibrary).patronCanBorrow(patron);
		lenient().when(book.isAvailable()).thenReturn(true);
		// act
		RuntimeException thrown = assertThrows(RuntimeException.class, 
				() -> {spyLibrary.issueLoan(book, patron);});
		// assert
		assertTrue(thrown.getClass().equals(RuntimeException.class));
	}

	@Test
	void commitLoan_ValidLoan_CallsLoanCommit() {
		// arrange
		ILibrary spyLibrary = spy(library);
		lenient().doReturn(true).when(spyLibrary).patronCanBorrow(patron);
		when(loan.getBook()).thenReturn(book);
		when(loan.getPatron()).thenReturn(patron);
		when(book.getId()).thenReturn(1);
		// act
		spyLibrary.commitLoan(loan);
		// assert
		verify(loan).commit(anyInt(), any(Date.class));
	}

	@Test
	void commitLoan_ValidLoan_PutsLoanIntoLoans() {
		// arrange
		ILibrary spyLibrary = spy(library);
		lenient().doReturn(true).when(spyLibrary).patronCanBorrow(patron);
		when(loan.getBook()).thenReturn(book);
		when(loan.getPatron()).thenReturn(patron);
		when(book.getId()).thenReturn(1);
		// act
		spyLibrary.commitLoan(loan);
		// assert
		verify(loans).put(anyInt(), eq(loan));
	}

	@Test
	void commitLoan_ValidLoan_PutsLoanIntoCurrentLoans() {
		// arrange
		int bookId = 1;
		ILibrary spyLibrary = spy(library);
		lenient().doReturn(true).when(spyLibrary).patronCanBorrow(patron);
		when(loan.getBook()).thenReturn(book);
		when(loan.getPatron()).thenReturn(patron);
		when(book.getId()).thenReturn(bookId);
		// act
		spyLibrary.commitLoan(loan);
		// assert
		verify(currentLoans).put(bookId, loan);
	}

	@Test
	void commitLoan_InvalidLoan_ThrowsException() {
		// arrange
		ILibrary spyLibrary = spy(library);
		lenient().doReturn(true).when(spyLibrary).patronCanBorrow(patron);
		doThrow(RuntimeException.class).when(loan).commit(anyInt(), any(Date.class));
		// act
		RuntimeException thrown = assertThrows(RuntimeException.class, 
				() -> {spyLibrary.commitLoan(loan);});
		// assert
		assertTrue(thrown.getClass().equals(RuntimeException.class));
	}
	

	@Test
	void getBookById_MatchingBook_ReturnsBook() {
		// arrange
		int bookId = 1;
		when(catalog.containsKey(bookId)).thenReturn(true);
		when(catalog.get(bookId)).thenReturn(book);
		// act
		IBook actual = library.getBookById(bookId);
		// assert
		assertEquals(book, actual);
	}

	@Test
	void getBookById_NoMatchingBook_ReturnsNull() {
		// arrange
		int bookId = 1;
		when(catalog.containsKey(anyInt())).thenReturn(false);
		// act
		IBook actual = library.getBookById(bookId);
		// assert
		assertNull(actual);
	}

	@Test
	void getPatronById_MatchingPatron_ReturnsPatron() {
		// arrange
		int patronId = 1;
		when(patrons.containsKey(patronId)).thenReturn(true);
		when(patrons.get(patronId)).thenReturn(patron);
		// act
		IPatron actual = library.getPatronById(patronId);
		// assert
		assertEquals(patron, actual);
	}

	@Test
	void getPatronById_NoMatchingPatron_ReturnsNull() {
		// arrange
		int patronId = 1;
		when(patrons.containsKey(patronId)).thenReturn(false);
		// act
		IPatron actual = library.getPatronById(patronId);
		// assert
		assertNull(actual);
	}

	@Test
	void patronWillReachLoanMax_HasNoLoanWillNotReachMax_ReturnsFalse() {
		// arrange
		int newLoans = ILibrary.LOAN_LIMIT - 1;
		when(patron.getNumberOfCurrentLoans()).thenReturn(0);
		boolean expected = false;
		// act
		boolean actual = library.patronWillReachLoanMax(patron, newLoans);
		// assert
		assertEquals(expected, actual);
	}

	@Test
	void patronWillReachLoanMax_HasOneLoanWillReachMax_ReturnsTrue() {
		// arrange
		int newLoans = ILibrary.LOAN_LIMIT - 1;
		when(patron.getNumberOfCurrentLoans()).thenReturn(1);
		boolean expected = true;
		// act
		boolean actual = library.patronWillReachLoanMax(patron, newLoans);
		// assert
		assertEquals(expected, actual);
	}

	@Test
	void patronWillReachLoanMax_HasOneLoanWillExceedMax_ReturnsTrue() {
		// arrange
		int newLoans = ILibrary.LOAN_LIMIT;
		when(patron.getNumberOfCurrentLoans()).thenReturn(1);
		boolean expected = true;
		// act
		boolean actual = library.patronWillReachLoanMax(patron, newLoans);
		// assert
		assertEquals(expected, actual);
	}

}
