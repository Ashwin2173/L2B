package app.lang.goashwingo.core;

import java.util.*;

import app.lang.goashwingo.exceptions.LoomSyntaxError;
import app.lang.goashwingo.models.*;
import app.lang.goashwingo.models.TreeModels.*;

public class Parser {
    private final List<Token> tokens;
    private final Set<String> imports;
    private final int length;
    private int index = -1;
    private final Stack<Token> trace = new Stack<>();

    public Parser(ArrayList<Token> tokens) {
        this.tokens = tokens;
        this.length = this.tokens.size();
        this.imports = new HashSet<>();
    }

    public Program parse() {
        return new Program("1.0", this.imports, this.parseStatement());
    }

    public Stack<Token> getStackTrace() {
        return this.trace;
    }

    private List<Statement> parseStatement() {
        List<Statement> statements = new ArrayList<>();
        while(this.isNotEof()) {
            Token token = this.peek();
            if(token.getType() == TokenType.KW_VAR) {
                statements.add(this.parseVariableDeclaration());
            } else if(token.getType() == TokenType.KW_IMPORT) {
                this.checkLocalScope("import statement");
                this.parseImportStatement();
            } else if(token.getType() == TokenType.KW_FUNCTION) {
                this.checkLocalScope("function declaration");
                statements.add(this.parseFunctionDeclaration());
            } else if(token.getType() == TokenType.KW_RETURN) {
                this.checkGlobalScope("return statement");
                statements.add(this.parseReturnStatement());
            } else if(token.getType() == TokenType.KW_WHILE) {
                this.checkGlobalScope("while statement");
                statements.add(this.parseWhileStatement());
            } else if(token.getType() == TokenType.KW_CALL) {
                this.checkGlobalScope("system call statement");
                statements.add(this.parseCallStatement());
            } else if(token.getType() == TokenType.CLOSE_BRACE) {
                this.trace.pop();
                return statements;
            } else {
                Token nextToken = this.peek(1);
                if(token.getType() == TokenType.ID && (nextToken != null && nextToken.getType() == TokenType.EQUAL)) {
                    statements.add(this.parseAssignmentStatement());
                } else {
                    statements.add(this.parseExpression());
                    this.consumeToken(TokenType.SEMICOLON);
                }
            }
        }
        return statements;
    }

    private void parseImportStatement() {
        this.consumeToken(TokenType.KW_IMPORT);
        Token moduleNameToken = this.peek();
        this.consumeToken(TokenType.ID);
        this.consumeToken(TokenType.SEMICOLON);
        this.addImport(moduleNameToken);
    }

    private AssignmentStatement parseAssignmentStatement() {
        Token name = this.peek();
        int line = name.getLine();
        this.consumeToken(TokenType.ID);
        this.consumeToken(TokenType.EQUAL);
        ExpressionStatement expressionStatement = new ExpressionStatement(this.equality());
        this.consumeToken(TokenType.SEMICOLON);
        return new AssignmentStatement(name, expressionStatement);
    }

    private WhileStatement parseWhileStatement() {
        Token whileToken = this.peek();
        this.trace.push(whileToken);
        int line = whileToken.getLine();

        WhileStatement whileStatement = new WhileStatement(line);
        this.consumeToken(TokenType.KW_WHILE);
        this.consumeToken(TokenType.OPEN_PARAM);
        ExpressionStatement expressionStatement = this.parseExpression();
        whileStatement.setExpressionStatement(expressionStatement);
        this.consumeToken(TokenType.CLOSE_PARAM);
        BlockStatement body = this.parseBlockStatement();
        whileStatement.setBody(body);
        this.consumeToken(TokenType.SEMICOLON);
        return whileStatement;
    }

    private ReturnStatement parseReturnStatement() {
        this.consumeToken(TokenType.KW_RETURN);
        ExpressionStatement expressionStatement = this.parseExpression();
        this.consumeToken(TokenType.SEMICOLON);
        return new ReturnStatement(expressionStatement);
    }

    private CallStatement parseCallStatement() {
        int line = this.peek().getLine();
        this.consumeToken(TokenType.KW_CALL);
        List<ExpressionStatement> params = this.parseExpressionList();
        this.consumeToken(TokenType.SEMICOLON);
        return new CallStatement(params, line);
    }

