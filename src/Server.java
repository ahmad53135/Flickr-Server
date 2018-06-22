import com.flickr4java.flickr.Flickr;
import com.flickr4java.flickr.FlickrException;
import com.flickr4java.flickr.REST;
import com.flickr4java.flickr.Transport;
import com.flickr4java.flickr.photos.Photo;
import com.flickr4java.flickr.photos.PhotoList;
import com.flickr4java.flickr.photos.PhotosInterface;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;



import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Iterator;
import java.util.List;



public class Server {

    public static String apiKey ="a4b2a5ed04bb6bee75ba4d989d175d22";
    public static String sharedSecret = "a1fe6dce56b3404c";
    public static String photoSetID = "72157695761376345";
    public static String userID = "156791166@N05";
    private static int numberOfPages = 1;
    private static int picsPerPage = 1000;


    private static int localport = 8415;

    public static String downloadedImagesFromFlickr = "/home/prg/Desktop/Flickr_Server_v2.0/Image/flickrImages/";
    private static String uploadedImagesFromServer = "/home/prg/Desktop/Flickr_Server_v2.0/Image/webImages/";

    public static double maxBytePerImage = 10350;

    //private static Thread httpsClientToServer;
    public static String receivedRequest;
    public static String serverBuffer="";

    public static String endMessageString = "\r\r\n\n";
    public static byte[] serverBufferByte;

    public static String photoTitle="";


    public static void main(String[] args) throws IOException, FlickrException {

        HttpDownloadUtility httpDownloadUtility = new HttpDownloadUtility();
        HttpDownloadUtility.downloadFile("https://www.cnn.com/", "/home/prg/Desktop/webpages/123");
        Proxy myProxy = new Proxy(8515);
        //Socket server = new Socket("localhost",8515);
        myProxy.listen();


    }

