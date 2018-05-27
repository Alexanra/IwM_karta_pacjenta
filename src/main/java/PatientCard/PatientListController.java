package PatientCard;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import ca.uhn.fhir.rest.gclient.StringClientParam;
import org.hl7.fhir.dstu3.model.Bundle;
import org.hl7.fhir.dstu3.model.Patient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class PatientListController {

    @Autowired
    MyFhirClient mfc;

    @GetMapping("/")
    public String patient(Model model) {
        // Perform a search
        Bundle results = this.mfc.client
                .search()
                .forResource(Patient.class)
                //.where(new StringClientParam("family").matches().value("Abbott701"))
                .returnBundle(Bundle.class)
                .execute();

        String type = ((Patient) results.getEntry().get(1).getResource()).getNameFirstRep().getGiven().get(0).toString();
        String r = String.valueOf(results.getEntry().size());
        String name = type;
        model.addAttribute("result_size", name);

        return "patientList";
    }

}