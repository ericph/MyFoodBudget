package spencer.myfoodbudget;

import java.io.Serializable;

/**
 * Created by Eric on 8/1/2017.
 * Config class stores the values of different settings variables
 */

public class Config implements Serializable {

    private static boolean quickEntry;
    private static boolean enableCamera;
    private static boolean useSampleData;

    public Config()
    {
        quickEntry = false;
        enableCamera = true;
        useSampleData = false;
    }

    public static void flipSwitch(String switchCode)
    {
        switch (switchCode)
        {
            case "qe": quickEntry = !quickEntry;
                break;
            case "ec": enableCamera = !enableCamera;
                break;
            case "sd": useSampleData = !useSampleData;
                break;
            default: return;
        }
    }

    public static boolean quickEntryOn() { return quickEntry; }

    public static boolean enableCameraOn() { return enableCamera; }

    public static boolean useSampleDataOn() { return useSampleData; }
}

