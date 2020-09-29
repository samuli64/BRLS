package library.test;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.Date;

public class TestUtilities {
	
	/** return a new Java Date object
	 * 
	 * @param year
	 * @param month
	 * @param dayOfMonth
	 * @return
	 */
	public static Date dateOf(int year, int month, int dayOfMonth) {
		// Create Date from LocalDate to avoid depreciated methods
		LocalDate date = LocalDate.of(year, month, dayOfMonth);
		Instant instant = date.atStartOfDay().toInstant(ZoneOffset.UTC);
		return new Date(instant.toEpochMilli());
	}
}
