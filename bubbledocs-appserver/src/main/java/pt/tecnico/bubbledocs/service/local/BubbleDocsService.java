package pt.tecnico.bubbledocs.service.local;

import pt.ist.fenixframework.Atomic;
import pt.tecnico.bubbledocs.exception.*;
import pt.ist.fenixframework.FenixFramework;
import pt.tecnico.bubbledocs.domain.BubbleDocsServer;

// add needed import declarations

public abstract class BubbleDocsService {

	
    @Atomic
    public final void execute() throws BubbleDocsException {
    	checkData();
        dispatch();
    }
    
    public final void nonAtomicExecute() throws BubbleDocsException{
    	checkData();
        dispatch();
    }
    
    static BubbleDocsServer getBubbleDocsServer() {
    	return FenixFramework.getDomainRoot().getBubbleDocsServer();
    }

    protected abstract void dispatch() throws BubbleDocsException;
    protected abstract void checkData() throws BubbleDocsException;
}
