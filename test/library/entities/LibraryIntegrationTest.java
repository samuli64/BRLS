package library.entities;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

import java.util.Date;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.junit.jupiter.MockitoExtension;

import library.entities.helpers.*;
import library.test.TestUtilities;

@ExtendWith(MockitoExtension.class)
@RunWith(MockitoJUnitRunner.class)
class LibraryIntegrationTest {

	
	ILibrary library;
	ILoanHelper loanHelper;
	IPatronHelper patronHelper;
	IBookHelper bookHelper;
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
		loanHelper = new LoanHelper();
		patronHelper = new PatronHelper();
		bookHelper = new BookHelper();
		library = new Library(bookHelper, patronHelper, loanHelper);
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
	void patronCanBorrow_NoRestrictions_ReturnsTrue() {
		// arrange
		boolean expected = true;
		// act
		boolean actual = library.patronCanBorrow(patron);
		// assert
		assertEquals(expected, actual);
	}
	
	@Test
	void patronCanBorrow_LoanLimitReached_ReturnsFalse() {
		// arrange
		for (int loanId = 1; loanId <= ILibrary.LOAN_LIMIT; loanId++) {
			IBook tmpBook = new Book(author, title, callNumber, loanId);
			ILoan tmpLoan = new Loan(tmpBook, patron);
			tmpLoan.commit(loanId, earlierDate);
		}
		assertEquals(patron.getNumberOfCurrentLoans(), ILibrary.LOAN_LIMIT);
		boolean expected = false;
		// act
		boolean actual = library.patronCanBorrow(patron);
		// assert
		assertEquals(expected, actual);
	}
	
	@Test
	void patronCanBorrow_MaxFinesOwedReached_ReturnsFalse() {
		// arrange
		patron.incurFine(ILibrary.MAX_FINES_OWED);
		assertEquals(patron.getFinesPayable(), ILibrary.MAX_FINES_OWED);
		boolean expected = false;
		// act
		boolean actual = library.patronCanBorrow(patron);
		// assert
		assertEquals(expected, actual);
	}
	
	@Test
	void patronCanBorrow_PatronHasOverDueLoans_ReturnsFalse() {
		// arrange
		loan.commit(loanId, earlierDate);
		loan.checkOverDue(laterDate);
		assertTrue(loan.isOverDue());
		assertTrue(patron.getLoans().contains(loan));
		boolean expected = false;
		// act
		boolean actual = library.patronCanBorrow(patron);
		// assert
		assertEquals(expected, actual);
	}

	@Test
	void testIssueLoan() {
		fail("Not yet implemented");
	}

	@Test
	void testCommitLoan() {
		fail("Not yet implemented");
	}

}
