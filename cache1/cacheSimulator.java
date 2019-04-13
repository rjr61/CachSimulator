package cache1;

import java.util.*;
import java.lang.Math;
import java.io.*;


public class cacheSimulator {

	private static Cache L1 = null;
	private static Cache L2 = null;
	private static int totalLatency;
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
		String wp,ap, fname;


		// Get user input to initialize L1 and L2 caches
		Scanner kb = new Scanner(System.in);

		System.out.println("Enter L1 cache size in bytes");
		cacheSizeL1 = kb.nextInt();
		System.out.println("Enter L2 cache size in bytes");
		cacheSizeL2 = kb.nextInt();
		System.out.println("Enter access latency of L1 cache");
		latency1 = kb.nextInt();
		System.out.println("Enter access latency of L2 cache");
		latency2 = kb.nextInt();
		System.out.println("Enter block size in bytes");
		blockSize = kb.nextInt();
		System.out.println("Enter L1 set associativity with 1 being D-Map");
		assocL1 = kb.nextInt();
		System.out.println("Enter L2 set associativity  with 1 being D-Map");
		assocL2 = kb.nextInt();
		kb.nextLine();
		System.out.println("Enter write policy");
		wp = kb.nextLine();
		System.out.println("Enter allocation policy");
		ap=kb.nextLine();
    System.out.println("Enter the fileName:");
    fname=kb.nextLine();
    File inFile=new File("src/"+fname);
    //Scanner instStream = new Scanner(inFile);
    BufferedReader instStream = new BufferedReader(new FileReader(inFile));
    String curInst;

		//System.out.println("Enter max number of misses");
    /*
    StringBuilder iv = new StringBuilder();
    iv.append("cacheSizeL1: " + cacheSizeL1);
    iv.append("\ncacheSizeL2: " + cacheSizeL2);
    iv.append("\nlatency1: " + latency1);
    iv.append("\nlatency2: " + latency2);
    iv.append("\nblockSize: " + blockSize);
    iv.append("\nassocL1: " + assocL1);
    iv.append("\nassocL2: " + assocL2);
    iv.append("\nwt: " + wp);
    iv.append("\nap: " + ap);
    System.out.println("Initializations:\n\n" + iv.toString());
    */
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

		L1 = new Cache(assocL1, blocksPerSetL1,"L1",taglengthL1,indexBitsL1,blockOffsetBitsL1,latency1);
		L2 = new Cache(assocL2, blocksPerSetL2, "L2", taglengthL2, indexBitsL2, blockOffsetBitsL2,latency2);

		System.out.println("Cache setup: |valid|tag|data|dirty|LRU|\n");
		System.out.println("L1: ");
		L1.printInfo();

		System.out.println("L2: ");
		L2.printInfo();


		int instL1[];
		int instL2[];
		//reading in instruction
		while ((curInst = instStream.readLine()) != null) {
			String[] instArr=curInst.split(" ");
			System.out.println(Arrays.toString(instArr));
			if(instArr.length > 1) {
				instL1 = decode(instArr[1], taglengthL1, indexBitsL1, blockOffsetBitsL1);
				instL2 = decode(instArr[1], taglengthL2, indexBitsL2, blockOffsetBitsL2);
				System.out.println("instL1: " + Arrays.toString(instL1));
				System.out.println("instL2: " + Arrays.toString(instL2));
				if(instArr[0].equals("R")){
					read(instL1, instL2, wp, ap, L1, L2);
				}
				else
					write(instL1, instL2, wp, ap, L1, L2);

				System.out.println("L1:");
				System.out.println(L1.toString());
				System.out.println("L2:");
				System.out.println(L2.toString());
			}
			//read op or write op
			//
		}

