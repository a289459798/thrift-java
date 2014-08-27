package example.simple;

import lib.ThriftForJava.ThriftClient;

import org.apache.thrift.TException;
import org.apache.thrift.transport.TTransportException;

import ThriftService.User.UserService;

public class UserClient {
	public static void main(String args[]) {
		ThriftClient thriftClient = ThriftClient.newInstance();
		thriftClient.setBlockingTransport();
		thriftClient.setBinaryProtocol();
		try {
			thriftClient.createClient();
			UserService.Client client = new UserService.Client(thriftClient.getProtocol());
			try {
				System.out.println(client.getUserCount());
			} catch (TException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} catch (TTransportException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
