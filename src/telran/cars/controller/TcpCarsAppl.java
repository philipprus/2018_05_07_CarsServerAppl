package telran.cars.controller;

import java.io.IOException;
import java.util.Scanner;


import telran.net.TcpServer;
import telran.cars.dto.*;
import telran.cars.model.IRentCompany;
import telran.cars.model.RentCompanyEmbedded;
import telran.cars.protocol.CarsProtocol;
public class TcpCarsAppl {

	public static void main(String[] args) throws IOException {
		IRentCompany company=
				RentCompanyEmbedded.restoreFromFile("cars.data");
		TcpServer server=
				new TcpServer(CarsApiConstants.PORT,
						new CarsProtocol(company));
					Thread thread=new Thread(server);
					thread.setDaemon(true);
					thread.start();
					Scanner scanner=new Scanner(System.in);
					while(true){
						System.out.println("Type quit for exit from the application");
						String line=scanner.nextLine();
						if(line.equals("quit"))
							break;
					}
					scanner.close();
					System.out.println("Server will stop after finishing all clients");

	}

}
