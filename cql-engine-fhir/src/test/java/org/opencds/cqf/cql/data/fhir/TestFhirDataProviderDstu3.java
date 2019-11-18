package org.opencds.cqf.cql.data.fhir;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.jpa.searchparam.registry.SearchParamRegistryDstu3;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import org.hl7.fhir.dstu3.model.*;
import org.opencds.cqf.cql.data.CompositeDataProvider;
import org.opencds.cqf.cql.execution.Context;
import org.opencds.cqf.cql.terminology.fhir.FhirTerminologyProvider;
import org.opencds.cqf.cql.model.*;
import org.opencds.cqf.cql.retrieve.*;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;

import static org.testng.Assert.assertTrue;

public class TestFhirDataProviderDstu3 extends FhirExecutionTestBase {

    private FhirContext fhirContext = FhirContext.forDstu3();
    private  IGenericClient fhirClient = fhirContext.newRestfulGenericClient("http://measure.eval.kanvix.com/cqf-ruler/baseDstu3");

    // @Test
    public void testFhirClient() {
        Bundle patients = fhirClient.search().forResource("Patient").returnBundle(Bundle.class).execute();
        assertTrue(patients.getEntry().size() > 0);
    }

    // @Test
    public void testDataProviderRetrieve() {
        Dstu3FhirModelResolver modelResolver = new Dstu3FhirModelResolver();
		RestFhirRetrieveProvider retrieveProvider = new RestFhirRetrieveProvider(new SearchParamRegistryDstu3(), fhirContext.newRestfulGenericClient("http://measure.eval.kanvix.com/cqf-ruler/baseDstu3"));
		CompositeDataProvider provider = new CompositeDataProvider(modelResolver, retrieveProvider);
		String contextPath = modelResolver.getContextPath("Patient", "Patient").toString();
        FhirBundleCursor results = (FhirBundleCursor) provider.retrieve("Patient", contextPath, null, "Patient", null, null, null, null, null, null, null, null);

		// BaseFhirDataProvider provider = new FhirDataProviderStu3().setEndpoint("http://measure.eval.kanvix.com/cqf-ruler/baseDstu3");
        // FhirBundleCursorStu3 results = (FhirBundleCursorStu3) provider.retrieve("Patient", null, "Patient", null, null, null, null, null, null, null, null);

        assertTrue(results.iterator().hasNext());
    }

    // @Test
    public void testPatientRetrieve() {
        Dstu3FhirModelResolver modelResolver = new Dstu3FhirModelResolver();
		RestFhirRetrieveProvider retrieveProvider = new RestFhirRetrieveProvider(new SearchParamRegistryDstu3(), fhirContext.newRestfulGenericClient("http://measure.eval.kanvix.com/cqf-ruler/baseDstu3"));
		CompositeDataProvider provider = new CompositeDataProvider(modelResolver, retrieveProvider);
		String contextPath = modelResolver.getContextPath("Patient", "Patient").toString();
        Iterable<Object> results = provider.retrieve("Patient", contextPath, "Patient-12214", "Patient", null, null, null, null, null, null, null, null);

		// BaseFhirDataProvider provider = new FhirDataProviderStu3().setEndpoint("http://measure.eval.kanvix.com/cqf-ruler/baseDstu3");
        // Iterable<Object> results = provider.retrieve("Patient", "Patient-12214", "Patient", null, null, null, null, null, null, null, null);
        List<Patient> patients = new ArrayList<>();

        int resultCount = 0;
        for (Object o : results) {
            patients.add((Patient)o);
            resultCount++;
        }

        assertTrue(patients.size() == resultCount);
    }

    // @Test
    public void testChoiceTypes() {
        Context context = new Context(library);
        context.registerDataProvider("http://hl7.org/fhir", dstu3Provider);

        Object result = context.resolveExpressionRef("testChoiceTypes").getExpression().evaluate(context);
        Assert.assertTrue(result != null);
    }

    // @Test
    public void testDateType() {
        Context context = new Context(library);
        context.registerDataProvider("http://hl7.org/fhir", dstu3Provider);
        context.setContextValue("Patient", "Patient-12214");

        Object result = context.resolveExpressionRef("testDateType").getExpression().evaluate(context);
        Assert.assertTrue(result != null);
    }

    @Test
    public void testFhirObjectEqual()
    {
        Context context = new Context(library);
        context.registerDataProvider("http://hl7.org/fhir", dstu3Provider);
        Object result = context.resolveExpressionRef("testFhirObjectEqual").getExpression().evaluate(context);
        Assert.assertTrue((Boolean) result);
    }

    @Test
    public void testFhirObjectEquivalent()
    {
        Context context = new Context(library);
        context.registerDataProvider("http://hl7.org/fhir", dstu3Provider);
        Object result = context.resolveExpressionRef("testFhirObjectEquivalent").getExpression().evaluate(context);
        Assert.assertTrue((Boolean) result);
    }

//    TODO - fix
//    @Test
    // public void testPostSearch() {
    //     Context context = new Context(library);

    //     String patientId = "post-search-example";
    //     Patient patient = new Patient();

    //     dstu3Provider.fhirClient.update().resource(patient).withId(patientId).execute();

    //     MedicationRequest request = new MedicationRequest();
    //     request.setIntent(MedicationRequest.MedicationRequestIntent.ORDER)
    //             .setStatus(MedicationRequest.MedicationRequestStatus.ACTIVE)
    //             .setMedication(new CodeableConcept().addCoding(new Coding().setCode("1049502").setSystem("http://www.nlm.nih.gov/research/umls/rxnorm")))
    //             .setSubject(new Reference("Patient/" + patientId))
    //             .setAuthoredOn(new Date());

