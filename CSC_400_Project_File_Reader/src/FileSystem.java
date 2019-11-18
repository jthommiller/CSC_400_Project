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
    
    public static void main(String[] args) throws IOException {
        //directory is otherwise System.getProperty("user.dir");
        File file = new File("C:\\Users\\Brian\\Downloads\\virtdisk");
        RandomAccessFile r = new RandomAccessFile(file, "r");
        //need to convert virtdisk to a byte array entirely
        //to correctly navigate it
        
        
        //size of our block -- i think
        int blkSize = 1024;
        
        //byte array with blkSize number of elements
        byte[] data = new byte[blkSize];
        
        //grab and read data from file equal to 1024 bytes
        r.seek(blkSize);
        r.readFully(data);
        
        //fill our byte array with converted data
        byte[] dataConv = data;
        
        //make a buffer for our converted data -- little endian is standard?
        ByteBuffer buffer = ByteBuffer.wrap(dataConv);
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        
        String oof = "";
        
        //building directory path from our buffer data
        for(int i = 0; i < 16; i++) {
            int value = buffer.get(128 + i);
            //checking if value is null or not basically
            if (value > 0) {
                //adding character to our path
                oof += (char)value + "";
                //doesnt retrieve char, does something weird instead
                //System.out.println(buffer.getChar(128+i));
            }
        }
        System.out.println(oof);
        System.out.println("____");
       
        
        while(true) {
            //displaying current path
            System.out.print(oof + "/" + ">>>");
            
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
