package edu.cmu.geoparser;
import java.util.Scanner;
public class test {

  public static void main(String argv[]){
    Scanner s = new Scanner("This is a test\tString.\nCome and see it.");
    s.useDelimiter("\t");
    while( s.hasNext()){
      System.out.println(s.next());
    }
  }
}
