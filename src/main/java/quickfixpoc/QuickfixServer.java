/**
 * 
 */
package quickfixpoc;

import java.io.FileNotFoundException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import quickfix.Application;
import quickfix.ConfigError;
import quickfix.DefaultMessageFactory;
import quickfix.FileLogFactory;
import quickfix.FileStoreFactory;
import quickfix.MessageFactory;
import quickfix.SessionSettings;
import quickfix.SocketAcceptor;

/**
 * 
 * quickfixpoc | quickfixpoc
 * -------------------------------------------------------------------------
 * 
 *
 * @author Sandeep K
 * @version 1.0
 * @since 06-Nov-2019
 */

/**
 * <Code modification record>
 * 
 * <pre>
 * No.	Modified by (ID) 				Date (MM DD, YYYY) 	[BUG-ID] 	Description
 * ----------------------------------------------------------------------------------
 * 1	sandy@day1tech.com				06-Nov-2019						Initial commit
 * 
 * </pre>
 */
public class QuickfixServer {
	private final static Logger log = LoggerFactory.getLogger(QuickfixServer.class);

	public static void main(String[] args) throws FileNotFoundException {
		SocketAcceptor socketAcceptor = null;
        try {
            SessionSettings executorSettings = new SessionSettings("config/acceptor.conf");
            
            Application application = new TradeAppExecutor();
            FileStoreFactory fileStoreFactory = new FileStoreFactory(
                    executorSettings);
            MessageFactory messageFactory = new DefaultMessageFactory();
            FileLogFactory fileLogFactory = new FileLogFactory(executorSettings);
            socketAcceptor = new SocketAcceptor(application, fileStoreFactory,
                    executorSettings, fileLogFactory, messageFactory);
            socketAcceptor.start();
        } catch (ConfigError e) {
            e.printStackTrace();
        }
	}
	
}
