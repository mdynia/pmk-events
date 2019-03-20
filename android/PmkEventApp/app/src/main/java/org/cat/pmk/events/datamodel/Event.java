package org.cat.pmk.events.datamodel;

import android.icu.text.SimpleDateFormat;

import org.json.JSONObject;

import java.net.URLEncoder;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Event {

    Map<Integer, String> daysOfWeek = new HashMap<Integer, String>()
    {{
        put(Calendar.MONDAY,    "Poniedziałek");
        put(Calendar.TUESDAY,   "Wtorek");
        put(Calendar.WEDNESDAY, "Środa");
        put(Calendar.THURSDAY,  "Czwartek");
        put(Calendar.FRIDAY,    "Piątek");
        put(Calendar.SATURDAY,  "Sobota");
        put(Calendar.SUNDAY,    "Niedziela");
    }};

    Map<Integer, String> months = new HashMap<Integer, String>()
    {{
        put(Calendar.JANUARY,   "Styczeń");
        put(Calendar.FEBRUARY,  "Luty");
        put(Calendar.MARCH,     "Marzec");
        put(Calendar.APRIL,     "Kwiecień");
        put(Calendar.MAY,       "Maj");
        put(Calendar.JUNE,      "Czerwiec");
        put(Calendar.JULY,      "Lipiec");
        put(Calendar.AUGUST,    "Sierpień");
        put(Calendar.SEPTEMBER, "Wrzesień");
        put(Calendar.OCTOBER,   "Październik");
        put(Calendar.NOVEMBER,  "Listopad");
        put(Calendar.DECEMBER,  "Grudzień");
    }};




    JSONObject record;

    public Event(JSONObject record) {
        this.record = record;
    }


    public String getId() {
        try {
            return record.getString("id");
        } catch (Exception e) {
            System.err.println("ERROR: " + e);
        }
        return "";
    }
    public String getUid() {
        try {
            return record.getString("uid");
        } catch (Exception e) {
            System.err.println("ERROR: " + e);
        }
        return "";
    }

    public String getType() {
        try {
            return record.getString("type");
        } catch (Exception e) {
            System.err.println("ERROR: " + e);
        }
        return "";
    }

    public String getTitle() {
        try {
            return record.getString("title");
        } catch (Exception e) {
            System.err.println("ERROR: " + e);
        }
        return "";
    }

    public String getDuration() {
        try {
            return record.getString("duration");
        } catch (Exception e) {
            System.err.println("ERROR: " + e);
        }
        return "";
    }


    public Integer getDistance() {
        Integer distance = 0;
        try {
            distance = Math.round(Float.parseFloat(record.getString("distance")));
        } catch (Exception e) {
            System.err.println("ERROR: " + e);
        }
        return distance;
    }


    public String getDescription() {
        try {
            return record.getString("description");
        } catch (Exception e) {
            System.err.println("ERROR: " + e);
        }
        return "";
    }

    //geo:37.7749,-122.4194
    public String getGeolocationTag() {
        try {
            return record.getString("geoLatitude") + "," + record.getString("geoLongitude");
        } catch (Exception e) {
            System.err.println("ERROR: " + e);
        }
        return "";
    }


    public String getStartDate() {
        String dateString = "";
        String timeString = "";
        int dayOfWeek = 1;
        int dayOfMonth = 1;
        int year = 1;
        int month = 1;

        try {
            dateString = record.getString("date_start");
            timeString = record.getString("time_start");

            // cut seconds
            timeString = timeString.substring(0, Math.min(timeString.length(),5));


            // check day of week
            Date dateStart = new SimpleDateFormat("yyyy-M-dd").parse(dateString);
            Calendar c = Calendar.getInstance();
            c.setTime(dateStart);
            dayOfWeek = c.get(Calendar.DAY_OF_WEEK);
            dayOfMonth = c.get(Calendar.DAY_OF_MONTH);
            year = c.get(Calendar.YEAR);
            month = c.get(Calendar.MONTH);

        } catch (Exception e) {
            System.err.println("ERROR: " + e);
        }



        String result = dayOfMonth + ". " + months.get(month) + " " + year;

        return result;
    }


    public String getStartDateDescription() {
        String dateString = "";
        String timeString = "";
        int dayOfWeek = 1;
        int dayOfMonth = 1;
        int month = 1;

        try {
            dateString = record.getString("date_start");
            timeString = record.getString("time_start");

            // cut seconds
            timeString = timeString.substring(0, Math.min(timeString.length(),5));


            // check day of week
            Date dateStart = new SimpleDateFormat("yyyy-M-dd").parse(dateString);
            Calendar c = Calendar.getInstance();
            c.setTime(dateStart);
            dayOfWeek = c.get(Calendar.DAY_OF_WEEK);
            dayOfMonth = c.get(Calendar.DAY_OF_MONTH);
            month = c.get(Calendar.MONTH);

        } catch (Exception e) {
            System.err.println("ERROR: " + e);
        }



        String result = daysOfWeek.get(dayOfWeek) +", " + timeString ;

        return result;
    }

    public String getAddress() {
        try {
            return record.getString("address");
        } catch (Exception e) {
            System.err.println("ERROR: " + e);
        }
        return "";
    }

    public String getAddressForUriParam() {
        try {
            return URLEncoder.encode(record.getString("address"), "UTF-8");
        } catch (Exception e) {
            System.err.println("ERROR: " + e);
        }
        return "";
    }


}