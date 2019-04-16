package cache2;

import java.util.Random;

public class Cache {

  // some global variable declarations
  private CacheEntry[][] cache;
  private int association, numBlocks, LRU_count, memData, latency, miss, hit;
  private String name;

  // private class used for passing association and numblocks indices efficiently
  private class CacheIndex {
    int i;
    int j;

    public CacheIndex(int i, int j) {
      this.i = i;
      this.j = j;
    }

    public int i() {
      return this.i;
    }
    public int j() {
      return this.j;
    }

    public void set(int i, int j) {
      this.i = i;
      this.j = j;
    }

    public String toString() {
      return "associativity: " + i() + ", block: " + j();
    }
  }

  //initialize empty
  public Cache() {
    this.cache = null;
    this.association = 0;
    this.numBlocks = 0;
    this.LRU_count = 0;
    this.name = null;
    this.memData=0;
    this.latency=0;
    this.hit = 0;
    this.miss = 0;
  }

  // initialize cache object and call initCache()
  public Cache(int association, int numBlocks, String name,int latency) {
    this.cache = new CacheEntry[association][numBlocks];
    this.association = association;
    this.numBlocks = numBlocks;
    this.LRU_count = 0;
    this.name=name;
    this.memData=generateMemAdress();
    this.latency=latency;
    this.hit = 0;
    this.miss = 0;
    initCache();
  }

  // initializes empty cache
  private void initCache() {
    for(int i=0;i<getAssociation();i++) {
      for(int j=0;j<getNumBlocks();j++) {
        this.cache[i][j]= new CacheEntry();
      }
    }
  }

  public String getName(){
    return this.name;
  }
  //returns association
  public int getAssociation() {
    return this.association;
  }
  //return latency
  public int getLatency() {
    return this.latency;
  }
  //returns numBlocks
  public int getNumBlocks() {
    return this.numBlocks;
  }

  public int getLRUCount() {
    return this.LRU_count++;
  }
  public int getMemData() {
    return this.memData++;
  }

  public int getHit() {
    return this.hit;
  }

  public int getMiss() {
    return this.miss;
  }

  public void incrementHit() {
    this.hit++;
  }

  public void incrementMiss() {
    this.miss++;
  }

  // returns cache object
  private CacheEntry[][] getCache() {
    return this.cache;
  }

  public void setCache(int i, int j, CacheEntry d) {
    getCache()[i][j] = d;
  }

  public CacheEntry newCacheEntry(int valid, int tag, int data, int dirty, int LRU) {
    return new CacheEntry(valid, tag, data, dirty, LRU);
  }
  public void evict(CacheIndex toEvict)
  {
    setCache(toEvict.i,toEvict.j,new CacheEntry());
  }

  // checks if global cache object is null
  public boolean isNull() {
    if(getCache() != null) {
      return false;
    } else {
      return true;
    }
  }

  public boolean isDirty(int index, int tag) {
    CacheIndex check = where(index, tag);

    return getCache()[check.i()][check.j()].getDirty() == 1;
  }

  public void memToCache(int tag, int index) {
    // find next available space or evict
    CacheIndex space = open(index);

    if(space.i() != -1 && space.j() != -1) {
      int data=generateMemAdress();
       setCache(space.i(), space.j(), newCacheEntry(1, tag, data, 0, getLRUCount()));
    } else {
      evictLRU(tag, index);
    }
  }

  public void editCache(int tag, int index, int dirty) {
    CacheIndex entry = where(index, tag);

    setCache(entry.i(), entry.j(), newCacheEntry(1, tag, generateMemAdress(), dirty, getLRUCount()));
  }
  //to update data on write thru
  public void updateCache(int tag, int index, int data) {
    CacheIndex entry = where(index, tag);

    setCache(entry.i(), entry.j(), newCacheEntry(1, tag, data, 0, getLRUCount()));
  }

  public void updateCache2(int tag, int index, int data) {
    CacheIndex entry = where(index, tag);

    setCache(entry.i(), entry.j(), newCacheEntry(1, tag, data, 1, getLRUCount()));
  }
  public void updateLRU(int tag, int index) {
    CacheIndex entry = where(index, tag);
    CacheEntry updateEntry=this.getCache()[entry.i][entry.j];
    updateEntry.setLRU(this.getLRUCount());
    setCache(entry.i(), entry.j(), updateEntry);
  }
  private void evictData(int tag, int index,int data) {
    CacheIndex LRU_index = findLRU(index);

    setCache(LRU_index.i(), LRU_index.j(), newCacheEntry(1, tag, data, 0, getLRUCount()));
  }
  private void evictLRU(int tag, int index) {
    CacheIndex LRU_index = findLRU(index);

    setCache(LRU_index.i(), LRU_index.j(), newCacheEntry(1, tag, generateMemAdress(), 0, getLRUCount()));
  }

