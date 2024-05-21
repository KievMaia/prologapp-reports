package br.com.zalf.prologappreports;

import org.apache.jorphan.collections.ListedHashTree;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;

@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class PrologAppReportsApplicationTests {
    final JmeterTestPlan testPlanClient = new JmeterTestPlan();

    @Test
    @Order(1)
    public void shouldReturnSummaryOfResultsWhenTestTenThreadsAndFiveRequests() {
        final var httpRequests = new ArrayList<HttpRequestCreateObject>();
        httpRequests.add(HttpRequestCreateObject.builder()
                                 .withDomainName("localhost")
                                 .withPath("/reports")
                                 .withHttpMethod("GET")
                                 .withPort(8081)
                                 .withProtocol("http")
                                 .build());
        httpRequests.add(HttpRequestCreateObject.builder()
                                 .withDomainName("localhost")
                                 .withPath(
                                         "/prolog/v2/checklist-offline/offline-support?codUnidade=215" +
                                                 "&forcarAtualizacao=true")
                                 .withHttpMethod("GET")
                                 .withPort(8080)
                                 .withProtocol("http")
                                 .withHeaderName("Authorization")
                                 .withHeaderValue("Bearer 2jadvp5jd3gsvios90bmbi45g6")
                                 .build());
        ListedHashTree createdTree =
                testPlanClient.createTestPlan("Compare Pdf report and Checklist offline",
                                              5,
                                              1,
                                              httpRequests,
                                              "src/main/resources/results/results-compare.csv");

        testPlanClient.engineRunner(createdTree);
    }

    @Test
    @Order(2)
    public void generateHTMLDashboard() {
        testPlanClient.htmlReportGenerator();
    }
}