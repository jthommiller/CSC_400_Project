package ext2FileReaderSystem;

/*
Authors: James Miller, Matthew Abney, Brian Spencer
Date: 12-3-19
Project: CSC 400 Group Project
EXT2 FILE SYSTEM
 */

import java.io.IOException;
import java.util.Arrays;

public class GroupDescriptor {
    int group_number;
    int block_bitmap_location;
    int inode_bitmap_location;
    int inode_table_location;
    int free_blocks;
    int free_inodes;
    int number_of_directories;
    
    GroupDescriptor(){
        //default constructor -- no parameters passed as arguments
    }
    
    GroupDescriptor(byte[] location, int groupNumber){
        // creating a new readfile for components of group descriptor
        ReadFile rf = new ReadFile();
        
        //block bitmap location
        block_bitmap_location = rf.processData(location, 0,3);
        
        //inode bitmap location
        inode_bitmap_location = rf.processData(location, 4,7);
        
        //inode table location
        inode_table_location = rf.processData(location, 8,11);
        
        //# of free blocks
        free_blocks = rf.processData(location, 12,13);
        
        //# of free inodes
        free_inodes = rf.processData(location, 14,15);
        
        //# of directories
        number_of_directories = rf.processData(location, 16,17);
        
        //setting the object's group_number attribute to the passed groupNumber var
        group_number = groupNumber;
    }
    
    //creating a group descriptor table using a superblock and a strating block
    public GroupDescriptor[] createGroupDescriptorTable(SuperBlock superblock, int startingBlock) throws IOException{
        //creating a new readfile
        ReadFile rf = new ReadFile();
        
        //byte array for copy data
        byte[] copyData = new byte[1024];
        
        //temp byte array for data read
        byte[] tempDataRead = new byte[1024];
        
        //setting gropu count
        int groupCount = (int)Math.ceil(superblock.block_count / superblock.block_per_group);
        
        //setting block size for our table
        int blockSizeForTable = (int)Math.ceil((32*groupCount)/superblock.block_size);
        
        //setting indiv blocks
        int individualBlocks = superblock.block_size/32;
        
        //our list object made with groupCount parameter as an arg
        GroupDescriptor[] tableOfGroupDescriptors = new GroupDescriptor[groupCount];
        
        //nested loops to copy data and fill table of group descriptors
        for(int i = 0; i < blockSizeForTable; i++){
            tempDataRead = rf.read(superblock.disk, startingBlock+1, tempDataRead);
            for(int j = 0; j < individualBlocks; j++){
                int index = (i * individualBlocks + j);
                if(index >= groupCount){
                    break;
                }
                copyData = Arrays.copyOfRange(tempDataRead, j*32, (j+1)*32);
                tableOfGroupDescriptors[index] = new GroupDescriptor(copyData, index);
            }
        }
        return tableOfGroupDescriptors;
    }

}
