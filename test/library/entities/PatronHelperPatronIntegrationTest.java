package library.entities;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.junit.jupiter.MockitoExtension;

import library.entities.IPatron.PatronState;
import library.entities.helpers.IPatronHelper;
import library.entities.helpers.PatronHelper;

@ExtendWith(MockitoExtension.class)
@RunWith(MockitoJUnitRunner.class)
class PatronHelperPatronIntegrationTest {

	IPatronHelper patronHelper;
	IPatron patron;
	String firstName = "Max";
	String lastName = "Mustermann";
	String emailAddress = "max.mustermann@example.com";
	long phoneNumber = 198765432;
	int patronId = 1;
	
	@BeforeEach
	void setUp() throws Exception {
		patronHelper = new PatronHelper();
	}

	@Test
	void makePatron_ValidArguments_ReturnsPatron() {
		// arrange
		patron = null;
		
		// act
		patron = patronHelper.makePatron(lastName, firstName, emailAddress, phoneNumber, patronId);
		
		// assert
		assertTrue(patron instanceof IPatron);
		assertTrue(((Patron) patron).getState() == PatronState.CAN_BORROW);
	}

}
