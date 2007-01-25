header {
/*
 * Copyright (c) 2001 Alexander V. Konstantinou (akonstan@acm.org)
 *
 * Permission to use, copy, modify, distribute and sell this software
 * and its documentation for any purpose is hereby granted without fee,
 * provided that the above copyright notice appear in all copies and
 * that both that copyright notice and this permission notice appear
 * in supporting documentation.  Alexander V. Konstantinou makes no
 * representations about the suitability of this software for any
 * purpose.  It is provided "as is" without express or implied warranty.
 *
 * ANTLR Object Constraint Language (OCL) grammar.  This grammar complies
 * with the UML 1.4 OCL grammar (version 0.1c)
 *
 * Id: ocl.g,v 1.1 2001/05/25 21:32:02 akonstan
 *
 * The latest version can be obtained at:
 *
 *         http://www.cs.columbia.edu/~akonstan/ocl/
 *
 * References:
 *
 *   - http://www.antlr.org/
 *   - http://www.omg.org/uml/
 *   - http://www.klasse.nl/ocl/
 */

 ////////////////////////////////////////////////////////////////////////////

/* CCM Tools : OCL parser
 * Robert Lechner <rlechner@sbox.tugraz.at>
 * copyright (c) 2003, 2004 Salomon Automation
 *
 * $Id: OCL.g,v 1.1 2004/07/20 16:21:23 teiniker Exp $
 *
 *
 * History:
 *
 * - Version 1.0: original release (Alexander V. Konstantinou)
 * - Version 1.1: check for UML 1.5 compliance (Robert Lechner)
 * - Version 1.2: Java Code (Aug. 2003, Robert Lechner)
 *
 *
 * This library is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation; either version 2.1 of the License, or (at
 * your option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public
 * License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation Inc,
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */

package ccmtools.OCL.parser;

import oclmetamodel.*;
}

class OclParser extends Parser;

options {
  k=2;
  exportVocab=OCL;
}


{
////////////////////////////////////////////////////////////////////////////

/**
 * Creates a parse tree from an OCL file.
 * @param fileName  the name of the OCL file
 * @param creator  the parsetree creator
 * @return the parse tree
 * @throws java.io.FileNotFoundException  file access error
 * @throws antlr.ANTLRException  parser error
 */
public static MFile parseFile( String fileName, OclParsetreeCreator creator )
 throws antlr.ANTLRException, java.io.FileNotFoundException
{
	java.io.BufferedInputStream stream = new java.io.BufferedInputStream(
		new java.io.FileInputStream(fileName) );
	OclLexer lexer = new OclLexer( stream );
	OclParser parser = new OclParser( lexer );
	parser.setParsetreeCreator( creator );
	return parser.oclFile();
}

private static OclParser the_parser_;

/**
 * Helper function for reporting error messages.
 */
public static void throwException( String message ) throws OclParserException
{
	Token t;
	String f;
	try
	{
		t = the_parser_.LT(1);
		f = the_parser_.getFilename();
	}
	catch( Exception e )
	{
		throw new OclParserException( message );
	}
	throw new OclParserException( message, f, t.getLine(), t.getColumn() );
}

private OclParsetreeCreator creator_;

/**
 * Sets the parsetree creator.
 */
public void setParsetreeCreator( OclParsetreeCreator creator )
{
	creator_ = creator;
}

private String removeDelimitation( String value )
{
    int len = value.length();
    if( value.indexOf('\'')==0 && value.lastIndexOf('\'')==(len-1) )
    {
        if( len>2 )
        {
            return value.substring( 1, len-1 );
        }
        else
        {
            return "";
        }
    }
    else
    {
        return value;
    }
}

////////////////////////////////////////////////////////////////////////////
}


oclFile returns [MFile p_file]
{
	if( creator_==null )
	{
		throw new OclParserException("no parsetree creator defined");
	}
	the_parser_=this;
	p_file=creator_.createFile();
	String p_name=null;
	MPackage p_pkg=null;
	MContext p_ctxt=null;
}
  : ( "package" p_name=pathName  {p_pkg=creator_.createPackage(p_name);}
       ( p_ctxt=constraint
         {creator_.add(p_pkg,p_ctxt);}
       )*
       "endpackage"
       {creator_.add(p_file,p_pkg);}
    )+
{
	the_parser_=null;
}
  ;

