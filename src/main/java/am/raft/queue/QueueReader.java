package am.raft.queue;

import am.raft.message.Message;

public interface QueueReader {
  Message pool();
}
