package org.ivoa.dm.proposal.prop;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

import static org.ivoa.dm.proposal.prop.ObservingProposal.createObservingProposal;

import static org.junit.jupiter.api.Assertions.*;

/*
 * Standard test that only tests basic things about model.
 * Created on 15/12/2021 by Paul Harrison (paul.harrison@manchester.ac.uk).
 */

class ProposalDMTest extends AbstractProposalTest {
    @BeforeEach
    void setUp() {

        proposal = createObservingProposal(proposalCommonSetup()
      );

    }

    @AfterEach
    void tearDown() {
    }

}