  public boolean LRU_isDirty(int index) {
    if(findLRU(index).i()==-1) return false;
    else return getCache()[findLRU(index).i()][findLRU(index).j()].getDirty() == 1;
  }

  private CacheIndex findLRU(int index) {
    int smallestLRU = -1;
    CacheIndex result = new CacheIndex(-1, -1);

    for (int i = 0; i < getAssociation(); i++) {
      if(getCache()[i][index].getLRU() != -1 && smallestLRU == -1) {
        smallestLRU = getCache()[i][index].getLRU();
        result.set(i,index);
      } else if(getCache()[i][index].getLRU() != -1 && getCache()[i][index].getLRU() < smallestLRU) {
        smallestLRU = getCache()[i][index].getLRU();
        result.set(i,index);
      }
    }

    return result;
  }

  public int[] getLRU(int index) {
    CacheIndex LRU = findLRU(index);

    return new int[] {getCache()[LRU.i()][LRU.j()].getTag(), getCache()[LRU.i()][LRU.j()].getData()};
  }

  // finds the association index where a tag is located, or returns -1
  public CacheIndex where(int index, int tag) {
    if(!isNull()) {
      for (int i = 0; i < getAssociation(); i++) {
        if(getCache()[i][index].getTag() == tag) {
          return new CacheIndex(i, index);
        }
      }
    }
    return new CacheIndex(-1, -1);
  }
  public CacheEntry getEntry(int index, int tag) {
    if(!isNull()) {
      for (int i = 0; i < getAssociation(); i++) {
        if(getCache()[i][index].getTag() == tag) {
          return this.getCache()[i][index];
        }
      }
    }
    return null;
  }

  public int getCacheData(int tag, int index) {
    CacheIndex where = where(index, tag);

    getCache()[where.i()][where.j()].setLRU(getLRUCount());
    return getCache()[where.i()][where.j()].getData();
  }

  public int getCacheData2(int tag, int index) {
    CacheIndex where = where(index, tag);

    return getCache()[where.i()][where.j()].getData();
  }

  public void writeToMem(int index) {
    CacheIndex LRU = findLRU(index);

    getCache()[LRU.i()][LRU.j()].setDirty(0);
  }

  public void writeToCache(int tag, int index, int data) {
    // find next available space or evict
    CacheIndex space = open(index);

    if(space.i() != -1 && space.j() != -1) {
      setCache(space.i(), space.j(), newCacheEntry(1, tag, data, 0, getLRUCount()));
    } else {
      evictData(tag, index,data);
    }
  }public void updateData(int tag, int index, int data,int lru) {
    // find next available space or evict
    CacheIndex space = open(index);

    if(space.i() != -1 && space.j() != -1) {
      setCache(space.i(), space.j(), newCacheEntry(1, tag, data, 0,lru));
    } else {
      evictLRU(tag, index);
    }
  }
  public void writeToCacheWB(int tag, int index, int data) {
    // find next available space or evict
    CacheIndex space = open(index);

    if(space.i() != -1 && space.j() != -1) {
      setCache(space.i(), space.j(), newCacheEntry(1, tag, data, 1, getLRUCount()));
    } else {
      evictLRU(tag, index);
    }
  }

  private int generateMemAdress() {
    Random rnd = new Random();
    int n = 100000 + rnd.nextInt(900000);
    return n;
  }

  // checks if a tag exists at an index
  public boolean contains(int index, int tag) {
    if(!isNull()) {
      for (int i = 0; i < getAssociation(); i++) {
        if(getCache()[i][index].getValid() != 0 && getCache()[i][index].getTag() == tag) {
          return true;
        }
      }
    }
    return false;
  }

  public CacheIndex open(int index) {
    for(int i=0;i<getAssociation();i++) {
      if(getCache()[i][index].getValid() == 0) {
        return new CacheIndex(i, index);
      }
    }

    return new CacheIndex(-1, -1);
  }
  //helper
  public boolean isFull(int index)
  {
    if(open(index).i==-1)return true;
    else return false;
  }

  // String formatted cache output
  public String toString() {
    StringBuilder sb = new StringBuilder();

    sb.append("==============\n");
    if(!isNull()) {
      for(int i=0;i<getAssociation();i++) {
        for (int j = 0; j < getNumBlocks(); j++) {
          if (getCache()[i][j].getValid() != 0) {
            sb.append(j + " ");
            sb.append(getCache()[i][j]);
            sb.append("\n");
          }
        }
        sb.append("==============\n");
      }
    }

    return sb.toString();
  }
  public int[] getEvictedInst(int j) {
    CacheIndex evicted =findLRU(j);
    CacheEntry evictBlock= this.cache[evicted.i][evicted.j];
    return new int[]{evictBlock.getTag(),j};
  }

}
