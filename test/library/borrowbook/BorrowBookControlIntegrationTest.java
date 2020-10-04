package library.borrowbook;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.junit.jupiter.MockitoExtension;

import library.entities.*;
import library.entities.helpers.*;
import library.test.TestUtilities;

@ExtendWith(MockitoExtension.class)
@RunWith(MockitoJUnitRunner.class)
class BorrowBookControlIntegrationTest {
	
	@Mock IBorrowBookUI borrowBookUI;
	IBorrowBookControl borrowBookControl;
	
	ILibrary library;
	IPatron patron;
	IBook bookTheHobbit;
	IBook bookToKillAMockingbird;
	IBook bookMobyDick;
	Date earlierDate;
	
	@Captor
	ArgumentCaptor<String> displayStrings;

	@BeforeEach
	void setUp() throws Exception {
		library = new Library(new BookHelper(), new PatronHelper(), new LoanHelper());
		
		bookTheHobbit = library.addBook("J.R.R. Tolkien", "The Hobbit", "c1");
		bookToKillAMockingbird = library.addBook("Harper lee", "To Kill A Mockingbird", "c2");
		bookMobyDick = library.addBook("Herman Melville", "Moby Dick", "c3");
		
		patron = library.addPatron("Mustermann", "Max", "max.mustermann@example.con", 198765432);
		
		borrowBookControl = new BorrowBookControl(library);
		borrowBookControl.setUI(borrowBookUI);
		
		earlierDate = TestUtilities.dateOf(2020, 2, 23);
	}

	@AfterEach
	void tearDown() throws Exception {
	}

	@Test
	void cardSwiped_ValidPatronId_SetsCurrentPatron() {
		// arrange
		int patronId = patron.getId();
		// act
		borrowBookControl.cardSwiped(patronId);
		IPatron actual = ((BorrowBookControl) borrowBookControl).getCurrentPatron();
		// assert
		assertEquals(patron, actual);
	}

	@Test
	void cardSwiped_ValidPatronCanBorrow_CallsSetScanning() {
		// arrange
		int patronId = patron.getId();
		// act
		borrowBookControl.cardSwiped(patronId);
		// assert
		verify(borrowBookUI).setScanning();
	}

	@Test
	void cardSwiped_ValidPatronCanBorrow_SetsStateScanning() {
		// arrange
		int patronId = patron.getId();
		IBorrowBookControl.BorrowControlState expected = IBorrowBookControl.BorrowControlState.SCANNING;
		// act
		borrowBookControl.cardSwiped(patronId);
		IBorrowBookControl.BorrowControlState actual = ((BorrowBookControl) borrowBookControl).getState();
		// assert
		assertEquals(expected, actual);
	}

	@Test
	void cardSwiped_ControlStateNotSwiping_ThrowsException() {
		// arrange
		int patronId = patron.getId();
		borrowBookControl = new BorrowBookControl(library);
		// act
		RuntimeException thrown = assertThrows(RuntimeException.class, 
				() -> { borrowBookControl.cardSwiped(patronId); });
		// assert
		assertEquals(RuntimeException.class, thrown.getClass());
	}
	
	@Test
	void cardSwiped_InvalidPatronId_CallsDisplay() {
		// arrange
		int invalidPatronId = patron.getId() + 1;
		String expectedMessage = "Invalid patronId";
		// act
		borrowBookControl.cardSwiped(invalidPatronId);
		// assert
		verify(borrowBookUI).display(expectedMessage);
	}

	@Test
	void cardSwiped_InvalidPatronId_LeavesStateSwiping() {
		// arrange
		int invalidPatronId = patron.getId() + 1;
		IBorrowBookControl.BorrowControlState expected = IBorrowBookControl.BorrowControlState.SWIPING;
		// act
		borrowBookControl.cardSwiped(invalidPatronId);
		IBorrowBookControl.BorrowControlState actual = ((BorrowBookControl) borrowBookControl).getState();
		// assert
		assertEquals(expected, actual);
	}

