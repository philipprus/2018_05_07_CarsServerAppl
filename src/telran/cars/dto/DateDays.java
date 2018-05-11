package telran.cars.dto;

import java.time.LocalDate;

public class DateDays {
	LocalDate currentDate;
	int days;
	public LocalDate getCurrentDate() {
		return currentDate;
	}
	public int getDays() {
		return days;
	}
	
	public void setCurrentDate(LocalDate currentDate) {
		this.currentDate = currentDate;
	}
	public void setDays(int days) {
		this.days = days;
	}
	@Override
	public String toString() {
		return "DateDays [currentDate=" + currentDate + ", days=" + days + "]";
	}
	public DateDays(LocalDate currentDate, int days) {
		super();
		this.currentDate = currentDate;
		this.days = days;
	}
	public DateDays() {}
	
	
}
