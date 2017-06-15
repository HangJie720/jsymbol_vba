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
 * C++14 declaration parser grammar, based on 
 * https://github.com/antlr/grammars-v4/tree/master/cpp
 ******************************************************************************/
parser grammar CppDeclarationParser;

options {
	tokenVocab = CppDeclarationLexer;
} 

/*Basic concepts*/
translationunit
:
	declaration* EOF
;

/*Expressions*/
primaryexpression
:
	literal
	| This
	| '(' ignoreInsideParens ')'
	| idexpression
	| lambdaexpression
;

idexpression
:
	unqualifiedid
	| qualifiedid
;

unqualifiedid
:
	Identifier
	| operatorfunctionid
	| conversionfunctionid
	| literaloperatorid
	| '~' classname
	| '~' decltypespecifier
	| templateid
;

qualifiedid
:
	nestednamespecifier Template? unqualifiedid
;

nestednamespecifier
:
	'::'
	| typename '::'
	| namespacename '::'
	| decltypespecifier '::'
	| nestednamespecifier Identifier '::'
	| nestednamespecifier Template? simpletemplateid '::'
;

lambdaexpression
:
	lambdaintroducer lambdadeclarator? compoundstatement
;

lambdaintroducer
:
	'[' lambdacapture? ']'
;

lambdacapture
:
	capturedefault
	| capturelist
	| capturedefault ',' capturelist
;

capturedefault
:
	'&'
	| '='
;

capturelist
:
	capture '...'?
	| capturelist ',' capture '...'?
;

capture
:
	simplecapture
	| initcapture
;

simplecapture
:
	Identifier
	| '&' Identifier
	| This
;

initcapture
:
	Identifier initializer
	| '&' Identifier initializer
;

lambdadeclarator
:
	'(' ignoreInsideParens ')' Mutable? exceptionspecification?
	attributespecifierseq? trailingreturntype?
;

postfixexpression
:
	primaryexpression
	| postfixexpression '[' ignoreInsideBrackets ']'
	| postfixexpression '(' ignoreInsideParens ')'
	| simpletypespecifier bracedinitlist
	| typenamespecifier bracedinitlist
	| postfixexpression '.' Template? idexpression
	| postfixexpression '->' Template? idexpression
	| postfixexpression '.' pseudodestructorname
	| postfixexpression '->' pseudodestructorname
	| postfixexpression '++'
	| postfixexpression '--'
	| Dynamic_cast '<' typeid '>' '(' ignoreInsideParens ')'
	| Static_cast '<' typeid '>' '(' ignoreInsideParens ')'
	| Reinterpret_cast '<' typeid '>' '(' ignoreInsideParens ')'
	| Const_cast '<' typeid '>' '(' ignoreInsideParens ')'
	| Typeid '(' ignoreInsideParens ')'
;

expressionlist
:
	initializerlist
;

pseudodestructorname
:
	nestednamespecifier? typename '::' '~' typename
	| nestednamespecifier Template simpletemplateid '::' '~' typename
	| nestednamespecifier? '~' typename
	| '~' decltypespecifier
;

unaryexpression
:
	postfixexpression
	| '++' castexpression
	| '--' castexpression
	| unaryoperator castexpression
	| Sizeof unaryexpression
	| Sizeof '(' typeid ')'
	| Sizeof '...' '(' Identifier ')'
	| Alignof '(' typeid ')'
	| noexceptexpression
	| newexpression
	| deleteexpression
;

unaryoperator
:
	'|'
	| '*'
	| '&'
	| '+'
	| '!'
	| '~'
	| '-'
;

newexpression
:
	'::'? New newplacement? newtypeid newinitializer?
	| '::'? New newplacement? '(' typeid ')' newinitializer?
;

newplacement
:
	'(' ignoreInsideParens ')'
;

newtypeid
:
	typespecifierseq newdeclarator?
;

newdeclarator
:
	ptroperator newdeclarator?
	| noptrnewdeclarator
;

noptrnewdeclarator
:
	'[' expression ']' attributespecifierseq?
	| noptrnewdeclarator '[' constantexpression ']' attributespecifierseq?
;

