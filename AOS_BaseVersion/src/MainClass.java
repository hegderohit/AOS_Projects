import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

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
}
