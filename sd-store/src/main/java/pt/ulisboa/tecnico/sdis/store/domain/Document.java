package pt.ulisboa.tecnico.sdis.store.domain;

public class Document {
    
    // Attributes
    private String _nameDoc;
    private byte[] _content;
    private Tag _tag;
    
    // Constructor
    public Document(String nameDoc, Tag t) {
        _nameDoc = nameDoc;
        _content = null;
        _tag = t;
    }
    
    public Document(String nameDoc, byte[] content, Tag t) {
        _nameDoc = nameDoc;
        _content = content;
        _tag = t;
    }
    
    // Getters
    public String getName() { return _nameDoc; }
    public byte[] getContent() { return _content; }
    public Tag getTag() {return _tag; }
    
    // Setters
    public void setNameDoc(String nameDoc) { _nameDoc = nameDoc; }
    public void setContent(byte[] content) { _content = content; }
    public void setTag(Tag tag) { _tag = tag; }
}