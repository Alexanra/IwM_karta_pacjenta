package PatientCard;

import ca.uhn.fhir.rest.api.MethodOutcome;
import org.hl7.fhir.dstu3.model.Address;
import org.hl7.fhir.dstu3.model.Patient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.List;

@Controller
public class EditPatientController {
    @Autowired
    MyFhirClient mfc;

    @GetMapping("/patient={id:.+}/edit")
    public String editData(@PathVariable("id") String id,
                           @ModelAttribute("city") String city,
                           @ModelAttribute("country") String country,
                           @ModelAttribute("postalCode") String postalCode,
                           Model model) {
        Address address = new Address();
        address.setCity(city);
        address.setCountry(country);
        address.setPostalCode(postalCode);
        ArrayList<Address> addresses = new ArrayList<>();
        addresses.add(address);
        Patient patient = new Patient();
        patient.setAddress(addresses);
        patient.setId(id);

        MethodOutcome outcome = mfc.client.update()
                .resource(patient)
                .execute();

        return "editPatient";//"redirect:/patient="+id;
    }
}

