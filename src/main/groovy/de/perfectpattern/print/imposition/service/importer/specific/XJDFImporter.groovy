package de.perfectpattern.print.imposition.service.importer.specific


import de.perfectpattern.print.imposition.model.Sheet
import de.perfectpattern.print.imposition.model.type.Rectangle
import org.springframework.stereotype.Controller

@Controller
public class XJDFImporter implements Importer {

    /**
     * Default constructor
     */
    public XJDFImporter() {

    }

    @Override
    boolean acceptDocument(byte[] bytes) {
        return false
    }

    @Override
    public Sheet importDocument(byte[] bytes) {

        def xjdf = new XmlSlurper().parse(new ByteArrayInputStream(bytes))

        Sheet layout = new Sheet()

        layout.setSurfaceContentsBox(new Rectangle(
                xjdf.ResourceSet.find {
                    it.@Name="Sheet"
                }.Resource.Layout.@SurfaceContentsBox.toString()
        ))

        return layout
    }
}
