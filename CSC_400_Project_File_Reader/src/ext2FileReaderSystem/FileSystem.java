package ext2FileReaderSystem;

/*
Authors: James Miller, Matthew Abney, Brian Spencer
Date: 12-3-19
Project: CSC 400 Group Project
EXT2 FILE SYSTEM
 */

import java.io.*;
import java.nio.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

public class FileSystem {
    
    public static void main(String[] args) throws IOException {
        Scanner in = new Scanner(System.in);
        
        //Put disk path here - ASK USER FOR DISK PATH 
        //WHICH EVER WAY YOU DECIDE - I DONT CARE
        ///Users/jthommiller/Documents/CSC_400_Project/CSC_400_Project_File_Reader
        File filePath = new File("/Users/jthommiller/Documents/CSC_400_Project/CSC_400_Project_File_Reader/virtdisk.dms");
        RandomAccessFile ext2Disk = new RandomAccessFile(filePath, "r");
        SuperBlock superblock = new SuperBlock(ext2Disk);
        GroupDescriptor groupDescriptor = new GroupDescriptor();
        GroupDescriptor[] groupDescriptorTable = groupDescriptor.createGroupDescriptorTable(superblock, 2);
        Directory rootDirectory = new Directory(superblock, groupDescriptorTable, 2);
        Directory currentDirectory = rootDirectory;
        ArrayList<Directory> subDirectories = new ArrayList();
        ArrayList<Inode> subFiles = new ArrayList();
        
        int size = rootDirectory.files.size();
        
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

        String commandLinePrompt;
        String command = "";
        String prompt = "";
        
        System.out.println("COMMAND MENU: ");
        System.out.println("cd <File Path- Change Directory");
        System.out.println("copy <File Name> - copy a file");
        System.out.println("dir - List Contents Of Current Directory");
        System.out.println("help - Display Command Menu");
        System.out.println("root - Return To Root Directory");
        System.out.println("stop - Quit File System");
        
        do{
            System.out.println();
            System.out.print("CMD>> ");
            commandLinePrompt = in.nextLine();
            
            if(commandLinePrompt.length() == 3 || commandLinePrompt.length() == 4){
                command = commandLinePrompt;
            }
            else{
            command = commandLinePrompt.substring(0, commandLinePrompt.indexOf(" "));
            prompt = commandLinePrompt.substring(commandLinePrompt.indexOf(" ")+1);
            }
            
            int directorySize;
            switch(command.toLowerCase()){
                case "cd":                    
                    int direcotrySize = currentDirectory.files.size();                    
                    for(int i = 0; i < direcotrySize; i++){
                        if(currentDirectory.files.get(i).name.equals(prompt)){
                            Directory directory = new Directory(superblock, groupDescriptorTable, currentDirectory.files.get(i).inode);
                            
                            currentDirectory = directory;
                            subFiles.clear();
                            subDirectories.clear();
                            
                            direcotrySize = currentDirectory.files.size();
                            
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
                case "copy":
                    Directory d = new Directory();
                    ReadFile rf = new ReadFile();
                    directorySize = currentDirectory.files.size();
                    for(int i = 0; i < directorySize; i++){
                        if(currentDirectory.files.get(i).name.equals(prompt)){
                            File file = new File(currentDirectory.files.get(i).name);
                            RandomAccessFile newFile = new RandomAccessFile(file, "rw");
                            ArrayList<Integer> blocks = new ArrayList();
                            ArrayList<Integer> AllBlocks = new ArrayList();
                            Inode fileInode = new Inode(superblock, groupDescriptorTable, currentDirectory.files.get(i).inode);
                            
                            for(int j = 0; j < 15; j++){
                                if(i < 12){
                                    blocks.add(fileInode.inode_blocks[i]);
                                }
                                else if(i == 12 && fileInode.inode_blocks[i] != 0){
                                    ArrayList<Integer> singleIndirect = d.getIndirectData(superblock, fileInode.inode_blocks[i], 1);
                                    size = singleIndirect.size();
                                    for(int k = 0; k < size; k++){
                                        blocks.add(singleIndirect.get(j));
                                    }
                                }
                                else if(i == 13 && fileInode.inode_blocks[i] != 0){
                                    ArrayList<Integer> doubleDirect = d.getIndirectData(superblock, fileInode.inode_blocks[i], 1);
                                    size = doubleDirect.size();
                                    for(int k = 0; k < size; k++){
                                        blocks.add(doubleDirect.get(j));
                                    }
                                }
                                else if(fileInode.inode_blocks[i] != 0){
                                    ArrayList<Integer> tripleDirect = d.getIndirectData(superblock, fileInode.inode_blocks[i], 1);
                                    size = tripleDirect.size();
                                    for(int k = 0; k < size; k++){
                                        blocks.add(tripleDirect.get(j));
                                    }
                                }
                            }
                            int arraySize = blocks.size();
                            for(int j = 0; j < size; j++){
                                int value = blocks.get(i);
                                if(value != 0){
                                    AllBlocks.add(value);
                                }
                            }
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
                case "dir":
                    directorySize = currentDirectory.files.size();
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
                case "help":
                    System.out.println("COMMAND MENU: ");
                    System.out.println("cd <File Path- Change Directory");
                    System.out.println("copy <File Name> - copy a file");
                    System.out.println("dir - List Contents Of Current Directory");
                    System.out.println("help - Display Command Menu");
                    System.out.println("root - Return To Root Directory");
                    System.out.println("stop - Quit File System");
                    break;
                case "root":
                    currentDirectory = rootDirectory;     
                    directorySize = currentDirectory.files.size();
        
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
                case "stop":
                    break;
                default:
                    System.out.println("Error: Invalid Command, please enter a"
                            + "valid command. For valid commands, enter 'help'.");
                    break;
            }
            
        }while (commandLinePrompt.toLowerCase().charAt(0) != 's' );
        System.out.println();
    }
}