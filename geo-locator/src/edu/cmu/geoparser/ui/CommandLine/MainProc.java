package edu.cmu.geoparser.ui.CommandLine;

import edu.cmu.geoparser.resource.gazindexing.GazIndexer;

public class MainProc {

  public static void main(String argv[]) throws Exception {

    if (argv.length < 1) {
      help();
    }

    String mode = argv[0];
    if (mode.equals("-sysin"))// -sysin -nmis allcountries.txt gazIndex
      CmdInputParser.main(new String[] { argv[1], argv[2],argv[3] });
    else if (mode.equals("-index")) {
      if (argv[1].equals("-write")) // -index -write allcountries.txt gazIndex
        GazIndexer.main(new String[] { "-write", argv[2],argv[3] });
      else if (argv[1].equals("-read"))// index -read gazindex 
        GazIndexer.main(new String[] { "-read", argv[2] });
      else {
        throw new Exception("wrong index usage command. Should be -read or -write.");
      }
    } else if (mode.equals("-batch"))// -batch -nmis allcountries.txt gazindex input type output
      // type should be -json or -t, where -j means json tweet, and -text means tweet text without json.
      formatter.main(new String[] { argv[1], argv[2], argv[3], argv[4],argv[5],argv[6] });
    else {
      help();
    }
  }

  static void help() {

    System.err
            .println("\n BATCH TAGGING EXAMPLE:\n java MainProc.java -batch -[mis/nmis] myfolder/Geonames/allCountries.txt myfolder/GazIndex test.csv output.csv");

    System.err
            .println("\n COMMAND LINE INPUT EXAMPLE:\n java MainProc.java -sysin -[mis/nmis] myfolder/Geonames/allCountries.txt myfolder/GazIndex");

    System.err
            .println("\n GAZ INDEXING AND QUERYING: \n"
                    + "java -Xmx...m MainProc.java -index [-write/-read] [file to index] \n"
                    + "WRITING EXAMPLE: java -Xmx3072m MainProc.java -index -write myfolder/GeoNames/allCountires.txt myfolder/GazIndex");

    System.err
            .println("READING EXAMPLE(only for index testing): java MainProc.java -index -read myfolder/GazIndex");

    System.err
            .println("\nIf you USE the allCountries.txt AS GAZETTEER, MAKE SURE THE MEMORY ALLOCATION IS ENOUGH.");

  }
}
