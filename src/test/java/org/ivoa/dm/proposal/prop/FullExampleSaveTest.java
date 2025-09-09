package org.ivoa.dm.proposal.prop;


/*
 * Created on 08/09/2025 by Paul Harrison (paul.harrison@manchester.ac.uk).
 */

import jakarta.persistence.TypedQuery;
import org.ivoa.dm.proposal.management.SubmittedProposal;
import org.ivoa.vodml.validation.AbstractBaseValidation;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class FullExampleSaveTest extends AbstractBaseValidation {



   /**
    * test the full DB save - note the transaction needs to be around the
    */
   @Test
   public void testDBSave()  {
      FullExample example = new FullExample();
      jakarta.persistence.EntityManager em = setupH2Db(example.getManagementModel().management().pu_name(), example.getManagementModel().modelDescription.allClassNames());

      em.getTransaction().begin();
      example.saveTodB(em);
      em.getTransaction().commit();
      dumpDbData(em, "fullexample.sql");
      em.clear(); //there is a hibernate error if we do not do this at least...
      TypedQuery<ObservingProposal> qp = em.createQuery("SELECT o FROM ObservingProposal o ", ObservingProposal.class);
      List<ObservingProposal> proposals = qp.getResultList();
      assertEquals(1,proposals.size(), "number of unsubmitted proposals");
      TypedQuery<SubmittedProposal> qs = em.createQuery("SELECT o FROM SubmittedProposal o ", SubmittedProposal.class);
      List<SubmittedProposal> submittedProposals = qs.getResultList();
      assertEquals(2, submittedProposals.size(), "number of submitted proposals");
      TypedQuery<Target> qsrc = em.createQuery("SELECT o FROM Target o",Target.class);
      List<Target> targets = qsrc.getResultList();
      assertEquals(3, targets.size(), "number of targets");
      TypedQuery<Person> psrc = em.createQuery("SELECT o FROM Person o", Person.class);
      List<Person> persons = psrc.getResultList();
      assertEquals(5, persons.size(), "number of persons");



   }

}
