package com.example.project;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.telephony.CellIdentityGsm;
import android.telephony.CellIdentityLte;
import android.telephony.CellIdentityWcdma;
import android.telephony.CellInfo;
import android.telephony.CellInfoCdma;
import android.telephony.CellInfoGsm;
import android.telephony.CellInfoLte;
import android.telephony.CellInfoTdscdma;
import android.telephony.CellInfoWcdma;
import android.telephony.CellSignalStrengthCdma;
import android.telephony.CellSignalStrengthGsm;
import android.telephony.CellSignalStrengthLte;
import android.telephony.CellSignalStrengthTdscdma;
import android.telephony.CellSignalStrengthWcdma;
import android.telephony.PhoneStateListener;
import android.telephony.ServiceState;
import android.telephony.TelephonyManager;
import android.telephony.gsm.GsmCellLocation;
import android.view.Menu;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;


public class MainActivity extends Activity {
    //Initialize Progress Bar Signal Levels
    private static final int EXCELLENT = 75;
    private static final int GOOD = 50;
    private static final int MODERATE = 25;
    private static final int WEAK = 0;

    //Array index of each state we used
    private static final int INFO_SERVICE_STATE_INDEX = 0;
    private static final int INFO_CONNECTION_STATE_INDEX = 1;
    private static final int INFO_SIGNAL_LEVEL_INDEX = 2;
    private static final int INFO_SIGNAL_LEVEL_INFO_INDEX = 3;
    private static final int INFO_DEVICE_INFO_INDEX = 4;

    //Array of ID's of each info we need
    private int[] info_ids = {
            R.id.serviceState_info,
            R.id.connectionState_info,
            R.id.signalLevel,
            R.id.signalLevelInfo,
            R.id.device_info
    };

    final int READ_PHONE_STATE_CODE = 100;
    final int ACCESS_FINE_LOCATION_CODE = 101;
    final int ACCESS_COARSE_LOCATION_CODE = 102;


