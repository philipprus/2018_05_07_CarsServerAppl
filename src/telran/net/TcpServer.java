package telran.net;
import java.io.*;
import java.net.*;
public class TcpServer implements Runnable{
	ServerSocket serverSocket;
	Protocol protocol;
public TcpServer(int port,Protocol protocol) throws IOException{
	 serverSocket=new ServerSocket(port);
	 this.protocol=protocol;
	System.out.println("server is listening on port: "+port);
	
}

@Override
public void run() {
	while(true){
		Socket socket;
		try {
			socket = serverSocket.accept();
			TcpServerClient client=new TcpServerClient(socket, protocol);
			Thread thread=new Thread(client);
			//thread.setDaemon(false);
			thread.start();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
}
}