newinitializer
:
	'(' ignoreInsideParens ')'
	| bracedinitlist
;

deleteexpression
:
	'::'? Delete castexpression
	| '::'? Delete '[' ']' castexpression
;

noexceptexpression
:
	Noexcept '(' expression ')'
;

castexpression
:
	unaryexpression
	| '(' typeid ')' castexpression
;

pmexpression
:
	castexpression
	| pmexpression '.*' castexpression
	| pmexpression '->*' castexpression
;

multiplicativeexpression
:
	pmexpression
	| multiplicativeexpression '*' pmexpression
	| multiplicativeexpression '/' pmexpression
	| multiplicativeexpression '%' pmexpression
;

additiveexpression
:
	multiplicativeexpression
	| additiveexpression '+' multiplicativeexpression
	| additiveexpression '-' multiplicativeexpression
;

shiftexpression
:
	additiveexpression
	| shiftexpression '<<' additiveexpression
	| shiftexpression rightShift additiveexpression
;

rightShift
:
//'>>'
	Greater Greater
;

relationalexpression
:
	shiftexpression
	| relationalexpression '<' shiftexpression
	| relationalexpression '>' shiftexpression
	| relationalexpression '<=' shiftexpression
	| relationalexpression '>=' shiftexpression
;

equalityexpression
:
	relationalexpression
	| equalityexpression '==' relationalexpression
	| equalityexpression '!=' relationalexpression
;

andexpression
:
	equalityexpression
	| andexpression '&' equalityexpression
;

exclusiveorexpression
:
	andexpression
	| exclusiveorexpression '^' andexpression
;

inclusiveorexpression
:
	exclusiveorexpression
	| inclusiveorexpression '|' exclusiveorexpression
;

logicalandexpression
:
	inclusiveorexpression
	| logicalandexpression '&&' inclusiveorexpression
;

logicalorexpression
:
	logicalandexpression
	| logicalorexpression '||' logicalandexpression
;

conditionalexpression
:
	logicalorexpression
	| logicalorexpression '?' expression ':' assignmentexpression
;

assignmentexpression
:
	conditionalexpression
	| logicalorexpression assignmentoperator initializerclause
	| throwexpression
;

assignmentoperator
:
	'='
	| '*='
	| '/='
	| '%='
	| '+='
	| '-='
	| rightShiftAssign
	| '<<='
	| '&='
	| '^='
	| '|='
;

rightShiftAssign
:
//'>>='
	Greater Greater Assign
;

expression
:
	assignmentexpression
	| expression ',' assignmentexpression
;

constantexpression
:
	conditionalexpression
;
/*Statements*/
statement
:
	labeledstatement
	| attributespecifierseq? expressionstatement
	| attributespecifierseq? compoundstatement
	| attributespecifierseq? selectionstatement
	| attributespecifierseq? iterationstatement
	| attributespecifierseq? jumpstatement
	| declarationstatement
	| attributespecifierseq? tryblock
;

labeledstatement
:
	attributespecifierseq? Identifier ':' statement
	| attributespecifierseq? Case constantexpression ':' statement
	| attributespecifierseq? Default ':' statement
;

expressionstatement
:
	expression? ';'
;

compoundstatement
:
	'{' ignoreInsideBraces '}'
;

selectionstatement
:
	If '(' ignoreInsideParens ')' statement
	| If '(' ignoreInsideParens ')' statement Else statement
	| Switch '(' ignoreInsideParens ')' statement
;

condition
:
	expression
	| attributespecifierseq? declspecifierseq declarator '=' initializerclause
	| attributespecifierseq? declspecifierseq declarator bracedinitlist
;

iterationstatement
:
	While '(' ignoreInsideParens ')' statement
	| Do statement While '(' ignoreInsideParens ')' ';'
	| For '(' ignoreInsideParens ')' statement
	| For '(' ignoreInsideParens ')' statement
;

forinitstatement
:
	expressionstatement
	| simpledeclaration
;

forrangedeclaration
:
	attributespecifierseq? declspecifierseq declarator
;

forrangeinitializer
:
	expression
	| bracedinitlist
;

