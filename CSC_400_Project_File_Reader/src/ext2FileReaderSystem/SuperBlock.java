package ext2FileReaderSystem;

/*
Authors: James Miller, Matthew Abney, Brian Spencer
Date: 12-3-19
Project: CSC 400 Group Project
EXT2 FILE SYSTEM
 */

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.*;
import java.util.Arrays;

public class SuperBlock {
    RandomAccessFile disk;
    int block_count;
    int block_per_group;
    int block_size;
    int first_inode;
    int free_block_count;
    int free_inode_count;
    int fragment_per_group;
    int fragment_size;
    int inode_count;
    int inode_per_group;
    int inode_size;
    int OSID;
    int preall_block;
    int preall_dir_block;
    int signiture;
    int state; 
    byte[] volume_name;
    String volumeName;
    
        //constructing our superblock using the virtdisk file
        SuperBlock(RandomAccessFile virtdisk) throws IOException {
            //creating a readfile
            ReadFile rf = new ReadFile();
            
            //creating a byte buffer to assign attributes of our superblock
            ByteBuffer buffer;
            
            //byte array for our superblock of size 1024
            byte[] superblock = new byte[1024];
            
            //assigning virtdisk parameter to disk var
            disk = virtdisk;
            
            //essentially assigning each component's value based on the
            //documentation provided for the project
            //each attribute is indexed specifically
            superblock = rf.read(virtdisk, 1, superblock);
            inode_count = rf.processData(superblock, 0, 3);
            block_count = rf.processData(superblock, 4, 7);
            free_block_count = rf.processData(superblock, 12, 15);
            free_inode_count = rf.processData(superblock, 16, 19);
            block_size = 1024 << rf.processData(superblock, 24, 27);
            fragment_size = 1024 << rf.processData(superblock, 28, 31);
            block_per_group = rf.processData(superblock, 32, 35);
            fragment_per_group = rf.processData(superblock, 36, 39);
            inode_per_group = rf.processData(superblock, 40, 43);
            signiture = rf.processData(superblock, 56, 57);
            state = rf.processData(superblock, 58, 59);
            OSID = rf.processData(superblock, 72, 75);
            first_inode = rf.processData(superblock, 84, 87);
            inode_size = rf.processData(superblock, 88, 89);
            preall_block = rf.processData(superblock, 204, 204);
            preall_dir_block = rf.processData(superblock, 205, 205);
            volume_name = rf.convertToLittleEndian(Arrays.copyOfRange(superblock, 120, 136));
            volumeName = rf.convertToString(volume_name);
        }
}
