package spencer.myfoodbudget;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.text.NumberFormat;
import java.util.Calendar;

public class BudgetSetupActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_budget_setup);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Spinner spinner = (Spinner) findViewById(R.id.budgetIntervalSpinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.budget_interval_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        TextView budgetNum = (TextView) findViewById(R.id.currentBudgetNumText);
        budgetNum.setText(NumberFormat.getCurrencyInstance().format(MFBlogic.getInstance().getCurrentBudget().getBudgetAmount()));

        final Button button = (Button) findViewById(R.id.applyBudgetButton);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                String amount_text = ((EditText) findViewById(R.id.newBudgetNumEdit)).getText().toString();
                double amount = -1.0;
                if (amount_text.equals(""))
                {
                    Toast toast = Toast.makeText(getApplicationContext(), "Enter budget amount before pressing button", Toast.LENGTH_SHORT);
                    toast.show();
                }
                else {
                    amount = Double.parseDouble(amount_text);

                    if (amount <= 0) {
                        Toast toast = Toast.makeText(getApplicationContext(), "Budget must be greater than 0!", Toast.LENGTH_SHORT);
                        toast.show();
                    } else if (amount > 999999999) {
                        Toast toast = Toast.makeText(getApplicationContext(), "Budget must be less than 1,000,000!", Toast.LENGTH_SHORT);
                        toast.show();
                    } else {
                        Calendar cal = Calendar.getInstance();
                        Budget budget;
                        TextView budgetIntervalText = (TextView) findViewById(R.id.budgetIntervalText);
                        Spinner spinner = (Spinner) findViewById(R.id.budgetIntervalSpinner);
                        String spinnerText = spinner.getSelectedItem().toString();
                        if (spinnerText.equals("Month")) {
                            budget = new MonthBudget(cal.get(Calendar.MONTH), amount);
                            budgetIntervalText.setText(R.string.per_month);
                        } else {
                            budget = new WeekBudget(cal.get(Calendar.MONTH), cal.get(Calendar.WEEK_OF_MONTH), amount);
                            budgetIntervalText.setText(R.string.per_week);
                        }
                        MFBlogic.getInstance().addBudget(budget);
                        MFBlogic.getInstance().writeSaveData(getApplicationContext());
                        TextView budgetNum = (TextView) findViewById(R.id.currentBudgetNumText);
                        budgetNum.setText(NumberFormat.getCurrencyInstance().format(budget.getBudgetAmount()));
                    }

                    finish();
                }
            }
        });

    }

    @Override
    public void onResume()
    {
        super.onResume();
        TextView budgetIntervalText = (TextView) findViewById(R.id.budgetIntervalText);
        if (MFBlogic.getInstance().getCurrentBudget() instanceof MonthBudget) {
            budgetIntervalText.setText(R.string.per_month);
        } else {
            budgetIntervalText.setText(R.string.per_week);
        }
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
                Intent intent2 = new Intent(this, MyExpensesActivity.class);
                startActivity(intent2);
                return true;
            case R.id.nav_setup_budget:
                // something
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

}
