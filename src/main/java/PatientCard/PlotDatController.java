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
import java.util.ListIterator;

@Controller
public class PlotDatController {

    @Autowired
    MyFhirClient mfc;

    @GetMapping("/patient={id:.+}/observation={name:.+}")
    public String observationPlotData(@PathVariable("id") String id, @PathVariable("name") String name, Model model) {
        ArrayList<Observation> observations = new ArrayList<>();
        //name = "Body Weight";
        name = "Estimated Glomerular Filtration Rate";
        Bundle o = this.mfc.client
                .search()
                .forResource(Observation.class)
                .where(new ReferenceClientParam("patient").hasId(id))
                .returnBundle(Bundle.class)
                .execute();
        getObseravtionsFormBundle(o, observations, name);
        model.addAttribute("observations", observations);
        return "patientPlot";
    }

    private void getObseravtionsFormBundle (Bundle bundle, ArrayList<Observation> list, String name) {
        for(ListIterator<Bundle.BundleEntryComponent> iter = bundle.getEntry().listIterator(); iter.hasNext(); ) {
            Observation observation = (Observation) iter.next().getResource();
            if(observation.getCode().hasText()){
                if (observation.getCode().getText().equals(name)) {
                    list.add(observation);
                    try {
                        observation.getValueQuantity().getValue();
                        observation.getValueQuantity().getCode();

                    } catch (FHIRException e) {
                        e.printStackTrace();
                    }

                }
                //observation.getEffectiveDateTimeType().toHumanDisplay()
                ;
            } else if (observation.hasComponent()) {
                observation.getComponent();
            }


        }
    }
}
