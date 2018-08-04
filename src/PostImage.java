import javax.net.ssl.HttpsURLConnection;
import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;
import com.splunk.HttpService;
import com.splunk.SSLSecurityProtocol;


/**
 * Created by Ahmad on 7/23/2017.
 */
public class PostImage {
    private HttpsURLConnection conn;
    private String imageName;
    private int sleepTime = 200; //miliseconds
    private String url;
    private String imagePath;
    private String ticket;

    public static void main(String[] args) throws Exception {
        //PostImage postImage = new PostImage();
        //postImage.sendPost("https://up.flickr.com/photos/upload/transfer/","C:\\Users\\Ahmad\\Desktop\\Jellyfish.jpg","1");

    }
    public void sendPost(String url, String imagePath, String imageName) throws Exception {




        long sendImageStart = System.currentTimeMillis();

        if(imagePath==null){
            return;
        }

        this.imageName = imageName;
        this.url = url;
        this.imagePath=imagePath;

        HttpService.setSslSecurityProtocol( SSLSecurityProtocol.TLSv1_2 );
        System.setProperty("http.proxyHost", "127.0.0.1");
        System.setProperty("http.proxyPort", "8080");
        System.setProperty("https.proxyHost","127.0.0.1");
        System.setProperty("https.proxyPort", "8080");

//        URL oracle = new URL("http://www.varzesh3.com/");
//        URLConnection yc = oracle.openConnection();
//        BufferedReader in = new BufferedReader(new InputStreamReader(
//                yc.getInputStream()));
//        String inputLine;
//        while ((inputLine = in.readLine()) != null)
//            System.out.println(inputLine);
//        in.close();


        String posturl = "https://up.flickr.com/photos/upload/transfer/";
        String boundary = "---------------------------10980066661674643583482671613";

        String crlf = "\r\n";
        String twoHyphens = "--";


        URL obj = new URL(posturl);
        conn = (HttpsURLConnection) obj.openConnection();
        conn.setRequestProperty("Host", "www.up.flickr.com");
        conn.setRequestMethod("POST");
        conn.setInstanceFollowRedirects(false);
        // Acts like a browser
        //conn.setUseCaches(false);

        conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:54.0) Gecko/20100101 Firefox/54.0");
        conn.setRequestProperty("Accept","text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
        conn.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
        conn.setRequestProperty("Accept-Encoding","gzip, deflate, br");

        conn.setRequestProperty("Content-Type","multipart/form-data; "+"boundary="+boundary);
        conn.setRequestProperty("Content-Length", "776765");
        conn.setRequestProperty("Referer", "https://www.flickr.com/photos/upload/basic");
        String cookies = "BX=57qvh7hc2pv0e&b=4&d=AYSLfZlpYEJDUw8U96du.xPtO10-&s=ng&i=VQ6rcY681aPbLb4RB4.3; xb=697742; vp=929%2C328%2C1%2C17%2Cphotolist-container%3A800%2Ctag-photos-everyone-view%3A800%2Cshowcase-container%3A640; localization=en-us%3Bus%3Bus; flrbp=1500845239-fd30959040f8d6a2742e27e03ab620e8dfa5878b; flrbs=1500845239-2f0b2d88131795d83f29e89707edc685973e5edb; flrbgrp=1500845239-59cfeb614c03509fe78220e6051d97a399342e37; flrbgdrp=1500845239-7d32670fce0e5137fc965cf6da703586f35648d2; flrbgmrp=1500845239-cc74c61a9beeed57ce6a04545808a767b922ab16; flrbcr=1500845239-1a9c902c8d8547cf3da1433dd30ac12e6cadbce5; flrbrst=1500845239-947446765277b9496a8424b0398769a2a4b52b0e; flrtags=1500845239-b2ef419020b13ed42193930bf0ca8134de9935a7; flrbrp=1500845239-a9a750226976e1a3cef3a0bdd98d65ec2cb625b5; ffs=156785826-558; cookie_accid=156785826; cookie_epass=65bb9834df69902fd9b190a16f1fab44; sa=1505832248%3A156791166%40N05%3A0e14600a850b63e056fa58b02b586792; flrbfd=1500845239-6ad6a36b94b611ca04436ae55f2bf7e48207f06b; cookie_session=156785826%3A65bb9834df69902fd9b190a16f1fab44; flrb=47; RT=s=1500845241621&u=&r=https%3A//www.flickr.com/photos/upload/";

        conn.setRequestProperty("Cookie",cookies);
        conn.setRequestProperty("Connection", "keep-alive");
        conn.setRequestProperty("Upgrade-Insecure-Requests","1");


        String str = conn.getRequestMethod();

        conn.setDoOutput(true);
        conn.setDoInput(true);
        DataOutputStream request = new DataOutputStream(conn.getOutputStream());
       // conn.setRequestMethod("POST");

        request.writeBytes("--"+boundary+crlf);
        request.writeBytes("Content-Disposition: form-data; name=\"done\"" + crlf+crlf + "1"+crlf);

        request.writeBytes("--"+boundary+crlf);
        request.writeBytes("Content-Disposition: form-data; name=\"complex_perms\"" + crlf+crlf + "0"+crlf);

        request.writeBytes("--"+boundary+crlf);
        request.writeBytes("Content-Disposition: form-data; name=\"magic_cookie\"" + crlf+crlf +
                "a0a111e21ff2ad8b2d0e9bed4a17e29f75d9701fd7fa583f2855cbfbb5e8e5f8"+crlf);

        request.writeBytes("--"+boundary+crlf);
        request.writeBytes("Content-Disposition: form-data; name=\"file1\"; filename=\"" +
                "1.jpg\""+crlf +
                "Content-Type: image/jpeg"+crlf+crlf);

        FileInputStream inputStream = new FileInputStream(imagePath);
        byte[] buffer = new byte[4096];
        int bytesRead = -1;
        while ((bytesRead = inputStream.read(buffer)) != -1) {
            request.write(buffer, 0, bytesRead);
        }
        request.flush();
        inputStream.close();

        String upload =  "-----------------------------10980066661674643583482671613" +crlf+
                "Content-Disposition: form-data; name=\"tags\"" +crlf+
                crlf+
                crlf+
                "-----------------------------10980066661674643583482671613" +crlf+
                "Content-Disposition: form-data; name=\"is_public_0\"" +crlf+
                crlf+
                "1" +crlf+
                "-----------------------------10980066661674643583482671613" +crlf+
                "Content-Disposition: form-data; name=\"safety_level\"" +crlf+
                crlf+
                "0" +crlf+
                "-----------------------------10980066661674643583482671613" +crlf+
                "Content-Disposition: form-data; name=\"content_type\"" +crlf+
                crlf+
                "0" +crlf+
                "-----------------------------10980066661674643583482671613" +crlf+
                "Content-Disposition: form-data; name=\"Submit\"" +crlf+
                crlf+
                "UPLOAD" +crlf+
                "-----------------------------10980066661674643583482671613--";

        String location = null;
        while (location == null) {
            request.writeBytes(upload);
            location = conn.getHeaderField("location");
            // Map<String, List<String>> map = conn.getHeaderFields();
        }
        int index = location.indexOf("?i=");
        String i = location.substring(index);

        System.out.println("\nSending 'POST' request to URL : " + url);
        TimeUnit.MILLISECONDS.sleep(sleepTime);
        getTickets(i);
        System.out.println("SendImageDuration="+Long.toString(System.currentTimeMillis() - sendImageStart));
    }

