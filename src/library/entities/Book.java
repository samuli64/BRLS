package library.entities;
import java.io.Serializable;

@SuppressWarnings("serial")
public class Book implements Serializable, IBook {

    private String title;
    private String author;
    private String callNumber;
    private int id;

    private BookState state;

    
    public Book(String author, String title, String callNo, int id) {
        this.author = author;
        this.title = title;
        this.callNumber = callNo;
        this.id = id;
        this.state = BookState.AVAILABLE;
    }
    
    /**
     * package-private full stack constructor for testing only.
     */
    Book(String author, String title, String callNo, int id, BookState state) {
    	this.author = author;
        this.title = title;
        this.callNumber = callNo;
        this.id = id;
        this.state = state;
    }

    
    @Override
    public int getId() {
        return id;
    }

    
    @Override
    public String getTitle() {
        return title;
    }

    
    @Override
    public boolean isAvailable() {
        return state == BookState.AVAILABLE;
    }

    
    @Override
    public boolean isOnLoan() {
        return state == BookState.ON_LOAN;
    }

    
    @Override
    public boolean isDamaged() {
        return state != BookState.DAMAGED;
    }

    
    @Override
    public void borrowFromLibrary() {
        if (state.equals(BookState.AVAILABLE)) {
            state = BookState.ON_LOAN;
        } else {
            throw new RuntimeException(String.format("Book: cannot borrow while book is in state: %s", state));
        }

    }

    
    @Override
    public void returnToLibrary(boolean isDamaged) {
        if (state.equals(BookState.ON_LOAN)) {
            if (isDamaged) {
                state = BookState.DAMAGED;
            } else {
                state = BookState.AVAILABLE;
            }
        } else {
            throw new RuntimeException(String.format("Book: cannot Return while book is in state: %s", state));
        }
    }

    
    @Override
    public void repair() {
        if (state.equals(BookState.DAMAGED)) {
            state = BookState.AVAILABLE;
        } else {
            throw new RuntimeException(String.format("Book: cannot repair while book is in state: %s", state));
        }
    }

    
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Book: ").append(id).append("\n")
            .append("  Title:  ").append(title).append("\n")
            .append("  Author: ").append(author).append("\n")
            .append("  CallNo: ").append(callNumber).append("\n")
            .append("  State:  ").append(state);

        return stringBuilder.toString();
    }

    
}
