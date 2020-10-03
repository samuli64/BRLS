package library.borrowbook;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.junit.jupiter.MockitoExtension;

import library.entities.*;
import library.entities.helpers.*;

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

	@BeforeEach
	void setUp() throws Exception {
		library = new Library(new BookHelper(), new PatronHelper(), new LoanHelper());
		
		bookTheHobbit = library.addBook("J.R.R. Tolkien", "The Hobbit", "c1");
		bookToKillAMockingbird = library.addBook("Harper lee", "To Kill A Mockingbird", "c2");
		bookMobyDick = library.addBook("Herman Melville", "Moby Dick", "c3");
		
		patron = library.addPatron("Mustermann", "Max", "max.mustermann@example.con", 198765432);
		
		borrowBookControl = new BorrowBookControl(library);
		borrowBookControl.setUI(borrowBookUI);
	}

	@AfterEach
	void tearDown() throws Exception {
	}

	@Test
	void cardSwiped_ValidPatronId_SetsCurrentPatron() {
		fail("Not yet implemented");
	}

	@Test
	void testBookScanned() {
		fail("Not yet implemented");
	}

	@Test
	void testCommitLoans() {
		fail("Not yet implemented");
	}

}
