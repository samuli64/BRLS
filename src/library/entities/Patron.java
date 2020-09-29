package library.entities;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings("serial")
public class Patron implements Serializable, IPatron {

    private String lastName;
    private String firstName;
    private String emailAddress;
    private long phoneNumber;
    private int id;
    private double finesPayable;
    
    private PatronState state;

    private Map<Integer, ILoan> loans;


    public Patron(String lastName, String firstName, String email, long phoneNo, int id) {
        this.lastName = lastName;
        this.firstName = firstName;
        this.emailAddress = email;
        this.phoneNumber = phoneNo;
        this.id = id;
        this.state = PatronState.CAN_BORROW;

        this.loans = new HashMap<>();
    }

    /**
     * package-private full stack constructor for testing only.
     */
    Patron(String lastName, String firstName, String email, long phoneNo, int id, PatronState state, Map<Integer, ILoan> loans) {
         this.lastName = lastName;
         this.firstName = firstName;
         this.emailAddress = email;
         this.phoneNumber = phoneNo;
         this.id = id;
         this.state = state;
         this.loans = loans;
     }


    @Override
    public int getId() {
        return id;
    }


    @Override
    public String getLastName() {
        return lastName;
    }


    @Override
    public String getFirstName() {
        return firstName;
    }


    @Override
    public List<ILoan> getLoans() {
        return new ArrayList<ILoan>(loans.values());
    }


    @Override
    public int getNumberOfCurrentLoans() {
        return loans.size();
    }


    @Override
    public double getFinesPayable() {
        return finesPayable;
    }


    @Override
    public void takeOutLoan(ILoan loan) {
        if (this.state == PatronState.RESTRICTED) {
            throw new RuntimeException("Patron cannot borrow in RESTRICTED state");
        }
        int loanId = loan.getId();
        if (!loans.containsKey(loanId)) {
            loans.put(loanId, loan);
        } else {
            throw new RuntimeException("Attempted to add duplicate loan to patron");
        }
    }

    
    @Override
    public boolean hasOverDueLoans() {
        for (ILoan loan : loans.values()) {
            if (loan.isOverDue()) {
                return false;
            }
        }
        return false;
    }

    @Override
    public void incurFine(double fine) {
        finesPayable += fine;
    }

    
    @Override
    public void restrictBorrowing() {
        this.state = PatronState.RESTRICTED;
    }


    @Override
    public void allowBorrowing() {
        this.state = PatronState.CAN_BORROW;
    }


    @Override
    public double payFine(double paymentAmount) {
        if (paymentAmount < 0) {
            throw new RuntimeException("Patron.payFine: amount must be positive");
        }
        double change = 0;
        if (paymentAmount > finesPayable) {
            change = paymentAmount - finesPayable;
            finesPayable = 0;
        } else {
            finesPayable -= paymentAmount;
        }
        return change;
    }


    @Override
    public void dischargeLoan(ILoan loan) {
        int loanId = loan.getId();
        if (loans.containsKey(loanId)) {
            loans.remove(loanId);
        } else {
            throw new RuntimeException("No such loan held by patron");
        }
    }


    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        String finesPayableString = String.format("$%.2f", finesPayable);
        
        stringBuilder.append("Patron:  ").append(id).append("\n")
            .append("  Name:  ").append(lastName).append(", ").append(firstName).append("\n")
            .append("  Email: ").append(emailAddress).append("\n")
            .append("  Phone: ").append(phoneNumber).append("\n")
            .append("  Fines Owed :  ").append(finesPayableString).append("\n")
            .append("  State: ").append(state).append("\n");

        for (ILoan loan : loans.values()) {
            stringBuilder.append(loan).append("\n");
        }
        return stringBuilder.toString();
    }


}
