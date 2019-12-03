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
}