		System.out.println("L1 hit rate is "+ calcRate(L1.getHits(),L1.getMisses()));
		System.out.println("L2 hit rate is "+calcRate( L2.getHits(),L2.getMisses()));
		System.out.println("The total Latency :"+ totalLatency);
	}

	// write helper function
	public static boolean write(int[] instL1, int[] instL2, String wp, String ap, Cache L1, Cache L2)
	{
		if(wp.equals("wt")) {
			if (!write(instL1, wp, ap, L1)) {
				write(instL2, wp, ap, L2);
			} else {
			}
		}
		else if(wp.equals("wb"))
			write(instL1,wp,ap, L1);
		return false;

	}
	public static boolean read(int[] instL1, int[] instL2, String wp, String ap, Cache L1, Cache L2)
	{
		if(L1.contains(instL1[1],instL1[0])){
			//L1 hit update LRU
			L1.incHits();
			totalLatency+=L1.getLatency();
			L1.update(instL1[1],instL1[0]);
			return true;
		}
		else if(L2.contains(instL2[1],instL2[0]))
		{
			L2.incHits();
			L1.incMisses();

			//L2 hit update LRU
			//add latency for missed probe to L1 and L2 access
			totalLatency+=L2.getLatency()+L1.getLatency();
			L2.update(instL2[1],instL2[0]);

			//if write back we can just write to L1 normally let write add l1 latency
			if(wp.equals("wb")) write(instL1,wp,ap,L1);
			else if(wp.equals("wt")){
				//add l1 update latency
				L1.update(instL1[1],instL1[0]);
				totalLatency+=L1.getLatency();
			}
			return true;
		}
		else
			//add latency for probbing 1 & 2 then for mem access and inc misses for both caches
			totalLatency+=L2.getLatency()+L1.getLatency()+L2.getLatency()+100;
		L2.incMisses();
		L1.incMisses();
		//MEMORY ACCESS
		//if write through then write through w/o mem latency

		//if write back can just do update L2 to place into L2 then write L1 to ensure an evicted block will be sent to L2
		if(wp.equals("wb")) {
			//add for L2 update let the write to L1 trigger += in write
			totalLatency+=L2.getLatency();
			L2.update(instL2[1],instL2[0]);
			write(instL1,wp,ap,L1);
		}
		else if(wp.equals("wt"))
		{
			//add latency for each level access
			totalLatency+=L1.getLatency();
			totalLatency+=L2.getLatency();
			L1.update(instL1[1],instL1[0]);
			L2.update(instL2[1],instL2[0]);
		}


		return false;

	}

	// write function
	public static boolean write(int[] instruction, String wp, String ap, Cache cache) {

		int index = instruction[1];
		int tag = instruction[0];

		//Latency += cache.Latency
		if (cache.contains(index, tag)) {
			//System.out.println("break 1");
			totalLatency+=cache.getLatency();
			cache.incHits();
			//cacheHit++;
			if (wp.equals("wt")) {
				cache.update(index, tag);
				return false; // write(instL2, wp, ap, L2);
			} else if (wp.equals("wb")) {
				cache.updateDirty(index, tag);
				return true;
			} else if(wp.equals("we")){
				cache.evictBlock(index,tag);
				String inst=instDecode(instruction,L2.getTagLength(),L2.getIndexBits());
				int[] instL2=L2decode(inst,L2.getTagLength(),L2.getIndexBits(),L2.getBlockOffsetBits());
				//evictblock L2 then evict block to mem so
				L2.evictBlock(instL2[1],instL2[0]);
				L2.incHits();
				totalLatency+=L2.getLatency();
				totalLatency+=L2.getLatency()+100;
			}
			else throw new IllegalArgumentException("Not a valid write policy.");
		} else {
			cache.incMisses();
			totalLatency+=cache.getLatency();
			//System.out.println("break 2");
			//cacheMiss++;
			if (ap.equals("wa")) {
				if (wp.equals("wt")) {
					cache.update(index, tag);
					System.out.println("~wt~");
					return false; // write(instL2, wp, ap, L2);
				}
				else if(wp.equals("wb")){
					System.out.println("~wb~");
					if(cache.getCacheIndex(cache.nextOpen(index))==-1){
						if(cache.getName().equals("L1")){
							String inst=cache.getEvictedInst(index);
							cache.update(index,tag);
							int[] instL2=L2decode(inst,L2.getTagLength(),L2.getIndexBits(),L2.getBlockOffsetBits());
							write(instL2,wp,ap,L2);
						}
						else {
							totalLatency+=L2.getLatency()+100+L2.getLatency();
							cache.update(index, tag);
						}
					}
					cache.update(index,tag);
					return true;

				}
			}
			else{
				if(cache.getName().equals("L1"))
				{
					String inst=instDecode(instruction,L2.getTagLength(),L2.getIndexBits());
					int[] instL2=L2decode(inst,L2.getTagLength(),L2.getIndexBits(),L2.getBlockOffsetBits());
					write(instL2,wp,ap,L2);
				}
				else {

					totalLatency += L2.getLatency() + 100;
					//System.out.println("break 2.2");
					return true;
					//write to mem latency+=memLatency;
				}
			}
		}
		//System.out.println("break 3");
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
	public static int[] L2decode(String inst, int tagLength,int indexBits,int blockOffsetBits)
	{
		int index;
		int tag=Integer.parseInt(inst.substring(0,tagLength),2);
		if(indexBits!=0)index=Integer.parseInt(inst.substring(tagLength,tagLength+indexBits),2);
		else index=0;
		return new int[]{tag,index,0};
	}

	public static int[] calcBits(int blockSize,int numBlocks,int assoc)
	{
		int blockOffsetBits= (int)(Math.log(blockSize)/Math.log(2));
		int indexBits= (int)(Math.log(numBlocks/assoc)/Math.log(2));

		return new int[]{blockOffsetBits,indexBits,32-blockOffsetBits-indexBits};
	}
	public static String instDecode(int[] instL1, int tagLength,int indexBits){
		String tag= intToBinary(instL1[1]);
		String index=intToBinary(instL1[0]);

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
		String inst=""+tag+index;
		return inst;
	}
	public static String intToBinary(int n)
	{
		String x="";
		while(n > 0)
		{
			int a = n % 2;
			x = a + x;
			n = n / 2;

		}
		return x;
	}
	public static double calcRate(int hits, int misses)
	{
		double hitNum=(double)hits;
		double missNum=(double)misses;

		return 100*(hitNum/(hitNum+missNum));


	}


}