	@Test
	void cardSwiped_ValidPatronHasMaxFinesCantBorrow_CallsSetRestricted() {
		// arrange
		int patronId = patron.getId();
		patron.incurFine(ILibrary.MAX_FINES_OWED);
		// act
		borrowBookControl.cardSwiped(patronId);
		// assert
		verify(borrowBookUI).setRestricted();
	}

	
	@Test
	void cardSwiped_ValidPatronHasMaxFinesCantBorrow_CallsDisplay() {
		// arrange
		int patronId = patron.getId();
		patron.incurFine(ILibrary.MAX_FINES_OWED);
		String expectedMessage = "Patron cannot borrow at this time";
		// act
		borrowBookControl.cardSwiped(patronId);
		// assert
		verify(borrowBookUI).display(expectedMessage);
	}

	
	@Test
	void cardSwiped_ValidPatronHasMaxFinesCantBorrow_SetsStateRestricted() {
		// arrange
		int patronId = patron.getId();
		patron.incurFine(ILibrary.MAX_FINES_OWED);
		IBorrowBookControl.BorrowControlState expected = IBorrowBookControl.BorrowControlState.RESTRICTED;
		// act
		borrowBookControl.cardSwiped(patronId);
		IBorrowBookControl.BorrowControlState actual = ((BorrowBookControl) borrowBookControl).getState();
		// assert
		assertEquals(expected, actual);
	}

	@Test
	void bookScanned_ValidBookIsAvailablePatronNotAtMaxLoans_DisplaysBookInformation() {
		// arrange
		int patronId = patron.getId();
		borrowBookControl.cardSwiped(patronId);
		int theHobbitId = bookTheHobbit.getId();

		// act
		borrowBookControl.bookScanned(theHobbitId);
		
		// assert
		verify(borrowBookUI).display(bookTheHobbit);
	}

	@Test
	void bookScanned_ValidBookIsAvailablePatronNotAtMaxLoans_AddsNewPendingLoan() {
		// arrange
		int patronId = patron.getId();
		borrowBookControl.cardSwiped(patronId);
		int theHobbitId = bookTheHobbit.getId();
		List<ILoan> pendingLoans = ((BorrowBookControl) borrowBookControl).getPendingLoans();
		int numberOfPendingLoans = pendingLoans.size(); 
		assertEquals(0, numberOfPendingLoans);

		// act
		borrowBookControl.bookScanned(theHobbitId);
		numberOfPendingLoans = pendingLoans.size();
		ILoan pendingLoan = pendingLoans.get(0);
		
		// assert
		assertEquals(1, numberOfPendingLoans);
		assertEquals(bookTheHobbit, pendingLoan.getBook());
	}
	
	@Test
	void bookScanned_ValidBookIsAvailablePatronNotAtMaxLoans_StateRemainsScanning() {
		// arrange
		int patronId = patron.getId();
		borrowBookControl.cardSwiped(patronId);
		int theHobbitId = bookTheHobbit.getId();
		IBorrowBookControl.BorrowControlState state = ((BorrowBookControl) borrowBookControl).getState();
		assertEquals(IBorrowBookControl.BorrowControlState.SCANNING, state);

		// act
		borrowBookControl.bookScanned(theHobbitId);
		state = ((BorrowBookControl) borrowBookControl).getState();
		
		// assert
		assertEquals(IBorrowBookControl.BorrowControlState.SCANNING, state);
	}

	@Test
	void bookScanned_ValidBookIsAvailablePatronWillBeAtMaxLoans_DisplaysLoanLimitReached() {
		// arrange
		int patronId = patron.getId();
		for (int loanId = 1; loanId < ILibrary.LOAN_LIMIT; loanId++) {
			IBook tmpBook = new Book("author", "title", "callNumber1", loanId);
			ILoan tmpLoan = new Loan(tmpBook, patron);
			tmpLoan.commit(loanId, earlierDate);
		}
		assertEquals(ILibrary.LOAN_LIMIT - 1, patron.getNumberOfCurrentLoans());
		
		IBorrowBookControl spyBorrowBookControl = spy(borrowBookControl);
		doNothing().when(spyBorrowBookControl).borrowingCompleted();
		spyBorrowBookControl.cardSwiped(patronId);
		int theHobbitId = bookTheHobbit.getId();
		
		String expected = "Loan limit reached";
		
		// act
		spyBorrowBookControl.bookScanned(theHobbitId);
		
		// assert
		verify(borrowBookUI, times(2)).display(displayStrings.capture());
		assertEquals(bookTheHobbit, displayStrings.getAllValues().get(0));
		assertEquals(expected, displayStrings.getAllValues().get(1));
	}

