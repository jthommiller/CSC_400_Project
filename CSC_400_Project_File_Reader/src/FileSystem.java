/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import java.io.*;
import java.nio.*;

/**
 *
 * @author jthommiller
 */
public class FileSystem {
    
    class SuperBlock {
        public final int blk_size;
        
        SuperBlock(int blkSize) {
            blk_size = blkSize;
        }
        
    }
    //basic methods to convert decimal integer to a binary string
    public static String dToB(int n) {
        String s = "";
        while (n > 0) {
            s = n%2+s;
            n /= 2;
        }
        while (s.length() < 8) {
            s = "0"+s;
        }
        System.out.println(s);
        return s;
    }
    
    //basic methods to convert binary string to 
    public static int bToD(String s) {
        int n = 0;
        for (int i = 0; i < s.length(); i++) {
            n += (int) (s.charAt(s.length()-1-i) - '0') * (Math.pow(2, i));
        }
        return n;
    }
    
    public static void main(String[] args) throws IOException {
        //directory is otherwise System.getProperty("user.dir");
        File file = new File("C:\\Users\\Brian\\Downloads\\virtdisk");
        RandomAccessFile r = new RandomAccessFile(file, "r");
        //need to convert virtdisk to a byte array entirely
        //to correctly navigate it
        
        
        //size of our block -- i think
        int blkSize = 1024;
        
        //byte array with blkSize number of elements
        //byte[] data = new byte[blkSize];
        
        //grab and read data from file equal to 1024 bytes
        for (int j = 1; j < 3; j++) {
            r.seek(blkSize*j);
            byte[] data = new byte[blkSize];
            r.readFully(data);
        
        
        //fill our byte array with converted data
        byte[] dataConv = data;
        
        //make a buffer for our converted data -- little endian is standard?
        ByteBuffer buffer = ByteBuffer.wrap(dataConv);
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        
        String oof = "";
        
        //building directory path from our buffer data
        int sum = 0;
        for(int i = 0; i < 1024; i++) {
            int value = buffer.get(0 + i);
            oof = dToB(value)+oof; 
            sum = bToD(oof);
            //checking if value is null or not basically
            if (true && (i+1)%4==0) {
                //adding character to our path
                //oof += (char)value + "";
                System.out.println(sum);
                oof = "";
                sum = 0;
                if (i == 11) {
                    System.exit(0);
                }
                //doesnt retrieve char, does something weird instead
                //System.out.println(buffer.getChar(128+i));
            }
        }
        //System.out.println("block: "+j+" stuff: "+oof);
        oof = "";
        }
        
        while(true) {
            //displaying current path
            //System.out.print(oof + "/" + "...");
            
            //Enter data using BufferReader 
            BufferedReader readInput =  new BufferedReader(new InputStreamReader(System.in)); 

            // Reading data using readLine 
            String cmd = readInput.readLine();

            readInput(cmd);
        }
        
    }
    
    
    public static void readInput(String cmd) {
        
        //check the command entered by the user
        switch(cmd.charAt(0)) {
            //change directory to parent directory
            case 'b':
                //change directory to: cmd.substring(1);
                break;
            //change directory to child directory
            case 'f':
                //change directory to: cmd.substring(1);
                break;
            //copy a file's contents
            case 'c':
                //copy file named: cmd.substring(1);
                break;
            //close program
            case 'e':
                System.exit(0);
                break;
            default:
                System.out.println(cmd+" is not a command.");
                break;
        }
    }
    
}