package telran.cars.dto;

import java.time.LocalDate;

public class ReturnCar {
	String carNumber;
	long licenseId;
	LocalDate returnDate;
	int gasTankPercent;
	int damages;
	public String getCarNumber() {
		return carNumber;
	}
	public long getLicenseId() {
		return licenseId;
	}
	public LocalDate getReturnDate() {
		return returnDate;
	}
	public int getGasTankPercent() {
		return gasTankPercent;
	}
	public int getDamages() {
		return damages;
	}
	
	
	public void setCarNumber(String carNumber) {
		this.carNumber = carNumber;
	}
	public void setLicenseId(long licenseId) {
		this.licenseId = licenseId;
	}
	public void setReturnDate(LocalDate returnDate) {
		this.returnDate = returnDate;
	}
	public void setGasTankPercent(int gasTankPercent) {
		this.gasTankPercent = gasTankPercent;
	}
	public void setDamages(int damages) {
		this.damages = damages;
	}
	@Override
	public String toString() {
		return "ReturnCar [carNumber=" + carNumber + ", licenseId=" + licenseId + ", returnDate=" + returnDate
				+ ", gasTankPercent=" + gasTankPercent + ", damages=" + damages + "]";
	}
	public ReturnCar(String carNumber, long licenseId, LocalDate returnDate, int gasTankPercent, int damages) {
		super();
		this.carNumber = carNumber;
		this.licenseId = licenseId;
		this.returnDate = returnDate;
		this.gasTankPercent = gasTankPercent;
		this.damages = damages;
	}
	public ReturnCar() {}
	
	

}
