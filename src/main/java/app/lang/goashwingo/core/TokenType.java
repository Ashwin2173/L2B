package app.lang.goashwingo.core;

import java.util.function.Predicate;

public enum TokenType {
    OPEN_BRACE,
    CLOSE_BRACE,
    OPEN_PARAM,
    CLOSE_PARAM,
    EOF,
    SEMICOLON,
    COLON,
    COMMA,
    ID,
    DOT,
    NOT,
    EQUAL,
    PLUS,
    GREATER,
    LESSER,
    GREATER_EQUALS,
    LESSER_EQUALS,
    DOUBLE_EQUALS,
    NOT_EQUALS,
    MINUS,
    STAR,
    SLASH,
    INT_LITERAL,
    STRING_LITERAL,
    FLOAT_LITERAL,

    KW_FUNCTION,
    KW_RETURN,
    KW_IMPORT,
    KW_VAR
}
