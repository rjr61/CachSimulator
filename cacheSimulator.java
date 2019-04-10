import java.util.*;
import java.lang.Math;
import java.io.*;
public class cacheSimulator {

	private static Cache L1;
	private static Cache L2;

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
		//System.out.println("Enter max number of misses");

		System.out.println("Enter the fileName:");
		File inFile=new File("src/instr.txt");
		//Scanner instStream = new Scanner(inFile);
		BufferedReader instStream = new BufferedReader(new FileReader(inFile));
		String curInst="";


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

		L1 = new Cache(assocL1, blocksPerSetL1);
		L2 = new Cache(assocL2, blocksPerSetL2);
		L1.printInfo();
		System.out.println(indexBitsL1);



		int instL1[];
		int instL2[];
		//reading in instruction
		while ((curInst= instStream.readLine()) != null) {
			String[] instArr=curInst.split(" ");
			System.out.println(Arrays.toString(instArr));
			instL1=decode(instArr[1],taglengthL1,indexBitsL1,blockOffsetBitsL1);
			instL2=decode(instArr[1],taglengthL2,indexBitsL2,blockOffsetBitsL2);
			System.out.println(Arrays.toString(instL1));
			//read op or write op
			//if(instArr[0].equals("read"))
			//else
			//write(instL1,instL2,String wp, ap ,L1);
		}


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
/*
	//write to cache
	//wp is write policy ap is allocation policy
	public static boolean write(int [] instL1, int[] instL2, String wp, String ap, Cache cache)
	{
		/*if(cache==null){
			//this is a mem access latency+=memmLatency return true;

		}

		//Latency += cache.Latency
		if(cache.contains(index,tag))
		{
			//cacheHit++;
			if(wp.equals("wt"))
			{
				cache.update(index,tag);
				write(instL1,instL2,wp, ap, cache.next());
			}
			else if(wp.equals("wb"))
			{
				cache.update(index, tag);
			}
			return true;
		}
		else
		{
			//cacheMiss++;
			if(ap.equals("wa"))
			{
				cache.update(index,tag);
				if(wp.equals("wt"))
				{
					write(instL1,instL2,wp, ap, cache.next());
				}
			}
			else
				//write to mem latency+=memLatency

			return false;
		}
	}*/
	//TODO:: read from cache


}
 