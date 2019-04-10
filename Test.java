import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.Scanner;

public class Test {
  private static cacheEntry[][] cacheL1;
  private static cacheEntry[][] cacheL2;
  public static void main(String[] args) throws Exception
  {

    //variable declarations
    int cacheSizeL1, cacheSizeL2;
    int indexBitsL1, indexBitsL2;
    int blockSize;
    int blockOffsetBitsL1,blockOffsetBitsL2;
    int taglengthL1, taglengthL2;
    int assocL1, assocL2;
    int setBits;
    int numBlocksL1, numBlocksL2;
    int blocksPerSetL1, blocksPerSetL2;
    int[] bits;

    //variable initializations
    cacheSizeL1 = 16;
    cacheSizeL2 = 16;
    blockSize = 4;
    assocL1 = 1;
    assocL2 = 1;


    numBlocksL1= cacheSizeL1/blockSize; //4
    numBlocksL2= cacheSizeL2/blockSize; //4

    blocksPerSetL1= numBlocksL1/assocL1; //4
    blocksPerSetL2= numBlocksL2/assocL2; //4

    bits = calcBits(blockSize,numBlocksL1,assocL1);
    blockOffsetBitsL1=bits[0];
    indexBitsL1=bits[1];
    taglengthL1=bits[2];
    bits=calcBits(blockSize,numBlocksL2,assocL2);
    blockOffsetBitsL2=bits[0];
    indexBitsL2=bits[1];
    taglengthL2=bits[2];


    //init empty caches
    cacheL1 = new cacheEntry[assocL1][numBlocksL1];
    for(int j=0;j<assocL1;j++)
    {
      for(int i=0; i<numBlocksL1;i++)
      {
        cacheL1[j][i]= new cacheEntry();
        System.out.println("assoc var: " + j + ", block_num: " + i + ", cache entry: " + cacheL1[j][i]);
      }
    }
    cacheL2= new cacheEntry[assocL2][numBlocksL2];
    for(int j=0;j<assocL2;j++)
    {
      for(int i=0; i<numBlocksL2;i++)
      {
        cacheL2[j][i]= new cacheEntry();
        System.out.println("assoc var: " + j + ", block_num: " + i + ", cache entry: " + cacheL1[j][i]);
      }
    }

  }
  //returns an int area that has the tag,index,and blockOffset
  public static int[] decode(String inst, int tagLength,int indexBits,int blockOffsetBits)
  {
    int tag=Integer.parseInt(inst.substring(0,tagLength));
    int index=Integer.parseInt(inst.substring(tagLength,tagLength+indexBits));
    int blockOffset=Integer.parseInt(inst.substring(tagLength+indexBits,tagLength+indexBits+blockOffsetBits));
    return new int[]{tag,index,blockOffset};
  }
  public static int[] calcBits(int blockSize,int numBlocks,int assoc)
  {
    int blockOffsetBits= (int)(Math.log(blockSize)/Math.log(2));
    int indexBits= (int)(Math.log(numBlocks/assoc)/Math.log(2));

    return new int[]{blockOffsetBits,indexBits,32-blockOffsetBits-indexBits};
  }
  //TODO:: write to cache read from cache

  public static void cacheRead() {

  }

}
