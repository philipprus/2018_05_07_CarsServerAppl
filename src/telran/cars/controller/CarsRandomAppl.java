package telran.cars.controller;

import java.time.LocalDate;
import java.util.Map;
import java.util.stream.Collectors;

import telran.cars.dto.Car;
import telran.cars.dto.Driver;
import telran.cars.dto.RentRecord;
import telran.cars.model.IRentCompany;
import telran.cars.model.RentCompanyEmbedded;

public class CarsRandomAppl {

	public static void main(String[] args) {
		IRentCompany rentCompany=new RentCompanyEmbedded();
		RandomRentCompanyController controller=
		new RandomRentCompanyController(rentCompany);
		controller.createRentCompany(1);
		System.out.println("most popular models for age less than 31");
		displayMostPopularModels(rentCompany,0,31);
		System.out.println("\nmost popular models for age from 31 to 45");
		displayMostPopularModels(rentCompany,31,46);
		System.out.println("\nmost popular models for age greater than 45");
		displayMostPopularModels(rentCompany,46,100);
		System.out.println("\nmost profitable model");
		displayMostProfitableModels(rentCompany);
		System.out.println("\nmost active drivers");
		displayMostActiveDriver(rentCompany);
		System.out.println("\nmost popular colors");
		displayMostPopularColor(rentCompany);
	}

	private static void displayMostPopularColor(IRentCompany rentCompany) {
		Map<String,Long> colorCount=
		rentCompany.getAllCars()
		.collect(Collectors.groupingBy(Car::getColor,
				Collectors.counting()));
		long maxCount=colorCount.values().stream()
				.max(Long::compare).orElse(0L);
		colorCount.entrySet().stream()
		.filter(x->x.getValue()==maxCount)
		.forEach(System.out::println);
		
	}

	private static void displayMostActiveDriver(IRentCompany rentCompany) {
		Map<Long,Long> driverOccurrences=
		rentCompany.getAllRecords().collect(Collectors
				.groupingBy(RentRecord::getLicenseId,
				Collectors.counting()));
		long maxOccurrences=driverOccurrences.values().stream()
				.max(Long::compare).orElse(0L);
		driverOccurrences.entrySet().stream()
		.filter(x->x.getValue()==maxOccurrences)
		.forEach(x->System.out.println(rentCompany.getDriver(x.getKey())));
		
	}

	private static void displayMostProfitableModels(IRentCompany rentCompany) {
		Map<String,Double>modelCost=rentCompany.getAllCars()
		.collect(Collectors.groupingBy(Car::getModelName,
				Collectors.summingDouble(c->getCarCost(rentCompany,c))));
		double max=modelCost.values().stream().max(Double::compare)
				.orElse(0.0);
		modelCost.entrySet().stream().filter(x->x.getValue()==max)
		.forEach(System.out::println);
		
	}
	private static double getCarCost(IRentCompany rentCompany,Car c){
		
		return rentCompany.getAllRecords()
				.filter(r->r.getCarNumber()==c.getRegNumber())
				.collect(Collectors.summingDouble(RentRecord::getCost));
	}

	private static void displayMostPopularModels(IRentCompany rentCompany,
			int ageFrom, int ageTo) {
		Map<String,Long> modelsCounts=
		rentCompany.getAllCars()
		.collect(Collectors.groupingBy(Car::getModelName,
				Collectors.summingLong(c->getCountAge(rentCompany,c,
						ageFrom,ageTo))));
		long maxCount=modelsCounts.values().stream()
				.max(Long::compare).orElse(0L);
		modelsCounts.entrySet().stream()
		.filter(x->x.getValue()==maxCount)
		.forEach(System.out::println);
	}

	private static Long getCountAge(IRentCompany rentCompany,
			Car c, int ageFrom, int ageTo) {
		
		return rentCompany.getAllRecords()
				.filter(r->filterCountAge(rentCompany,r,ageFrom,ageTo,c))
				.count();
	}

	private static boolean filterCountAge(IRentCompany rentCompany,
			RentRecord r, int ageFrom, int ageTo, Car c) {
		if(r.getCarNumber()!=c.getRegNumber())
			return false;
		int yearTo=getBirthYear(ageFrom);
		int yearFrom=getBirthYear(ageTo);
		Driver driver=rentCompany.getDriver(r.getLicenseId());
		int birthYear=driver.getBirthYear();
		return birthYear>=yearFrom && birthYear<=yearTo;
	}

	private static int getBirthYear(int age) {
		return LocalDate.now().minusYears(age).getYear();
	}

}
