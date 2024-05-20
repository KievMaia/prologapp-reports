package br.com.zalf.prologappreports;

import org.apache.jorphan.collections.HashTree;
import org.apache.jorphan.collections.ListedHashTree;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;

@SpringBootTest
class PrologAppReportsApplicationTests {

    @Test
    public void contextLoads() throws IOException {
        System.out.println("hello world");

        JmeterTestPlan testPlanClient = new JmeterTestPlan();

        ListedHashTree createdTree = testPlanClient.createTestPLan(
                "localhost",
                "reports",
                "GET",
                10);

        testPlanClient.engineRunner(createdTree);
    }
}