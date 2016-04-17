package pt.ulisboa.tecnico.sdis.store.dto;

import pt.ulisboa.tecnico.sdis.store.domain.Tag;

public class DocumentDTO {
	private byte[] _document;
	private Tag _tag;
	
	public DocumentDTO(byte[] document, Tag tag) {
		_document = document;
		_tag = tag;
	}
	
	public byte[] getDocument() { return _document; }
	public Tag getTag() { return _tag; }
	public void setDocument(byte[] document) { _document = document; }
	public void setTag(Tag tag) { _tag = tag; }
}