constraint returns [MContext p_ctxt=null]
{
	MDefinition p_def=null;
	MConstraint p_con=null;
}
  : p_ctxt=contextDeclaration
    ( p_def=conDefinition  {creator_.add(p_ctxt,p_def);}
      | p_con=conConstraint  {creator_.add(p_ctxt,p_con);}
    )+
  ;

conDefinition returns [MDefinition p_def=null]
{
	String p_name=null;
	MLetStatement p_let=null;
}
  : "def" (n1:NAME {p_name=n1.getText();})? COLON
    {p_def=creator_.createDefinition(p_name);}
    (
    	p_let=letExpression  {creator_.add(p_def,p_let);}
    )*
  ;

conConstraint returns [MConstraint p_con=null]
{
	String p_st=null, p_name=null;
	MConstraintExpression p_oclexpr=null;
}
  : p_st=stereotype (n1:NAME {p_name=n1.getText();})? COLON p_oclexpr=oclExpression
    {p_con=creator_.createConstraint(p_st,p_name,p_oclexpr);}
  ;

contextDeclaration returns [MContext p_ctxt=null]
  : "context" (p_ctxt=operationContext | p_ctxt=classifierContext);

classifierContext returns [MClassifierContext p_ctxt=null]
  : (n1:NAME COLON n2:NAME)
  	{p_ctxt=creator_.createClassifierContext(n1.getText(),n2.getText());}
  | n3:NAME {p_ctxt=creator_.createClassifierContext(OclConstants.KEYWORD_SELF,n3.getText());}
  ;

operationContext returns [MOperationContext p_ctxt=null]
{
	String p_opName=null;
	MTypeSpecifier p_typeSpec=null;
}
  : n0:NAME DCOLON p_opName=operationName
    {p_ctxt=creator_.createOperationContext(n0.getText(),p_opName);}
    LPAREN
    ( n1:NAME COLON p_typeSpec=typeSpecifier
      {creator_.add(p_ctxt, creator_.createFormalParameter(n1.getText(),p_typeSpec));}
      (COMMA n2:NAME COLON p_typeSpec=typeSpecifier
       {creator_.add(p_ctxt, creator_.createFormalParameter(n2.getText(),p_typeSpec));}
      )*
    )?
    RPAREN
    ( COLON p_typeSpec=typeSpecifier
      {p_ctxt.setReturnType(p_typeSpec);}
    )?
    ;

typeSpecifier returns [MTypeSpecifier p_typeSpec=null]
  : p_typeSpec=simpleTypeSpecifier
  | p_typeSpec=collectionType
  ;

simpleTypeSpecifier returns [MSimpleTypeSpec p_typeSpec=null]
{
	String p_name=null;
}
  : p_name=pathName  {p_typeSpec=creator_.createSimpleTypeSpec(p_name);}
  ;

collectionType returns [MCollectionTypeSpec p_typeSpec=null]
{
	String p_kind=null;
	MSimpleTypeSpec p_type=null;
}
  : p_kind=collectionParameterKind LPAREN p_type=simpleTypeSpecifier RPAREN
    {p_typeSpec=creator_.createCollectionTypeSpec(p_kind,p_type);}
  ;


oclExpression returns [MConstraintExpression p_oclexpr]
{
	p_oclexpr = creator_.createConstraintExpression();
	MLetStatement p_le=null;
	MExpression p_expr=null;
}
  : ( ( p_le=letExpression {creator_.add(p_oclexpr,p_le);} )* "in" )?
    p_expr=expression  {creator_.set(p_oclexpr,p_expr);}
  ;

expression returns [MExpression p_expr=null]
  : p_expr=logicalExpression
  ;

letExpression returns [MLetStatement p_le=null]
{
	MTypeSpecifier p_typeSpec=null;
	MExpression p_expr=null;
}
  : "let" n1:NAME  {p_le=creator_.createLetStatement(n1.getText());}
    (
      LPAREN
      ( n2:NAME COLON p_typeSpec=typeSpecifier
        {creator_.add(p_le, creator_.createFormalParameter(n2.getText(),p_typeSpec));}
        (COMMA n3:NAME COLON p_typeSpec=typeSpecifier
         {creator_.add(p_le, creator_.createFormalParameter(n3.getText(),p_typeSpec));}
        )*
      )?
      RPAREN
    )?
    ( COLON p_typeSpec=typeSpecifier {p_le.setType(p_typeSpec);} )?
    EQUAL p_expr=expression  {p_le.setExpression(p_expr);}
  ;


logicalExpression returns [MExpression p_expr=null]
{
	String p_op=null;
	MExpression p_e2=null;
}
  : p_expr=relationalExpression
    ( p_op=logicalOperator p_e2=relationalExpression
      {p_expr=creator_.createOperationExpression(p_expr,p_op,p_e2);}
    )*
  ;

