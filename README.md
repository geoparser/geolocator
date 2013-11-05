Read Me

GeoLocator v1.0
The geolocation algorithm contains both geoparser that extract locations and a geo-coder that assigns latitude and longitude to each location.

The geolocation algorithm contains 4 English parsers (building parser, Toponym heuristic parser, Stanford NER and our CRF tweet-trained parser) and 3 Spanish parsers (building parser, toponym heuristic parser, CRF trained parser) which are included in edu/cmu/geoparser/ folder. The common interface for those parsers is in the folder too.

The algorithm takes a .txt file as input, or else, use the command line tool by entering one sentence per line. The best way to use it is to look at the CmdInputParser.java included in edu.cmu.geoparser.ui.CommandLine.

In addition to a gelocation algorithm, the package contains a fuzzy match algorithm that takes web 2.0 tags plus latitude and longitude as input, and compares them with location entries in the GeoNames gazetteer to determine whether the web 2.0 entries match with the gazetteer entries or they are novel.

/////////////// Introduction ///////////////

Tagging the command line input

The output format for the commandline and batch file: Each recognized location is wraped as XX{location}XX, where XX could be any of the eight tags: TP,tp, ST,st,BD,bd,AB,ab. TP, ST, BD, AB are output from the Named Entity Recognizer. tp,st,bd,ab are the output from the rule based and toponym lookup parsers.

/////////////// How to Install: ///////////////

The algorithm can run on Windows, Mac, or Linux/Unix platforms.

1.Check out the project.
In eclipse, try import ->project from git.

2. After checked out the project into Eclipse workspace,
Go to the terminal (if you are using linux or mac osx), or cygwin for windows, cd to the geo-locator folder, run isntall.sh to install the software.
This is a long process because we have to download jar files, resources from geonames, and most time-consuming is the indexing of the geoname.
The estimate time is about 1 hour. It varies with your machine. 

To run the fuzzy match algorithm in edu.cmu.geoparser.nlp.spelling, please see the instructions in FuzzyGeoMatch project.

Please send email to gelern@cs.cmu.edu if you find any bug or have any question, or any suggestions.

Thank you.