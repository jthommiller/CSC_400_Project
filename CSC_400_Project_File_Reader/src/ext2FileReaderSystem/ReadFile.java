package ext2FileReaderSystem;


import static FileSystem.bToD;
import static FileSystem.dToB;
import static FileSystem.readInput;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author jthommiller
 */
public class ReadFile {
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
