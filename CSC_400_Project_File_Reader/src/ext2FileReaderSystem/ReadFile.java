package ext2FileReaderSystem;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class ReadFile {
    
    ReadFile(){
        
    }
    
    public byte[] read(RandomAccessFile system, int offset, byte[] data) throws IOException{
        long pointer = system.getFilePointer();
        int readSet = 1024 * offset;
        system.seek(readSet);
        try{
            system.read(data, 0, readSet);
        }
        catch(IOException e){
            e.printStackTrace();
        }
        system.seek(pointer);
        return data;
    }
    
    public int processData(byte[] data, int start, int end){
        String binary = convertByteArrayToBinaryString(data, start, end);
        int decimal = convertBinaryStringToInteger(binary);
        return decimal;
    }
    
    public byte[] convertToLittleEndian(byte[] data){
        ByteBuffer buffer = ByteBuffer.wrap(data);
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        buffer.rewind();
        byte[] orderedData = new byte[buffer.remaining()];
        buffer.get(orderedData);
        return orderedData;
    }
    
    public String convertToString(byte[] data){
        String temp = "";
        int end = data.length;
            for(int i = 0; i < end; i++){
                int bytePosition = (int)data[i];
                if(bytePosition==0)
                    break;
                temp += (char)bytePosition;
            }
        return temp;
    }
    
    public String convertByteArrayToBinaryString(byte[] data, int start, int end){
        String binaryString = "";
        for(int i = start; i<= end; i++){
            String byteToString = String.format("%8s", Integer.toBinaryString(data[i] & 0xFF));
            byteToString = byteToString.replace(' ', '0');
            binaryString = byteToString + binaryString;
        }
        return binaryString;
    }
    
    public int convertBinaryStringToInteger(String binary){
        int pow = 0, sum = 0;
        int length = binary.length();
        for(int i = 0; i < length; i++){
            if(binary.charAt(i) == '1')
                pow = (int)Math.pow(2, length-i-1);
                sum += pow;
        }
        return sum;
    }
    /*
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
        }*/
}
