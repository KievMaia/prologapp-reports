package br.com.zalf.prologappreports;

import org.apache.jmeter.config.Arguments;
import org.apache.jmeter.config.gui.ArgumentsPanel;
import org.apache.jmeter.control.LoopController;
import org.apache.jmeter.control.gui.TestPlanGui;
import org.apache.jmeter.engine.StandardJMeterEngine;
import org.apache.jmeter.gui.action.HtmlReportGenerator;
import org.apache.jmeter.protocol.http.control.Header;
import org.apache.jmeter.protocol.http.control.HeaderManager;
import org.apache.jmeter.protocol.http.control.gui.HttpTestSampleGui;
import org.apache.jmeter.protocol.http.sampler.HTTPSampler;
import org.apache.jmeter.protocol.http.sampler.HTTPSamplerProxy;
import org.apache.jmeter.reporters.ResultCollector;
import org.apache.jmeter.reporters.Summariser;
import org.apache.jmeter.testelement.TestElement;
import org.apache.jmeter.testelement.TestPlan;
import org.apache.jmeter.threads.SetupThreadGroup;
import org.apache.jmeter.threads.gui.ThreadGroupGui;
import org.apache.jmeter.util.JMeterUtils;
import org.apache.jorphan.collections.HashTree;
import org.apache.jorphan.collections.ListedHashTree;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class JmeterTestPlan {
    /**
     * Used to create Jmeter test plan.
     */
    public ListedHashTree createTestPlan(@NotNull final String testPlanName,
                                         final int threadCount,
                                         final int numberOfRequestsPerThread,
                                         @NotNull final List<HttpRequestCreateObject> httpRequestCreateObject,
                                         @NotNull final String outPutCsvFile) {
        //import the jmeter properties, as is provided
        JMeterUtils.loadJMeterProperties("src/main/resources/jmeter.properties");
        //Set locale
        JMeterUtils.setLocale(new Locale("pt", "BR"));

        //Will be used to compose the testPlan, acts as container
        final var hashTree = new ListedHashTree();

        //Create the tesPlan item
        final var testPlan = new TestPlan(testPlanName);
        //Adding GUI pieces for Jmeter gui
        testPlan.setProperty(TestElement.TEST_CLASS, TestPlan.class.getName());
        testPlan.setProperty(TestElement.GUI_CLASS, TestPlanGui.class.getName());
        testPlan.setUserDefinedVariables((Arguments) new ArgumentsPanel().createTestElement());

        hashTree.add(testPlan);

        //LoopController, handles iteration settings
        final var setupThreadGroup = this.getSetupThreadGroup(threadCount, numberOfRequestsPerThread);

        final var groupTree = hashTree.add(testPlan, setupThreadGroup);

        //HTTPSampler acts as the container for the HTTP request to the site.
        final var httpRequests = new ArrayList<HTTPSampler>();
        httpRequestCreateObject.forEach(request -> httpRequests.add(this.getHttpSampler(request.getDomainName(),
                                                                                        request.getPath(),
                                                                                        request.getHttpMethod(),
                                                                                        request.getPort(),
                                                                                        request.getProtocol(),
                                                                                        request.getHeaderName(),
                                                                                        request.getHeaderValue())));
        httpRequests.forEach(groupTree::add);

        //Added summarizer for logging meta info
        final var summariser = new Summariser("summaryOfResults");

        //Collect results
        final var resultCollector = new ResultCollector(summariser);
        resultCollector.setFilename(outPutCsvFile);

        hashTree.add(hashTree.getArray()[0], resultCollector);

        return hashTree;
    }

    @NotNull
    private SetupThreadGroup getSetupThreadGroup(final int threadCount,
                                                 final int numberOfRequestsPerThread) {
        final var loopController = new LoopController();
        loopController.setLoops(numberOfRequestsPerThread);
        loopController.setFirst(false);
        loopController.initialize();

        //Thread groups/user count
        final var setupThreadGroup = new SetupThreadGroup();
        setupThreadGroup.setName("Users Groups");
        setupThreadGroup.setNumThreads(threadCount);
        setupThreadGroup.setRampUp(1);
        setupThreadGroup.setSamplerController(loopController);
        //Adding GUI pieces for Jmeter
        setupThreadGroup.setProperty(TestElement.TEST_CLASS, ThreadGroup.class.getName());
        setupThreadGroup.setProperty(TestElement.GUI_CLASS, ThreadGroupGui.class.getName());
        return setupThreadGroup;
    }

    @NotNull
    private HTTPSampler getHttpSampler(@NotNull final String domainName,
                                       @NotNull final String path,
                                       @NotNull final String httpMethod,
                                       final int port,
                                       @NotNull final String protocol,
                                       @Nullable final String headerName,
                                       @Nullable final String headerValue) {
        HTTPSampler httpHandler = new HTTPSampler();
        httpHandler.setDomain(domainName);
        httpHandler.setPort(port);
        httpHandler.setProtocol(protocol);
        httpHandler.setPath(path);
        httpHandler.setMethod(httpMethod);
        httpHandler.setName("Jasper Reports PDF");

        // Create a new HTTP Header object
        Header header = new Header();
        header.setName(headerName); // Replace with your desired header name
        header.setValue(headerValue); // Replace with your actual token

        // Add the header to the sampler
        HeaderManager headers = new HeaderManager();
        headers.add(header);
        httpHandler.setHeaderManager(headers);

        //Adding pieces to enable this to be exported to a .jmx and loaded into Jmeter
        httpHandler.setProperty(TestElement.TEST_CLASS, HTTPSamplerProxy.class.getName());
        httpHandler.setProperty(TestElement.GUI_CLASS, HttpTestSampleGui.class.getName());
        return httpHandler;
    }

    public void htmlReportGenerator() {
        final var jmeterHomeDir = System.getenv("JMETER_HOME");
        JMeterUtils.setJMeterHome(jmeterHomeDir);
        final var htmlReportGenerator = new HtmlReportGenerator("C:/dev/projects/prologapp-reports/src/main/resources/results/results-compare.csv",
                                                                "src/main/resources/user.properties",
                                                                null);
        htmlReportGenerator.run();
    }

    public void engineRunner(@NotNull final HashTree hashTree) {
        //Create the Jmeter engine to be used (Similar to Android's GUI engine)
        final var jEngine = new StandardJMeterEngine();
        jEngine.configure(hashTree);
        jEngine.run();
    }
}