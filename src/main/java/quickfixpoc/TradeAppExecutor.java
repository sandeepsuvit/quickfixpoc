package quickfixpoc;

import java.util.HashMap;
import java.util.Map;

import quickfix.Application;
import quickfix.DoNotSend;
import quickfix.FieldNotFound;
import quickfix.IncorrectDataFormat;
import quickfix.IncorrectTagValue;
import quickfix.Message;
import quickfix.MessageCracker;
import quickfix.RejectLogon;
import quickfix.Session;
import quickfix.SessionID;
import quickfix.SessionNotFound;
import quickfix.UnsupportedMessageType;
import quickfix.field.AvgPx;
import quickfix.field.CumQty;
import quickfix.field.ExecID;
import quickfix.field.ExecTransType;
import quickfix.field.ExecType;
import quickfix.field.LeavesQty;
import quickfix.field.OrdStatus;
import quickfix.field.OrdType;
import quickfix.field.OrderID;
import quickfix.field.Price;
import quickfix.field.Side;
import quickfix.field.Symbol;
import quickfix.fix42.ExecutionReport;
import quickfix.fix42.NewOrderSingle;

public class TradeAppExecutor extends MessageCracker implements Application {

	private Map<String, Double> priceMap = null;

	public TradeAppExecutor() {
		// Dummy price map
		priceMap = new HashMap<String, Double>();
		priceMap.put("BIL", 91.47);
		priceMap.put("JPST", 50.45);
		priceMap.put("SPY", 314.85);
		priceMap.put("QQQ", 204.91);
		priceMap.put("SDY", 106.88);
		priceMap.put("IYLD", 25.2);
		priceMap.put("AGG", 112.54);
		priceMap.put("SPYX", 77.13);
		priceMap.put("SUSB", 25.44);
		priceMap.put("GLD", 137.51);
	}

	public void onCreate(SessionID sessionId) {
		System.out.println("Executor Session Created with SessionID = " + sessionId);
	}

	public void onLogon(SessionID sessionId) {
		// TODO Auto-generated method stub

	}

	public void onLogout(SessionID sessionId) {
		// TODO Auto-generated method stub

	}

	public void toAdmin(Message message, SessionID sessionId) {
		// TODO Auto-generated method stub

	}

	public void fromAdmin(Message message, SessionID sessionId)
			throws FieldNotFound, IncorrectDataFormat, IncorrectTagValue, RejectLogon {
		System.out.println("Admin Message Received (Acceptor) :" + message.toString());
	}

	public void toApp(Message message, SessionID sessionId) throws DoNotSend {
		// TODO Auto-generated method stub

	}

	public void fromApp(Message message, SessionID sessionId)
			throws FieldNotFound, IncorrectDataFormat, IncorrectTagValue, UnsupportedMessageType {
		crack(message, sessionId); // calls onMessage(..,..)
	}

	public void onMessage(NewOrderSingle message, SessionID sessionID)
			throws FieldNotFound, UnsupportedMessageType, IncorrectTagValue {
		System.out.println("### NewOrder Received:" + message.toString());
		System.out.println("### Symbol" + message.getSymbol().toString());
		System.out.println("### Side" + message.getSide().toString());
		System.out.println("### Type" + message.getOrdType().toString());
		System.out.println("### Quantity" + message.getOrderQty().toString());
		System.out.println("### TransactioTime" + message.getTransactTime().toString());

		respondToClient(message, sessionID);
	}

	/**
	 * Respond message back to client
	 * 
	 * @param message
	 * @param sessionID
	 * @throws FieldNotFound
	 */
	private void respondToClient(NewOrderSingle message, SessionID sessionID) throws FieldNotFound {
		OrdType orderType = message.getOrdType();
		Symbol currencyPair = message.getSymbol();
		
		System.out.println("Currency pair >>> "+ currencyPair.getValue());
		System.out.println("Price >>> "+ this.priceMap.get(currencyPair.getValue()));

		// Assuming that we are directly dealing with Market
		Price price = null;
		
		// Applicable only for market on close order type
		if (OrdType.MARKET_ON_CLOSE == orderType.getValue()) {
			if (this.priceMap.containsKey(currencyPair.getValue())) {
				price = new Price(this.priceMap.get(currencyPair.getValue()));
			} else {
				price = new Price(1.4589);
			}
		}
		
		// We are hard coding this to 1, but in real world this may be something like a
		// sequence.
		OrderID orderNumber = new OrderID("1");

		// Id of the report, a unique identifier, once again this will be something like
		// a sequence
		ExecID execId = new ExecID("1");

		// In this case this is a new order with no exception hence mark it as NEW
		ExecTransType exectutionTransactioType = new ExecTransType(ExecTransType.NEW);

		// This execution report is for confirming a new Order
		ExecType purposeOfExecutionReport = new ExecType(ExecType.FILL);

		// Represents status of the order, since this order was successful mark it as
		// filled.
		OrdStatus orderStatus = new OrdStatus(OrdStatus.FILLED);

		// Represents the currencyPair
		Symbol symbol = currencyPair;

		// Represents side
		Side side = message.getSide();

		// What is the quantity left for the day, we will take 100 as a hard coded
		// value,
		// we can also keep a note of this from say limit module.
		LeavesQty leavesQty = new LeavesQty(100);

		// Total quantity of all the trades booked in this application, here it is
		// hard coded to be 100.
		CumQty cummulativeQuantity = new CumQty(100);

		// Average Price, say make it 1.235
		AvgPx avgPx = new AvgPx(1.235);
		
		// Send the execution report message
		ExecutionReport executionReport = new ExecutionReport(orderNumber, execId, exectutionTransactioType,
				purposeOfExecutionReport, orderStatus, symbol, side, leavesQty, cummulativeQuantity, avgPx);
		executionReport.set(price);
		try {
			Session.sendToTarget(executionReport, sessionID);
		} catch (SessionNotFound e) {
			e.printStackTrace();
		}
	}
}
