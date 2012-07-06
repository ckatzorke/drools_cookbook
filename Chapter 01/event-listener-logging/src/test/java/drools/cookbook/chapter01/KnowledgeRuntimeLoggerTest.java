package drools.cookbook.chapter01;

import static org.junit.Assert.assertNotNull;
import junit.framework.Assert;

import org.drools.KnowledgeBase;
import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderError;
import org.drools.builder.KnowledgeBuilderFactory;
import org.drools.builder.ResourceType;
import org.drools.definition.type.FactType;
import org.drools.io.impl.ClassPathResource;
import org.drools.logger.KnowledgeRuntimeLogger;
import org.drools.logger.KnowledgeRuntimeLoggerFactory;
import org.drools.runtime.StatefulKnowledgeSession;
import org.junit.Test;

/**
 * 
 * @author Lucas Amador
 * 
 */
public class KnowledgeRuntimeLoggerTest {

	@Test
	public void simpleTest() {

		KnowledgeBuilder kbuilder = KnowledgeBuilderFactory
				.newKnowledgeBuilder();
		kbuilder.add(new ClassPathResource("rules.drl", getClass()),
				ResourceType.DRL);

		if (kbuilder.hasErrors()) {
			if (kbuilder.getErrors().size() > 0) {
				for (KnowledgeBuilderError kerror : kbuilder.getErrors()) {
					System.err.println(kerror);
				}
			}
		}

		KnowledgeBase kbase = kbuilder.newKnowledgeBase();
		StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();

		KnowledgeRuntimeLogger logger = KnowledgeRuntimeLoggerFactory
				.newFileLogger(ksession, "target/runtimelog.xml");
		
		//perform stuff
		FactType serverType = kbase.getFactType("drools.cookbook.chapter01",
				"Server");

		assertNotNull(serverType);

		Object windowsServer = null;
		try {
			windowsServer = serverType.newInstance();
		} catch (InstantiationException e) {
			System.err
					.println("the class Server on drools.cookbook.chapter01 package hasn't a constructor");
		} catch (IllegalAccessException e) {
			System.err
					.println("unable to access the class Server on drools.cookbook.chapter01 package");
		}
		serverType.set(windowsServer, "name", "server001");
		serverType.set(windowsServer, "processors", 1);
		serverType.set(windowsServer, "memory", 2048); // 2 gigabytes
		serverType.set(windowsServer, "diskSpace", 2048); // 2 terabytes
		serverType.set(windowsServer, "cpuUsage", 3);

		ksession.insert(windowsServer);

		ksession.fireAllRules();

		Assert.assertEquals(0, ksession.getObjects().size());
		
		//close
		logger.close();

	}

}
