import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.FileImageOutputStream;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;


public class Encoder {

    //public static List<String> list = Collections.synchronizedList(new ArrayList(10000000));
    public static Map<String,Long> hashMap = new HashMap<>(10000000);
    public static Map<Long,String> hashMapReverse = new HashMap<>(10000000);

    private static String codeBookPath = "/home/prg/Desktop/Flickr_Server_v2.0/TOTALBITS.txt";
    private static String uploadedImagesFromClient = "/home/prg/Desktop/Flickr_Server_v2.0/Image/clientImages/";




    public static void main(String[] args) {

        Encoder encoder = new Encoder();

        //encoder.dec2Bit();
        //byte2Image();
    }

   /* public static  void dec2Bit(){


        Path inputFile = Paths.get("C:\\Users\\Ahmad\\IdeaProjects\\DCTcompute\\ValidValues-9Block\\TOTAL.txt");
        Path outputFile = Paths.get("C:\\Users\\Ahmad\\IdeaProjects\\DCTcompute\\ValidValues-9Block\\TOTALBITS-test.txt");

        int cnt = 0;
        try (InputStream in = Files.newInputStream(inputFile);
             BufferedReader reader =
                     new BufferedReader(new InputStreamReader(in))) {
            String line = null;
            OutputStream out = new BufferedOutputStream(
                    Files.newOutputStream(outputFile, CREATE, APPEND));
            while ((line = reader.readLine()) != null) {
                System.out.println(line);

                String[] dcts = line.split(",");
                String binaries = "";

                for(String dct:dcts) {
                    Integer number = Integer.parseInt(dct);
                    String binary = Integer.toBinaryString(number);
                    while (binary.length() < 4){
                        binary = "0"+binary;
                    }
                    binaries = binaries+binary;

                }
                byte data[] = binaries.getBytes();
                out.write(data, 0, data.length);
                list.add(binaries);
                out.write('\n');
                cnt++;

            }
        } catch (IOException x) {
            System.err.println(x);
        }
        System.out.println(cnt);

    }
*/
    public static void load2List(){

        long loadListStart = System.currentTimeMillis();
        synchronized (hashMap) {
            Path inputFile = Paths.get(codeBookPath);
            try (InputStream in = Files.newInputStream(inputFile);
                 BufferedReader reader =
                         new BufferedReader(new InputStreamReader(in))) {
                String line = null;

                System.out.println("Loading List");
                long cnt = 0;
                while ((line = reader.readLine()) != null) {
                    //
                    // System.out.println(line);

                    hashMap.put(line,cnt);
                    hashMapReverse.put(cnt,line);

                    cnt++;
                    //list.add(line);
                }
            } catch (IOException x) {
                System.out.println("load2list error");
                System.err.println(x);
            }
        }
        long loadListFinish = System.currentTimeMillis();
        System.out.println("LoadList="+Long.toString(loadListFinish-loadListStart));
    }

    public static String convertTobinary(int number) {
        //int[] binary = new int[8];
        String result = "";
        for (int i = 7, num = number; i >= 0; i--, num >>>= 1)
            result = Integer.toString(num & 1)+result;
        return result;
    }