    private FunctionDeclaration parseFunctionDeclaration() {
        this.consumeToken(TokenType.KW_FUNCTION);
        Token name = this.peek();
        this.trace.push(name);
        this.consumeToken(TokenType.ID);
        FunctionDeclaration functionDeclaration = new FunctionDeclaration(name);

        if(this.match(TokenType.COLON)) {
            boolean isFirstArgument = true;
            this.consumeToken(TokenType.COLON);
            do {
                Token currentToken = this.peek();
                if (isFirstArgument && currentToken.getType() == TokenType.OPEN_BRACE) {    // this is for a stupid feature
                    break;
                }
                functionDeclaration.addArguments(currentToken);
                this.consumeToken(TokenType.ID);
                isFirstArgument = false;
            } while (this.matchConsume(TokenType.COMMA));
        }

        BlockStatement body = this.parseBlockStatement();
        functionDeclaration.setBody(body);
        return functionDeclaration;
    }

    private VariableDeclaration parseVariableDeclaration() {
        this.consumeToken(TokenType.KW_VAR);
        Token variableToken = this.peek();
        this.consumeToken(TokenType.ID);
        this.consumeToken(TokenType.EQUAL);
        ExpressionStatement expression = this.parseExpression();
        this.consumeToken(TokenType.SEMICOLON);
        return new VariableDeclaration(variableToken, expression);
    }

    private CallExpression parseCallExpression() {
        Token currentToken = this.peek();
        int line = currentToken.getLine();
        List<Identifier> calleeList = this.parseMembers();
        this.consumeToken(TokenType.OPEN_PARAM);
        List<ExpressionStatement> argumentList = this.parseExpressionList();
        this.consumeToken(TokenType.CLOSE_PARAM);
        return new CallExpression(calleeList, argumentList, line);
    }

    private List<Identifier> parseMembers() {
        List<Identifier> membersList = new ArrayList<>();
        do {
            Token token = this.peek();
            Identifier identifier = new Identifier(token.getRaw(), token.getLine());
            membersList.add(identifier);
            this.consumeToken(TokenType.ID);
        } while(this.matchConsume(TokenType.DOT));
        return membersList;
    }

    private List<ExpressionStatement> parseExpressionList() {
        List<ExpressionStatement> expressionStatementList = new ArrayList<>();
        if(this.match(TokenType.CLOSE_PARAM)) {
            return expressionStatementList;
        }
        do {
            ExpressionStatement argumentExpression = this.parseExpression();
            expressionStatementList.add(argumentExpression);
        } while(this.matchConsume(TokenType.COMMA));
        return expressionStatementList;
    }

    private BlockStatement parseBlockStatement() {
        int line = this.peek().getLine();
        this.consumeToken(TokenType.OPEN_BRACE);
        List<Statement> statements = this.parseStatement();
        this.consumeToken(TokenType.CLOSE_BRACE);
        return new BlockStatement(statements, line);
    }

    private ExpressionStatement parseExpression() {
        return new ExpressionStatement(this.equality());
    }

    private Expression equality() {
        Expression leftExpression = this.comparison();
        while(this.match(TokenType.DOUBLE_EQUALS, TokenType.NOT_EQUALS)) {
            Token operator = this.peek();
            this.nextToken();
            Expression rightExpression = this.comparison();
            leftExpression = new BinaryExpression(leftExpression, operator, rightExpression);
        }
        return leftExpression;
    }

    private Expression comparison() {
        Expression leftExpression = this.term();
        while(this.match(TokenType.LESSER, TokenType.LESSER_EQUALS, TokenType.GREATER, TokenType.GREATER_EQUALS)) {
            Token operator = this.peek();
            this.nextToken();
            Expression rightExpression = this.term();
            leftExpression = new BinaryExpression(leftExpression, operator, rightExpression);
        }
        return leftExpression;
    }


    private Expression term() {
        Expression leftExpression = this.factor();
        while(this.match(TokenType.PLUS, TokenType.MINUS)) {
            Token operator = this.peek();
            this.nextToken();
            Expression rightExpression = this.factor();
            leftExpression = new BinaryExpression(leftExpression, operator, rightExpression);
        }
        return leftExpression;
    }

