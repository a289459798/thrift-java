package example.asyn;


import org.apache.thrift.async.AsyncMethodCallback;
import org.apache.thrift.async.TAsyncClientManager;
import org.apache.thrift.transport.TNonblockingTransport;
import org.apache.thrift.transport.TTransportException;

import ThriftServer.Base.ThriftClient;
import ThriftService.User.*;
import ThriftService.User.UserService.AsyncClient.addUser_call;

/**
 * 
 * @author zhangzy
 * 
 */
public class UserClient {

	public static final String SERVER_IP = "localhost";
	public static final int SERVER_PORT = 8090;
	public static final int TIMEOUT = 30000;

	public static <T> void main(String[] args) {
		ThriftClient thriftClient = ThriftClient.newInstance(SERVER_IP,
				SERVER_PORT, TIMEOUT);
		try {
			TAsyncClientManager clientManager = new TAsyncClientManager();
			thriftClient.setAsynTransport().setTCompactProtocol().createClient();
			UserService.AsyncClient client2 = new UserService.AsyncClient(
					thriftClient.getProtocolFactory(), clientManager,
					(TNonblockingTransport) thriftClient.getTransport());
			User user = new User();
			user.age = 10;
			user.id = 1;
			user.name = "anme";
			user.sex = "nan";
			client2.addUser(user, new AsyncMethodCallback<addUser_call>() {

				public void onComplete(addUser_call arg0) {
					// TODO Auto-generated method stub
					System.out.println(arg0);
				}

				public void onError(Exception arg0) {
					// TODO Auto-generated method stub
					
				}
				
			});
			System.out.println("aysn...");
			//延迟1秒退出，不然还未执行回掉程序就已经退出了
			while(true) {
				Thread.sleep(1000);
			}
		} catch (TTransportException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}
}
