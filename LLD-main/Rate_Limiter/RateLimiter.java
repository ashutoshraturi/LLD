//Models

//Rate Limiting based on multiple fields such as userId, IP or location for 3 different algorithms

public enum RateLimiterType{
	TOKEN_BUCKET,
	LEAKY_BUCKET,
	SLIDING_WINDOW;
}

public class User{
	private long userId;
	private String ip;
	private String location;
}

public class Request{
	private long requestId;
	private String requestMessage;
}

//Services

public interface RateLimiter{
	public boolean grantAccess(Request request);
}

public class LeakyBucket implements RateLimiter{
	BlockingQueue<Long> leakyBucket;

	 public LeakyBucket(int capacity) {
        this.queue = new LinkedBlockingQueue<>(capacity);
    }

    @Override
    public boolean grantAccess(Request request) {
        if(leakyBucket.remainingCapacity() > 0){
            leakyBucket.add(request.getRequestId());
            return true;
        }
        return false;
    }
}

public class TokenBucket implements RateLimiter{
	private int bucketCapacity;
    private int refreshRate;
    private AtomicInteger currentCapacity;
    private AtomicLong lastUpdatedTime;

    public TokenBucket(int bucketCapacity, int refreshRate) {
        this.bucketCapacity = bucketCapacity;
        this.refreshRate = refreshRate;
        currentCapacity = new AtomicInteger(bucketCapacity);
        lastUpdatedTime = new AtomicLong(System.currentTimeMillis());
    }

    @Override
    public boolean grantAccess() {
        refreshBucket();
        if(currentCapacity.get() > 0){
            currentCapacity.decrementAndGet();
            return true;
        }
        return false;
    }

    void refreshBucket(){
        long currentTime = System.currentTimeMillis();
        int additionalToken = (int) ((currentTime-lastUpdatedTime.get())/1000 * refreshRate);
        int currCapacity = Math.min(currentCapacity.get()+additionalToken, bucketCapacity);
        currentCapacity.getAndSet(currCapacity);
        lastUpdatedTime.getAndSet(currentTime);
    }
	
}

public class SlidingWindow implements RateLimiter{
	Queue<Long> slidingWindow;
    int timeWindowInSeconds;
    int bucketCapacity;

    public SlidingWindow(int timeWindowInSeconds, int bucketCapacity) {
        this.timeWindowInSeconds = timeWindowInSeconds;
        this.bucketCapacity = bucketCapacity;
        slidingWindow = new ConcurrentLinkedQueue<>();
    }

    @Override
    public boolean grantAccess() {
        long currentTime = System.currentTimeMillis();
        checkAndUpdateQueue(currentTime);
        if(slidingWindow.size() < bucketCapacity){
            slidingWindow.offer(currentTime);
            return true;
        }
        return false;
    }

    private void checkAndUpdateQueue(long currentTime) {
        if(slidingWindow.isEmpty()) return;

        long calculatedTime = (currentTime - slidingWindow.peek())/1000;
        while(calculatedTime >= timeWindowInSeconds){
            slidingWindow.poll();
            if(slidingWindow.isEmpty()) break;
            calculatedTime = (currentTime - slidingWindow.peek())/1000;
     	}
	}
}

//utils

public class RateLimiterFactory{
	@Value("{token.bucket.limit}")
	private int tokenBucketLimit;
	@Value("{token.bucket.refresh_rate")
	private int tokenBucketRefreshRate;
	@Value("{leaky.bucket.limit}")
	private int leakyBucketLimit;
	@Value("{sliding.window.limit}")
	private int slidingWindowLimit;
	@Value("{sliding.window.window_size}")
	private int slidingWindowSize;

	public RateLimiter returnRateLimiter(String ratelimiterType){
		
			if(RateLimiterType.TOKEN_BUCKET.isEqualsIgnoreCase(ratelimiterType))
				return new TokenBucket(tokenBucketLimit, tokenBucketRefreshRate);
			else if(RateLimiterType.LEAKY_BUCKET.isEqualsIgnoreCase(ratelimiterType))
				return new LeakyBucket(leakyBucketLimit);
			else
				return new SlidingWindow(slidingWindowLimit, slidingWindowSize);
	}
}

//controller

public class RateLimiterController{

	@Value("{rate.limit.algorithm}")
	private String rateLimitAlgorithm;
	@Value("#{rate.limit.fields}")
	private List<String> rateLimitFields;

	Map<Long, RateLimiter> userBasedLimiting;
	Map<String, RateLimiter> ipBasedLimiting;
	Map<String, RateLimiter> locationBasedLimiting;

	public RateLimiterController(){
		this.userBasedLimiting = new HashMap<>();
		this.ipBasedLimiting = new HashMap<>();
		this.locationBasedLimiting = new HashMap<>();
	}

	RateLimiterFactory rateLimiterFactory;
    RateLimiter rateLimiter = rateLimiterFactory.returnRateLimiter(rateLimitAlgorithm);

    public boolean grantAccess(User user, Request request){
    	for(String field: rateLimitFields){
    		if(field.equals("userId")){
    			if(!userBasedLimiting.containsKey(user.getUserId()))
    				userBasedLimiting.put(user.getUserId(), rateLimiter);

    			Boolean granted = userBasedLimiting.get(user.getUserId()).grantAccess(request);
    			if(!granted){
    				return false;
    			}
    		}else if(field.equals("ip")){
    			if(!ipBasedLimiting.containsKey(user.getIp()))
    				userBasedLimiting.put(user.getIp(), rateLimiter);

    			Boolean granted = userBasedLimiting.get(user.getIp()).grantAccess(request);
    			if(!granted){
    				return false;
    			}
    		}else if(field.equals("location")){
    			if(!userBasedLimiting.containsKey(user.getLocation()))
    				userBasedLimiting.put(user.getLocation(), rateLimiter);

    			Boolean granted = userBasedLimiting.get(user.getLocation()).grantAccess(request);
    			if(!granted){
    				return false;
    			}
    		}
    	}
    	return true;
    }
}


//app properties
/*
token.bucket.limit=10;
token.bucket.refresh_rate=1;
leaky.bucket.limit=10;
sliding.window.limit = 10;
sliding.window.window_size=1;
rate.limit.algorithm=SLIDING_WINDOW;
rate.limit.fields= {"userId", "ip"};
*/