    public static void runServer() throws IOException, FlickrException {




        Socket server = null;
//        Socks5Proxy socks5Proxy = new Socks5Proxy(host,9191);
//        socks5Proxy.getPort();
        //server = new Socket("192.168.56.1",9195);
        server = new Socket("localhost",8080);





        //Encoder.load2List();
//        ServerSocket ss = new ServerSocket(localport);

//        int size = ss.getReceiveBufferSize();
        //byte[] reply = new byte[4096*2];
        final byte[] reply = new byte[(Global.imageheight * Global.imageWidth / (8 * 8)) * Global.bitsPerBlock/8];        //maximum size for image
        //final byte[] reply = new byte[1024];
        final byte[] totalReply = new byte[50*reply.length];

        final Transport tr = new REST();
        final Flickr f = new Flickr(apiKey, sharedSecret, tr);
        final List<String> clientRequests = new ArrayList<>();

        //Socket server = null;
//
//        Socks5Proxy socks5Proxy = new Socks5Proxy(host,9191);
//        socks5Proxy.getPort();
//        server = new SocksSocket(socks5Proxy,host, 9191);
        //server = new Socket(host, 9191);
        InputStream streamFromServer = server.getInputStream();
        OutputStream streamToServer = server.getOutputStream();

        while (true) {


            //server.setSoTimeout(200000);
            //Thread t = new Thread()
            ///{
            //public void run() {
            int exitFlag = 0;
            String Request = "";
            int bytesToCut = 0;
            String photoTitle = "";
            while (exitFlag == 0){
                try {
                    PhotoList list = f.getPhotosetsInterface().getPhotos(photoSetID, 1000, 1);
                    for (Iterator iterator = list.iterator(); iterator.hasNext(); ) {
                        Photo photo = (Photo) iterator.next();
                        File file2 = null;
                        if (photo.getTitle().contains("-C") && clientRequests.contains(photo.getTitle()) == false) {
                            clientRequests.add(photo.getTitle());

                            try {
                                if (server != null)
                                    server.close();
                                server = new Socket("localhost", 8515);/////////------------------------------------------------------------------------------------------------------------------------------

                            } catch (IOException e) {

                            }
                            file2 = new File(downloadedImagesFromFlickr + photo.getTitle() + ".jpg");
                            photoTitle = photo.getTitle();
                        } else
                            continue;


                        ByteArrayOutputStream b = new ByteArrayOutputStream();
                        PhotosInterface photosInterface = new PhotosInterface(apiKey, sharedSecret, tr);
                        InputStream inputStream = photosInterface.getImageAsStream(photo, 5);
                        byte[] bytes = IOUtils.toByteArray(inputStream);
                        b.write(bytes);
                        FileUtils.writeByteArrayToFile(file2, b.toByteArray());
                        byte[] msg = Decoder.image2Byte(file2.getPath());
                        //String msg = Decoder.image2Byte(file2.getPath());
                        String tmp = new String(msg,StandardCharsets.ISO_8859_1);
                        Request = tmp;
                        if(photo.getTitle().contains("2-")){
                            //serverBuffer=msg;
                        }

                        photoTitle = photo.getTitle();
                        receivedRequest = Request;
                        String urlString = Request;
                        int newLinePos = Request.indexOf(endMessageString);
                        urlString = Request.substring(0,newLinePos);
                        //int tt =
                        int secondLinePos = Request.indexOf(endMessageString,newLinePos+endMessageString.length());
                        serverBuffer = Request.substring(newLinePos+endMessageString.length(),secondLinePos);
                        serverBufferByte = new byte[secondLinePos-(newLinePos+endMessageString.length())];
                        for (int i=0; i< secondLinePos-(newLinePos+endMessageString.length()); i++){
                            serverBufferByte[i] = msg[(newLinePos+endMessageString.length()) +i];
                        }





                        if(receivedRequest.length() > 0) return;

                        if (Request.contains("CONNECT")) {      //change to Request //ToDo

                            //Thread thread = new Thread(new RequestHandler(server));

                            //thread.start();


                            RequestHandler RH = new RequestHandler(server);
                            RH.handleHTTPSRequest(urlString);


                        } else {
                            // Create the URL
                            URL remoteURL = new URL(urlString);
                            // Create a connection to remote server
                            HttpURLConnection proxyToServerCon = (HttpURLConnection) remoteURL.openConnection();
                            proxyToServerCon.setRequestProperty("User-Agent", "Mozilla/5.0 (X11; Ubuntu; Linux x86_64; rv:59.0) Gecko/20100101 Firefox/59.0");
                            proxyToServerCon.setRequestProperty("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
                            proxyToServerCon.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
                            proxyToServerCon.setRequestProperty("Accept-Encoding", "gzip, deflate");
                            //proxyToServerCon.setRequestProperty("Content-Type","application/x-www-form-urlencoded");
                            //proxyToServerCon.setRequestProperty("Content-Language", "en-US");
                            proxyToServerCon.setUseCaches(false);
                            proxyToServerCon.setDoOutput(true);

                            // Create Buffered Reader from remote Server
                            BufferedReader proxyToServerBR = new BufferedReader(new InputStreamReader(proxyToServerCon.getInputStream()));
                            String line;
                            String responseString = "";
                            while ((line = proxyToServerBR.readLine()) != null) {
                                // Send on data to client
                                responseString += line;
                                //proxyToClientBw.write(line);

                            }

                            // Close Down Resources
                            if (proxyToServerBR != null) {
                                proxyToServerBR.close();
                            }

                        exitFlag = 1;
                        Encoder.byte2Image(responseString.getBytes(), responseString.getBytes().length, photo.getTitle().split("-")[0] + "-S");
                    }
                        }


                    }
                    catch (Exception e){
                        System.out.println("error in server");
                        e.printStackTrace();
                    //ToDo
                    }


                }
        }
//                finally {
//                        try {
//                            if (server != null)
//                                server.close();
//                        } catch (IOException e) {
//                        }
//                    }
            //}
        //};

            //t.start();
//            try {
//                if (server != null)
//                    server.close();
//            } catch (IOException e) {
//            }

    }
}
