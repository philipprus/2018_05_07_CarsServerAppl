package telran.cars.model;
import java.util.*;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import telran.cars.dto.*;

@SuppressWarnings("serial")
public class RentCompanyEmbedded extends AbstractRentCompany {
	String fileName;
private HashMap<String,Car> cars=new HashMap<>();
private HashMap<Long,Driver> drivers=new HashMap<>();
private HashMap<String,List<RentRecord>> carRecords=new HashMap<>();
private HashMap<Long,List<RentRecord>> driverRecords=new HashMap<>();
private HashMap<String,Model> models=new HashMap<>();
private TreeMap<LocalDate,List<RentRecord>> returnedRecords=new TreeMap<>();
	@Override
	public CarsReturnCode addModel(Model model) {
		
		return models.putIfAbsent(model.getModelName(), model)==null?
				CarsReturnCode.OK:CarsReturnCode.MODEL_EXISTS;
	}

	@Override
	public CarsReturnCode addCar(Car car) {
		Model model=models.get(car.getModelName());
		if(model==null)
			return CarsReturnCode.NO_MODEL;
		return cars.putIfAbsent(car.getRegNumber(),
				car)==null?CarsReturnCode.OK:CarsReturnCode.CAR_EXISTS;
	}

	@Override
	public CarsReturnCode addDriver(Driver driver) {
		return drivers.putIfAbsent(driver.getLicenseId(),
				driver)==null?CarsReturnCode.OK:
					CarsReturnCode.DRIVER_EXISTS;
	}

	@Override
	public Model getModel(String modelName) {
		
		return models.get(modelName);
	}

	@Override
	public Car getCar(String carNumber) {
		return cars.get(carNumber);
	}

	@Override
	public Driver getDriver(long licenseId) {
		return drivers.get(licenseId);
	}

	@Override
	public CarsReturnCode rentCar
	(String carNumber, long licenseId, LocalDate rentDate, int rentDays) {
		Car car=getCar(carNumber);
		if(car==null||car.isFlRemoved())
			return CarsReturnCode.NO_CAR;
		if(car.isInUse())
			return CarsReturnCode.CAR_IN_USE;
		if(getDriver(licenseId)==null)
			return CarsReturnCode.NO_DRIVER;
		RentRecord record=new RentRecord(licenseId, carNumber, rentDate, rentDays);
		addCarRecords(record);
		addDriverRecords(record);
		car.setInUse(true);
		return CarsReturnCode.OK;
	}

	private void addDriverRecords(RentRecord record) {
		long licenseId=record.getLicenseId();
		List<RentRecord> records=driverRecords.get(licenseId);
		if(records==null){
			records=new ArrayList<>();
			driverRecords.put(licenseId, records);
		}
		records.add(record);
		
	}

	private void addCarRecords(RentRecord record) {
		String carNumber=record.getCarNumber();
		List<RentRecord> records=carRecords.get(carNumber);
		if(records==null){
			records=new ArrayList<>();
			carRecords.put(carNumber, records);
		}
		records.add(record);
		
	}

	@Override
	public CarsReturnCode returnCar(String carNumber, long licenseId, LocalDate returnDate, int gasTankPercent,
			int damages) {
		if(drivers.get(licenseId)==null)
			return CarsReturnCode.NO_DRIVER;
		List<RentRecord>recordsCar=carRecords.get(carNumber);
		if(recordsCar==null)
			return CarsReturnCode.CAR_NOT_RENTED;
		Car car=getCar(carNumber);
		if(car==null || !car.isInUse())
			return CarsReturnCode.CAR_NOT_RENTED;
		RentRecord record=findRecordInUse(recordsCar);
		if(record==null)
			throw new IllegalArgumentException("record in use doesn't exists");
		if(returnDate.isBefore(record.getRentDate()))
			return CarsReturnCode.RETURN_DATE_WRONG;
		record.setDamages(damages);
		record.setGasTankPercent(gasTankPercent);
		record.setReturnDate(returnDate);
		setCost(record,car);
		updateCarData(damages, car);
		addReturnedRecords(record);
		return CarsReturnCode.OK;
	}

	private void addReturnedRecords(RentRecord record) {
		LocalDate returnDate=record.getReturnDate();
		List<RentRecord>records=returnedRecords.get(returnDate);
		if(records==null){
			records=new ArrayList<>();
			returnedRecords.put(returnDate, records);
		}
		records.add(record);
		
	}

	private void updateCarData(int damages, Car car) {
		if(damages>0 && damages<10)
			car.setState(State.GOOD);
		else if(damages>=10&&damages<30)
			car.setState(State.BAD);
		else if(damages>=30)
			car.setFlRemoved(true);
		car.setInUse(false);
	}

	private void setCost(RentRecord record, Car car) {
		long period=ChronoUnit.DAYS.between
				(record.getRentDate(), record.getReturnDate());
		float costPeriod=0;
		Model model=getModel(car.getModelName());
		float costGas=0;
		costPeriod = getCostPeriod(record, period, model);
		costGas = getCostGas(record, model);
		record.setCost(costPeriod+costGas);
		
	}

