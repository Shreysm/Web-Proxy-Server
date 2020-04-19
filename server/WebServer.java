/**
 * CSE 5344 – Computer Networks Project 1
 * Author:Shreyas Mohan
 * Student Id-1001669806
 */

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

//WebServer class implements proxy web server and listens to client's request
//Reference: Thread Tutorial from http://docs.oracle.com/javase/tutorial/essential/concurrency/runthread.html
//Reference: Socket Communications from http://www.oracle.com/technetwork/java/socket-140484.html
//Reference: Computer Network Textbook by Kurose and Ross: Chapter 2 Socket Assignment 1
public class WebServer implements Runnable {
	
	private ServerSocket serverSocket; //Refers to the server socket
	private String serverHost; //Host name
	private int serverPort; //port where server has to start
	
	
	private final String HOST = "localhost";
	
	
	//Parameterized constructor 
	public WebServer (int port)
	{
		this.serverHost = HOST; //hostname of the server
		this.serverPort = port;
	}

	
	@Override
	public void run() {
		
		try {

			//Retrieve the inet address of the host
			InetAddress serverInet = InetAddress.getByName(serverHost);
			
			
			//now using serverInet address and serverPort, initialize serverSocket
			serverSocket = new ServerSocket(serverPort, 0, serverInet);

			System.out.println("SERVER: SERVER started at host- " + serverSocket.getInetAddress() + " port: " + serverSocket.getLocalPort() + "\n");
			
			//To differentiate process,clientID is used which acts as counter variable
			int clientID=1;
			
			//Multithread Implementation
			while(true){
				
			
				Socket clientSocket = serverSocket.accept();
				
				//Client gets connected to the Server
				System.out.println("SERVER - CLIENT:"+clientID+" Connection established with the client at " + clientSocket.getInetAddress() + ":" + clientSocket.getPort());
				
				//Handling HTTP requests in separate thread
				HttpRequest hr = new HttpRequest(clientSocket, clientID);
				
				//handover processing for the newly connected client to HttpRequest in a separate thread
				new Thread(hr).start();
				
				clientID++;
			}
			
		} catch (UnknownHostException error) {
			System.out.println("SERVER: UnknownHostException for the hostname- " + serverHost);
		} catch (IllegalArgumentException illegal) {
			System.out.println("SERVER: EXCEPTION in starting the SERVER- " + illegal.getMessage());
		}
		catch (IOException err) {
			System.out.println("SERVER: EXCEPTION in starting the SERVER- " + err.getMessage());
		}
		finally {
				try {
					if(serverSocket != null){
						serverSocket.close(); //Closing the connection
					}
				} catch (IOException error) {
					System.err.println("SERVER: EXCEPTION in closing the server socket." + error);
				}
		}
	}
		public static void main(String[] args) {

			//Initialize port number to desired port
			int port = 8081;//Default set to 8081

			System.out.println("SERVER: Using Server Port : " + port);
			
			//Calling the instance of WebServer 
			WebServer web = new WebServer(port);
			new Thread(web).start();
		}

}
