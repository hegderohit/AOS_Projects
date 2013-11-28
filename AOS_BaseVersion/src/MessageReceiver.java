import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.CharacterCodingException;

import com.sun.nio.sctp.MessageInfo;
import com.sun.nio.sctp.SctpChannel;

public class MessageReceiver implements Runnable {
	SctpChannel cliSctpChannel;

	public MessageReceiver(SctpChannel channel) {
		// TODO Auto-generated constructor stub
		cliSctpChannel = channel;
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		ByteBuffer byteBuffer = null;
		String message = null;

		while (true) {
			MessageInfo messageInfo = null;

			byteBuffer = ByteBuffer.allocate(512);
			byteBuffer.clear();
			try {
				messageInfo = cliSctpChannel.receive(byteBuffer, null, null);
				if (messageInfo != null) {
					MainClass.increementLogicalClock();
					/*
					 * System.out.println("CLOCK VALUE ............ " +
					 * MainClass.getLamportClock() + "\n");
					 */
					message = byteToString(byteBuffer);
					// System.out.println("Received Message from Server:");
					// System.out.println(message);

					// System.out.println("received mess before processing ..........");
					// System.out.println(message);

					synchronized (this) {
						/*
						 * Message messageClass = new Message(message);
						 * MainClass.processMessage(messageClass);
						 */
						MainClass.receivedMessageBuffer.add(message);
					}

				}

			} catch (CharacterCodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

	private static String byteToString(ByteBuffer byteBuffer) {
		byteBuffer.position(0);
		byteBuffer.limit(512);
		byte[] bufArr = new byte[byteBuffer.remaining()];
		byteBuffer.get(bufArr);
		return new String(bufArr);
	}

}
