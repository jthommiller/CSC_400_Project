package ext2FileReaderSystem;

/*
Authors: James Miller, Matthew Abney, Brian Spencer
Date: 12-3-19
Project: CSC 400 Group Project
EXT2 FILE SYSTEM
 */

import java.io.IOException;
import java.util.Arrays;

public class Inode {
    int inodeID;
    int mode;
    int UID;
    int size;
    int GID;
    int link_count;
    int inode_data_blocks;
    int[] inode_blocks = new int[15];
    
    Inode(byte[] data){
        ReadFile rf = new ReadFile();
        mode = rf.processData(data, 0, 1);
        UID = rf.processData(data, 2, 3);
        size = rf.processData(data, 4, 7);
        GID = rf.processData(data, 24, 25);
        link_count = rf.processData(data, 26, 27);
        inode_data_blocks = rf.processData(data, 28, 31);
        for(int i = 0; i < 15; i++){
            int offset = 40;
            inode_blocks[i] = rf.processData(data, offset, offset+3);
            offset += 4;
        }
    }
    
    Inode(SuperBlock superblock, GroupDescriptor[] GroupDescriptorTable, int inodeNumber) throws IOException{
        ReadFile rf = new ReadFile();
        int group = (inodeNumber-1)/superblock.inode_per_group;
        
        int inodesPerBlock = superblock.block_size/superblock.inode_size;
        int GroupID = (inodeNumber-1) % superblock.inode_per_group;
        int iTableLocation = GroupDescriptorTable[group].inode_table_location;
        int block = iTableLocation + (GroupID / inodesPerBlock);
        
        int index = (inodeNumber-1) % inodesPerBlock;
        
        byte[] inodeDiskData = new byte[1024];
        byte[] inodDiskData = rf.read(superblock.disk, (block*superblock.block_size/1024), inodeDiskData);
        byte[] inodeData = Arrays.copyOfRange(inodeDiskData, (superblock.inode_size * index), (superblock.inode_size*(index+1)));
        
        mode = rf.processData(inodeData, 0, 1);
        UID = rf.processData(inodeData, 2, 3);
        size = rf.processData(inodeData, 4, 7);
        GID = rf.processData(inodeData, 24, 25);
        link_count = rf.processData(inodeData, 26, 27);
        inode_data_blocks = rf.processData(inodeData, 28, 31);
        for(int i = 0; i < 15; i++){
            int offset = 40;
            inode_blocks[i] = rf.processData(inodeData, offset, offset+3);
            offset += 4;
        }

    }
}

