package org.ivoa.dm.proposal.prop;
/*
 * Created on 17/03/2022 by Paul Harrison (paul.harrison@manchester.ac.uk).
 */

import org.ivoa.dm.proposal.management.ProposalCycle;
import org.ivoa.dm.stc.coords.SpaceSys;

public interface ExampleGenerator extends ProposalGenerator, TACFunctions {

    
    
    SpaceSys getICRF();
    SpaceSys getICRS();

}
