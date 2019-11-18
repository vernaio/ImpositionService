package de.perfectpattern.print.imposition.service.imposition;

import org.junit.Before;
import org.junit.Test;

public class ManualImpositionServiceTest extends ImpositionServiceTest {

    @Before
    public void setup() {
        super.setManualCheck(true);
    }

    /**
     * Single sided job.
     */
    @Test
    public void manual_imposition_form_1() throws Exception {
        super.imposition_form_1();
    }

}