    //     dstu3Provider.fhirClient.update().resource(request).withId(patientId).execute();

    //     dstu3Provider.setSearchUsingPOST(true);
    //     dstu3Provider.setTerminologyProvider(new FhirTerminologyProvider().setEndpoint("http://measure.eval.kanvix.com/cqf-ruler/baseDstu3", false));
    //     context.registerDataProvider("http://hl7.org/fhir", dstu3Provider);
    //     context.enterContext("Patient");
    //     context.setContextValue("Patient", patientId);

    //     Object result = context.resolveExpressionRef("Active Ambulatory Opioid Rx").getExpression().evaluate(context);
    //     Assert.assertTrue(result instanceof List && ((List) result).size() == 1);
    // }

    // @Test
    public void testList() {
        Dstu3FhirModelResolver modelResolver = new Dstu3FhirModelResolver();
		RestFhirRetrieveProvider retrieveProvider = new RestFhirRetrieveProvider(new SearchParamRegistryDstu3(), fhirContext.newRestfulGenericClient("http://fhir.hl7.de:8080/baseDstu3"));
		CompositeDataProvider provider = new CompositeDataProvider(modelResolver, retrieveProvider);
		String contextPath = modelResolver.getContextPath("Patient", "List").toString();
		Iterable<Object> results = provider.retrieve("Patient", contextPath, null, "List", null, null, null, null, null, null, null, null);

		//BaseFhirDataProvider provider = new FhirDataProviderStu3().setEndpoint("http://fhir.hl7.de:8080/baseDstu3");
        //FhirBundleCursorStu3 results = (FhirBundleCursorStu3) provider.retrieve("Patient", null, "List", null, null, null, null, null, null, null, null);
        List<ListResource> lists = new ArrayList<>();
        int resultCount = 0;
        for (Object o : results) {
            lists.add((ListResource)o);
            resultCount++;
        }

        assertTrue(lists.size() == resultCount);
    }

    //@Test
    public void testContained()
    {
        String patient = "{  \n" +
                "        \"resourceType\":\"Patient\",\n" +
                "        \"id\":\"81ee6581-02b9-44de-b026-7401bf36643a\",\n" +
                "        \"meta\":{  \n" +
                "          \"profile\":[  \n" +
                "            \"http://hl7.org/fhir/profiles/Patient\"\n" +
                "          ]\n" +
                "        },\n" +
                "        \"birthDate\":\"2012-01-01\"\n" +
                "      }";

        fhirClient.update().resource(patient).withId("81ee6581-02b9-44de-b026-7401bf36643a").execute();

        String condition = "{  \n" +
                "        \"resourceType\":\"Condition\",\n" +
                "        \"id\":\"77d90968-1965-4574-aa34-19d7d1483d8a\",\n" +
                "        \"contained\":[  \n" +
                "          {  \n" +
                "            \"resourceType\":\"Provenance\",\n" +
                "            \"id\":\"c76ceb3b-ff93-4d4a-ae1f-83b78ce39228\",\n" +
                "            \"target\":[  \n" +
                "              {  \n" +
                "                \"reference\":\"Condition/77d90968-1965-4574-aa34-19d7d1483d8a\"\n" +
                "              }\n" +
                "            ],\n" +
                "            \"entity\":[  \n" +
                "              {  \n" +
                "                \"role\":\"source\",\n" +
                "                \"whatReference\":{  \n" +
                "                  \"reference\":\"Claim/920013f1-da9b-42ec-89ec-b50069a7aa5c\"\n" +
                "                }\n" +
                "              }\n" +
                "            ]\n" +
                "          }\n" +
                "        ],\n" +
                "        \"clinicalStatus\":\"active\",\n" +
                "        \"verificationStatus\":\"confirmed\",\n" +
                "        \"code\":{  \n" +
                "          \"coding\":[  \n" +
                "            {  \n" +
                "              \"system\":\"ICD-10-CM\",\n" +
                "              \"code\":\"Z00.00\",\n" +
                "              \"display\":\"HEDIS2019_Ambulatory_Visits_ValueSets\"\n" +
                "            }\n" +
                "          ]\n" +
                "        },\n" +
                "        \"subject\":{  \n" +
                "          \"reference\":\"Patient/81ee6581-02b9-44de-b026-7401bf36643a\"\n" +
                "        },\n" +
                "        \"onsetDateTime\":\"2018-01-01\", \n" +
                "        \"evidence\":[   \n" +
                "            {  \n" +
                "               \"detail\":{   \n" +
                "                  \"reference\": \"#c76ceb3b-ff93-4d4a-ae1f-83b78ce39228\"   \n" +
                "               }  \n" +
                "            }  \n" +
                "         ]  \n" +
                "      }";

        fhirClient.update().resource(condition).withId("77d90968-1965-4574-aa34-19d7d1483d8a").execute();

        Context context = new Context(library);

		dstu3RetrieveProvider.setTerminologyProvider(new FhirTerminologyProvider().setEndpoint("http://measure.eval.kanvix.com/cqf-ruler/baseDstu3", false));
        //dstu3Provider.setTerminologyProvider(new FhirTerminologyProvider().setEndpoint("http://measure.eval.kanvix.com/cqf-ruler/baseDstu3", false));
        context.registerDataProvider("http://hl7.org/fhir", dstu3Provider);
        context.enterContext("Patient");
        context.setContextValue("Patient", "81ee6581-02b9-44de-b026-7401bf36643a");

        Object result = context.resolveExpressionRef("GetProvenance").getExpression().evaluate(context);
        Assert.assertTrue(result instanceof List && ((List) result).size() == 1);
    }
}
