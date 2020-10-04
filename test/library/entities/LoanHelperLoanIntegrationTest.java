package library.entities;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.junit.jupiter.MockitoExtension;

import library.entities.helpers.ILoanHelper;
import library.entities.helpers.LoanHelper;

@ExtendWith(MockitoExtension.class)
@RunWith(MockitoJUnitRunner.class)
class LoanHelperLoanIntegrationTest {

	ILoanHelper loanHelper;
	ILoan loan;
	@Mock IBook book;
	@Mock IPatron patron;
	
	@BeforeEach
	void setUp() throws Exception {
		loanHelper = new LoanHelper();
	}

	@AfterEach
	void tearDown() throws Exception {
	}

	@Test
	void makeLoan_ValidBookAndLoan_ReturnsPendingLoan() {
		// arrange
		loan = null;
		
		// act
		loan = loanHelper.makeLoan(book, patron);
		
		// assert
		assertTrue(loan instanceof ILoan);
		assertTrue(loan.isPending());
	}

	@Test
	void makeLoan_NullBookArgument_ThrowsException() {
		// arrange
		loan = null;
		
		// act
		IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, 
				() -> { loan = loanHelper.makeLoan(null, patron); });
		
		// assert
		assertTrue(thrown.getClass().equals(IllegalArgumentException.class));
	}

	@Test
	void makeLoan_NullPatronArgument_ThrowsException() {
		// arrange
		loan = null;
		
		// act
		IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, 
				() -> { loan = loanHelper.makeLoan(book, null); });
		
		// assert
		assertTrue(thrown.getClass().equals(IllegalArgumentException.class));
	}

}
