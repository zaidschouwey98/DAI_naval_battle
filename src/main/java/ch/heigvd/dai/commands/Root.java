package ch.heigvd.dai.commands;

import picocli.CommandLine;

@CommandLine.Command(
        description = "Simulation of a online game of naval battle using TCP sockets.\n" +
                      "Requires 1 server and 2 players min..",
        version = "1.0.0",
        subcommands = {
                Client.class,
                Server.class,
        },
        scope = CommandLine.ScopeType.INHERIT,
        mixinStandardHelpOptions = true)
public class Root {}