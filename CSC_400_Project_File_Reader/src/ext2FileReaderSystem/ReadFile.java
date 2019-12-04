package ext2FileReaderSystem;

/*
Authors: James Miller, Matthew Abney, Brian Spencer
Date: 12-3-19
Project: CSC 400 Group Project
EXT2 FILE SYSTEM - File reading and processing class that reads from a RandomAccessFile
                   and processes data from byte arrays
 */

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class ReadFile {
    
    // Read File Constructor to allow access to class functions
    ReadFile(){
        
    }
    
    // Function to read from a Random Access File
    public byte[] read(RandomAccessFile system, int offset, byte[] data) throws IOException{
        // Saves Pointer
        long pointer = system.getFilePointer();
        // Finds new pointer to begin reading from
        int readSet = 1024 * offset;
        system.seek(readSet);
        try{
            //reads data into byte array
            system.readFully(data);
        }
        catch(IOException e){
            e.printStackTrace();
        }
        // Return to original pointer to maintain stability of the RandomAccessFile
        system.seek(pointer);
        // Returns byte array
        return data;
    }
    
    // Function to process a given amount from a  byte array at a specific offset
    public int processData(byte[] data, int start, int end){
        // Byte buffer object to wrap byte array and transpose it to Little Endian Format
        ByteBuffer buffer = ByteBuffer.wrap(data);
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        // Switch statement to read data from a length of 1, 2, or 4 bytes
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
        // Returns 0 if there is an error
        return 0;
    }
    
    // Function that converts a byte array to Little Endian format
    public byte[] convertToLittleEndian(byte[] data){
        ByteBuffer buffer = ByteBuffer.wrap(data);
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        buffer.rewind();
        byte[] orderedData = new byte[buffer.remaining()];
        buffer.get(orderedData);
        return orderedData;
    }
    
    // Function that converts a byte array to string
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
}
