package de.hsharz.empfehlungssystem.beans;

import java.sql.Date;
import java.sql.Time;

public class Event {

	private int id;
	private String title;
	private String description;
	private String genre;
	private String subGenre;
	private String city;
	private String street;
	private String zip;
	private String housenumber;
	private String organizer;
	private Date date;
	private Time time;
	private long duration;
	private double price;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getGenre() {
		return genre;
	}

	public void setGenre(String genre) {
		this.genre = genre;
	}

	public String getSubGenre() {
		return subGenre;
	}

	public void setSubGenre(String subGenre) {
		this.subGenre = subGenre;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public double getPrice() {
		return price;
	}

	public void setPrice(double price) {
		this.price = price;
	}

	public Time getTime() {
		return time;
	}

	public void setTime(Time time) {
		this.time = time;
	}

	public long getDuration() {
		return duration;
	}

	public void setDuration(long duration) {
		this.duration = duration;
	}

	public String getOrganizer() {
		return organizer;
	}

	public void setOrganizer(String organizer) {
		this.organizer = organizer;
	}

	public String getHouseNumber() {
		return housenumber;
	}

	public void setHouseNumber(String housenumber) {
		this.housenumber = housenumber;
	}

	public String getZIP() {
		return zip;
	}

	public void setZIP(String zip) {
		this.zip = zip;
	}

	public String getStreet() {
		return street;
	}

	public void setStreet(String street) {
		this.street = street;
	}

	@Override
	public String toString() {
		return "Event: " + getTitle();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Event) {
			Event e = (Event) obj;
			return getTitle().equals(e.getTitle());
		}
		return super.equals(obj);
	}

}
