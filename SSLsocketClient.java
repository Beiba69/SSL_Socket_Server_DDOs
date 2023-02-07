package vyuka;

import java.io.*;
import java.security.*;
import java.security.cert.*;

import javax.net.ssl.*;

// example from https://docs.oracle.com/javase/10/security/sample-code-illustrating-secure-socket-connection-client-and-server.htm
public class SSLsocketClient {
	private static class SavingTrustManager implements X509TrustManager {

		private final X509TrustManager tm;
		private X509Certificate[] chain;

		SavingTrustManager(X509TrustManager tm) {
			this.tm = tm;
		}

		public X509Certificate[] getAcceptedIssuers() {
			return new X509Certificate[0];
		}

		/*
		 * public X509Certificate[] getAcceptedIssuers() { throw new
		 * UnsupportedOperationException(); }
		 */
		public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
			throw new UnsupportedOperationException();
		}

		public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
			this.chain = chain;
			tm.checkServerTrusted(chain, authType);
		}
	}

	public static void main(String[] args) {
		try {
			String file = "public.p12";
			char passphrase[] = "testpass".toCharArray();
			System.out.println("Loading KeyStore " + file + "...");
			InputStream inf = new FileInputStream(file);
			KeyStore ks = KeyStore.getInstance(KeyStore.getDefaultType());//Certicate type: ("pkcs12");
			ks.load(inf, passphrase);
			inf.close();

			SSLContext context = SSLContext.getInstance("TLS");
			TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
			tmf.init(ks);
			X509TrustManager defaultTrustManager = (X509TrustManager) tmf.getTrustManagers()[0];
			SavingTrustManager tm = new SavingTrustManager(defaultTrustManager);
			context.init(null, new TrustManager[] { tm }, null);

			SSLSocketFactory factory = context.getSocketFactory();
			SSLSocket socket = (SSLSocket) factory.createSocket("localhost", 8888);

			/*
			 * send http request
			 *
			 * Before any application data is sent or received, the SSL socket will do SSL
			 * handshaking first to set up the security attributes.
			 *
			 * SSL handshaking can be initiated by either flushing data down the pipe, or by
			 * starting the handshaking by hand.
			 *
			 * Handshaking is started manually in this example because PrintWriter catches
			 * all IOExceptions (including SSLExceptions), sets an internal error flag, and
			 * then returns without rethrowing the exception.
			 *
			 * Unfortunately, this means any error messages are lost, which caused lots of
			 * confusion for others using this code. The only way to tell there was an error
			 * is to call PrintWriter.checkError().
			 */
			socket.startHandshake();

			PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())));

			out.println("Hello, waiting for client\n Inputs:\n.");
			out.println();
			out.flush();

			/*
			 * Make sure there were no surprises
			 */
			if (out.checkError())
				System.out.println("SSLSocketClient:  java.io.PrintWriter error");

			/* read response */
			BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

			String inputLine;
			while ((inputLine = in.readLine()) != null)
				System.out.println(inputLine);

			in.close();
			out.close();
			socket.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
