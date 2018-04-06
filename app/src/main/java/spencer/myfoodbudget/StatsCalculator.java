package spencer.myfoodbudget;

import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * Created by Eric on 8/9/2017.
 */

public class StatsCalculator {

    public StatsCalculator() {}

    public double avgSpentPerDay(Budget b)
    {
        int numDays = 7;
        if (b instanceof MonthBudget)
        {
            Calendar cal = new GregorianCalendar(2000, b.getMonth(), 1);
            numDays = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
        }
        if (b.getRemainingDays() > 0)
            numDays -= b.getRemainingDays();
        double total = 0.0;
        for (Expense e : b.getExpenses())
        {
            total += e.getAmount();
        }
        return total / numDays;
    }

    public double avgSpentPerDayTotal()
    {
        int numMonths = 0;
        double total = 0.0;
        int size = MFBlogic.getInstance().getBudgets().size();
        for (int i = 0; i < size; i++)
        {
            Budget b = MFBlogic.getInstance().getBudgets().get(i);
            if (b instanceof MonthBudget)
                total += avgSpentPerDay(b);
            else
            {
                int numWeeks = 1;
                double monthTotal = avgSpentPerDay(b);
                while (i + 1 < size && b instanceof WeekBudget)
                {
                    b = MFBlogic.getInstance().getBudgets().get(++i);
                    monthTotal += avgSpentPerDay(b);
                    numWeeks++;
                }
                total += (monthTotal / numWeeks);
                i--;
            }
            numMonths++;
        }
        return total / numMonths;
    }

    public double avgSpentPerWeek(Budget b)
    {
        if (b instanceof WeekBudget)
            return -1;
        double total = 0.0;
        for (Expense e : b.getExpenses())
        {
            total += e.getAmount();
        }
        double numWeeks = 4.0;
        if (b.getRemainingDays() > 0)
        {
            Calendar cal = new GregorianCalendar(2000, b.getMonth(), 1);
            int numDaysInMonth = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
            numWeeks *= ((numDaysInMonth - b.getRemainingDays()) / numDaysInMonth);
        }
        return total / numWeeks;
    }

    public double avgSpentPerWeekTotal()
    {
        return avgSpentPerDayTotal() * 7;
    }

    public double avgSpentOn(int dayOfWeek) // 1 = sunday, 7 = saturday
    {
        int numDays = 0;
        double total = 0.0;
        int size = MFBlogic.getInstance().getBudgets().size();
        for (int i = 0; i < size; i++)
        {
            Budget b = MFBlogic.getInstance().getBudgets().get(i);
            for (Expense e : b.getExpenses())
            {
                if (e.getDay() == dayOfWeek)
                {
                    total += e.getAmount();
                    numDays++;
                }
            }
        }
        if (numDays == 0)
            return 0;
        return total / numDays;
    }

    public double mostSpentDay(Budget b)
    {
        double mostSpent = 0.0;
        int size = b.getExpenses().size();
        for (int i = 0; i < size; i++)
        {
            Expense e = b.getExpenses().get(i);
            int day = e.getCalendar().get(Calendar.DAY_OF_MONTH);
            double dayAmount = e.getAmount();
            while (i + 1 < size)
            {
                e = b.getExpenses().get(++i);
                if (day != e.getCalendar().get(Calendar.DAY_OF_MONTH))
                    break;
                day = e.getCalendar().get(Calendar.DAY_OF_MONTH);
                dayAmount += e.getAmount();
            }
            if (dayAmount > mostSpent)
                mostSpent = dayAmount;
        }
        return mostSpent;
    }

    public double mostSpentDayTotal()
    {
        double mostSpent = 0.0;
        for (Budget b : MFBlogic.getInstance().getBudgets())
        {
            if (mostSpentDay(b) > mostSpent)
                mostSpent = mostSpentDay(b);
        }
        return mostSpent;
    }

    public double mostSpentWeek(Budget b)
    {
        double mostSpent = 0.0;
        int size = b.getExpenses().size();
        for (int i = 0; i < size; i++)
        {
            Expense e = b.getExpenses().get(i);
            int week = e.getWeek();
            double weekAmount = e.getAmount();
            while (i + 1 < size)
            {
                e = b.getExpenses().get(++i);
                if (week != e.getWeek())
                    break;
                week = e.getWeek();
                weekAmount += e.getAmount();
            }
            if (weekAmount > mostSpent)
                mostSpent = weekAmount;
        }
        return mostSpent;
    }

    public double mostSpentWeekTotal()
    {
        double mostSpent = 0.0;
        for (Budget b : MFBlogic.getInstance().getBudgets())
        {
            if (mostSpentWeek(b) > mostSpent)
                mostSpent = mostSpentWeek(b);
        }
        return mostSpent;
    }

    public double budgetDifference()
    {
        Budget b = MFBlogic.getInstance().getCurrentBudget();
        int numDays = 7;
        if (b instanceof MonthBudget)
        {
            Calendar cal = new GregorianCalendar(2000, b.getMonth(), 1);
            numDays = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
        }
        int numDaysPassed = numDays - b.getRemainingDays();
        double totalSpent = b.getBudgetAmount() - b.getRemainingBudgetAmount();
        double avgSpent = totalSpent / numDaysPassed;
        return b.getBudgetAmount() - (avgSpent * numDays);
    }
}