    public static void byte2Image(byte[]request, int totalbytes, String imageName) {

        synchronized (hashMap) {
            if (hashMap.isEmpty()) {
                load2List();
            }
        }

        long byte2imageStart = System.currentTimeMillis();

        if (totalbytes == 0) {
            return ;
        }
        String bits = "";
        for (int cnt = 0; cnt < totalbytes; cnt++)
            bits += String.format("%8s", Integer.toBinaryString(request[cnt] & 0xFF)).replace(' ', '0');

        System.out.println("Bits Length=" + bits.length());
        System.out.println("TotalBytes Length=" + totalbytes);

        while ((bits.length() % Global.bitsPerBlock) != 0) {
            bits += "0";
        }
        int NumOfImages = (int) (totalbytes*1.0 / Server.maxBytePerImage);///##############Hard coded, must be changed

        ArrayList<String> ImagesPath= new ArrayList<>();
        for (int imgcnt = 0; imgcnt <= NumOfImages; imgcnt++){
            int location = 0;

            String spareBits = "";
            double[][] pixels = new double[Global.imageWidth][Global.imageheight];
            double[][] block = new double[8][8];

            System.out.println("Bits Length=" + bits.length());

            for (int cnt = imgcnt*10350*8; cnt < bits.length() && location < 3600; cnt = cnt + Global.bitsPerBlock) {
                String subBits = "";
                if (cnt + Global.bitsPerBlock > bits.length()) {
                    subBits = bits.substring(cnt, bits.length());
                    while (subBits.length() < Global.bitsPerBlock) {
                        subBits += subBits + "0";
                    }
                } else {
                    subBits = bits.substring(cnt, cnt + Global.bitsPerBlock);
                }

                spareBits += subBits;
                //cnt = cnt + Global.bitsPerBlock;

                long decimal = Long.parseLong(subBits, 2);


                long buildblockmap = System.currentTimeMillis();
                //block = buildBlock(ha.get(decimal));
                try {
                    block = buildBlock(hashMapReverse.get(decimal));
                } catch (NullPointerException e) {
                    System.out.println("Encoder Yeah :))");
                    block = null;
                }

                //System.out.println("buildBlock="+(Long.toString(System.currentTimeMillis() - buildblockmap)));
                if (block == null) {
                    break;
                }
                double[][] pixelBlock = inverseDCT(block);
                if (isValid(pixelBlock) == false) {
                    System.out.println("ERRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRooRRRRRRRRRRRRRRRRRRRRRRRRRRR");

                    System.exit(-1);
                }


                int XPosition = (location % (Global.imageWidth / 8)) * 8;
                int YPosition = (location / (Global.imageheight / 8)) * 8;

                for (int itr1 = 0; itr1 < 8; itr1++) {
                    for (int itr2 = 0; itr2 < 8; itr2++) {

                        pixels[itr1 + XPosition][itr2 + YPosition] = pixelBlock[itr1][itr2];

                    }
                }
                location++;
                //System.out.println(bytesRead);

            }
            System.out.println("byte2Image_part1 = " + Long.toString(System.currentTimeMillis() - byte2imageStart));
            String ImagePath = "";
            try {
                //imageName +="-"+Integer.toString(imgcnt);
                ImagePath = createImage(pixels, imageName+"-"+Integer.toString(imgcnt));
                ImagesPath.add(ImagePath);
                String realImageName =imageName+"-"+Integer.toString(imgcnt);

                //PostImage postImage = new PostImage();
                //postImage.sendPost("https://up.flickr.com/photos/upload/transfer/",ImagePath,imageName+"-"+Integer.toString(imgcnt));
                //PostImage postImage = new PostImage();
                //postImage.sendPost("https://up.flickr.com/photos/upload/transfer/",path,"");
            } catch (Exception IOException) {
                System.out.println("EXEPTION--");
            }
        }
        try {


            PostImage postImage = new PostImage();
            postImage.sendPostMulti("https://up.flickr.com/photos/upload/transfer/",ImagesPath);
            //PostImage postImage = new PostImage();
            //postImage.sendPost("https://up.flickr.com/photos/upload/transfer/",path,"");
        } catch (Exception IOException) {
            System.out.println("EXEPTION----");
        }



        //make flush image and insert number of bits in it
        //return path;

    }

    public static double[][] buildBlock(String code){

        //long buildBlockStart = System.currentTimeMillis();
        double[][] input = new double[8][8];

        int [] quantizationValue = {
                16 ,    11,     10,    16 ,   24,   40,   51 ,   61,
                12 ,    12,     14,    19 ,   26,   58,   60 ,   55,
                14 ,    13,     16,    24 ,   40,   57,   69 ,   56,
                14 ,    17,     22,    29 ,   51,   87,   80 ,   62,
                18 ,    22,     37,    56 ,   68,  109,  103 ,   77,
                24 ,    35,     55,    64 ,   81,  104,  113 ,   92,
                49 ,    64,     78,    87 ,  103,  121,  120 ,  101,
                72 ,    92 ,    95  ,  98   ,112  ,100  ,103   , 99,
        };

        List<Integer> quantizationMatrix = new ArrayList<>();

        for (int cnt = 0; cnt < 64; cnt++){
            quantizationMatrix.add(quantizationValue[cnt]);
        }

        List<Integer> ziczacMatrix = new ArrayList<Integer>();

        int [] zigzag = {0 ,    1,     5,    6 ,   14,   15,   27 ,   28,
                2 ,    4,     7,    13 ,   16,   26,   29 ,   42,
                3 ,    8,     12,    17 ,   25,   30,   41 ,   43,
                9 ,    11,     18,    24 ,   31,   40,   44 ,   53,
                10 ,    19,     23,    32 ,   39,  45,  52 ,   54,
                20 ,    22,     33,    38 ,   46,  51,  55 ,   60,
                21 ,    34,     37,    47 ,  50,  56,  59 ,  61,
                35 ,    36 ,    48  ,  49   ,57  ,58  ,62   , 63};

        for (int cnt = 0; cnt < 64; cnt++){
            ziczacMatrix.add(zigzag[cnt]);
        }



        int [] resultTable = new int[64];

        int i = 0;
        while ( i < code.length()) {

            for(int j=0; j < 9 /*number of sells we wanna push data in */; j++){

                int quantIndex = quantizationMatrix.indexOf(j); //index of what we want in quantization table
                int zigIndex = ziczacMatrix.indexOf(j);

                String str = code.substring(i, i + (int)(Math.log(256/quantizationValue[zigIndex])/Math.log(2)));
                int n = Integer.parseInt(str,2);
                resultTable[zigIndex] = (int) (n * Math.pow(2,8 - (int)(Math.log(256/quantizationValue[zigIndex])/Math.log(2))));

                i = i + (int)(Math.log(256/quantizationValue[zigIndex])/Math.log(2));
            }
            int m = 0;
            while (m < 64) {
                for (int k = 0; k < 8; k++) {
                    for (int l = 0; l < 8; l++) {
                        input[k][l] = resultTable[m];
                        m++;
                    }
                }
            }

        }
        //System.out.println("BuildBlock = "+Long.toString(System.currentTimeMillis() - buildBlockStart));
        return input;
    }


