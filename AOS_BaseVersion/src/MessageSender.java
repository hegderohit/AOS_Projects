import java.nio.charset.CharacterCodingException;

public class MessageSender implements Runnable {

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Runnable#run() Sending Messages to the Cohert
	 * Applications...
	 * 
	 * Message Format:
	 * "[(AppMessage),(SenderID),(ReceiverId),(MonotonicalValue MessID),(Body)]"
	 */

	@Override
	public void run() {

		try {
			while ((MainClass.sentMessageCount <= MainClass.totalMessageCount)
					&& (MainClass.applicationMessageMutex == true)) {

				MainClass.increementLogicalClock();

				for (int j = 0; j < MainClass.connectionChannel.size(); j++) {
					String message = "AppMessage," + +MainClass.nodeId + ","
							+ MainClass.cohertList.get(j) + ","
							+ MainClass.messageId + ","
							+ "HEY This is a Test Message";
					MainClass.sendMessage(MainClass.connectionChannel.get(j),
							message);
					MainClass.sentMessageBuffer.add(message);
					MainClass.messageId++;
				}

				MainClass.sentMessageCount++;
			}
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		} catch (CharacterCodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}
