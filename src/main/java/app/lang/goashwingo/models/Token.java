package app.lang.goashwingo.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import app.lang.goashwingo.core.TokenType;

@AllArgsConstructor
@Getter
@Setter
public class Token {
    String raw;
    TokenType type;
    int line;
    String path;
}
