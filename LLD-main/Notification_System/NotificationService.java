//Notification Service

public abstract class Notification{
	private String notificationId;
	private String text;
	private String layout;
	private NotificationType notificationType;
}

Enum NotificationType{
	Email,
	SMS,
	WhatsApp
}

public class User{
	private long userId;
	private String phoneNumber;
	private String emailAddress;
	private String whatsappNumber;
}

public class Device{
	private long deviceId;
	private long userId;
	private String layout;
}

//DAO
public class UserDao{
	private HashMap<Long, User> userMap;
	private HashMap<Long, List<Long>> userToDeviceMap;
	private static UserDao instance = new UserDao();

	private UserDao(){
	}

	public static UserDao getInstance(){
		if(instance == null){
			instance = new UserDao();
		}
		return instance;
	}
}

public class DeviceDao{
	HashMap<Long, Device> deviceMap;
	HashMap<Long, Long> deviceToUserMap;
	HashMap<Long, List<NotificationType>> deviceToSubscribedNotificationMap;
}

//Services

public class UserService{
	UserDao userDao = UserDao.getInstance();
	DeviceService deviceService;

	public void addUser(User user){

	}

	public void removeUser(User user){

	}

	public void addDevice(User user, Device device){
		
	}

	public void removeDevice(User user, Device device){
	
	}
}

public class DeviceService{
	DeviceDao deviceDao = DeviceDao.getInstance();

	public void storeDevice(Device device){

	}

	public void removeDevice(Device device){

	}

	public void addInSubscribedTo(Device device, NotificationType notificationType){
		
	}

	public void removeDevice(Device device){
		
	}
}

interface NotificationService{
	public void sendNotification(User user, text);
}

public class EmailNotificationService implements NotificationService{

	public void sendNotification(User user, String text){
		//send notification on user.getEmail();
	}

}

public class SMSNotificationService implements NotificationService{
	
}

public class WhatsappNotificationService implements NotificationService{
	
}

//Controller

public class NotificationController{

	NotificationFactory notificationFactory;

	public void send(User user, String text){
		List<Device> devices = user.getDevices();
		for(Device device: devices){
			List<NotificationType> subscribedTo = device.getSubscribedTo();
			for(NotificationType notificationType : subscribedTo){
				NotificationService subscribedNotification = 
				NotificationFactory.getNotification(notificationType);
				subscribedNotification.sendNotification(user, text);
			}
		}
	}
}

//Util

public class NotificationFactory{

	public Notification getNotification(NotificationType notificationType){
		if(notificationType == NotificationType.Email){
			return new EmailNotificationService();
		}
		// so on
	}
}
