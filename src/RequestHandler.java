import com.flickr4java.flickr.FlickrException;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.List;

public class RequestHandler implements Runnable {

	/**
	 * Socket connected to client passed by Proxy server
	 */
	Socket clientSocket;

	/**
	 * Read data client sends to proxy
	 */
	BufferedReader proxyToClientBr;

	/**
	 * Send data from proxy to client
	 */
	BufferedWriter proxyToClientBw;
	

	/**
	 * Thread that is used to transmit data read from client to server when using HTTPS
	 * Reference to this is required so it can be closed once completed.
	 */
	private Thread httpsClientToServer;
    public static String endMessageString = "\r\r\n\n";


	/**
	 * Creates a ReuqestHandler object capable of servicing HTTP(S) GET requests
	 * @param clientSocket socket connected to the client
	 */
	public RequestHandler(Socket clientSocket){
		System.out.println("RequestHandler\n\n");
		this.clientSocket = clientSocket;
        /*try {
            Server.runServer();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (FlickrException e) {
            e.printStackTrace();
        }*/

		try{

			this.clientSocket.setSoTimeout(2000);
			proxyToClientBr = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
			proxyToClientBw = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));
		} 
		catch (IOException e) {
			e.printStackTrace();
		}
	}


	
	/**
	 * Reads and examines the requestString and calls the appropriate method based 
	 * on the request type. 
	 */
	@Override
	public void run() {

		// Get Request from client
		String requestString;
		System.out.println("RequestHandler RUN \n\n");
		//try{
			//requestString = proxyToClientBr.readLine();

        requestString = Server.receivedRequest;
		/*} catch (IOException e) {
			//e.printStackTrace();
			System.out.println("Error reading request from client");
			return;
		}*/

		// Parse out URL

		System.out.println("Request Received " + requestString);
		/*
		// Get the Request type
		String request = requestString.substring(0,requestString.indexOf(' '));

		// remove request type and space
		String urlString = requestString.substring(requestString.indexOf(' ')+1);

		// Remove everything past next space
		urlString = urlString.substring(0, urlString.indexOf(' '));

		// Prepend http:// if necessary to create correct URL
		if(!urlString.substring(0,4).equals("http")){
			String temp = "http://";
			urlString = temp + urlString;\


		}

*/

        int newLinePos = requestString.indexOf(Server.endMessageString);
        String urlString = requestString.substring(0,newLinePos);
        Server.receivedRequest = requestString.substring(newLinePos+Server.endMessageString.length());


		// Check request type
		if(requestString.contains("CONNECT")){


			System.out.println("HTTPS Request for : " + urlString + "\n");
			String url = urlString.replaceFirst("CONNECT ", "");
			//handleHTTPSRequest(urlString);
            handleHTTPSRequest(url);
		} 

		else{

			System.out.println("HTTP GET for : " + urlString + "\n");
			sendNonCachedToClient(urlString);

		}
	} 



	/**
	 * Sends the contents of the file specified by the urlString to the client
	 * @param urlString URL ofthe file requested
	 */
	private void sendNonCachedToClient(String urlString){
		System.out.println("sendNonCachedToClient\n\n");

		try{

			String fileExtension=""; ////Temporary
			// Check if file is an image
			if((fileExtension.contains(".png")) || fileExtension.contains(".jpg") ||
					fileExtension.contains(".jpeg") || fileExtension.contains(".gif")){
				// Create the URL
				/*URL remoteURL = new URL(urlString);
				BufferedImage image = ImageIO.read(remoteURL);

				if(image != null) {
					// Cache the image to disk
					//ImageIO.write(image, fileExtension.substring(1), fileToCache);

					// Send response code to client
					String line = "HTTP/1.0 200 OK\n" +
							"Proxy-agent: ProxyServer/1.0\n" +
							"\r\n";
					proxyToClientBw.write(line);
					proxyToClientBw.flush();

					// Send them the image data
					ImageIO.write(image, fileExtension.substring(1), clientSocket.getOutputStream());

				// No image received from remote server
				} else {
					System.out.println("Sending 404 to client as image wasn't received from server"
							+ fileName);
					String error = "HTTP/1.0 404 NOT FOUND\n" +
							"Proxy-agent: ProxyServer/1.0\n" +
							"\r\n";
					proxyToClientBw.write(error);
					proxyToClientBw.flush();
					return;
				}*/
			}

			// File is a text file
			else {

				//Global.imageCounter++;
				//Encoder.byte2Image(urlString.getBytes(), urlString.getBytes().length,Integer.toString(Global.imageCounter)+"-S");


				// Create the URL
				URL remoteURL = new URL(urlString);
				// Create a connection to remote server
				HttpURLConnection proxyToServerCon = (HttpURLConnection)remoteURL.openConnection();
				proxyToServerCon.setRequestProperty("Content-Type", 
						"application/x-www-form-urlencoded");
				proxyToServerCon.setRequestProperty("Content-Language", "en-US");  
				proxyToServerCon.setUseCaches(false);
				proxyToServerCon.setDoOutput(true);
			
				// Create Buffered Reader from remote Server
				BufferedReader proxyToServerBR = new BufferedReader(new InputStreamReader(proxyToServerCon.getInputStream()));



				// Send success code to client
                //not necessary in the server side
				/*String line = "HTTP/1.0 200 OK\n" +
						"Proxy-agent: ProxyServer/1.0\n" +
						"\r\n";


				proxyToClientBw.write(line);
				//Client.runProxy(this.clientSocket);
				proxyToClientBw.flush();
				*/


				// Read from input stream between proxy and remote server
                String line="";
                String tmpLine = "";
				while((tmpLine = proxyToServerBR.readLine()) != null){
					// Send on data to client

					//proxyToClientBw.write(line);  //instead we write in line

                    line += tmpLine;

					// Write to our cached copy of the file
					//if(caching){
					//	fileToCacheBW.write(line);
					//}
				}

				line += endMessageString;
				
				// Ensure all data is sent by this point
				proxyToClientBw.flush();

                Global.imageCounter++;
                Encoder.byte2Image(line.getBytes(), line.getBytes().length,Integer.toString(Global.imageCounter)+"-S");


				// Close Down Resources
				if(proxyToServerBR != null){
					proxyToServerBR.close();
				}
			}

			if(proxyToClientBw != null){
				proxyToClientBw.close();
			}

		} 

		catch (Exception e){
			e.printStackTrace();
		}
	}

	
	/**
	 * Handles HTTPS requests between client and remote server
	 * @param urlString desired file to be transmitted over https
	 */
	public void handleHTTPSRequest(String urlString){

		System.out.println("handleHTTPSRequest\n\n");

        URL aURL = null;
        try {
            aURL = new URL(urlString);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }


		/*String Connect = "CONNECT";
		int l1 = urlString.indexOf(Connect);
		int l2 = urlString.indexOf(":");

		//String url = urlString.substring(l1+Connect.length(),l2);
		// Extract the URL and port of remote 
		//String url = urlString.substring(7);

        //ToDo

        */

		String url = aURL.getHost();
		int port = aURL.getPort();
        /*String url = urlString.substring(7);
		//url.trim();
		url = url.trim();
		String pieces[] = url.split(":");
		url = pieces[0];
		int port  = Integer.valueOf(pieces[1]);*/

		try{
			// Only first line of HTTPS request has been read at this point (CONNECT *)
			// Read (and throw away) the rest of the initial data on the stream
			String str="";
			/*for(int i=0;i<5 ;i++){
				str += proxyToClientBr.readLine();
				System.out.println(str);
				str += "\r\n";
				if(str.equals("")){
					break;
				}
			}*/

            /*Global.imageCounter++;
            Encoder.byte2Image(str.getBytes(), str.getBytes().length,Integer.toString(Global.imageCounter)+"-C");*/

            //Global.imageCounter++;
            //Encoder.byte2Image(urlString.getBytes(), urlString.getBytes().length,Integer.toString(Global.imageCounter)+"-C");


			// Get actual IP associated with this URL through DNS
			InetAddress address = InetAddress.getByName(url);

            System.out.println("IP ADDRESS= "+address.toString());

            Socket proxyToServerSocket = null;
            if(Server.socketList.isEmpty()) {
                // Open a socket to the remote server
                proxyToServerSocket = new Socket(address, port);
                proxyToServerSocket.setSoTimeout(95000);
                if (proxyToServerSocket.getKeepAlive()) {
                    System.out.println("alive");
                }
                Server.socketList.add(proxyToServerSocket);
            }
            else{
                proxyToServerSocket = Server.socketList.get(0);
                if(proxyToServerSocket.isClosed()){
                    System.out.println("socket is closed now");
                }
                System.out.println(proxyToServerSocket.toString());
            }


			// Send Connection established to the client
//			String line = "HTTP/1.0 200 Connection established\r\n" +
//					"Proxy-Agent: ProxyServer/1.0\r\n" +
//					"\r\n";
//			proxyToClientBw.write(line);
//			proxyToClientBw.flush();
			
			
			
			// Client and Remote will both start sending data to proxy at this point
			// Proxy needs to asynchronously read data from each party and send it to the other party


			//Create a Buffered Writer betwen proxy and remote
			BufferedWriter proxyToServerBW = new BufferedWriter(new OutputStreamWriter(proxyToServerSocket.getOutputStream()));

			// Create Buffered Reader from proxy and remote
			BufferedReader proxyToServerBR = new BufferedReader(new InputStreamReader(proxyToServerSocket.getInputStream()));



			// Create a new thread to listen to client and transmit to server
			ClientToServerHttpsTransmit clientToServerHttps = 
					new ClientToServerHttpsTransmit(/*clientSocket.getInputStream(),*/ proxyToServerSocket.getOutputStream());
			
			httpsClientToServer = new Thread(clientToServerHttps);
			httpsClientToServer.start();
			
			
			// Listen to remote server and relay to client
			try {
				byte[] buffer = new byte[4096];
				int read;
				int cnt=0;
				do {
					read = proxyToServerSocket.getInputStream().read(buffer);
					if (read > 0) {
					    byte[] tmp = new byte[read+endMessageString.getBytes().length];

                        System.arraycopy(buffer, 0, tmp, 0, read);
                        System.arraycopy(endMessageString.getBytes(), 0, tmp, read, endMessageString.getBytes().length);
						System.out.println("Encoding part");
						//Encoder.byte2Image(buffer, read,Integer.toString(Global.imageCounter)+"-C");
                        Encoder.byte2Image(tmp, read+endMessageString.getBytes().length, Server.photoTitle.split("-")[0] + "-S");
                        cnt++;
						//clientSocket.getOutputStream().write(buffer, 0, read);
						if (proxyToServerSocket.getInputStream().available() < 1) {
							//clientSocket.getOutputStream().flush();

						}
					}
				} while (read >= 0);
			}
			catch (SocketTimeoutException e) {
				
			}
			catch (IOException e) {
				e.printStackTrace();
			}


			// Close Down Resources
			if(proxyToServerSocket != null){
				proxyToServerSocket.close();
			}

			if(proxyToServerBR != null){
				proxyToServerBR.close();
			}

			if(proxyToServerBW != null){
				proxyToServerBW.close();
			}

			if(proxyToClientBw != null){
				proxyToClientBw.close();
			}
			
			
		} catch (SocketTimeoutException e) {
			String line = "HTTP/1.0 504 Timeout Occured after 10s\n" +
					"User-Agent: ProxyServer/1.0\n" +
					"\r\n";
			try{
				proxyToClientBw.write(line);
				proxyToClientBw.flush();
			} catch (IOException ioe) {
				ioe.printStackTrace();
			}
		} 
		catch (Exception e){
			System.out.println("Error on HTTPS : " + urlString );
			e.printStackTrace();
		}
	}

	


	/**
	 * Listen to data from client and transmits it to server.
	 * This is done on a separate thread as must be done 
	 * asynchronously to reading data from server and transmitting 
	 * that data to the client. 
	 */
	class ClientToServerHttpsTransmit implements Runnable {
		
		InputStream proxyToClientIS;
		OutputStream proxyToServerOS;
		
		/**
		 * Creates Object to Listen to Client and Transmit that data to the server
		 * @param proxyToClientIS Stream that proxy uses to receive data from client
		 * @param proxyToServerOS Stream that proxy uses to transmit data to remote server
		 */
		public ClientToServerHttpsTransmit(/*InputStream proxyToClientIS,*/ OutputStream proxyToServerOS) {
			this.proxyToClientIS = proxyToClientIS;
			this.proxyToServerOS = proxyToServerOS;
			System.out.println("ClientToServerHttpsTransmit\n\n");
		}

		@Override
		public void run(){
			try {
				System.out.println("Client To Server RUN\n\n");
				// Read byte by byte from client and send directly to server
				byte[] buffer = new byte[4096];
				int read;
				do {
					//read = proxyToClientIS.read(buffer);
                    read = Server.serverBuffer.length();        //ClientHello
                    //System.out.println("Client To Server RUN-inside while loop  read="+read+"\n\n");
					if (read > 0) {
                        System.out.println(read);
						//proxyToServerOS.write(buffer, 0, read);
                        proxyToServerOS.write(Server.serverBufferByte, 0, Server.serverBufferByte.length);
                        System.out.println("Finally Write");
                        Server.serverBuffer="";
						//System.out.println("ClientToServer: "+ new String(buffer)+"\n\n\n");
						/*if (proxyToClientIS.available() < 1) {
							proxyToServerOS.flush();
						}*/
					}
				} while (read >= 0);
			}
			catch (SocketTimeoutException ste) {
				// TODO: handle exception
			}
			catch (IOException e) {
				System.out.println("Proxy to client HTTPS read timed out");
				e.printStackTrace();
			}
		}
	}



}




