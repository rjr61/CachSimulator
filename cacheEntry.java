
public class cacheEntry {
	public int setSel=0;
	public int valid=0;
	public String tag=null;
	public String data=null;
	public int recent=0;
	public int dirty=0;
	
	public cacheEntry() {
		
	}
	public cacheEntry(int setSel) {
		this.setSel=setSel;
	}
	public cacheEntry(int setSel, int valid,String tag, String data,int recent,int dirty) {
		this.setSel=setSel;
		this.valid=valid;
		this.tag=tag;
		this.data=data;
		this.recent=recent;
		this.dirty=dirty;
	}

}
