
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
    
    int nBlocks; // no of cache lines/ blocks in cache
    int blockSize;   //line size = block size THIS IS CACHE AND RAM
    int ramSize;  // THIS IS RAM
    
    int nReplaceI = 0; // for accociative mapping, specifically picking victim block using FIFO 
    
    CacheLine[] cache; // cache line where data is mapped to 
    Set<Integer> seenBlocks = new HashSet<>(); // All TAM blocks that were prevousely accesed
 
    
    //-----------------------------------------------------------------------------------------------------------------------------------------
    //Direct: 1-1/1-many mapping, depends on cache and Ram size
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
        // allows for many to one mapping as direct mapping works that way
        int lineNumber = ramBlockNo % nBlocks;
        
        // get the cache line at that index
        CacheLine line = cache[lineNumber];
        
        // types of misses
        String missType = "";

        // Hit or miss?
        if (line.valid && line.blockNumber == ramBlockNo) {
            missType = "none";
            return new CacheResult(cpuAddress, ramBlockNo, lineNumber, offset, true, missType);
        } else {
            
            //find out miss type
            if (!seenBlocks.contains(ramBlockNo)) {
                missType = "cold";
                seenBlocks.add(ramBlockNo);
            } else if (line.valid && line.blockNumber != ramBlockNo) {
                missType = "conflict";
                 //conflic miss only if cache is smaller then ram --> not 1-to-1
                // conflic miss if:
                //     17 = 01 [00] [01]
                //     1 = 00 [00]  [01]
                //        tag block offset
                //              block and offeset identical == miss
            } else {
                missType = "Capacity / other";
            }
            
            // load block to Cache line, updates line to carry new RAM block
            line.blockNumber = ramBlockNo; // replaces the existing block with the new one
            line.valid = true;
            return new CacheResult(cpuAddress, ramBlockNo, lineNumber, offset, false, missType);
        }

    }
   
    //-----------------------------------------------------------------------------------------------------------------------------------------
    public CacheResult fullyMap(int cpuAddress) { 
        
        //check if the cpu address is valid
        if(cpuAddress < 0 || cpuAddress >= ramSize){
            throw new IllegalArgumentException("Invalid CPU address to map");
        }
        //find ram block number
        int ramBlockNo = cpuAddress / blockSize;
        //find offset number
        int offset = cpuAddress % blockSize;
        
        //search entire cache for hit. associative -> no fixed index, use tag to search parallel-searcg and find the block
        for( int i = 0; i < cache.length; i++){
            if(cache[i].valid && cache[i].blockNumber == ramBlockNo){ // "cache[i].blockNumber == ramBlockNo" --> tag comparison
                return new CacheResult(cpuAddress, ramBlockNo, i, offset, true, "none");
            }
        }
        
        // if the hit is not found; check what type of miss is present
        // associative mapping: no conflict misses at all
        String missType = "";
        
        if(!seenBlocks.contains(ramBlockNo)){
            missType = "cold";
            seenBlocks.add(ramBlockNo);
        } else {
            missType = "Capacity / other";
        }
        
        //find first empty cache line and map the RAM block to that cache line
        // in hardware, this would be in parallel, not sequential
        for( int i = 0; i < cache.length; i++){
            if(!cache[i].valid){
                cache[i].blockNumber = ramBlockNo; 
                cache[i].valid = true;
                return new CacheResult(cpuAddress, ramBlockNo, i, offset, false, missType);
            }
        }
        
        //cache full = FIFO to find Victim block
        cache[nReplaceI].blockNumber = ramBlockNo;
        cache[nReplaceI].valid = true;
        
        //save victim index to return as result
        int victimIndex = nReplaceI;
        //circular index selection
        nReplaceI = (nReplaceI + 1) % nBlocks;
        
        return new CacheResult(cpuAddress, ramBlockNo, victimIndex, offset, false, missType);

    }
 
    //-----------------------------------------------------------------------------------------------------------------------------------------
    public CacheResult setMap(int cpuAddress) {
        //check if the cpu address is valid
        if(cpuAddress < 0 || cpuAddress >= ramSize){
            throw new IllegalArgumentException("Invalid CPU address to map");
        }
        
        // eg for understanding form ppt CH06 - Memory, slide 40:
        // Imagine your cache is 16 blocks
        // the size of each block is 8 words
        // and the blocks are divided into 4 sets
        // our cpu address requires the 3rd set
        
        // cpuAddress = 69
        // ramBlockNo = 69 / 8 = 8
        // offset = 69 % 8 = 5 
        // setindex = 8 % 4 = 2
        // startOfReqSet = 2 * 4 = 8
        // endOfReqSet = 8 + 4 -1 = 11 
        
        
        //find ram block number
        int ramBlockNo = cpuAddress / blockSize;
        //find offset number
        int offset = cpuAddress % blockSize;
        
        //set index:
        int setSize = 2; //!!!!!!!!!!! 2-way set associative !!!!!!!!!!!
        int noOfSets = nBlocks / setSize; // number of blocks / size of each set 
        int setIndex = ramBlockNo % noOfSets; 
        int startOfReqIndex = setIndex * setSize;
        int endOfReqIndex = (startOfReqIndex + setSize) - 1;
        
        // search required set for hit
        for( int i = startOfReqIndex; i <= endOfReqIndex; i++){
            if( cache[i].valid && cache[i].blockNumber == ramBlockNo){ // check tag hit, like in assocative
                return new CacheResult(cpuAddress, ramBlockNo, i, offset, true, "none", setIndex);
            }
        }
        
        // these steps are identical to assciative with th echange bing that its withing the set range
        // so between StartOfReqSet --> EndOfReqSet
        // if no hit, determine miss type
        String missType = "";
        if(!seenBlocks.contains(ramBlockNo)){
            missType = "cold";
            seenBlocks.add(ramBlockNo);
        } else {
            missType = "Capacity / other";
        }
        
        //check for empty line in set. if empty then, like in assoative, map the cpuAddress there 
        for(int i = startOfReqIndex; i <= endOfReqIndex; i++){
            if(!cache[i].valid){
                cache[i].valid = true;
                cache[i].blockNumber = ramBlockNo;
                return new CacheResult(cpuAddress, ramBlockNo, i, offset, false, missType, setIndex);
            }
        }
        
        //cache full = FIFO to find Victim block just like in associative
        cache[nReplaceI].blockNumber = ramBlockNo;
        cache[nReplaceI].valid = true;
        
        //save victim index to return as result
        int victimIndex = nReplaceI;
        //circular index selection
        nReplaceI = (nReplaceI + 1) % nBlocks;
        
        return new CacheResult(cpuAddress, ramBlockNo, victimIndex, offset, false, missType, setIndex);
    }
    //-----------------------------------------------------------------------------------------------------------------------------------------

    
    
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
        
        // 
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
