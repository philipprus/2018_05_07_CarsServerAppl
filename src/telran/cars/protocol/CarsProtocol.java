package telran.cars.protocol;



import java.io.IOException;
import java.lang.reflect.Method;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import telran.net.Protocol;
import telran.cars.dto.*;
import telran.cars.model.IRentCompany;
public class CarsProtocol implements Protocol{
private IRentCompany company;
private ObjectMapper mapper=new ObjectMapper();
	public CarsProtocol(IRentCompany company) {
	super();
	this.company = company;
	mapper.registerModule(new JavaTimeModule());
}

	@Override
	public String getResponse(String request) {
		//Any request contains <type>#<string of parameters>
		int sharpIndex=request.indexOf("#");
		if(sharpIndex<0)
			return CarsApiConstants.WRONG_REQUEST;
		String requestType=request.substring(0,sharpIndex);
		try {
			Method method=this.getClass()
			.getDeclaredMethod(requestType, String.class);
			return (String) method.invoke(this, request.substring(sharpIndex+1));
		} catch (Exception e) {
			return CarsApiConstants.WRONG_REQUEST;
		}
	}
	//Protocol methods
	String add_car(String json){
		Car car;
		try {
			car = mapper.readValue(json, Car.class);
		} catch (IOException e) {
			return CarsApiConstants.WRONG_REQUEST;
		}
		CarsReturnCode code=company.addCar(car);
		return code.toString();
	}
	String add_car_model(String json){
		Model model;
		try {
			model = mapper.readValue(json, Model.class);
		} catch (IOException e) {
			return CarsApiConstants.WRONG_REQUEST;
		}
		CarsReturnCode code=company.addModel(model);
		return code.toString();
	}
	String add_driver(String json){
		Driver driver;
		try {
			driver = mapper.readValue(json, Driver.class);
		} catch (IOException e) {
			return CarsApiConstants.WRONG_REQUEST;
		}
		CarsReturnCode code=company.addDriver(driver);
		return code.toString();
	}
	String clear_cars (String parameters){
		//Parameters separated by #
		//first - current date
		//second - days
		String arParams[]=parameters.split("#");
		try {
			LocalDate currentDate=LocalDate.parse(arParams[0]) ;
			int days=Integer.parseInt(arParams[1]);
			List<Car>cars=company.clear(currentDate, days);
			return mapper.writeValueAsString(cars);
		}
		catch(Exception e){
			return CarsApiConstants.WRONG_REQUEST;
		}
	}
	String get_all_cars(String dummy){
		List<Car> cars=company.getAllCars().collect(Collectors.toList());
		try {
			return mapper.writeValueAsString(cars);
		} catch (JsonProcessingException e) {
			return CarsApiConstants.WRONG_REQUEST;
		}
	}
	String get_all_drivers(String dummy){
		List<Driver> cars=company.getAllDrivers().collect(Collectors.toList());
		try {
			return mapper.writeValueAsString(cars);
		} catch (JsonProcessingException e) {
			return CarsApiConstants.WRONG_REQUEST;
		}
	}
	String get_all_models(String dummy){
		List<String> cars=company.getAllModels();
		try {
			return mapper.writeValueAsString(cars);
		} catch (JsonProcessingException e) {
			return CarsApiConstants.WRONG_REQUEST;
		}
	}
	String get_all_records(String dummy){
		List<RentRecord> cars=company.getAllRecords().collect(Collectors.toList());
		try {
			return mapper.writeValueAsString(cars);
		} catch (JsonProcessingException e) {
			return CarsApiConstants.WRONG_REQUEST;
		}
	}
	String get_car(String param){
		//One parameter containing car number
		try {
			Car car=company.getCar(param);
			return mapper.writeValueAsString(car);
		}
		catch(Exception e){
			return CarsApiConstants.WRONG_REQUEST;
		}
	}
	String get_driver(String param){
		//One parameter containing license number
		try {
			
			long license=Long.parseLong(param);
			Driver driver=company.getDriver(license);
			return mapper.writeValueAsString(driver);
		}
		catch(Exception e){
			return CarsApiConstants.WRONG_REQUEST;
		}
	}
	String get_model(String param){
		//One parameter containing model name
		try {
			Model model=company.getModel(param);
			return mapper.writeValueAsString(model);
		}
		catch(Exception e){
			return CarsApiConstants.WRONG_REQUEST;
		}
	}
	String remove_car(String param){
		//One parameter containing car number
		CarsReturnCode code=company.removeCar(param);
		return code.toString();
	}
	String rent_car(String json){
		try {
			RentRecord record=mapper.readValue(json, RentRecord.class);
			String carNumber=record.getCarNumber();
			long licenseId=record.getLicenseId();
			LocalDate rentDate=record.getRentDate();
			int rentDays=record.getRentDays();
			CarsReturnCode code=company.rentCar(carNumber, licenseId,
					rentDate, rentDays);
			return code.toString();
		} catch (Exception e) {
			return CarsApiConstants.WRONG_REQUEST;
		} 
		
	}
	String return_car(String json){
		try {
			RentRecord record=mapper.readValue(json, RentRecord.class);
			String carNumber=record.getCarNumber();
			long licenseId=record.getLicenseId();
			LocalDate returnDate=record.getReturnDate();
			int gasTankPercent=record.getGasTankPercent();
			int damages=record.getDamages();
			CarsReturnCode code=company.returnCar
			(carNumber, licenseId, returnDate, gasTankPercent, damages);
			return code.toString();
		}
		catch(Exception e){
			return CarsApiConstants.WRONG_REQUEST;
		}
	}
	String save(String dummy){
		company.save();
		return "";
	}
	String get_car_drivers(String param){
		//One parameter containing car number
		try {
			
			List<Driver> drivers=company.getCarDrivers(param);
			return mapper.writeValueAsString(drivers);
		}catch(Exception e){
			return CarsApiConstants.WRONG_REQUEST;
		}
	}
	String get_driver_cars(String param){
		//One parameter containing license id
		try {
			long licenseId=Long.parseLong(param);
			List<Car> cars=company.getDriverCars(licenseId);
			return mapper.writeValueAsString(cars);
		}catch(Exception e){
			return CarsApiConstants.WRONG_REQUEST;
		}
	}

}
