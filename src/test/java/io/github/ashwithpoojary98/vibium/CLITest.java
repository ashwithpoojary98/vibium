package io.github.ashwithpoojary98.vibium;

import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link CLI}.
 */
class CLITest {

    @Test
    void run_withNullArgs_returnsOne() {
        int exitCode = CLI.run(null);

        assertEquals(1, exitCode);
    }

    @Test
    void run_withEmptyArgs_returnsOne() {
        int exitCode = CLI.run(new String[]{});

        assertEquals(1, exitCode);
    }

    @Test
    void run_withHelp_returnsZero() {
        int exitCode = CLI.run(new String[]{"help"});

        assertEquals(0, exitCode);
    }

    @Test
    void run_withHelpFlag_returnsZero() {
        int exitCode = CLI.run(new String[]{"-h"});

        assertEquals(0, exitCode);
    }

    @Test
    void run_withHelpLongFlag_returnsZero() {
        int exitCode = CLI.run(new String[]{"--help"});

        assertEquals(0, exitCode);
    }

    @Test
    void run_withVersion_returnsZero() {
        int exitCode = CLI.run(new String[]{"version"});

        assertEquals(0, exitCode);
    }

    @Test
    void run_withVersionFlag_returnsZero() {
        int exitCode = CLI.run(new String[]{"-v"});

        assertEquals(0, exitCode);
    }

    @Test
    void run_withVersionLongFlag_returnsZero() {
        int exitCode = CLI.run(new String[]{"--version"});

        assertEquals(0, exitCode);
    }

    @Test
    void run_withUnknownCommand_returnsOne() {
        int exitCode = CLI.run(new String[]{"unknown"});

        assertEquals(1, exitCode);
    }

    @Test
    void run_withInvalidCommand_printsError() {
        PrintStream originalErr = System.err;
        ByteArrayOutputStream errContent = new ByteArrayOutputStream();
        System.setErr(new PrintStream(errContent));

        try {
            CLI.run(new String[]{"invalid-command"});

            String errorOutput = errContent.toString();
            assertTrue(errorOutput.contains("Unknown command"));
        } finally {
            System.setErr(originalErr);
        }
    }

    @Test
    void run_withHelp_printsUsage() {
        PrintStream originalOut = System.out;
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));

        try {
            CLI.run(new String[]{"help"});

            String output = outContent.toString();
            assertTrue(output.contains("Usage"));
            assertTrue(output.contains("Commands"));
            assertTrue(output.contains("install"));
            assertTrue(output.contains("version"));
            assertTrue(output.contains("help"));
        } finally {
            System.setOut(originalOut);
        }
    }

    @Test
    void run_withVersion_printsVersion() {
        PrintStream originalOut = System.out;
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));

        try {
            CLI.run(new String[]{"version"});

            String output = outContent.toString();
            assertTrue(output.contains("vibium"));
        } finally {
            System.setOut(originalOut);
        }
    }

    @Test
    void run_commandIsCaseInsensitive() {
        assertEquals(0, CLI.run(new String[]{"HELP"}));
        assertEquals(0, CLI.run(new String[]{"Help"}));
        assertEquals(0, CLI.run(new String[]{"VERSION"}));
        assertEquals(0, CLI.run(new String[]{"Version"}));
    }

    @Test
    void command_from_withInstall_returnsInstall() {
        CLI.Command command = CLI.Command.from("install");

        assertEquals(CLI.Command.INSTALL, command);
    }

    @Test
    void command_from_withVersion_returnsVersion() {
        CLI.Command command = CLI.Command.from("version");

        assertEquals(CLI.Command.VERSION, command);
    }

    @Test
    void command_from_withHelp_returnsHelp() {
        CLI.Command command = CLI.Command.from("help");

        assertEquals(CLI.Command.HELP, command);
    }

    @Test
    void command_from_withNull_throwsException() {
        assertThrows(IllegalArgumentException.class, () -> {
            CLI.Command.from(null);
        });
    }

    @Test
    void command_from_withUnknown_throwsException() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
            CLI.Command.from("unknown");
        });

        assertTrue(ex.getMessage().contains("Unknown command"));
    }

    @Test
    void command_from_isCaseInsensitive() {
        assertEquals(CLI.Command.INSTALL, CLI.Command.from("INSTALL"));
        assertEquals(CLI.Command.INSTALL, CLI.Command.from("Install"));
        assertEquals(CLI.Command.VERSION, CLI.Command.from("VERSION"));
        assertEquals(CLI.Command.HELP, CLI.Command.from("HELP"));
    }
}
