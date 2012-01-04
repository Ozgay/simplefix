package simplefix.examples;

import simplefix.Application;
import simplefix.Engine;
import simplefix.EngineFactory;
import simplefix.Message;
import simplefix.MsgType;
import simplefix.Session;
import simplefix.Tag;

public class Executor {

    private static EngineFactory _engineFact;

    public static void main(final String args[]) throws Exception {
        try {
            String engineName = args[0];
            String initParas = args[1];

            Class<?> classobj = Class.forName(engineName);
            Object engineobj = classobj.newInstance();

            if (engineobj instanceof EngineFactory) {

                _engineFact = (EngineFactory) engineobj;
                Engine engine = _engineFact.getEngine();
                engine.initEngine(initParas);

                Application application = new _Application();

                engine.startInProcess(application);

                System.out.println("press <enter> to quit");
                System.in.read();

                engine.stop();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static class _Application implements Application {

        public _Application() {
            // TODO Auto-generated constructor stub
        }

        public void onLogon(final Session sessionId) {
            // TODO Auto-generated method stub

        }

        public void onLogout(final Session sessionId) {
            // TODO Auto-generated method stub

        }

        public void onAppMessage(final Message message, final Session sessionId) {
            if (MsgType.ORDER_SINGLE.equals(message.getMsgType())) {
                Message replyMsg = _engineFact.createMessage(MsgType.EXECUTION_REPORT);

                replyMsg.setValue(Tag.OrderID, genOrderID());
                replyMsg.setValue(Tag.ExecID, genExecID());

                replyMsg.setValue(Tag.ExecTransType, "0");
                replyMsg.setValue(Tag.ExecType, "2");
                replyMsg.setValue(Tag.OrdStatus, "2");

                replyMsg.setValue(Tag.ClOrdID, message.getValue(Tag.ClOrdID));
                replyMsg.setValue(Tag.Symbol, message.getValue(Tag.Symbol));
                replyMsg.setValue(Tag.Side, message.getValue(Tag.Side));
                replyMsg.setValue(Tag.OrderQty, message.getValue(Tag.OrderQty));
                replyMsg.setValue(Tag.Price, message.getValue(Tag.Price));

                replyMsg.setValue(Tag.LeavesQty, "0");
                replyMsg.setValue(Tag.CumQty, message.getValue(Tag.OrderQty));
                replyMsg.setValue(Tag.AvgPx, message.getValue(Tag.Price));
                replyMsg.setValue(Tag.LastPx, message.getValue(Tag.Price));
                replyMsg.setValue(Tag.LastQty, message.getValue(Tag.OrderQty));

                sessionId.sendAppMessage(replyMsg);

            }

        }

        public int genOrderID() {
            return ++m_orderID;
        }

        public int genExecID() {
            return ++m_execID;
        }

        private int m_orderID = 0;
        private int m_execID = 0;
    }

}