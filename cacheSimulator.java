import java.util.*;
import java.lang.Math;
import java.io.*;
public class cacheSimulator {

	private static cacheEntry[][] cacheL1;
	private static cacheEntry[][] cacheL2;
	public static void main(String[] args) throws Exception
	{
		
		Scanner kb= new Scanner(System.in);
		int cacheSizeL1, cacheSizeL2=0;
		int indexBitsL1, indexBitsL2=0;
		int latency1=0;
		int latency2=0;
		int memLatency=0;
		int blockSize=0;
		int blockOffsetBitsL1,blockOffsetBitsL2=0;
		int taglengthL1, taglengthL2=0;
		int assocL1, assocL2=0;
		int setBits=0;
		
		System.out.println("Enter cache size in bytes");
		cacheSizeL1=kb.nextInt();
		System.out.println("Enter access latency of Level 1 cache");
		latency1=kb.nextInt();
		System.out.println("Enter access latency of Level 2 cache");
		latency2=kb.nextInt();
		System.out.println("Enter block size in bytes");
		blockSize=kb.nextInt();
		System.out.println("Enter L1 set associativity with 0 being D-Map");
		assocL1=kb.nextInt();
		System.out.println("Enter L2 set associativity  with 0 being D-Map");
		assocL2=kb.nextInt();
		/*System.out.println("Enter write policy");
		System.out.println("Enter allocation policy");
		System.out.println("Enter max number of misses");*/
		System.out.println("Enter the fileName:");
		File inFile=new File(kb.nextLine());
		BufferedReader instStream = new BufferedReader(new FileReader(inFile));
		String curInst="";

			
		//determine numBlocks and TagLength
		int numBlocksL1= cacheSizeL1/blockSize;
    	int numBlocksL2= cacheSizeL2/blockSize;
		memLatency=100+latency2;
	   	int blocksPerSetL1= numBlocksL1/assocL1;
		int blocksPerSetL2= numBlocksL2/assocL2;
		int[] bits=calcBits(blockSize,numBlocksL1,assocL1);
		blockOffsetBitsL1=bits[0];
		indexBitsL1=bits[1];
		taglengthL1=bits[2];
		bits=calcBits(blockSize,numBlocksL2,assocL2);
		blockOffsetBitsL2=bits[0];
	 	indexBitsL2=bits[1];
		taglengthL2=bits[2];


		//init empty caches
		cacheL1= new cacheEntry[assocL1][numBlocksL1];
		for(int j=0;j<assocL1;j++)
		{
			for(int i=0; i<numBlocksL1;i++) 
			{
				cacheL1[j][i]= new cacheEntry();
			}
		}
		cacheL2= new cacheEntry[assocL2][numBlocksL2];
		for(int j=0;j<assocL2;j++)
		{
			for(int i=0; i<numBlocksL2;i++) 
			{
				cacheL2[j][i]= new cacheEntry();
			}
		}
		
		
		int instAddrL1[];
		int instAddrL2[];
		/*reading in instruction
		while ((curInst= instStream.readLine()) != null) {
			String[] instArr=curInst.split(" ");
			instrAddrL1=decode(instArr[1],taglengthL1,indexBitsL1,blockOffsetBitsL1);
			instrAddrL2=decode(instArr[1],taglengthL2,indexBitsL2,blockOffsetBitsL2);
			//read op or write op
			if(instArr[0].equals("read"))
			else 
		}
		*/




		
		
		

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
		int blockOffsetBits= (int)(Math.log(blockSize));
		int indexBits= (int)(Math.log(numBlocks/assoc));
		
		return new int[]{blockOffsetBits,indexBits,32-blockOffsetBits-indexBits};	
	}
	//TODO:: write to cache read from cache


}
 