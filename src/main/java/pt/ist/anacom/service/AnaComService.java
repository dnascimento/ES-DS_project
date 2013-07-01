package pt.ist.anacom.service;

import jvstm.Atomic;
import pt.ist.anacom.shared.exceptions.AnaComException;


public abstract class AnaComService {

    @Atomic
    public void execute() throws AnaComException {
        dispatch();
    }

    public abstract void dispatch() throws AnaComException;
}
