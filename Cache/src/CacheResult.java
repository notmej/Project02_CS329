/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author yuna
 * Represents the result of mapping 
 */
public class CacheResult {
    
    private int cpuAddress;
    private int ramBlockNo;
    private int cacheIndex;
    private int offset;
    private boolean hit;
    private String missType; 

    public CacheResult(int cpuAddress, int ramBlockNo, int cacheIndex, int offset, boolean hit, String missType) {
        this.cpuAddress = cpuAddress;
        this.ramBlockNo = ramBlockNo;
        this.cacheIndex = cacheIndex; // called line number in Cache.java. Is the position in the cache array
        this.offset = offset;
        this.hit = hit;
        this.missType = missType;
    }

    public int getCpuAddress() {
        return cpuAddress;
    }

    public int getRamBlockNo() {
        return ramBlockNo;
    }

    public int getCacheIndex() {
        return cacheIndex;
    }

    public int getOffset() {
        return offset;
    }

    public boolean isHit() {
        return hit;
    }

    @Override
    public String toString() {
        return "CacheResult{" + "cpuAddress=" + cpuAddress + ", ramBlockNo=" + ramBlockNo + ", cacheIndex=" + cacheIndex + ", offset=" + offset + ", hit=" + hit + ", missType=" + missType + '}';
    }

    
    
    
}