	@Test
	void bookScanned_ValidBookIsAvailablePatronWillBeAtMaxLoans_AddsNewPendingLoan() {
		// arrange
		int patronId = patron.getId();
		for (int loanId = 1; loanId < ILibrary.LOAN_LIMIT; loanId++) {
			IBook tmpBook = new Book("author", "title", "callNumber1", loanId);
			ILoan tmpLoan = new Loan(tmpBook, patron);
			tmpLoan.commit(loanId, earlierDate);
		}
		assertEquals(ILibrary.LOAN_LIMIT - 1, patron.getNumberOfCurrentLoans());
		
		IBorrowBookControl spyBorrowBookControl = spy(borrowBookControl);
		doNothing().when(spyBorrowBookControl).borrowingCompleted();
		spyBorrowBookControl.cardSwiped(patronId);
		int theHobbitId = bookTheHobbit.getId();
		List<ILoan> pendingLoans = ((BorrowBookControl) spyBorrowBookControl).getPendingLoans();
		int numberOfPendingLoans = pendingLoans.size(); 
		assertEquals(0, numberOfPendingLoans);

		// act
		spyBorrowBookControl.bookScanned(theHobbitId);
		numberOfPendingLoans = pendingLoans.size();
		ILoan pendingLoan = pendingLoans.get(0);
		
		// assert
		assertEquals(1, numberOfPendingLoans);
		assertEquals(bookTheHobbit, pendingLoan.getBook());
	}

	@Test
	void bookScanned_ValidBookIsAvailablePatronWillBeAtMaxLoans_CallsBorrowingCompleted() {
		// arrange
		int patronId = patron.getId();
		for (int loanId = 1; loanId < ILibrary.LOAN_LIMIT; loanId++) {
			IBook tmpBook = new Book("author", "title", "callNumber1", loanId);
			ILoan tmpLoan = new Loan(tmpBook, patron);
			tmpLoan.commit(loanId, earlierDate);
		}
		assertEquals(ILibrary.LOAN_LIMIT - 1, patron.getNumberOfCurrentLoans());
		
		IBorrowBookControl spyBorrowBookControl = spy(borrowBookControl);
		doNothing().when(spyBorrowBookControl).borrowingCompleted();
		spyBorrowBookControl.cardSwiped(patronId);
		int theHobbitId = bookTheHobbit.getId();

		// act
		spyBorrowBookControl.bookScanned(theHobbitId);
		
		// assert
		verify(spyBorrowBookControl).borrowingCompleted();
	}

	@Test
	void bookScanned_InvalidBookId_DisplaysInvalidBookIdMessage() {
		// arrange
		int patronId = patron.getId();
		borrowBookControl.cardSwiped(patronId);
		int invalidId = -1;
		String expected = "Invalid bookId";

		// act
		borrowBookControl.bookScanned(invalidId);
		
		// assert
		verify(borrowBookUI).display(expected);
	}

	@Test
	void bookScanned_InvalidBookId_StateRemainsScanning() {
		// arrange
		int patronId = patron.getId();
		borrowBookControl.cardSwiped(patronId);
		int invalidId = -1;
		IBorrowBookControl.BorrowControlState state = ((BorrowBookControl) borrowBookControl).getState();
		assertEquals(IBorrowBookControl.BorrowControlState.SCANNING, state);

		// act
		borrowBookControl.bookScanned(invalidId);
		state = ((BorrowBookControl) borrowBookControl).getState();
		
		// assert
		assertEquals(IBorrowBookControl.BorrowControlState.SCANNING, state);
	}

	@Test
	void bookScanned_ValidBookIsUnavailable_DisplaysInvalidBookIdMessage() {
		// arrange
		int patronId = patron.getId();
		borrowBookControl.cardSwiped(patronId);
		int theHobbitId = bookTheHobbit.getId();
		bookTheHobbit.borrowFromLibrary();
		String expected = "Book cannot be borrowed";

		// act
		borrowBookControl.bookScanned(theHobbitId);
		
		// assert
		verify(borrowBookUI).display(expected);
	}

	@Test
	void bookScanned_ValidBookIsUnavailable_StateRemainsScanning() {
		// arrange
		int patronId = patron.getId();
		borrowBookControl.cardSwiped(patronId);
		int theHobbitId = bookTheHobbit.getId();
		bookTheHobbit.borrowFromLibrary();
		IBorrowBookControl.BorrowControlState state = ((BorrowBookControl) borrowBookControl).getState();
		assertEquals(IBorrowBookControl.BorrowControlState.SCANNING, state);

		// act
		borrowBookControl.bookScanned(theHobbitId);
		state = ((BorrowBookControl) borrowBookControl).getState();
		
		// assert
		assertEquals(IBorrowBookControl.BorrowControlState.SCANNING, state);
	}

	@Test
	void testCommitLoans() {
		// arrange
		
		// act
		
		// assert
		fail("Not yet implemented");
	}

}
