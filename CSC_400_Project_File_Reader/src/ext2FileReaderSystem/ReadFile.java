package ext2FileReaderSystem;

/*
Authors: James Miller, Matthew Abney, Brian Spencer
Date: 12-3-19
Project: CSC 400 Group Project
EXT2 FILE SYSTEM
 */

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class ReadFile {
    
    ReadFile(){
        //default constructor -- no parameters
    }
    
    //reading (and returning data as a byte array) from file
    public byte[] read(RandomAccessFile system, int offset, byte[] data) throws IOException{
        long pointer = system.getFilePointer();
        int readSet = 1024 * offset;
        system.seek(readSet);
        try{
            system.readFully(data);
        }
        catch(IOException e){
            e.printStackTrace();
        }
        system.seek(pointer);
        return data;
    }
    
     //processing data as binary bits
    public int processData(byte[] data, int start, int end){
        ByteBuffer buffer = ByteBuffer.wrap(data);
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        switch(end-start+1){
            case 1:
                return buffer.get(start);
            case 2:
                return buffer.getShort(start);
            case 4:
                return buffer.getInt(start);
            default:
                break;
        }
        return 0;
    }
    
    //reading bytes in little endian mode -- LSB on right -- to properly read data 
    public byte[] convertToLittleEndian(byte[] data){
        ByteBuffer buffer = ByteBuffer.wrap(data);
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        buffer.rewind();
        byte[] orderedData = new byte[buffer.remaining()];
        buffer.get(orderedData);
        return orderedData;
    }
    
    //converting a byte array to a string 
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
    
    //converting a byte array to a binary string
    public String convertByteArrayToBinaryString(byte[] data, int start, int end){
        String binaryString = "";
        for(int i = start; i<= end; i++){
            String byteToString = String.format("%8s", Integer.toBinaryString(data[i] & 0xFF));
            byteToString = byteToString.replace(' ', '0');
            binaryString = byteToString + binaryString;
        }
        return binaryString;
    }
    
    //converting a binary string to an integer
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
}
