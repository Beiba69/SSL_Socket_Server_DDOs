package vyuka;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.net.ssl.SSLSocket;


public class SSLClientHandler implements Runnable {

	SSLSocket _c ;
	
	
	//Constructor
	public SSLClientHandler(SSLSocket c) {
		_c = c;
	}

	@Override
	public void run() {
		byte[] byteArr = new byte[1000];
		int num;
		InputStream is;
		OutputStream os;	
		try {
			is = _c.getInputStream();
			os = _c.getOutputStream();
			while((num = is.read(byteArr)) > 0) {
				os.write(byteArr,0,num);
				os.flush();
			}
			//closing the socket
			_c.close();
		}catch (IOException e) {
			System.out.println("Connection was closed !");
		}
		
		
	}

}
