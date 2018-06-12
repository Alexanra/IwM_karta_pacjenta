package PatientCard;

import ca.uhn.fhir.rest.gclient.ReferenceClientParam;
import ca.uhn.fhir.rest.gclient.TokenClientParam;
import org.hl7.fhir.dstu3.model.Bundle;
import org.hl7.fhir.dstu3.model.MedicationRequest;
import org.hl7.fhir.dstu3.model.Observation;
import org.hl7.fhir.dstu3.model.Patient;
import org.hl7.fhir.exceptions.FHIRException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.ListIterator;

@Controller
public class PlotDataController {

    @Autowired
    MyFhirClient mfc;

    @GetMapping("/patient={id:.+}/observation={name:.+}")
    public String observationPlotData(@PathVariable("id") String id, @PathVariable("name") String name, Model model) {
        ArrayList<String[]> observations = new ArrayList<>();
        //name = "Estimated Glomerular Filtration Rate";

        Patient patient;
        Bundle p = mfc.client
                .search()
                .forResource(Patient.class)
                .where(new TokenClientParam("_id").exactly().code(id))
                .returnBundle(Bundle.class)
                .execute();
        patient = (Patient) p.getEntry().get(0).getResource();

        Bundle o = this.mfc.client
                .search()
                .forResource(Observation.class)
                .where(new ReferenceClientParam("patient").hasId(id))
                .returnBundle(Bundle.class)
                .execute();
        try {
            getObseravtionsFormBundle(o, observations, name);
        } catch (FHIRException e) {
            e.printStackTrace();
        }

        while (o.getLink(Bundle.LINK_NEXT) != null) {
            // load next page
            o = this.mfc.client.loadPage().next(o).execute();
            try {
                getObseravtionsFormBundle(o, observations, name);
            } catch (FHIRException e) {
                e.printStackTrace();
            }
        }
        model.addAttribute("observations", observations);
        model.addAttribute("patient", patient);
        model.addAttribute("patientId", id);
        return "patientPlot";
    }

    private void getObseravtionsFormBundle (Bundle bundle, ArrayList<String[]> list, String name) throws FHIRException {
        for(ListIterator<Bundle.BundleEntryComponent> iter = bundle.getEntry().listIterator(); iter.hasNext(); ) {
            Observation observation = (Observation) iter.next().getResource();
            if(observation.getCode().hasText()){
                if (observation.getCode().getText().replace(" ", "_").replace("/", "-").equals(name)) {
                    String[] array = new String[4];
                    array[0] = observation.getCode().getText();
                    array[1] = observation.getEffectiveDateTimeType().toHumanDisplay();
                    array[2] = String.valueOf(observation.getValueQuantity().getValue());
                    array[3] = observation.getValueQuantity().getCode();

                    list.add(array);
                }
            }
        }
        Collections.sort(list,new Comparator<String[]>() {
            public int compare(String[] strings, String[] otherStrings) {
                return strings[1].compareTo(otherStrings[1]);
            }
        });
    }

}
