package vyuka;

import java.io.*;
import java.security.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.net.ssl.*;

public class SslEchoer {
	public static void main(String[] args) {
		String ksName = "YOURNAME.p12";
		char ksPass[] = "testpass".toCharArray();
		char ctPass[] = "testpass".toCharArray();
		// Creating the thread pool:
		ExecutorService executorService = Executors.newFixedThreadPool(1000);
		
		
		try {
			KeyStore ks = KeyStore.getInstance("pkcs12");
			ks.load(new FileInputStream(ksName), ksPass);
			KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");
			kmf.init(ks, ctPass);
			SSLContext sc = SSLContext.getInstance("TLS");
			sc.init(kmf.getKeyManagers(), null, null);
			SSLServerSocketFactory ssf = sc.getServerSocketFactory();
			SSLServerSocket s = (SSLServerSocket) ssf.createServerSocket(8888);
			printServerSocketInfo(s);
			// accept client connection:
			while (true) {
				SSLSocket c = (SSLSocket) s.accept();
				SSLClientHandler clientHandler = new SSLClientHandler(c);
				executorService.execute(clientHandler);
				printSocketInfo(c);
			}

			
		} catch (Exception e) {
			System.err.println(e.toString());
		}
	}

	private static void printSocketInfo(SSLSocket s) {
		System.out.println("Socket class: " + s.getClass());
		System.out.println("   Remote address = " + s.getInetAddress().toString());
		System.out.println("   Remote port = " + s.getPort());
		System.out.println("   Local socket address = " + s.getLocalSocketAddress().toString());
		System.out.println("   Local address = " + s.getLocalAddress().toString());
		System.out.println("   Local port = " + s.getLocalPort());
		System.out.println("   Need client authentication = " + s.getNeedClientAuth());
		SSLSession ss = s.getSession();
		System.out.println("   Cipher suite = " + ss.getCipherSuite());
		System.out.println("   Protocol = " + ss.getProtocol());
	}

	private static void printServerSocketInfo(SSLServerSocket s) {
		System.out.println("Server socket class: " + s.getClass());
		System.out.println("   Socket address = " + s.getInetAddress().toString());
		System.out.println("   Socket port = " + s.getLocalPort());
		System.out.println("   Need client authentication = " + s.getNeedClientAuth());
		System.out.println("   Want client authentication = " + s.getWantClientAuth());
		System.out.println("   Use client mode = " + s.getUseClientMode());
	}
}