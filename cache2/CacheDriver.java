package cache2;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Scanner;

public class CacheDriver {

  private static Cache L1;
  private static Cache L2;

  public static void main(String[] args) {
    Queue<String> instructions = new LinkedList<>();
    Queue<String> instruction2 = new LinkedList<>();
    BufferedReader reader;
    Scanner sc = new Scanner(System.in);

    int sizeL1, sizeL2, sizeBlock, setAssoc, latencyL1, latencyL2, memLatency, maxMisses, blocksL1, blocksL2, setsL1, setsL2, offsetBits, indexLengthL1, indexLengthL2, tagLengthL1, tagLengthL2,curTime=0;
    String writePolicy, allocatePolicy, fname, nextInstruction, mode;

    // hard-coded values
/*    sizeL1 = 512;
    sizeL2 = 2048;
    sizeBlock = 64;
    setAssoc = 4;
    latencyL1 = 1;
    latencyL2 = 4;
    maxMisses = 9;
    writePolicy = "wt";
    allocatePolicy = "nwa";*/
    mode="aum";

    System.out.println("Enter the size of L1: ");
    sizeL1 = sc.nextInt();
    System.out.println("Enter the size of L2: ");
    sizeL2 = sc.nextInt();
    System.out.println("Enter the block size: ");
    sizeBlock = sc.nextInt();
    System.out.println("Enter the set associativity: ");
    setAssoc = sc.nextInt();
    System.out.println("Enter the write policy (wb/wt): ");
    writePolicy = sc.nextLine();writePolicy = sc.nextLine();
    System.out.println("Enter the allocation policy policy (wa/nwa): ");
    allocatePolicy = sc.nextLine();
    System.out.println("Enter the maximum number of outstanding misses: ");
    maxMisses = sc.nextInt();
    System.out.println("Enter the hit latency for L1: ");
    latencyL1 = sc.nextInt();
    System.out.println("Enter the hit latency for L2: ");
    latencyL2 = sc.nextInt();

    System.out.println("Enter the file name: ");
    fname = sc.nextLine();
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

    System.out.println(blocksL1);
    System.out.println(blocksL2);
    System.out.println(setsL1);
    System.out.println(setsL2);
    System.out.println(offsetBits);
    System.out.println(indexLengthL1);
    System.out.println(indexLengthL2);

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

    Queue<Integer> buffer = new LinkedList<>();
    int count = 0, stop = instructions.size(), serveTime = 0, returnTime;
    while(instructions.peek() != null && count++ < stop) {


      while(buffer.peek()!=null && buffer.peek()<=curTime)buffer.remove();

      System.out.println("Serve time: " + ++serveTime);
      nextInstruction = instructions.remove();

      instType = nextInstruction.split(" ")[0];
      instruction = nextInstruction.split(" ")[1];
      System.out.println(instType + " "+ instruction);
      tagL1 = instruction.substring(0, tagLengthL1);
      tagL2 = instruction.substring(0, tagLengthL2);

      indexL1 = instruction.substring(tagLengthL1, tagLengthL1 + indexLengthL1);
      indexL2 = instruction.substring(tagLengthL2, tagLengthL2 + indexLengthL2);

      tagL1_int = Integer.parseInt(tagL1,2);
      tagL2_int = Integer.parseInt(tagL2,2);
      indexL1_int = Integer.parseInt(indexL1,2);
      indexL2_int = Integer.parseInt(indexL2,2);

      System.out.println("indexL1: " + indexL1_int);
      System.out.println("tagL1: " + tagL1_int);
      System.out.println("indexL2: " + indexL2_int);
      System.out.println("tagL2: " + tagL2_int);


      returnTime = serveTime;

      if(instType.equals("R")) {
        // readL1, increment cycles by latencyL1
        returnTime += latencyL1;
        curTime+= latencyL1;

        if(writePolicy.equals("wt")||writePolicy.equals("we")) {
          if (!readL1(tagL1_int, indexL1_int)) { //cache miss
            System.out.println("!!L1 miss!!");
            // readL2, increment cycles by latencyL2
            returnTime += latencyL2;
            curTime += latencyL2;
            if (!readL2(tagL2_int, indexL2_int)) {
              System.out.println("!L2 miss!");
              // increment cycles by memLatency
              returnTime += memLatency;
              if (buffer.size() == maxMisses) curTime = buffer.remove();
              buffer.add(curTime + memLatency);
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
            L1.updateLRU(tagL1_int, indexL1_int);
          }
        }
        else
          {
            int[] instL2= new int[2];
            if (!readL1(tagL1_int, indexL1_int)) { //cache miss
              System.out.println("!!L1 miss!!");
              // readL2, increment cycles by latencyL2
              returnTime += latencyL2;
              curTime += latencyL2;

              if (!readL2(tagL2_int, indexL2_int)) {
                System.out.println("!L2 miss!");
                // increment cycles by memLatenc
                returnTime += memLatency;
                if (buffer.size() == maxMisses) curTime = buffer.remove();
                buffer.add(curTime + memLatency);

                //check to see if L1 LRU will get evicted
                // if so update the L2 data
                //if not then just cascade write from mem from L1 down
                if (L1.LRU_isDirty(indexL1_int)&& L1.isFull(indexL1_int)){
                  //if its dirty update L2 data
                  instL2 = instDecode(L1.getEvictedInst(indexL1_int), tagLengthL1, indexLengthL1, tagLengthL2, indexLengthL2);
                  ///this is the line
                  if (readL2(instL2[0], instL2[1])) {
                    cacheToCacheUpdate2(L1, L2, L1.getLRU(indexL1_int)[0], indexL1_int, instL2[0], instL2[1]);
                  }
                    else
                    cacheToCache(L1, L2, L1.getLRU(indexL1_int)[0], indexL1_int, instL2[0], instL2[1]);

                }
                L1.memToCache(tagL1_int, indexL1_int);
                cacheToCache(L1, L2, tagL1_int, indexL1_int, tagL2_int, indexL2_int);
              } else {
                System.out.println("*L2 hit*");
                // copy L2 to L1 and check if L1 LRU will get evicted
                if (L1.LRU_isDirty(indexL1_int)&& L1.isFull(indexL1_int)) {
                  //if its dirty update L2 data then write memBlock into L1
                  instL2 = instDecode(L1.getEvictedInst(indexL1_int), tagLengthL1, indexLengthL1, tagLengthL2, indexLengthL2);
                  cacheToCache(L1, L2, L1.getLRU(indexL1_int)[0], indexL1_int, instL2[0], instL2[1]);
                }
                cacheToCache(L2, L1, tagL2_int, indexL2_int, tagL1_int, indexL1_int);
              }
            } else {
              System.out.println("**L1 hit**");
              L1.updateLRU(tagL1_int, indexL1_int);
            }
          }
      } else if(instType.equals("W")) {
        if(writePolicy.equals("wt")) {
          if(L1.contains(tagL1_int, indexL1_int)) {
            System.out.println("**L1 hit**");
            // latency is a combination of all 3 accesses
            returnTime += latencyL1 + latencyL2 + memLatency;
            curTime+= latencyL1+latencyL2;
            if(buffer.size()==maxMisses)curTime=buffer.remove();
            buffer.add(curTime+memLatency);
            // hit, write to L1, L2, and memory
            // write to L1 and from L1 to L2
            L1.editCache(tagL1_int, indexL1_int, 0);
            cacheToCacheUpdate(L1, L2, tagL1_int, indexL1_int, tagL2_int, indexL2_int);
          } else if(L2.contains(tagL2_int, indexL2_int)) {
            System.out.println("*L2 hit*");
            // miss L1, hit L2; check write allocate policy, write to L2 and memory
            // latency is a combination of all 3 accesses ? (assuming check L1, edit L2, edit mem)
            returnTime += latencyL1 + latencyL2 + memLatency;
            curTime+= latencyL1+latencyL2;
            if(buffer.size()==maxMisses)curTime=buffer.remove();
            buffer.add(curTime+memLatency);
            L2.editCache(tagL2_int, indexL2_int, 0);
          } else {
            System.out.println("!!Write miss!!");
            // total cycles = check L1 + check L2 + write to mem
            returnTime += latencyL1 + latencyL2 + memLatency;
            curTime+= latencyL1+latencyL2;
            if(buffer.size()==maxMisses)curTime=buffer.remove();
            buffer.add(curTime+memLatency);
            // copy from mem to L2 and from L2 to L1
            L2.memToCache(tagL2_int, indexL2_int);
            cacheToCache(L2, L1, tagL2_int, indexL2_int, tagL1_int, indexL1_int);
          }
        } else if(writePolicy.equals("wb")) {
          if(L1.contains(indexL1_int, tagL1_int)) {
            System.out.println("**L1 hit**");
            // update data and dirty bit
            returnTime+=latencyL1;
            curTime+= latencyL1;
             L1.editCache(tagL1_int, indexL1_int, 1);
          } else if(L2.contains(tagL2_int, indexL2_int)) {
            System.out.println("*L2 hit*");
            // L2 hit, update data and dirty = 1
            L2.editCache(tagL2_int, indexL2_int, 1);
            returnTime+=latencyL2;
            curTime+= latencyL2;
            if(allocatePolicy.equals("wa")) {
              // write back to L1; CHECK IF DIRTY
              // find LRU and check if dirty
              //only need to do this is cache is full

                  if (L1.LRU_isDirty(indexL1_int)&& L1.isFull(indexL1_int)) {
                    //have to get evicted inst dumbass
                    int[] instL2 = instDecode(L1.getEvictedInst(indexL1_int), tagLengthL1, indexLengthL1, tagLengthL2, indexLengthL2);
                    cacheToCache(L1, L2, L1.getLRU(indexL1_int)[0], indexL1_int, instL2[0], instL2[1]);
                   /*this was unreachable will never have cascading dirties
                    if (L2.LRU_isDirty(instL2[1])&&L2.isFull(instL2[1])) {

                      // write L2_LRU to memory
                      L2.writeToMem(instL2[1]);

                      cacheToCache(L2, L1, tagL2_int, indexL2_int, tagL1_int, indexL1_int);*/
                  }
                      //else just write to L1
                      cacheToCache(L2, L1, tagL2_int, indexL2_int, tagL1_int, indexL1_int);


            }
          } else {
            System.out.println("!!!Write miss!!!");
            returnTime+=latencyL2;
            if(buffer.size()==maxMisses)curTime=buffer.remove();
            buffer.add(curTime+memLatency);
            // MISS =WRITE TO MEM for wb and we
            // memory -> L2, L2 -> L1

            if(allocatePolicy.equals("wa")) {
              // write back to L2 and L1; CHECK DIRTY

              if(L2.LRU_isDirty(indexL2_int)&&L2.isFull(indexL2_int)) {
                // write L2_LRU to memory

                L2.writeToMem(indexL2_int);
                // write memory to L2
                // evict L2_LRU
                L2.memToCache(tagL2_int, indexL2_int);
              } else {
                // evict L2_LRU
                // write memory to L2
                L2.memToCache(tagL2_int, indexL2_int);
              }

              if(L1.LRU_isDirty(indexL1_int)&&L1.isFull(indexL1_int)) {
                //write evicted to mem
                //then from mem to cache
                L2.writeToMem(indexL2_int);
                L2.memToCache(tagL2_int, indexL2_int);

                if(L1.LRU_isDirty(indexL1_int)&&L1.isFull(indexL1_int)) {
                  // evict from L1 and write L1_LRU to L2
                  int[] instL2=instDecode(L1.getEvictedInst(indexL1_int), tagLengthL1, indexLengthL1, tagLengthL2, indexLengthL2);
                  cacheToCache(L1, L2, L1.getLRU(indexL1_int)[0], indexL1_int, instL2[0], instL2[1]);
                } else {
                  cacheToCache(L2, L1, tagL2_int, indexL2_int, tagL1_int, indexL1_int);
                }
              } else {
                // evict L2_LRU
                //no mem
                cacheToCache(L2, L1, tagL2_int, indexL2_int, tagL1_int, indexL1_int);
                L2.memToCache(tagL2_int, indexL2_int);
              }

            }
          }

        }
        else
        {
            returnTime+=latencyL1+latencyL2;
            curTime+=latencyL1+ latencyL2;
            if(L1.contains(tagL1_int, indexL1_int)) {
              System.out.println("!!L1 hit!!");
              //access both L1 L2 to boot the entry
              L1.evict(L1.where(tagL1_int,indexL1_int));
              L2.evict(L2.where(tagL2_int,indexL2_int));
            }
            else if(L2.contains(tagL2_int, indexL2_int))
            {
              System.out.println("!L2 hit!");
              //access L1 to boot the entry
              L2.evict(L2.where(tagL2_int,indexL2_int));
            }
            else
            {
              System.out.println("Write miss");
                if(buffer.size()==maxMisses)curTime=buffer.remove();
                buffer.add(curTime+memLatency);
            }
            //have to write to mem no matter what
            //mem Latency++;
        }

    }

      System.out.println("Return time: " + returnTime);

      serveTime = returnTime;

      System.out.println(String.format("Iteration: %d\nCache:\n%s\n%s", count, L1.toString(), L2.toString()));
      if(instructions.peek() != null) curTime+=10;
    }
    while(buffer.peek()!=null) curTime=buffer.remove();
    System.out.println("STOPPING AT INSTRUCTION " + stop);
    System.out.println("The access under misses latency is " + curTime);
  }

  // cache1 -> cache2
  private static void cacheToCache(Cache cache1, Cache cache2, int tag1, int index1, int tag2, int index2) {
    int data = cache1.getCacheData(tag1, index1);
    //System.out.println("got here and data is "+ data);
    cache2.writeToCache(tag2, index2, data);
  }
  private static void cacheToCacheUpdate(Cache cache1, Cache cache2, int tag1, int index1, int tag2, int index2) {
    int data = cache1.getCacheData(tag1, index1);
    cache2.updateCache(tag2, index2, data);
  }
  private static void cacheToCacheUpdate2(Cache cache1, Cache cache2, int tag1, int index1, int tag2, int index2) {
    int data = cache1.getCacheData2(tag1, index1);
    cache2.updateCache2(tag2, index2, data);
  }

  public static int increaseTag(int tag, int len) {
    String stringTag= Integer.toBinaryString(tag);
    if(stringTag.length()!=len)
    {
      for(int i=0;i<len-stringTag.length();i++)stringTag= "0"+stringTag;
    }

    return Integer.parseInt(stringTag);

  }

  public static int increaseIndex(int index, int len) {
    String stringIndex=Integer.toBinaryString(index);
    return -1;
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
  public static int[] instDecode(int[] instL1,int tagLength,int indexBits,int newTagLength,int newIndexLength){
    //convert tag back to binary
    String tag= Integer.toBinaryString(instL1[0]);
    String index=Integer.toBinaryString(instL1[1]);

    int tag_len = tag.length();
    int index_len = index.length();

    if(tag_len!=tagLength)
    {
      for(int i=0;i<tagLength-tag_len;i++)tag= "0"+tag;
    }
    if(index_len!=indexBits)
    {
      for(int i=0;i<indexBits-index_len;i++)index= "0"+index;
    }
    //concatenate the tag and index binaries back together
    String inst=""+tag+index;
    int newIndex;
    //divide the instruction based on L2 parameters and return new tag and index
    int newTag=Integer.parseInt(inst.substring(0,newTagLength),2);
    if(newIndexLength!=0)newIndex=Integer.parseInt(inst.substring(newTagLength,newTagLength+newIndexLength),2);
    else newIndex=0;
    return new int[]{newTag,newIndex};
  }


}