    public static double[][] inverseDCT(double[][] input) {

        //long inverseDCTStart = System.currentTimeMillis();
        final int N = input.length;
        final double mathPI = Math.PI;
        final int halfN = N / 2;
        final double doubN = 2.0 * N;

        double[][] c = new double[N][N];
        c = initCoefficients(c);

        double[][] output = new double[N][N];


        for (int x = 0; x < N; x++) {
            double temp_x = 2*x+1;
            for (int y = 0; y < N; y++) {
                double temp_y = 2*y+1;
                double sum = 0.0;
                for (int u = 0; u < N; u++) {
                    double temp_u = u*mathPI;
                    for (int v = 0; v < N; v++) {
                        double temp_v = v*mathPI;


                        sum += c[u][v] * Math.cos(temp_x * temp_u / doubN) * Math.cos(((temp_y* temp_v) / doubN) ) * input[u][v];
                        //sum += c[u][v] * Math.cos((temp_x / doubN) * temp_u) * Math.cos((temp_y / doubN) * temp_v)* input[u][v] ;
                    }
                }

                output[x][y] = sum/halfN ;
            }
        }
        //System.out.println("InverseDCT="+Long.toString(System.currentTimeMillis()-inverseDCTStart));
        return output;
    }

    public static double[][] initCoefficients(double[][] c) {
        final int N = c.length;
        final double value = 1 / Math.sqrt(2.0);

        for (int i = 1; i < N; i++) {
            for (int j = 1; j < N; j++) {
                c[i][j] = 1;
            }
        }

        for (int i = 0; i < N; i++) {
            c[i][0] = value;
            c[0][i] = value;
        }
        c[0][0] = 0.5;
        return c;
    }

    private static boolean isValid(double[][] inverse) {
        //long isValidStart = System.currentTimeMillis();
        for(int i =0; i < inverse.length; i++){
            for(int j=0; j < inverse.length; j++){
                if(inverse[i][j] < 0){
                    //System.out.println("isValidDuration = "+ Long.toString(System.currentTimeMillis() - isValidStart));
                    return false;
                }
            }
        }
        //System.out.println("isValidDuration = "+ Long.toString(System.currentTimeMillis() - isValidStart));
        return true;
    }

    public static String createImage(double [][]pixels, String imageName) throws IOException {

        long createImageStart = System.currentTimeMillis();
        int [] pix = new int[pixels.length*pixels.length];
        int cnt = 0;
        for(int i=0; i < pixels.length; i++){
            for(int j=0; j<pixels.length; j++){
                pix[cnt] = (int)pixels[i][j];
                cnt++;
            }
        }

        BufferedImage bufferedImage = new BufferedImage(Global.imageWidth, Global.imageheight, BufferedImage.TYPE_BYTE_GRAY);
        WritableRaster raster = bufferedImage.getRaster();
        raster.setSamples(0,0,Global.imageWidth,Global.imageheight,0,pix);

        Iterator iter = ImageIO.getImageWritersByFormatName("jpeg");
        ImageWriter imageWriter = (ImageWriter)iter.next();
        ImageWriteParam iwp = imageWriter.getDefaultWriteParam();

        iwp.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
        iwp.setCompressionQuality(1);

        Random random = new Random();
        int rand = random.nextInt();

        //File file = new File("C:\\tmp2\\"+Integer.toString(rand)+".jpg");
        File file = new File(uploadedImagesFromClient+imageName+".jpg");
        FileImageOutputStream output = new FileImageOutputStream(file);
        imageWriter.setOutput(output);
        IIOImage image = new IIOImage(bufferedImage, null, null);
        imageWriter.write(null, image, iwp);
        imageWriter.dispose();
        output.close();
        System.out.println("CreateImageDuration="+Long.toString(System.currentTimeMillis() - createImageStart));
        return file.getPath();
    }

}