    public void getTickets(String i) throws IOException, InterruptedException {


        HttpService.setSslSecurityProtocol( SSLSecurityProtocol.TLSv1_2 );
        System.setProperty("http.proxyHost", "127.0.0.1");
        System.setProperty("http.proxyPort", "8080");
        System.setProperty("https.proxyHost","127.0.0.1");
        System.setProperty("https.proxyPort", "8080");

        String geturl = "https://up.flickr.com/photos/upload/process/"+i;

        URL obj = new URL(geturl);
        conn = (HttpsURLConnection) obj.openConnection();
        conn.setRequestProperty("Host", "www.flickr.com");
        conn.setRequestMethod("GET");
        conn.setInstanceFollowRedirects(false);
        conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:54.0) Gecko/20100101 Firefox/54.0");
        conn.setRequestProperty("Accept","text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
        conn.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
        conn.setRequestProperty("Accept-Encoding","gzip, deflate, br");
        conn.setRequestProperty("Referer","https://www.flickr.com/photos/upload/basic");

        String cookies = "BX=57qvh7hc2pv0e&b=4&d=AYSLfZlpYEJDUw8U96du.xPtO10-&s=ng&i=VQ6rcY681aPbLb4RB4.3; xb=697742; vp=929%2C328%2C1%2C17%2Cphotolist-container%3A800%2Ctag-photos-everyone-view%3A800%2Cshowcase-container%3A640; localization=en-us%3Bus%3Bus; flrbp=1500845239-fd30959040f8d6a2742e27e03ab620e8dfa5878b; flrbs=1500845239-2f0b2d88131795d83f29e89707edc685973e5edb; flrbgrp=1500845239-59cfeb614c03509fe78220e6051d97a399342e37; flrbgdrp=1500845239-7d32670fce0e5137fc965cf6da703586f35648d2; flrbgmrp=1500845239-cc74c61a9beeed57ce6a04545808a767b922ab16; flrbcr=1500845239-1a9c902c8d8547cf3da1433dd30ac12e6cadbce5; flrbrst=1500845239-947446765277b9496a8424b0398769a2a4b52b0e; flrtags=1500845239-b2ef419020b13ed42193930bf0ca8134de9935a7; flrbrp=1500845239-a9a750226976e1a3cef3a0bdd98d65ec2cb625b5; ffs=156785826-558; cookie_accid=156785826; cookie_epass=65bb9834df69902fd9b190a16f1fab44; sa=1505832248%3A156791166%40N05%3A0e14600a850b63e056fa58b02b586792; flrbfd=1500845239-6ad6a36b94b611ca04436ae55f2bf7e48207f06b; cookie_session=156785826%3A65bb9834df69902fd9b190a16f1fab44; flrb=47; RT=s=1500845241621&u=&r=https%3A//www.flickr.com/photos/upload/";

        conn.setRequestProperty("Cookie",cookies);
        conn.setRequestProperty("Connection", "close");
        conn.setRequestProperty("Upgrade-Insecure-Requests","1");

        String str = conn.getRequestMethod();
        conn.setDoOutput(true);
        conn.setDoInput(true);

        BufferedReader in = null;
        while (in == null) {
            in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        }
        //BufferedReader in =new BufferedReader(new InputStreamReader(conn.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();

        int firstIndex_tickets = response.indexOf("tickets");
        int secondIndex_tickets = response.indexOf("'",firstIndex_tickets );
        int thirdIndex_tickets = response.indexOf("'",secondIndex_tickets+1 );
        String tickets = response.substring(secondIndex_tickets+1,thirdIndex_tickets);

        int firstIndex_fails = response.indexOf("fails");
        int secondIndex_fails = response.indexOf("'",firstIndex_fails );
        int thirdIndex_fails = response.indexOf("'",secondIndex_fails+1 );
        String fails = response.substring(secondIndex_fails+1,thirdIndex_fails);


        int firstIndex_oks = response.indexOf("oks");
        int secondIndex_oks = response.indexOf("'",firstIndex_oks );
        int thirdIndex_oks = response.indexOf("'",secondIndex_oks+1 );
        String oks = response.substring(secondIndex_oks+1,thirdIndex_oks);

        TimeUnit.MILLISECONDS.sleep(sleepTime);
        this.ticket = tickets;
        getOKS(tickets,fails,oks,i);


    }

