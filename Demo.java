/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */

/**
 *
 * @author Boshra
 */
public class Demo {

    /**
     * @param args the command line arguments
     */
    
    
    
    
    public static void main(String[] args) {

        
        Cache cache = new Cache(4, 4, 32);
        
        System.out.println("=== DIRECT MAPPING === ");
        CacheResult r1 = cache.directMap(5);
        System.out.println(r1);
        
        CacheResult r2 = cache.directMap(5);  
        System.out.println(r2);

        CacheResult r3 = cache.directMap(21); 
        System.out.println(r3);

        CacheResult r4 = cache.directMap(5);  
        System.out.println(r4);
        
        cache.print();
        
        System.out.println("\n=== FULLY ASSOCIATIVE MAPPING ===");
        cache = new Cache(4, 4, 32); 
        
        CacheResult f1 = cache.fullyMap(5);   
        System.out.println(f1);
        CacheResult f2 = cache.fullyMap(9);   
        System.out.println(f2);
        CacheResult f3 = cache.fullyMap(21);  
        System.out.println(f3);
        CacheResult f4 = cache.fullyMap(5);  
        System.out.println(f4);
        
        cache.print();
        
         System.out.println("\n=== SET ASSOCIATIVE MAPPING ===");
         cache = new Cache(4, 4, 32); 
         
        CacheResult s1 = cache.setMap(5);   
        System.out.println(s1);
        CacheResult s2 = cache.setMap(21); 
        System.out.println(s2);
        CacheResult s3 = cache.setMap(5);   
        System.out.println(s3);
        CacheResult s4 = cache.setMap(9);   
        System.out.println(s4);
        
        cache.print();
    }
    
}
