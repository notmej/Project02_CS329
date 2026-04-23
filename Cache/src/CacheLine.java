/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author yuna
 * Represents 1 Block/Lines a Cache, contains:
 *    - the block's number 
 *    - if it contains data or not (valid)
 */
public class CacheLine {
    public int blockNumber;
    public boolean valid;
    
    public CacheLine() {
        this.blockNumber = -1;
        this.valid = false;
    }
    
}
