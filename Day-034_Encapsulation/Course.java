import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Day 34 — encapsulation done properly.
 *
 * Encapsulation is not "make fields private and add a getter and setter for
 * each". That combination is public fields with extra typing. Encapsulation is
 * controlling HOW state changes, so the object can guarantee something about
 * itself.
 *
 * This class guarantees three things, at all times:
 *   - roster never exceeds capacity
 *   - roster never contains a duplicate
 *   - credits is between 1 and 6
 *
 * Nothing outside the class can break those, because nothing outside the class
 * can reach the state except through methods that check.
 *
 * @author  Divesh Agarwal
 * @since   2026-09-16
 */
public class Course {

    private final String code;
    private final int capacity;

    /** Mutable, and therefore never handed out directly. */
    private final List<String> roster;

    private int credits;

    public Course(String code, int capacity, List<String> initialRoster, int credits) {
        if (code == null || code.isBlank()) {
            throw new IllegalArgumentException("code is required");
        }
        if (capacity <= 0) {
            throw new IllegalArgumentException("capacity must be positive: " + capacity);
        }
        if (initialRoster != null && initialRoster.size() > capacity) {
            throw new IllegalArgumentException("initial roster exceeds capacity");
        }

        this.code = code;
        this.capacity = capacity;
        this.credits = validCredits(credits);

        // DEFENSIVE COPY IN: without this, the caller keeps a reference to the
        // very list this object depends on, and can add to it afterwards.
        this.roster = initialRoster == null
                ? new ArrayList<>()
                : new ArrayList<>(initialRoster);
    }

    /**
     * UNMODIFIABLE VIEW OUT: callers can read the roster, and any attempt to
     * change it throws rather than silently corrupting this object.
     *
     * A copy would also be safe. A view is cheaper and stays in sync, at the
     * cost of failing at runtime instead of being independent.
     */
    public List<String> getRoster() {
        return Collections.unmodifiableList(roster);
    }

    /**
     * BEHAVIOUR, not a setter. There is no setRoster, because "replace the
     * whole roster" is not a thing the rules allow. Enrolling is.
     *
     * @return true if the student was added
     */
    public boolean enrol(String student) {
        if (student == null || student.isBlank()) {
            throw new IllegalArgumentException("student is required");
        }
        if (isFull() || roster.contains(student)) {
            return false;                       // refuses, rather than corrupting
        }
        return roster.add(student);
    }

    public boolean withdraw(String student) {
        return roster.remove(student);
    }

    /**
     * A legitimate setter: it validates. The point was never that setters are
     * forbidden — only that an unchecked one gives away the guarantee.
     */
    public void setCredits(int credits) {
        this.credits = validCredits(credits);
    }

    private static int validCredits(int credits) {
        if (credits < 1 || credits > 6) {
            throw new IllegalArgumentException("credits must be 1..6: " + credits);
        }
        return credits;
    }

    /** DERIVED, not stored. A field here could drift out of step with roster. */
    public int getSeatsLeft() {
        return capacity - roster.size();
    }

    public boolean isFull() {
        return roster.size() >= capacity;
    }

    public int getEnrolled() {
        return roster.size();
    }

    public String getCode() {
        return code;
    }

    public int getCapacity() {
        return capacity;
    }

    public int getCredits() {
        return credits;
    }

    @Override
    public String toString() {
        return code + "[" + roster.size() + "/" + capacity + ", " + credits + " credits]";
    }
}
