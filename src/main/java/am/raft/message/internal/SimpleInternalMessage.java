package am.raft.message.internal;

import am.raft.message.MessageKind;

public enum SimpleInternalMessage implements InternalMessage<Void>, InternalMessageKind {
  TRIGGER_ELECTION;

  @Override
  public MessageKind getKind() {
    return this;
  }

  @Override
  public Void getContents() {
    return null;
  }
}