    public void getOKS(String tickets, String fails, String oks,String i) throws IOException, InterruptedException {
        HttpService.setSslSecurityProtocol( SSLSecurityProtocol.TLSv1_2 );
        System.setProperty("http.proxyHost", "127.0.0.1");
        System.setProperty("http.proxyPort", "8080");
        System.setProperty("https.proxyHost","127.0.0.1");
        System.setProperty("https.proxyPort", "8080");

        String geturl = "https://www.flickr.com/photos_upload_rs_check.gne?oks="+oks+"&fails="+fails+"&tickets="+tickets;

        System.out.println("getOKS---------------------------------");

        URL obj = new URL(geturl);
        conn = (HttpsURLConnection) obj.openConnection();
        conn.setRequestProperty("Host", "www.flickr.com");
        conn.setRequestMethod("GET");
        conn.setInstanceFollowRedirects(false);
        conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:54.0) Gecko/20100101 Firefox/54.0");
        conn.setRequestProperty("Accept","text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
        conn.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
        conn.setRequestProperty("Accept-Encoding","gzip, deflate, br");
        conn.setRequestProperty("Referer","https://www.flickr.com/photos/upload/process/"+i);

        String cookies = "BX=57qvh7hc2pv0e&b=4&d=AYSLfZlpYEJDUw8U96du.xPtO10-&s=ng&i=VQ6rcY681aPbLb4RB4.3; xb=697742; vp=929%2C328%2C1%2C17%2Cphotolist-container%3A800%2Ctag-photos-everyone-view%3A800%2Cshowcase-container%3A640; localization=en-us%3Bus%3Bus; flrbp=1500845239-fd30959040f8d6a2742e27e03ab620e8dfa5878b; flrbs=1500845239-2f0b2d88131795d83f29e89707edc685973e5edb; flrbgrp=1500845239-59cfeb614c03509fe78220e6051d97a399342e37; flrbgdrp=1500845239-7d32670fce0e5137fc965cf6da703586f35648d2; flrbgmrp=1500845239-cc74c61a9beeed57ce6a04545808a767b922ab16; flrbcr=1500845239-1a9c902c8d8547cf3da1433dd30ac12e6cadbce5; flrbrst=1500845239-947446765277b9496a8424b0398769a2a4b52b0e; flrtags=1500845239-b2ef419020b13ed42193930bf0ca8134de9935a7; flrbrp=1500845239-a9a750226976e1a3cef3a0bdd98d65ec2cb625b5; ffs=156785826-558; cookie_accid=156785826; cookie_epass=65bb9834df69902fd9b190a16f1fab44; sa=1505832248%3A156791166%40N05%3A0e14600a850b63e056fa58b02b586792; flrbfd=1500845239-6ad6a36b94b611ca04436ae55f2bf7e48207f06b; cookie_session=156785826%3A65bb9834df69902fd9b190a16f1fab44; flrb=47; RT=s=1500845241621&u=&r=https%3A//www.flickr.com/photos/upload/";

        conn.setRequestProperty("Cookie",cookies);
        conn.setRequestProperty("Connection", "close");


        String str = conn.getRequestMethod();
        conn.setDoOutput(true);
        conn.setDoInput(true);

        BufferedReader in = null;
        while (in == null) {
             in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        }
        String inputLine;
        StringBuffer response = new StringBuffer();


        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }

