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
import org.springframework.web.bind.annotation.*;

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
        if (nameId.isEmpty()) {
            Bundle results = this.mfc.client
                    .search()
                    .forResource(Patient.class)
                    //.where(new StringClientParam("family").matches().value(""))
                    .returnBundle(Bundle.class)
                    .execute();
            getDataFromBundle(results, this.nameId);

            total = results.getTotal() - 10;

            while (total > 0) {
                if (results.getLink(Bundle.LINK_NEXT) != null) {
                    // load next page
                    Bundle nextPage = this.mfc.client.loadPage().next(results).execute();
                    getDataFromBundle(nextPage, this.nameId);
                    total -= 10;
                }
            }
        }
        model.addAttribute("result", this.nameId);
        model.addAttribute("query", new Query());
        return "patientList";
    }

    @PostMapping("/search")
    public String patientSurname(@ModelAttribute Query query, Model model) {
        ArrayList<String[]> nameIdFiltered = new ArrayList<>();

        Bundle results = this.mfc.client
                .search()
                .forResource(Patient.class)
                .where(new StringClientParam("family").matches().value(query.getText()))
                .returnBundle(Bundle.class)
                .execute();
        getDataFromBundle(results, nameIdFiltered);

        while (results.getLink(Bundle.LINK_NEXT) != null) {
            // load next page
            results = this.mfc.client.loadPage().next(results).execute();
            getDataFromBundle(results, nameIdFiltered);
        }

        model.addAttribute("query", query.getText());
        model.addAttribute("result", nameIdFiltered);
        return "searchResults";
    }
/*
    @GetMapping("/search={surname:.+}")
    public String patientSurname(@PathVariable("surname") String surname, Model model) {
        ArrayList<String[]> nameIdFiltered = new ArrayList<>();

        Bundle results = this.mfc.client
                .search()
                .forResource(Patient.class)
                .where(new StringClientParam("family").matches().value(surname))
                .returnBundle(Bundle.class)
                .execute();
        getDataFromBundle(results, nameIdFiltered);

        while (results.getLink(Bundle.LINK_NEXT) != null) {
                // load next page
                results = this.mfc.client.loadPage().next(results).execute();
                getDataFromBundle(results, nameIdFiltered);
        }

        model.addAttribute("result", nameIdFiltered);

        return "patientList";
    }
*/

    private void getDataFromBundle(Bundle b, ArrayList<String[]> list) {
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
            list.add(patientInfo);
        }
    }

}