/*
 * Created on 20 Jan 2022 
 * Copyright 2022 Paul Harrison (paul.harrison@manchester.ac.uk)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License in file LICENSE
 */ 

package org.ivoa.dm.proposal.prop;

import static org.junit.jupiter.api.Assertions.*;


/**
 * Tests concepts needed for e-MERLIN configuration.
 * @author Paul Harrison (paul.harrison@manchester.ac.uk) 
 * @since 20 Jan 2022
 */
class EmerlinExampleTest extends AbstractProposalTest {
    /**
     * @param ef
     */
    public EmerlinExampleTest() {
        super(new exampleFactory() {

            @Override
            public ExampleGenerator create() {
                return new EmerlinExample();
            }
        });
       
    }

   @org.junit.jupiter.api.Test 
   public  void testObservations() {
       ObservingProposal prop = propDbInOut();
       assertEquals( 1, prop.observations.size(), "number of observations");
       Observation obs = prop.observations.get(0);
       assertNotNull(obs.target);
       assertTrue(obs.target instanceof CelestialTarget);
      
   }
 
}


