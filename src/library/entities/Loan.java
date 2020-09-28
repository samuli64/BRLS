package library.entities;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

@SuppressWarnings("serial")
public class Loan implements Serializable, ILoan {

    private int loanId;
    private IBook book;
    private IPatron patron;
    private Date dueDate;
    private LoanState state;

    
    public Loan(IBook book, IPatron patron) {
        this.book = book;
        this.patron = patron;

        this.state = LoanState.PENDING;
    }
 
    
    @Override
    public int getId() {
        return loanId;
    }

    
    @Override
    public IPatron getPatron() {
        return patron;
    }

    
    @Override
    public IBook getBook() {
        return book;
    }

    
    @Override
    public Date getDueDate() {
        return dueDate;
    }

    
    @Override
    public void commit(int loanId, Date dueDate) {
        if (state != LoanState.PENDING) {
            throw new RuntimeException("Cannot commit a non PENDING loan");
        }
        this.loanId = loanId;
        this.dueDate = dueDate;
        this.state = LoanState.CURRENT;
        
        patron.takeOutLoan(this);
        book.borrowFromLibrary();                
    }

    
    @Override
    public void discharge(boolean isDamaged) {
        if (! (state == LoanState.CURRENT || state == LoanState.OVER_DUE)) {
            throw new RuntimeException("Cannot discharge a loand that is not CURRENT or OVERDUE");
        }
        state = LoanState.DISCHARGED;

        patron.dischargeLoan(this);
        book.returnToLibrary(isDamaged);
    }


    @Override
    public boolean checkOverDue(Date currentDate) {
        if (state == LoanState.CURRENT && currentDate.after(dueDate)) {
            this.state = LoanState.OVER_DUE;
            return true;
        }
        return false;
    }

    
    @Override
    public boolean isOverDue() {
        return state == LoanState.OVER_DUE;
    }

    
    public String toString() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        String dueDateString = sdf.format(dueDate);
        int bookId = book.getId();
        String bookTitle = book.getTitle();
        int patronId = patron.getId();
        String patronlastName = patron.getLastName();
        String patronFirstName = patron.getFirstName();

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Loan:  ").append(loanId).append("\n")
            .append("  Borrower ").append(patronId).append(" : ")
            .append(patronlastName).append(", ").append(patronFirstName).append("\n")
            .append("  Book ").append(bookId).append(" : ").append(bookTitle).append("\n")
            .append("  DueDate: ").append(dueDateString).append("\n")
            .append("  State: ").append(state);
        
        return stringBuilder.toString();
    }

    
}
