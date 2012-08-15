/**
 * 
 */
package test;

import java.net.URL;

import org.mortbay.jetty.Server;
import org.mortbay.xml.XmlConfiguration;


public class TestJetty {

	public static void main(String[] args) {

		Server server = null;
		try {
			String applicationHome = System.getProperty("application.home");

			if (applicationHome == null) {
				applicationHome = System.getProperty("user.dir");
			}
			if (applicationHome != null) {
				applicationHome = applicationHome.replace('\\', '/');
			}

			URL configUrl = new URL(new StringBuffer().append("file:").append(
					applicationHome).append("/jetty/jetty.xml").toString());

			XmlConfiguration xmlConfiguration = new XmlConfiguration(configUrl);
			server = new Server();
			xmlConfiguration.configure(server);
			server.start();
			
		} catch (Exception e) {

			System.err.println(new StringBuffer().append(
					"Could not start the Jetty server: ").append(e).toString());

			if (server != null) {
				try {
					server.stop();
				} catch (Exception e1) {
					System.err.println(new StringBuffer().append(
							"Unable to stop the jetty server: ").append(e1)
							.toString());
				}
			}
		}

	}

}

