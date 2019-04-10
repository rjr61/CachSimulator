import java.util.*;
import java.lang.Math;
import java.io.*;
public class Test2 {

  private static Cache L1 = null;
  private static Cache L2 = null;

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
    int latency1, latency2;
    int[] bits;
    String wp,ap;

    //System.out.println("Enter the fileName:");
    File inFile=new File("src/instr.txt");
    //Scanner instStream = new Scanner(inFile);
    BufferedReader instStream = new BufferedReader(new FileReader(inFile));
    String curInst="";

    cacheSizeL1 = 32;
    cacheSizeL2 = 32;
    latency1 = 8;
    latency2 = 8;
    blockSize = 4;
    assocL1 = 1;
    assocL2 = 2;
    //wp = ;
    //ap = ;

    StringBuilder iv = new StringBuilder();
    iv.append("cacheSizeL1: " + cacheSizeL1);
    iv.append("\ncacheSizeL2: " + cacheSizeL2);
    iv.append("\nlatency1: " + latency1);
    iv.append("\nlatency2: " + latency2);
    iv.append("\nblockSize: " + blockSize);
    iv.append("\nassocL1: " + assocL1);
    iv.append("\nassocL2: " + assocL2);
    //iv.append(wp);
    //iv.append(ap);
    System.out.println("Initializations:\n\n" + iv.toString());

    // calculations
    numBlocksL1= cacheSizeL1/blockSize;
    numBlocksL2= cacheSizeL2/blockSize;

    blocksPerSetL1= numBlocksL1/assocL1;
    blocksPerSetL2= numBlocksL2/assocL2;

    bits = calcBits(blockSize,numBlocksL1,assocL1);
    blockOffsetBitsL1=bits[0];
    indexBitsL1=bits[1];
    taglengthL1=bits[2];
    bits=calcBits(blockSize,numBlocksL2,assocL2);
    blockOffsetBitsL2=bits[0];
    indexBitsL2=bits[1];
    taglengthL2=bits[2];

    StringBuilder c = new StringBuilder();
    c.append("numBlocksL1: " + numBlocksL1);
    c.append("\nnumBlocksL2: " + numBlocksL2);
    c.append("\nblocksPerSetL1: " + blocksPerSetL1);
    c.append("\nblocksPerSetL2: " + blocksPerSetL2);
    c.append("\nblockOffsetBitsL1: " + blockOffsetBitsL1);
    c.append("\nblockOffsetBitsL2: " + blockOffsetBitsL2);
    c.append("\nindexBitsL1: " + indexBitsL1);
    c.append("\nindexBitsL2: " + indexBitsL2);
    c.append("\ntaglengthL1: " + taglengthL1);
    c.append("\ntagLengthL2: " + taglengthL2);
    System.out.println("\nCalculations:\n\n" + c.toString() + "\n");

    L1 = new Cache(assocL1, blocksPerSetL1);
    L2 = new Cache(assocL2, blocksPerSetL2);
    System.out.println("L1: ");
    L1.printInfo();

    System.out.println("L2: ");
    L2.printInfo();


    int instL1[];
    int instL2[];
    //reading in instruction
    System.out.println("Instructions:");
    while ((curInst= instStream.readLine()) != null) {
      String[] instArr=curInst.split(" ");
      System.out.println(Arrays.toString(instArr));
      if(instArr.length > 1) {
        instL1 = decode(instArr[1], taglengthL1, indexBitsL1, blockOffsetBitsL1);
        instL2 = decode(instArr[1], taglengthL2, indexBitsL2, blockOffsetBitsL2);
        System.out.println("instL1: " + Arrays.toString(instL1));
        System.out.println("instL2: " + Arrays.toString(instL2));

        write(instL1, instL2, "wt", "wa", L1, L2);

        System.out.println("L1:");
        System.out.println(L1.toString());
        System.out.println("L2:");
        System.out.println(L2.toString());
      }
      //read op or write op
      //if(instArr[0].equals("read"))
      //else
      //write(instL1,instL2,String wp, ap ,L1);
    }
  }

  // write helper function
  public static boolean write(int[] instL1, int[] instL2, String wp, String ap, Cache L1, Cache L2)
  {
    if(!write(instL1, wp, ap, L1)) {
      write(instL2, wp, ap, L2);
    }
    else {
      System.out.println("Cache is null, cache contains = true + wb, ap != wa, , function reached end???");
    }
    return false;
  }

  // write function
  public static boolean write(int[] instruction, String wp, String ap, Cache cache) {
    if (cache.isNull()) {
      //this is a mem access latency+=memLatency return true;
      System.out.println("TODO: Cache is null");
      return true;
    }

    int index = instruction[1];
    int tag = instruction[0];

    //Latency += cache.Latency
    if (cache.contains(index, tag)) {
      System.out.println("break 1");
      //cacheHit++;
      if (wp.equals("wt")) {
        System.out.println("break 1.1");
        cache.update(index, tag);
        return false; // write(instL2, wp, ap, L2);
      } else if (wp.equals("wb")) {
        System.out.println("break 1.2");
        cache.update(index, tag);
        return true;
      } else {
        System.out.println("break 1.3");
        throw new IllegalArgumentException("Not a valid write policy.");
      }
    } else {
      System.out.println("break 2");
      //cacheMiss++;
      if (ap.equals("wa")) {
        System.out.println("break 2.1");
        cache.update(index, tag);
        if (wp.equals("wt")) {
          System.out.println("break 2.1.1");
          return false; // write(instL2, wp, ap, L2);
        }
      } else {
        System.out.println("break 2.2");
        return true;
        //write to mem latency+=memLatency;
      }
    }
    System.out.println("break 3");
    return true;
  }

  //returns an int array that has the tag,index,and blockOffset
  public static int[] decode(String inst, int tagLength,int indexBits,int blockOffsetBits)
  {
    int index;
    int tag=Integer.parseInt(inst.substring(0,tagLength),2);
    if(indexBits!=0)index=Integer.parseInt(inst.substring(tagLength,tagLength+indexBits),2);
    else index=0;
    int blockOffset=Integer.parseInt(inst.substring(tagLength+indexBits,tagLength+indexBits+blockOffsetBits),2);
    return new int[]{tag,index,blockOffset};
  }
  public static int[] calcBits(int blockSize,int numBlocks,int assoc)
  {
    int blockOffsetBits= (int)(Math.log(blockSize)/Math.log(2));
    int indexBits= (int)(Math.log(numBlocks/assoc)/Math.log(2));

    return new int[]{blockOffsetBits,indexBits,32-blockOffsetBits-indexBits};
  }

}
