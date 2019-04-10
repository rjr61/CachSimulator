import java.util.Arrays;

public class Cache {

  private static cacheEntry[][] cache;
  private int association, numBlocks;

  //initialize empty
  public Cache() {
    cache = null;
    this.association = 0;
    this.numBlocks = 0;
  }

  public Cache(int association, int numBlocks) {
    cache = new cacheEntry[association][numBlocks];
    this.association = association;
    this.numBlocks = numBlocks;
    initCache();
  }

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

  // String formatted cache output
  public String toString() {
    StringBuilder sb = new StringBuilder();

    sb.append("==============\n");
    for(int i=0;i<getAssociation();i++) {
      for(int j=0;j<getNumBlocks();j++) {
        if(getCache() != null) {
          sb.append(getCache()[i][j]);
          sb.append("\n");
        }
      }
      sb.append("==============\n");
    }
   // sb.append("==============\n");

    return sb.toString();
  }

  // Prints a summary of variables
  public void printInfo() {
    System.out.println("association: " + getAssociation());
    System.out.println("numBlocks: " + getNumBlocks());
    System.out.println("cache: \n" + toString());
  }


}
