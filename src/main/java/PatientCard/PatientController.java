package PatientCard;

import ca.uhn.fhir.context.FhirContext;
//import ca.uhn.fhir.model.dstu2.resource.Bundle;
//import ca.uhn.fhir.model.dstu2.resource.Patient;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import org.hl7.fhir.dstu3.model.Bundle;
import org.hl7.fhir.dstu3.model.Patient;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class PatientController {



    @GetMapping("/")
    public String patient(String name, Model model) {
        FhirContext ctx = FhirContext.forDstu3();
        String serverBase = "http://localhost:8088/baseDstu3/";

        IGenericClient client = ctx.newRestfulGenericClient(serverBase);

        // Perform a search
        Bundle results = client
                .search()
                .forResource(Patient.class)
                .returnBundle(Bundle.class)
                .execute();

        String r = String.valueOf(results.getEntry().size());
        name = r;
        model.addAttribute("result_size", name);

        return "patient";
    }

}