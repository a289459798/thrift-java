thrift-java
===========

thrift java端service和client的简单封装

##一、安装thrift
待更新

##二、编写thrift脚本

create file `User.thrift`

	namespace java ThriftService.User
	status User {
		1:i32 id;
		2:string name;
		3:string sex;
		4:i32 age;
	}
	
	service UserService {
		User getUser(1:i32 UserId),
		bool addUser(User user),
		i32 getUserCount(),
		list<User> getUsers()
	}

> 
   * bool：布尔值，true 或 false，对应 Java 的 boolean
   * i32：32 位有符号整数，对应 Java 的 int
   * string：utf-8编码的字符串，对应 Java 的 String
   * struct：定义公共的对象，类似于 C 语言中的结构体定义，在 Java 中是一个 JavaBean
   * list：对应 Java 的 ArrayList
   * service：对应服务的类


##三、生成java脚本

	thrift -r -gen java User.thrift
	
##四、创建Maven项目并配置

####1.安装maven

`eclipes` -> `help` -> `marketplace` -> `search maven` -> 安装

###2.创建Maven项目

`eclipes` -> `file` -> `new` -> `project` -> `maven project` -> `一直next` -> `配置相应groupid package即可`

###3.修改pom.xml

	<dependency>
		<groupId>org.apache.thrift</groupId>
		<artifactId>libthrift</artifactId>
		<version>0.9.1</version>
	</dependency>

	<dependency>
		<groupId>org.slf4j</groupId>
		<artifactId>slf4j-log4j12</artifactId>
		<version>1.5.8</version>
	</dependency>

> 在`dependencies`中添加，引用thrift与slf4j包

##五、编写thrift server

###1.首先将生成的thrift java脚本copy到项目中
###2.编写实现服务的接口impl
	
	package lmpl;

	import java.util.ArrayList;
	import java.util.List;

	import org.apache.thrift.TException;

	import ThriftService.User.*;

	public class UserLmpl implements UserService.Iface{

	private ArrayList<User> mUser = new ArrayList<User>();
	public User getUser(int userId) throws TException {
		// TODO Auto-generated method stub
		if(mUser != null) {
			for(int i = 0; i < mUser.size(); i++) {
				if(mUser.get(i).getId() == userId) {
					return mUser.get(i);
				}
			}
		}
		return null;
	}

	public boolean addUser(User user) throws TException {
		// TODO Auto-generated method stub
		System.out.println("111");
		return mUser.add(user);
	}

	public int getUserCount() throws TException {
		return mUser.size();
	}

	public List<User> getUsers() throws TException {
		// TODO Auto-generated method stub
		return mUser;
	}

}

###3.创建TProcessor

	UserService.processor processor = new UserService.Processor<UserService.Iface>(
						new UserLmpl();

###4.创建TServerTransport

	int port = 8090;
	TServerTransport transport = new TServerSocket(port);
	TServer.Args args = new TServer.Args(port);
	
> * 阻塞试：TServerSocket 对应 TThreadPoolServer.Args
> * 非阻塞：TNonblockinfServerSocket 对应 TNonblockingServer.Args
> * 半同步半异步：TNonblockinfServerSocket 对应 THsHaServer.Args

> 注意：使用非阻塞和半异步方式都需要使用 `args.transportFactory(new TFramedTransport.Factory())`
###5.创建tprotocol

	args.protocolFactory(new TBinaryProtocol.Factory());
	
> * 二进制传输 `TBinaryProtocol`
> * json传输	`TJsonProtocol`
> * 压缩传输 `TCompactProtocol`

###5.创建TServer

	TServer server = new TSimpleServer(args);
	
> * 最简单方式，一般用于测试 `TSimpleServer`
> * 阻塞试 `TThreadPoolServer`
> * 非阻塞 `TNonblockingServer`
> * 半异步 `THsHaServer`

###6.启动server

	server.server();
	

##六、编写 thrift client

###1.创建Transport

	String host = 'localhost';
	int port = 8090;
	TTransport transport = new TSocket(host, port);

> 需要与服务端使用的统一

###2.创建Protocol

	TProtocol protocol = new TBinaryProtocol(transport);
	
> 需要与服务端保持一致

###3.创建client

	UserService.Client client = new UserService.Client(protocol);

###4.调用方法

	client.getUserCount();
	client.getUser(userid);
	client.addUser(user);
	client.getUsers();