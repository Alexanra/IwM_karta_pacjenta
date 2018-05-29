package PatientCard;


import ca.uhn.fhir.rest.gclient.ReferenceClientParam;
import ca.uhn.fhir.rest.gclient.StringClientParam;
import ca.uhn.fhir.rest.gclient.TokenClientParam;
import org.hl7.fhir.dstu3.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.ArrayList;
import java.util.ListIterator;

@Controller
public class PatientDataController {

    @Autowired
    MyFhirClient mfc;

    @GetMapping("/patient={id:.+}")
    public String patientData(@PathVariable("id") String id, Model model) {
        Patient patient;
        ArrayList<Observation> observations = new ArrayList<>();
        ArrayList<MedicationRequest> medicationRequests = new ArrayList<>();

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

        getObseravtionsFormBundle(o, observations);

        while (o.getLink(Bundle.LINK_NEXT) != null) {
            // load next page
            o = this.mfc.client.loadPage().next(o).execute();
            getObseravtionsFormBundle(o, observations);
        }

        Bundle m = this.mfc.client
                .search()
                .forResource(MedicationRequest.class)
                .where(new ReferenceClientParam("patient").hasId(id))
                .returnBundle(Bundle.class)
                .execute();

        getMedRequestFormBundle(m, medicationRequests);

        while (m.getLink(Bundle.LINK_NEXT) != null) {
            // load next page
            m = this.mfc.client.loadPage().next(m).execute();
            getMedRequestFormBundle(m, medicationRequests);
        }

        model.addAttribute("patient", patient);
        model.addAttribute("observations", observations);
        model.addAttribute("medications", medicationRequests);

        return "patientData";
    }

    private void getObseravtionsFormBundle (Bundle bundle, ArrayList<Observation> list) {
        for(ListIterator<Bundle.BundleEntryComponent> iter = bundle.getEntry().listIterator(); iter.hasNext(); ) {
            Observation observation = (Observation) iter.next().getResource();
            list.add(observation);

        }
    }

    private void getMedRequestFormBundle (Bundle bundle, ArrayList<MedicationRequest> list) {
        for(ListIterator<Bundle.BundleEntryComponent> iter = bundle.getEntry().listIterator(); iter.hasNext(); ) {
            MedicationRequest medicationRequest = (MedicationRequest) iter.next().getResource();
            list.add(medicationRequest);

        }
    }

}
