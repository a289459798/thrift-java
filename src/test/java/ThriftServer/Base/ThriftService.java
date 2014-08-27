package ThriftServer.Base;

import java.util.Hashtable;

import org.apache.thrift.TBaseProcessor;
import org.apache.thrift.TProcessor;
import org.apache.thrift.TProcessorFactory;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TCompactProtocol;
import org.apache.thrift.protocol.TJSONProtocol;
import org.apache.thrift.server.AbstractNonblockingServer.AbstractNonblockingServerArgs;
import org.apache.thrift.server.THsHaServer;
import org.apache.thrift.server.TNonblockingServer;
import org.apache.thrift.server.TServer;
import org.apache.thrift.server.TServer.AbstractServerArgs;
import org.apache.thrift.server.TServerEventHandler;
import org.apache.thrift.server.TSimpleServer;
import org.apache.thrift.server.TThreadPoolServer;
import org.apache.thrift.server.TThreadPoolServer.Args;
import org.apache.thrift.transport.TFramedTransport;
import org.apache.thrift.transport.TNonblockingServerSocket;
import org.apache.thrift.transport.TNonblockingServerTransport;
import org.apache.thrift.transport.TServerSocket;
import org.apache.thrift.transport.TServerTransport;
import org.apache.thrift.transport.TTransportException;

/**
 * thrift server 基类
 * 
 * @author zhangzy
 * 
 */
public class ThriftService {
	private static final int SERVER_PORT = 8090;
	private int serverPort;
	private static Hashtable<Integer, ThriftService> instances = new Hashtable<Integer, ThriftService>();
	private TServerTransport serverTransport; // 传输的服务模型
	private AbstractServerArgs<?> serverArgs;
	private TProcessor serverTprocessor;
	private TServer server;

	/**
	 * 单例模式，每个端口一个单例
	 * 
	 * @return
	 */
	public static ThriftService newInstance() {
		return newInstance(SERVER_PORT);
	}

	public static ThriftService newInstance(int port) {
		if (instances.get(port) == null) {
			instances.put(port, new ThriftService(port));
		}
		return instances.get(port);
	}

	/**
	 * 私有构造方法，防止被实例化
	 */
	private ThriftService(int port) {
		// TODO Auto-generated constructor stub
		serverPort = port;
	}

	public void setProcessor(TBaseProcessor<?> processor) {
		serverTprocessor = processor;
	}

	/**
	 * 设置数据传输协议
	 */
	public void setProtocol(String protocol) {
		if (protocol == "json") {
			this.setJSONProtocol();
		} else if (protocol == "compact") {
			this.setCompactProtocol();
		} else {
			this.setBinaryProtocol();
		}
	}

	/**
	 * 设置二进制传输
	 */
	public void setBinaryProtocol() {
		serverArgs.protocolFactory(new TBinaryProtocol.Factory());
	}

	/**
	 * 设置json传输
	 */
	public void setJSONProtocol() {
		serverArgs.protocolFactory(new TJSONProtocol.Factory());
	}

	/**
	 * 设置压缩传输
	 */
	public void setCompactProtocol() {
		serverArgs.protocolFactory(new TCompactProtocol.Factory());
	}

	/**
	 * 设置一般的服务模型
	 */
	public void setTransport() throws TTransportException {
		serverTransport = new TServerSocket(serverPort);
		serverArgs = new TServer.Args(serverTransport);
	}

	/**
	 * 设置非阻塞的服务模型
	 */
	public void setNonblockingTransport() throws TTransportException {
		serverTransport = new TNonblockingServerSocket(serverPort);
		serverArgs = new TNonblockingServer.Args(
				(TNonblockingServerTransport) serverTransport);
	}

	/**
	 * 设置阻塞的服务模型
	 */
	public void setBlockingTransport() throws TTransportException {
		serverTransport = new TServerSocket(serverPort);
		serverArgs = new TThreadPoolServer.Args(serverTransport);
	}

	/**
	 * 设置办半步半异步服务模型
	 * 
	 * @throws TTransportException
	 */
	public void setHsHaTransport() throws TTransportException {
		serverTransport = new TNonblockingServerSocket(serverPort);
		serverArgs = new THsHaServer.Args(
				(TNonblockingServerTransport) serverTransport);
	}

	public void start() {
		start(null);
	}

	/**
	 * 开始服务
	 * @param eventHandler
	 */
	public void start(TServerEventHandler eventHandler) {
		serverArgs.processor(serverTprocessor);
		if (serverArgs instanceof TThreadPoolServer.Args) {
			// 一般阻塞式
			server = new TThreadPoolServer((Args) serverArgs);
		} else if (serverArgs instanceof TNonblockingServer.Args) {
			// 非阻塞异步
			// 非阻塞必须
			serverArgs.transportFactory(new TFramedTransport.Factory());
			serverArgs.processorFactory(new TProcessorFactory(serverTprocessor));
			server = new TNonblockingServer(
					(AbstractNonblockingServerArgs<?>) serverArgs);
		} else if (serverArgs instanceof THsHaServer.Args) {
			// 半同步半异步
			serverArgs.transportFactory(new TFramedTransport.Factory());
			serverArgs.processorFactory(new TProcessorFactory(serverTprocessor));
			server = new THsHaServer((THsHaServer.Args) serverArgs);
		} else {
			// 简单，用于测试
			server = new TSimpleServer(serverArgs);
		}
		setEventHandler(eventHandler);
		server.serve();
	}

	/**
	 * 关闭服务
	 */
	public void stop() {
		server.stop();
	}

	/**
	 * 设置服务器事件处理
	 * 
	 * @param eventHandler
	 */
	private void setEventHandler(TServerEventHandler eventHandler) {
		if (eventHandler != null) {
			server.setServerEventHandler(eventHandler);
		}
	}
}
