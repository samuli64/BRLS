package library.entities;

import java.util.Date;

public interface ILoan {

    enum LoanState {
        PENDING, CURRENT, OVER_DUE, DISCHARGED
    };

    int getId();
    
    boolean checkOverDue(Date currentDate);

    boolean isOverDue();

    boolean isCurrent();

    boolean isPending();

    Date getDueDate();

    IPatron getPatron();

    IBook getBook();
    
    void commit(int loanId, Date dueDate);

    void discharge(boolean isDamaged);

}