jumpstatement
:
	Break ';'
	| Continue ';'
	| Return expression? ';'
	| Return bracedinitlist ';'
	| Goto Identifier ';'
;

declarationstatement
:
	blockdeclaration
;

declaration
:
	blockdeclaration
	| functiondefinition
	| templatedeclaration
	| explicitinstantiation
	| explicitspecialization
	| linkagespecification
	| namespacedefinition
	| emptydeclaration
	| attributedeclaration
;

blockdeclaration
:
	simpledeclaration
	| asmdefinition
	| namespacealiasdefinition
	| usingdeclaration
	| usingdirective
	| static_assertdeclaration
	| aliasdeclaration
	| opaqueenumdeclaration
;

aliasdeclaration
:
	Using Identifier attributespecifierseq? '=' typeid ';'
;

simpledeclaration
:
	declspecifierseq? initdeclaratorlist? ';'
	| attributespecifierseq declspecifierseq? initdeclaratorlist ';'
;

static_assertdeclaration
:
	Static_assert '(' ignoreInsideParens ')' ';'
;

emptydeclaration
:
	';'
;

attributedeclaration
:
	attributespecifierseq ';'
;

declspecifier
:
	storageclassspecifier
	| typespecifier
	| functionspecifier
	| Friend
	| Typedef
	| Constexpr
;

declspecifierseq
:
	declspecifier attributespecifierseq?
	| declspecifier declspecifierseq
;

storageclassspecifier
:
	Register
	| Static
	| Thread_local
	| Extern
	| Mutable
;

functionspecifier
:
	Inline
	| Virtual
	| Explicit
;

typedefname
:
	Identifier
;

typespecifier
:
	trailingtypespecifier
	| classspecifier
	| enumspecifier
;

trailingtypespecifier
:
	simpletypespecifier
	| elaboratedtypespecifier
	| typenamespecifier
	| cvqualifier
;

typespecifierseq
:
	typespecifier attributespecifierseq?
	| typespecifier typespecifierseq
;

trailingtypespecifierseq
:
	trailingtypespecifier attributespecifierseq?
	| trailingtypespecifier trailingtypespecifierseq
;

simpletypespecifier
:
	nestednamespecifier? typename
	| nestednamespecifier Template simpletemplateid
	| Char
	| Char16
	| Char32
	| Wchar
	| Bool
	| Short
	| Int
	| Long
	| Signed
	| Unsigned
	| Float
	| Double
	| Void
	| Auto
	| decltypespecifier
;

typename
:
	classname
	| enumname
	| typedefname
	| simpletemplateid
;

decltypespecifier
:
	Decltype '(' ignoreInsideParens ')'
	| Decltype '(' Auto ')'
;

elaboratedtypespecifier
:
	classkey attributespecifierseq? nestednamespecifier? Identifier
	| classkey simpletemplateid
	| classkey nestednamespecifier Template? simpletemplateid
	| Enum nestednamespecifier? Identifier
;

enumname
:
	Identifier
;

enumspecifier
:
	enumhead '{' enumeratorlist? '}'
	| enumhead '{' enumeratorlist ',' '}'
;

enumhead
:
	enumkey attributespecifierseq? Identifier? enumbase?
	| enumkey attributespecifierseq? nestednamespecifier Identifier enumbase?
;

opaqueenumdeclaration
:
	enumkey attributespecifierseq? Identifier enumbase? ';'
;

enumkey
:
	Enum
	| Enum Class
	| Enum Struct
;

enumbase
:
	':' typespecifierseq
;

enumeratorlist
:
	enumeratordefinition
	| enumeratorlist ',' enumeratordefinition
;

enumeratordefinition
:
	enumerator
	| enumerator '=' constantexpression
;

enumerator
:
	Identifier
;

namespacename
:
	originalnamespacename
	| namespacealias
;

originalnamespacename
:
	Identifier
;

namespacedefinition
:
	namednamespacedefinition
	| unnamednamespacedefinition
;

namednamespacedefinition
:
	originalnamespacedefinition
	| extensionnamespacedefinition
;

originalnamespacedefinition
:
	Inline? Namespace Identifier '{' namespacebody '}'
