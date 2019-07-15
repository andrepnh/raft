package am.raft.queue;

import am.raft.message.Message;

public interface CoalescableDequeWriter {
  /**
   * Inserts the message at the last position only if it's not present already.
   */
  boolean coalesce(Message<?> message);

  /**
   * Inserts the message at the first position and removes any repetitions.
   */
  boolean coalesceIntoFirst(Message<?> message);
}
