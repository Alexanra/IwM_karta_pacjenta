package PatientCard;

import ca.uhn.fhir.rest.api.MethodOutcome;
import ca.uhn.fhir.rest.gclient.TokenClientParam;
import org.hl7.fhir.dstu3.model.Address;
import org.hl7.fhir.dstu3.model.Bundle;
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
    public String editData(@PathVariable("id") String id, Model model) {

        Edit edit = new Edit();

        Bundle p = mfc.client
                .search()
                .forResource(Patient.class)
                .where(new TokenClientParam("_id").exactly().code(id))
                .returnBundle(Bundle.class)
                .execute();

        Patient patient = (Patient) p.getEntry().get(0).getResource();

        edit.setCity(patient.getAddressFirstRep().getCity());
        edit.setCountry(patient.getAddressFirstRep().getCountry());
        edit.setZipCode(patient.getAddressFirstRep().getPostalCode());

        model.addAttribute("patient", patient);
        model.addAttribute("patientAddress", edit);
        return "editPatient";//"redirect:/patient="+id;
    }

    @PostMapping("/patient={id:.+}/edited")
    public String editedData(@PathVariable("id") String id, @ModelAttribute Edit edit, Model model) {

        Address address = new Address();
        address.setCity(edit.getCity());
        address.setCountry(edit.getCountry());
        address.setPostalCode(edit.getZipCode());
        ArrayList<Address> addresses = new ArrayList();
        addresses.add(address);

        Bundle p = mfc.client
                .search()
                .forResource(Patient.class)
                .where(new TokenClientParam("_id").exactly().code(id))
                .returnBundle(Bundle.class)
                .execute();

        Patient patient = (Patient) p.getEntry().get(0).getResource();

        for (Address a: patient.getAddress()) {
            addresses.add(a);
        }
        patient.setAddress(addresses);

        MethodOutcome outcome = mfc.client.update()
                .resource(patient)
                .execute();

        return "redirect:/patient="+id;
    }


}

