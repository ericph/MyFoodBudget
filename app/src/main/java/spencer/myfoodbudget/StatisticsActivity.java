package spencer.myfoodbudget;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import java.text.DateFormatSymbols;
import java.text.NumberFormat;
import java.util.ArrayList;

public class StatisticsActivity extends AppCompatActivity {

    private StatsCalculator sc;
    private Spinner statsSpinner;
    private int mode; // 0 = all, 1 = rg, 2 = dow
    private boolean monthBudget; // true = month budget, false = week budget
    private ArrayList<TextView> allTexts;
    private ArrayList<TextView> rgTexts;
    private ArrayList<TextView> dowTexts;
    private ArrayList<String> daysOfTheWeek;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        sc = new StatsCalculator();
        statsSpinner = (Spinner) findViewById(R.id.statsSpinner);
        mode = 0;
        monthBudget = true;

        allTexts = new ArrayList<TextView>();
        rgTexts = new ArrayList<TextView>();
        dowTexts = new ArrayList<TextView>();
        daysOfTheWeek = new ArrayList<String>();
        daysOfTheWeek.add("Sundays: ");
        daysOfTheWeek.add("Mondays: ");
        daysOfTheWeek.add("Tuesdays: ");
        daysOfTheWeek.add("Wednesdays: ");
        daysOfTheWeek.add("Thursdays: ");
        daysOfTheWeek.add("Fridays: ");
        daysOfTheWeek.add("Saturdays: ");

