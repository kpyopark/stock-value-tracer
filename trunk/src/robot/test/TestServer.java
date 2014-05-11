package robot.test;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

public class TestServer {

	ServerSocket serverSoc = null;
	
	public TestServer() throws Exception {
		serverSoc = new ServerSocket(8080);
	}
	
	public void accept() throws Exception {
		Socket soc = serverSoc.accept();
		BufferedReader br = new BufferedReader( new InputStreamReader( soc.getInputStream() ) );
		while( br.readLine() != null ) {
			System.out.println( br.readLine() );
		}
	}
	
	public static void main(String[] args) throws Exception {
		TestServer server = new TestServer();
		server.accept();
	}
	
}
