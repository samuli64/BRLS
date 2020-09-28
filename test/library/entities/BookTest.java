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
		
	}

	@Test
	void testBorrowFromLibrary() {
		// arrange
		
		// act
		
		// assert
		
		fail("Not yet implemented");
	}

}
