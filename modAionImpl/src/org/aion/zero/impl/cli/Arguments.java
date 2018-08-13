/*
 * Copyright (c) 2017-2018 Aion foundation.
 *
 *     This file is part of the aion network project.
 *
 *     The aion network project is free software: you can redistribute it
 *     and/or modify it under the terms of the GNU General Public License
 *     as published by the Free Software Foundation, either version 3 of
 *     the License, or any later version.
 *
 *     The aion network project is distributed in the hope that it will
 *     be useful, but WITHOUT ANY WARRANTY; without even the implied
 *     warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *     See the GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with the aion network project source files.
 *     If not, see <https://www.gnu.org/licenses/>.
 *
 * Contributors:
 *     Aion foundation.
 */
package org.aion.zero.impl.cli;

import java.util.ArrayList;
import java.util.List;
import picocli.CommandLine;
import picocli.CommandLine.Option;


/**
 * Command line arguments for the Aion kernel.
 *
 * @author Alexandra Roatis
 */
public class Arguments {

    @Option(names = {"-h", "--help"}, usageHelp = true, description = "display a help message")
    private boolean helpRequested = false;


    @Option(names = {"-a create",
        "--account create"}, description = "create a new account\n")
    public boolean create_account;

    @Option(names = {"-a list",
        "--account list"}, description = "list all existing accounts\n")
    public boolean list_accounts;

    @Option(names = {"-a export",
        "--account export"}, description = "export private key of an account\n")
    public String export_account;

    @Option(names = {"-a import",
        "--account import"}, description = "import private key\n")
    public boolean import_account = true;


    // CREATE CONFIG
    @Option(names = {"-c", "--config"}, description = "Level of verbosity")
    private Integer account_command = 1;

//    ACCOUNT_CREATE,
//    ACCOUNT_EXPORT,
//    ACCOUNT_IMPORT,
//    ACCOUNT_LIST,
//    CREATE_CONFIG,
//    DATA_COMPACT,
//    DATA_DIRECTORY,
//    DUMP_BLOCKS,
//    DUMP_STATE,
//    DUMP_STATE_SIZE,
//    HELP,
//    INFORMATION,
//    NETWORK,
//    REVERT_BLOCK,
//    REVERT_CLEAN,
//    SSL,
//    STATE_REORGANIZE,
//    VERSION_LONG
//    VERSION_SHORT,

    public static void main(String... args) {
        Arguments params = new Arguments();
        String[] argv = {"-a create", "-a list", "-a export", "0x123"};

        CommandLine commandLine = new CommandLine(params);
        commandLine.parse(args);

        commandLine.parse(argv);
        System.out.println(params.create_account);
        System.out.println(params.export_account.toString());

        commandLine.usage(System.out);
    }


    /**
     * Compacts the account options into specific commands.
     */
    public static String[] preprocess(String[] arguments) {
        List<String> list = new ArrayList<>();

        int i = 0;
        while (i < arguments.length) {
            if (arguments[i].equals("-a") || arguments[i].equals("--account")) {
                list.add(arguments[i] + " " + arguments[i + 1]);
                i++;
            } else {
                list.add(arguments[i]);
            }
            i++;
        }

        return list.toArray(new String[list.size()]);
    }
}