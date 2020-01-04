package quickfixpoc;

import quickfix.Application;
import quickfix.DoNotSend;
import quickfix.FieldNotFound;
import quickfix.IncorrectDataFormat;
import quickfix.IncorrectTagValue;
import quickfix.Message;
import quickfix.MessageCracker;
import quickfix.RejectLogon;
import quickfix.SessionID;
import quickfix.UnsupportedMessageType;
import quickfix.fix42.ExecutionReport;
import quickfix.fix42.Logon;
import quickfix.fix42.NewOrderSingle;
import quickfix.fix42.SecurityDefinition;

public class TradeAppInitiator extends MessageCracker implements Application {

	public void onCreate(SessionID sessionId) {
		System.out.println("Successfully called onCreate for sessionId : " + sessionId);
	}

	public void onLogon(SessionID sessionId) {
		System.out.println("Successfully logged on for sessionId : " + sessionId);
	}

	public void onLogout(SessionID sessionId) {
		System.out.println("Successfully logged out for sessionId : " + sessionId);
	}

	public void toAdmin(Message message, SessionID sessionId) {
		System.out.println("Inside toAdmin");
	}

	public void fromAdmin(Message message, SessionID sessionId)
			throws FieldNotFound, IncorrectDataFormat, IncorrectTagValue, RejectLogon {
		System.out.println("Successfully called fromAdmin for sessionId : " + message);
	}

	public void toApp(Message message, SessionID sessionId) throws DoNotSend {
		System.out.println("Message : " + message + " for sessionid : " + sessionId);
	}

	public void fromApp(Message message, SessionID sessionId)
			throws FieldNotFound, IncorrectDataFormat, IncorrectTagValue, UnsupportedMessageType {
		System.out.println("Successfully called fromApp for sessionId : " + message);
		crack(message, sessionId); // calls onMessage(..,..)
	}
	
    public void onMessage(NewOrderSingle message, SessionID sessionID)
            throws FieldNotFound, UnsupportedMessageType, IncorrectTagValue {
        System.out.println("Inside onMessage for New Order Single");
        super.onMessage(message, sessionID);
    }
 
    public void onMessage(SecurityDefinition message, SessionID sessionID)
            throws FieldNotFound, UnsupportedMessageType, IncorrectTagValue {
        System.out.println("Inside onMessage for SecurityDefinition");
        super.onMessage(message, sessionID);
    }
 
    public void onMessage(Logon message, SessionID sessionID)
            throws FieldNotFound, UnsupportedMessageType, IncorrectTagValue {
        System.out.println("Inside Logon Message");
        super.onMessage(message, sessionID);
    }
    
    public void onMessage(ExecutionReport message, SessionID sessionID)
            throws FieldNotFound, UnsupportedMessageType, IncorrectTagValue {
        System.out.println("Received Execution report from server");
        System.out.println("Order Id : " + message.getOrderID().getValue());
        System.out.println("Order Status : " + message.getOrdStatus().getValue());
        System.out.println("Order Price : " + message.getPrice().getValue());
    }
}