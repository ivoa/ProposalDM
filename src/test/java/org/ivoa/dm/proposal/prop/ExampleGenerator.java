package org.ivoa.dm.proposal.prop;
/*
 * Created on 17/03/2022 by Paul Harrison (paul.harrison@manchester.ac.uk).
 */


public interface ExampleGenerator extends ProposalGenerator, TACFunctions {

    
    //TODO since these are just strings now - this interface is hardly necessary
    String getICRF();
    String getICRS();

}
