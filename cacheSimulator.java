import java.util.*;
import java.lang.Math;
public class cacheSimulator {

	public static void main(String[] args) {
		
		Scanner kb= new Scanner(System.in);
		int cacheSize=0;
		int latency1=0;
		int latency2=0;
		int memLatency=0;
		int blockSize=0;
		int numBlocks=0;
		int blockOffsetBits=0;
		int assoc=0;
		int setBits=0;
		
		System.out.println("Enter cache size in bytes");
		cacheSize=kb.nextInt();
		System.out.println("Enter access latency of Level 1 cache");
		latency1=kb.nextInt();
		System.out.println("Enter access latency of Level 2 cache");
		latency2=kb.nextInt();
		System.out.println("Enter block size in bytes");
		blockSize=kb.nextInt();
		System.out.println("Enter set associativity");
		assoc=kb.nextInt();
		/*System.out.println("Enter write policy");
		System.out.println("Enter allocation policy");
		System.out.println("Enter max number of misses");*/
		
		
		memLatency=100+latency2;
		blockOffsetBits=(int) Math.log(blockSize);
		setBits=(int) Math.log(numBlocks/assoc);
		
		//init empty cache
		cacheEntry[] cache= new cacheEntry[numBlocks];
		for(int i=0; i<numBlocks;i++) 
		{
			cache[i]= new cacheEntry(i/assoc);
		}
		
		
		

	}

}
