
import java.util.HashSet;
import java.util.Set;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */

/**
 *
 * @author yuna
 */
public class Cache {
    
    int nBlocks; // no of cache lines in cache
    int blockSize;   //line size = block size THIS IS CACHE AND RAM
    int ramSize;  // THIS IS RAM
    
    CacheLine[] cache; // cache line where data is mapped to 
    Set<Integer> seenBlocks = new HashSet<>(); // All TAM blocks that were prevousely accesed
 
    
    // cpuAddress => location in RAM that the CPU wants to read or write
    // convert cpuAddress to RAM block, then map to cache
    public CacheResult directMap(int cpuAddress) throws IllegalArgumentException {
        
        //check if the cpu address is valid
        if(cpuAddress < 0 || cpuAddress >= ramSize){
            throw new IllegalArgumentException("Invalid CPU address to map");
        }
        
        // If top condition is valid, find RAM Block Number
        // how many full blocks fit before this address
        //eg: 45/4 = 11 blocks so we are on the 11th block, but we dont know the offset yet.
        // Another approach would be to convert 45 to binary and find how many bits are block bits, add them up afterwards:
        //eg: assume Ram size = 64
        //           block size =4
        //           number of blocks = 64/ 4 = 16
        //           cpuAddress = 45
        //     no of bits in cpu address alllows = log base 2 (ram size) = 6 bits
        //     45 in 6 bits = 101101
        //     amt of bits representing block number = log base 2(ram size) - log base 2 (block size) = 6 - 2 = 4 bits from the right side (after offset)
        //     offset bits log base 2 (block size) = 2 bits from the right
        int ramBlockNo = cpuAddress / blockSize;
        
        //Then find the value of the offset
        // the remainder of the division to find ram block number gives us the offset
        // 45 % 4 = 45-(4*11) = 1 so we are at offset index position 1 
        int offset = cpuAddress % blockSize;
        
        //Cache index:
        // allows for many to one mapping as direct mapping works
        // 
        int cacheIndex = ramBlockNo % nBlocks;
        
        // get the cache line at that index
        CacheLine line = cache[cacheIndex];
        
        // types of misses
        String missType = "";

        // Hit or miss?
        if (line.valid && line.blockNumber == ramBlockNo) {
            missType = "none";
            return new CacheResult(cpuAddress, ramBlockNo, cacheIndex, offset, true, missType);
        } else {
            
            //find out miss type
            if (!seenBlocks.contains(ramBlockNo)) {
                missType = "cold";
                seenBlocks.add(ramBlockNo);
            } else if (line.valid && line.blockNumber != ramBlockNo) {
                missType = "conflict";
                 //conflic miss only if cache is smaller then ram --> not 1-to-1
                // conflic miss if:
                //     17 = 01[00][01]
                //     1 = 00[00][01]
                //         tag block offset
                //              block and offeset identical == miss
            } else {
                missType = "Cap / other";
            }
            
            // load block to Cache
            line.blockNumber = ramBlockNo;
            line.valid = true;
            return new CacheResult(cpuAddress, ramBlockNo, cacheIndex, offset, false, missType);
        }

    }
   
    //todo
    public CacheResult fullyMap(int cpuAddress) { return null;}
    
    //todo
    public CacheResult setMap(int cpuAddress) {return null;}
    
    
    
    /*	MUST STORE:
        Cache block number 
	Stored RAM block 
	Valid/empty status 
    */
    public void print() {
        System.out.println("\n------- CACHE STATE ------");

        for (int i = 0; i < nBlocks; i++) {
            System.out.print("Cache Block "+ i + ": "); 

            if (!cache[i].valid) {
                System.out.println("EMPTY");
            } else {
                System.out.println("VALID: RAM Block "+ cache[i].blockNumber );
            }
        }

        System.out.println("--------------------------\n");
    }

    
    
    //Constructor
    public Cache(int nBlocks, int blockSize, int ramSize) {
        this.nBlocks = nBlocks;
        this.blockSize = blockSize;
        this.ramSize = ramSize;
        
        this.cache = new CacheLine[nBlocks];
        for(int i = 0; i < nBlocks; i++){
            cache[i] = new CacheLine();
        }

    }

    
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
       
        
        
        // Cache: number of blocks, size of blocks, RAM size
        Cache c1 = new Cache(4,4,64);
        CacheResult r1 = c1.directMap(1);
        CacheResult r2 = c1.directMap(17);
        CacheResult r3 = c1.directMap(1);
        c1.print();
        System.out.println(r1.toString() + "\n" + r2.toString() + "\n" + r3.toString());
        
    }
    
}
