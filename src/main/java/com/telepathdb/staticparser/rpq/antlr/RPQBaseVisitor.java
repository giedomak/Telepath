/**
 * Copyright (C) 2017-2017 - All rights reserved.
 * This file is part of the telepathdb project which is released under the GPLv3 license.
 * See file LICENSE.txt or go to http://www.gnu.org/licenses/gpl.txt for full license details.
 * You may use, distribute and modify this code under the terms of the GPLv3 license.
 */

// Generated from rpq/RPQ.g4 by ANTLR 4.5
package com.telepathdb.staticparser.rpq.antlr;

import org.antlr.v4.runtime.tree.AbstractParseTreeVisitor;

/**
 * This class provides an empty implementation of {@link RPQVisitor},
 * which can be extended to create a visitor which only needs to handle a subset
 * of the available methods.
 *
 * @param <T> The return type of the visit operation. Use {@link Void} for
 *            operations with no return type.
 */
public class RPQBaseVisitor<T> extends AbstractParseTreeVisitor<T> implements RPQVisitor<T> {
  /**
   * {@inheritDoc}
   * <p>
   * <p>The default implementation returns the result of calling
   * {@link #visitChildren} on {@code ctx}.</p>
   */
  @Override
  public T visitBinaryExpression(RPQParser.BinaryExpressionContext ctx) {
    return visitChildren(ctx);
  }

  /**
   * {@inheritDoc}
   * <p>
   * <p>The default implementation returns the result of calling
   * {@link #visitChildren} on {@code ctx}.</p>
   */
  @Override
  public T visitLeaf(RPQParser.LeafContext ctx) {
    return visitChildren(ctx);
  }

  /**
   * {@inheritDoc}
   * <p>
   * <p>The default implementation returns the result of calling
   * {@link #visitChildren} on {@code ctx}.</p>
   */
  @Override
  public T visitUnaryExpression(RPQParser.UnaryExpressionContext ctx) {
    return visitChildren(ctx);
  }

  /**
   * {@inheritDoc}
   * <p>
   * <p>The default implementation returns the result of calling
   * {@link #visitChildren} on {@code ctx}.</p>
   */
  @Override
  public T visitParenthesis(RPQParser.ParenthesisContext ctx) {
    return visitChildren(ctx);
  }

  /**
   * {@inheritDoc}
   * <p>
   * <p>The default implementation returns the result of calling
   * {@link #visitChildren} on {@code ctx}.</p>
   */
  @Override
  public T visitUnaryOperator(RPQParser.UnaryOperatorContext ctx) {
    return visitChildren(ctx);
  }

  /**
   * {@inheritDoc}
   * <p>
   * <p>The default implementation returns the result of calling
   * {@link #visitChildren} on {@code ctx}.</p>
   */
  @Override
  public T visitBinaryOperator(RPQParser.BinaryOperatorContext ctx) {
    return visitChildren(ctx);
  }
}