
public class cacheEntry {
	private int valid;
	private int tag;
	private String data;
	private int recent;
	private int dirty;
	private int LRU;
	
	public cacheEntry() {
		this.valid=0;
		this.tag=0;
		this.data=null;
		this.recent=0;
		this.dirty=0;
		this.LRU=0;
	}

	public cacheEntry(int valid,int tag, String data,int recent,int dirty, int LRU) {
		this.valid=valid;
		this.tag=tag;
		this.data=data;
		this.recent=recent;
		this.dirty=dirty;
		this.LRU=LRU;
	}

	public int getTag() {
		return this.tag;
	}

	public void setData(String data) {
		this.data = data;
	}

	public void setLRU(int lru) {
		this.LRU = lru;
	}

	public int getValid() {
		return this.valid;
	}

	public String toStringV() {
		String pstring = "";
		pstring += "|valid|tag|data|recent|dirty|LRU";
		pstring += toString();

		return pstring;
	}

	public String toString() {
		String pstring = "";
		pstring += "|" + valid + "|" + tag + "|" + data + "|" + recent + "|" + dirty + "|" + LRU + "|";
		return pstring;
	}

}
