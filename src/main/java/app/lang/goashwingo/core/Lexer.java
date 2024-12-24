package app.lang.goashwingo.core;

import java.util.*;

import app.lang.goashwingo.exceptions.LoomSyntaxError;
import app.lang.goashwingo.models.Token;

public class Lexer {
    private final String program;
    private final String programPath;
    private final int length;
    private int index = -1;
    private int start = 0;
    private int line = 1;

    public Lexer(String program, String programPath) {
        this.program = program;
        this.length = program.length();
        this.programPath = programPath;
    }

    public ArrayList<Token> tokenize() {
        ArrayList<Token> tokens = new ArrayList<>();
        Token token = this.nextToken();
        while(token.getType() != TokenType.EOF) {
            tokens.add(token);
            token = this.nextToken();
        }
        return tokens;
    }

    private Token nextToken() {
        char ch = this.getChar();
        this.start = this.index;
        if(Character.isSpaceChar(ch)) {
            this.skipSpace();
            return this.nextToken();
        }
        if(ch == '\r') {
            return this.nextToken();
        }
        if(ch == '\n') {
            this.line++;
            return this.nextToken();
        }
        if(ch == ';') {
            return this.make(";", TokenType.SEMICOLON);
        }
        if(ch == ':') {
            return this.make(":", TokenType.COLON);
        }
        if(ch == '!') {
            return this.make("!", TokenType.NOT);
        }
        if(ch == ',') {
            return this.make(",", TokenType.COMMA);
        }
        if(ch == '{') {
            return this.make("{", TokenType.OPEN_BRACE);
        }
        if(ch == '}') {
            return this.make("}", TokenType.CLOSE_BRACE);
        }
        if(ch == '.') {
            return this.make(".", TokenType.DOT);
        }
        if(ch == '(') {
            return this.make("(", TokenType.OPEN_PARAM);
        }
        if(ch == ')') {
            return this.make(")", TokenType.CLOSE_PARAM);
        }
        if(ch == '=') {
            if(this.peekNext() == '=') {
                this.getChar();
                return this.make("==", TokenType.DOUBLE_EQUALS);
            }
            return this.make("=", TokenType.EQUAL);
        }
        if(ch == '<') {
            if(this.peekNext() == '=') {
                this.getChar();
                return this.make("<=", TokenType.LESSER_EQUALS);
            }
            return this.make("<", TokenType.LESSER);
        }
        if(ch == '+') {
            return this.make("+", TokenType.PLUS);
        }
        if(ch == '-') {
            return this.make("-", TokenType.MINUS);
        }
        if(ch == '*') {
            return this.make("*", TokenType.STAR);
        }
        if(ch == '/') {
            return this.make("/", TokenType.SLASH);
        }
        if(ch == '"') {
            return this.make(this.parseString(), TokenType.STRING_LITERAL);
        }
        if(Character.isAlphabetic(ch)) {
            String word = this.parseIdentifier();
            TokenType keyword = this.getKeyword(word);
            if(keyword != null) {
                return this.make(word, keyword);
            }
            return this.make(this.parseIdentifier(), TokenType.ID);
        }
        if(Character.isDigit(ch)) {
            return this.make(this.parseNumber(), TokenType.INT_LITERAL);
        }
        if(ch == 0) {
            return this.make("",TokenType.EOF);
        }
        String errorMessage = String.format("Unknown character '%s' at line %d", ch, this.line);
        throw new LoomSyntaxError(errorMessage);
    }

    private Token make(String raw, TokenType type) {
        return new Token(
                raw,
                type,
                this.line,
                this.programPath
        );
    }

    private String parseNumber() {
        while(this.isNotEof() && Character.isDigit(this.peekNext())) {
            this.getChar();
        }
        return this.getWord(this.start, this.index + 1);
    }

    private String parseString() {
        while(this.isNotEof() && this.peekNext() != '"') {
            this.getChar();
        }
        return this.getWord(this.start + 1, ++this.index);
    }

    private String parseIdentifier() {
        while(this.isNotEof() && this.isAlphaNumeric(this.peekNext())) {
            this.getChar();
        }
        return this.getWord(this.start, this.index + 1);
    }

    private void skipSpace() {
        while(this.isNotEof() && Character.isSpaceChar(this.peekNext())) {
            this.getChar();
        }
    }

    private String getWord(int start, int end) {
        return this.program.substring(start, end);
    }

    private boolean isAlphaNumeric(char ch) {
        return Character.isAlphabetic(ch) || Character.isDigit(ch) || ch == '_';
    }

    private char getChar() {
        this.index++;
        return this.peek();
    }

    private char peekNext() {
        return this.peek(1);
    }

    private char peek() {
        return this.peek(0);
    }

    private char peek(int offset) {
        if(index + offset < length) {
            return this.program.charAt(index + offset);
        }
        return 0;
    }

    private boolean isNotEof() {
        return this.index < this.length;
    }

    public TokenType getKeyword(String word) {
        Map<String, TokenType> keywordsMap = new HashMap<>() {{
            put("import", TokenType.KW_IMPORT);
            put("fn", TokenType.KW_FUNCTION);
            put("var", TokenType.KW_VAR);
            put("ret", TokenType.KW_RETURN);
            put("call", TokenType.KW_CALL);
            put("true", TokenType.KW_TRUE);
            put("false", TokenType.KW_FALSE);
            put("while", TokenType.KW_WHILE);
        }};
        return keywordsMap.getOrDefault(word, null);
    }
}