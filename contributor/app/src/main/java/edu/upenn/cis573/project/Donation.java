package edu.upenn.cis573.project;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class Donation implements Serializable {

    private String fundName;
    private String contributorName;
    private long amount;
    private String date;

    public Donation(String fundName, String contributorName, long amount, String date) {
        this.fundName = fundName;
        this.contributorName = contributorName;
        this.amount = amount;
        this.date = date;
    }

    public String getFundName() {
        return fundName;
    }

    public String getContributorName() {
        return contributorName;
    }

    public long getAmount() {
        return amount;
    }

    public String getDate() {
        return date;
    }

    public String toString() {
        //Date tempDate = Date.localDate(this.date);
        LocalDate tempDate = LocalDate.parse(date, DateTimeFormatter.ISO_DATE_TIME);
        String fdate = tempDate.format(DateTimeFormatter.ofPattern("MMMM dd, yyyy"));

        return fundName + ": $" + amount + " on " + fdate;
    }


}