    private Expression factor() {
        Expression leftExpression = this.unary();
        while(this.match(TokenType.STAR, TokenType.SLASH)) {
            Token operator = this.peek();
            this.nextToken();
            Expression rightExpression = this.unary();
            leftExpression = new BinaryExpression(leftExpression, operator, rightExpression);
        }
        return leftExpression;
    }

    private Expression unary() {
        if(this.match(TokenType.NOT, TokenType.MINUS)) {
            Token operator = this.peek();
            this.nextToken();
            Expression rightExpression = this.unary();
            return new UnaryExpression(operator, rightExpression);
        }
        return this.primary();
    }

    private Expression primary() {
        Token token = this.peek();
        if(token.getType() == TokenType.INT_LITERAL) {
            this.nextToken();
            int value = Integer.parseInt(token.getRaw());
            return new IntLiteral(value);
        }
        if(token.getType() == TokenType.KW_TRUE) {
            this.nextToken();
            return new BooleanLiteral(true);
        }
        if(token.getType() == TokenType.KW_FALSE) {
            this.nextToken();
            return new BooleanLiteral(false);
        }
        if(token.getType() == TokenType.ID) {
            Token nextToken = this.peek(1);
            if(nextToken != null) {
                if (nextToken.getType() == TokenType.OPEN_PARAM) {
                    return this.parseCallExpression();
                } else if (nextToken.getType() == TokenType.DOT) {
                    return this.parseCallExpression();
                }
            }
            this.nextToken();
            return new Identifier(token.getRaw(), token.getLine());
        }
        if(token.getType() == TokenType.STRING_LITERAL) {
            this.nextToken();
            return new StringLiteral(token.getRaw());
        }
        if(token.getType() == TokenType.OPEN_PARAM) {
            this.nextToken();
            Expression expression = this.equality();
            this.consumeToken(TokenType.CLOSE_PARAM);
            this.nextToken();
            return expression;
        }
        String errorMessage = String.format("Invalid literal '%s', at line %d", token.getRaw(), token.getLine());
        throw new LoomSyntaxError(errorMessage);
    }

    private boolean match(TokenType... tokenTypes) {
        Token currentToken = this.peek();
        if(currentToken == null) {
            return false;
        }
        TokenType currentTokenType = currentToken.getType();
        for(TokenType tokentype : tokenTypes) {
            if(tokentype == currentTokenType) {
                return true;
            }
        }
        return false;
    }

    private void consumeToken(TokenType expected) {
        if(!isNotEof()) {
            String errorMessage = String.format("expected '%s', but at end of file", expected);
            throw new LoomSyntaxError(errorMessage);
        }
        TokenType currentTokenType = this.peek().getType();
        if(currentTokenType != expected) {
            String errorMessage = String.format("expected '%s', but got '%s'", expected, currentTokenType);
            throw new LoomSyntaxError(errorMessage);
        }
        this.nextToken();
    }

    private boolean matchConsume(TokenType tokenType) {
        if(this.match(tokenType)) {
            this.consumeToken(tokenType);
            return true;
        }
        return false;
    }

    private void addImport(Token token) {
        String moduleName = token.getRaw();
        this.imports.add(moduleName);
    }

    private Token nextToken() {
        this.index++;
        return this.peek();
    }

    public Token peek() {
        return this.peek(0);
    }

    private Token peek(int offset) {
        if(this.index == -1) {
            this.index = 0;
        }
        if(this.index + offset < this.length) {
            return this.tokens.get(this.index + offset);
        }
        return null;
    }

    private void checkGlobalScope(String thing) {
        if(this.trace.isEmpty()) {
            String errorMessage = String.format("Usage of %s at GlobalScope", thing);
            throw new LoomSyntaxError(errorMessage);
        }
    }

    private void checkLocalScope(String thing) {
        if(!this.trace.isEmpty()) {
            String errorMessage = String.format("Usage of %s at LocalScope", thing);
            throw new LoomSyntaxError(errorMessage);
        }
    }
    private boolean isNotEof() {
        return this.index < this.length;
    }
}