relationalExpression returns [MExpression p_expr=null]
{
	String p_op=null;
	MExpression p_e2=null;
}
  : p_expr=additiveExpression
    ( p_op=relationalOperator p_e2=additiveExpression
      {p_expr=creator_.createOperationExpression(p_expr,p_op,p_e2);}
    )?
  ;

additiveExpression returns [MExpression p_expr=null]
{
	String p_op=null;
	MExpression p_e2=null;
}
  : p_expr=multiplicativeExpression
    ( p_op=addOperator p_e2=multiplicativeExpression
      {p_expr=creator_.createOperationExpression(p_expr,p_op,p_e2);}
    )*
  ;

multiplicativeExpression returns [MExpression p_expr=null]
{
	String p_op=null;
	MExpression p_e2=null;
}
  : p_expr=unaryExpression
    ( p_op=multiplyOperator p_e2=unaryExpression
      {p_expr=creator_.createOperationExpression(p_expr,p_op,p_e2);}
    )*
  ;

unaryExpression returns [MExpression p_expr=null]
{
	String p_op=null;
}
  : ( p_op=unaryOperator p_expr=postfixExpression )
    {p_expr=creator_.createOperationExpression(null,p_op,p_expr);}
  | p_expr=postfixExpression
  ;

postfixExpression returns [MExpression p_expr=null]
{
	MPropertyCall p_call=null;
}
  : p_expr=primaryExpression
    (
      (DOT p_call=propertyCall)  {p_expr=creator_.createPostfixExpression(p_expr,p_call,false);}
      | (RARROW p_call=propertyCall)  {p_expr=creator_.createPostfixExpression(p_expr,p_call,true);}
    )*
  ;

propertyCall returns [MPropertyCall p_call=null]
{
	String p_name=null;
	boolean p_previous=false;
	MActualParameters p_ap=null;
	MPropertyCallParameters p_pcp=null;
}
  : p_name=pathName (timeExpression {p_previous=true;})?
    {p_call=creator_.createPropertyCall(p_name,p_previous);}
    (p_ap=qualifiers {p_call.setQualifiers(p_ap);})?
    (p_pcp=propertyCallParameters {p_call.setCallParameters(p_pcp);})?
  ;

timeExpression
  : ATSIGN "pre"
  ;

/*propertyCallParameters
  : LPAREN (declarator)? (actualParameterList)? RPAREN
  ;*/
propertyCallParameters returns [MPropertyCallParameters p_pcp=null]
{
	MActualParameters p_ap1=null, p_ap2=null;
	MDeclarator p_decl=null;
}
    : ( LPAREN NAME (COMMA NAME)*
        (COLON simpleTypeSpecifier)?
        (SEMICOL NAME COLON typeSpecifier EQUAL expression)?
        BAR ) =>
          LPAREN p_decl=declarator (p_ap1=actualParameterList)? RPAREN
          {p_pcp=creator_.createPropertyCallParameters(p_decl,p_ap1);}
        | LPAREN (p_ap2=actualParameterList)? RPAREN
          {p_pcp=creator_.createPropertyCallParameters(null,p_ap2);}
  ;

declarator returns [MDeclarator p_decl=null]
{
	MSimpleTypeSpec p_simple=null;
	MTypeSpecifier p_type=null;
	MExpression p_expr=null;
}
  : n1:NAME  {p_decl=creator_.createDeclarator(n1.getText());}
    (COMMA n2:NAME {creator_.add(p_decl,n2.getText());})*
    (COLON p_simple=simpleTypeSpecifier {p_decl.setSimpleType(p_simple);})?
    (SEMICOL n3:NAME COLON p_type=typeSpecifier EQUAL p_expr=expression
     {p_decl.setOptName(n3.getText()); p_decl.setOptType(p_type); p_decl.setOptExpression(p_expr);}
    )?
    BAR
  ;

qualifiers returns [MActualParameters p_ap=null]
  : LBRACK p_ap=actualParameterList RBRACK
  ;

actualParameterList returns [MActualParameters p_ap=null]
{
	MExpression p_expr=null;
}
  : p_expr=expression  {p_ap=creator_.createActualParameters(p_expr);}
    (COMMA p_expr=expression
     {creator_.add(p_ap,p_expr);}
    )*
  ;

/*primaryExpression
  : literalCollection
  | literal
  | propertyCall
  | LPAREN expression RPAREN
  | ifExpression
  ;*/
