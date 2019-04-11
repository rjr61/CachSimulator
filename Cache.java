
public class Cache {

  // some global variable declarations
  private cacheEntry[][] cache;
  private int association, numBlocks, LRU_count, tagLength,indexBits,blockOffsetBits,hits,misses,latency;
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
    this.name="";
    this.tagLength=0;
    this.indexBits=0;
    this.blockOffsetBits=0;
    this.hits=0;
    this.misses=0;
    this.latency=0;
  }

  // initialize cache object and call initCache()
  public Cache(int association, int numBlocks,String name,int tagLength,int indexBits,int blockOffsetBits,int latency) {
    this.cache = new cacheEntry[association][numBlocks];
    this.association = association;
    this.numBlocks = numBlocks;
    this.LRU_count = 0;
    this.name=name;
    this.tagLength=tagLength;
    this.indexBits=indexBits;
    this.blockOffsetBits=blockOffsetBits;
    this.hits=0;
    this.misses=0;
    this.latency=latency;
    initCache();
  }

  // initializes empty cache
  private void initCache() {
    for(int i=0;i<getAssociation();i++) {
      for(int j=0;j<getNumBlocks();j++) {
        this.cache[i][j]= new cacheEntry();
      }
    }
  }
  public int getLatency()
  {
    return this.latency;
  }
  public int getHits()
  {
    return this.hits;
  }
  public void incHits()
  {
    this.hits++;
  }
  public int getMisses()
  {
    return this.misses;
  }
  public void incMisses()
  {
    this.misses++;
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
  public int getTagLength()
  {
    return this.tagLength;
  }
  public int getBlockOffsetBits(){
    return this.blockOffsetBits;
  }
  public int getIndexBits(){
    return this.indexBits;
  }


  // returns cache object
  public cacheEntry[][] getCache() {
    return this.cache;
  }

  // checks if global cache object is null
  public boolean isNull() {
    if(getCache() != null) {
      return false;
    } else {
      return true;
    }
  }

/*  // returns a cacheEntry object; currently unused
  public cacheEntry getCacheEntry(int a, int n) {
    if(!isNull()) {
      return getCache()[a][n];
    }

    return null;
  }*/

  // checks if a tag exists at an index
  public boolean contains(int index, int tag) {
    if(!isNull()) {
      for (int i = 0; i < getAssociation(); i++) {
          if(getCache()[i][index].getValid() != -1 && getCache()[i][index].getTag() == tag) {
            return true;
        }
      }
    }
    return false;
  }

/*  // calls where() and either allocates the new data and sets the LRU, or calls set()
  // TODO: return type??
  public void update(int index, int tag) {
    int a = where(index, tag);
    if(a == -1) {
      set();
    } else {
      // TODO: come back to this
      getCache()[a][index].setData("new_data");
      getCache()[a][index].setLRU(-1);
    }
  }*/

  public void update(int index, int tag) {
    if(where(index, tag).i() == -1) {
      if(nextOpen(index).i() == -1) {
        // tag not found and no open indices
        //find smallest LRU value and evict
        evict(index, tag);
      } else {
        //System.out.println("open index at: " + nextOpen(index));
        setNew(nextOpen(index), tag);
      }
    } else {
      //System.out.println("found at: " + where(index, tag));
      set(where(index, tag));
    }
  }

  public void updateDirty(int index, int tag) {
    if(where(index, tag).i() == -1) {
      System.out.println("See updateDirty().");
    }
    else {
      CacheIndex ci = where(index, tag);
      getCache()[ci.i()][ci.j()].setLRU(LRU_count++);
      getCache()[ci.i()][ci.j()].setDirty(1);
      getCache()[ci.i()][ci.j()].setData("dirty");
    }
  }

  private void evict(int index, int tag) {
    CacheIndex toEvict = smallestLRU(index);

    if(toEvict.i() != -1) {
      setNew(toEvict, tag);
    } else {
      System.out.println("See evict method().");
    }
  }

  private CacheIndex smallestLRU(int j) {
    int smallestLRU = -1;
    CacheIndex result = new CacheIndex(-1, -1);

    for (int i = 0; i < getAssociation(); i++) {
      if(getCache()[i][j].getLRU() != -1 && smallestLRU == -1) {
        smallestLRU = getCache()[i][j].getLRU();
        result.set(i,j);
      } else if(getCache()[i][j].getLRU() != -1 && getCache()[i][j].getLRU() < smallestLRU) {
        smallestLRU = getCache()[i][j].getLRU();
        result.set(i,j);
      }
    }

    return result;
  }
  public String getEvictedInst(int j) {
    CacheIndex evicted =smallestLRU(j);
    cacheEntry evictBlock= this.cache[evicted.i][evicted.j];
    String tag= intToBinary(evictBlock.getTag());
    String index=intToBinary(j);

    int tag_len = tag.length();
    int index_len = index.length();

    if(tag_len!=getTagLength())
    {
      for(int i=0;i<getTagLength()-tag_len;i++)tag= "0"+tag;
    }
    if(index_len!=getIndexBits())
    {
      for(int i=0;i<getIndexBits()-index_len;i++)index= "0"+index;
    }
    String inst=""+tag+index;
    return inst;
  }
  public String intToBinary(int n)
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

/*  // TODO: does this check every i,j ???
  private CacheIndex largestLRU() {
    int largestLRU = -1;
    CacheIndex result = new CacheIndex(-1, -1);

    for (int i = 0; i < getAssociation(); i++) {
      for(int j=0; j<getNumBlocks();j++) {
        if(getCache()[i][j].getLRU() != -1 && largestLRU == -1) {
          largestLRU = getCache()[i][j].getLRU();
          result.set(i,j);
        } else if(getCache()[i][j].getLRU() != -1 && getCache()[i][j].getLRU() > largestLRU) {
          largestLRU = getCache()[i][j].getLRU();
          result.set(i,j);
        }
      }
    }

    return result;
  }
  */

  public void setNew(CacheIndex ci, int tag) {
    getCache()[ci.i()][ci.j()].setAll(1, tag, "new", 0, LRU_count++);
  }

  public void set(CacheIndex ci) {
    getCache()[ci.i()][ci.j()].setLRU(LRU_count++);
  }

  // searches across associations for the next open index
  public CacheIndex nextOpen(int index) {
    if (!isNull()) {
      for (int i = 0; i < getAssociation(); i++) {
        if (getCache()[i][index].getValid() == -1) {
          return new CacheIndex(i, index);
        }
      }
    }
    return new CacheIndex(-1, -1);
  }
  public int getCacheIndex(CacheIndex j)
  {
      return j.j();
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


/*  // finds next available index and allocates new data or needs to evict
  public void setNextAvailable() {
    CacheIndex ci = isFull();

    //cache is not full
    if(ci.i() != -1) {
      System.out.println(String.format("TDO: set information at %d, %d", ci.i(), ci.j()));
      getCache()[ci.i()][ci.j()].setAll(1, 666, "new", 222, 111);
    }
    else {
      // TODO: eviction
      System.out.println("TODO: eviction()");
    }
  }

  // returns index of next available spot
  public CacheIndex isFull() {
    if(!isNull()) {
      for (int i = 0; i < getAssociation(); i++) {
        for (int j = 0; j < getNumBlocks(); j++) {
          if(getCache()[i][j].getValid() == 0) {
            return new CacheIndex(i,j);
          }
        }
      }
    }
    return new CacheIndex(-1, -1);
  }*/

  // String formatted cache output
  public String toString() {
    StringBuilder sb = new StringBuilder();

    sb.append("==============\n");
    if(!isNull()) {
      for(int i=0;i<getAssociation();i++) {
        for (int j = 0; j < getNumBlocks(); j++) {
          sb.append(getCache()[i][j]);
          sb.append("\n");
        }
        sb.append("==============\n");
      }
    }

    return sb.toString();
  }

  // Prints a summary of variables
  public void printInfo() {
    System.out.println("association: " + getAssociation());
    System.out.println("numBlocks: " + getNumBlocks());
    System.out.println("cache: \n" + toString());
  }


}
