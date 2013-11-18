package edu.cmu.geoparser.resource.Map;

public abstract class Map<T> {

  public abstract T load();
  public abstract T getValue(String code);
}
