package spencer.myfoodbudget;

import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;

import java.io.File;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.List;

public class SettingsActivity extends AppCompatActivity {

    private static File storageDir;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);

        Switch quickEntrySwitch = (Switch) findViewById(R.id.quick_entry_switch);
        switchListener(quickEntrySwitch, "qe");

        Switch enableCameraSwitch = (Switch) findViewById(R.id.enable_camera_switch);
        //switchListener(enableCameraSwitch, "ec");
        enableCameraSwitch.setVisibility(View.GONE);

        Switch sampleDataSwitch = (Switch) findViewById(R.id.sample_data_switch);
        switchListener(sampleDataSwitch, "sd");

        setSwitches();

        Button deleteDataButton = (Button) findViewById(R.id.delete_data_button);
        deleteDataButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                FragmentManager fm = getFragmentManager();
                DeleteDataDialogFragment dddf = new DeleteDataDialogFragment();
                dddf.show(fm, "DDDF");
            }
        });
    }

    @Override
    public void onResume()
    {
        super.onResume();
        setSwitches();
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
                Intent intent3 = new Intent(this, BudgetSetupActivity.class);
                startActivity(intent3);
                return true;
            case R.id.nav_statistics:
                Intent intent4 = new Intent(this, StatisticsActivity.class);
                startActivity(intent4);
                return true;
            case R.id.nav_settings:
                // something
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void switchListener(Switch s, final String switchCode)
    {
        s.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                MFBlogic.getInstance().config.flipSwitch(switchCode);
                setSwitches();
                MFBlogic.getInstance().writeConfigData(getApplicationContext());
            }
        });
    }

    private void setSwitches()
    {
        Switch quickEntrySwitch = (Switch) findViewById(R.id.quick_entry_switch);
        quickEntrySwitch.setChecked(MFBlogic.getInstance().config.quickEntryOn());

        Switch enableCameraSwitch = (Switch) findViewById(R.id.enable_camera_switch);
        enableCameraSwitch.setChecked(MFBlogic.getInstance().config.enableCameraOn());

        Switch sampleDataSwitch = (Switch) findViewById(R.id.sample_data_switch);
        sampleDataSwitch.setChecked(MFBlogic.getInstance().config.useSampleDataOn());
        if (MFBlogic.getInstance().config.useSampleDataOn())
            MFBlogic.getInstance().useSampleData();
        else
            MFBlogic.getInstance().useSaveData();
    }

    public static class DeleteDataDialogFragment extends DialogFragment {
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle("Warning!");
            builder.setMessage("Are you sure you want to delete all budgets and all expense history?")
                    .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {

                            MFBlogic.getInstance().deleteSaveData(getActivity().getApplicationContext(), storageDir);
                            MFBlogic.getInstance().resetBudgets();
                            Toast toast = Toast.makeText(getActivity().getApplicationContext(),
                                    "Save data deleted", Toast.LENGTH_SHORT);
                            toast.show();
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
