import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.File;
import java.io.IOException;


public class Decoder {


    static String IMG ="base.jpg";

    public static void main(String[] args) {
        Global global = new Global();
        Global global1 = new Global();
        boolean flag = Global.list.isEmpty();
        image2Byte(IMG);;
    }
    //public static String image2Byte(String path){
    public static byte[] image2Byte(String path){

        //long befloadlist = System.currentTimeMillis();
        if(Encoder.hashMap.isEmpty()) {
            Encoder.load2List();
            System.out.println("List loaded!");
        }
        //System.out.println("afterloadlist = "+Long.toString(System.currentTimeMillis() - befloadlist));

        long image2ByteStart = System.currentTimeMillis();
        int [][] pixels = readimage(path);
        String message = "";

        int cnt = 0;
        long index2 = 0;

        //long befwhile = System.currentTimeMillis();
        while (index2 != -1 && cnt < 3600) {
            int [][] input = new int[8][8];

            int XPosition = (cnt / (480 / 8)) * 8;
            int YPosition = (cnt % (480 / 8)) * 8;

            for (int itr1 = 0; itr1 < 8; itr1++) {
                for (int itr2 = 0; itr2 < 8; itr2++) {
                    input[itr2][itr1] = pixels[itr1 + XPosition][itr2 + YPosition];
                }
            }

            double[][] forward = forwardDCT(input);

            int cell1 = (int) Math.round(forward[0][0] / 16);
            int cell2 = (int) Math.round(forward[0][1] / 16);
            int cell3 = (int) Math.round(forward[1][0] / 16);
            int cell4 = (int) Math.round(forward[2][0] / 16);
            int cell5 = (int) Math.round(forward[1][1] / 16);
            int cell6 = (int) Math.round(forward[0][2] / 16);
            int cell7 = (int) Math.round(forward[0][3] / 16);
            int cell8 = (int) Math.round(forward[1][2] / 16);
            int cell9 = (int) Math.round(forward[2][1] / 16);

            String encodedMessage = cell1 + "," + cell2 + "," + cell3 + "," + cell4 + "," + cell5 + "," + cell6 + "," + cell7 + "," + cell8 + "," + cell9;

            String binaries = dec2Bit(encodedMessage);

            try{
                index2 = Encoder.hashMap.get(binaries);
            }catch (NullPointerException e){
                index2 = -1;
                System.out.printf("yeah :)");
            }

            //System.out.println("beforeindex="+Long.toString(System.currentTimeMillis() - beforeindex));

            if(index2 != -1){
                message += binary2Bits(index2);
            }
            cnt++;
        }
        //System.out.println("afterwhile = "+Long.toString(System.currentTimeMillis() - befwhile));

        while ((message.length()%8) != 0) {
            message = message.substring(0,message.length()-1);
            //ToDo
        }

        String str = "";
        byte[] b = new byte[message.length()/8];

        for (int i = 0; i < message.length()/8; i++) {

            int a = Integer.parseInt(message.substring(8*i,(i+1)*8),2);
            //str += (char)(a);
            b[i] = (byte) a;
        }

        System.out.println("Image2ByteDuration = "+Long.toString(System.currentTimeMillis() - image2ByteStart));
        //return str;
        return b;
    }

    public static String binary2Bits(long index){

        //long bintobits = System.currentTimeMillis();
        String binary = Long.toBinaryString(index);
        while (binary.length() < 23){
            binary = "0"+binary;
        }
        //System.out.println("binary2Bits="+Long.toString(System.currentTimeMillis() - bintobits));
        return binary;
    }

    public static  String dec2Bit(String line){

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
        //byte data[] = binaries.getBytes();
        //out.write(data, 0, data.length);
        //list.add(binaries);
        //out.write('\n');
        //System.out.println(binaries);
        return binaries;
    }


    public static int[][] readimage(String path) {

        BufferedImage img;
        byte[] pixels = null;
        int [][]pixelArray = null;

        try {
            img = ImageIO.read(new File(path));
            pixels = ((DataBufferByte) img.getRaster().getDataBuffer()).getData();
            pixelArray =new int [img.getHeight()][img.getWidth()];

            int counter = 0;
            for (int i = 0; i < img.getHeight(); i++) {
                for (int j = 0; j < img.getWidth(); j++) {

                    pixelArray[i][j] = pixels[i +  j *img.getHeight()] ;
                    if(pixelArray[i][j] < 0){
                        pixelArray[i][j] += 256;
                    }
                    counter++;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return pixelArray;
    }

    public static double[][] forwardDCT(int[][] input) {

        //long forwarddct = System.currentTimeMillis();
        final int N = input.length;
        final double mathPI = Math.PI;
        final int halfN = N / 2;
        final double doubN = 2.0 * N;

        double[][] c = new double[N][N];
        c = initCoefficients(c);

        double[][] output = new double[N][N];

        for (int u = 0; u < N; u++) {
            double temp_u = u * mathPI;
            for (int v = 0; v < N; v++) {
                double temp_v = v * mathPI;
                double sum = 0.0;
                for (int x = 0; x < N; x++) {
                    int temp_x = 2 * x + 1;

                    for (int y = 0; y < N; y++) {
                        int temp_y = 2 * y + 1;

                        //sum += input[x][y] * Math.cos((temp_x/doubN)*temp_u) * Math.cos(((2*y+1)/doubN)*temp_v);
                        double temp = Math.cos((double) ((temp_y)* temp_v / doubN));
                        sum +=  Math.cos((double) (temp_x * temp_u) / doubN) * Math.cos((double) ((temp_y)* temp_v / doubN)) * input[x][y];
                    }
                }
                //sum *= c[u][v]/ halfN;
                output[u][v] = ((sum / halfN) * c[u][v]);
            }
        }

        //System.out.println("ForwardDCT = "+ Long.toString(System.currentTimeMillis() - forwarddct));
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

}
