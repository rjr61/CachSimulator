package cache2;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Scanner;
import java.util.ArrayList;
import static java.lang.System.exit;

public class CacheDriver {

  private static Cache L1;
  private static Cache L2;

  public static void main(String[] args) {
    Queue<String> instructions = new LinkedList<>();
    Queue<String> instruction2 = new LinkedList<>();
    BufferedReader reader;
    Scanner sc = new Scanner(System.in);

    int sizeL1, sizeL2, sizeBlock, setAssoc, latencyL1, latencyL2, memLatency, maxMisses, blocksL1, blocksL2, setsL1, setsL2, offsetBits, indexLengthL1, indexLengthL2, tagLengthL1, tagLengthL2;
    String writePolicy, allocatePolicy, fname, nextInstruction, mode;

    // hard-coded values
    sizeL1 = 1024;
    sizeL2 = 2056;
    sizeBlock = 64;
    setAssoc = 4;
    latencyL1 = 1;
    latencyL2 = 5;
    maxMisses = 6;
    writePolicy = "wb";
    allocatePolicy = "wa";
    mode="aum";
/*    System.out.println("Enter the size of L1: ");
    sizeL1 = sc.nextInt();
    System.out.println("Enter the size of L2: ");
    sizeL2 = sc.nextInt();
    System.out.println("Enter the block size: ");
    sizeBlock = sc.nextInt();
    System.out.println("Enter the set associativity: ");
    setAssoc = sc.nextInt();
    System.out.println("Enter the write policy (wb/wt): ");
    writePolicy = sc.nextLine();
    System.out.println("Enter the allocation policy policy (wa/nwa): ");
    allocatePolicy = sc.nextLine();
    System.out.println("Enter the maximum number of outstanding misses: ");
    maxMisses = sc.nextInt();
    System.out.println("Enter the hit latency for L1: ");
    latencyL1 = sc.nextInt();
    System.out.println("Enter the hit latency for L2: ");
    latencyL2 = sc.nextInt();
    System.out.println("Enter the max number of misses");
    maxMisses = sc.nextInt();*/

    System.out.println("Enter the file name: ");
    fname = sc.nextLine();

    // cache calculations
    blocksL1 = sizeL1 / sizeBlock;
    blocksL2 = sizeL2 / sizeBlock;

    setsL1 = blocksL1 / setAssoc;
    setsL2 = blocksL2 / setAssoc;

    offsetBits = (int)(Math.log(sizeBlock)/Math.log(2));

    indexLengthL1 = (int)(Math.log(setsL1)/Math.log(2));
    indexLengthL2 = (int)(Math.log(setsL2)/Math.log(2));

    L1 = new Cache(setAssoc, setsL1, "L1",latencyL1);
    L2 = new Cache(setAssoc, setsL2, "L2",latencyL2);

    memLatency = latencyL2 + 100;

/*    System.out.println(blocksL1);
    System.out.println(blocksL2);
    System.out.println(setsL1);
    System.out.println(setsL2);
    System.out.println(offsetBits);
    System.out.println(indexLengthL1);
    System.out.println(indexLengthL2);*/

    try {
      reader = new BufferedReader(new FileReader("src/"+fname));
      String line = reader.readLine();
      while(line != null) {
        instructions.add(line);
        instruction2.add(line);
        line = reader.readLine();
      }
      reader.close();
    } catch(FileNotFoundException e) {
      e.printStackTrace();
    } catch(IOException e) {
      e.printStackTrace();
    }

    System.out.println("Instructions: " + instructions);
    //access mode enabled?


    int instLength = instructions.peek().split(" ")[1].length();
    //System.out.println(instLength);

    String instType, instruction, tagL1, tagL2, indexL1, indexL2;
    int tagL1_int, tagL2_int, indexL1_int, indexL2_int,cycleTime=0;

    tagLengthL1 = instLength - indexLengthL1 - offsetBits;
    tagLengthL2 = instLength - indexLengthL2 - offsetBits;


    int count = 0, stop = 10, serveTime = 0, returnTime;
    while(instructions.peek() != null && count++ < stop) {
      System.out.println("Serve time: " + ++serveTime);

      nextInstruction = instructions.remove();

      instType = nextInstruction.split(" ")[0];
      instruction = nextInstruction.split(" ")[1];

      tagL1 = instruction.substring(0, tagLengthL1);
      tagL2 = instruction.substring(0, tagLengthL2);

      indexL1 = instruction.substring(tagLengthL1, tagLengthL1 + indexLengthL1);
      indexL2 = instruction.substring(tagLengthL2, tagLengthL2 + indexLengthL2);

      tagL1_int = Integer.parseInt(tagL1,2);
      tagL2_int = Integer.parseInt(tagL2,2);
      indexL1_int = Integer.parseInt(indexL1,2);
      indexL2_int = Integer.parseInt(indexL2,2);


      returnTime = serveTime;

      if(instType.equals("R")) {
        // readL1, increment cycles by latencyL1
        returnTime += latencyL1;
        if(!readL1(tagL1_int, indexL1_int)) { //cache miss
          System.out.println("!!L1 miss!!");
          // readL2, increment cycles by latencyL2
          returnTime += latencyL2;
          if(!readL2(tagL2_int, indexL2_int)) {
            System.out.println("!L2 miss!");
            // increment cycles by memLatency
            returnTime += memLatency;
            // copy from mem to L2 and from L2 to L1
            L2.memToCache(tagL2_int, indexL2_int);
            cacheToCache(L2, L1, tagL2_int, indexL2_int, tagL1_int, indexL1_int);
          } else {
            System.out.println("*L2 hit*");
            // copy L2 to L1
            cacheToCache(L2, L1, tagL2_int, indexL2_int, tagL1_int, indexL1_int);
          }
        } else {
          System.out.println("**L1 hit**");
        }
      } else if(instType.equals("W")) {
        if(writePolicy.equals("wt")) {
          if(L1.contains(tagL1_int, indexL1_int)) {
            // latency is a combination of all 3 accesses
            returnTime += latencyL1 + latencyL2 + memLatency;
            // hit, write to L1, L2, and memory
            // copy from write to L1 and from L1 to L2
            L1.editCache(tagL1_int, indexL1_int);
            cacheToCache(L1, L2, tagL1_int, indexL1_int, tagL2_int, indexL2_int);
          } else if(L2.contains(tagL2_int, indexL2_int)) {
            // miss L1, hit L2; check write allocate policy, write to L2 and memory
            // latency is a combination of all 3 accesses ? (assuming check L1, edit L2, edit mem)
            returnTime += latencyL1 + latencyL2 + memLatency;
            L2.editCache(tagL2_int, indexL2_int);

            if(allocatePolicy.equals("wa")) {
              // copy from L2 to L1
              // latency is another L2 + L1
              cacheToCache(L2, L1, tagL2_int, indexL2_int, tagL1_int, indexL1_int);
            }
            // LEFT OFF HERE
          }
        }
      }

      System.out.println("Return time: " + returnTime);

      serveTime = returnTime;

      System.out.println(String.format("Iteration: %d\nCache:\n%s\n%s", count, L1.toString(), L2.toString()));

    }

    System.out.println("STOPPING AT INSTRUCTION " + stop);
    System.out.println("The access under misses latency is " + cycleTime);
  }

