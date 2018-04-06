package spencer.myfoodbudget;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.widget.ImageView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.Serializable;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;


/**
 * Created by Spencer on 7/31/2017.
 */

public class Expense implements Serializable {

    //private static final Image DEFAULT_PICTURE = ;
    private static final String DEFAULT_NOTE = "";
    public static final String BLANK_PHOTO_PATH = "no photo";

    private boolean isRestaurant; // true for restaurant, false for groceries
    private Calendar calendar;
    private double amount;
    private String note;
    private String photoPath;

    // Constructor for Quick Entry Mode
    public Expense(boolean isRestaurant, double amount)
    {
        this.isRestaurant = isRestaurant;
        calendar = Calendar.getInstance();
        this.amount = amount;
        this.note = DEFAULT_NOTE;
        photoPath = BLANK_PHOTO_PATH;
    }

    public Expense(boolean isRestaurant, Date date, double amount, String note)
    {
        this.isRestaurant = isRestaurant;
        calendar = Calendar.getInstance();
        if (date != null)
            calendar.setTime(date);
        this.amount = amount;


        if (note == null)
            this.note = DEFAULT_NOTE;
        else
            this.note = note;

        photoPath = BLANK_PHOTO_PATH;
    }

    public Bitmap getImageBitmap(int width, int height){
        if(photoPath.equals(BLANK_PHOTO_PATH))
            return null;

        try {

            if(width < 0)
            {
                return BitmapFactory.decodeFile(photoPath);
            }
            else
            {
                // Get the dimensions of the View
                int targetW = width;
                int targetH = height;

                // Get the dimensions of the bitmap
                BitmapFactory.Options bmOptions = new BitmapFactory.Options();
                bmOptions.inJustDecodeBounds = true;
                BitmapFactory.decodeFile(photoPath, bmOptions);
                int photoW = bmOptions.outWidth;
                int photoH = bmOptions.outHeight;

                // Determine how much to scale down the image
                int scaleFactor = Math.min(photoW/targetW, photoH/targetH);

                // Decode the image file into a Bitmap sized to fill the View
                bmOptions.inJustDecodeBounds = false;
                bmOptions.inSampleSize = scaleFactor;
                bmOptions.inPurgeable = true;
                return BitmapFactory.decodeFile(photoPath, bmOptions);
            }


        }
        catch (Exception e)
        {
            e.printStackTrace();
            return null;
        }
    }


    //true for restaurant, false for grocery
    public boolean isRestaurant() {
        return isRestaurant;
    }

    public String getFullDateString() {
        SimpleDateFormat formatter = new SimpleDateFormat("EEE MMM dd yyyy hh:mma");
        //SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yy");
        return formatter.format(calendar.getTime());
        //return "test";
    }

    public String getDayOfWeekString() {
        SimpleDateFormat formatter = new SimpleDateFormat("EEE");
        //SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yy");
        return formatter.format(calendar.getTime());
    }

    public String getDateString() {
        SimpleDateFormat formatter = new SimpleDateFormat("MMM dd yyyy");
        //SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yy");
        return formatter.format(calendar.getTime());
    }

    public String getTimeString() {
        SimpleDateFormat formatter = new SimpleDateFormat("hh:mm a");
        //SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yy");
        return formatter.format(calendar.getTime());
    }

    public String getAmountString() {
        return NumberFormat.getCurrencyInstance().format(amount);
    }

    public int getYear() { return calendar.get(Calendar.YEAR); }

    public int getMonth() { return calendar.get(Calendar.MONTH); }

    public int getWeek() { return calendar.get(Calendar.WEEK_OF_MONTH); }

    public int getDay() { return calendar.get(Calendar.DAY_OF_WEEK); }

    public double getAmount() { return amount; }

    public void setDate(Date date) { calendar.setTime(date); }

    public void setAmount(double amount) { this.amount = amount; }

    public void setType(boolean isRestaurant) {this.isRestaurant = isRestaurant; }

    public void setNote(String note) { this.note = note; }

    public String getNote() { return note; }

    public void setPhotoPath(String path){
        photoPath = path;
    }

    public String getPhotoPath() {
        return photoPath;
    }

    public Calendar getCalendar(){
        return calendar;
    }

    @Override
    public boolean equals(Object obj)
    {
        if(obj instanceof  Expense && calendar.equals(((Expense) obj).getCalendar())){
            return true;
        }
        else
        {
            return false;
        }
    }
}