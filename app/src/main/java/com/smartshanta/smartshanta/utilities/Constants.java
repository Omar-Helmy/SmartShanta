package com.smartshanta.smartshanta.utilities;

/**
 * Created by OMAR on 22/01/2017.
 */
public abstract class Constants {

    /*Bluetooth*/
    //actions:
    public static final String BL_ACTION_CONNECT = "connect";
    public static final String BL_ACTION_SEND = "send";
    public static final String BL_ACTION_ITEM_CHECK = "check";
    public static final String BL_ACTION_LOCATE = "locate";

    public static final String BL_MAC = "30:14:11:20:04:11";
    public static final String UUID = "00001101-0000-1000-8000-00805F9B34FB";
    public static boolean isShantaConnected = false;
    //Messages:
    public static final String BL_MSG_CONNECTED = "connected";
    public static final String BL_MSG_STUFF = "a";
    public static final String BL_MSG_FIND_ME = "b";
    public static final String BL_MSG_FIND_YOU = "c";
    public static final String BL_MSG_GET_BAG_NUMBER = "d";
    public static final String BL_MSG_SET_USER_NUMBER = "e";
    public static final String BL_MSG_DEFINE_ITEM = "f";
    public static final String BL_MSG_UNDEFINE_ITEM = "g";
    public static final String BL_MSG_FINGER_DEFINE_USER = "l";
    public static final String BL_MSG_FINGER_REMOVE_USER = "m";
    public static final String BL_MSG_LOCATE = "h";


    public static final String SHARED_PREF_FILE = "com.smartshanta.smartshanta.SharedPreferences";


}


/*
>>> Tutorials:
https://guides.codepath.com/android/ViewPager-with-FragmentPagerAdapter
https://guides.codepath.com/android/Starting-Background-Services
 */