package ext2FileReaderSystem;

/*
Authors: James Miller, Matthew Abney, Brian Spencer
Date: 12-3-19
Project: CSC 400 Group Project
EXT2 FILE SYSTEM
 */

import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;

public class FileSystem {
    
    public static void main(String[] args) throws IOException {
        Scanner in = new Scanner(System.in);
        
        //default system directory
        String path = System.getProperty("user.dir");
        
        // subfolders to our src, package, and file
        File filePath = new File(path + "//src//ext2FileReaderSystem//virtdisk");
        
        // Sets up information we need
        RandomAccessFile ext2Disk = new RandomAccessFile(filePath, "r");
        SuperBlock superblock = new SuperBlock(ext2Disk);
        GroupDescriptor groupDescriptor = new GroupDescriptor();
        GroupDescriptor[] groupDescriptorTable = groupDescriptor.createGroupDescriptorTable(superblock, 2);
        Directory rootDirectory = new Directory(superblock, groupDescriptorTable, 2);
        Directory currentDirectory = rootDirectory;
        ArrayList<Directory> subDirectories = new ArrayList();
        ArrayList<Inode> subFiles = new ArrayList();
        
        int size = rootDirectory.files.size();
        
        // Tells what is in the current place in directory
        for(int i = 0; i < size; i++){
            int typeOfContents = currentDirectory.files.get(i).type;
            //type = 1 -> file
            if(typeOfContents == 1){
                Inode newInode = new Inode(superblock, groupDescriptorTable, currentDirectory.files.get(i).inode);
                subFiles.add(newInode);
            }
            //type = 2 -> directory
            else if(typeOfContents == 2){
                Directory newDirectory = new Directory(superblock, groupDescriptorTable, currentDirectory.files.get(i).inode);
                subDirectories.add(newDirectory);
            }
        }

        // Variables for later
        String commandLinePrompt;
        String command = "";
        String prompt = "";
        
        // Sets up command prompt menu
        System.out.println("COMMAND MENU: ");
        System.out.println("cd <File Path- Change Directory");
        System.out.println("copy <File Name> - copy a file");
        System.out.println("dir - List Contents Of Current Directory");
        System.out.println("help - Display Command Menu");
        System.out.println("root - Return To Root Directory");
        System.out.println("stop - Quit File System");
        
        // Allows the user to navigate the ext2 file system
        do{
            System.out.println();
            System.out.print("CMD>> ");
            commandLinePrompt = in.nextLine();
            
            // Seperates the command from the file/directory
            if(commandLinePrompt.length() == 3 || commandLinePrompt.length() == 4){
                command = commandLinePrompt;
            }
            else{
                command = commandLinePrompt.substring(0, commandLinePrompt.indexOf(" "));
                prompt = commandLinePrompt.substring(commandLinePrompt.indexOf(" ")+1);
            }
            
            // Gets the directory size
            int directorySize;
            
            // Checks which command was used
            switch(command.toLowerCase()){
                    
                // If they chose to change directory
                case "cd":                    
                    int direcotrySize = currentDirectory.files.size();   
                    
                    // Sets up the new directory
                    for(int i = 0; i < direcotrySize; i++){
                        if(currentDirectory.files.get(i).name.equals(prompt)){
                            Directory directory = new Directory(superblock, groupDescriptorTable, currentDirectory.files.get(i).inode);
                            
                            // Clears out current directly information
                            currentDirectory = directory;
                            subFiles.clear();
                            subDirectories.clear();
                            
                            direcotrySize = currentDirectory.files.size();
                            
                            // Tells what is in the new place in directory
                            for (int j = 0; j < direcotrySize; j++) {
                                int typeOfContents = currentDirectory.files.get(j).type;
                                //type = 1 -> file
                                if (typeOfContents == 1) {
                                    Inode newInode = new Inode(superblock, groupDescriptorTable, currentDirectory.files.get(j).inode);
                                    subFiles.add(newInode);
                                }
                                //type = 2 -> directory
                                else if (typeOfContents == 2) {
                                    Directory newDir = new Directory(superblock, groupDescriptorTable, currentDirectory.files.get(j).inode);
                                    subDirectories.add(newDir);
                                }
                            }
                            break;
                        }
                    }
                    break;
                    
                // If they chose to copy a file
                case "copy":
                    
                    // Sets up important info
                    Directory d = new Directory();
                    ReadFile rf = new ReadFile();
                    directorySize = currentDirectory.files.size();
                    
                    // Begins the copy process
                    for(int i = 0; i < directorySize; i++){
                        if(currentDirectory.files.get(i).name.equals(prompt)){
                            
                            // Sets up needed variables
                            File file = new File(currentDirectory.files.get(i).name);
                            RandomAccessFile newFile = new RandomAccessFile(file, "rw");
                            ArrayList<Integer> blocks = new ArrayList();
                            ArrayList<Integer> AllBlocks = new ArrayList();
                            Inode fileInode = new Inode(superblock, groupDescriptorTable, currentDirectory.files.get(i).inode);
                            
                            // Makes sure that the inodes with indirect data are placed into singles, doubles, or triples
                            for(int j = 0; j < 15; j++){
                                
                                // If there are no indirect blocks
                                if(i < 12){
                                    blocks.add(fileInode.inode_blocks[i]);
                                }
                                // Handles single indirect inodes
                                else if(i == 12 && fileInode.inode_blocks[i] != 0){
                                    ArrayList<Integer> singleIndirect = d.getIndirectData(superblock, fileInode.inode_blocks[i], 1);
                                    size = singleIndirect.size();
                                    for(int k = 0; k < size; k++){
                                        blocks.add(singleIndirect.get(j));
                                    }
                                }
                                // Handles double indirect inodes
                                else if(i == 13 && fileInode.inode_blocks[i] != 0){
                                    ArrayList<Integer> doubleDirect = d.getIndirectData(superblock, fileInode.inode_blocks[i], 1);
                                    size = doubleDirect.size();
                                    for(int k = 0; k < size; k++){
                                        blocks.add(doubleDirect.get(j));
                                    }
                                }
                                // Handles triple indirect inodes
                                else if(fileInode.inode_blocks[i] != 0){
                                    ArrayList<Integer> tripleDirect = d.getIndirectData(superblock, fileInode.inode_blocks[i], 1);
                                    size = tripleDirect.size();
                                    for(int k = 0; k < size; k++){
                                        blocks.add(tripleDirect.get(j));
                                    }
                                }
                            }
                            
                            // Gets the entire directory into a single place
                            int arraySize = blocks.size();
                            for(int j = 0; j < size; j++){
                                int value = blocks.get(i);
                                if(value != 0){
                                    AllBlocks.add(value);
                                }
                            }
                            
                            // Reads all the copied blocks
                            arraySize = AllBlocks.size();
                            byte[] temp = new byte[1024];
                            for(int j = 0; j < size; j++){
                                int index = 0;
                                temp = rf.read(superblock.disk, AllBlocks.get(i), temp);
                                newFile.write(temp);
                            }
                        }
                    }
                    break;
                    
                // If they choose to reshow all the files in the current directory location
                case "dir":
                    directorySize = currentDirectory.files.size();
                    
                    // Checks if current value is a file or folder and prints it
                    for(int i = 0; i < directorySize; i++){
                        if(currentDirectory.files.get(i).type == 1) {
                            System.out.print(currentDirectory.files.get(i).name);
                            System.out.print("                  ");
                            Inode data = new Inode(superblock, groupDescriptorTable, currentDirectory.files.get(i).inode);
                            System.out.println((int)Math.ceil((double)data.size/(double)1024) + "mb");
                        }
                        else if(currentDirectory.files.get(i).type == 2) {
                            if(currentDirectory.files.get(i).name.equals("."))
                                continue;
                            System.out.println(currentDirectory.files.get(i).name + "/");
                        }
                    }
                    break;
                    
                // If the user chooses to reprint the menu of commands
                case "help":
                    System.out.println("COMMAND MENU: ");
                    System.out.println("cd <File Path- Change Directory");
                    System.out.println("copy <File Name> - copy a file");
                    System.out.println("dir - List Contents Of Current Directory");
                    System.out.println("help - Display Command Menu");
                    System.out.println("root - Return To Root Directory");
                    System.out.println("stop - Quit File System");
                    break;
                    
                // If they choose to go back to the root of the file system
                case "root":
                    currentDirectory = rootDirectory;     
                    directorySize = currentDirectory.files.size();
                    
                    // Tells what is in the new place in directory
                    for(int i = 0; i < directorySize; i++){
                        int typeOfContents = currentDirectory.files.get(i).type;
                        //type = 1 -> file
                        if(typeOfContents == 1){
                            Inode newInode = new Inode(superblock, groupDescriptorTable, currentDirectory.files.get(i).inode);
                            subFiles.add(newInode);
                        }
                        //type = 2 -> directory
                        else if(typeOfContents == 2){
                            Directory newDirectory = new Directory(superblock, groupDescriptorTable, currentDirectory.files.get(i).inode);
                            subDirectories.add(newDirectory);
                        }
                    }
                    break;
                // If they choose to stop
                case "stop":
                    break;
                // If the command wasn't valid
                default:
                    System.out.println("Error: Invalid Command, please enter a"
                            + "valid command. For valid commands, enter 'help'.");
                    break;
            }
            
        }while (commandLinePrompt.toLowerCase().charAt(0) != 's' );
        System.out.println();
    }
}
