package com.uberapps.mytravellog;

import java.util.Comparator;
import java.util.Date;



public class TravelLogEntry {
	int _id;
    Date m_from;
    Date m_to;
    String m_country;


    public static final Comparator<TravelLogEntry> TO_DATE_COMPARER = new Comparator<TravelLogEntry>() {
    	public int compare(TravelLogEntry lhs, TravelLogEntry rhs) {
			return rhs.getTo().compareTo(lhs.getTo());
    	}
    };

    public static final Comparator<TravelLogEntry> FROM_DATE_COMPARER = new Comparator<TravelLogEntry>() {
    	public int compare(TravelLogEntry lhs, TravelLogEntry rhs) {
			return rhs.getFrom().compareTo(lhs.getFrom());
    	}
    };

    
    // constructor
    public TravelLogEntry(int id, String country, Date from, Date to){
        this._id = id;
        this.m_country= country;
        this.m_from = from;
        this.m_to= to;
    }
     
    // constructor
    public TravelLogEntry(String country, Date from , Date to){
    	this.m_country= country;
        this.m_from = from;
        this.m_to= to;    
    }
    
    // getting ID
    public int getID(){
        return this._id;
    }
     
    // setting id
    public void setID(int id){
        this._id = id;
    }
    
    public String getCountry() {
    	return m_country;
    }
    
    void setCountry(String country) {
    	m_country= country;
    }
    
    public Date getFrom() {
    	return m_from;
    }
    
    public void setFrom(Date from) {
    	m_from = from;
    }
    
    public Date getTo() {
    	return m_to;
    }
    
    public void setTo(Date to) {
    	m_to = to;
    }
    
    public int getTotalDays () {
    	return DayDifferenceFromTo(m_from);
    }
    
    public int DayDifferenceFromTo(Date dateToCompare) {
    	return (int)( (m_to.getTime() - dateToCompare.getTime()) / (1000 * 60 * 60 * 24)) +1;

    }
}
