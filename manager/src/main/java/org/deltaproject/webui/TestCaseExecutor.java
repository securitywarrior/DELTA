package org.deltaproject.webui;

import org.deltaproject.manager.core.AttackConductor;
import org.deltaproject.manager.core.ControllerManager;
import org.deltaproject.manager.testcase.TestSwitchCase;
import org.deltaproject.manager.utils.AgentLogger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.deltaproject.webui.TestCase.Status.*;

/**
 * A executor for queued test case.
 * Created by Changhoon on 9/8/16.
 */
public class TestCaseExecutor extends Thread {

    private AttackConductor conductor;
    private TestQueue queue = TestQueue.getInstance();
    private boolean running;
    private static final Logger log = LoggerFactory.getLogger(ControllerManager.class.getName());

    public TestCaseExecutor(AttackConductor conductor) {
        this.conductor = conductor;
        running = true;
    }

    @Override
    public void run() {
        while (running) {
            if (!queue.isEmpty()) {
                conductor.setTestSwitchCase(new TestSwitchCase(conductor.getChannelManger()));

                TestCase test = queue.getNext();
                try {
                    queue.setRunningTestCase(test);
                    conductor.executeTestCase(test);
                    queue.unsetRunningTestCase(test);
                    AgentLogger.stopAllLogger();
                } catch (InterruptedException e) {
                    test.setStatus(UNAVAILABLE);
                    log.error(e.toString());
                }
            }

            try {
                Thread.sleep(999);
            } catch (InterruptedException e) {
                running = false;
            }
        }
    }
}