  // cache1 -> cache2
  private static void cacheToCache(Cache cache1, Cache cache2, int tag1, int index1, int tag2, int index2) {
    int data = cache1.getCacheData(tag1, index1);
    cache2.writeToCache(tag2, index2, data);
  }

  public static boolean readL1(int tag, int index) {
    if(L1.contains(index, tag)) {
      // hit
      return true;
    }

    // miss
    return false;
  }

  public static boolean readL2(int tag, int index) {
    if(L2.contains(index, tag)) {
      // hit
      return true;
    }

    // miss
    return false;
  }
  public static int latencyCalc(Queue<String> instructions, int tagLengthL1,int indexLengthL1, int tagLengthL2,int indexLengthL2, int max) {
    String nextInstruction,tagL1,tagL2,indexL1,indexL2,instType,instruction;
    int tagL1_int,tagL2_int,indexL1_int,indexL2_int,curTime=0,retTime=0;
    Queue<Integer> buffer = new LinkedList<>();
    while (instructions.peek() != null) {

      nextInstruction = instructions.remove();

      instType = nextInstruction.split(" ")[0];
      instruction = nextInstruction.split(" ")[1];

      tagL1 = instruction.substring(0, tagLengthL1);
      tagL2 = instruction.substring(0, tagLengthL2);

      indexL1 = instruction.substring(tagLengthL1, tagLengthL1 + indexLengthL1);
      indexL2 = instruction.substring(tagLengthL2, tagLengthL2 + indexLengthL2);

      tagL1_int = Integer.parseInt(tagL1, 2);
      tagL2_int = Integer.parseInt(tagL2, 2);
      indexL1_int = Integer.parseInt(indexL1, 2);
      indexL2_int = Integer.parseInt(indexL2, 2);

      //add one cycle to fetch inst and initialize return time

      retTime=curTime;
      while(buffer.peek()!=null && buffer.peek()<=curTime)buffer.remove();
      //if L1 just add L1 latency to curTime and process next instruction
      if(L1.contains(indexL1_int,tagL1_int)) curTime+= L1.getLatency();
      else if(L2.contains(indexL2_int,tagL2_int))curTime+=L2.getLatency()+L1.getLatency();
      else {
        retTime=L2.getLatency()+L1.getLatency()+100;
        if(buffer.size()==max)
        {
          curTime=buffer.remove();
        }
          buffer.add(retTime+curTime);
      }
      if(instructions.peek() != null) curTime+=10;
    }

    while(buffer.peek()!=null) curTime=buffer.remove();
    return curTime;
  }

}
