package de.hsharz.empfehlungssystem.beans;

public class TicketType {

	private String name;
	private int percentage;

	public int getPercentage() {
		return percentage;
	}

	public void setPercentage(int percentage) {
		this.percentage = percentage;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return name + " (" + percentage + "%)";
	}

}
