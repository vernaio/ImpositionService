package de.perfectpattern.print.imposition;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.List;

import de.perfectpattern.commons.api.marshalling.XmlContext;
import de.perfectpattern.commons.api.marshalling.XmlContextProvider;
import de.perfectpattern.sPrint.one.v3.api.format.event.DtoAbstractEvent;
import de.perfectpattern.sPrint.one.v3.api.format.event.DtoEvents;
import de.perfectpattern.sPrint.one.v3.api.format.event.gangJob.DtoGangJobEvent;
import de.perfectpattern.sPrint.one.v3.api.format.workspace.DtoWorkspaces_ROOT;

public class BasicTest {
	
	private static XmlContext xc = new XmlContextProvider().get();
	
	public static void main(String[] args) {
		System.out.println("test");
		
		final String ss = "87C051BF-F576-40A0-93CF-20ECF9BC413F";
		final String s = "api/rest/workspaces/id=test/layoutTasks/id=87C051BF-F576-40A0-93CF-20ECF9BC413F/result/events/id=85970220-c783-4b45-aedc-8f23748e2ac5";
    String a = s.split("layoutTasks/id=")[1].split("/result")[0];
    System.out.println(a);
		
		FileInputStream fis = null;
		try {
			fis = new FileInputStream(new File("C:\\Users\\Alex\\pp\\PIMP\\PIMP-48\\clone.xml"));
		} catch(FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		Object umo = xc.unmarshal(fis);
		if (umo instanceof DtoWorkspaces_ROOT) {
			DtoWorkspaces_ROOT dtoWS = (DtoWorkspaces_ROOT) umo;
			//writeSpoInstance(umo);
			haveFunWithdtoWS(dtoWS);
		} else {
			throw new IllegalArgumentException("Cannot process " + umo.getClass().getCanonicalName());
		}
	}
	
	private static void haveFunWithdtoWS(DtoWorkspaces_ROOT dtoWS) {
		List<DtoAbstractEvent> a = dtoWS.getWorkspaces().get(0).getPlannedEvents().getEvents();
		System.out.println(a.size());
		for (DtoAbstractEvent ae:a) {
			if (ae instanceof DtoGangJobEvent) {
				DtoGangJobEvent dtoGJE = (DtoGangJobEvent) ae;
				System.out.println("Yey, wir haben Spass");
				System.out.println(dtoGJE.getGangJob().getBinderySignatures().getBinderySignatures().size());
				
        //float mediaWidth = DimensionUtil.micro2dtp((float) gangJobXml.media.format.@width.toFloat())
				
			float mediaWidth = dtoGJE.getGangJob().getMedia().getFormat().getWidth().floatValue();
				
				break;
			}
		}
		
	}

	private static void writeSpoInstance(Object o) {
		try {
			FileOutputStream fio = new FileOutputStream(new File ("C:\\Users\\Alex\\pp\\PIMP\\PIMP-48\\clone2.xml"));
			xc.marshal(o, true, fio);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
		
}
