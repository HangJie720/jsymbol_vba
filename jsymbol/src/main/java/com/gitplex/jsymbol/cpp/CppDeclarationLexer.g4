/*******************************************************************************
 * The MIT License (MIT)
 * 
 * Copyright (c) 2015 Camilo Sanchez (Camiloasc1)
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 ******************************************************************************/

/*******************************************************************************
 * C++14 lexer grammar, based on 
 * https://github.com/antlr/grammars-v4/tree/master/cpp
 ******************************************************************************/
 
lexer grammar CppDeclarationLexer;

@lexer::members {
	public static int MACRO_DEFINITION_CHANNEL = 2;
}

//TODO: Add cpp raw string support

/*Keywords*/
Alignas
:
	'alignas'
;

Alignof
:
	'alignof'
;

Asm
:
	'asm'
;

Auto
:
	'auto'
;

Bool
:
	'bool'
;

Break
:
	'break'
;

Case
:
	'case'
;

Catch
:
	'catch'
;

Char
:
	'char'
;

Char16
:
	'char16_t'
;

Char32
:
	'char32_t'
;

Class
:
	'class'
;

Const
:
	'const'
;

Constexpr
:
	'constexpr'
;

Const_cast
:
	'const_cast'
;

Continue
:
	'continue'
;

Decltype
:
	'decltype'
;

Default
:
	'default'
;

Delete
:
	'delete'
;

Do
:
	'do'
;

Double
:
	'double'
;

Dynamic_cast
:
	'dynamic_cast'
;

Else
:
	'else'
;

Enum
:
	'enum'
;

Explicit
:
	'explicit'
;

Export
:
	'export'
;

Extern
:
	'extern'
;

False
:
	'false'
;

Final
:
	'final'
;

Float
:
	'float'
;

For
:
	'for'
;

Friend
:
	'friend'
;

Goto
:
	'goto'
;

If
:
	'if'
;

Inline
:
	'inline'
;

Int
:
	'int'
;

Long
:
	'long'
;

Mutable
:
	'mutable'
;

Namespace
:
	'namespace'
;

New
:
	'new'
;

Noexcept
:
	'noexcept'
;

Nullptr
:
	'nullptr'
;

Operator
:
	'operator'
;

Override
:
	'override'
;

Private
:
	'private'
;

Protected
:
	'protected'
;

Public
:
	'public'
;

Register
:
	'register'
;

Reinterpret_cast
:
	'reinterpret_cast'
;

Return
:
	'return'
;

Short
:
	'short'
;

Signed
:
	'signed'
;

Sizeof
:
	'sizeof'
;

Static
:
	'static'
;

Static_assert
:
	'static_assert'
;

Static_cast
:
	'static_cast'
;

Struct
:
	'struct'
;

Switch
:
	'switch'
;

Template
:
	'template'
;

This
:
	'this'
;

Thread_local
:
	'thread_local'
;

Throw
:
	'throw'
;

True
:
	'true'
;

Try
:
	'try'
;

Typedef
:
	'typedef'
;

Typeid
:
	'typeid'
;

Typename
:
	'typename'
;

Union
:
	'union'
;

Unsigned
:
	'unsigned'
;

Using
:
	'using'
;

Virtual
:
	'virtual'
;

Void
:
	'void'
;

Volatile
:
	'volatile'
;

Wchar
:
	'wchar_t'
;

While
:
	'while'
;

/*Operators*/
LeftParen
:
	'('
;

RightParen
:
	')'
;

LeftBracket
:
	'['
;

RightBracket
:
	']'
;

LeftBrace
:
	'{'
;

RightBrace
:
	'}'
;

Plus
:
	'+'
;

Minus
:
	'-'
;

Star
:
	'*'
;

Div
:
	'/'
;

Mod
:
	'%'
;

Caret
:
	'^'
;

And
:
	'&'
;

Or
:
	'|'
;

Tilde
:
	'~'
;

Not
:
	'!'
;

Assign
:
	'='
;

Less
:
	'<'
;

Greater
:
	'>'
;