    @RequiresApi(api = Build.VERSION_CODES.R)
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){ActivityCompat.requestPermissions(MainActivity.this, new String[] { Manifest.permission.ACCESS_FINE_LOCATION }, ACCESS_FINE_LOCATION_CODE);}
        //if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){ActivityCompat.requestPermissions(MainActivity.this, new String[] {Manifest.permission.ACCESS_COARSE_LOCATION }, ACCESS_COARSE_LOCATION_CODE);}
       // if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED){ActivityCompat.requestPermissions(MainActivity.this, new String[] { Manifest.permission.READ_PHONE_STATE }, READ_PHONE_STATE_CODE);}

        startSignalLevelListener(); //When application starts we need to display the Signal Level bar by starting it
        displayTelephonyInfo(); //Display all of the phone information we need (Signal Strength, Cell ID, etc...)
        try {
            Database();
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }


    @Override
    protected void onStop() {
        super.onStop();
        //Stop listening to the telephony events
        StopListener();

    }

    @Override
    protected void onPause() {
        super.onPause();
        //Stop listening to the telephony events
        StopListener();
    }

    @Override
    protected void onResume() {
        super.onResume();
        //subscribes to the telephony related events
        startSignalLevelListener();
    }

    //set Text view contents
    private void setTextViewText(int id, String text) {
        ((TextView) findViewById(id)).setText(text);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }

    //Listen to the subscriber events to set the Signal Level Progress bar, Register to the subscriber events
    private void startSignalLevelListener() {

        TelephonyManager tm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
        @SuppressWarnings("deprecation")
        int events = PhoneStateListener.LISTEN_SIGNAL_STRENGTH |
                PhoneStateListener.LISTEN_DATA_ACTIVITY |
                PhoneStateListener.LISTEN_DATA_CONNECTION_STATE |
                PhoneStateListener.LISTEN_SERVICE_STATE;

        tm.listen(phoneListener, events);
    }


    //Stop listening to the Subscriber events, De-register from subscriber events
    private void StopListener() {
        TelephonyManager tm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
        tm.listen(phoneListener, PhoneStateListener.LISTEN_NONE);
    }


    /*
    * tried to do permission at runtime but didn't work properly
    * */

    //display all of the network information we choose to display
    @RequiresApi(api = Build.VERSION_CODES.R)
    private void displayTelephonyInfo() {

        //access to the telephony services
        TelephonyManager tm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);

        //access to the gsm info ,..requires ACCESS_FINE_LOCATION and ACCESS_COARSE_LOCATION permission
        if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, ACCESS_FINE_LOCATION_CODE);
        }
        else{
        GsmCellLocation gsmLoc = (GsmCellLocation) tm.getCellLocation();

        //Get Device ID / IMEI
        //String deviceid = tm.getDeviceId();
        //Get the name of current registered operator.
        String operatorname = tm.getNetworkOperatorName();
        //Get  the ISO country code equivalent for the SIM provider's country code. (i.e. lb, us, uk)
        String simcountrycode = tm.getSimCountryIso();
        //Get  the Service Provider Name.
        String simoperator = tm.getSimOperatorName();
        //Get  the serial number of the SIM, if applicable. Return null if it is unavailable.
        //String simserialno = tm.getSimSerialNumber();
        //Get  the unique subscriber ID, the IMSI for a GSM phone
        //String subscriberid = tm.getSubscriberId();
        //Get the type indicating the radio technology (network type) currently in use on the device for data transmission.
        //EDGE,GPRS,UMTS, LTE  etc
        String networktype = getNetworkTypeString(tm.getNetworkType());
        //indicating the device phone type. This indicates the type of radio used to transmit voice calls
        //GSM,CDMA etc
        String phonetype = getPhoneTypeString(tm.getPhoneType());
        //Get the cell ID, BTS ID, which is the Base SStation ID connected to
        int cid = gsmLoc.getCid();
        String cellID = String.valueOf(cid);
        //Get current Time
        Date currentTime = Calendar.getInstance().getTime();
        //List of all of the cellular info that is needed (Rsrsp, ss, rsrq, etc...)
        List<CellInfo> cellInfos = tm.getAllCellInfo();

        //print inside the textview called device info
        String deviceinfo = "";

        /*
        * the methods whose commented are because they are not important, and cause issues in API>29 as
        * they are no longer supported by new APIs
        * */

        //deviceinfo += ("IMEI/Device ID: " + deviceid + "\n" + "\n");
        deviceinfo += ("Operator Name: " + operatorname + "\n" + "\n");
        deviceinfo += ("SIM Country Code: " + simcountrycode + "\n" + "\n");
        deviceinfo += ("SIM Operator: " + simoperator + "\n" + "\n");
        //deviceinfo += ("SIM Serial No.: " + simserialno + "\n" + "\n");
        //deviceinfo += ("Subscriber ID: " + subscriberid + "\n" + "\n");
        deviceinfo += ("Network Type: " + networktype + "\n" + "\n");
        deviceinfo += ("Phone Type: " + phonetype + "\n" + "\n");
        deviceinfo += ("Timestamp: " + currentTime + "\n" + "\n");
        deviceinfo += ("RSRP/Signal Strength: " + Dbm() + " dBm   " + Asu() + " asu   " + "\n" + "\n");
        deviceinfo += ("RSRQ: " + getRsrq() + "    " + "RSSNR/SINR: " + getRssnr() + "\n" + "\n");
        deviceinfo += ("Cell ID: " + cellID + "\n" + "\n");
        //deviceinfo += ("Cell Info: " + cellInfos + "\n" + "\n");
        try {
            deviceinfo += ("Cell Bands: " + getBand() + "\n" + "\n");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        setTextViewText(info_ids[INFO_DEVICE_INFO_INDEX], deviceinfo);

        //refresh the textview every 1 second for real time information querying
        final Handler handler = new Handler();
        Timer timer = new Timer();
        TimerTask doTask = new TimerTask() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    @SuppressWarnings("unchecked")
                    public void run() {
                        try {
                            Intent intent = getIntent();

                            displayTelephonyInfo();
                        } catch (Exception e) {
                            // TODO Auto-generated catch block
                        }
                    }
                });
            }
        };
        timer.schedule(doTask, 1000);
    }}

    //Method to obtain Network's RSRQ which is the Signal Receive Quality
    //it is the ration of RSRP and RSSI
    @RequiresApi(api = Build.VERSION_CODES.O)
    private String getRsrq() {
        TelephonyManager tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, ACCESS_FINE_LOCATION_CODE);
        }
        else{
        List<CellInfo> cellInfoList = tm.getAllCellInfo();
        String Rsrq;
        for (CellInfo cellInfo : cellInfoList) {
            if (cellInfo instanceof CellInfoLte) {
                Rsrq = String.valueOf(((CellInfoLte) cellInfo).getCellSignalStrength().getRsrq());
                return Rsrq;
            }
        }}
        return null;
    }

    //Get the RSSNR which is the Signal to Noise Ratio of the Network
    @RequiresApi(api = Build.VERSION_CODES.O)
    private String getRssnr() {
        TelephonyManager tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, ACCESS_FINE_LOCATION_CODE);
        }
        else{
        List<CellInfo> cellInfoList = tm.getAllCellInfo();
        String Rssnr;
        for (CellInfo cellInfo : cellInfoList) {
            if (cellInfo instanceof CellInfoLte) {
                Rssnr = String.valueOf(((CellInfoLte) cellInfo).getCellSignalStrength().getRssnr());
                return Rssnr;
            } else if (cellInfo instanceof CellInfoCdma) {
                Rssnr = String.valueOf(((CellInfoCdma) cellInfo).getCellSignalStrength().getEvdoSnr());
                return Rssnr;
            }
        }}
        return null;
    }

    //Get the Signal Strength in dBm
    @RequiresApi(api = Build.VERSION_CODES.Q)
    private String Dbm() {
        TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, ACCESS_FINE_LOCATION_CODE);
        }
        else{
        TelephonyManager tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);

        for (CellInfo info : tm.getAllCellInfo()) {
            String gsmStrength;
            if (info instanceof CellInfoGsm) {
                CellSignalStrengthGsm gsm = ((CellInfoGsm) info).getCellSignalStrength();
                gsmStrength = String.valueOf(gsm.getDbm());
            } else if (info instanceof CellInfoCdma) {
                CellSignalStrengthCdma cdma = ((CellInfoCdma) info).getCellSignalStrength();
                gsmStrength = String.valueOf(cdma.getDbm());
            } else if (info instanceof CellInfoLte) {
                CellSignalStrengthLte lte = ((CellInfoLte) info).getCellSignalStrength();
                gsmStrength = String.valueOf(lte.getDbm());
            } else if (info instanceof CellInfoWcdma) {
                CellSignalStrengthWcdma Wcdma = ((CellInfoWcdma) info).getCellSignalStrength();
                gsmStrength = String.valueOf(((CellSignalStrengthWcdma) Wcdma).getDbm());
            } else if (info instanceof CellInfoTdscdma) {
                CellSignalStrengthTdscdma tdscdma = ((CellInfoTdscdma) info).getCellSignalStrength();
                gsmStrength = String.valueOf(tdscdma.getDbm());
            } else {
                gsmStrength = String.valueOf("Unknown");
            }
            return gsmStrength;
        }}
        return null;
    }

    //Get the asu of the Network, which is the Arbitrary Strength Unit
    @RequiresApi(api = Build.VERSION_CODES.Q)
    private String Asu() {
        TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, ACCESS_FINE_LOCATION_CODE);
        }
        else{
        TelephonyManager tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);

        for (CellInfo info : tm.getAllCellInfo()) {
            String AsuStrength;
            if (info instanceof CellInfoGsm) {
                CellSignalStrengthGsm gsm = ((CellInfoGsm) info).getCellSignalStrength();
                AsuStrength = String.valueOf(gsm.getAsuLevel());
            } else if (info instanceof CellInfoCdma) {
                CellSignalStrengthCdma cdma = ((CellInfoCdma) info).getCellSignalStrength();
                AsuStrength = String.valueOf(cdma.getAsuLevel());
            } else if (info instanceof CellInfoLte) {
                CellSignalStrengthLte lte = ((CellInfoLte) info).getCellSignalStrength();
                AsuStrength = String.valueOf(lte.getAsuLevel());
            } else if (info instanceof CellInfoWcdma) {
                CellSignalStrengthWcdma Wcdma = ((CellInfoWcdma) info).getCellSignalStrength();
                AsuStrength = String.valueOf(((CellSignalStrengthWcdma) Wcdma).getAsuLevel());
            } else if (info instanceof CellInfoTdscdma) {
                CellSignalStrengthTdscdma tdscdma = ((CellInfoTdscdma) info).getCellSignalStrength();
                AsuStrength = String.valueOf(tdscdma.getAsuLevel());
            } else {
                AsuStrength = String.valueOf("Unknown");
            }
            return AsuStrength;
        }}
        return null;
    }


    //Get the Bands and Bandwidth of the Network (only works in API 30)
    /*
    * Added if conditions in case the API does not meet the requirements, so instead of crashing
    * it will just not execute
    * */

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    private JSONArray getBand() throws JSONException {
        TelephonyManager tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);

        JSONArray cellList = new JSONArray();
        if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, ACCESS_FINE_LOCATION_CODE);
            }
        }
        else{
        List<CellInfo> cellInfos = tm.getAllCellInfo();
        for (int i = 0; i < cellInfos.size(); i++) {
            JSONObject cellObj = new JSONObject();
            CellInfo info = cellInfos.get(i);
            if (info instanceof CellInfoLte) {
                CellSignalStrengthLte lte = ((CellInfoLte) info).getCellSignalStrength();
                CellIdentityLte identityLte = ((CellInfoLte) info).getCellIdentity();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {cellObj.put("Band: ", identityLte.getBands());}
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {cellObj.put("Bandwidth: ", identityLte.getBandwidth());}
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {cellObj.put("DL Frequency: ", identityLte.getEarfcn());}
                cellList.put(cellObj);
            } else if (info instanceof CellInfoGsm) {
                CellSignalStrengthGsm gsm = ((CellInfoGsm) info).getCellSignalStrength();
                CellIdentityGsm identityGsm = ((CellInfoGsm) info).getCellIdentity();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {cellObj.put("Plmns: ", identityGsm.getAdditionalPlmns());}
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {cellObj.put("Arfcn: ", identityGsm.getArfcn());}
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {cellObj.put("BSIC: ", identityGsm.getBsic());}
                cellList.put(cellObj);
            } else if (info instanceof CellInfoWcdma) {
                CellSignalStrengthWcdma wcdma = ((CellInfoWcdma) info).getCellSignalStrength();
                CellIdentityWcdma identityWcdma = ((CellInfoWcdma) info).getCellIdentity();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {cellObj.put("Plmns: ", identityWcdma.getAdditionalPlmns());}
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {cellObj.put("UARFCN/Radio Frequency Channel Number (MHz) ", identityWcdma.getUarfcn());}
                cellList.put(cellObj);
            }
        }}
        return cellList;
    }

    //Calculate the Signal level to compare and display on the Progress Bar
    private void setSignalLevel(int Sigid, int SigInfoid, int level) {

        int progress = (int) ((((float) level) / 31.0) * 100);

        String signalLevelString = getSignalLevelString(progress);

        //set the status
        ((ProgressBar) findViewById(Sigid)).setProgress(progress);

        //set the status string
        ((TextView) findViewById(SigInfoid)).setText(signalLevelString);

    }

    //Displays whether the Signal is Strong or weak, etc... depending on the level
    private String getSignalLevelString(int level) {

        String signalLevelString = "Weak";

        if (level > EXCELLENT) signalLevelString = "Excellent";
        else if (level > GOOD) signalLevelString = "Good";
        else if (level > MODERATE) signalLevelString = "Moderate";
        else if (level > WEAK) signalLevelString = "Weak";

        return signalLevelString;
    }

    //Get the Network Type in String form depending on the type received (EDGE, UMTS, LTE, etc...)
    private String getNetworkTypeString(int type) {
        String typeString = "Unknown";

        switch (type) {
            case TelephonyManager.NETWORK_TYPE_EDGE:
                typeString = "EDGE";
                break;
            case TelephonyManager.NETWORK_TYPE_GPRS:
                typeString = "GPRS";
                break;
            case TelephonyManager.NETWORK_TYPE_UMTS:
                typeString = "UMTS";
                break;
            case TelephonyManager.NETWORK_TYPE_GSM:
                typeString = "GSM";
                break;
            case TelephonyManager.NETWORK_TYPE_LTE:
                typeString = "LTE";
                break;
            case TelephonyManager.NETWORK_TYPE_HSPA:
                typeString = "HSPA";
                break;
            case TelephonyManager.NETWORK_TYPE_CDMA:
                typeString = "CDMA";
                break;
            case TelephonyManager.NETWORK_TYPE_EHRPD:
                typeString = "EHRDP";
                break;
            case TelephonyManager.NETWORK_TYPE_EVDO_0:
                typeString = "EVDO_0";
                break;
            case TelephonyManager.NETWORK_TYPE_EVDO_A:
                typeString = "EVDO_A";
                break;
            case TelephonyManager.NETWORK_TYPE_EVDO_B:
                typeString = "EVDO_B";
                break;
            case TelephonyManager.NETWORK_TYPE_HSDPA:
                typeString = "HSDPA";
                break;
            case TelephonyManager.NETWORK_TYPE_HSPAP:
                typeString = "HSPAP";
                break;
            case TelephonyManager.NETWORK_TYPE_HSUPA:
                typeString = "HSUPA";
                break;

            default:
                typeString = "UNKNOWN";
                break;
        }

        return typeString;
    }

    //Get the Phone type whether the device is a GSM, CDMA, SIP type
    private String getPhoneTypeString(int type) {
        String typeString = "Unknown";

        switch (type) {
            case TelephonyManager.PHONE_TYPE_GSM:
                typeString = "GSM";
                break;
            case TelephonyManager.PHONE_TYPE_CDMA:
                typeString = "CDMA";
                break;
            case TelephonyManager.PHONE_TYPE_SIP:
                typeString = "SIP";
                break;
            case TelephonyManager.PHONE_TYPE_NONE:
                typeString = "UNKNOWN";
                break;
            default:
                typeString = "UNKNOWN";
                break;
        }

        return typeString;
    }


    private final PhoneStateListener phoneListener = new PhoneStateListener() {

        //Display the phone connection state if it is connected to a network, or disconnected
        public void onDataConnectionStateChanged(int state) {

            String phoneState = "UNKNOWN";

            switch (state) {

                case TelephonyManager.DATA_CONNECTED:
                    phoneState = "Connected";
                    break;
                case TelephonyManager.DATA_CONNECTING:
                    phoneState = "Connecting..";
                    break;
                case TelephonyManager.DATA_DISCONNECTED:
                    phoneState = "Disconnected";
                    break;
                case TelephonyManager.DATA_SUSPENDED:
                    phoneState = "Suspended";
                    break;
            }

            setTextViewText(info_ids[INFO_CONNECTION_STATE_INDEX], phoneState);

            super.onDataConnectionStateChanged(state);
        }


        //Display the Phone's Service Status if it's in Emergency mode, In Service, or Out of Service
        public void onServiceStateChanged(ServiceState serviceState) {

            String strServiceState = "NONE";

            switch (serviceState.getState()) {

                case ServiceState.STATE_EMERGENCY_ONLY:
                    strServiceState = "Emergency";
                    break;

                case ServiceState.STATE_IN_SERVICE:
                    strServiceState = "In Service";
                    break;
                case ServiceState.STATE_OUT_OF_SERVICE:
                    strServiceState = "Out of Service";
                    break;
                case ServiceState.STATE_POWER_OFF:
                    strServiceState = "Power off";
                    break;
            }

            setTextViewText(info_ids[INFO_SERVICE_STATE_INDEX], strServiceState);

            super.onServiceStateChanged(serviceState);

        }

        //Display when Signal Strength changes based on Strength Level, and if it is Excellent, Good, etc...
        public void onSignalStrengthChanged(int asu) {

            setSignalLevel(info_ids[INFO_SIGNAL_LEVEL_INDEX], info_ids[INFO_SIGNAL_LEVEL_INFO_INDEX], asu);

            super.onSignalStrengthChanged(asu);
        }

    };


    /*
    * Database method to run the database to create table and add entries every 60 seconds
    * */
    @RequiresApi(api = Build.VERSION_CODES.Q)
    public void Database() throws JSONException {
        TelephonyManager tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, ACCESS_FINE_LOCATION_CODE);
        }
        else{
        GsmCellLocation gsmLoc = (GsmCellLocation) tm.getCellLocation();
        DatabaseHelper dh = new DatabaseHelper(this);
        if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_PHONE_STATE}, READ_PHONE_STATE_CODE);
        }
        else{
        dh.addData(tm.getNetworkOperatorName(), getNetworkTypeString(tm.getNetworkType()), getss(), SINR(), gsmLoc.getCid(), Calendar.getInstance().getTime(), getBand().toString());

        /*
         * runs the code every 60 seconds to add data every 60 seconds
         * */
        final Handler handler = new Handler();
        Timer timer = new Timer();
        TimerTask doTask = new TimerTask() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    @SuppressWarnings("unchecked")
                    public void run() {
                        try {
                            Intent intent = getIntent();

                            Database();
                        } catch (Exception e) {
                            // TODO Auto-generated catch block
                        }
                    }
                });
            }
        };
        timer.schedule(doTask, 60000);
    }}}






