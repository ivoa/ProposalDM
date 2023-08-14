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

import java.util.Arrays;
import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;

import org.ivoa.dm.proposal.management.ProposalCycle;


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
   
     @org.junit.jupiter.api.Test 
   public  void testDbCreate() {
       
        jakarta.persistence.EntityManager em = setupH2Db(ProposalModel.pu_name());
        em.getTransaction().begin();
        final ObservingProposal proposal = ex.getProposal();
        proposal.persistRefs(em);
        em.persist(proposal);
        final ProposalCycle cycle = ex.getCycle();
        cycle.persistRefs(em);
        em.persist(cycle);
        em.getTransaction().commit();

   }
   
      @org.junit.jupiter.api.Test 
   public void testJackson() throws JsonProcessingException
   {
   
       List<Field> fields = Arrays.asList(new TargetField("first"), new TargetField("second"),
               new Point(new org.ivoa.dm.stc.coords.Point(),"a point") );
       String json = ProposalModel.jsonMapper().writeValueAsString(fields.get(0));
       assertEquals("{\"@type\":\"proposal:TargetField\",\"_id\":0,\"name\":\"first\"}", json);
       System.out.println(json);
       System.out.println(ProposalModel.jsonMapper().writeValueAsString(new TargetField("targetField")));
       
       
   }
   
 
}


