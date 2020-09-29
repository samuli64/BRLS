package library.entities;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Map;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import library.entities.helpers.IBookHelper;
import library.entities.helpers.ILoanHelper;
import library.entities.helpers.IPatronHelper;

@ExtendWith(MockitoExtension.class)
class LibraryTest {
	
	ILibrary library;
	@Mock IBookHelper bookHelper;
	@Mock IPatronHelper patronHelper;
	@Mock ILoanHelper loanHelper;
	@Mock Map<Integer, IBook> catalog;
	@Mock Map<Integer, IPatron> patrons;
	@Mock Map<Integer, ILoan> loans;
	@Mock Map<Integer, ILoan> currentLoans;
	@Mock Map<Integer, IBook> damagedBooks;
	

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
	void testPatronCanBorrow() {
		fail("Not yet implemented");
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
