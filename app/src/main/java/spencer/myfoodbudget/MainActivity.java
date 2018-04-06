package spencer.myfoodbudget;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.text.NumberFormat;

public class MainActivity extends AppCompatActivity  {

    ColorStateList oldColors;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        MFBlogic.getInstance().readSaveData(getApplicationContext());
        MFBlogic.getInstance().readConfigData(getApplicationContext());

        Bitmap iconBitmap = getBitmapIcon();
        MFBlogic.getInstance().setupIcon(iconBitmap);

        TextView remainingMoneyText = (TextView) findViewById(R.id.remainingMoneyText);
        oldColors =  remainingMoneyText.getTextColors(); //save original colors
        remainingMoneyText.setText(NumberFormat.getCurrencyInstance().format(
                MFBlogic.getInstance().getCurrentBudget().getRemainingBudgetAmount()));

        TextView numDaysText = (TextView) findViewById(R.id.numDaysText);
        numDaysText.setText("" + MFBlogic.getInstance().getCurrentBudget().getRemainingDays());

        final Button rButton = (Button) findViewById(R.id.restaurantButton);
        setEntryButtonListener(rButton, true);

        final Button gButton = (Button) findViewById(R.id.groceryButton);
        setEntryButtonListener(gButton, false);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.nav_menu, menu);
        return true;
    }

    @Override
    public void onResume()
    {  // After a pause OR at startup
        super.onResume();

        EditText amountSpentText = (EditText) findViewById(R.id.amountSpentText);
        amountSpentText.setText("");

        TextView remainingMoneyText = (TextView) findViewById(R.id.remainingMoneyText);
        double remainingAmount = MFBlogic.getInstance().getCurrentBudget().getRemainingBudgetAmount();
        remainingMoneyText.setText(NumberFormat.getCurrencyInstance().format(remainingAmount));
        if (remainingAmount > 0)
            remainingMoneyText.setTextColor(oldColors);
        else
            remainingMoneyText.setTextColor(Color.parseColor("#F44336"));

        TextView numDaysText = (TextView) findViewById(R.id.numDaysText);
        numDaysText.setText("" + MFBlogic.getInstance().getCurrentBudget().getRemainingDays());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_home:
                // something
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

    private Bitmap getBitmapIcon() {
        // Get the dimensions of the View
        int targetW = 50;
        int targetH = 50;

        // Get the dimensions of the bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;


        BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        // Determine how much to scale down the image
        int scaleFactor = Math.min(photoW/targetW, photoH/targetH);

        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;

        return BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher, bmOptions);
    }

    private void setEntryButtonListener(Button b, final boolean isRestaurant)
    {
        b.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                EditText editText = (EditText) findViewById(R.id.amountSpentText);
                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
                if (isValidAmount(editText, getApplicationContext()))
                {
                    double amount = Double.parseDouble(editText.getText().toString());
                    editText.setText("");
                    if (MFBlogic.getInstance().config.quickEntryOn())
                    {
                        MFBlogic.getInstance().getCurrentBudget().addExpense(new Expense(isRestaurant, amount), getApplicationContext());
                        Toast toast = Toast.makeText(getApplicationContext(), "Expense recorded", Toast.LENGTH_SHORT);
                        toast.show();
                        MFBlogic.getInstance().writeSaveData(getApplicationContext());
                        TextView remainingMoneyText = (TextView) findViewById(R.id.remainingMoneyText);
                        double remainingAmount = MFBlogic.getInstance().getCurrentBudget().getRemainingBudgetAmount();
                        remainingMoneyText.setText(NumberFormat.getCurrencyInstance().format(remainingAmount));
                        if (remainingAmount > 0)
                            remainingMoneyText.setTextColor(oldColors);
                        else
                            remainingMoneyText.setTextColor(Color.parseColor("#F44336"));
                    }
                    else
                    {
                        Expense newExpense = new Expense(isRestaurant, amount);
                        Intent intent = new Intent(v.getContext(), NewExpenseActivity.class);
                        intent.putExtra("newExpense", newExpense);
                        startActivity(intent);
                    }
                }
            }
        });
    }

    public static boolean isValidAmount(EditText editText, Context context)
    {
        String amount_text = editText.getText().toString();
        if (amount_text.equals(""))
        {
            Toast toast = Toast.makeText(context, "Enter expense amount before pressing button", Toast.LENGTH_SHORT);
            toast.show();
            return false;
        }
        double amount = Double.parseDouble(amount_text);
        if (amount <= 0)
        {
            Toast toast = Toast.makeText(context, "Expense must be greater than 0!", Toast.LENGTH_SHORT);
            toast.show();
            return false;
        }
        if (amount > 999999999)
        {
            Toast toast = Toast.makeText(context, "Expense must be less than 1,000,000!", Toast.LENGTH_SHORT);
            toast.show();
            return false;
        }
        return true;
    }
}
