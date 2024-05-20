package br.com.zalf.prologappreports;

import org.apache.jmeter.config.Arguments;
import org.apache.jmeter.config.gui.ArgumentsPanel;
import org.apache.jmeter.control.LoopController;
import org.apache.jmeter.control.gui.TestPlanGui;
import org.apache.jmeter.engine.StandardJMeterEngine;
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

import java.util.Locale;

public class JmeterTestPlan {
    /**
     * Used to create Jmeter test plan.
     */
    public ListedHashTree createTestPLan(@NotNull final String testPlanName,
                                         @NotNull final String domainName,
                                         @NotNull final String path,
                                         @NotNull final String httpMethod,
                                         final int threadCount,
                                         final int numberOfRequestsPerThread,
                                         @NotNull final String outPutCsvFile) {
        //import the jmeter properties, as is provided
        JMeterUtils.loadJMeterProperties("src/main/resources/jmeter.properties");
        //Set locale
        JMeterUtils.setLocale(new Locale("pt", "BR"));

        //Will be used to compose the testPlan, acts as container
        final var hashTree = new ListedHashTree();

        //HTTPSampler acts as the container for the HTTP request to the site.
        final var httpHandler = this.getHttpSampler(domainName, path, httpMethod);

        //LoopController, handles iteration settings
        final var setupThreadGroup = this.getSetupThreadGroup(threadCount, numberOfRequestsPerThread);

        //Create the tesPlan item
        final var testPlan = new TestPlan(testPlanName);
        //Adding GUI pieces for Jmeter gui
        testPlan.setProperty(TestElement.TEST_CLASS, TestPlan.class.getName());
        testPlan.setProperty(TestElement.GUI_CLASS, TestPlanGui.class.getName());
        testPlan.setUserDefinedVariables((Arguments) new ArgumentsPanel().createTestElement());

        hashTree.add(testPlan);

        HashTree groupTree = hashTree.add(testPlan, setupThreadGroup);
        groupTree.add(httpHandler);

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
                                       @NotNull final String httpMethod) {
        HTTPSampler httpHandler = new HTTPSampler();
        httpHandler.setDomain(domainName);
        httpHandler.setPort(8080);
        httpHandler.setProtocol("http");
        httpHandler.setPath(path);
        httpHandler.setMethod(httpMethod);
        httpHandler.setName("Jasper Reports PDF");
        //Adding pieces to enable this to be exported to a .jmx and loaded into Jmeter
        httpHandler.setProperty(TestElement.TEST_CLASS, HTTPSamplerProxy.class.getName());
        httpHandler.setProperty(TestElement.GUI_CLASS, HttpTestSampleGui.class.getName());
        return httpHandler;
    }

    public void engineRunner(@NotNull final HashTree hashTree) {
        //Create the Jmeter engine to be used (Similar to Android's GUI engine)
        final var jEngine = new StandardJMeterEngine();
        jEngine.configure(hashTree);
        jEngine.run();
    }
}