;

extensionnamespacedefinition
:
	Inline? Namespace originalnamespacename '{' namespacebody '}'
;

unnamednamespacedefinition
:
	Inline? Namespace '{' namespacebody '}'
;

namespacebody
:
	declaration*
;

namespacealias
:
	Identifier
;

namespacealiasdefinition
:
	Namespace Identifier '=' qualifiednamespacespecifier ';'
;

qualifiednamespacespecifier
:
	nestednamespecifier? namespacename
;

usingdeclaration
:
	Using Typename? nestednamespecifier unqualifiedid ';'
	| Using '::' unqualifiedid ';'
;

usingdirective
:
	attributespecifierseq? Using Namespace nestednamespecifier? namespacename ';'
;

asmdefinition
:
	Asm '(' Stringliteral ')' ';'
;

linkagespecification
:
	Extern Stringliteral '{' declaration* '}'
	| Extern Stringliteral declaration
;

attributespecifierseq
:
	attributespecifier
	| attributespecifierseq attributespecifier
;

attributespecifier
:
	'[' '[' attributelist ']' ']'
	| alignmentspecifier
;

alignmentspecifier
:
	Alignas '(' typeid '...'? ')'
	| Alignas '(' ignoreInsideParens ')'
;

attributelist
:
	attribute?
	| attributelist ',' attribute?
	| attribute '...'
	| attributelist ',' attribute '...'
;

attribute
:
	attributetoken attributeargumentclause?
;

attributetoken
:
	Identifier
	| attributescopedtoken
;

attributescopedtoken
:
	attributenamespace '::' Identifier
;

attributenamespace
:
	Identifier
;

attributeargumentclause
:
	'(' ignoreInsideParens ')'
;

balancedtokenseq
:
	balancedtoken?
	| balancedtokenseq balancedtoken
;

balancedtoken
:
	'(' ignoreInsideParens ')'
	| '[' balancedtokenseq ']'
	| '{' balancedtokenseq '}'
	/*any token other than a parenthesis , a bracket , or a brace*/
;

/*Declarators*/
initdeclaratorlist
:
	initdeclarator
	| initdeclaratorlist ',' initdeclarator
;

initdeclarator
:
	declarator initializer?
;

declarator
:
	ptrdeclarator
	| noptrdeclarator parametersandqualifiers trailingreturntype
;

ptrdeclarator
:
	noptrdeclarator
	| ptroperator ptrdeclarator
;

noptrdeclarator
:
	declaratorid attributespecifierseq?
	| noptrdeclarator parametersandqualifiers
	| noptrdeclarator '[' ignoreInsideBrackets ']' attributespecifierseq?
	| '(' ignoreInsideParens ')'
;

parametersandqualifiers
:
	'(' ignoreInsideParens ')' cvqualifierseq? refqualifier?
	exceptionspecification? attributespecifierseq?
;

trailingreturntype
:
	'->' trailingtypespecifierseq abstractdeclarator?
;

ptroperator
:
	'*' attributespecifierseq? cvqualifierseq?
	| '&' attributespecifierseq?
	| '&&' attributespecifierseq?
	| nestednamespecifier '*' attributespecifierseq? cvqualifierseq?
;

cvqualifierseq
:
	cvqualifier cvqualifierseq?
;

cvqualifier
:
	Const
	| Volatile
;

refqualifier
:
	'&'
	| '&&'
;

declaratorid
:
	'...'? idexpression
;

typeid
:
	typespecifierseq abstractdeclarator?
;

abstractdeclarator
:
	ptrabstractdeclarator
	| noptrabstractdeclarator? parametersandqualifiers trailingreturntype
	| abstractpackdeclarator
;

ptrabstractdeclarator
:
	noptrabstractdeclarator
	| ptroperator ptrabstractdeclarator?
;

noptrabstractdeclarator
:
	noptrabstractdeclarator parametersandqualifiers
	| parametersandqualifiers
	| noptrabstractdeclarator '[' constantexpression? ']' attributespecifierseq?
	| '[' constantexpression? ']' attributespecifierseq?
	| '(' ignoreInsideParens ')'
;

