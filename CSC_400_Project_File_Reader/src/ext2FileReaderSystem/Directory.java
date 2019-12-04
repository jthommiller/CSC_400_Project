package ext2FileReaderSystem;

/*
Authors: James Miller, Matthew Abney, Brian Spencer
Date: 12-3-19
Project: CSC 400 Group Project
EXT2 FILE SYSTEM
 */

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Arrays;

// Handles the directory of the system
public class Directory {
    String directory_name;
    ArrayList<SubDirectory> files = new ArrayList();
    Inode inode;
    
    // Needed to setup filesystem
    Directory(){
        
    }
    
    // Starts to create the directory
    Directory(SuperBlock superblock, GroupDescriptor[] groupDescriptorTable, int inodeNumber) throws IOException{
        inode = new Inode(superblock, groupDescriptorTable, inodeNumber);
        ReadFile rf = new ReadFile();
        int size = 0;
        byte[] temp = new byte[1024];
        ArrayList<Integer> blocks = new ArrayList();
        ArrayList<Integer> AllBlocks = new ArrayList();
        
        // Makes sure that the inodes with indirect data are placed into singles, doubles, or triples
        for(int i = 0; i < 15; i++){
            
            // If there are no indirect blocks
            if(i < 12){
                blocks.add(inode.inode_blocks[i]);
            }
            
            // Handles single indirect inodes
            else if(i == 12 && inode.inode_blocks[i] != 0){
                ArrayList<Integer> singleIndirect = getIndirectData(superblock, inode.inode_blocks[i], 1);
                size = singleIndirect.size();
                for(int j = 0; j < size; j++){
                    blocks.add(singleIndirect.get(j));
                }
            }
            
            // Handles double indirect inodes
            else if(i == 13 && inode.inode_blocks[i] != 0){
                ArrayList<Integer> doubleDirect = getIndirectData(superblock, inode.inode_blocks[i], 1);
                size = doubleDirect.size();
                for(int j = 0; j < size; j++){
                    blocks.add(doubleDirect.get(j));
                }
            }
            
            // Handles triple indirect inodes
            else if(inode.inode_blocks[i] != 0){
                ArrayList<Integer> tripleDirect = getIndirectData(superblock, inode.inode_blocks[i], 1);
                size = tripleDirect.size();
                for(int j = 0; j < size; j++){
                    blocks.add(tripleDirect.get(j));
                }
            }
        }
        
        // Gets the entire directory into a single place
        size = blocks.size();
        for(int i = 0; i < size; i++){
            int value = blocks.get(i);
            if(value != 0){
                AllBlocks.add(value);
            }
        }
        
        // Adds the files to the directory
        size = AllBlocks.size();
        for(int i = 0; i < size; i++){
            int index = 0;
            temp = rf.read(superblock.disk, AllBlocks.get(i), temp);
            while(index < superblock.block_size){
                int dir = rf.processData(temp, index, index+3);
                    if(dir == 0){
                        break;
                    }
                    int dirSize = rf.processData(temp, index+4, index+5);
                    files.add(new SubDirectory(Arrays.copyOfRange(temp, index, index+dirSize)));
                    index += dirSize;
            }
        }
    }
    
    public ArrayList<Integer> getIndirectData(SuperBlock superblock, int index, int indirectType) throws IOException{
        // Gets all variables we need
        ReadFile rf = new ReadFile();
        RandomAccessFile disk = superblock.disk;
        ArrayList<Integer> List = new ArrayList();
        ArrayList<Integer> TempList = new ArrayList();
        ArrayList<Integer> TempList2 = new ArrayList();
        byte[] data = new byte[1024];
        byte[] data2 = new byte[1024];
        byte[] data3 = new byte[1024];
        int blockSize = superblock.block_size/4;
        int listSize = 0;
        
        // Checks how many indirect inodes is needed
        switch(indirectType){
                
            // Case for if only one inodes
            case 1: 
                data = rf.read(disk, index, data);
                for(int i = 0; i < blockSize; i++) {
                    int start = i * 4;
                    int end = ((i + 1) * 4) - 1;
                    int position = rf.processData(data, start, end);
                    if (position == 0){
                        break;
                    }
                    List.add(position);
                }
                
            // Case for if two inodes
            case 2:
                data2 = rf.read(disk, index, data2);
                for(int i = 0; i < blockSize; i++) {
                    int start = i * 4;
                    int end = ((i + 1) * 4) - 1;
                    int position = rf.processData(data2, start, end);
                    if (position == 0){
                        break;
                    }
                    TempList.add(position);
                }
                listSize = TempList.size();
                for(int i = 0; i < listSize; i++) {
                    data = rf.read(disk, TempList.get(i), data);
                    for(int j = 0; j < blockSize; j++){
                        int start = i * 4;
                        int end = ((i + 1) * 4) - 1;
                        int position = rf.processData(data, start, end);
                        //if its 0, no more blocks to process
                        if (position == 0){
                            break;
                        }
                        List.add(position);
                    }
                }
                
            // If the inodes isn't listed or have three handle it as if three inodes    
            default:
                data3 = rf.read(disk, index, data3);
                for(int i = 0; i < blockSize; i++) {
                    int start = i * 4;
                    int end = ((i + 1) * 4) - 1;
                    int position = rf.processData(data3, start, end);
                    if (position == 0){
                        break;
                    }
                    TempList2.add(position);
                }
                listSize = TempList2.size();
                for(int i = 0; i< listSize; i++){
                    data2 = rf.read(disk, TempList2.get(i), data2);
                    for(int j=0; j < blockSize; j++){
                        int start = i * 4;
                    int end = ((i + 1) * 4) - 1;
                    int position = rf.processData(data2, start, end);
                    if (position == 0){
                        break;
                    }
                    TempList.add(position);
                    }
                }
                listSize = TempList.size();
                for(int i = 0; i< listSize; i++){
                    data = rf.read(disk, TempList.get(i), data);
                    for(int j=0; j < blockSize; j++){
                        int start = i * 4;
                        int end = ((i + 1) * 4) - 1;
                        int position = rf.processData(data, start, end);
                        //if its 0, no more blocks to process
                        if (position == 0){
                            break;
                        }
                        List.add(position);
                    }
                }
        }
        return List;
    }
}
