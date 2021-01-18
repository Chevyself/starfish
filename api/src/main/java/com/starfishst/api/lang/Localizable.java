package com.starfishst.api.lang;

import lombok.NonNull;

/** This objects represents an entity that can be localized */
public interface Localizable {

  /**
   * Set the language that this entity must use
   *
   * @param lang the new language that this entity must use
   */
  void setLang(@NonNull String lang);

  /**
   * Get the language that this entity is using
   *
   * @return the language that this entity is using
   */
  @NonNull
  String getLang();
}
