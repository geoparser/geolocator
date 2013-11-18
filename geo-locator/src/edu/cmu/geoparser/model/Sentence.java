package edu.cmu.geoparser.model;

import java.util.Arrays;

public class Sentence {

  String sentenceString;

  Token[] tokens;

  String id;

  public Sentence(String s){
    this.setSentenceString(s);
  }
  public void setTokens(Token[] tokens) {
    this.tokens = tokens;
  }

  Dependency[] dependencies;

  public String getSentenceString() {
    return sentenceString;
  }

  public Sentence setSentenceString(String sentenceString) {
    this.sentenceString = sentenceString;
    return this;
  }

  public Token[] getTokens() {
    return tokens;
  }

  public String getId() {
    return id;
  }

  public Sentence setId(String sentenceId) {
    this.id = sentenceId;
    return this;
  }

  public Dependency[] getDependencies() {
    return dependencies;
  }

  public Sentence setDependencies(Dependency[] dependencies) {
    this.dependencies = dependencies;

    return this;
  }

  public int tokenLength() {
    if (tokens == null)
      return 0;
    return tokens.length;
  }

  public int charLength() {
    return sentenceString.length();
  }
  
  public String toString(){
    return "id is " + id + ", sentence string is : " + sentenceString + "\n tokens are : " + Arrays.asList(tokens);
  }
}
