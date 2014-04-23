import java.net.*;
import java.io.*;

public class WebServer { 
	private RPi pi;
	
	public WebServer(RPi pi) {
		this.pi = pi;
	}
	
	public void listen(int port) throws IOException {
		System.out.println("-----Starting Server-----");
		ServerSocket ss = new ServerSocket(port);
		System.out.printf("-----Listening On %d-----\n", port);
		while (true) {
			new ConnectionHandler(ss.accept(), this.pi).start();
		}
	}
}