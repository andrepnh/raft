package am.raft;

import am.raft.node.state.NodeType;
import io.cucumber.core.api.TypeRegistry;
import io.cucumber.core.api.TypeRegistryConfigurer;
import io.cucumber.cucumberexpressions.ParameterType;

import java.util.Locale;

public class TypeRegistryConfiguration implements TypeRegistryConfigurer {
  @Override
  public Locale locale() {
    return Locale.ENGLISH;
  }

  @Override
  public void configureTypeRegistry(TypeRegistry typeRegistry) {
    typeRegistry.defineParameterType(new ParameterType<>(
        "nodeType",
        "followers?|candidates?|leaders?",
        NodeType.class,
        NodeType::of
    ));
  }
}
