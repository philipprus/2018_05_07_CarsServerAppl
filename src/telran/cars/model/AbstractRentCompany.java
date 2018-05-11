package telran.cars.model;

import java.io.Serializable;

import telran.cars.dto.RentCompanyData;

@SuppressWarnings("serial")
public abstract class AbstractRentCompany implements IRentCompany,Serializable {
protected RentCompanyData companyData=new RentCompanyData();
@Override
public void setCompanyData(RentCompanyData companyData){
	this.companyData=companyData;
}
}
