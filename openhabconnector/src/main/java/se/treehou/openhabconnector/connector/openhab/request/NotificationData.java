package se.treehou.openhabconnector.connector.openhab.request;

import org.joda.time.DateTime;

import java.util.Date;

public class NotificationData {

    private String message = "";
    private Date date;
    boolean viewed;

    public NotificationData() {
    }

    public NotificationData(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public DateTime getDate() {
        return new DateTime(date);
    }

    public void setDate(DateTime date) {
        this.date = date.toDate();
    }

    public boolean isViewed() {
        return viewed;
    }

    public void setViewed(boolean viewed) {
        this.viewed = viewed;
    }
}
