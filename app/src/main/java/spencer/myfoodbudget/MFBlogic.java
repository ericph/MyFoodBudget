package spencer.myfoodbudget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Random;

public class MFBlogic {

    public final static int JAN = 0;
    public final static int FEB = 1;
    public final static int MAR = 2;
    public final static int APR = 3;
    public final static int MAY = 4;
    public final static int JUN = 5;
    public final static int JUL = 6;
    public final static int AUG = 7;
    public final static int SEP = 8;
    public final static int OCT = 9;
    public final static int NOV = 10;
    public final static int DEC = 11;

    private static Context saveContext;

    private final static String SAVE_DATA_FILENAME = "logicSaveData";
    private final static String CONFIG_DATA_FILENAME = "configSaveData";

    private static MFBlogic instance;

    private static ArrayList<Budget> budgets = new ArrayList<Budget>(Arrays.asList(
            new MonthBudget(Calendar.getInstance().get(Calendar.MONTH), Budget.DEFAULT_BUDGET_AMT)));
    private static ArrayList<Budget> temp = new ArrayList<Budget>();
    public static Config config = new Config();
    public static Bitmap iconBitmap;

    private MFBlogic() {}

    public static MFBlogic getInstance(){
        if(instance == null)
            instance = new MFBlogic();
        return instance;
    }

    public Budget getCurrentBudget(){
        if(budgets.size() == 0)
            return null;
        return budgets.get(0);
    }

    public static void setupIcon(Bitmap b) {
        iconBitmap = b;
    }

    public ArrayList<Budget> getBudgets() { return budgets; }

    public void setBudgets(ArrayList<Budget> budgets) { this.budgets = budgets; }

    public void resetBudgets()
    {
        budgets = new ArrayList<Budget>(Arrays.asList(
                new MonthBudget(Calendar.getInstance().get(Calendar.MONTH), Budget.DEFAULT_BUDGET_AMT)));
        temp = new ArrayList<Budget>();
    }

    public void addBudget(Budget budget)
    {
        Budget prev = budgets.get(0);
        while (budget instanceof MonthBudget && prev.getMonth() == budget.getMonth())
        {
            for (Expense e : prev.getExpenses())
            {
                budget.addExpense(e);
            }
            budgets.remove(0);
            if (budgets.isEmpty())
                break;
            prev = budgets.get(0);
        }
        if (budget instanceof WeekBudget && prev.getMonth() == budget.getMonth())
        {
            for (Expense e : prev.getExpenses())
            {
                if (e.getWeek() == ((WeekBudget) budget).getWeek())
                    budget.addExpense(e);
            }
            if (prev instanceof WeekBudget)
                budgets.remove(0);
        }
        budgets.add(0, budget);
        setCurrentBudget(budget);
    }

    public void removeBudget(Budget budget) {
        budgets.remove(budget);
        if(budgets.isEmpty())
            resetBudgets();
    }


    private void setCurrentBudget(Budget budget) {
        int day = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
        int week = Calendar.getInstance().get(Calendar.WEEK_OF_MONTH);
        int month = budget.getMonth();

        // if new month/week
        if (getCurrentBudget().getMonth() != month ||
                ((budget instanceof WeekBudget && getCurrentBudget() instanceof WeekBudget) &&
                        ((WeekBudget) getCurrentBudget()).getWeek() != week))
            addBudget(budget);

        // same month/week and same type of budget
        else if ((budget instanceof MonthBudget && getCurrentBudget() instanceof MonthBudget)
                || (budget instanceof WeekBudget && getCurrentBudget() instanceof WeekBudget))
            getCurrentBudget().setBudgetAmount(budget.getBudgetAmount());

        // change from weekly to monthly
        else if (budget instanceof MonthBudget && getCurrentBudget() instanceof WeekBudget)
        {
            // create new MonthBudget to replace the current WeekBudget
            MonthBudget mbudget = (MonthBudget) budget;
            Budget prevBudget = getCurrentBudget();
            budgets.remove(getCurrentBudget());

            // for each WeekBudget in the current month, add all expenses to the new MonthBudget
            int i = 0;
            while (i < budgets.size() && prevBudget instanceof WeekBudget)
            {
                if (prevBudget.getMonth() == month)
                {
                    for (Expense e : prevBudget.getExpenses())
                    {
                        mbudget.addExpense(e);
                    }
                    budgets.remove(prevBudget);
                }
                i++;
                prevBudget = budgets.get(i);
            }

            // add MonthBudget to budget list
            addBudget(mbudget);
        }

        // change from monthly to weekly
        else
        {
            // create new WeekBudget to replace the current MonthBudget
            WeekBudget wbudget = (WeekBudget) budget;
            Budget prevBudget = getCurrentBudget();
            budgets.remove(getCurrentBudget());

            // converts previous MonthBudget's budgetAmount to a reasonable weekly budgetAmount
            // (roughly 1/4 of the monthly amount)
            Calendar cal = new GregorianCalendar(budget.getYear(), month, day);
            int numDaysInMonth = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
            double convertedAmount = prevBudget.getBudgetAmount() * (7.0 / numDaysInMonth);

            // create WeekBudgets for previous possible weeks in the month
            WeekBudget wbudget2 = new WeekBudget(month, week - 1, convertedAmount);
            WeekBudget wbudget3 = new WeekBudget(month, week - 2, convertedAmount);
            WeekBudget wbudget4 = new WeekBudget(month, week - 3, convertedAmount);

            // add expenses from MonthBudget to their respective WeekBudget
            // (based on week of expense)
            for (Expense e : prevBudget.getExpenses())
            {
                if (e.getWeek() == week)
                    wbudget.addExpense(e);
                else if (e.getWeek() == week - 1)
                    wbudget2.addExpense(e);
                else if (e.getWeek() == week - 2)
                    wbudget3.addExpense(e);
                else if (e.getWeek() == week - 3)
                    wbudget4.addExpense(e);
            }

            // add WeekBudgets to budget list as necessary,
            // making most recent one the current budget
            if (week - 3 >= 0)
                budgets.add(wbudget4);
            if (week - 2 >= 0)
                budgets.add(wbudget3);
            if (week - 1 >= 0)
                budgets.add(wbudget2);
            addBudget(wbudget);
        }
    }

