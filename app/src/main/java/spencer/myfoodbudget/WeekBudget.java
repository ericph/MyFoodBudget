package spencer.myfoodbudget;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by Spencer on 7/31/2017.
 */

public class WeekBudget extends Budget implements Serializable {

    private ArrayList<Expense> expenses;
    private double budgetAmount;
    private int month;
    private int week;

    public WeekBudget(int month, int week, double budgetAmount)
    {
        super(month, budgetAmount);
        this.week = week;
    }

    public int getWeek() { return week; }

    @Override
    public int getRemainingDays()
    {
        Calendar cal = Calendar.getInstance();
        return 8 - cal.get(Calendar.DAY_OF_WEEK);
    }
}
