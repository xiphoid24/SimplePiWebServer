import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketException;
import java.util.HashMap;
import java.util.StringTokenizer;

class ConnectionHandler extends Thread {
	Socket client;
	RPi pi;

	ConnectionHandler (Socket client, RPi pi) throws SocketException {
		this.client = client;
		this.pi = pi;
		setPriority(NORM_PRIORITY - 1);
	}

	@Override
	public void run() {
		try {
			// set up reader and writer from client object
			BufferedReader reader = new BufferedReader( new InputStreamReader(client.getInputStream(), "8859_1") );
			OutputStream out = client.getOutputStream();
			PrintWriter writer = new PrintWriter(new OutputStreamWriter(out, "8859_1"), true);
			// Get first line of request from client called requestHeader
			String requestHead = reader.readLine();
			// get tokenizer to better analyze the data get request type and location
			StringTokenizer st = new StringTokenizer(requestHead);
			String type = st.nextToken();
			String location = st.nextToken();
			// switch on request type
			if (type.equals("GET")) {              // GET request
				try {
					get(location, out);
				} catch (ObjectNotFoundException e) {
					try {
						loadPage("error.html", out);
					} catch (ObjectNotFoundException e1) {
						writer.println("404 Object Not Found");
					}
				}
			} else if (type.equals("POST")) {      // POST request
				HashMap<String, String> form = parseBody(reader);
				try {
					post(location, out, writer, form);
				} catch (ObjectNotFoundException ex) {
					try {
						loadPage("error.html", out);
					} catch (ObjectNotFoundException e1) {
						writer.println("404 Object Not Found");
					}
				} catch (InterruptedException e) {
					writer.println("Error Morsing Your Code");
				}
			}  else {
				writer.println("400 Bad Request");
			}
			client.close();
		} catch (IOException e) {
			System.out.println("I/O error " + e);
		}
	}

	// GET Request method
	private void get(String location, OutputStream out) throws ObjectNotFoundException {
		if (location.startsWith("/")) {
			location = location.substring(1);
		}
		if (location.endsWith("/") || location.equals("")) {
			if (this.pi.getLedOn()) {
				location = "on.html";
			} else {
				location = "off.html";
			}
		}
		if (location.equals("on")) {     // toggle handler
			this.pi.on();
			loadPage("on.html", out);
		} else if (location.equals("off")) {
			this.pi.off();
			loadPage("off.html", out);
		} else {
			loadPage(location, out);
		}
	}

	// POST request method
	private void post(String location, OutputStream out, PrintWriter writer, HashMap<String, String> form) throws ObjectNotFoundException, InterruptedException {
		if (location.startsWith("/")) {
			location = location.substring(1);
		}
		// switch on location
		if (location.equals("morse")) { 	// morse code handler
			try {
				this.pi.morseCode(form.get("code").toLowerCase().toCharArray());
			} catch (PiInUseException e) {
				loadPage("inUse.html", out);
			}
			if (this.pi.getLedOn()) {
				loadPage("on.html", out);
			} else {
				loadPage("off.html", out);
			}
		}
	}

	// parse body (form) of request into hashmap and return 
	private HashMap<String, String> parseBody(BufferedReader reader) throws IOException {
		String line = reader.readLine();
		char[] c = new char[1024];
		while (true) {
			line = reader.readLine();
			if (line == null || line.length() == 0) {
				reader.read(c);
				break;
			}
		}
		String body = new String(c);
		body = body.replace('+', ' ');
		String[] sl  = body.split("&");
		HashMap<String, String> form = new HashMap<String, String>();
		for (int i = 0; i < sl.length; i++) {
			form.put(sl[i].split("=")[0], sl[i].split("=")[1]);
		}
		return form;
	}

	// load an html page from disk to client
	private void loadPage(String location, OutputStream out) throws ObjectNotFoundException {
		try {	
			FileInputStream fis = new FileInputStream (location);
			byte [] data = new byte [fis.available()];
			fis.read(data);
			out.write(data);
			out.flush();
		} catch (FileNotFoundException ex) {
			throw new ObjectNotFoundException();
		} catch (IOException ex) {
			System.out.println("I/O Error " + ex);
		}
	}
}
