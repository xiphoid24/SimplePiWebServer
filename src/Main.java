import java.io.IOException;

import com.pi4j.io.gpio.RaspiPin;


public class Main {

	public static void main(String[] args) {
		RPi pi = new RPi();
		pi.setLedPin(RaspiPin.GPIO_07);
		WebServer server = new WebServer(pi);
		try {
			server.listen(8080);
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

}