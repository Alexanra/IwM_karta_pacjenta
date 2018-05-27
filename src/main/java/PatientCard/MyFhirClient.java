package PatientCard;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
public class MyFhirClient {

    FhirContext ctx;
    IGenericClient client;
    @Value("${local.address}")
    String serverBase;

    @PostConstruct
    void connectToClient() {
        this.ctx = FhirContext.forDstu3();
        this.client = ctx.newRestfulGenericClient(this.serverBase);
    }
}
