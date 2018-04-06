package spencer.myfoodbudget;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by Spencer on 7/31/2017.
 */

public class MonthBudget extends Budget implements Serializable {

    private ArrayList<Expense> expenses;
    private double budgetAmount;
    private int month;

    public MonthBudget(int month, double budgetAmount)
    {
        super(month, budgetAmount);
    }

    @Override
    public int getRemainingDays()
    {
        Calendar cal = Calendar.getInstance();
        return cal.getActualMaximum(Calendar.DAY_OF_MONTH) - cal.get(Calendar.DAY_OF_MONTH) + 1;
    }
}
