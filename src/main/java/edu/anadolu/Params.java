package edu.anadolu;

import com.lexicalscope.jewel.cli.Option;

public interface Params {

    @Option(description = "number of depots", shortName = "d", longName = "depots", defaultValue = "4")
    int getNumDepots();

    @Option(description = "number of salesmen per depot", shortName = {"s"}, longName = {"salesmen", "vehicles"}, defaultValue = "2")
    int getNumSalesmen();

    @Option(description = "use city to start from first depot", shortName = "c", longName = "city",defaultValue = "66")
    int getStartCity();

    @Option(description = "use city names when displaying/printing", shortName = "v", longName = "verbose")
    boolean getVerbose();

    @Option(helpRequest = true, description = "display help", shortName = "h")
    boolean getHelp();

    @Option(description = "select solution nn algorithm or initial random", shortName = "n",defaultValue = "all")
    String getSolutionType();
}