    public boolean noExpenses()
    {
        for (Budget b : budgets)
        {
            if (!b.getExpenses().isEmpty())
                return false;
        }
        return true;
    }

    final static String TAG = "testTag";

    public static void writeSaveData(Context context) {
        Log.d(TAG, "Entering writeSaveData");
        saveContext = context;
        Runnable r = new Runnable()
        {
            @Override
            public void run()
            {
                Log.d(TAG, "Entering save thread");
                File file = saveContext.getFileStreamPath(SAVE_DATA_FILENAME);
                if (!file.exists())
                {
                    try {
                        file.createNewFile();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                try {
                    FileOutputStream fos = saveContext.openFileOutput(SAVE_DATA_FILENAME, Context.MODE_PRIVATE);
                    ObjectOutputStream oos = new ObjectOutputStream(fos);
                    oos.writeObject(budgets);
                    oos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Log.d(TAG, "Exiting save thread");
            }
        };

        Thread t = new Thread(r);
        t.start();


        Log.d(TAG, "exiting writeSaveData");
    }

    public static void readSaveData(Context context) {
        File file = context.getFileStreamPath(SAVE_DATA_FILENAME);
        if (!file.exists())
            return;
        try {
            FileInputStream fis = context.openFileInput(SAVE_DATA_FILENAME);
            ObjectInputStream is = new ObjectInputStream(fis);
            Object readObject = is.readObject();
            is.close();
            if (readObject == null || (readObject instanceof String && readObject.equals("")))
                return;
            if (readObject instanceof ArrayList) {
                budgets = (ArrayList<Budget>) readObject;
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static void deleteSaveData(Context context, File pictureFile) {
        if (pictureFile.exists())
            pictureFile.delete();

        File file = context.getFileStreamPath(SAVE_DATA_FILENAME);
        if (!file.exists())
            return;
        file.delete();

        budgets = new ArrayList<Budget>(Arrays.asList(
                new MonthBudget(Calendar.getInstance().get(Calendar.MONTH), Budget.DEFAULT_BUDGET_AMT)));
        temp = new ArrayList<Budget>();
    }

    public static void writeConfigData(Context context) {
        File file = context.getFileStreamPath(CONFIG_DATA_FILENAME);
        if (!file.exists())
        {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            FileOutputStream fos = context.openFileOutput(CONFIG_DATA_FILENAME, Context.MODE_PRIVATE);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(config);
            oos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // TODO - fix
    public static void readConfigData(Context context) {

        File file = context.getFileStreamPath(CONFIG_DATA_FILENAME);
        if (!file.exists())
            return;
        try {
            FileInputStream fis = context.openFileInput(CONFIG_DATA_FILENAME);
            ObjectInputStream is = new ObjectInputStream(fis);
            Object readObject = is.readObject();
            is.close();
            if (readObject != null && readObject instanceof Config)
            {
                //Config cf = ((Config) readObject);
                if (((Config) readObject).quickEntryOn())
                    config.flipSwitch("qe");
                if (!((Config) readObject).enableCameraOn())
                    config.flipSwitch("ec");
                if (((Config) readObject).useSampleDataOn())
                    config.flipSwitch("sd");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void useSampleData()
    {
        ArrayList<Budget> sampleData = new ArrayList<Budget>();
        MonthBudget mayBudget = new MonthBudget(MAY, 425);
        MonthBudget juneBudget = new MonthBudget(JUN, 450);
        MonthBudget julyBudget = new MonthBudget(JUL, 475);
        MonthBudget augBudget = new MonthBudget(AUG, 500);

        sampleData.add(augBudget);
        sampleData.add(julyBudget);
        sampleData.add(juneBudget);
        sampleData.add(mayBudget);

        Random r = new Random();
        Calendar gc = Calendar.getInstance();

        for (Budget b : sampleData)
        {
            int numDays = 31;
            if(b.getMonth() == AUG)
                numDays = gc.get(Calendar.DAY_OF_MONTH);
            for (int i = 1; i < numDays; i++)
            {
                gc = new GregorianCalendar(2017, b.getMonth(), i, r.nextInt(23), r.nextInt(59), 0);
                boolean isRestaurant = true;
                if (r.nextInt(7) == 0)
                    isRestaurant = false;
                double amountSpent;
                if (isRestaurant)
                    amountSpent = r.nextDouble() + 4 + r.nextInt(15); // 5 to 20
                else
                    amountSpent = r.nextDouble() + 29 + r.nextInt(20); // 30 to 50
                b.addExpense(new Expense(isRestaurant, gc.getTime(), amountSpent, "Sample Note"));
            }
        }
        temp = new ArrayList<Budget>(budgets);
        budgets = new ArrayList<Budget>(sampleData);
    }

    public void useSaveData()
    {
        if (!temp.isEmpty())
            budgets = new ArrayList<Budget>(temp);
    }
}
