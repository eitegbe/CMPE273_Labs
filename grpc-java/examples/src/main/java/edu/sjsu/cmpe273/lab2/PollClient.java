  
package edu.sjsu.cmpe273.lab2;

import io.grpc.ChannelImpl;
import io.grpc.transport.netty.NegotiationType;
import io.grpc.transport.netty.NettyChannelBuilder;

import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;


public class PollClient	{

 private static final Logger logger = Logger.getLogger(PollClient.class.getName());
 private final ChannelImpl channel;
 private final PollServiceGrpc.PollServiceBlockingStub pollStub;
 private String moderatorId;
 private String question;
 private String startedAt;
 private String expiredAt;
 private String[] choice;

	public PollClient(String host, int port) {
		channel = NettyChannelBuilder.forAddress(host, port).negotiationType(NegotiationType.PLAINTEXT).build();
           pollStub = PollServiceGrpc.newBlockingStub(channel);
	}

  	public void shutdown() throws InterruptedException {
	channel.shutdown().awaitTerminated(5, TimeUnit.SECONDS);
	} 
	
	public void setPolls(String id, String question, String startedAt, String expiredAt, String[] choice) {
	
		if(choice == null || choice.length<2){
		new RuntimeException("Choice must have two items");
		}
	try{
		PollRequest request = PollRequest.newBuilder().setModeratorId(id)
					.setQuestion(question)
					.setStartedAt(startedAt)
					.setExpiredAt(expiredAt)
					.addChoice(choice[0])
                                        .addChoice(choice[1])
                                        .build();
		PollResponse response = pollStub.createPoll(request);
		logger.info("Created a new poll with id = " + response.getId());
	}
		catch(RuntimeException e) {
		logger.log(Level.WARNING, "RPC failed", e);
		}
	}
	public static void main(String[] args) throws Exception {
		PollClient client = new PollClient("localhost", 50051);
		try {
			String moderator = "1";
			String question = "What type of smart phone do you have?";
			String startedAt = "2015-03-18T13:00:00.000Z";
			String expiredAt = "2015-03-19T13:00:00:000Z";
			String[] choice = new String[]{"Android", "Iphone"}; 
              
			client.setPolls(moderator, question, startedAt, expiredAt, choice);
	}         
	finally {
		client.shutdown();
	}
 }

}
