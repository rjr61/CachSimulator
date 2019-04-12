package cache2;

public class CacheEntry {
  private int valid;
  private int tag;
  private int data;
  private int dirty;
  private int LRU;

  public CacheEntry() {
    this.valid=0;
    this.tag=-1;
    this.data=-1;
    this.dirty=-1;
    this.LRU=-1;
  }

  public CacheEntry(int valid,int tag, int data,int dirty, int LRU) {
    this.valid=valid;
    this.tag=tag;
    this.data=data;
    this.dirty=dirty;
    this.LRU=LRU;
  }

  public int getTag() {
    return this.tag;
  }

  public int getValid() {
    return this.valid;
  }

  public int getLRU() {return this.LRU;}

  public int getData() {return this.data;}

  // setters

  public void setValid(int valid) {this.valid = valid;}

  public void setTag(int tag) {this.tag = tag;}

  public void setData(int data) {
    this.data = data;
  }


  public void setDirty(int dirty) {this.dirty = dirty;}

  public void setLRU(int lru) {
    this.LRU = lru;
  }

  public void setAll(int valid, int tag, int data, int dirty, int LRU) {
    setValid(valid);
    setTag(tag);
    setData(data);
    setDirty(dirty);
    setLRU(LRU);
  }

  public String toStringV() {
    String pstring = "";
    pstring += "|valid|tag|data|dirty|LRU|";
    pstring += toString();

    return pstring;
  }

  public String toString() {
    String pstring = "";
    pstring += "|" + valid + "|" + tag + "|" + data + "|" + dirty + "|" + LRU + "|";
    return pstring;
  }

}
