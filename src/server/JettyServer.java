package server;

import java.net.URL;

import org.mortbay.jetty.Server;
import org.mortbay.jetty.nio.SelectChannelConnector;
import org.mortbay.jetty.servlet.ServletHandler;
import org.mortbay.jetty.servlet.ServletHolder;
import org.mortbay.xml.XmlConfiguration;

public class JettyServer {

	public static void main(String[] args) {
		Server server = new Server();
		try {
			server = new Server();
	        SelectChannelConnector connector = new SelectChannelConnector();
	        connector.setPort(6000);
	        connector.open();
	        server.addConnector(connector);

	        ServletHandler handler = new ServletHandler();
	        handler.addServletWithMapping(new ServletHolder(new MyServlet()), "/remote");
			
			server.addHandler(handler);			
			server.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