/*
* these last two methods are for the database to match the return type of INTEGER
* */

    //Get the RSSNR which is the Signal to Noise Ratio of the Network as integer for the database
    @RequiresApi(api = Build.VERSION_CODES.O)
    private int SINR() {
        TelephonyManager tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, ACCESS_FINE_LOCATION_CODE);
        }
        else{
        List<CellInfo> cellInfoList = tm.getAllCellInfo();
        int Rssnr;
        for (CellInfo cellInfo : cellInfoList) {
            if (cellInfo instanceof CellInfoLte) {
                Rssnr = ((CellInfoLte) cellInfo).getCellSignalStrength().getRssnr();
                return Rssnr;
            } else if (cellInfo instanceof CellInfoCdma) {
                Rssnr = ((CellInfoCdma) cellInfo).getCellSignalStrength().getEvdoSnr();
                return Rssnr;
            }
        }}
        return 0;
    }

    //Get the Signal Strength as integer for the database
    @RequiresApi(api = Build.VERSION_CODES.Q)
    private int getss() {
        TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, ACCESS_FINE_LOCATION_CODE);
        }
        else{
        TelephonyManager tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);

        for (CellInfo info : tm.getAllCellInfo()) {
            int gsmStrength;
            if (info instanceof CellInfoGsm) {
                CellSignalStrengthGsm gsm = ((CellInfoGsm) info).getCellSignalStrength();
                gsmStrength = gsm.getDbm();
            } else if (info instanceof CellInfoCdma) {
                CellSignalStrengthCdma cdma = ((CellInfoCdma) info).getCellSignalStrength();
                gsmStrength = cdma.getDbm();
            } else if (info instanceof CellInfoLte) {
                CellSignalStrengthLte lte = ((CellInfoLte) info).getCellSignalStrength();
                gsmStrength = lte.getDbm();
            } else if (info instanceof CellInfoWcdma) {
                CellSignalStrengthWcdma Wcdma = ((CellInfoWcdma) info).getCellSignalStrength();
                gsmStrength = ((CellSignalStrengthWcdma) Wcdma).getDbm();
            } else if (info instanceof CellInfoTdscdma) {
                CellSignalStrengthTdscdma tdscdma = ((CellInfoTdscdma) info).getCellSignalStrength();
                gsmStrength = tdscdma.getDbm();
            } else {
                gsmStrength = 0;
            }
            return gsmStrength;
        }}
        return 0;
    }

}
