package am.raft.queue;

import am.raft.message.Message;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Objects;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Supplier;

import static com.google.common.base.Preconditions.checkNotNull;

class ThreadSafeCoalescableDeque implements CoalescableDeque {
  private static final Logger LOG = LogManager.getLogger(ThreadSafeCoalescableDeque.class);

  private final Deque<Message> deque = new ArrayDeque<>();

  private final Lock lock = new ReentrantLock();

  private final Condition notEmpty = lock.newCondition();

  @Override
  public boolean coalesce(Message<?> message) {
    checkNotNull(message);
    return usingLock(lock, () -> {
      if (!deque.contains(message)) {
        var inserted = deque.offerLast(message);
        LOG.debug("Coalesced to last {}? {}", message, inserted);
        if (inserted) {
          notEmpty.signal();
        }
        return inserted;
      }
      LOG.debug("Skipping message {}", message);
      return false;
    });
  }

  @Override
  public boolean coalesceIntoFirst(Message<?> message) {
    checkNotNull(message);
    return usingLock(lock, () -> {
      deque.removeIf(queuedMessage -> Objects.equals(queuedMessage, message));
      var inserted = deque.offerFirst(message);
      if (inserted) {
        notEmpty.signal();
      }
      LOG.debug("Coalesced into first {}? {}", message, inserted);
      return inserted;
    });
  }

  @Override
  public Message pool() {
    return usingLock(lock, () -> {
      while (deque.isEmpty()) {
        try {
          notEmpty.await();
        } catch (InterruptedException e) {
          // TODO considerate interruptions properly
          throw new IllegalStateException(e);
        }
      }

      return deque.pollFirst();
    });
  }

  private <T> T usingLock(Lock lock, Supplier<T> command) {
    var acquiredLock = false;
    try {
      lock.lockInterruptibly();
      acquiredLock = true;
      return command.get();
    } catch (InterruptedException e) {
      // TODO considerate interruptions properly
      throw new IllegalStateException(e);
    } finally {
      if (acquiredLock) {
        lock.unlock();
      }
    }
  }
}
