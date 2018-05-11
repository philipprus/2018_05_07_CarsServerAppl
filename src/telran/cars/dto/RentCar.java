package telran.cars.dto;

import java.time.LocalDate;

public class RentCar {
	
	String carNumber;
	long licenseId;
	LocalDate rentDate; 
	int rentDays;
	public String getCarNumber() {
		return carNumber;
	}
	public long getLicenseId() {
		return licenseId;
	}
	public LocalDate getRentDate() {
		return rentDate;
	}
	public int getRentDays() {
		return rentDays;
	}
	
	public void setCarNumber(String carNumber) {
		this.carNumber = carNumber;
	}
	public void setLicenseId(long licenseId) {
		this.licenseId = licenseId;
	}
	public void setRentDate(LocalDate rentDate) {
		this.rentDate = rentDate;
	}
	public void setRentDays(int rentDays) {
		this.rentDays = rentDays;
	}
	public RentCar(String carNumber, long licenseId, LocalDate rentDate, int rentDays) {
		super();
		this.carNumber = carNumber;
		this.licenseId = licenseId;
		this.rentDate = rentDate;
		this.rentDays = rentDays;
	}
	@Override
	public String toString() {
		return "RentCar [carNumber=" + carNumber + ", licenseId=" + licenseId + ", rentDate=" + rentDate + ", rentDays="
				+ rentDays + "]";
	}
	public RentCar() {}
	
	

}
