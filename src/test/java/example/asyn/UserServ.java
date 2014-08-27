package example.asyn;

import lib.ThriftForJava.ThriftService;
import lmpl.UserLmpl;

import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.server.ServerContext;
import org.apache.thrift.server.TServerEventHandler;
import org.apache.thrift.transport.TTransport;
import org.apache.thrift.transport.TTransportException;

import ThriftService.User.*;

public class UserServ implements TServerEventHandler {

	public static void main(String[] args) {
		UserServ userListen = new UserServ();
		System.out.println("service is start...");
		ThriftService thriftService = ThriftService.newInstance();
		thriftService
				.setProcessor(new UserService.Processor<UserService.Iface>(
						new UserLmpl()));
		try {
			//设置传输服务模型
			thriftService.setHsHaTransport();
			//设置数据传输协议
			thriftService.setCompactProtocol();
			//开启服务,并设置事件
			thriftService.start(userListen);
		} catch (TTransportException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	public ServerContext createContext(TProtocol arg0, TProtocol arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	public void deleteContext(ServerContext arg0, TProtocol arg1, TProtocol arg2) {
		// TODO Auto-generated method stub
	}

	public void preServe() {
		// TODO Auto-generated method stub
	}

	public void processContext(ServerContext arg0, TTransport arg1,
			TTransport arg2) {
		// TODO Auto-generated method stub
	}
}
