package org.ivoa.dm.proposal.prop;


/*
 * Created on 09/09/2025 by Paul Harrison (paul.harrison@manchester.ac.uk).
 */

import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.persistence.TypedQuery;
import jakarta.xml.bind.JAXBException;
import org.ivoa.dm.proposal.management.ProposalCycle;
import org.ivoa.dm.proposal.management.ProposalManagementModel;
import org.ivoa.dm.proposal.management.SubmittedProposal;
import org.ivoa.dm.proposal.management.TAC;
import org.junit.jupiter.api.Order;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactoryConfigurationError;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class EmerlinExampleTACTest extends AbstractProposalTest {
    public EmerlinExampleTACTest() {
       super(ProposalManagementModel.modelDescription, new EmerlinFactory());
    }

   protected Long cycleId;
    protected ProposalCycle cycle;

   @org.junit.jupiter.api.Test
   void reviewDmJPATest() {



      em.getTransaction().begin();
      List<ProposalCycle> props = em.createNamedQuery("ProposalCycle.findById", ProposalCycle.class)
            .setParameter("id", cycleId).getResultList();
      assertEquals(1, props.size());
      ProposalCycle pc = props.get(0);
      em.getTransaction().commit();
      assertNotNull(pc);

      em.getTransaction().begin();
      Long i = em.createQuery("select count(o) from SubmittedProposal o", Long.class).getSingleResult().longValue();
      em.getTransaction().commit();
      assertEquals( 1,i.longValue(),"number submitted proposals"); // trivial test

   }

   @org.junit.jupiter.api.Test
   public  void testTACQuery() throws JsonProcessingException {
   TypedQuery<TAC> q = em.createQuery("SELECT o FROM TAC o", TAC.class);
      TAC tac = q.getSingleResult();
      System.out.println(tac.getMembers().get(0).getId());
      String json = ProposalManagementModel.jsonMapper().writeValueAsString(tac);
      System.out.println(json);

   }
   @org.junit.jupiter.api.Test
   public  void testDbDelete() throws JsonProcessingException {
     TypedQuery<ObservingProposal> qp = em.createQuery("select o from ObservingProposal o", ObservingProposal.class);
      ObservingProposal propIn = qp.getSingleResult();
      propIn.forceLoad();
      em.getTransaction().begin();

      propIn.delete(em);
      em.getTransaction().commit();

   }
   @org.junit.jupiter.api.Test
   void reviewJSONTest() throws JsonProcessingException  {


      ProposalManagementModel model = new ProposalManagementModel();
      model.addContent(doTacWork());
      model.processReferences();

      ProposalManagementModel modelin = modelRoundTripJSONwithTest(model);
      List<ProposalCycle> revs = modelin.getContent(ProposalCycle.class);
      assertEquals(1, revs.size());

   }

   @org.junit.jupiter.api.Test
   @Order(1010)
   void reviewJaxbTest() throws JAXBException,
         TransformerConfigurationException, ParserConfigurationException,
         TransformerFactoryConfigurationError, TransformerException {
      logger.debug("starting test");
      ProposalManagementModel model = new ProposalManagementModel();
      model.addContent(doTacWork());
      model.processReferences();
      validateModel(model);
      ProposalManagementModel modelin = modelRoundTripXMLwithTest(model);
      List<ProposalCycle> revs = modelin.getContent(ProposalCycle.class);
      assertEquals(1, revs.size());

   }

   protected ProposalCycle doTacWork() { //TODO this probably needs refactoring with new ordered tests


      ProposalModel pm = new ProposalModel();
      pm.createContext();
      SubmittedProposal sprop = ex.submitProposal(proposal, cycle);
      sprop.updateClonedReferences();
      ex.allocateProposal(sprop);
      return cycle;

   }

   @Override
   protected void doUrTest() {
      ex = ef.create();

      em.getTransaction().begin();
      final ObservingProposal localproposal = ex.getProposal();
      ProposalManagementModel model = new ProposalManagementModel();
      model.addContent(localproposal);
      final ProposalCycle cycle = ex.getCycle();
      model.addContent(cycle);
      model.management().persistRefs(em);
      em.persist(localproposal);
      em.persist(cycle);
      em.getTransaction().commit();

      //flush any existing entities
      em.clear();
      em.getEntityManagerFactory().getCache().evictAll();
      //read in again
      cycleId = cycle.getId(); //FIXME - need to add something in VO-DML gen to know what the "natural key" is for db work.

      proposalId = localproposal.getId();
      retrieveProposal();
      retrieveCycle();
   }

   private void retrieveCycle() {
      em.getTransaction().begin();
      List<ProposalCycle> props = em.createNamedQuery("ProposalCycle.findById", ProposalCycle.class)
            .setParameter("id", cycleId).getResultList();
      assertEquals(1, props.size());
      cycle = props.get(0);
      em.getTransaction().commit();
   }
}
