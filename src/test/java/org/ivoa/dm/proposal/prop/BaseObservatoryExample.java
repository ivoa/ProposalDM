/*
 * Created on 20 Oct 2024 
 * Copyright 2024 Paul Harrison (paul.harrison@manchester.ac.uk)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License in file LICENSE
 */ 

package org.ivoa.dm.proposal.prop;

import org.ivoa.dm.proposal.management.ProposalCycle;

/**
 *  .
 * @author Paul Harrison (paul.harrison@manchester.ac.uk) 
 * @since 20 Oct 2024
 */
public abstract class BaseObservatoryExample extends BaseExample implements TACFunctions {

    protected ProposalCycle cycle;

    /**
     * {@inheritDoc}
     * overrides @see org.ivoa.dm.proposal.prop.TACFunctions#getCycle()
     */
    @Override
    public ProposalCycle getCycle() {
         return cycle;
        
    }
    
    
    
}


