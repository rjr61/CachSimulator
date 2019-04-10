
public class Cache {

  // some global variable declarations
  private static cacheEntry[][] cache;
  private int association, numBlocks;

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
  }

  //initialize empty
  public Cache() {
    cache = null;
    this.association = 0;
    this.numBlocks = 0;
  }

  // initialize cache object and call initCache()
  public Cache(int association, int numBlocks) {
    cache = new cacheEntry[association][numBlocks];
    this.association = association;
    this.numBlocks = numBlocks;
    initCache();
  }

  // initializes empty cache
  private void initCache() {
    for(int i=0;i<getAssociation();i++) {
      for(int j=0;j<getNumBlocks();j++) {
        cache[i][j]= new cacheEntry();
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

  // returns a cacheEntry object; currently unused
  public cacheEntry getCacheEntry(int a, int n) {
    if(!isNull()) {
      return getCache()[a][n];
    }

    return null;
  }

  // checks if a tag exists at an index; currently not used
  public boolean contains(int index, int tag) {
    if(!isNull()) {
      for (int i = 0; i < getAssociation(); i++) {
          if(getCache()[i][index].getTag() == tag) {
            return true;
        }
      }
    }
    return false;
  }

  // finds the association index where a tag is located, or returns -1
  public int where(int index, int tag) {
    if(!isNull()) {
      for (int i = 0; i < getAssociation(); i++) {
        if(getCache()[i][index].getTag() == tag) {
          return i;
        }
      }
    }
    return -1;
  }

  // calls where() and either allocates the new data and sets the LRU, or calls set()
  // TODO: return type??
  public void update(int index, int tag) {
    int a = where(index, tag);
    if(a == -1) {
      // TODO: set()
    } else {
      // TODO: come back to this
      getCache()[a][index].setData("new_data");
      getCache()[a][index].setLRU(-1);
    }
  }

  // finds next available index and allocates new data or needs to evict
  public void set() {
    CacheIndex ci = isFull();
    if(ci.i() != -1) {
      getCache()[ci.i()][ci.j()].setData("new_data");
      getCache()[ci.i()][ci.j()].setLRU(-1);
    }
    else {
      // TODO: eviction
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
  }

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
    }
      sb.append("==============\n");
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
