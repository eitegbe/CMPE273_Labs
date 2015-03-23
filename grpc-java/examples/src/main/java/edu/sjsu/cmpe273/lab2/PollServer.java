
package edu.sjsu.cmpe273.lab2;

import io.grpc.ServerImpl;
import io.grpc.stub.StreamObserver;
import io.grpc.transport.netty.NettyServerBuilder;

import java.util.logging.Logger;


public class PollServer {

	private static final Logger logger = Logger.getLogger(PollServer.class.getName());

	private int port = 50051;
	private ServerImpl server;

	private void start() throws Exception {
	
		server = NettyServerBuilder.forPort(port).addService(PollServiceGrpc.bindService(new PollServiceImpl())).build().start();
		logger.info("Server started, listening on " + port);
		Runtime.getRuntime().addShutdownHook(new Thread() {
		@Override
		public void run() {
	
		System.err.println("*** shutting down gRPC server since JVM is shutting down");
		PollServer.this.stop();
		System.err.println("**** server shutdown");
		}
	       });
	}
	private void stop() {
	if(server !=null) {
	server.shutdown();
	  }
	}	
	
     public static void main(String[] args) throws Exception {

	final PollServer server = new PollServer();
	server.start();
	}

	private class PollServiceImpl implements PollServiceGrpc.PollService {
	@Override
	public void createPoll(PollRequest req, StreamObserver<PollResponse> observer) {
	PollResponse res = PollResponse.newBuilder().setId("Id: "+ req.getModeratorId()).build();
	logger.info("The moderator id is : "+req.getModeratorId());
	observer.onValue(res);
	observer.onCompleted();
	}
}
}
