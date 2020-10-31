grammar newlan;

@header {
package generated;
}

root:
  (expression SEMICOL)*
;

expression:primaryExpresssion
       | expression bop ='.' expression
       | expression bop=('*'|'/'|'%') expression
       | expression bop=('+'|'-') expression
       | expression ('<' '<' | '>' '>' '>' | '>' '>') expression
       | expression bop=('<=' | '>=' | '>' | '<') expression
       | expression bop='&&' expression
       | expression bop='||' expression
       | expression bop=('==' | '!=') expression
       ;

primaryExpresssion
    : '(' expression ')'
    | atom
    ;

atom: (STRING|INT|DECIMAL|IDENT);

BlockComment
    :   '/*' .*? '*/'
        -> skip
    ;

LineComment
    :   '//' ~[\r\n]*
        -> skip
    ;

NEWLINE            : ['\r\n' | '\r' | '\n']+ ->skip ;
WS                 : [\t ]+ -> skip ;

SEMICOL: ';';

INT             : '0'|[1-9][0-9]* ;
DECIMAL         : [0-9][0-9]* '.' [0-9]+ ;
STRING
    : '"' SCharSequence? '"'
    ;

fragment
SCharSequence
    :   SChar+
    ;

fragment
SChar
    :   ~["\\\r\n]
    |   EscapeSequence
    |   '\\\n'   // Added line
    |   '\\\r\n' // Added line
;
fragment
EscapeSequence
    :   '\\' ['"?abfnrtv\\]
;

IDENT                 : [_]*[A-Za-z0-9_]+ ;
