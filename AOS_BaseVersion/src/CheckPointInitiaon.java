import java.io.*;

import javax.xml.parsers.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.*;
import javax.xml.transform.stream.*;

import org.w3c.dom.*;

public class CheckPointInitiaon implements Runnable {

	public CheckPointInitiaon() {

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Runnable#run()
	 * 
	 * Trigger The CheckPoint for every 10th tick of clock Freeze the sending of
	 * messages
	 * 
	 * Phase1: Take a Tentative Check Point Send a Check point request to all
	 * the cohert processes
	 */
	@Override
	public void run() {
		// TODO Auto-generated method stub

		while (MainClass.sentCheckPointMessage < MainClass.totalCheckPointMessage) {
			if ((MainClass.getLogicalClock()) % 10 == 0) {
				MainClass.applicationMessageMutex = false;
				try {

					tentativeCheckPoint();

				} catch (ParserConfigurationException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException | TransformerException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

	private void tentativeCheckPoint() throws ParserConfigurationException,
			IOException, TransformerException {
		// TODO Auto-generated method stub
		// TODO Auto-generated method stub

		DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory
				.newInstance();

		DocumentBuilder documentBuilder = documentBuilderFactory
				.newDocumentBuilder();
		Document document = documentBuilder.newDocument();

		String root = "Messages";
		Element rootElement = document.createElement(root);
		document.appendChild(rootElement);
		for (int i = 0; i < MainClass.sentMessageBuffer.size(); i++) {

			String message;
			String type, senderId, receiverId, messageId, body;

			message = MainClass.sentMessageBuffer.get(i);
			String[] tokens = message.split(",");
			// (AppMessage),(SenderID),(ReceiverId),(MonotonicalValue
			// MessID),(Body)
			type = tokens[0];
			senderId = tokens[1];
			receiverId = tokens[2];
			messageId = tokens[3];
			body = tokens[4];

			String element = "Message";
			Element em = document.createElement(element);
			em.appendChild(document.createTextNode(type));
			rootElement.appendChild(em);

			Attr attr = document.createAttribute("SenderId");
			attr.setValue(senderId);
			em.setAttributeNode(attr);

			Attr attr2 = document.createAttribute("ReceiverId");
			attr2.setValue(receiverId);
			em.setAttributeNode(attr2);

			Attr attr3 = document.createAttribute("MessageId");
			attr3.setValue(messageId);
			em.setAttributeNode(attr3);

			Attr attr1 = document.createAttribute("MessageBody");
			attr1.setValue(body);
			em.setAttributeNode(attr1);

		}

		File file = new File("./SentMessageTentativeBackup.xml");
		if (!file.exists()) {
			file.createNewFile();
		}

		FileWriter fw = new FileWriter(file.getAbsoluteFile());
		BufferedWriter bw = new BufferedWriter(fw);

		TransformerFactory transformerFactory = TransformerFactory
				.newInstance();
		Transformer transformer = transformerFactory.newTransformer();
		DOMSource source = new DOMSource(document);
		StreamResult result = new StreamResult(bw);
		transformer.transform(source, result);

	}
}
