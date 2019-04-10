

public class cacheEntry {
	public int valid;
	public int tag;
	public String data;
	public int recent;
	public int dirty;
	public int LRU;
	
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

	public String toStringV() {
		String pstring = "";
		pstring += "Valid: " + valid + "\n";
		pstring += "Tag: " + tag + "\n";
		pstring += "Data: " + data + "\n";
		pstring += "Recent: " + recent + "\n";
		pstring += "Dirty: " + dirty + "\n";

		return pstring;
	}

	public String toString() {
		String pstring = "";
		pstring += "|" + valid + "|" + tag + "|" + data + "|" + recent + "|" + dirty + "|";
		return pstring;
	}

}
