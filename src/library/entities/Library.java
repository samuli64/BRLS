package library.entities;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import library.entities.helpers.IBookHelper;
import library.entities.helpers.ILoanHelper;
import library.entities.helpers.IPatronHelper;


@SuppressWarnings("serial")
public class Library implements Serializable, ILibrary {
	
    private int currentlyIssuingBookId;
    private int currentlyIssuingPatronId;
    private int currentlyIssuingLoanId;
	
    private Map<Integer, IBook> catalog;
    private Map<Integer, IPatron> patrons;
    private Map<Integer, ILoan> loans;
    private Map<Integer, ILoan> currentLoans;
    private Map<Integer, IBook> damagedBooks;
    
    private IBookHelper bookHelper;
    private IPatronHelper patronHelper;
    private ILoanHelper loanHelper;
	

    public Library(IBookHelper bookHelper, IPatronHelper patronHelper, ILoanHelper loanHelper) {
        this.bookHelper = bookHelper;
        this.patronHelper = patronHelper;
        this.loanHelper = loanHelper;
        
        catalog = new HashMap<>();
        patrons = new HashMap<>();
        loans = new HashMap<>();
        currentLoans = new HashMap<>();
        damagedBooks = new HashMap<>();
        currentlyIssuingBookId = 1;
        currentlyIssuingPatronId = 1;		
        currentlyIssuingLoanId = 1;		
    }
    
    /**
     * package-private full stack constructor for testing only.
     */
    Library(IBookHelper bookHelper, IPatronHelper patronHelper, ILoanHelper loanHelper,
    		Map<Integer, IBook> catalog, Map<Integer, IPatron> patrons,
    		Map<Integer, ILoan> loans, Map<Integer, ILoan> currentLoans,
    		Map<Integer, IBook> damagedBooks) {
        this.bookHelper = bookHelper;
        this.patronHelper = patronHelper;
        this.loanHelper = loanHelper;
        
        this.catalog = catalog;
        this.patrons = patrons;
        this.loans = loans;
        this.currentLoans = currentLoans;
        this.damagedBooks = damagedBooks;
        currentlyIssuingBookId = 1;
        currentlyIssuingPatronId = 1;		
        currentlyIssuingLoanId = 1;		
    }

	
    private int getNextBookId() {
        return currentlyIssuingBookId++;
    }

	
    private int getNextPatronId() {
        return currentlyIssuingPatronId++;
    }

	
    private int getNextLoanId() {
        return currentlyIssuingLoanId++;
    }        

	
    @Override
    public List<IPatron> getPatronList() {		
        return new ArrayList<IPatron>(patrons.values()); 
    }


    @Override
    public List<IBook> getBookList() {		
        return new ArrayList<IBook>(catalog.values()); 
    }


    @Override
    public List<ILoan> getCurrentLoansList() {
        return new ArrayList<ILoan>(currentLoans.values());
    }


    @Override
    public List<ILoan> getAllLoansList() {
        return new ArrayList<ILoan>(loans.values());
    }


    @Override
    public IPatron addPatron(String lastName, String firstName, String email, long phoneNo) {
        int patronId = getNextPatronId();
        
        IPatron patron = patronHelper.makePatron(lastName, firstName, email, phoneNo, patronId);
        
        patrons.put(patronId, patron);	
        
        return patron;
    }

	
    @Override
    public IBook addBook(String author, String title, String callNumber) {
        int bookId = getNextBookId();
        
        IBook book = bookHelper.makeBook(author, title, callNumber, bookId);
        
        catalog.put(bookId, book);		        
        return book;
    }

	
    @Override
    public IPatron getPatronById(int patronId) {
        if (patrons.containsKey(patronId)) {
            return patrons.get(patronId);
        }
        return null;
    }

	
    @Override
    public IBook getBookById(int bookId) {
        if (catalog.containsKey(bookId)) {
            return catalog.get(bookId);		
        }
        return null;
    }

		
    @Override
    public boolean patronWillReachLoanMax(IPatron patron, int numberOfPendingLoans) {
        return patron.getNumberOfCurrentLoans() + numberOfPendingLoans == LOAN_LIMIT;
    }
    
    
    @Override
    public boolean patronCanBorrow(IPatron patron) {		
        if (patron.getNumberOfCurrentLoans() > LOAN_LIMIT) {
            return false;
        }
				
        if (patron.getFinesPayable() >= MAX_FINES_OWED) {
            return false;
        }
				
        if (patron.hasOverDueLoans()) {
                return false;
        }			
        return true;
    }

	
    private void setPatronBorrowingRestrictions(IPatron patron) {
        if (!patronCanBorrow(patron)) {
            patron.restrictBorrowing();
        } else {
            patron.allowBorrowing();
        }
    }


    @Override
    public ILoan issueLoan(IBook book, IPatron patron) {        
        ILoan loan = loanHelper.makeLoan(book, patron); 
        return loan;
    }
    
    
    @Override
    public void commitLoan(ILoan loan) {
        Date dueDate = Calendar.getInstance().getDueDate(LOAN_PERIOD);
        int loanId = getNextLoanId();
        
        loan.commit(loanId, dueDate);
        loans.put(loanId, loan);
        
        IBook book = loan.getBook();
        Integer bookId = book.getId();
        currentLoans.put(bookId, loan);

        IPatron patron = loan.getPatron();
        setPatronBorrowingRestrictions(patron);        
    }
	
	
    @Override
    public ILoan getCurrentLoanByBookId(int bookId) {
        if (currentLoans.containsKey(bookId)) {
            return currentLoans.get(bookId);
        }
        return null;
    }

	
    @Override
    public void dischargeLoan(ILoan loan, boolean isDamaged) {
        IPatron patron = loan.getPatron();
        IBook book  = loan.getBook();
        
        double overDueFine = calculateOverDueFine(loan);
        patron.incurFine(overDueFine);	
		
        Integer bookId = book.getId();
        if (isDamaged) {
            patron.incurFine(DAMAGE_FEE);
            damagedBooks.put(bookId, book);
        }
        loan.discharge(isDamaged); 
        
        currentLoans.remove(bookId);        
        setPatronBorrowingRestrictions(patron);
    }
    

    @Override
    public double calculateOverDueFine(ILoan loan) {
        double fine = 0.0;
        if (loan.isOverDue()) {
            Date dueDate = loan.getDueDate();
            long daysOverDue = Calendar.getInstance().getDaysDifference(dueDate);
            fine = daysOverDue * FINE_PER_DAY;
        }
        return fine;     
    }
    

    @Override
    public void checkCurrentLoansOverDue() {
        boolean overdue;
        Date currentDate = Calendar.getInstance().getDate();
        for (ILoan loan : currentLoans.values()) {
            overdue = loan.checkOverDue(currentDate);
            if (overdue) {
                IPatron patron = loan.getPatron();
                setPatronBorrowingRestrictions(patron);
            }
        }		
    }


    @Override
    public void repairBook(IBook currentBook) {
        Integer currentBookId = currentBook.getId();
        if (damagedBooks.containsKey(currentBookId)) {
            currentBook.repair();
            damagedBooks.remove(currentBookId);
        }
        else {
            throw new RuntimeException("Library: repairBook: book is not damaged");
        }
		
    }

    @Override
    public double payFine(IPatron patron, double amount) {
        double change = patron.payFine(amount);
        setPatronBorrowingRestrictions(patron);        
        return change;
    }
}
