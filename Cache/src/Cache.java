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
 
    
    /* Return the position of where CPU address has written into */
    public CacheResult directMap(int cpuAddress) throws IllegalArgumentException {
        
        //check if the cpu address is valid
        if(cpuAddress < 0 || cpuAddress >= ramSize){
            throw new IllegalArgumentException("Invalid CPU address to map");
        }
        
        // If top condition is valid, find RAM Block Number
        int ramBlockNo = cpuAddress / blockSize;
        
        //Then find the value of the offset
        int offset = cpuAddress % blockSize;
        
        //Cache index:
        int cacheIndex = ramBlockNo % nBlocks;
        
        // get the cache line at that index
        CacheLine line = cache[cacheIndex];

        // Hit or miss?
        if (line.valid && line.blockNumber == ramBlockNo) {
            return new CacheResult(cpuAddress, ramBlockNo, cacheIndex, offset, true);
        } else {
            // load block to Cache
            line.blockNumber = ramBlockNo;
            line.valid = true;
            return new CacheResult(cpuAddress, ramBlockNo, cacheIndex, offset, false);
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

        for (int i = 0; i< nBlocks; i++) {
            System.out.print("Cache Line "+i + ": "); 

            if (!cache[i].valid) {
                System.out.println("EMPTY");
            } else {
                System.out.println("RAM Block "+ cache[i].blockNumber );
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
        Cache c1 = new Cache(4,4,16);
        c1.directMap(12);
        c1.directMap(13);
        c1.print();
    }
    
}
