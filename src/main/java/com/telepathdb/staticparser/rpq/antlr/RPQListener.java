/**
 * Copyright (C) 2017-2017 - All rights reserved.
 * This file is part of the telepathdb project which is released under the GPLv3 license.
 * See file LICENSE.txt or go to http://www.gnu.org/licenses/gpl.txt for full license details.
 * You may use, distribute and modify this code under the terms of the GPLv3 license.
 */

// Generated from rpq/RPQ.g4 by ANTLR 4.5
package com.telepathdb.staticparser.rpq.antlr;
import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link RPQParser}.
 */
public interface RPQListener extends ParseTreeListener {
	/**
	 * Enter a parse tree produced by the {@code binaryExpression}
	 * labeled alternative in {@link RPQParser#query}.
	 * @param ctx the parse tree
	 */
	void enterBinaryExpression(RPQParser.BinaryExpressionContext ctx);
	/**
	 * Exit a parse tree produced by the {@code binaryExpression}
	 * labeled alternative in {@link RPQParser#query}.
	 * @param ctx the parse tree
	 */
	void exitBinaryExpression(RPQParser.BinaryExpressionContext ctx);
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
	 * Enter a parse tree produced by the {@code unaryExpression}
	 * labeled alternative in {@link RPQParser#query}.
	 * @param ctx the parse tree
	 */
	void enterUnaryExpression(RPQParser.UnaryExpressionContext ctx);
	/**
	 * Exit a parse tree produced by the {@code unaryExpression}
	 * labeled alternative in {@link RPQParser#query}.
	 * @param ctx the parse tree
	 */
	void exitUnaryExpression(RPQParser.UnaryExpressionContext ctx);
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