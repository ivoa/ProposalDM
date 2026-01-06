package org.ivoa.dm.proposal.prop;


/*
 * Created on 06/01/2026 by Paul Harrison (paul.harrison@manchester.ac.uk).
 */

public class NOTexampleTest extends AbstractProposalTest {
    public NOTexampleTest() {
        super(new ExampleFactory() {
            @Override
            public ExampleGenerator create() {
                return new BaseGenerator(new NOTexample());
            }
        });
    }
}
