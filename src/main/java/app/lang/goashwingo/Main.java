package app.lang.goashwingo;

import java.io.FileNotFoundException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;

import app.lang.goashwingo.core.Lexer;
import app.lang.goashwingo.core.Parser;
import app.lang.goashwingo.core.interpreter.Orchestrator;
import app.lang.goashwingo.exceptions.InternalError;
import app.lang.goashwingo.exceptions.LoomSyntaxError;
import app.lang.goashwingo.exceptions.UnImplementedBlockError;
import app.lang.goashwingo.models.TreeModels.Program;
import app.lang.goashwingo.models.Token;

public class Main {
    public static void main(String[] args) {
        if(args.length < 1) {
            print_usage();
        }

        String program = null;
        String programPath = null;
        if(args[0].equals("mod")) {
           throw new UnImplementedBlockError("mod block");
        } else {
            try {
                Path path = Path.of(args[0]);
                program = Files.readString(path);
                programPath = args[0];
            } catch (FileNotFoundException e) {
                System.err.println("[ERROR] File Not Found!");
                print_usage();
            } catch (Exception e) {
                throw new InternalError("Unhandled error while reading file... Exception message: " + e);
            }
        }

        ArrayList<Token> tokens = tokenizeProgram(program, programPath);
        // printTokens(tokens);

        Program loomProgram = parseProgram(tokens);
        // System.out.println("response: " + loomProgram);

        runProgram(loomProgram);
    }

    private static void printTokens(ArrayList<Token> tokens) {
        for(Token token : tokens) {
            System.out.println(
                    token.getType() +", "+
                    token.getRaw() +", "+
                    token.getLine()
            );
        }
    }

    private static ArrayList<Token> tokenizeProgram(String program, String programPath) {
        Lexer lexer = new Lexer(program, programPath);
        return lexer.tokenize();
    }

    private static void runProgram(Program program) {
        Orchestrator orchestrator = new Orchestrator(program);
        orchestrator.start();
    }

    private static Program parseProgram(ArrayList<Token> tokens) {
        Parser parser = null;
        try {
            parser = new Parser(tokens);
            return parser.parse();
        } catch(LoomSyntaxError error) {
            if(parser != null) {
                System.err.println("StackTrace: ");
                for (Token token : parser.getStackTrace()) {
                    System.err.printf("  In '%s' block, at line %d\n", token.getRaw(), token.getLine());
                }
                Token currentToken = parser.peek();
                System.err.printf("Syntax Error at line %s: %s", currentToken == null ? "EOF" : currentToken.getLine(), error.getMessage());
            } else {
                System.err.println("Internal Error: SWW while processing the LoomSyntaxError");
            }
        } catch(InternalError error) {
            System.err.printf("Internal Error: %s", error.getMessage());
        } catch(Exception error) {
            System.err.printf("Unknown Error: %s", error.getMessage());
        }
        System.exit(1);
        return null;
    }

    private static void print_usage() {
        System.err.println("USAGE: loom [subcommand] <file_path>");
        System.err.println("subcommands: ");
        System.err.println("   mod  -  build *.mod file");
        System.exit(1);
    }
}