

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.text.DecimalFormat;

public class NetworkSpeedMonitor {
    /**
     * The main method.
     *
     * @param args the arguments
     */
    public static void main(String[] args) {

        if(args.length==1){
            Start(args[0]);

        }else{
            System.out.println("ex) speed.tar eth0");
        }


    }

    /**
     * Start.
     *
     * @param strInterface the str interface
     */
    private static void Start(String strInterface){
        while(true){
            //System.out.println(strInterface+"1");
            double r1=ReadFileToInt("/sys/class/net/"+strInterface+"/statistics/rx_bytes");
            double t1=ReadFileToInt("/sys/class/net/"+strInterface+"/statistics/tx_bytes");
            Sleep(1000);
            double r2=ReadFileToInt("/sys/class/net/"+strInterface+"/statistics/rx_bytes");
            double t2=ReadFileToInt("/sys/class/net/"+strInterface+"/statistics/tx_bytes");

            double TBPS= t2-t1;
            double RBPS= r2-r1;


            DecimalFormat df = new DecimalFormat("#.##");

            String TKBPS="";
            if((TBPS/1024)>=1024){
                TKBPS= df.format(TBPS/1024/1024)+ "MB/s";
            }else{
                TKBPS= df.format(TBPS/1024)+ "KB/s";
            }


            String RKBPS="";
            if((RBPS/1024)>=1024){
                RKBPS= df.format(RBPS/1024/1024)+ "MB/s";
            }else{
                RKBPS= df.format(RBPS/1024)+ "KB/s";
            }



            System.out.println("UP: "+ TKBPS+ " DOWN: " + RKBPS);

            //System.out.println(strInterface+"2");
        }

    }


    /**
     * Read file to int.
     *
     * @param strFilePath the str file path
     * @return the double
     */
    public static double ReadFileToInt(String strFilePath){
        double number=0;

        try{

            FileInputStream fstream = new FileInputStream(strFilePath);
            // Get the object of DataInputStream
            DataInputStream in = new DataInputStream(fstream);
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            String strLine;
            //Read File Line By Line
            int Line=0;
            while ((strLine = br.readLine()) != null)   {
                Line++;
                if(Line==1){
                    number=Double.parseDouble(strLine.trim());
                }
                // Print the content on the console
                //System.out.println (strLine);
            }
            //Close the input stream
            in.close();
        }catch (Exception e){//Catch exception if any
            System.err.println("Error: " + e.getMessage());
        }
        return number;
    }


    /**
     * Sleep.
     *
     * @param intMiliSec the int mili sec
     */
    private static void Sleep(int intMiliSec){
        try{
            Thread.sleep(intMiliSec);//sleep for 1000 ms
        }
        catch(Exception ie){
            System.err.println("Error: " + ie.getMessage());
        }
    }
}
