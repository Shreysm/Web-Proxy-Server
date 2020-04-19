
# * CSE 5344 – Computer Networks Project 1 Web Proxy Server
# * Author:Shreyas Mohan
 
 

-----------
A Java implementation of Simple HTTP Web Client(/Web Browser) and a Multithreaded Web Server.

### Development Tools:    
1. **Programming Language:** Java (jdk 11.0.1)
2. **IDE:** Eclipse Photon
4. **OS:** Windows 10
5. **Command Line Interface:** Windows command prompt used to run/test the program

### Directory Structure
1. **server:** Contains Web Server implementation  
    * `WebServer.java`: Implements a mulithreaded server and initializes a serverSocket to listens to the client requests. Once a client is connected, the processing is handed over to a separate thread HttpRequest.Default Port number is set to 8081
    * `HttpRequest.java`: Request is handled by this method in a separate thread
    * `index.html`: A default html file which is sent to the client in case a GET request contains "/" filepath i.i if no file path is mentioned.
    * `demo2.html`: A additional html file which can be requested to display.
	

2. **client:** Contains Client Implementation
    * `WebClient.java`: Implements a web client which sends a HTTP request to the server. It mentions the host name,port number and path to be requested.
    
### Compile & Run Instructions (Run on Eclipse):

1. Compile and execute WebServer.java (Port is set to 8081)
2. Compile and execute WebClient.java
   You can pass the arguments in the following fashion-
   i. hostname (localhost)
   ii.hostname portNumber (localhost 8081)
   iii. hostname portnumebr filePath (localhost 8081 server/demo2.html)
3. You can also send the request through web browser in the following manner-
    i. localhost:8081
    ii.localhost:8081/demo2.html
    
    Cached files are stored in the main folder.
    
### Compile & Run Instructions (Run on Windows cmd prompt):

1. Compile all server and client code located in separate directories.  
	cd server    
	javac *.java      
	cd client     
	javac *.java      

3. Run web server in server directory. 

	java WebServer


4. Run web client in client directory by passing at least one argument i.e. serverHost/IP address. Other optional arguments are port and the path of the file to request from the server.

	*       java WebClient localhost

	*       java WebClient localhost 8081
    
	*      java WebClient localhost server/demo2.html




5. All requested file paths must be relative to the `HttpRequest.java` class. If not, 404 error will be returned.


### References

1. Book: Computer Networking. A Top Down Approach. Fifth Edition by James F. Kurose, Keith W. Ross. Chapter 2.
2. Thread Tutorial from [Oracle](http://docs.oracle.com/javase/tutorial/essential/concurrency/runthread.html)
3.Reference: Computer Network Textbook by Kurose and Ross: Chapter 2 Socket Assignment 1
4. Socket Communications from [Oracle](http://www.oracle.com/technetwork/java/socket-140484.html)
5. https://www.geeksforgeeks.org/socket-programming-in-java/
6. https://www.binarytides.com/java-socket-programming-tutorial/
