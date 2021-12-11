import com.jafpl.messages.Message;
import com.jafpl.steps.DataConsumer;
import com.xmlcalabash.XMLCalabash;
import com.xmlcalabash.messages.XdmNodeItemMessage;
import com.xmlcalabash.util.PipelineOutputConsumer;
import junit.framework.TestCase;
import net.sf.saxon.s9api.XdmNode;

public class SmokeTest extends TestCase {

    public void testPipeline() {
        XMLCalabash calabash = XMLCalabash.newInstance();
        Consumer output = new Consumer();
        PipelineOutputConsumer result = new PipelineOutputConsumer("result", output);
        calabash.args().pipeline("src/test/resources/pipe.xpl");
        calabash.parameter(result);
        calabash.configure();
        calabash.run();
        if (output.message instanceof XdmNodeItemMessage) {
            XdmNode node = ((XdmNodeItemMessage) output.message).item();
            assertNotNull(node); // FIXME: it would be better to check that it's got the right details
        } else {
            fail();
        }
    }

    private static class Consumer implements DataConsumer {
        Message message = null;

        @Override
        public void consume(String port, Message message) {
            this.message = message;
        }
    }

}
