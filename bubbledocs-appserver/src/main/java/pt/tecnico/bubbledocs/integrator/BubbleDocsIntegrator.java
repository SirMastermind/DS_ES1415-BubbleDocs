package pt.tecnico.bubbledocs.integrator;


import pt.tecnico.bubbledocs.exception.*;
import pt.ist.fenixframework.FenixFramework;
import pt.tecnico.bubbledocs.domain.BubbleDocsServer;


public abstract class BubbleDocsIntegrator {

    public final void execute() throws BubbleDocsException {
        dispatch();
    }
    
    static BubbleDocsServer getBubbleDocsServer() {
    	return FenixFramework.getDomainRoot().getBubbleDocsServer();
    }

    protected abstract void dispatch() throws BubbleDocsException;
}
