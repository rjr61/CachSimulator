#include<stdio.h>
#include<stdlib.h>
#include<math.h>

typedef struct cacheEntry{
    int valid;
    char *tag;
    char *block;
    int index;
    int LRU;
    int dirty;
    int tag_length;
    struct cacheEntry* nextSetEntry;
} cacheEntry;
void create_cache(int ,struct cacheEntry* ,int ,int );
struct cacheEntry* addNextEntry(int,int ,int , int ,int );
void printCache(struct cacheEntry* , int, int, int ,int);
cacheEntry* L1cache;
cacheEntry* L2cache;
int main(int argc,char **argv)
{

    if(argc<10 || argc>10){
        printf("Error incorrect format\n");
        return 1;
    }
    int cacheSizeL1=atoi(argv[1]);
    int cacheSizeL2=atoi(argv[2]);
    int latencyL1=atoi(argv[3]);
    int latencyL2=atoi(argv[4]);
    int blockSize=atoi(argv[5]);
    int assoc=atoi(argv[6]);
    int writePol=atoi(argv[7]);
    int allocPol=atoi(argv[8]);
    int maxMisses=atoi(argv[9]);
    int memLatency= 100+latencyL2;
    printf(" args were | %d || %d || %d || %d |", cacheSizeL1,cacheSizeL1,blockSize,assoc);
    int numBlocksL1= cacheSizeL1/blockSize;
    int numBlocksL2= cacheSizeL2/blockSize;
    //assume byte addressable 
    int blockOffsetBits= (int)(log(blockSize));
    int indexBitsL1= (int)(log(numBlocksL1/assoc));
    int indexBitsL2= (int)(log(numBlocksL2/assoc));
    //assume mem 2048 2^11 
    int taglengthL1= 11-blockOffsetBits-indexBitsL1;
    int taglengthL2= 11-blockOffsetBits-indexBitsL2;
    //createL1

    create_cache(numBlocksL1,L1cache,assoc,taglengthL1);
    create_cache(numBlocksL2,L2cache,assoc,taglengthL2);

    printCache(L1cache,numBlocksL1,assoc,blockSize,taglengthL1);
    printCache(L2cache,numBlocksL2,assoc,blockSize,taglengthL2);

    free(L1cache);
    free(L2cache);
    return 0;
}

void create_cache(int numBlocks,struct cacheEntry* cache,int assoc,int taglength)
{
    cache=malloc(numBlocks*(sizeof(cacheEntry)));
    struct cacheEntry* curEntry=cache;
    //if direct map i.e. assoc==numBlocks
    int i=0;
    if(assoc==1)
    {
        for(i;i<numBlocks;i++)
        {
            struct cacheEntry cur;
            cur.tag=NULL;
            cur.block=NULL;
            cur.index=i;
            cur.valid=0;
            cur.LRU=0;
            cur.tag_length=taglength;
            cur.valid=0;
            cur.nextSetEntry=NULL;
            curEntry[i]=cur;
        }
    }
    else 
    {
        for(i;i<numBlocks/assoc;i++)
        {
            struct cacheEntry cur;
            cur.tag=NULL;
            cur.block=NULL;
            cur.index=i;
            cur.valid=0;
            cur.LRU=0;
            cur.tag_length=taglength;
            cur.valid=0;
            cur.nextSetEntry=addNextEntry(numBlocks, assoc, taglength, i, 1);
            curEntry[i]=cur;
        }
    }

}

struct cacheEntry* addNextEntry(int numBlocks,int assoc,int taglength, int i,int set)
{
        
    struct cacheEntry* cur;
    cur->tag=NULL;
    cur->block=NULL;
    cur->index=i;
    cur->valid=0;
    cur->LRU=0;
    cur->tag_length=taglength;
    cur->valid=0;
    if(set==assoc) cur->nextSetEntry=NULL;
    else cur->nextSetEntry=addNextEntry(numBlocks, assoc, taglength, i, set++);
    return cur;
}

void printCache(struct cacheEntry* cache, int numBlocks, int assoc, int blockSize,int taglength)
{
    int i=0;

    for(i;i<assoc;i++)
    {
        printf("|index||LRU||valid||dirty||---tag---|||-----block------|");
    }
    i=0;
    struct cacheEntry cur = cache[0];
    for(i;i<numBlocks/assoc;i++)
    {
        cur=cache[i];
        printf("| %d || %d || %d || %d || %c || %c |", cur.index,cur.LRU,cur.valid,cur.dirty,cur.tag,cur.block);
    }

}
