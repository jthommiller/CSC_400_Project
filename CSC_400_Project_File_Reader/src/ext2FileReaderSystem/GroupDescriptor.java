package ext2FileReaderSystem;

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
        
    }
    
    GroupDescriptor(byte[] location, int groupNumber){
        ReadFile rf = new ReadFile();
        block_bitmap_location = rf.processData(location, 0,3);
        inode_bitmap_location = rf.processData(location, 4,7);
        inode_table_location = rf.processData(location, 8,11);
        free_blocks = rf.processData(location, 12,13);
        free_inodes = rf.processData(location, 14,15);
        number_of_directories = rf.processData(location, 16,17);
        group_number = groupNumber;
    }
    
    public GroupDescriptor[] createGroupDescriptorTable(SuperBlock superblock, int startingBlock) throws IOException{
        ReadFile rf = new ReadFile();
        byte[] copyData = new byte[1024];
        byte[] tempDataRead = new byte[1024];
        int groupCount = (int)Math.ceil((double)superblock.block_count / (double)superblock.block_per_group);
        int blockSizeForTable = (int)Math.ceil((double)(32*groupCount)/(double)superblock.block_size);
        int individualBlocks = superblock.block_size/32;
        GroupDescriptor[] tableOfGroupDescriptors = new GroupDescriptor[groupCount];
        
        for(int i = 0; i < blockSizeForTable; i++){
            tempDataRead = rf.read(superblock.disk, startingBlock+i, tempDataRead);
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