PlusAssign
:
	'+='
;

MinusAssign
:
	'-='
;

StarAssign
:
	'*='
;

DivAssign
:
	'/='
;

ModAssign
:
	'%='
;

XorAssign
:
	'^='
;

AndAssign
:
	'&='
;

OrAssign
:
	'|='
;

LeftShift
:
	'<<'
;

LeftShiftAssign
:
	'<<='
;

Equal
:
	'=='
;

NotEqual
:
	'!='
;

LessEqual
:
	'<='
;

GreaterEqual
:
	'>='
;

AndAnd
:
	'&&'
;

OrOr
:
	'||'
;

PlusPlus
:
	'++'
;

MinusMinus
:
	'--'
;

Comma
:
	','
;

ArrowStar
:
	'->*'
;

Arrow
:
	'->'
;

Question
:
	'?'
;

Colon
:
	':'
;

Doublecolon
:
	'::'
;

Semi
:
	';'
;

Dot
:
	'.'
;

DotStar
:
	'.*'
;

Ellipsis
:
	'...'
;

/*Lexer*/
fragment
Hexquad
:
	HEXADECIMALDIGIT HEXADECIMALDIGIT HEXADECIMALDIGIT HEXADECIMALDIGIT
;

fragment
Universalcharactername
:
	'\\u' Hexquad
	| '\\U' Hexquad Hexquad
;

Identifier
:
/*
	Identifiernondigit
	| Identifier Identifiernondigit
	| Identifier DIGIT
	*/
	Identifiernondigit
	(
		Identifiernondigit
		| DIGIT
	)*
;

fragment
Identifiernondigit
:
	NONDIGIT
	| Universalcharactername
	/* other implementation defined characters*/
;

fragment
NONDIGIT
:
	[a-zA-Z_]
;

fragment
DIGIT
:
	[0-9]
;

Integerliteral
:
	Decimalliteral Integersuffix?
	| Octalliteral Integersuffix?
	| Hexadecimalliteral Integersuffix?
	| Binaryliteral Integersuffix?
;

Decimalliteral
:
	NONZERODIGIT
	(
		'\''? DIGIT
	)*
;

Octalliteral
:
	'0'
	(
		'\''? OCTALDIGIT
	)*
;

Hexadecimalliteral
:
	(
		'0x'
		| '0X'
	) HEXADECIMALDIGIT
	(
		'\''? HEXADECIMALDIGIT
	)*
;

Binaryliteral
:
	(
		'0b'
		| '0B'
	) BINARYDIGIT
	(
		'\''? BINARYDIGIT
	)*
;

fragment
NONZERODIGIT
:
	[1-9]
;

fragment
OCTALDIGIT
:
	[0-7]
;

fragment
HEXADECIMALDIGIT
:
	[0-9a-fA-F]
;

fragment
BINARYDIGIT
:
	[01]
;

Integersuffix
:
	Unsignedsuffix Longsuffix?
	| Unsignedsuffix Longlongsuffix?
	| Longsuffix Unsignedsuffix?
	| Longlongsuffix Unsignedsuffix?
;

fragment
Unsignedsuffix
:
	[uU]
;

fragment
Longsuffix
:
	[lL]
;

fragment
Longlongsuffix
:
	'll'
	| 'LL'
;

Characterliteral
:
	'\'' Cchar+ '\''
	| 'u' '\'' Cchar+ '\''
	| 'U' '\'' Cchar+ '\''
	| 'L' '\'' Cchar+ '\''
;

