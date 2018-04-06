package spencer.myfoodbudget;

import android.content.Context;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;

/**
 * Created by Spencer on 7/31/2017.
 */

public class Budget implements Serializable {

    public static final double DEFAULT_BUDGET_AMT = 100.0;

    private ArrayList<Expense> expenses;
    private double budgetAmount;
    private int month;

    public Budget(int month, double budgetAmount)
    {
        this.month = month;
        expenses = new ArrayList<Expense>();
        this.budgetAmount = budgetAmount;
    }

    public ArrayList<Expense> getExpenses() { return expenses; }

    public int getMonth() { return month; }

    public int getYear() { return expenses.get(0).getYear(); }

    public double getBudgetAmount() { return budgetAmount; }

    public double getRemainingBudgetAmount()
    {
        double amount = budgetAmount;
        for (Expense e : expenses) { amount -= e.getAmount(); }
        return amount;
    }

    public int getRemainingDays()
    {
        return -1;
    }

    public void setBudgetAmount(double budgetAmount) { this.budgetAmount = budgetAmount; }

    public void addExpense(Expense e) {
        expenses.add(0, e);
    }

    public void addExpense(Expense e, Context context) {
        expenses.add(0, e);
        MFBlogic.getInstance().writeSaveData(context);
    }

    public void sortByDate() { Collections.sort(expenses, new DateComparator()); }

    public void sortByAmount() { Collections.sort(expenses, new AmountComparator()); }

    public static class DateComparator implements Comparator<Expense>
    {
        public int compare(Expense e1, Expense e2)
        {
            return e2.getCalendar().compareTo(e1.getCalendar());
        }
    }

    public static class AmountComparator implements Comparator<Expense>
    {
        public int compare(Expense e1, Expense e2)
        {
            Double value = e2.getAmount() - e1.getAmount();
            if (value < 0)
                return -1;
            else if (value > 0)
                return 1;
            else
                return 0;
        }
    }

    public void editExpense(Expense e, Context context)
    {
        for (Expense exp: expenses)
        {
            if (exp.equals(e))
            {
                exp.setAmount(e.getAmount());
                exp.setType(e.isRestaurant());
                exp.setNote(e.getNote());
                break;
            }
        }
        MFBlogic.getInstance().writeSaveData(context);
    }

    public void removeExpense(Expense e, Context context)
    {
        for (int i = 0; i < expenses.size(); i++)
        {
            Expense exp = expenses.get(i);
            if (exp.equals(e))
            {
                expenses.remove(exp);
                if (this != MFBlogic.getInstance().getCurrentBudget() && expenses.isEmpty())
                    MFBlogic.getInstance().removeBudget(this);
                break;
            }
        }
        MFBlogic.getInstance().writeSaveData(context);
    }

}
