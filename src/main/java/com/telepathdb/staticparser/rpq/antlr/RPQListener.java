// Generated from rpq/RPQ.g4 by ANTLR 4.5
package com.telepathdb.staticparser.rpq.antlr;
import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link RPQParser}.
 */
public interface RPQListener extends ParseTreeListener {
	/**
	 * Enter a parse tree produced by the {@code leaf}
	 * labeled alternative in {@link RPQParser#query}.
	 * @param ctx the parse tree
	 */
	void enterLeaf(RPQParser.LeafContext ctx);
	/**
	 * Exit a parse tree produced by the {@code leaf}
	 * labeled alternative in {@link RPQParser#query}.
	 * @param ctx the parse tree
	 */
	void exitLeaf(RPQParser.LeafContext ctx);
	/**
	 * Enter a parse tree produced by the {@code parenthesis}
	 * labeled alternative in {@link RPQParser#query}.
	 * @param ctx the parse tree
	 */
	void enterParenthesis(RPQParser.ParenthesisContext ctx);
	/**
	 * Exit a parse tree produced by the {@code parenthesis}
	 * labeled alternative in {@link RPQParser#query}.
	 * @param ctx the parse tree
	 */
	void exitParenthesis(RPQParser.ParenthesisContext ctx);
	/**
	 * Enter a parse tree produced by the {@code queryOperatorQuery}
	 * labeled alternative in {@link RPQParser#query}.
	 * @param ctx the parse tree
	 */
	void enterQueryOperatorQuery(RPQParser.QueryOperatorQueryContext ctx);
	/**
	 * Exit a parse tree produced by the {@code queryOperatorQuery}
	 * labeled alternative in {@link RPQParser#query}.
	 * @param ctx the parse tree
	 */
	void exitQueryOperatorQuery(RPQParser.QueryOperatorQueryContext ctx);
	/**
	 * Enter a parse tree produced by the {@code queryOperator}
	 * labeled alternative in {@link RPQParser#query}.
	 * @param ctx the parse tree
	 */
	void enterQueryOperator(RPQParser.QueryOperatorContext ctx);
	/**
	 * Exit a parse tree produced by the {@code queryOperator}
	 * labeled alternative in {@link RPQParser#query}.
	 * @param ctx the parse tree
	 */
	void exitQueryOperator(RPQParser.QueryOperatorContext ctx);
	/**
	 * Enter a parse tree produced by {@link RPQParser#unaryOperator}.
	 * @param ctx the parse tree
	 */
	void enterUnaryOperator(RPQParser.UnaryOperatorContext ctx);
	/**
	 * Exit a parse tree produced by {@link RPQParser#unaryOperator}.
	 * @param ctx the parse tree
	 */
	void exitUnaryOperator(RPQParser.UnaryOperatorContext ctx);
	/**
	 * Enter a parse tree produced by {@link RPQParser#binaryOperator}.
	 * @param ctx the parse tree
	 */
	void enterBinaryOperator(RPQParser.BinaryOperatorContext ctx);
	/**
	 * Exit a parse tree produced by {@link RPQParser#binaryOperator}.
	 * @param ctx the parse tree
	 */
	void exitBinaryOperator(RPQParser.BinaryOperatorContext ctx);
}