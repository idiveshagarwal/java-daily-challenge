/**
 * Day 31 — a first real class.
 *
 * A CLASS is a blueprint: it declares what state each object will hold
 * (fields) and what it can do (methods). An OBJECT is one concrete instance
 * built from that blueprint with `new`, holding its own copy of the state.
 *
 *     class BankAccount   the blueprint — written once
 *     new BankAccount(..) an object      — created as many times as needed
 *
 * @author  Divesh Agarwal
 * @since   2026-09-13
 */
public class BankAccount {

    // ── state: one copy PER OBJECT ─────────────────────────────────────────

    /** Set once in the constructor and never changed (Day 6's blank final). */
    private final String owner;

    /** Private: only this class's own methods may change it. */
    private double balance;

    // ── state: one copy for the WHOLE CLASS ────────────────────────────────

    /** static — shared by every account, not stored inside any one of them. */
    private static int accountsOpened = 0;

    // ── constructor ────────────────────────────────────────────────────────

    /**
     * Runs once, when `new` creates the object. Its job is to leave the object
     * in a valid state before anyone can use it.
     *
     * `this.owner = owner` — the parameter shadows the field, so `this.`
     * names the field explicitly. Writing `owner = owner` compiles, assigns
     * the parameter to itself, and leaves the field unset (ObjectPitfalls).
     */
    public BankAccount(String owner, double openingDeposit) {
        if (owner == null || owner.isBlank()) {
            throw new IllegalArgumentException("owner is required");
        }
        if (openingDeposit < 0) {
            throw new IllegalArgumentException("opening deposit cannot be negative");
        }
        this.owner = owner;
        this.balance = openingDeposit;
        accountsOpened++;
    }

    // ── behaviour ──────────────────────────────────────────────────────────

    /** Instance method: acts on THIS object's balance. */
    public void deposit(double amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("deposit must be positive: " + amount);
        }
        balance += amount;
    }

    /**
     * Returns whether the withdrawal happened, rather than silently allowing
     * an overdraft. The object refuses to enter an invalid state.
     */
    public boolean withdraw(double amount) {
        if (amount <= 0 || amount > balance) {
            return false;
        }
        balance -= amount;
        return true;
    }

    public double getBalance() {
        return balance;
    }

    public String getOwner() {
        return owner;
    }

    /** static method: needs no object, reads only static state. */
    public static int getAccountsOpened() {
        return accountsOpened;
    }

    /**
     * Without this, printing an account shows Object's default —
     * "BankAccount@1b6d3586", the class name and a hash, which tells you
     * nothing about the account.
     */
    @Override
    public String toString() {
        return String.format("BankAccount[owner=%s, balance=%.2f]", owner, balance);
    }
}