primaryExpression returns [MExpression p_expr=null]
  : p_expr=literalCollection
  | (   STRING
      | NUMBER
      | "true"
      | "false"
      | (NAME DCOLON NAME (DCOLON NAME)*)
    ) => p_expr=literal
  | p_expr=propertyCall
  | LPAREN p_expr=expression RPAREN
  | p_expr=ifExpression
  ;

ifExpression returns [MIfExpression p_expr0=null]
{
	MExpression p_expr1=null, p_expr2=null, p_expr3=null;
}
  : "if" p_expr1=expression
    "then" p_expr2=expression
    "else" p_expr3=expression
    "endif"
    {p_expr0=creator_.createIfExpression(p_expr1,p_expr2,p_expr3);}
  ;

literal returns [MLiteralExpression p_expr=null]
  : s1:STRING  {p_expr=creator_.createStringLiteral(removeDelimitation(s1.getText()));}
  | n1:NUMBER  {p_expr=creator_.createNumericLiteral(n1.getText());}
  | "true"  {p_expr=creator_.createBooleanLiteral(true);}
  | "false"  {p_expr=creator_.createBooleanLiteral(false);}
  | p_expr=enumLiteral
  ;

enumLiteral returns [MEnumLiteral p_enum=null]
  : n1:NAME DCOLON n2:NAME
    {p_enum=creator_.createEnumLiteral(n1.getText(),n2.getText());}
    (
      DCOLON n3:NAME
      {creator_.add(p_enum,n3.getText());}
    )*
  ;


literalCollection returns [MCollectionLiteral p_expr=null]
{
	String p_kind=null;
	MCollectionPart p_item=null;
}
  : p_kind=collectionLiteralKind LCURLY  {p_expr=creator_.createCollectionLiteral(p_kind);}
    ( p_item=collectionItem  {creator_.add(p_expr,p_item);}
      ( COMMA p_item=collectionItem
        {creator_.add(p_expr,p_item);}
      )*
    )?
    RCURLY
  ;

collectionItem returns [MCollectionPart p_item=null]
{
	MExpression p_expr1=null, p_expr2=null;
	boolean p_range=false;
}
  : p_expr1=expression (DOTDOT p_expr2=expression {p_range=true;})?
  {
	if( p_range )
	{
		p_item=creator_.createCollectionRange(p_expr1,p_expr2);
	}
	else
	{
		p_item=creator_.createCollectionItem(p_expr1);
	}
  }
  ;


pathName returns [String name=null]
  : n1:NAME {name=n1.getText();} (DCOLON n2:NAME {name+=(OclConstants.PATHNAME_SEPARATOR+n2.getText());})*
  ;


stereotype returns [String kind=null]
    : "pre"  {kind=OclConstants.STEREOTYPE_PRECONDITION;}
    | "post"  {kind=OclConstants.STEREOTYPE_POSTCONDITION;}
    | "inv"  {kind=OclConstants.STEREOTYPE_INVARIANT;}
    ;

collectionParameterKind returns [String kind=null]
  : "Set"  {kind=OclConstants.COLLECTIONKIND_SET;}
  | "Bag"  {kind=OclConstants.COLLECTIONKIND_BAG;}
  | "Sequence"  {kind=OclConstants.COLLECTIONKIND_SEQUENCE;}
  | "Collection"  {kind=OclConstants.COLLECTIONKIND_COLLECTION;}
  ;

collectionLiteralKind returns [String kind=null]
  : "Set"  {kind=OclConstants.COLLECTIONKIND_SET;}
  | "Bag"  {kind=OclConstants.COLLECTIONKIND_BAG;}
  | "Sequence"  {kind=OclConstants.COLLECTIONKIND_SEQUENCE;}
  ;

operationName returns [String operator=null]
  : name:NAME  {operator=name.getText();}
  | EQUAL  {operator=OclConstants.OPERATOR_EQUAL;}
  | PLUS  {operator=OclConstants.OPERATOR_PLUS;}
  | MINUS  {operator=OclConstants.OPERATOR_MINUS;}
  | LT  {operator=OclConstants.OPERATOR_LT;}
  | LE  {operator=OclConstants.OPERATOR_LE;}
  | GE  {operator=OclConstants.OPERATOR_GE;}
  | GT  {operator=OclConstants.OPERATOR_GT;}
  | DIVIDE  {operator=OclConstants.OPERATOR_DIVIDE;}
  | MULT  {operator=OclConstants.OPERATOR_MULT;}
  | NEQUAL  {operator=OclConstants.OPERATOR_NEQUAL;}
  | "implies"  {operator=OclConstants.OPERATOR_IMPLIES;}
  | "not"  {operator=OclConstants.OPERATOR_NOT;}
  | "or"  {operator=OclConstants.OPERATOR_OR;}
  | "xor"  {operator=OclConstants.OPERATOR_XOR;}
  | "and"  {operator=OclConstants.OPERATOR_AND;}
  ;

