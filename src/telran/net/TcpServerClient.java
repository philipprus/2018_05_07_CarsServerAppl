package telran.net;
import java.io.*;
import java.net.*;
public class TcpServerClient implements Runnable {
	Socket socket;
	
	Protocol protocol;
	

	public TcpServerClient(Socket socket, Protocol protocol) {
		super();
		this.socket = socket;
		this.protocol = protocol;
	}


	@Override
	public void run() {
		try (BufferedReader reader=
				new BufferedReader(new InputStreamReader
				(socket.getInputStream()));
			PrintStream writer=new PrintStream(socket.getOutputStream())){
			while(true){
				String line=null;
				line=reader.readLine();
				if(line==null)
					break;
				line=protocol.getResponse(line);
				writer.println(line);
			}
		}
			
		catch (Exception e){
			System.out.println(e.getMessage());
		}

	

}
}
