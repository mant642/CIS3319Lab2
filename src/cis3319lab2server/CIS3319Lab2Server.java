/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cis3319lab2server;

import java.net.*; 
import javax.crypto.*;
import java.security.*;
import java.io.*;
import java.util.Arrays;
import java.util.Scanner;

/**
 *
 * @author MAnthony
 */
public class CIS3319Lab2Server {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws Exception {
        // Setting up the socket 
        ServerSocket server = new ServerSocket(6400);
        Socket s = server.accept();
        
        // Reading the key from the file the client wrote to 
        ObjectInputStream in = new ObjectInputStream(new FileInputStream("/Users/MAnthony/Documents/cis3319practice3.txt"));
        Key key = (Key) in.readObject();
        in.close();
        
        // 
        ObjectInputStream in2 = new ObjectInputStream(new FileInputStream("/Users/MAnthony/Documents/cis3319hmac.txt"));
        Key key2 = (Key) in2.readObject();
        in2.close(); 
        
        // Setting up the Data Input and Output streams 
        DataInputStream dis = new DataInputStream(s.getInputStream());
        DataOutputStream dout = new DataOutputStream (s.getOutputStream());
        
        Scanner sc = new Scanner (System.in);
        
        String message;
        String message2;

        // Cipher created with DES parameters, initiated in decrypt mode 
        Cipher cipher = Cipher.getInstance("DES/ECB/NoPadding");
        cipher.init(Cipher.DECRYPT_MODE, key);
        
        // Creates a MAC object configured for HmacSHA256, initializes with Hmac key 
        Mac mac = Mac.getInstance("HmacSHA256"); 
        mac.init(key2);
        
        // New method: Receives the combined message+HMAC, decrypts, and splits back into two separate arrays 
        byte[] cipherText = new byte[48];
        dis.read(cipherText);
        System.out.println("Ciphertext = " + Arrays.toString(cipherText));
        byte[] plainAndMac = cipher.doFinal(cipherText);
        byte[] plainText = new byte[16];
        // To split plainAndMac, using copyOfRange method 
        plainText.copyOfRange(plainAndMac, 0, 15);
        
        // Original method of receiving receiving cipherText, decrypts plainText and derives HMAC from it 
        
        /*
        // Receiving the client's ciphertext from the DataInputStream, decrypting with the cipher, and printing to screen 
        byte[] cipherText = new byte[16];
        dis.read(cipherText);
        System.out.println("Ciphertext = " + Arrays.toString(cipherText));
        byte[] plainText = cipher.doFinal(cipherText);
        
        // Generate HMAC for comparison 
        byte[] macBytes = mac.doFinal(plainText);
        System.out.println("HMAC = " + Arrays.toString(macBytes));
        
        message = new String(plainText);
        System.out.println("Key = " + Arrays.toString(key.getEncoded()));
        System.out.println(message);
        */
        
        // Beginning Server talkback 
         
        message2 = sc.nextLine();
        System.out.println("Message converted from Bytes = " + new String(message2.getBytes()));
        System.out.println("Length = " + message2.getBytes().length);
        System.out.println("Key = " + Arrays.toString(key.getEncoded()));
        
        // Cipher switched to encrypt mode 
        cipher.init(Cipher.ENCRYPT_MODE, key);
        
        // Server response turned into byte array, which is encrypted by the cipher object, and sent over the DataOutputStream 
        byte[] plainText2 = message2.getBytes();
        byte[] cipherText2 = cipher.doFinal(plainText2);
        System.out.println("Ciphertext = " + Arrays.toString(cipherText2));
        
        dout.write(cipherText2, 0, cipherText2.length);
        dout.flush();
        dis.close();
        s.close();
        server.close();
        
        // Original implementation, uses the CipherInputStream object 
        /*
        CipherInputStream cipherIn = new CipherInputStream(s.getInputStream(), cipher);
        
        byte[] array = new byte[10000];
        cipherIn.read(array);
        cipherIn.close();
        s.close(); 
        
        String message = new String (array);
        System.out.println(message);
         */
    }
    
}