fragment
Cchar
:
	~['\\\r\n]
	| Escapesequence
	| Universalcharactername
;

fragment
Escapesequence
:
	Simpleescapesequence
	| Octalescapesequence
	| Hexadecimalescapesequence
;

fragment
Simpleescapesequence
:
	'\\\''
	| '\\"'
	| '\\?'
	| '\\\\'
	| '\\a'
	| '\\b'
	| '\\f'
	| '\\n'
	| '\\r'
	| '\\t'
	| '\\v'
;

fragment
Octalescapesequence
:
	'\\' OCTALDIGIT
	| '\\' OCTALDIGIT OCTALDIGIT
	| '\\' OCTALDIGIT OCTALDIGIT OCTALDIGIT
;

fragment
Hexadecimalescapesequence
:
	'\\x' HEXADECIMALDIGIT+
;

Floatingliteral
:
	Fractionalconstant Exponentpart? Floatingsuffix?
	| Digitsequence Exponentpart Floatingsuffix?
;

fragment
Fractionalconstant
:
	Digitsequence? '.' Digitsequence
	| Digitsequence '.'
;

fragment
Exponentpart
:
	'e' SIGN? Digitsequence
	| 'E' SIGN? Digitsequence
;

fragment
SIGN
:
	[+-]
;

fragment
Digitsequence
:
	DIGIT
	(
		'\''? DIGIT
	)*
;

fragment
Floatingsuffix
:
	[flFL]
;

Stringliteral
:
	Encodingprefix? '"' Schar* '"'
	| Encodingprefix? 'R' Rawstring
;

fragment
Encodingprefix
:
	'u8'
	| 'u'
	| 'U'
	| 'L'
;

fragment
Schar
:
	~["\\\r\n]
	| Escapesequence
	| Universalcharactername
;

fragment
Rawstring /* '"' dcharsequence? '(' rcharsequence? ')' dcharsequence? '"' */
:
	'"' .*? '(' .*? ')' .*? '"'
;

Userdefinedintegerliteral
:
	Decimalliteral Udsuffix
	| Octalliteral Udsuffix
	| Hexadecimalliteral Udsuffix
	| Binaryliteral Udsuffix
;

Userdefinedfloatingliteral
:
	Fractionalconstant Exponentpart? Udsuffix
	| Digitsequence Exponentpart Udsuffix
;

Userdefinedstringliteral
:
	Stringliteral Udsuffix
;

Userdefinedcharacterliteral
:
	Characterliteral Udsuffix
;

fragment
Udsuffix
:
	Identifier
;

Whitespace
:
	[ \t]+ -> skip
;

Newline
:
	(
		'\r' '\n'?
		| '\n'
	) -> skip
;

BlockComment
:
	'/*' .*? '*/' -> skip
;

LineComment
:
	'//' ~[\r\n]* -> skip
;


MacroDefinitionBegin
    :   '#' Whitespace* 'define' Whitespace*
    	->	pushMode(MACRO_DEFINITION), skip
    ;
    
PreprocessDirectiveBegin
	:	'#' Whitespace* Identifier* Whitespace*
		-> pushMode(PREPROCESS_DIRECTIVE), skip
	;        

/*
 * Use lexer mode instead of semantic predicate for better performance
 */
mode PREPROCESS_DIRECTIVE;

PreprocessDirectiveBlockComment
	:	BlockComment
		-> skip
	;
	
PreprocessDirectiveStringLiteral
	:	Stringliteral
		-> skip
	;
	
PreprocessDirectiveLineEscape
	:	'\\' '\r'? '\n'
		-> skip
	;
	
PreprocessDirectiveEnd
	:	('\n' | EOF)
		-> popMode, skip
	;		
	
PreprocessDirectiveBody
	:	.
		->skip
	;	
	
mode MACRO_DEFINITION;

MacroDefinitionName
	:	Identifier
		-> pushMode(MACRO_DEFINITION_BODY), channel(MACRO_DEFINITION_CHANNEL)
	;
	
mode MACRO_DEFINITION_BODY;

MacroDefinitionBlockComment
	:	PreprocessDirectiveBlockComment
		-> skip
	;
	
MacroDefinitionStringLiteral
	:	PreprocessDirectiveStringLiteral
		-> skip
	;
	
MacroDefinitionLineEscape
	:	PreprocessDirectiveLineEscape
		-> skip
	;
	
MacroDefinitionEnd
	:	PreprocessDirectiveEnd
		-> popMode, popMode, skip
	;
	
MacroDefinitionBody
	:	PreprocessDirectiveBody
		-> skip
	;
