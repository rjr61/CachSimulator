
public class Cache {

  // some global variable declarations
  private cacheEntry[][] cache;
  private int association, numBlocks, LRU_count;

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
  }

  // initialize cache object and call initCache()
  public Cache(int association, int numBlocks) {
    this.cache = new cacheEntry[association][numBlocks];
    this.association = association;
    this.numBlocks = numBlocks;
    this.LRU_count = 0;
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

  //returns association
  public int getAssociation() {
    return this.association;
  }

  //returns numBlocks
  public int getNumBlocks() {
    return this.numBlocks;
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
        evict();
      } else {
        //System.out.println("open index at: " + nextOpen(index));
        set(nextOpen(index));
      }
    } else {
      //System.out.println("found at: " + where(index, tag));
      set(where(index, tag));
    }
  }

  private void evict() {
    CacheIndex toEvict = smallestLRU();

    if(toEvict.i() != -1) {
      set(toEvict);
    } else {
      System.out.println("See evict method().");
    }
  }

  // TODO: does this check every i,j ???
  private CacheIndex smallestLRU() {
    int smallestLRU = -1;
    CacheIndex result = new CacheIndex(-1, -1);

    for (int i = 0; i < getAssociation(); i++) {
      for(int j=0; j<getNumBlocks();j++) {
        if(getCache()[i][j].getLRU() != -1 && smallestLRU == -1) {
          smallestLRU = getCache()[i][j].getLRU();
          result.set(i,j);
        } else if(getCache()[i][j].getLRU() != -1 && getCache()[i][j].getLRU() < smallestLRU) {
          smallestLRU = getCache()[i][j].getLRU();
          result.set(i,j);
        }
      }
    }

    return result;
  }

  // TODO: does this check every i,j ???
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

  public void set(CacheIndex ci) {
    getCache()[ci.i()][ci.j()].setAll(1, 666, "new", 0, LRU_count++);
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
