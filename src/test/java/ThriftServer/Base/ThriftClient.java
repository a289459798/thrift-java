package ThriftServer.Base;

import java.io.IOException;
import java.util.Hashtable;

import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TCompactProtocol;
import org.apache.thrift.protocol.TJSONProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.protocol.TProtocolFactory;
import org.apache.thrift.transport.TFramedTransport;
import org.apache.thrift.transport.TNonblockingSocket;
import org.apache.thrift.transport.TNonblockingTransport;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;
import org.apache.thrift.transport.TTransportException;

/**
 * 客户端基类
 * 
 * @author zhangzy
 * 
 */
public class ThriftClient {
	private static final int SERVER_PORT = 8090;
	private static final String SERVER_HOST = "localhost";
	private static final int TIMEOUT = 3000;
	private static Hashtable<String, ThriftClient> instances = new Hashtable<String, ThriftClient>();
	private int serverPort;
	private String serverHost;
	private int timeout;
	private TTransport serverTransport; // 服务端的服务模型
	private TProtocolFactory serverProtocolFactory;	//服务端的传输工厂

	/**
	 * 单例模式，host+port 是一个单例
	 * 
	 * @return
	 */
	public static ThriftClient newInstance() {
		return newInstance(SERVER_HOST, SERVER_PORT, TIMEOUT);
	}

	public static ThriftClient newInstance(String host, int port) {
		return newInstance(host, port, TIMEOUT);
	}

	public static ThriftClient newInstance(String host, int port, int timeout) {
		if (instances.get(host + ":" + port) == null) {
			instances.put(host + ":" + port, new ThriftClient(host, port,
					timeout));
		}
		return instances.get(host + ":" + port);
	}

	private ThriftClient(String host, int port, int timeout) {
		this.serverHost = host;
		this.serverPort = port;
		this.timeout = timeout;
	}

	/**
	 * 设置数据传输协议
	 * 
	 * @param protocol
	 */
	public ThriftClient setProtocol(String protocol) {
		if (protocol == "compact") {
			this.setTCompactProtocol();
		} else if (protocol == "json") {
			this.setJsonProtocol();
		} else {
			this.setBinaryProtocol();
		}
		return this;
	}

	/**
	 * 设置传输的服务模型
	 * 
	 * @param transport
	 */
	public ThriftClient setTransport (String transport) throws IOException {
		if (transport == "asyn") {
			this.setAsynTransport();
		} else if (transport == "nonblock") {
			this.setNonblockingTransport();
		} else {
			this.setBlockingTransport();
		}
		return this;
	}

	/**
	 * 设置二进制传输
	 */
	public ThriftClient setBinaryProtocol() {
		serverProtocolFactory = new TBinaryProtocol.Factory();
		return this;
	}

	/**
	 * 设置json传输
	 */
	public ThriftClient setJsonProtocol() {
		serverProtocolFactory = new TJSONProtocol.Factory();
		return this;
	}
	
	/**
	 * 设置压缩传输
	 * @return
	 */
	public ThriftClient setTCompactProtocol() {
		serverProtocolFactory = new TCompactProtocol.Factory();
		return this;
	}

	/**
	 * 阻塞模型
	 */
	public ThriftClient setBlockingTransport() {
		serverTransport = new TSocket(serverHost, serverPort, timeout);
		return this;
	}

	/**
	 * 设置非阻塞模型
	 */
	public ThriftClient setNonblockingTransport() {
		serverTransport = new TFramedTransport(new TSocket(serverHost,
				serverPort, timeout));
		return this;
	}

	/**
	 * 设置异步模型
	 * 
	 * @return
	 * @throws IOException
	 */
	public ThriftClient setAsynTransport() throws IOException {
		serverTransport = new TNonblockingSocket(serverHost, serverPort,
				timeout);
		return this;
	}

	/**
	 * 创建客户端连接
	 * @throws TTransportException
	 */
	public void createClient() throws TTransportException {
		if (!(serverTransport instanceof TNonblockingTransport)) {
			serverTransport.open();
		}
	}

	public TTransport getTransport() {
		return this.serverTransport;
	}

	/**
	 * 获取传输协议
	 * @return
	 */
	public TProtocol getProtocol() {
		return serverProtocolFactory.getProtocol(serverTransport);
	}
	
	public TProtocolFactory getProtocolFactory() {
		return this.serverProtocolFactory;
	}
}
