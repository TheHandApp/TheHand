package com.tianyaqu.thehand.app.utils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Alex on 2015/11/21.
 */
public class CommonUtils {
    public static int indexOfItem(ArrayList<String> list, final String item) {
        if (item == null || list.isEmpty()) {
            return -1;
        }
        for (int i = 0; i < list.size(); i++) {
            if (item.equals(list.get(i))) {
                return i;
            }
        }
        return -1;
    }

    public static String splitDate(String pattern,String url){
        String date = "";
        Matcher m = Pattern.compile(pattern).matcher(url);

        if(m.find()){
            date = m.group(1);
        }

        return date;
    }

    public static long dateStrToLong(String pattern,String strDate){
        long date = 0;
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        try {
            Date t = sdf.parse(strDate);
            date = t.getTime();
        }catch (Exception e){
            e.printStackTrace();
        }
        return date;
    }

    public static String longToDateStr(String pattern,long date){
        SimpleDateFormat sf = new SimpleDateFormat(pattern);
        Date t = new Date(date);
        return sf.format(t);
    }

    public static String dateToDateStr(String pattern, Date d){
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        return sdf.format(d);
    }

    private static  String ordinal(int num)
    {
        String[] suffix = {"th", "st", "nd", "rd", "th", "th", "th", "th", "th", "th"};
        int m = num % 100;
        return String.valueOf(num) + suffix[(m > 10 && m < 20) ? 0 : (m % 10)];
    }

    private static String monthStr(int month){
        String[] names = {"Jan","Feb","Mar","Apr","May","Jun","Jul","Aug","Sep","Oct","Nov","Dec"};
        return names[month - 1];
    }

    private static String monthDetailStr(int month){
        String[] names = {"January","February","March","April","May","June","July","August","September",
                "October","November","December"};
        return names[month - 1];
    }

    public static String prettyDateStr(Date d){
        String day = dateToDateStr("d",d);
        String month = dateToDateStr("MM",d);
        String year = dateToDateStr("yyyy",d);

        int dayNum = Integer.parseInt(day);
        int monthNum = Integer.parseInt(month);

        return monthStr(monthNum) + " " + ordinal(dayNum) + " " + year;
    }

    public static String prettyDateDetailStr(Date d){
        String day = dateToDateStr("d",d);
        String month = dateToDateStr("MM",d);
        String year = dateToDateStr("yyyy",d);

        int dayNum = Integer.parseInt(day);
        int monthNum = Integer.parseInt(month);

        return monthDetailStr(monthNum) + " " + ordinal(dayNum) + " " + year;
    }

    public static String currentYear(){
       return dateToDateStr("yyyy",new Date());
    }
}
