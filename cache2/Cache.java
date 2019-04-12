package cache2;

import java.util.Random;

public class Cache {

  // some global variable declarations
  private CacheEntry[][] cache;
  private int association, numBlocks, LRU_count, memData;
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
  }

  // initialize cache object and call initCache()
  public Cache(int association, int numBlocks, String name) {
    this.cache = new CacheEntry[association][numBlocks];
    this.association = association;
    this.numBlocks = numBlocks;
    this.LRU_count = 0;
    this.name=name;
    this.memData=generateMemAdress();
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

  // returns cache object
  private CacheEntry[][] getCache() {
    return this.cache;
  }

  public void setCache(int i, int j, CacheEntry d) {
    getCache()[i][j] = d;
  }

  private CacheEntry newCacheEntry(int valid, int tag, int data, int dirty, int LRU) {
    return new CacheEntry(valid, tag, data, dirty, LRU);
  }

  // checks if global cache object is null
  public boolean isNull() {
    if(getCache() != null) {
      return false;
    } else {
      return true;
    }
  }

  public void memToCache(int tag, int index) {
    // find next available space or evict
    CacheIndex space = open(index);

    if(space.i() != -1 && space.j() != -1) {
       setCache(space.i(), space.j(), newCacheEntry(1, tag, generateMemAdress(), 0, getLRUCount()));
    } else {
      evictLRU(tag, index);
    }
  }

  public void editCache(int tag, int index) {
    CacheIndex entry = where(index, tag);

    setCache(entry.i(), entry.j(), newCacheEntry(1, tag, generateMemAdress(), 0, getLRUCount()));
  }

  private void evictLRU(int tag, int index) {
    CacheIndex LRU_index = findLRU(index);

    setCache(LRU_index.i(), LRU_index.j(), newCacheEntry(1, tag, generateMemAdress(), 0, getLRUCount()));
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

  public int getCacheData(int tag, int index) {
    CacheIndex where = where(index, tag);

    return getCache()[where.i()][where.j()].getData();
  }

  public void writeToCache(int tag, int index, int data) {
    // find next available space or evict
    CacheIndex space = open(index);

    if(space.i() != -1 && space.j() != -1) {
      setCache(space.i(), space.j(), newCacheEntry(1, tag, data, 0, getLRUCount()));
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
}
