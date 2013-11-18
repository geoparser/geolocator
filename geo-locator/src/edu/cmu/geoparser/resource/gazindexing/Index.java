package edu.cmu.geoparser.resource.gazindexing;

import java.util.ArrayList;
import org.apache.lucene.document.*;
public interface Index {
  
  /**
   * search in the resource for existance.
   * @param phrase
   * @return boolean value
   */
  public boolean inIndex(String phrase);
//  public boolean inIndexStrict(String phrase);
  
  /**
   * search index, return the documents fetched.
   * @param phrase to search
   * @return list of docuements containing detail info.
   */
  public ArrayList<Document> getDocumentsByPhrase(String phrase);
//  public ArrayList<Document> getDocumentsByPhraseStrict(String phrase);
  
  /**
   * search index by id.
   */
  public Document getDocumentsById(String id);
  
  /**
   * open index
   */
  public Index open();
  /**
   * close index
   */
   public void close();
}