	private float getCostGas(RentRecord record, Model model) {
		float costGas;
		int gasTank=model.getGasTank();
		float litersCost=(float)(100-record.getGasTankPercent())*gasTank/100;
		costGas=litersCost*companyData.getGasPrice();
		return costGas;
	}

	private float getCostPeriod(RentRecord record, long period, Model model) {
		float costPeriod;
		long delta=period-record.getRentDays();
		float additionalPeriodCost=0;
		
		if(model==null)
			throw new IllegalArgumentException("Car contains wrong model");
		int pricePerDay=model.getPriceDay();
		int rentDays=record.getRentDays();
		if(delta>0){
			additionalPeriodCost=getAdditionalPeriodCost
					(pricePerDay,delta);
		}
		costPeriod=rentDays*pricePerDay+additionalPeriodCost;
		return costPeriod;
	}

	private float getAdditionalPeriodCost(int pricePerDay, long delta) {
		float fineCostPerDay=pricePerDay*companyData.getFinePercent()/100;
		return (pricePerDay+fineCostPerDay)*delta;
	}

	private RentRecord findRecordInUse(List<RentRecord> recordsCar) {
		return recordsCar.stream()
				.filter(r->r.getReturnDate()==null)
				.findFirst().get();
	}

	@Override
	public CarsReturnCode removeCar(String carNumber) {
		Car car=cars.get(carNumber);
		if(car==null)
			return CarsReturnCode.NO_CAR;
		if(car.isInUse())
			return CarsReturnCode.CAR_IN_USE;
		car.setFlRemoved(true);
		return CarsReturnCode.OK;
	}

	@Override
	public List<Car> clear(LocalDate currentDate, int days) {
		LocalDate returnedDateDelete=currentDate.minusDays(days);
		List<RentRecord> recordsForDelete=getRecordsForDelete(returnedDateDelete);
		recordsForDelete.forEach(this::deleteFromReturnedRecords);
		List<Car> carsForDelete=getCarsForDelete(recordsForDelete);
		carsForDelete.forEach(this::deleteCar);
		return carsForDelete;
	}
	private void deleteFromReturnedRecords(RentRecord record){
		//getting list of records returned on record.getReturnDate() date
		//removing record from the list
		returnedRecords.get(record.getReturnDate()).remove(record);
	}
	private List<Car> getCarsForDelete(List<RentRecord> recordsForDelete) {
		
		return recordsForDelete.stream().map(r->getCar(r.getCarNumber()))
				.filter(c->!c.isInUse()).collect(Collectors.toList());
	}
	private List<RentRecord> getRecordsForDelete(LocalDate returnedDateDelete) {
		
		return returnedRecords.headMap(returnedDateDelete).values().stream()
				.flatMap(List::stream).filter(r->getCar(r.getCarNumber()).isFlRemoved())
				.collect(Collectors.toList());
	}

	private void deleteCar(Car car){
		//car should be removed from cars and all related records from carRecords and driverRecords
		//removing from carRecords and getting records that should be removed from driverRecords
		String carNumber=car.getRegNumber();
		List<RentRecord> removedRecords=carRecords.remove(carNumber);
		removeFromDriverRecords(removedRecords);
		//remove from cars
		cars.remove(carNumber);
	}
	
	private void removeFromDriverRecords(List<RentRecord> removedRecords) {
		//for each record: getting licenseId, getting list of records for appropriated driver,
		//removing from the list
		removedRecords.forEach(r->driverRecords.get(r.getLicenseId()).remove(r));
		
	}

	@Override
	public List<Driver> getCarDrivers(String carNumber) {
		List<Driver> res=new ArrayList<>();
		if (!carRecords.isEmpty()) {
			carRecords.get(carNumber).forEach(r -> res.add(getDriver(r.getLicenseId())));
		}
		return res;
	}

	@Override
	public List<Car> getDriverCars(long licenseId) {
		List<Car> res=new ArrayList<>();
		 if (!driverRecords.isEmpty()) {
			driverRecords.get(licenseId).forEach(r -> res.add(getCar(r.getCarNumber())));
		}
		return res;
	}

	@Override
	public Stream<Car> getAllCars() {
		
		return cars.values().stream();
	}

	@Override
	public Stream<Driver> getAllDrivers() {
		return drivers.values().stream();
	}

	@Override
	public Stream<RentRecord> getAllRecords() {
		return carRecords.values().stream().flatMap(List::stream);
	}

	@Override
	public List<String> getAllModels() {
		
		return models.values().stream()
				.map(Model::getModelName)
				.collect(Collectors.toList());
	}
public static RentCompanyEmbedded restoreFromFile(String fileName){
	RentCompanyEmbedded res=null;
	try (ObjectInputStream stream=
			new ObjectInputStream(new FileInputStream(fileName))){
		res=(RentCompanyEmbedded) stream.readObject();
	}catch (Exception e){
		res=new RentCompanyEmbedded();
	}
	res.fileName=fileName;
	return res;
}
	@Override
	public void save() {
		if(fileName==null){
			System.out.println("file name isn't provided");
		return;
		}
		try (ObjectOutputStream stream=
		new ObjectOutputStream(new FileOutputStream(fileName))){
			stream.writeObject(this);
		}catch(IOException e){
			System.out.println("Save failed with error message: "+e.getMessage());
		}
		
	}

}
