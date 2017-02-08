/*
 * Regular path query (RPQ) grammar for ANTLR4.
 *
 * @author Giedo Mak
 * @author Nikolay Yakovets
 */

grammar RPQ;

/**
 * Parser rules
 * Each parser rule gets one of the following names: queryOperator, queryOperatorQuery, leaf or parenthesis
 */

query
    : query unaryOperator               # unaryExpression
    | query binaryOperator query 	    # binaryExpression
    | LABEL						        # leaf
    | '(' query ')'		 		        # parenthesis
    ;

unaryOperator
    : ( KLEENE_STAR | PLUS ) ;

binaryOperator
    : ( CONJUNCTION | UNION ) ;

// Lexer rules

LABEL
    : CHARS+ ;

KLEENE_STAR
    : '*' ;

PLUS
    : '+' ;

CONJUNCTION
    : '/' ;

UNION
    : '|' ;

CHARS
    : 'A'..'Z'
    | 'a'..'z'
    | '0'
    | [1-9]
    | '\u00C0'..'\u00D6'
    | '\u00D8'..'\u00F6'
    | '\u00F8'..'\u02FF'
    | '\u0370'..'\u037D'
    | '\u037F'..'\u1FFF'
    | '\u200C'..'\u200D'
    | '\u2070'..'\u218F'
    | '\u2C00'..'\u2FEF'
    | '\u3001'..'\uD7FF'
    | '\uF900'..'\uFDCF'
    | '\uFDF0'..'\uFFFD'
    ;

WHITESPACE
    : ( '\t' | ' ' | '\r' | '\n'| '\u000C' )+ -> skip ;