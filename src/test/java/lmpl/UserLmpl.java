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
//		try {
//			Thread.sleep(1000);
//		} catch (InterruptedException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		return mUser.size();
	}

	public List<User> getUsers() throws TException {
		// TODO Auto-generated method stub
		return mUser;
	}

}
