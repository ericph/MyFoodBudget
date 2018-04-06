package spencer.myfoodbudget;

import android.app.Dialog;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class ExpenseDetailsActivity extends AppCompatActivity {

    private static Expense expense;
    private ArrayList<View> displayViews;
    private ArrayList<View> editViews;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expense_details);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        displayViews = new ArrayList<View>();
        editViews = new ArrayList<View>();

        Intent i = getIntent();
        Expense temp = (Expense) i.getSerializableExtra("thisExpense");
        expense = new Expense(temp.isRestaurant(), temp.getCalendar().getTime(), temp.getAmount(), temp.getNote());
        expense.setPhotoPath(temp.getPhotoPath());

        TextView dateText = (TextView) findViewById(R.id.detailsDateText);
        dateText.setText(expense.getFullDateString());

        ImageView thumbnailImage = (ImageView) findViewById(R.id.detailsThumbnailImage);
        thumbnailImage.setImageBitmap(expense.getImageBitmap(150, 150));
        thumbnailImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(view.getContext(), FullscreenPictureActivity.class);
                i.putExtra("expense", expense);
                startActivity(i);
            }
        });

        Button deleteButton = (Button) findViewById(R.id.detailDeleteButton);
        deleteButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                FragmentManager fm = getFragmentManager();
                DeleteExpenseDialogFragment dedf = new DeleteExpenseDialogFragment();
                dedf.show(fm, "DEDF");
            }
        });

        TextView amountText = (TextView) findViewById(R.id.detailsAmountText);
        TextView typeText = (TextView) findViewById(R.id.detailsTypeText);
        TextView noteText = (TextView) findViewById(R.id.detailsNoteText);
        Button editButton = (Button) findViewById(R.id.detailEditButton);
        EditText editAmountText = (EditText) findViewById(R.id.detailsEditAmountText);
        Spinner editTypeSpinner = (Spinner) findViewById(R.id.detailsEditTypeSpinner);
        EditText editNoteText = (EditText) findViewById(R.id.detailsEditNoteText);
        Button confirmButton = (Button) findViewById(R.id.detailConfirmButton);

        displayViews.add(amountText);
        displayViews.add(typeText);
        displayViews.add(noteText);
        displayViews.add(deleteButton);
        displayViews.add(editButton);
        editViews.add(editAmountText);
        editViews.add(editTypeSpinner);
        editViews.add(editNoteText);
        editViews.add(confirmButton);

        updateDetails();

        editButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                editDetails();
            }
        });

        confirmButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                EditText editAmountText = (EditText) findViewById(R.id.detailsEditAmountText);
                Spinner editTypeSpinner = (Spinner) findViewById(R.id.detailsEditTypeSpinner);
                EditText editNoteText = (EditText) findViewById(R.id.detailsEditNoteText);
                if (MainActivity.isValidAmount(editAmountText, getApplicationContext()))
                {
                    expense.setAmount(Double.parseDouble(editAmountText.getText().toString()));
                    if (((String) editTypeSpinner.getSelectedItem()).equals("Restaurant"))
                        expense.setType(true);
                    else
                        expense.setType(false);
                    expense.setNote(editNoteText.getText().toString());
                    MyExpensesActivity.budgetToDisplay.editExpense(expense, getApplicationContext());
                    updateDetails();
                    displayDetails();
                }
            }
        });

        displayDetails();
    }

    private void updateDetails()
    {
        TextView amountText = (TextView) findViewById(R.id.detailsAmountText);
        amountText.setText(expense.getAmountString());

        TextView typeText = (TextView) findViewById(R.id.detailsTypeText);
        if(expense.isRestaurant())
            typeText.setText("Restaurant");
        else
            typeText.setText("Grocery");

        TextView noteText = (TextView) findViewById(R.id.detailsNoteText);
        noteText.setText(expense.getNote());

        EditText editAmountText = (EditText) findViewById(R.id.detailsEditAmountText);
        editAmountText.setText(expense.getAmountString().substring(1));

        Spinner editTypeSpinner = (Spinner) findViewById(R.id.detailsEditTypeSpinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.expense_type_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        editTypeSpinner.setAdapter(adapter);
        if (!expense.isRestaurant())
            editTypeSpinner.setSelection(1);

        EditText editNoteText = (EditText) findViewById(R.id.detailsEditNoteText);
        editNoteText.setHint("Enter a note");
        if (!expense.getNote().equals(""))
            editNoteText.setText(expense.getNote());
    }

    private void displayDetails()
    {
        for (View v : displayViews)
        {
            v.setVisibility(View.VISIBLE);
            v.setClickable(true);
        }
        for (View v : editViews)
        {
            v.setVisibility(View.GONE);
            v.setClickable(false);
        }
    }

    private void editDetails()
    {
        for (View v : displayViews)
        {
            v.setVisibility(View.GONE);
            v.setClickable(false);
        }
        for (View v : editViews)
        {
            v.setVisibility(View.VISIBLE);
            v.setClickable(true);
        }
    }

    public static class DeleteExpenseDialogFragment extends DialogFragment {
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle("Warning!");
            builder.setMessage("Are you sure you want to delete this expense?")
                    .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            MyExpensesActivity.budgetToDisplay.removeExpense(expense,
                                    getActivity().getApplicationContext());
                            Toast toast = Toast.makeText(getActivity().getApplicationContext(),
                                    "Expense deleted", Toast.LENGTH_SHORT);
                            toast.show();
                            getActivity().finish();
                        }
                    })
                    .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // User cancelled the dialog
                        }
                    });
            builder.setIcon(android.R.drawable.ic_dialog_alert);
            return builder.create();
        }
    }
}
