package br.com.zalf.prologappreports;

import org.apache.jorphan.collections.ListedHashTree;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class PrologAppReportsApplicationTests {

    @Test
    public void shouldReturnSummaryOfResultsWhenTestTenThreadsAndFiveRequests() {
        final var testPlanClient = new JmeterTestPlan();
        ListedHashTree createdTree = testPlanClient.createTestPLan("Jasper Reports PDF",
                                                                   "localhost",
                                                                   "/reports",
                                                                   "GET",
                                                                   10,
                                                                   5,
                                                                   "src/main/resources/results/results-10users" +
                                                                           "-5requests.csv");

        testPlanClient.engineRunner(createdTree);
    }

    @Test
    public void shouldReturnSummaryOfResultsWhenTestOneThousandNinetySixThreadsAndOneRequestEach() {
        final var testPlanClient = new JmeterTestPlan();
        ListedHashTree createdTree = testPlanClient.createTestPLan("Jasper Reports PDF",
                                                                   "localhost",
                                                                   "/reports",
                                                                   "GET",
                                                                   1096,
                                                                   1,
                                                                   "src/main/resources/results/results-1096users" +
                                                                           "-5requests.csv");

        testPlanClient.engineRunner(createdTree);
    }
}