abstractpackdeclarator
:
	noptrabstractpackdeclarator
	| ptroperator abstractpackdeclarator
;

noptrabstractpackdeclarator
:
	noptrabstractpackdeclarator parametersandqualifiers
	| noptrabstractpackdeclarator '[' constantexpression? ']'
	attributespecifierseq?
	| '...'
;

parameterdeclarationclause
:
	parameterdeclarationlist? '...'?
	| parameterdeclarationlist ',' '...'
;

parameterdeclarationlist
:
	parameterdeclaration
	| parameterdeclarationlist ',' parameterdeclaration
;

parameterdeclaration
:
	attributespecifierseq? declspecifierseq declarator
	| attributespecifierseq? declspecifierseq declarator '=' initializerclause
	| attributespecifierseq? declspecifierseq abstractdeclarator?
	| attributespecifierseq? declspecifierseq abstractdeclarator? '='
	initializerclause
;

functiondefinition
:
	attributespecifierseq? declspecifierseq? declarator virtspecifierseq?
	functionbody
;

functionbody
:
	ctorinitializer? compoundstatement
	| functiontryblock
	| '=' Default ';'
	| '=' Delete ';'
;

initializer
:
	braceorequalinitializer
	| '(' ignoreInsideParens ')'
;

braceorequalinitializer
:
	'=' initializerclause
	| bracedinitlist
;

initializerclause
:
	assignmentexpression
	| bracedinitlist
;

initializerlist
:
	initializerclause '...'?
	| initializerlist ',' initializerclause '...'?
;

bracedinitlist
:
	'{' ignoreInsideBraces '}'
;

/*Classes*/
classname
:
	Identifier
	| simpletemplateid
;

classspecifier
:
	classhead '{' memberspecification? '}'
;

classhead
:
	classkey attributespecifierseq? classheadname classvirtspecifier? baseclause?
	| classkey attributespecifierseq? baseclause?
;

classheadname
:
	nestednamespecifier? classname
;

classvirtspecifier
:
	Final
;

classkey
:
	Class
	| Struct
	| Union
;

memberspecification
:
	memberdeclaration memberspecification?
	| accessspecifier ':' memberspecification?
;

memberdeclaration
:
	attributespecifierseq? declspecifierseq? memberdeclaratorlist? ';'
	| functiondefinition
	| usingdeclaration
	| static_assertdeclaration
	| templatedeclaration
	| aliasdeclaration
	| emptydeclaration
;

memberdeclaratorlist
:
	memberdeclarator
	| memberdeclaratorlist ',' memberdeclarator
;

memberdeclarator
:
	declarator virtspecifierseq? purespecifier?
	| declarator braceorequalinitializer?
	| Identifier? attributespecifierseq? ':' constantexpression
;

virtspecifierseq
:
	virtspecifier
	| virtspecifierseq virtspecifier
;

virtspecifier
:
	Override
	| Final
;

/*
purespecifier:
	'=' '0'//Conflicts with the lexer
 ;
 */
purespecifier
:
	Assign val = Octalliteral
	{if($val.text.compareTo("0")!=0) throw new InputMismatchException(this);}

;

/*Derived classes*/
baseclause
:
	':' basespecifierlist
;

basespecifierlist
:
	basespecifier '...'?
	| basespecifierlist ',' basespecifier '...'?
;

basespecifier
:
	attributespecifierseq? basetypespecifier
	| attributespecifierseq? Virtual accessspecifier? basetypespecifier
	| attributespecifierseq? accessspecifier Virtual? basetypespecifier
;

classordecltype
:
	nestednamespecifier? classname
	| decltypespecifier
;

basetypespecifier
:
	classordecltype
;

accessspecifier
:
	Private
	| Protected
	| Public
;

/*Special member functions*/
conversionfunctionid
:
	Operator conversiontypeid
;

conversiontypeid
:
	typespecifierseq conversiondeclarator?
;

conversiondeclarator
:
	ptroperator conversiondeclarator?
;

ctorinitializer
:
	':' meminitializerlist
;

meminitializerlist
:
	meminitializer '...'?
	| meminitializer '...'? ',' meminitializerlist
