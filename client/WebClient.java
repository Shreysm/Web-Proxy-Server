/**
 * CSE 5344 – Computer Networks Project 1
 * Author:Shreyas Mohan
 * Student Id-1001669806
 */

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.Socket;

//WebClient represents single client(or Web Browser)

//Reference: Socket Communications from http://www.oracle.com/technetwork/java/socket-140484.html
public class WebClient {

	public static void main(String[] args) {
		final String NewLineFeed = "\r\n"; //carriage return line feed
		
		
		//initialize serverPort 
	    int serverPort = 8081;
				
		//initialize filePath 
		String filePath = "/";
				
		String webServer = null;
		
		
		//check command line arguments for webServer, port, and filePath
		if(args.length == 1)
		{
			//First Argument is the host
			webServer = args[0];
		}
		else if (args.length == 2){
			//first argument is webServer
			webServer = args[0];
			
			//second can be either serverPort or filePath
			try {
				serverPort = Integer.parseInt(args[1]); //check if port is an integer
			}
			catch (NumberFormatException nfe)
			{
				System.out.println("CLIENT: Port Entered is Invalid. Default Server port will be used.");
				
				//the it is filePath
				filePath = args[1];
			}
		}
		else if (args.length == 3){
			//first argument is webServer
			webServer = args[0];
			
			//second argument is serverPort
			try {
				serverPort = Integer.parseInt(args[1]); //check if port is an integer
			}
			catch (NumberFormatException nfe)
			{
				System.out.println("CLIENT: Port Entered is Invalid. Default Server port will be used.");
			}
			
			//third argument is fileName
			filePath = args[2];
		}
		else
		{
			System.out.println("CLIENT:Invalid Input.Enter server host(mandatory),port number and file path");
			System.exit(1);//Unsuccessful Termination
		}
		System.out.println("CLIENT: HOST SERVER-"+webServer);
		System.out.println("CLIENT: Using Server Port- " + serverPort);
		System.out.println("CLIENT: Using FilePath- " + filePath);
		
		//define a socket
		Socket clientSocket = null;
		
		
		BufferedReader socketInput = null; //reads data received through the socket
		DataOutputStream socketOutStream = null; //writes data to the server
		
		FileOutputStream fos = null; //To write the contents of the file
		
		try {
			
			//get inet address of the webServer
			InetAddress webServerInet = InetAddress.getByName(webServer);
			
			//Connect to the server
			clientSocket = new Socket(webServerInet, serverPort);
			System.out.println("CLIENT: Connected to the server at " + webServer + ":" + serverPort);
			
			//get a reference to socket's inputStream
			socketInput = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
			
			//get a reference to socket's outputStream
			socketOutStream = new DataOutputStream(clientSocket.getOutputStream());

			//Send the HTTP request to the server
			String requestLine = "GET" + " " + filePath + " " +"HTTP/1.0" + NewLineFeed;
			System.out.println("CLIENT: Sending HTTP GET request- " + requestLine);
			
			//send the request
			socketOutStream.writeBytes(requestLine);
			
			//send an empty line
			socketOutStream.writeBytes(NewLineFeed);
			
			
			socketOutStream.flush();
			
			System.out.println("CLIENT: Waiting for the response from the server");
			//Interpret the response
			String responseLine = socketInput.readLine();
			System.out.println("CLIENT: Received HTTP Response with status line: " + responseLine);

			//extract content type of the response
			String contentType = socketInput.readLine();
			System.out.println("CLIENT: Received " + contentType);

			//read a blank line i.e. NewLineFeed
			socketInput.readLine();

			System.out.println("CLIENT: Received Response Body:");
			//start reading content body
			StringBuilder content = new StringBuilder();
			String line;
			while((line = socketInput.readLine()) != null)
			{
				//save content to a buffer
				content.append(line + "\n");
				System.out.println(line);
			}
			
			//get the name of the file requested
			String fileName = getFileName(content.toString());
			
			
			//file will be created if it does not exist
			fos = new FileOutputStream(fileName);
			
			fos.write(content.toString().getBytes());
			fos.flush();
			
			System.out.println("CLIENT: HTTP Response received. File Created is  " + fileName);

		} catch (IllegalArgumentException illegal) {
			System.out.println("CLIENT: Could not connect to the Server " + illegal.getMessage());
		} catch (IOException e) {
			System.out.println("CLIENT: ERROR " + e);
		}
	
			try {
				//Closing all the resources used
				if (socketInput != null) {
					socketInput.close();
				}
				if (socketOutStream != null) {
					socketOutStream.close();
				}
				if (fos != null) {
					fos.close();
				}
				if (clientSocket != null) {
					clientSocket.close();
					System.out.println("CLIENT: Closing the Connection.");
				}
			} catch (IOException error) {
				System.out.println("CLIENT: Could not close the resource. " + error);
			}
		
	}

	/**
	 * Returns a file name from the html content.
	 * Generally it is the value of the <title> tag
	 * @param content
	 * @return fileName
	 */
	private static String getFileName(String content)
	{
		//default filename if <title> tag is empty
		String filename = "";
		
		filename = content.substring(content.indexOf("<title>")+("<title>").length(), content.indexOf("</title>"));
		
		if(filename.equals(""))
		{
			filename = "index";
		}
		
		filename = filename+".html";
		
		return filename;
	}
}
