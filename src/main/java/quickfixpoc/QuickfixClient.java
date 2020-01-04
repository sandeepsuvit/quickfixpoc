package quickfixpoc;

import java.io.IOException;
import java.util.Scanner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import quickfix.Application;
import quickfix.ConfigError;
import quickfix.DefaultMessageFactory;
import quickfix.FileLogFactory;
import quickfix.FileStoreFactory;
import quickfix.MessageFactory;
import quickfix.Session;
import quickfix.SessionID;
import quickfix.SessionNotFound;
import quickfix.SessionSettings;
import quickfix.SocketInitiator;
import quickfix.field.ClOrdID;
import quickfix.field.Currency;
import quickfix.field.HandlInst;
import quickfix.field.OrdType;
import quickfix.field.OrderQty;
import quickfix.field.Side;
import quickfix.field.Symbol;
import quickfix.field.TransactTime;
import quickfix.fix42.NewOrderSingle;

public class QuickfixClient {
	private final static Logger log = LoggerFactory.getLogger(QuickfixClient.class);

	public static void main(String[] args) {

		registerClient();
	}

	private static void registerClient() {
		SocketInitiator socketInitiator = null;
		try {
			SessionSettings initiatorSettings = new SessionSettings("config/initiator.conf");
			Application initiatorApplication = new TradeAppInitiator();
			FileStoreFactory fileStoreFactory = new FileStoreFactory(initiatorSettings);
			FileLogFactory fileLogFactory = new FileLogFactory(initiatorSettings);
			MessageFactory messageFactory = new DefaultMessageFactory();
			socketInitiator = new SocketInitiator(initiatorApplication, fileStoreFactory, initiatorSettings,
					fileLogFactory, messageFactory);
			socketInitiator.start();
			SessionID sessionId = socketInitiator.getSessions().get(0);
			Session.lookupSession(sessionId).logon();
			
			while (!Session.lookupSession(sessionId).isLoggedOn()) {
				System.out.println("Waiting for login success");
				Thread.sleep(1000);
			}
			
			System.out.println("Logged In...");

			Thread.sleep(5000);
			bookSingleOrder(sessionId);

			System.out.println("Type to quit");
			Scanner scanner = new Scanner(System.in);
			scanner.next();
			Session.lookupSession(sessionId).disconnect("Done", false);
			socketInitiator.stop();
		} catch (ConfigError e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}


	private static void bookSingleOrder(SessionID sessionID) {
		// In real world this won't be a hard coded value rather than a sequence.
		ClOrdID orderId = new ClOrdID("1");

		// to be executed on the exchange
		HandlInst instruction = new HandlInst(HandlInst.AUTOMATED_EXECUTION_ORDER_PRIVATE_NO_BROKER_INTERVENTION);
		
		// Since its FX currency pair name
		Symbol mainCurrency = new Symbol("USD");

		// Which side buy, sell
		Side side = new Side(Side.BUY);

		// Time of transaction
		TransactTime transactionTime = new TransactTime();

		// Type of our order, here we are assuming this is being executed on the
		// exchange
		OrdType orderType = new OrdType(OrdType.FOREX_MARKET);

		NewOrderSingle newOrderSingle = new NewOrderSingle(orderId, instruction, mainCurrency, side, transactionTime,
				orderType);

		// Quantity
		newOrderSingle.set(new OrderQty(100));
		try {
			Session.sendToTarget(newOrderSingle, sessionID);
		} catch (SessionNotFound e) {
			e.printStackTrace();
		}
	}
}