;

meminitializer
:
	meminitializerid '(' ignoreInsideParens ')'
	| meminitializerid bracedinitlist
;

meminitializerid
:
	classordecltype
	| Identifier
;

booleanliteral
:
	False
	| True
;

pointerliteral
:
	Nullptr
;

userdefinedliteral
:
	Userdefinedintegerliteral
	| Userdefinedfloatingliteral
	| Userdefinedstringliteral
	| Userdefinedcharacterliteral
;

literal
:
	Integerliteral
	| Characterliteral
	| Floatingliteral
	| Stringliteral
	| booleanliteral
	| pointerliteral
	| userdefinedliteral
;

operator
:
	New
	| Delete
	| New '[' ']'
	| Delete '[' ']'
	| '+'
	| '-'
	| '*'
	| '/'
	| '%'
	| '^'
	| '&'
	| '|'
	| '~'
	| '!'
	| '='
	| '<'
	| '>'
	| '+='
	| '-='
	| '*='
	| '/='
	| '%='
	| '^='
	| '&='
	| '|='
	| '<<'
	| rightShift
	| rightShiftAssign
	| '<<='
	| '=='
	| '!='
	| '<='
	| '>='
	| '&&'
	| '||'
	| '++'
	| '--'
	| ','
	| '->*'
	| '->'
	| '(' ')'
	| '[' ']'
;

/*Overloading*/
operatorfunctionid
:
	Operator operator
;

literaloperatorid
:
	Operator Stringliteral Identifier
	| Operator Userdefinedstringliteral
;

/*Templates*/
templatedeclaration
:
	Template '<' templateparameterlist '>' declaration
;

templateparameterlist
:
	templateparameter
	| templateparameterlist ',' templateparameter
;

templateparameter
:
	typeparameter
	| parameterdeclaration
;

typeparameter
:
	Class '...'? Identifier?
	| Class Identifier? '=' typeid
	| Typename '...'? Identifier?
	| Typename Identifier? '=' typeid
	| Template '<' templateparameterlist '>' Class '...'? Identifier?
	| Template '<' templateparameterlist '>' Class Identifier? '=' idexpression
;

simpletemplateid
:
	templatename '<' templateargumentlist? '>'
;

templateid
:
	simpletemplateid
	| operatorfunctionid '<' templateargumentlist? '>'
	| literaloperatorid '<' templateargumentlist? '>'
;

templatename
:
	Identifier
;

templateargumentlist
:
	templateargument '...'?
	| templateargumentlist ',' templateargument '...'?
;

templateargument
:
	typeid
	| constantexpression
	| idexpression
;

typenamespecifier
:
	Typename nestednamespecifier Identifier
	| Typename nestednamespecifier Template? simpletemplateid
;

explicitinstantiation
:
	Extern? Template declaration
;

explicitspecialization
:
	Template '<' '>' declaration
;

/*Exception handling*/
tryblock
:
	Try compoundstatement handlerseq
;

functiontryblock
:
	Try ctorinitializer? compoundstatement handlerseq
;

handlerseq
:
	handler handlerseq?
;

handler
:
	Catch '(' ignoreInsideParens ')' compoundstatement
;

exceptiondeclaration
:
	attributespecifierseq? typespecifierseq declarator
	| attributespecifierseq? typespecifierseq abstractdeclarator?
	| '...'
;

throwexpression
:
	Throw assignmentexpression?
;

exceptionspecification
:
	dynamicexceptionspecification
	| noexceptspecification
;

dynamicexceptionspecification
:
	Throw '(' ignoreInsideParens ')'
;

typeidlist
:
	typeid '...'?
	| typeidlist ',' typeid '...'?
;

noexceptspecification
:
	Noexcept '(' ignoreInsideParens ')'
	| Noexcept
;

ignoreInsideParens
	:	(~('('|')') | '(' ignoreInsideParens ')')*
	;

ignoreInsideBraces
	:	(~('{'|'}') | '{' ignoreInsideBraces '}')*
	;
	  
ignoreInsideBrackets
	:	(~('['|']') | '[' ignoreInsideBrackets ']')*
	;

