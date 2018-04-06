package spencer.myfoodbudget;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.io.IOException;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class NewExpenseActivity extends AppCompatActivity {

    static final int REQUEST_TAKE_PHOTO = 1;
    Expense expense;
    EditText addNoteText;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_expense);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent i = getIntent();
        expense = (Expense) i.getSerializableExtra("newExpense");

        addNoteText = (EditText) findViewById(R.id.addNoteText);

        ImageView thumbnail = (ImageView) findViewById(R.id.newExpenseThumbnail);
        thumbnail.setImageBitmap(expense.getImageBitmap(100, 100));

        TextView amountReview = (TextView) findViewById(R.id.reviewAmountText);
        amountReview.setText(NumberFormat.getCurrencyInstance().format(expense.getAmount()));

        TextView typeReview = (TextView) findViewById(R.id.typeReviewText);
        if(expense.isRestaurant())
            typeReview.setText("Restaurant");
        else
            typeReview.setText("Grocery");

        final Button acceptButton = (Button) findViewById(R.id.confirmExpenseButton);
        acceptButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                expense.setNote(addNoteText.getText().toString());
                MFBlogic.getInstance().getCurrentBudget().addExpense(expense, getApplicationContext());
                //MFBlogic.getInstance().writeSaveData(getApplicationContext());
                finish();
            }
        });

        final Button pictureButton = (Button) findViewById(R.id.addPhotoButton);
        pictureButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //Intent to start camera
                dispatchTakePictureIntent();
            }
        });

//        final Button addNoteButton = (Button) findViewById(R.id.addNoteButton);
//        addNoteButton.setOnClickListener(new View.OnClickListener() {
//            public void onClick(View v) {
//                final EditText addNoteText = (EditText) findViewById(R.id.addNoteText);
//                expense.setNote(addNoteText.getText().toString());
//            }
//        });
    }


    // https://developer.android.com/training/camera/photobasics.html
    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        //File storageDir = getFilesDir();
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        expense.setPhotoPath(image.getAbsolutePath());
        return image;
    }


    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.example.android.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_TAKE_PHOTO && resultCode == RESULT_OK) {
            Bitmap tempBits = getBitmapFromPhoto();
            if(tempBits != null) {
                Bitmap imageBitmap = tempBits;
                ImageView thumbnail = (ImageView) findViewById(R.id.newExpenseThumbnail);
                thumbnail.setImageBitmap(imageBitmap);
            }
        }
    }

    private Bitmap getBitmapFromPhoto() {
        // Get the dimensions of the View
        int targetW = 100;
        int targetH = 100;

        // Get the dimensions of the bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;

        if(expense.getPhotoPath().equals(Expense.BLANK_PHOTO_PATH)){
            return null;
        }

        BitmapFactory.decodeFile(expense.getPhotoPath(), bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        // Determine how much to scale down the image
        int scaleFactor = Math.min(photoW/targetW, photoH/targetH);

        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;

        return BitmapFactory.decodeFile(expense.getPhotoPath(), bmOptions);
    }

}