        rgTexts.add((TextView) findViewById(R.id.statsText4));
        rgTexts.add((TextView) findViewById(R.id.statsText5));
        rgTexts.add((TextView) findViewById(R.id.statsText6));
        rgTexts.add((TextView) findViewById(R.id.statsText7));
        allTexts.add((TextView) findViewById(R.id.statsText0));
        allTexts.add((TextView) findViewById(R.id.statsText1));
        allTexts.add((TextView) findViewById(R.id.statsText2));
        allTexts.add((TextView) findViewById(R.id.statsText3));
        allTexts.addAll(rgTexts);
        dowTexts.add((TextView) findViewById(R.id.sunText));
        dowTexts.add((TextView) findViewById(R.id.monText));
        dowTexts.add((TextView) findViewById(R.id.tuesText));
        dowTexts.add((TextView) findViewById(R.id.wedText));
        dowTexts.add((TextView) findViewById(R.id.thursText));
        dowTexts.add((TextView) findViewById(R.id.friText));
        dowTexts.add((TextView) findViewById(R.id.satText));
        dowTexts.add((TextView) findViewById(R.id.statsTextB));
    }

    public void onResume()
    {
        super.onResume();
        makeStatistics();
    }

    private void makeStatistics()
    {
        String[] statsOptions = getStatsOptions();
        ArrayAdapter<String> ssAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, statsOptions);
        ssAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        statsSpinner.setAdapter(ssAdapter);
        setStatsSpinnerListener();

        if (monthBudget)
        {
            allTexts.get(1).setText(R.string.your_spending_month);
            allTexts.get(3).setText(R.string.remaining_month);
        }
        else
        {
            allTexts.get(1).setText(R.string.your_spending_week);
            allTexts.get(3).setText(R.string.remaining_week);
        }

        updateStatistics();
    }

    private void updateStatistics()
    {
        switch (mode)
        {
            case 0:
                double budgetDifference = sc.budgetDifference();
                double avgSpentPerDayTotal = sc.avgSpentPerDayTotal();
                double avgSpentPerWeekTotal = sc.avgSpentPerWeekTotal();
                double mostSpentDayTotal = sc.mostSpentDayTotal();
                double mostSpentWeekTotal = sc.mostSpentWeekTotal();
                if (budgetDifference >= 0)
                {
                    allTexts.get(0).setText("");
                    allTexts.get(2).setText(R.string.within_budget);
                }
                else
                {
                    allTexts.get(0).setText(R.string.uh_oh);
                    allTexts.get(2).setText(R.string.over_budget);
                }
                if (monthBudget)
                    allTexts.get(3).setText(getString(R.string.remaining_month) + NumberFormat.getCurrencyInstance().format(budgetDifference));
                else
                    allTexts.get(3).setText(getString(R.string.remaining_week) + NumberFormat.getCurrencyInstance().format(budgetDifference));
                allTexts.get(4).setText(getString(R.string.spent_per_day) + NumberFormat.getCurrencyInstance().format(avgSpentPerDayTotal));
                allTexts.get(5).setText(getString(R.string.spent_per_week) + NumberFormat.getCurrencyInstance().format(avgSpentPerWeekTotal));
                allTexts.get(6).setText(getString(R.string.most_spent_day) + NumberFormat.getCurrencyInstance().format(mostSpentDayTotal));
                allTexts.get(7).setText(getString(R.string.most_spent_week) + NumberFormat.getCurrencyInstance().format(mostSpentWeekTotal));
                for (TextView tv : allTexts)
                {
                    tv.setVisibility(View.VISIBLE);
                }
                for (TextView tv : dowTexts)
                {
                    tv.setVisibility(View.GONE);
                }
                break;
            case 1:
                double avgSpentPerDay = sc.avgSpentPerDay(MyExpensesActivity.budgetToDisplay);
                double avgSpentPerWeek = sc.avgSpentPerWeek(MyExpensesActivity.budgetToDisplay);
                double mostSpentDay = sc.mostSpentDay(MyExpensesActivity.budgetToDisplay);
                double mostSpentWeek = sc.mostSpentWeek(MyExpensesActivity.budgetToDisplay);
                rgTexts.get(0).setText(getString(R.string.spent_per_day) + NumberFormat.getCurrencyInstance().format(avgSpentPerDay));
                rgTexts.get(1).setText(getString(R.string.spent_per_week) + NumberFormat.getCurrencyInstance().format(avgSpentPerWeek));
                rgTexts.get(2).setText(getString(R.string.most_spent_day) + NumberFormat.getCurrencyInstance().format(mostSpentDay));
                rgTexts.get(3).setText(getString(R.string.most_spent_week) + NumberFormat.getCurrencyInstance().format(mostSpentWeek));
                for (TextView tv : allTexts)
                {
                    tv.setVisibility(View.GONE);
                }
                for (TextView tv : dowTexts)
                {
                    tv.setVisibility(View.GONE);
                }
                for (TextView tv : rgTexts)
                {
                    tv.setVisibility(View.VISIBLE);
                }
                break;
            case 2:
                for (TextView tv : allTexts)
                {
                    tv.setVisibility(View.GONE);
                }
                for (int i = 0; i < dowTexts.size() - 1; i++)
                {
                    dowTexts.get(i).setText(daysOfTheWeek.get(i) + NumberFormat.getCurrencyInstance().format(sc.avgSpentOn(i+1)));
                    dowTexts.get(i).setVisibility(View.VISIBLE);
                }
                dowTexts.get(7).setVisibility(View.VISIBLE);
                break;
            default: return;
        }
        allTexts.get(0).setVisibility(View.GONE);
        allTexts.get(1).setVisibility(View.GONE);
        allTexts.get(2).setVisibility(View.GONE);
        allTexts.get(3).setVisibility(View.GONE);
    }

    private void setStatsSpinnerListener()
    {
        statsSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                if (position == 0)
                    mode = 0;
                else if (position == 1)
                    mode = 2;
                else
                {
                    mode = 1;
                    MyExpensesActivity.generateBTD(MFBlogic.getInstance().getBudgets().get(position - 2));
                }
                updateStatistics();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parentView) {}
        });
    }

    private String[] getStatsOptions()
    {
        String[] recordedMonths = MyExpensesActivity.getRecordedMonths();
        String[] temp = new String[2];
        temp[0] = "All time stats";
        temp[1] = "Day of week stats";
        String[] statsOptions = new String[recordedMonths.length + 2];
        System.arraycopy(temp, 0, statsOptions, 0, 2);
        System.arraycopy(recordedMonths, 0, statsOptions, 2, recordedMonths.length);
        return statsOptions;
    }

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
                Intent intent2 = new Intent(this, MyExpensesActivity.class);
                startActivity(intent2);
                return true;
            case R.id.nav_setup_budget:
                Intent intent3 = new Intent(this, BudgetSetupActivity.class);
                startActivity(intent3);
                return true;
            case R.id.nav_statistics:
                // something
                return true;
            case R.id.nav_settings:
                Intent intent5 = new Intent(this, SettingsActivity.class);
                startActivity(intent5);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
