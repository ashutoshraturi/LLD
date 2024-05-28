//Task Scheduler

public interface ExecutionContext {
    void execute();
}

public abstract class ScheduledTask {
  public final ExecutionContext context;
  public ScheduledTask (ExecutionContext context) {
   this.context = context;
  }

  abstract boolean isRecurring();

  void execute() {
   context.execute();
  }

  abstract ScheduledTask nextScheduledTask();

  abstract long getNextExecutionTime();
}

public class OneTimeTask extends ScheduledTask {
    private final long executionTime;

    public ScheduledTaskImpl(ExecutionContext context, long executionTime) {
    super(context);
        this.executionTime = executionTime;
    }

    @Override
    public long getNextExecutionTime() {
        return executionTime;
    }

    @Override
    public boolean isRecurring() {
        return false;
    }

    @Override
    public ScheduledTask nextScheduledTask() {
        return null;
    }
}

public class RecurringTask extends ScheduledTask {
    private final long executionTime;
    private final long interval;

    public RecurringTask(ExecutionContext context, long executionTime, long interval) {
        super(context);
        this.executionTime = executionTime;
        this.interval = interval;
    }

    @Override
    public long getNextExecutionTime() {
        return executionTime;
    }

    @Override
    public boolean isRecurring() {
        return true;
    }

    @Override
    public ScheduledTask nextScheduledTask() {
        return new RecurringTask(context, executionTime + interval, interval);
    }
}

public interface TaskStore<T extends ScheduledTask> {

    T peek();

    T poll();

    void add(T task);

    boolean remove(T task);

    boolean isEmpty();
}

public class PriorityBlockingQueueTaskStore implements TaskStore<ScheduledTask> {

    private final PriorityBlockingQueue<ScheduledTask> taskQueue;
    private final Set<ScheduledTask> tasks;

    public PriorityBlockingQueueTaskStore(Comparator<ScheduledTask> comparator, Integer queueSize) {
        this.taskQueue = new PriorityBlockingQueue<>(queueSize, comparator);
        this.tasks = new HashSet<>();
    }

    @Override
    public void add(ScheduledTask task) {
        taskQueue.offer(task);
    }

    @Override
    public ScheduledTask poll() {
        return taskQueue.poll();
    }

    @Override
    public ScheduledTask peek() {
        return taskQueue.peek();
    }

    @Override
    public boolean remove(ScheduledTask task) {
        if (tasks.contains(task)) {
            taskQueue.remove(task);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public synchronized boolean isEmpty() {
        return taskQueue.isEmpty();
    }
}


public class TaskRunner implements Runnable {
    private final TaskStore<ScheduledTask> taskStore;
    private boolean running;

    public TaskRunner(TaskStore<ScheduledTask> taskStore) {
        this.taskStore = taskStore;
    }

    public void run() {
        while (running) {
            ScheduledTask scheduledTask = taskStore.poll();
            if (scheduledTask == null) {
                break;
            }
            scheduledTask.execute();
            if (scheduledTask.isRecurring()) {
                taskStore.add(scheduledTask.nextScheduledTask());
            }
        }
    }

    public void stop() {
        this.running = false;
    }
}

public class ExecutorConfig {
    private int numThreads;

    public ExecutorConfig(int numThreads) {
        this.numThreads = numThreads;
    }

    public int getNumThreads() {
        return numThreads;
    }

    public void setNumThreads(int numThreads) {
        this.numThreads = numThreads;
    }
}

public class TaskScheduler {
    private final List<Runnable> threads;
    private final TaskStore<ScheduledTask> taskStore;

    public TaskScheduler(ExecutorConfig executorConfig, TaskStore<ScheduledTask> taskStore) {
        this.threads = new ArrayList<>();
        this.taskStore = taskStore;
        for (int i = 0; i < executorConfig.getNumThreads(); i++) {
            Thread thread = new Thread(new TaskRunner(taskStore));
            thread.start();
            threads.add(thread);
        }
    }

    public void stop() {
        threads.forEach(t -> ((TaskRunner) t).stop());
    }
}