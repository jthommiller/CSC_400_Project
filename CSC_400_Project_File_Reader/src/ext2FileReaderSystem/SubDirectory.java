/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ext2FileReaderSystem;

import java.util.Arrays;

/**
 *
 * @author jthommiller
 */
public class SubDirectory {
    int inode;
    int length;
    int length_of_name;
    int type;
    byte[] byte_name;
    String name;
    
    SubDirectory(byte[] data){
        ReadFile rf = new ReadFile();
        inode = rf.processData(data, 0, 3);
        length = rf.processData(data, 4, 5);
        length_of_name = rf.processData(data, 6, 6);
        type = rf.processData(data, 7, 7);
        byte_name = rf.convertToLittleEndian(Arrays.copyOfRange(data, 8, length+8));
        name = rf.convertToString(byte_name);
    }
    
}