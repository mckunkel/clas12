/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jlab.hipo;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Map;
import java.util.TreeMap;
import java.util.zip.Deflater;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 *
 * @author gavalian
 */
public class HipoByteUtils {
    
    public static final int MTU = 1024*1024;
    public static TreeMap<Integer,Integer> bitMap = HipoByteUtils.createBitMap();
    
    public static TreeMap<Integer,Integer>  createBitMap(){
        TreeMap<Integer,Integer> map = new TreeMap<Integer,Integer>();
        for(int loop = 0; loop < 32; loop++){
            int  integer_value = 0;
            for(int hb = 0 ; hb < loop; hb++){
                integer_value = integer_value | (1<<hb);
            }
            map.put(loop, integer_value);
        }
        return map;
    }
    

    /**
     * returns the byte array GZIP compressed.
     * @param count
     * @return 
     */
    public static byte[] gzip(byte[] ungzipped) {
        final ByteArrayOutputStream bytes = new ByteArrayOutputStream();        
        try {
            final GZIPOutputStream gzipOutputStream = new GZIPOutputStream(bytes);
            /*{
                {
                    this.def.setLevel(Deflater.BEST_COMPRESSION);
                }
            };*/
            
            gzipOutputStream.write(ungzipped);
            gzipOutputStream.close();
        } catch (IOException e) {
           // LOG.error("Could not gzip " + Arrays.toString(ungzipped));
            System.out.println("[iG5DataCompressor] ERROR: Could not gzip the array....");
        }
        return bytes.toByteArray();
    }
    /**
     * The fastest Ungzip implementation. See PageInfoTest in ehcache-constructs.
     * A high performance implementation, although not as fast as gunzip3.
     * gunzips 100000 of ungzipped content in 9ms on the reference machine.
     * It does not use a fixed size buffer and is therefore suitable for arbitrary
     * length arrays.
     * 
     * @param gzipped
     * @return a plain, uncompressed byte[]
     */
    public static byte[] ungzip(final byte[] gzipped) {
        byte[] ungzipped = new byte[0];
        int    internalBufferSize  = 1*1024*1024;
        try {
            final GZIPInputStream inputStream = new GZIPInputStream(new ByteArrayInputStream(gzipped),1024*1024);
            
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(gzipped.length);
            final byte[] buffer = new byte[HipoByteUtils.MTU];
            int bytesRead = 0;
            while (bytesRead != -1) {
                bytesRead = inputStream.read(buffer, 0, HipoByteUtils.MTU);
                if (bytesRead != -1) {
                    byteArrayOutputStream.write(buffer, 0, bytesRead);
                }
            }
            ungzipped = byteArrayOutputStream.toByteArray();
            inputStream.close();
            byteArrayOutputStream.close();
        } catch (IOException e) {
            //LOG.error("Could not ungzip. Heartbeat will not be working. " + e.getMessage());
            System.out.println("[iG5DataCompressor] ERROR: could not uncompress the array. "
                    + e.getMessage());
        }
        return ungzipped;
    }

    public static byte[] generateByteArray(int count){
        byte[] array = new byte[count];
        byte data = 0;
        for(int i = 0; i < count; i++){
            array[i] = data;
            data++;
            if(data>125) data = 0;
        }
        return array;
    }
    
    public static void printBitMap(){
        for(Map.Entry<Integer,Integer> entry : bitMap.entrySet()){
            System.out.println(String.format("%4d : ", entry.getKey()) 
                    + String.format("%32s", Integer.toBinaryString(entry.getValue())).replace(' ', '0'));
        }
    }
    
    public static int getInteger(int data, int bitstart, int bitend){
        int length = bitend - bitstart + 1;
        if(HipoByteUtils.bitMap.containsKey(length)==true){
            int value = ((data>>bitstart)&HipoByteUtils.bitMap.get(length));
            return value;
        } else {
            System.out.println("[DataUtilities] : ERROR length = " + length);
        }
        return 0;
    }
    
    public static short getShortFromByte(byte data){
        short short_data = 0;
        return (short) ((short_data|data)&0x00FF);
    }
    
    public static int  getIntFromShort(short data){
        int int_data = 0;
        return (int) ( (int_data|data)&0x0000FFFF);
    }
    
    public static int  getIntFromByte(byte data){
        int int_data = 0;
        return (int) ( (int_data|data)&0x0000FFFF);
    }
    
    public static short  getShortFromInt(int data){
        short int_data = 0;
        return (short) ( (int_data|data)&0xFFFF);
    }
    
    public static byte   getByteFromInt(int data){
        byte byte_data = 0;
        return (byte) ((byte_data|data)&0xFF);
    }
    /**
     * returns a byte from short number, anything that exceeds the number that
     * fits in the byte they will be thrown away.
     * @param data short data (2 bytes)
     * @return a byte which is lower part of the given short (1 - byte)
     */
    public static byte   getByteFromShort(short data){
        byte byte_data = 0;
        return (byte) ((byte_data|data)&0xFF);
    }
    /**
     * returns byte string representation of the given integer, with 
     * 32 bits 0 and 1.
     * @param word reference integer word
     * @return 
     */
    public static String getByteString(int word){
        String strL = String.format("%32s", Integer.toBinaryString(word)).replace(' ', '0');
        StringBuilder str = new StringBuilder();
        for(int i = 0; i < 32; i++){
            str.append(strL.charAt(i));
            if((i+1)%8==0) str.append(" ");
        }
        return str.toString();
    }
    /**
     * write a number to a specific bits of existing integer. the specified
     * bits are reset to 0 first then the provided number is written into 
     * specified bits
     * @param word reference integer word (4 bits) is not changed
     * @param number and integer number written (user should check if the number fits)
     * @param start starting bits (first bit is 0)
     * @param end ending bit (for one bit start=end)
     * @return returns a new integer with bits written
     */
    public static int write(final int word, int number, int start, int end){
        int index = end - start + 1;
        int result = (word & ~(HipoByteUtils.bitMap.get(index)<<(start)));
        result = result|( (number&HipoByteUtils.bitMap.get(index))<<(start)) ;
        return result;
    }
    /**
     * read specific bit from given integer.
     * @param word integer reference word
     * @param start starting bit (0 being the first one)
     * @param end   ending bit (for reading 1 bit start must be = to end)
     * @return 
     */
    public static int read(final int word, int start, int end){
        int index = end-start+1;
        return (word>>(start))&HipoByteUtils.bitMap.get(index);
    }
    
    public static void main(String[] args){
        //BioByteUtils.printBitMap();
        //System.out.println(BioByteUtils.getByteString(1245678));
        int header  = 0x00FFFFFF;
        int headerM = HipoByteUtils.write(header, 4, 10, 12);
        int headerR = HipoByteUtils.read(headerM, 10,12);
        
        System.out.println(HipoByteUtils.getByteString(header));
        System.out.println(HipoByteUtils.getByteString(headerM));
        System.out.println(HipoByteUtils.getByteString(headerR));
        
        byte[] buffer = HipoByteUtils.generateByteArray(1845000);
        System.out.println("Length = " + buffer.length);
        byte[] compressed = HipoByteUtils.gzip(buffer);
        System.out.println("compressed length = " + compressed.length);
        
        long stime_ = System.currentTimeMillis();
        for(int loop = 0; loop < 50000; loop++){
            byte[] dc = HipoByteUtils.ungzip(compressed);
        }
        long etime_ = System.currentTimeMillis();
        double time = (etime_-stime_)/1000.0;
        System.out.println("Deflate speed = " + time + " sec");
    }
}
