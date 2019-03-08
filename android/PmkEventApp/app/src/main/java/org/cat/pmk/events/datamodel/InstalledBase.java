package org.cat.pmk.events.datamodel;

import org.json.JSONObject;

public class InstalledBase {

    JSONObject record;

    public InstalledBase(JSONObject record) {
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

    public String getSerialNumber() {
        try {
            return record.getString("SER__SerialNo__c");
        } catch (Exception e) {
            System.err.println("ERROR: " + e);
        }
        return "";
    }

    public String getProduct() {
        try {
            return record.getString("SER__ProductNameCalc__c");
        } catch (Exception e) {
            System.err.println("ERROR: " + e);
        }
        return "";
    }

    public String getShortText() {
        try {
            return record.getString("SER__ProductNameCalc__c");
        } catch (Exception e) {
            System.err.println("ERROR: " + e);
        }
        return "";
    }

    public String getLastModifiedDate() {
        try {
            return record.getString("LastModifiedDate");
        } catch (Exception e) {
            System.err.println("ERROR: " + e);
        }
        return "";
    }

}