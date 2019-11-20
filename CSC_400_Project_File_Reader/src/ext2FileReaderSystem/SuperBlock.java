package ext2FileReaderSystem;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
SuperBlock Class
	- Inode Count, offset 0, 4 bytes long
	- Number of blocks, offset 4, 4 bytes long
	- Block Size, offset 24, 4 bytes long
	- Blocks Per Group, offset 32, 4 bytes long
	- Inodes Per Group, offset 40, 4 bytes long
	- Inode Size, offset 88, 2 bytes long
	- Hash Seed, offset 236, 4x4 bytes long 


Determining the Number of Block Groups
From the Superblock, extract the size of each block, the total number of inodes, the total number of blocks, 
the number of blocks per block group, and the number of inodes in each block group. From this information we 
can infer the number of block groups there are by:

	- Rounding up the total number of blocks divided by the number of blocks per block group
	- Rounding up the total number of inodes divided by the number of inodes per block group
	- Both (and check them against each other)
 */
public class SuperBlock {
    private final int block_count;
    private final int block_per_group;
    private final int block_size;
    private final int[][] hash_seed = new int[4][4];
    private final int inode_count;
    private final int inode_per_group;
    private final int inode_size;
        
        SuperBlock(int blkSize) {
            block_size = blkSize;
        }
}
