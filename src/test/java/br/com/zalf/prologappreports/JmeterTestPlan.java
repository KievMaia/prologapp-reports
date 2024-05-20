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

public class JmeterTestPlan {
    /**
     * Used to create Jmeter test plan, also saves testplan as a .jmx file
     * in resource folder
     */
    public ListedHashTree createTestPLan(String domainName,
                                         String path,
                                         String httpMethod,
                                         int threadCount) {
        JMeterUtils.setJMeterHome("target/jmeter");

        //import the jmeter properties, as is provided
        JMeterUtils.loadJMeterProperties("src/main/resources/jmeter.properties");
        //Set locale
        JMeterUtils.initLocale();

        //Will be used to compose the testPlan, acts as container
        ListedHashTree hashTree = new ListedHashTree();

        //HTTPSampler acts as the container for the HTTP request to the site.
        HTTPSampler httpHandler = new HTTPSampler();
        httpHandler.setDomain(domainName);
        httpHandler.setPort(8080);
        httpHandler.setProtocol("http");
        httpHandler.setPath(path);
        httpHandler.setMethod(httpMethod);
        httpHandler.setName("Jasper Reports PDF");

        //Adding pieces to enable this to be exported to a .jmx and loaded
        //into Jmeter
        httpHandler.setProperty(TestElement.TEST_CLASS, HTTPSamplerProxy.class.getName());
        httpHandler.setProperty(TestElement.GUI_CLASS, HttpTestSampleGui.class.getName());

        //LoopController, handles iteration settings
        LoopController loopController = new LoopController();
        loopController.setLoops(5);
        loopController.setFirst(true);
        loopController.initialize();

        //Thread groups/user count
        SetupThreadGroup setupThreadGroup = new SetupThreadGroup();
        setupThreadGroup.setName("Users Groups");
        setupThreadGroup.setNumThreads(threadCount);
        setupThreadGroup.setRampUp(1);
        setupThreadGroup.setSamplerController(loopController);

        //Adding GUI pieces for Jmeter
        setupThreadGroup.setProperty(TestElement.TEST_CLASS, ThreadGroup.class.getName());
        setupThreadGroup.setProperty(TestElement.GUI_CLASS, ThreadGroupGui.class.getName());

        //Create the tesPlan item
        TestPlan testPlan = new TestPlan("Jasper Reports PDF");
        //Adding GUI pieces for Jmeter gui
        testPlan.setProperty(TestElement.TEST_CLASS, TestPlan.class.getName());
        testPlan.setProperty(TestElement.GUI_CLASS, TestPlanGui.class.getName());
        testPlan.setUserDefinedVariables((Arguments) new ArgumentsPanel().createTestElement());

        hashTree.add(testPlan);

        HashTree groupTree = hashTree.add(testPlan, setupThreadGroup);
        groupTree.add(httpHandler);

        //Added summarizer for logging meta info
        Summariser summariser = new Summariser("summaryOfResults");

        //Collect results

        ResultCollector resultCollector = new ResultCollector(summariser);

        resultCollector.setFilename("src/main/resources/Results.csv");

        hashTree.add(hashTree.getArray()[0], resultCollector);

        return hashTree;
    }

    public void engineRunner(HashTree hashTree) {
        //Create the Jmeter engine to be used (Similar to Android's GUI engine)
        StandardJMeterEngine jEngine = new StandardJMeterEngine();

        jEngine.configure(hashTree);

        jEngine.run();
    }
}