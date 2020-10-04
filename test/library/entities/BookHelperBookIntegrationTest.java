package library.entities;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.junit.jupiter.MockitoExtension;

import library.entities.helpers.BookHelper;
import library.entities.helpers.IBookHelper;

@ExtendWith(MockitoExtension.class)
@RunWith(MockitoJUnitRunner.class)
class BookHelperBookIntegrationTest {

	IBookHelper bookHelper;
	IBook book;
	String author = "Stephen King";
	String title = "The Shining";
	String callNumber = "call123";
	int id = 1;
	
	@BeforeEach
	void setUp() throws Exception {
		bookHelper = new BookHelper();
	}

	@AfterEach
	void tearDown() throws Exception {
	}

	@Test
	void makeBook_ValidArguments_ReturnsAvailableBook() {
		// arrange
		book = null;
		
		// act
		book = bookHelper.makeBook(author, title, callNumber, id);
		
		// assert
		assertTrue(book instanceof IBook);
		assertTrue(book.isAvailable());
	}

}
