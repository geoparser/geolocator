geolocator 
=========

This is a public release version of the geo-locator, which includes the geoparser, which is a location entity recognizer, and a location resolver, which tries to attach the correct geo-location to the location entites.

How To Run: 

Install:
If you have seen the ReadMe and installed the program, just skip 1 and 2 steps, and go to step 3.

1. download http://download.geonames.org/export/dump/allCountries.txt, and citites1000.txt in the same place, and put it into the GeoNames folder in the project root.

2. Run the GazIndexer.java in edu.cmu.geoparser.resource.gazIndexing, and set the parameters of the java program parameter as: -write GeoNames/allCountries.txt. Then run the program. It will generate a folder GazIndex in the project folder, which is the index of the gazetteer.

3. Goto the edu.cmu.geoparser.ui.CmdinputParser.java. Set the java virtual machine parameter to -Xmx3072m, and then run the program, after waiting sometime loading the resource, you will then able to test the parser in the commandline. If you are using eclipse, you can input the test sentences into the Console directly.


Classes:

location entity recognizer for English:
edu.cmu.geoparser.parser.english.EnglishParser.java

location entity recognizer for Spanish:
edu.cmu.geoparser.parser.spanish.SpanishParser.java

geo coding for English and Spanish:
edu.cmu.geoparser.disambiguation.ContextDisamb.java

Misspelling model:
edu.cmu.geoparser.nlp.spelling.MisspellParser.java

Demo class:

edu.cmu.geoparser.ui.CommandLine.CmdInputParser.java
This is the demo for showing on-the-fly parsing of the command line input as a (tweet)sentence.
This class includes all the function you need to run the geolocator.

