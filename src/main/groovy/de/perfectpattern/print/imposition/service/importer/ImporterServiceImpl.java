package de.perfectpattern.print.imposition.service.importer;

import de.perfectpattern.print.imposition.model.Sheet;
import de.perfectpattern.print.imposition.service.importer.specific.Importer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.io.IOException;
import java.util.List;

/**
 * Implementation of the ImporterService interface.
 */
@Controller
public class ImporterServiceImpl implements ImporterService {

    @Autowired
    private List<Importer> importers;


    @Override
    public Sheet importDocument(byte[] bytes) throws IOException, Exception {
        Sheet sheet = null;


        for(int i = 0; i < importers.size() && sheet == null; i ++)  {
            Importer importer = importers.get(i);

            if (importer.acceptDocument(bytes)) {

                // import file
                sheet = importer.importDocument(bytes);
            }
        }

        return sheet;
    }
}
