package library.entities;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class BookTest {

	private IBook defaultBook;
	private String author = "Stephen King";
	private String title = "The Shining";
	private String callNumber = "call123";
	private int id = 1;
	

	@BeforeEach
	void setUp() throws Exception {
		defaultBook = new Book(author, title, callNumber, id);
	}

	@AfterEach
	void tearDown() throws Exception {
	}

	@Test
	void isAvailable_WhenStateAvailable_ReturnsTrue() {
		// arrange
		boolean expected = true;
		// act
		boolean actual = defaultBook.isAvailable();
		// assert
		assertEquals(expected, actual);
	}

	@Test
	void isAvailable_WhenStateDamaged_ReturnsFalse() {
		// arrange
		IBook damagedBook = new Book(author, title, callNumber, id, IBook.BookState.DAMAGED);
		boolean expected = false;
		// act
		boolean actual = damagedBook.isAvailable();
		// assert
		assertEquals(expected, actual);
	}

	@Test
	void isAvailable_WhenStateOnLoan_ReturnsFalse() {
		// arrange
		IBook onLoanBook = new Book(author, title, callNumber, id, IBook.BookState.ON_LOAN);
		boolean expected = false;
		// act
		boolean actual = onLoanBook.isAvailable();
		// assert
		assertEquals(expected, actual);
	}

	@Test
	void isOnLoan_WhenStateAvailable_ReturnsFalse() {
		// arrange
		boolean expected = false;
		// act
		boolean actual = defaultBook.isOnLoan();
		// assert
		assertEquals(expected, actual);
	}

	@Test
	void isOnLoan_WhenStateDamaged_ReturnsFalse() {
		// arrange
		IBook damagedBook = new Book(author, title, callNumber, id, IBook.BookState.DAMAGED);
		boolean expected = false;
		// act
		boolean actual = damagedBook.isOnLoan();
		// assert
		assertEquals(expected, actual);
	}

	@Test
	void isOnLoan_WhenStateOnLoan_ReturnsTrue() {
		// arrange
		IBook onLoanBook = new Book(author, title, callNumber, id, IBook.BookState.ON_LOAN);
		boolean expected = true;
		// act
		boolean actual = onLoanBook.isOnLoan();
		// assert
		assertEquals(expected, actual);
	}

	@Test
	void isDamaged_WhenStateAvailable_ReturnsFalse() {
		// arrange
		boolean expected = false;
		// act
		boolean actual = defaultBook.isOnLoan();
		// assert
		assertEquals(expected, actual);
	}

	@Test
	void isDamaged_WhenStateDamaged_ReturnsTrue() {
		// arrange
		IBook damagedBook = new Book(author, title, callNumber, id, IBook.BookState.DAMAGED);
		boolean expected = true;
		// act
		boolean actual = damagedBook.isOnLoan();
		// assert
		assertEquals(expected, actual);
	}

	@Test
	void isDamaged_WhenStateOnLoan_ReturnsFalse() {
		// arrange
		IBook onLoanBook = new Book(author, title, callNumber, id, IBook.BookState.ON_LOAN);
		boolean expected = false;
		// act
		boolean actual = onLoanBook.isOnLoan();
		// assert
		assertEquals(expected, actual);
	}
	
	@Test
	void borrowFromLibrary_WhenStateAvailable_UpdatesState() {
		// arrange
		boolean expected = true;
		// act
		defaultBook.borrowFromLibrary();
		boolean actual = defaultBook.isOnLoan();
		// assert
		assertEquals(expected, actual);
	}
	
	@Test
	void borrowFromLibrary_WhenStateDamaged_ThrowsException() {
		// arrange
		IBook damagedBook = new Book(author, title, callNumber, id, IBook.BookState.DAMAGED);
		// act
		RuntimeException thrown = assertThrows(RuntimeException.class, () -> {damagedBook.borrowFromLibrary();});
		// assert
		assertTrue(thrown.getClass().equals(RuntimeException.class));
	}
	
	@Test
	void borrowFromLibrary_WhenStateDamaged_LeavesStateUnchanged() {
		// arrange
		IBook damagedBook = new Book(author, title, callNumber, id, IBook.BookState.DAMAGED);
		boolean expected = true;
		// act
		RuntimeException thrown = assertThrows(RuntimeException.class, () -> {damagedBook.borrowFromLibrary();});
		boolean actual = damagedBook.isDamaged();
		// assert
		assertEquals(expected, actual);
	}
	
	@Test
	void borrowFromLibrary_WhenStateOnLoan_ThrowsException() {
		// arrange
		IBook onLoanBook = new Book(author, title, callNumber, id, IBook.BookState.ON_LOAN);
		// act
		RuntimeException thrown = assertThrows(RuntimeException.class, () -> {onLoanBook.borrowFromLibrary();});
		// assert
		assertTrue(thrown.getClass().equals(RuntimeException.class));
	}
	
	@Test
	void borrowFromLibrary_WhenStateOnLoan_LeavesStateUnchanged() {
		// arrange
		IBook onLoanBook = new Book(author, title, callNumber, id, IBook.BookState.ON_LOAN);
		boolean expected = true;
		// act
		RuntimeException thrown = assertThrows(RuntimeException.class, () -> {onLoanBook.borrowFromLibrary();});
		boolean actual = onLoanBook.isOnLoan();
		// assert
		assertEquals(expected, actual);
	}

}
