/** CSE 5344 – Computer Networks Project 1
 * Author:Shreyas Mohan
 * Student Id-1001669806
 */

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

//HttpRequest class handles processing for each of the client in a separate thread
//HttpRequest class implements Runnable interface and override it's public void run() method.

//Reference: Thread Tutorial from http://docs.oracle.com/javase/tutorial/essential/concurrency/runthread.html
public class HttpRequest implements Runnable {

	private Socket clientSocket; 
	private int clientID;

	private final String CRLF = "\r\n"; //carriage return line feed
	
	
	public HttpRequest(Socket client, int cID) {
		this.clientSocket = client;
		this.clientID = cID;
	}

	@Override
	public void run() {
		
		//define input and output streams
		BufferedReader socketInput = null; 
		DataOutputStream socketOutput = null; 
		
		FileInputStream fis = null; //reads the file from the local file system
		
		try {
			//get a reference to clientSocket's inputStream
			socketInput = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
			
			//get a reference to clientSocket's outputStream
			socketOutput = new DataOutputStream(clientSocket.getOutputStream());

			//read a request from socket inputStream
			String packet = socketInput.readLine();
			
			if(packet != null)
			{
				System.out.println("SERVER - CLIENT"+clientID+"-- Received a request: " + packet);

				
				//split request line based on single whitespace into three parts
				String[] msgParts = packet.split(" ");
				
				// check if the request type is GET
				if (msgParts[0].equals("GET") && msgParts.length == 3) {
					
					//now get the path of the requested file from the request
					String filePath = msgParts[1];
					
					//check if filePath starts with a forward slash "/"
					//if not, add a forward slash and make it relative to the current file path
					if(filePath.indexOf("/") != 0)
					{	//filePath does not start with a forward slash
						filePath = "/" + filePath;
					}
					
					//Display the requested file path
					System.out.println("SERVER - CLIENT"+clientID+": Requested filePath: " + filePath);
					
					//if requested filePath is null or requesting a default index file
					if(filePath.equals("/"))
					{
						System.out.println("SERVER - CLIENT"+clientID+": Respond with default index.html file");
						
						//set filePath to the default file index.html
						filePath = filePath + "server/index.html";
					
					}
				
					//make the filePath relative to the current location
					filePath = "." + filePath;

					//initialize a File object using filePath
					File file = new File(filePath);
					try {
						//check if file with filePath exists on this server
						if (file.isFile() && file.exists()) {
							
							//we are good.
							//now create a HTTP response and send it back to the client
							
							/**HTTP Response Format:
							 * HTTP/1.0 200 OK CRLF
							 * Content-type: text/html CRLF
							 * CRLF
							 * FILE_CONTENT....
							 * FILE_CONTENT....
							 * FILE_CONTENT....
							 */
							
							//write a status line on the response
							//since the requested file exists, we will send a 200 OK response
							String responseLine = "HTTP/1.0" + " " + "200" + " " + "OK" + CRLF;
							socketOutput.writeBytes(responseLine);

							//write content type header line
							socketOutput.writeBytes("Content-type: " + getContentType(filePath) + CRLF);
							
							//write a blank line representing end of response header
							socketOutput.writeBytes(CRLF);
							
							//open the requested file
							fis = new FileInputStream(file);

							// initialize a buffer of size 1K.
							byte[] buffer = new byte[1024];
							int bytes = 0;
							
							// start writing content of the requested file into the socket's output stream.
							while((bytes = fis.read(buffer)) != -1 ) {
								socketOutput.write(buffer, 0, bytes);
							}
							
							System.out.println("SERVER - CLIENT"+clientID+": Sending Response with status line: " + responseLine);
							//flush outputstream
							socketOutput.flush();
							System.out.println("SERVER - CLIENT"+clientID+": HTTP Response sent");
							
						} else {
							//The requested file does not exist on this server
							System.out.println("SERVER - CLIENT"+clientID+": ERROR: Requested filePath " + filePath + " does not exist");

							//write a status line on the response with 404 Not Found response
							String responseLine = "HTTP/1.0" + " " + "404" + " " + "Not Found" + CRLF;
							socketOutput.writeBytes(responseLine);

							//write content type header line
							socketOutput.writeBytes("Content-type: text/html" + CRLF);
							
							//write a blank line representing end of response header
							socketOutput.writeBytes(CRLF);
							
							//send content of the errorFile
							socketOutput.writeBytes(getErrorFile());
							
							System.out.println("SERVER - CLIENT"+clientID+": Sending Response with status line: " + responseLine);
							
							//flush outputstream
							socketOutput.flush();
							System.out.println("SERVER - CLIENT"+clientID+": HTTP Response sent");
						}
						
					} catch (FileNotFoundException e) {
						System.err.println("SERVER - CLIENT"+clientID+": EXCEPTION: Requested filePath " + filePath + " does not exist");
					} catch (IOException e) {
						System.err.println("SERVER - CLIENT"+clientID+": EXCEPTION in processing request." + e.getMessage());
					}
				} else {
					System.err.println("SERVER - CLIENT"+clientID+": Invalid HTTP GET Request. " + msgParts[0]);
				}
			}
			else
			{
				//While testing with the browser, I found that sometimes browser send other request like favicon etc.
				//Therefore I discard those unknown requests.
				System.err.println("SERVER - CLIENT"+clientID+": Discarding a NULL/unknown HTTP request.");
			}

		} catch (IOException e) 
		{
			System.err.println("SERVER - CLIENT"+clientID+": EXCEPTION in processing request." + e.getMessage());
			
		} finally {
			//close the resources
			try {
				if (fis != null) {
					fis.close();
				}
				if (socketInput != null) {
					socketInput.close();
				}
				if (socketOutput != null) {
					socketOutput.close();
				}
				if (clientSocket != null) {
					clientSocket.close();
					System.out.println("SERVER - CLIENT"+clientID+": Closing the connection.\n");
				}
			} catch (IOException e) {
				System.err.println("SERVER - CLIENT"+clientID+": EXCEPTION in closing resource." + e);
			}
		}
	}
	
	/**
	 * Get Content-type of the file using its extension
	 * @param filePath
	 * @return content type
	 */
	private String getContentType(String filePath)
	{
		//check if file type is html
		if(filePath.endsWith(".html") || filePath.endsWith(".htm"))
		{
			return "text/html";
		}
		//otherwise, a binary file
		return "application/octet-stream";
	}
	
	/**
	 * Get content of a general 404 error file
	 * @return errorFile content
	 */
	private String getErrorFile ()
	{
		String errorFileContent = 	"<!doctype html>" + "\n" +
									"<html lang=\"en\">" + "\n" +
									"<head>" + "\n" +
									"    <meta charset=\"UTF-8\">" + "\n" +
									"    <title>404 Error </title>" + "\n" +
									"</head>" + "\n" +
									"<body>" + "\n" +
									"    <h2>Oops!!Page Not Found!!</h2>" + "\n" +
									"    <b>ErrorCode:</b> 404" + "\n" +
									"    <br>" + "\n" +
									"    <b>Error Message:</b> The requested file does not exist on this server." + "\n" +
									"</body>" + "\n" +
									"</html>";
		return errorFileContent;
	}
}
