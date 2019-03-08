package org.cat.pmk.events.datamodel;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Order {

    JSONObject record;

    public Order(JSONObject record) {
        this.record = record;
    }


    public String getId() {
        try {
            return record.getString("Id");
        } catch (Exception e) {
            System.err.println("ERROR: " + e);
        }
        return "";
    }

    public String getName() {
        try {
            return record.getString("Name");
        } catch (Exception e) {
            System.err.println("ERROR: " + e);
        }
        return "";
    }

    public String getType() {
        try {
            return record.getString("SER__Type__c");
        } catch (Exception e) {
            System.err.println("ERROR: " + e);
        }
        return "";
    }

    public String getActivity() {
        try {
            return record.getString("SER__Activity__c");
        } catch (Exception e) {
            System.err.println("ERROR: " + e);
        }
        return "";
    }

    public String getProduct() {
        try {
            return record.getString("SER__OrderItem__c");
        } catch (Exception e) {
            System.err.println("ERROR: " + e);
        }
        return "";
    }

    public String getInstalledBaseList() {
        String result = "";
        try {
            JSONArray recordsOIs =  record.getJSONObject("SER__OrderItem__r").getJSONArray("records");
            for (int i = 0; i< recordsOIs.length(); i++) {
                JSONObject oi = (JSONObject)recordsOIs.get(i);
                String sn = oi.getString("Serial_Number__c");
                String product = oi.getString("SER__ProductNameCalc__c");
                result += product + " (" + sn + ") ";
            }

        } catch (Exception e) {
            System.err.println("ERROR: " + e);
        }
        return result;
    }

    public String getAppointments() {
        String result = "";
        try {
            JSONArray recordsOIs =  record.getJSONObject("SER__Appointments__r").getJSONArray("records");
            String sep = "";
            for (int i = 0; i< recordsOIs.length(); i++) {
                JSONObject oi = (JSONObject)recordsOIs.get(i);
                String start = oi.getString("SER__Start__c");
                String duration = oi.getString("SER__Duration__c");
                // cut after .
                duration = duration.substring(0, duration.indexOf("."));

                //2018-12-07T13:16:00.000+0000
                String startHuman = start.substring(0,10) + " " + (Integer.valueOf(start.substring(11,13))+1) + ":" + start.substring(14,16);

                result += sep + startHuman + " (" + duration +" Min.)";
                sep =", ";
            }

        } catch (Exception e) {
            System.err.println("ERROR: " + e);
        }
        return result;
    }


    public String getOrderLines() {
        String result = "";
        try {
            JSONArray recordsOIs =  record.getJSONObject("SER__OrderLine__r").getJSONArray("records");
            String sep = "";
            for (int i = 0; i< recordsOIs.length(); i++) {
                JSONObject oi = (JSONObject)recordsOIs.get(i);

                String article = oi.getString("SER__ArticleName__c");
                String price = oi.getString("SER__Price__c");

                result += sep + article + " (" + price +" €)";
                sep =", ";
            }

        } catch (Exception e) {
            System.err.println("ERROR: " + e);
        }
        return result;
    }






}