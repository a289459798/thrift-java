package example.simple;

import lib.ThriftForJava.ThriftService;
import lmpl.UserLmpl;

import org.apache.thrift.transport.TTransportException;

import ThriftService.User.UserService;

public class UserServ {
	public static void main(String args[]) {
		UserServ server = new UserServ();
		ThriftService thriftService = ThriftService.newInstance();
		thriftService
				.setProcessor(new UserService.Processor<UserService.Iface>(
						new UserLmpl()));
		try {
			thriftService.setTransport();
			thriftService.setBinaryProtocol();
			thriftService.start();
		} catch (TTransportException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
