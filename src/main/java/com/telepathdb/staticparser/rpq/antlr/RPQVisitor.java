// Generated from rpq/RPQ.g4 by ANTLR 4.5
package com.telepathdb.staticparser.rpq.antlr;
import org.antlr.v4.runtime.tree.ParseTreeVisitor;

/**
 * This interface defines a complete generic visitor for a parse tree produced
 * by {@link RPQParser}.
 *
 * @param <T> The return type of the visit operation. Use {@link Void} for
 * operations with no return type.
 */
public interface RPQVisitor<T> extends ParseTreeVisitor<T> {
	/**
	 * Visit a parse tree produced by the {@code leaf}
	 * labeled alternative in {@link RPQParser#query}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitLeaf(RPQParser.LeafContext ctx);
	/**
	 * Visit a parse tree produced by the {@code parenthesis}
	 * labeled alternative in {@link RPQParser#query}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitParenthesis(RPQParser.ParenthesisContext ctx);
	/**
	 * Visit a parse tree produced by the {@code queryOperatorQuery}
	 * labeled alternative in {@link RPQParser#query}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitQueryOperatorQuery(RPQParser.QueryOperatorQueryContext ctx);
	/**
	 * Visit a parse tree produced by the {@code queryOperator}
	 * labeled alternative in {@link RPQParser#query}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitQueryOperator(RPQParser.QueryOperatorContext ctx);
	/**
	 * Visit a parse tree produced by {@link RPQParser#unaryOperator}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitUnaryOperator(RPQParser.UnaryOperatorContext ctx);
	/**
	 * Visit a parse tree produced by {@link RPQParser#binaryOperator}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitBinaryOperator(RPQParser.BinaryOperatorContext ctx);
}