logicalOperator returns [String operator=null]
  : "and"  {operator=OclConstants.OPERATOR_AND;}
  | "or"  {operator=OclConstants.OPERATOR_OR;}
  | "xor"  {operator=OclConstants.OPERATOR_XOR;}
  | "implies"  {operator=OclConstants.OPERATOR_IMPLIES;}
  ;

relationalOperator returns [String operator=null]
  : EQUAL  {operator=OclConstants.OPERATOR_EQUAL;}
  | GT  {operator=OclConstants.OPERATOR_GT;}
  | LT  {operator=OclConstants.OPERATOR_LT;}
  | GE  {operator=OclConstants.OPERATOR_GE;}
  | LE  {operator=OclConstants.OPERATOR_LE;}
  | NEQUAL  {operator=OclConstants.OPERATOR_NEQUAL;}
  ;

addOperator returns [String operator=null]
  : PLUS  {operator=OclConstants.OPERATOR_PLUS;}
  | MINUS  {operator=OclConstants.OPERATOR_MINUS;}
  ;

multiplyOperator returns [String operator=null]
  : MULT  {operator=OclConstants.OPERATOR_MULT;}
  | DIVIDE  {operator=OclConstants.OPERATOR_DIVIDE;}
  ;

unaryOperator returns [String operator=null]
  : MINUS  {operator=OclConstants.OPERATOR_MINUS;}
  | "not"  {operator=OclConstants.OPERATOR_NOT;}
  ;

//////////////////////////////////////////////////////////////////////////////

class OclLexer extends Lexer;

options {
  exportVocab=OCL;
  testLiterals=false;
  k = 2;
}


// this is cracked up, but for some reason antlr can't handle dollars
// in a single-line comment.

SL_COMMENT options { paraphrase = "a comment"; }
    :   "--" ( options { warnWhenFollowAmbig = false; } : '$' )?
        ( ~ ( '\n' | '\r' ) )* ( '\n' | '\r' ( '\n' )? )
        { $setType(Token.SKIP); newline(); } ;


WS
options {
  paraphrase = "white space";
}
	:	(' '
	|	'\t'
	|	'\n'  { newline(); }
	|	'\r')
		{ $setType(Token.SKIP); }
	;

LPAREN			: '(';
RPAREN			: ')';
LBRACK			: '[';
RBRACK			: ']';
LCURLY			: '{';
RCURLY			: '}';
COLON			: ':';
DCOLON			: "::";
COMMA			: ',';
EQUAL			: '=';
NEQUAL			: "<>";
LT				: '<';
GT				: '>';
LE				: "<=";
GE				: ">=";
RARROW			: "->";
DOTDOT			: "..";
DOT				: '.';
POUND			: '#';
SEMICOL			: ';';
BAR				: '|';
ATSIGN			: '@';
PLUS			: '+';
MINUS			: '-';
MULT			: '*';
DIVIDE			: '/';

NAME
options {
  testLiterals = true;
}
    : ( ('a'..'z') | ('A'..'Z') | ('_') )
        ( ('a'..'z') | ('0'..'9') | ('A'..'Z') | ('_') )*
  ;

NUMBER
  : ('0'..'9')+
    ( { LA(2) != '.' }? '.' ('0'..'9')+ )?
    ( ('e' | 'E') ('+' | '-')? ('0'..'9')+ )?
  ;

STRING : '\'' ( ( ~ ('\'' | '\\' | '\n' | '\r') )
	| ('\\' ( ( 'n' | 't' | 'b' | 'r' | 'f' | '\\' | '\'' | '\"' )
		| ('0'..'3')
		  (
			options {
			  warnWhenFollowAmbig = false;
			}
		  :	('0'..'7')
			(
			  options {
				warnWhenFollowAmbig = false;
			  }
			:	'0'..'7'
			)?
		  )?
		|	('4'..'7')
		  (
			options {
			  warnWhenFollowAmbig = false;
			}
		  :	('0'..'9')
		  )? ) ) )* '\''
    ;

