package pt.ulisboa.tecnico.sdis.store;

public class Tag {
    private int _seq;
    private int _cid;

    public Tag (int seq, int cid) {
    	_seq = seq;
    	_cid = cid;
    }
    
    public int getSeq() { return _seq; }
    public int getCid() { return _cid; }
    public void setSeq(int seq) { _seq = seq; }
    public void setCid(int cid) { _cid = cid; }
    
    public boolean equals(Tag t) {
    	return _seq == t.getSeq() ? _cid == t.getCid() : false;
    }
    
    public boolean greaterThan(Tag t) {
    	return _seq > t.getSeq() ? true : _seq == t.getSeq() ? _cid > t.getCid() : false;
    }
    
    public String toString() {
    	return _seq + "," + _cid;
    }
}