        int firstIndex = response.indexOf("|");
        int secondIndex = response.indexOf("|",firstIndex+1);
        String oks2 = response.substring(firstIndex+1,secondIndex);

        in.close();

        TimeUnit.MILLISECONDS.sleep(sleepTime);
        getImageID(oks2,fails,i);

    }

    public void getImageID(String oks, String fails, String i) throws IOException, InterruptedException {

        String crlf = "\r\n";

        HttpService.setSslSecurityProtocol( SSLSecurityProtocol.TLSv1_2 );
        System.setProperty("http.proxyHost", "127.0.0.1");
        System.setProperty("http.proxyPort", "8080");
        System.setProperty("https.proxyHost","127.0.0.1");
        System.setProperty("https.proxyPort", "8080");

        System.out.println("getImageID---------------------------------");


        String postUrl = "https://www.flickr.com/photos/upload/edit";
        URL obj = new URL(postUrl);
        conn = (HttpsURLConnection) obj.openConnection();
        conn.setRequestProperty("Host", "www.flickr.com");
        conn.setRequestMethod("POST");
        conn.setInstanceFollowRedirects(false);
        conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:54.0) Gecko/20100101 Firefox/54.0");
        conn.setRequestProperty("Accept","text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
        conn.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
        conn.setRequestProperty("Accept-Encoding","gzip, deflate, br");
        conn.setRequestProperty("Referer","https://www.flickr.com/photos/upload/process/"+i);

        String cookies = "BX=57qvh7hc2pv0e&b=4&d=AYSLfZlpYEJDUw8U96du.xPtO10-&s=ng&i=VQ6rcY681aPbLb4RB4.3; xb=697742; vp=929%2C328%2C1%2C17%2Cphotolist-container%3A800%2Ctag-photos-everyone-view%3A800%2Cshowcase-container%3A640; localization=en-us%3Bus%3Bus; flrbp=1500845239-fd30959040f8d6a2742e27e03ab620e8dfa5878b; flrbs=1500845239-2f0b2d88131795d83f29e89707edc685973e5edb; flrbgrp=1500845239-59cfeb614c03509fe78220e6051d97a399342e37; flrbgdrp=1500845239-7d32670fce0e5137fc965cf6da703586f35648d2; flrbgmrp=1500845239-cc74c61a9beeed57ce6a04545808a767b922ab16; flrbcr=1500845239-1a9c902c8d8547cf3da1433dd30ac12e6cadbce5; flrbrst=1500845239-947446765277b9496a8424b0398769a2a4b52b0e; flrtags=1500845239-b2ef419020b13ed42193930bf0ca8134de9935a7; flrbrp=1500845239-a9a750226976e1a3cef3a0bdd98d65ec2cb625b5; ffs=156785826-558; cookie_accid=156785826; cookie_epass=65bb9834df69902fd9b190a16f1fab44; sa=1505832248%3A156791166%40N05%3A0e14600a850b63e056fa58b02b586792; flrbfd=1500845239-6ad6a36b94b611ca04436ae55f2bf7e48207f06b; cookie_session=156785826%3A65bb9834df69902fd9b190a16f1fab44; flrb=47; RT=s=1500845241621&u=&r=https%3A//www.flickr.com/photos/upload/";

        conn.setRequestProperty("Cookie",cookies);
        conn.setRequestProperty("Connection", "close");
        conn.setRequestProperty("Upgrade-Insecure-Requests","1");

        conn.setDoOutput(true);
        conn.setDoInput(true);

        DataOutputStream request = new DataOutputStream(conn.getOutputStream());

        request.writeBytes(crlf);
        String payload = "tickets=&oks="+oks+"&fails="+fails;
        request.writeBytes(payload);
        request.flush();

        //TimeUnit.MILLISECONDS.sleep(10000);
        String str = conn.getRequestMethod();
        conn.getHeaderFields();

        //System.out.println("before buffer");
        //TimeUnit.MILLISECONDS.sleep(1000);
        BufferedReader in = null;
        while (in == null) {
            in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        }
        //BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));

        //System.out.println("after buffer");
        String inputLine;
        StringBuffer response = new StringBuffer();

        while (((inputLine = in.readLine()) != null) ) {
            response.append(inputLine);
        }


        String searchStr_id = "id=\"upload_ids\" value=\"";

        int firstIndex_id = response.indexOf(searchStr_id);

        int secondIndex_id = response.indexOf("\"",firstIndex_id+searchStr_id.length());
        if(firstIndex_id < 0 || secondIndex_id < 0){
            System.out.println("---------------------ErrorTime---------------------------------------");
            try {
                //sendPost(this.url,this.imagePath,this.imageName);
                getOKS( this.ticket,  fails,  oks, i);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        String upload_id = response.substring(firstIndex_id+searchStr_id.length(),secondIndex_id);

        String searchStr_magic = "magic_cookie=";
        int firstIndex_magic = response.indexOf("magic_cookie=");
        int secondIndex_magic = response.indexOf("\"",firstIndex_magic+searchStr_magic.length());

        String magic_cookie = response.substring(firstIndex_magic+searchStr_magic.length(),secondIndex_magic);
        in.close();
        TimeUnit.MILLISECONDS.sleep(sleepTime);
        upload2Set(upload_id,magic_cookie);
    }

    public void upload2Set(String uploadID, String magic_cookie) throws IOException {
        System.out.println("upload2Set---------------------------------");
        String crlf = "\r\n";

        HttpService.setSslSecurityProtocol( SSLSecurityProtocol.TLSv1_2 );
        System.setProperty("http.proxyHost", "127.0.0.1");
        System.setProperty("http.proxyPort", "8080");
        System.setProperty("https.proxyHost","127.0.0.1");
        System.setProperty("https.proxyPort", "8080");

        String postUrl = "https://www.flickr.com/photos/upload/edit";
        URL obj = new URL(postUrl);
        conn = (HttpsURLConnection) obj.openConnection();
        conn.setRequestProperty("Host", "www.flickr.com");
        conn.setRequestMethod("POST");
        conn.setInstanceFollowRedirects(false);
        conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:54.0) Gecko/20100101 Firefox/54.0");
        conn.setRequestProperty("Accept","text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
        conn.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
        conn.setRequestProperty("Accept-Encoding","gzip, deflate, br");
        conn.setRequestProperty("Referer","https://www.flickr.com/photos/upload/done/");

        String cookies = "BX=57qvh7hc2pv0e&b=4&d=AYSLfZlpYEJDUw8U96du.xPtO10-&s=ng&i=phSZTcVkJMTXHwJeRBaT; xb=697742; vp=929%2C931%2C1%2C17%2Cphotolist-container%3A800%2Ctag-photos-everyone-view%3A800%2Cshowcase-container%3A640%2Calbums-list-page-view%3A800%2Calbum-page-view%3A800%2Cexplore-page-view%3A800; localization=en-us%3Bus%3Bus; liqpw=946; liqph=899; flrbp=1503001901-854fe75a344cad7ed38c6a69b05428f9e9288d43; flrbs=1503001901-3ff3b7c75c51acfff4c92fe4539b75b76b5a90bf; flrbgrp=1503001901-f2a423596418e34c3376f41ba3372a4183a889e8; flrbgdrp=1503001901-939bdaab1ed8976f5cf38382eb4419b8777d520f; flrbgmrp=1503001901-5d4f775c14cad13c31f5e4cff175c128dcec3023; flrbcr=1503001901-8e848de59d2c096c9c4d47d054923b65f764cdd6; flrbrst=1503001901-00e5466610c612fd6ded7d8dd1f103091f929607; flrtags=1503001901-651bff30bae469fc61d284ba9ccccd6836b08584; flrbrp=1503001901-95235297a00e5c1b3b5808b075d0126a8f4d8eca; ffs=156785826-558; cookie_accid=156785826; cookie_epass=65bb9834df69902fd9b190a16f1fab44; sa=1505832248%3A156791166%40N05%3A0e14600a850b63e056fa58b02b586792; flrbfd=1503001901-ee881996230c722a67c627e98c136ce8ddd0aa29; cookie_session=156785826%3A65bb9834df69902fd9b190a16f1fab44; RT=s=1502992875624&u=&r=https%3A//www.flickr.com/photos/upload/basic; flrb=45";

        conn.setRequestProperty("Cookie",cookies);
        conn.setRequestProperty("Connection", "close");
        conn.setRequestProperty("Upgrade-Insecure-Requests","1");
        conn.setDoOutput(true);
        conn.setDoInput(true);

        DataOutputStream request = new DataOutputStream(conn.getOutputStream());

        String[] uploadIdList = uploadID.split(",");
        String[] imageNameList = this.imageName.split(",");
        int counter = uploadIdList.length;
        String payload = "edit_done=1&upload_ids="+uploadID+"&just_photo_ids=&set_id="+Server.photoSetID+"&magic_cookie="+magic_cookie;
        int nameindex = 0;
        while (counter > 0){
            int index = counter-1;

            payload += "&title_"+uploadIdList[index]+"="+imageNameList[index]+"&description_"+uploadIdList[index]+"=&tags_"+uploadIdList[index]+"=&";
            counter--;
            nameindex++;
        }

        //String payload = "edit_done=1&upload_ids="+uploadID+"&just_photo_ids=&set_id="+Client.photoSetID+"&magic_cookie="+magic_cookie+"&title_"+uploadID+"="+this.imageName+"&description_"+uploadID+"=&tags_"+uploadID+"=&Submit=SAVE";
        payload += "Submit=SAVE";
        request.writeBytes(payload);
        request.flush();

        //String str = conn.getRequestMethod();
        conn.getHeaderFields();

        BufferedReader in = null;
        while (in == null) {
            in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        }
        //BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();
    }

    //SendPost for multiple images

    public void sendPostMulti(String url, ArrayList<String> imagesPath) throws Exception {


        long sendImageStart = System.currentTimeMillis();

        if(imagesPath.size()==0){
            return;
        }

        //this.imageName = imageName;
        this.url = url;
        //this.imagePath=imagePath;

        HttpService.setSslSecurityProtocol( SSLSecurityProtocol.TLSv1_2 );
        System.setProperty("http.proxyHost", "127.0.0.1");
        System.setProperty("http.proxyPort", "8080");
        System.setProperty("https.proxyHost","127.0.0.1");
        System.setProperty("https.proxyPort", "8080");

//        URL oracle = new URL("http://www.varzesh3.com/");
//        URLConnection yc = oracle.openConnection();
//        BufferedReader in = new BufferedReader(new InputStreamReader(
//                yc.getInputStream()));
//        String inputLine;
//        while ((inputLine = in.readLine()) != null)
//            System.out.println(inputLine);
//        in.close();


        String posturl = "https://up.flickr.com/photos/upload/transfer/";
        String boundary = "---------------------------10980066661674643583482671613";

        String crlf = "\r\n";
        String twoHyphens = "--";


        URL obj = new URL(posturl);
        conn = (HttpsURLConnection) obj.openConnection();
        conn.setRequestProperty("Host", "www.up.flickr.com");
        conn.setRequestMethod("POST");
        conn.setInstanceFollowRedirects(false);
        // Acts like a browser
        //conn.setUseCaches(false);

        conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:54.0) Gecko/20100101 Firefox/54.0");
        conn.setRequestProperty("Accept","text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
        conn.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
        conn.setRequestProperty("Accept-Encoding","gzip, deflate, br");

        conn.setRequestProperty("Content-Type","multipart/form-data; "+"boundary="+boundary);
        conn.setRequestProperty("Content-Length", "776765");
        conn.setRequestProperty("Referer", "https://www.flickr.com/photos/upload/basic");
        String cookies = "BX=57qvh7hc2pv0e&b=4&d=AYSLfZlpYEJDUw8U96du.xPtO10-&s=ng&i=VQ6rcY681aPbLb4RB4.3; xb=697742; vp=929%2C328%2C1%2C17%2Cphotolist-container%3A800%2Ctag-photos-everyone-view%3A800%2Cshowcase-container%3A640; localization=en-us%3Bus%3Bus; flrbp=1500845239-fd30959040f8d6a2742e27e03ab620e8dfa5878b; flrbs=1500845239-2f0b2d88131795d83f29e89707edc685973e5edb; flrbgrp=1500845239-59cfeb614c03509fe78220e6051d97a399342e37; flrbgdrp=1500845239-7d32670fce0e5137fc965cf6da703586f35648d2; flrbgmrp=1500845239-cc74c61a9beeed57ce6a04545808a767b922ab16; flrbcr=1500845239-1a9c902c8d8547cf3da1433dd30ac12e6cadbce5; flrbrst=1500845239-947446765277b9496a8424b0398769a2a4b52b0e; flrtags=1500845239-b2ef419020b13ed42193930bf0ca8134de9935a7; flrbrp=1500845239-a9a750226976e1a3cef3a0bdd98d65ec2cb625b5; ffs=156785826-558; cookie_accid=156785826; cookie_epass=65bb9834df69902fd9b190a16f1fab44; sa=1505832248%3A156791166%40N05%3A0e14600a850b63e056fa58b02b586792; flrbfd=1500845239-6ad6a36b94b611ca04436ae55f2bf7e48207f06b; cookie_session=156785826%3A65bb9834df69902fd9b190a16f1fab44; flrb=47; RT=s=1500845241621&u=&r=https%3A//www.flickr.com/photos/upload/";

        conn.setRequestProperty("Cookie",cookies);
        conn.setRequestProperty("Connection", "keep-alive");
        conn.setRequestProperty("Upgrade-Insecure-Requests","1");


        String str = conn.getRequestMethod();

        conn.setDoOutput(true);
        conn.setDoInput(true);
        DataOutputStream request = new DataOutputStream(conn.getOutputStream());
        // conn.setRequestMethod("POST");

        request.writeBytes("--"+boundary+crlf);
        request.writeBytes("Content-Disposition: form-data; name=\"done\"" + crlf+crlf + "1"+crlf);

        request.writeBytes("--"+boundary+crlf);
        request.writeBytes("Content-Disposition: form-data; name=\"complex_perms\"" + crlf+crlf + "0"+crlf);

        request.writeBytes("--"+boundary+crlf);
        request.writeBytes("Content-Disposition: form-data; name=\"magic_cookie\"" + crlf+crlf +
                "a0a111e21ff2ad8b2d0e9bed4a17e29f75d9701fd7fa583f2855cbfbb5e8e5f8"+crlf);


        this.imageName="";
        for(int i=1; i <= imagesPath.size(); i++){


            this.imagePath=imagesPath.get(i-1);

            if(i > 1){
                this.imageName += ",";
            }

            String tmpImage = imagePath.substring(imagePath.lastIndexOf("/")+1);
            tmpImage = tmpImage.replace(".jpg","");

            if(i == imagesPath.size()){
                tmpImage += "-Flush";
            }
            //this.imageName += imagePath.substring(imagePath.lastIndexOf("/")+1);
            this.imageName += tmpImage;

            request.writeBytes("--"+boundary+crlf);
            request.writeBytes("Content-Disposition: form-data; name=\"file"+Integer.toString(i)+"\"; filename=\"" +
                    Integer.toString(i)+".jpg\""+crlf +
                    "Content-Type: image/jpeg"+crlf+crlf);

            FileInputStream inputStream = new FileInputStream(imagesPath.get(i-1));
            byte[] buffer = new byte[4096];
            int bytesRead = -1;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                request.write(buffer, 0, bytesRead);
            }
            request.flush();
            inputStream.close();

            request.writeBytes("-----------------------------10980066661674643583482671613" +crlf+
                    "Content-Disposition: form-data; name=\"tags\"" +crlf+
                            crlf+
                            crlf);
        }



        String upload = twoHyphens+"---------------------------10980066661674643583482671613" +crlf+
                "Content-Disposition: form-data; name=\"is_public_0\"" +crlf+
                crlf+
                "1" +crlf+
                "-----------------------------10980066661674643583482671613" +crlf+
                "Content-Disposition: form-data; name=\"safety_level\"" +crlf+
                crlf+
                "0" +crlf+
                "-----------------------------10980066661674643583482671613" +crlf+
                "Content-Disposition: form-data; name=\"content_type\"" +crlf+
                crlf+
                "0" +crlf+
                "-----------------------------10980066661674643583482671613" +crlf+
                "Content-Disposition: form-data; name=\"Submit\"" +crlf+
                crlf+
                "UPLOAD" +crlf+
                "-----------------------------10980066661674643583482671613--";

        String location = null;
        while (location == null) {
            request.writeBytes(upload);
            location = conn.getHeaderField("location");
            // Map<String, List<String>> map = conn.getHeaderFields();
        }
        int index = location.indexOf("?i=");
        String i = location.substring(index);

        System.out.println("\nSending 'POST' request to URL : " + url);
        TimeUnit.MILLISECONDS.sleep(sleepTime);
        getTickets(i);
        System.out.println("SendImageDuration="+Long.toString(System.currentTimeMillis() - sendImageStart));
    }



}
