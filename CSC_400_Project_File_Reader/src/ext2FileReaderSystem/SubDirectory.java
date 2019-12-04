package ext2FileReaderSystem;

/*
Authors: James Miller, Matthew Abney, Brian Spencer
Date: 12-3-19
Project: CSC 400 Group Project
EXT2 FILE SYSTEM
 */

import java.util.Arrays;

//constructing a subdirectory using a byte array
public class SubDirectory {
    int inode;
    int length;
    int length_of_name;
    int type;
    byte[] byte_name;
    String name;
    
    
    //constructing a subdirectory using a byte array
    SubDirectory(byte[] data){
        //creating a readfile
        ReadFile rf = new ReadFile();
        
        //attributes set using static index values
        inode = rf.processData(data, 0, 3);
        length = rf.processData(data, 4, 5);
        length_of_name = rf.processData(data, 6, 6);
        type = rf.processData(data, 7, 7);
        byte_name = rf.convertToLittleEndian(Arrays.copyOfRange(data, 8, length+8));
        name = rf.convertToString(byte_name);
    }
    
}
