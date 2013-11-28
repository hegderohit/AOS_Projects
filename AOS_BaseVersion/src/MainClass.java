import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.CharacterCodingException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.sun.nio.sctp.MessageInfo;
import com.sun.nio.sctp.SctpChannel;

public class MainClass {

	/*
	 * 1. Read from the Configuration file to fill in the Node Identity 2. Read
	 * from the Topology and set its Connection Map
	 */

	// My Node Identifier
	static int nodeId;
	static String nodedomainName = null;
	static int nodeportNumber = 0;

	static int nodeCount;

	static ArrayList<String> domainNameList = new ArrayList<String>();
	static ArrayList<Integer> portNumerList = new ArrayList<Integer>();
	static ArrayList<Integer> cohertList = new ArrayList<Integer>();

	// List to maintain the connection list.
	static ArrayList<SctpChannel> connectionChannel = new ArrayList<SctpChannel>();

	// Message Count
	static int totalMessageCount = 20;
	static int sentMessageCount = 0;
	static int messageId = 0;

	// Synchronization Handler Variables SHARED RESOURSES
	static boolean applicationMessageMutex = true;

	// Clock Tick
	static int logicalClock = 0;
	static int clockTrigger = 10;

	// Data Structures to store the messages sent and received before writing it
	// into the back up file (from last check point to the current point)
	static ArrayList<String> sentMessageBuffer = new ArrayList<String>();
	static ArrayList<String> receivedMessageBuffer = new ArrayList<String>();
	
	

	public static void main(String[] args) throws NumberFormatException,
			IOException, InterruptedException {
		nodeId = Integer.parseInt(args[0]);

		// List the Coherts
		readTopologyFile();

		// Count the Total number of nodes for each process
		nodeCount = cohertList.size();

		// Read the config file and set the Lists
		readConfigurationFile();

		// Start Server Thread
		new Thread(new ServerConnections()).start();

		Thread.currentThread();
		Thread.sleep(9000 - (nodeId * 200));

		// Starts the Client Thread
		new Thread(new ClientConnections()).start();

		Thread.sleep(7000 - (nodeId * 200));

		// Thread that starts Request message
		MessageSender messagesender = new MessageSender();
		new Thread(messagesender).start();

	}

	private static void readTopologyFile() throws IOException {
		// TODO Auto-generated method stub
		String fileName = "./topology.txt";
		String currentLIne = null;

		BufferedReader br = new BufferedReader(new FileReader(fileName));
		int count = 0;
		String[] tokens;

		while ((currentLIne = br.readLine()) != null) {
			tokens = currentLIne.split(" ");
			if (Integer.parseInt(tokens[0]) == nodeId) {
				cohertList.add(Integer.parseInt(tokens[1]));
			}
		}

		br.close();

	}

	private static void readConfigurationFile() throws NumberFormatException,
			IOException {

		// TODO Auto-generated method stub
		String fileName = "./configuration.txt";
		String currentLIne = null;

		BufferedReader br = new BufferedReader(new FileReader(fileName));
		int count = 0;
		String[] tokens;

		while ((currentLIne = br.readLine()) != null) {
			tokens = currentLIne.split(" ");

			if (Integer.parseInt(tokens[0]) == nodeId) {
				nodedomainName = tokens[1];
				nodeportNumber = Integer.parseInt(tokens[2]);
			}

			if (cohertList.contains(Integer.parseInt(tokens[0]))) {
				domainNameList.add(tokens[1]);
				portNumerList.add(Integer.parseInt(tokens[2]));
			}

		}

		for (int i = 0; i < portNumerList.size(); i++) {
			System.out.println(portNumerList.get(i) + ""
					+ domainNameList.get(i));
		}

		System.out.println("MY Host name :" + nodedomainName);
		System.out.println("MY Port num" + nodeportNumber);
		System.out.println("Total Nodes" + nodeCount);
		br.close();
	}

	public static synchronized int getLogicalClock() {
		return logicalClock;
	}

	public static synchronized void increementLogicalClock() {
		// TODO Auto-generated method stub
		logicalClock++;
	}

	// Synchronized block to send the Message via channels
	public synchronized static void sendMessage(SctpChannel clientSock,
			String Message) throws CharacterCodingException {

		ByteBuffer sendBuffer = ByteBuffer.allocate(512);
		sendBuffer.clear();
		sendBuffer.put(Message.getBytes());
		sendBuffer.flip();

		try {
			// Send a message in the channel
			MessageInfo messageInfo = MessageInfo.createOutgoing(null, 0);
			clientSock.send(sendBuffer, messageInfo);
		} catch (IOException ex) {
			Logger.getLogger(MainClass.class.getName()).log(Level.SEVERE, null,
					ex);
		}

	}

}
