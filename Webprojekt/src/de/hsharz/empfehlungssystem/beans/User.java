package de.hsharz.empfehlungssystem.beans;

public class User {

	private int id;
	private String username;

	private String gender;
	private String firstname;
	private String lastname;

	private String street;
	private String houseNr;
	private String zip;
	private String city;
	private String country;
	private String countryShort;

	private String email;
	private String password;
	
	private String birthday;

	private String preference1;
	private String preference2;
	private String preference3;
	
	
	public User() {

	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getZip() {
		return zip;
	}

	public void setZip(String zip) {
		this.zip = zip;
	}

	public String getHouseNr() {
		return houseNr;
	}

	public void setHouseNr(String houseNr) {
		this.houseNr = houseNr;
	}

	public String getStreet() {
		return street;
	}

	public void setStreet(String street) {
		this.street = street;
	}

	public String getLastname() {
		return lastname;
	}

	public void setLastname(String lastname) {
		this.lastname = lastname;
	}

	public String getFirstname() {
		return firstname;
	}

	public void setFirstname(String firstname) {
		this.firstname = firstname;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public String getBirthday() {
		return birthday;
	}

	public void setBirthday(String birthday) {
		this.birthday = birthday;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getCountryShort() {
		return countryShort;
	}

	public void setCountryShort(String countryShort) {
		this.countryShort = countryShort;
	}

	public String getPreference3() {
		return preference3;
	}

	public void setPreference3(String preference3) {
		this.preference3 = preference3;
	}

	public String getPreference2() {
		return preference2;
	}

	public void setPreference2(String preference2) {
		this.preference2 = preference2;
	}

	public String getPreference1() {
		return preference1;
	}

	public void setPreference1(String preference1) {
		this.preference1 = preference1;
	}


}
