package com.starfishst.api.utility.properties;

import java.util.Map;
import lombok.NonNull;
import lombok.experimental.Delegate;

public class PropertyMap {

  @NonNull @Delegate private final Map<String, Property> properties;

  protected PropertyMap(@NonNull Map<String, Property> properties) {
    this.properties = properties;
  }
}
