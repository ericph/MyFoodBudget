package spencer.myfoodbudget;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import java.io.IOException;
import java.text.DateFormatSymbols;
import java.util.ArrayList;
import java.util.List;

public class MyExpensesActivity extends AppCompatActivity {

    private static boolean sortByDate; // true = by date, false = by amount
    public static Budget budgetToDisplay;
    private ListView expensesListView;
    private TextView noExpensesText;
    private Spinner monthSpinner;
    private Spinner sortSpinner;
    private TextView sortByText;

    private Bitmap iconBitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_expenses);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        iconBitmap = MFBlogic.getInstance().iconBitmap;

        sortByDate = true;
        expensesListView = (ListView) findViewById(R.id.MyExpensesListView);
        noExpensesText = (TextView) findViewById(R.id.noExpensesText);
        monthSpinner = (Spinner) findViewById(R.id.monthSpinner);
        sortSpinner = (Spinner) findViewById(R.id.sortSpinner);
        sortByText = (TextView) findViewById(R.id.sortByText);

        //makeMyExpenses();
    }

    @Override
    public void onResume()
    {
        super.onResume();
        makeMyExpenses();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.nav_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_home:
                Intent intent1 = new Intent(this, MainActivity.class);
                startActivity(intent1);
                return true;
            case R.id.nav_my_expenses:
                // something
                return true;
            case R.id.nav_setup_budget:
                Intent intent3 = new Intent(this, BudgetSetupActivity.class);
                startActivity(intent3);
                return true;
            case R.id.nav_statistics:
            Intent intent4 = new Intent(this, StatisticsActivity.class);
            startActivity(intent4);
            return true;
            case R.id.nav_settings:
                Intent intent5 = new Intent(this, SettingsActivity.class);
                startActivity(intent5);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void makeMyExpenses()
    {
        if (MFBlogic.getInstance().noExpenses())
        {
            noExpensesText.setVisibility(View.VISIBLE);
            sortByText.setVisibility(View.GONE);

            monthSpinner.setEnabled(false);
            monthSpinner.setClickable(false);
            monthSpinner.setVisibility(View.GONE);

            sortSpinner.setEnabled(false);
            sortSpinner.setClickable(false);
            sortSpinner.setVisibility(View.GONE);

            expensesListView.setEnabled(false);
            expensesListView.setClickable(false);
            expensesListView.setVisibility(View.GONE);
        }
        else
        {
            noExpensesText.setVisibility(View.GONE);
            sortByText.setVisibility(View.VISIBLE);

            monthSpinner.setEnabled(true);
            monthSpinner.setClickable(true);
            monthSpinner.setVisibility(View.VISIBLE);
            String[] recordedMonths = getRecordedMonths();
            ArrayAdapter<String> msAdapter = new ArrayAdapter<String>(this,
                    android.R.layout.simple_spinner_item, recordedMonths);
            msAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            monthSpinner.setAdapter(msAdapter);
            setMonthSpinnerListener();

            sortSpinner.setEnabled(true);
            sortSpinner.setClickable(true);
            sortSpinner.setVisibility(View.VISIBLE);
            ArrayAdapter ssAdapter = ArrayAdapter.createFromResource(this,
                    R.array.sort_array, android.R.layout.simple_spinner_item);
            ssAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            sortSpinner.setAdapter(ssAdapter);
            setSortSpinnerListener();

            expensesListView.setEnabled(true);
            expensesListView.setClickable(true);
            expensesListView.setVisibility(View.VISIBLE);

//            if(budgetToDisplay == null)
//            {
                int index = 0;
                Budget budget = MFBlogic.getInstance().getBudgets().get(index);
                while (budget.getExpenses().isEmpty())
                {
                    index++;
                    budget = MFBlogic.getInstance().getBudgets().get(index);
                }
                generateBTD(budget);
//            }

            setListViewListener();

            updateMyExpenses();
        }
    }

    private void updateMyExpenses()
    {
        ListAdapter adapter = new ListAdapter(getApplicationContext(),
                R.layout.my_expenses_item, getExpenseListItems(budgetToDisplay));
        expensesListView.setAdapter(adapter);
    }


    public static void generateBTD(Budget budget)
    {
        if (budget instanceof WeekBudget)
        {
            WeekBudget wb = (WeekBudget) budget;
            budgetToDisplay = new MonthBudget(wb.getMonth(), 1.0);
            double amount = 0.0;
            int count = 1;
            for (Budget b : MFBlogic.getInstance().getBudgets())
            {
                if (b instanceof WeekBudget && b.getMonth() == wb.getMonth())
                {
                    amount += b.getBudgetAmount();
                    if (!b.equals(wb))
                        count++;
                    for (Expense e : b.getExpenses())
                    {
                        budgetToDisplay.addExpense(e);
                    }
                }
            }
            budgetToDisplay.setBudgetAmount(amount / count);
        }
        else
            budgetToDisplay = budget;
    }

    private class ListAdapter extends ArrayAdapter<ExpenseListItem> {

        private ListAdapter(Context context, int resource, List<ExpenseListItem> items) {
            super(context, resource, items);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View v = convertView;
            if (v == null) {
                LayoutInflater vi;
                vi = LayoutInflater.from(getContext());
                v = vi.inflate(R.layout.my_expenses_item, null);
            }

            ExpenseListItem p = getItem(position);
            if (p != null) {
                ImageView listThumbnail = (ImageView) v.findViewById(R.id.listThumbnail);
                //TextView listDate = (TextView) v.findViewById(R.id.listDate);
                TextView listDate2 = (TextView) v.findViewById(R.id.listDate2);
                TextView listDate3 = (TextView) v.findViewById(R.id.listDate3);
                TextView tt3 = (TextView) v.findViewById(R.id.listAmount);
                TextView tt4 = (TextView) v.findViewById(R.id.listTypeText);

                if(p.expense.getPhotoPath().equals(Expense.BLANK_PHOTO_PATH))
                    listThumbnail.setImageBitmap(iconBitmap);
                else
                    listThumbnail.setImageBitmap(p.thumbnail);
                //listDate.setText(p.dayOfWeek);
                listDate2.setText(p.date);
                listDate3.setText(p.time);
                tt3.setText(p.amount);
                if (p.isRestaurant)
                    tt4.setText(R.string.restaurant);
                else
                    tt4.setText(R.string.grocery);
            }
            return v;
        }
    }

    private void setSortSpinnerListener()
    {
        sortSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                if (position == 0)
                    sortByDate();
                else
                    sortByAmount();
                updateMyExpenses();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parentView) {}
        });
    }

    private void setMonthSpinnerListener()
    {
        monthSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                generateBTD(MFBlogic.getInstance().getBudgets().get(position));
                updateMyExpenses();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parentView) {}
        });
    }

    private List<ExpenseListItem> getExpenseListItems(Budget budget)
    {
        List<ExpenseListItem> expenseListItems = new ArrayList<ExpenseListItem>();
        if (sortByDate)
            budget.sortByDate();
        else
            budget.sortByAmount();
        for (Expense e : budget.getExpenses()) {
            expenseListItems.add(new ExpenseListItem(e.getImageBitmap(100, 100),
                    e.getDayOfWeekString(), e.getDateString(), e.getTimeString(),
                    e.getAmountString(), e.isRestaurant(), e));
        }
        return expenseListItems;
    }

    private void setListViewListener()
    {
        expensesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

                ExpenseListItem tempItem = (ExpenseListItem) parent.getItemAtPosition(position);
                Intent i = new Intent(view.getContext(), ExpenseDetailsActivity.class);
                i.putExtra("thisExpense", tempItem.expense);
                i.putExtra("thisBudget", budgetToDisplay);
                startActivity(i);
            }
        });
    }

    public static String[] getRecordedMonths()
    {
        ArrayList<Budget> uniqueBudgetList = new ArrayList<Budget>();
        boolean isUnique = true;
        for (Budget b : MFBlogic.getInstance().getBudgets())
        {
            if (!b.getExpenses().isEmpty())
            {
                for (Budget u : uniqueBudgetList)
                {
                    if (b.getMonth() == u.getMonth() && b.getYear() == u.getYear())
                    {
                        isUnique = false;
                        break;
                    }
                }
                if (isUnique)
                    uniqueBudgetList.add(b);
            }
            isUnique = true;
        }
        String[] recordedMonths = new String[uniqueBudgetList.size()];
        for (int i = 0; i < uniqueBudgetList.size(); i++)
        {
            String month = new DateFormatSymbols().getMonths()[uniqueBudgetList.get(i).getMonth()];
            recordedMonths[i] = "" + month + " " + uniqueBudgetList.get(i).getYear();
        }
        return recordedMonths;
    }

    private class ExpenseListItem {
        Bitmap thumbnail;
        String dayOfWeek;
        String date;
        String time;
        String amount;
        boolean isRestaurant;
        Expense expense;

        private ExpenseListItem(Bitmap b, String day, String d, String t, String a, boolean iR, Expense e) {
            thumbnail = b;
            dayOfWeek = day;
            date = d;
            time = t;
            amount = a;
            isRestaurant = iR;
            expense = e;
        }
    }

    private void sortByDate() { sortByDate = true; }

    private void sortByAmount() { sortByDate = false; }
}
