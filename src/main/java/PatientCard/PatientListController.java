package PatientCard;

import ca.uhn.fhir.rest.annotation.Search;
import ca.uhn.fhir.rest.api.EncodingEnum;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import ca.uhn.fhir.rest.gclient.StringClientParam;
import org.hl7.fhir.dstu3.model.Bundle;
import org.hl7.fhir.dstu3.model.Patient;
import org.hl7.fhir.dstu3.model.StringType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

@Controller
public class PatientListController {

    @Autowired
    MyFhirClient mfc;
    ArrayList<String[]> nameId = new ArrayList<>();
    Integer total;

    @GetMapping("/")
    public String patientList(Model model) {
        // Perform a search
        Bundle results = this.mfc.client
                .search()
                .forResource(Patient.class)
                //.where(new StringClientParam("family").matches().value("Abbott701"))
                .returnBundle(Bundle.class)
                .execute();
        getDataFromBundle(results);

        total = results.getTotal() - 10;

        while (total > 0) {
            if (results.getLink(Bundle.LINK_NEXT) != null) {
                // load next page
                Bundle nextPage = this.mfc.client.loadPage().next(results).execute();
                getDataFromBundle(nextPage);
                total -= 10;
            }
        }

        model.addAttribute("result", this.nameId);
        return "patientList";
    }


    private void getDataFromBundle(Bundle b) {
        for(ListIterator<Bundle.BundleEntryComponent> iter = b.getEntry().listIterator(); iter.hasNext(); ) {
            Patient patient = (Patient) iter.next().getResource();
            String names = "";
            for(StringType name: patient.getNameFirstRep().getGiven()) {
                names = names + " " + name.toString();
            }
            names.replaceFirst(" ", "");
            String surname = patient.getNameFirstRep().getFamily();
            String id = patient.getIdElement().getIdPart();
            String[] patientInfo = {names, surname, id};
            this.nameId.add(patientInfo);
        }
    }

}