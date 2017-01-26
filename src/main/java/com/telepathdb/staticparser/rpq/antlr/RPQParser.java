/**
 * Copyright (C) 2017-2017 - All rights reserved.
 * This file is part of the telepathdb project which is released under the GPLv3 license.
 * See file LICENSE.txt or go to http://www.gnu.org/licenses/gpl.txt for full license details.
 * You may use, distribute and modify this code under the terms of the GPLv3 license.
 */

// Generated from rpq/RPQ.g4 by ANTLR 4.5
package com.telepathdb.staticparser.rpq.antlr;

import org.antlr.v4.runtime.FailedPredicateException;
import org.antlr.v4.runtime.NoViableAltException;
import org.antlr.v4.runtime.Parser;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.RuleContext;
import org.antlr.v4.runtime.RuntimeMetaData;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.Vocabulary;
import org.antlr.v4.runtime.VocabularyImpl;
import org.antlr.v4.runtime.atn.ATN;
import org.antlr.v4.runtime.atn.ATNDeserializer;
import org.antlr.v4.runtime.atn.ParserATNSimulator;
import org.antlr.v4.runtime.atn.PredictionContextCache;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.tree.ParseTreeListener;
import org.antlr.v4.runtime.tree.ParseTreeVisitor;
import org.antlr.v4.runtime.tree.TerminalNode;

import java.util.List;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast"})
public class RPQParser extends Parser {
	static { RuntimeMetaData.checkVersion("4.5", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		T__0=1, T__1=2, LABEL=3, KLEENE_STAR=4, PLUS=5, CONJUNCTION=6, UNION=7, 
		CHARS=8, WHITESPACE=9;
	public static final int
		RULE_query = 0, RULE_unaryOperator = 1, RULE_binaryOperator = 2;
	public static final String[] ruleNames = {
		"query", "unaryOperator", "binaryOperator"
	};

	private static final String[] _LITERAL_NAMES = {
		null, "'('", "')'", null, "'*'", "'+'", "'/'", "'|'"
	};
	private static final String[] _SYMBOLIC_NAMES = {
		null, null, null, "LABEL", "KLEENE_STAR", "PLUS", "CONJUNCTION", "UNION", 
		"CHARS", "WHITESPACE"
	};
	public static final Vocabulary VOCABULARY = new VocabularyImpl(_LITERAL_NAMES, _SYMBOLIC_NAMES);

	/**
	 * @deprecated Use {@link #VOCABULARY} instead.
	 */
	@Deprecated
	public static final String[] tokenNames;
	static {
		tokenNames = new String[_SYMBOLIC_NAMES.length];
		for (int i = 0; i < tokenNames.length; i++) {
			tokenNames[i] = VOCABULARY.getLiteralName(i);
			if (tokenNames[i] == null) {
				tokenNames[i] = VOCABULARY.getSymbolicName(i);
			}

			if (tokenNames[i] == null) {
				tokenNames[i] = "<INVALID>";
			}
		}
	}

	@Override
	@Deprecated
	public String[] getTokenNames() {
		return tokenNames;
	}

	@Override

	public Vocabulary getVocabulary() {
		return VOCABULARY;
	}

	@Override
	public String getGrammarFileName() { return "RPQ.g4"; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public ATN getATN() { return _ATN; }

	public RPQParser(TokenStream input) {
		super(input);
		_interp = new ParserATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}
	public static class QueryContext extends ParserRuleContext {
		public QueryContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_query; }
	 
		public QueryContext() { }
		public void copyFrom(QueryContext ctx) {
			super.copyFrom(ctx);
		}
	}
	public static class BinaryExpressionContext extends QueryContext {
		public List<QueryContext> query() {
			return getRuleContexts(QueryContext.class);
		}
		public QueryContext query(int i) {
			return getRuleContext(QueryContext.class,i);
		}
		public BinaryOperatorContext binaryOperator() {
			return getRuleContext(BinaryOperatorContext.class,0);
		}
		public BinaryExpressionContext(QueryContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof RPQListener) ((RPQListener)listener).enterBinaryExpression(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof RPQListener) ((RPQListener)listener).exitBinaryExpression(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof RPQVisitor) return ((RPQVisitor<? extends T>)visitor).visitBinaryExpression(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class LeafContext extends QueryContext {
		public TerminalNode LABEL() { return getToken(RPQParser.LABEL, 0); }
		public LeafContext(QueryContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof RPQListener) ((RPQListener)listener).enterLeaf(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof RPQListener) ((RPQListener)listener).exitLeaf(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof RPQVisitor) return ((RPQVisitor<? extends T>)visitor).visitLeaf(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class UnaryExpressionContext extends QueryContext {
		public QueryContext query() {
			return getRuleContext(QueryContext.class,0);
		}
		public UnaryOperatorContext unaryOperator() {
			return getRuleContext(UnaryOperatorContext.class,0);
		}
		public UnaryExpressionContext(QueryContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof RPQListener) ((RPQListener)listener).enterUnaryExpression(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof RPQListener) ((RPQListener)listener).exitUnaryExpression(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof RPQVisitor) return ((RPQVisitor<? extends T>)visitor).visitUnaryExpression(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class ParenthesisContext extends QueryContext {
		public QueryContext query() {
			return getRuleContext(QueryContext.class,0);
		}
		public ParenthesisContext(QueryContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof RPQListener) ((RPQListener)listener).enterParenthesis(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof RPQListener) ((RPQListener)listener).exitParenthesis(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof RPQVisitor) return ((RPQVisitor<? extends T>)visitor).visitParenthesis(this);
			else return visitor.visitChildren(this);
		}
	}

	public final QueryContext query() throws RecognitionException {
		return query(0);
	}

	private QueryContext query(int _p) throws RecognitionException {
		ParserRuleContext _parentctx = _ctx;
		int _parentState = getState();
		QueryContext _localctx = new QueryContext(_ctx, _parentState);
		QueryContext _prevctx = _localctx;
		int _startState = 0;
		enterRecursionRule(_localctx, 0, RULE_query, _p);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(12);
			switch (_input.LA(1)) {
			case LABEL:
				{
				_localctx = new LeafContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;

				setState(7);
				match(LABEL);
				}
				break;
			case T__0:
				{
				_localctx = new ParenthesisContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(8);
				match(T__0);
				setState(9);
				query(0);
				setState(10);
				match(T__1);
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
			_ctx.stop = _input.LT(-1);
			setState(22);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,2,_ctx);
			while ( _alt!=2 && _alt!= ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					if ( _parseListeners!=null ) triggerExitRuleEvent();
					_prevctx = _localctx;
					{
					setState(20);
					switch ( getInterpreter().adaptivePredict(_input,1,_ctx) ) {
					case 1:
						{
						_localctx = new BinaryExpressionContext(new QueryContext(_parentctx, _parentState));
						pushNewRecursionContext(_localctx, _startState, RULE_query);
						setState(14);
						if (!(precpred(_ctx, 3))) throw new FailedPredicateException(this, "precpred(_ctx, 3)");
						setState(15);
						binaryOperator();
						setState(16);
						query(4);
						}
						break;
					case 2:
						{
						_localctx = new UnaryExpressionContext(new QueryContext(_parentctx, _parentState));
						pushNewRecursionContext(_localctx, _startState, RULE_query);
						setState(18);
						if (!(precpred(_ctx, 4))) throw new FailedPredicateException(this, "precpred(_ctx, 4)");
						setState(19);
						unaryOperator();
						}
						break;
					}
					}
				}
				setState(24);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,2,_ctx);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			unrollRecursionContexts(_parentctx);
		}
		return _localctx;
	}

	public static class UnaryOperatorContext extends ParserRuleContext {
		public TerminalNode KLEENE_STAR() { return getToken(RPQParser.KLEENE_STAR, 0); }
		public TerminalNode PLUS() { return getToken(RPQParser.PLUS, 0); }
		public UnaryOperatorContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_unaryOperator; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof RPQListener) ((RPQListener)listener).enterUnaryOperator(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof RPQListener) ((RPQListener)listener).exitUnaryOperator(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof RPQVisitor) return ((RPQVisitor<? extends T>)visitor).visitUnaryOperator(this);
			else return visitor.visitChildren(this);
		}
	}

	public final UnaryOperatorContext unaryOperator() throws RecognitionException {
		UnaryOperatorContext _localctx = new UnaryOperatorContext(_ctx, getState());
		enterRule(_localctx, 2, RULE_unaryOperator);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(25);
			_la = _input.LA(1);
			if ( !(_la==KLEENE_STAR || _la==PLUS) ) {
			_errHandler.recoverInline(this);
			} else {
				consume();
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class BinaryOperatorContext extends ParserRuleContext {
		public TerminalNode CONJUNCTION() { return getToken(RPQParser.CONJUNCTION, 0); }
		public TerminalNode UNION() { return getToken(RPQParser.UNION, 0); }
		public BinaryOperatorContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_binaryOperator; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof RPQListener) ((RPQListener)listener).enterBinaryOperator(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof RPQListener) ((RPQListener)listener).exitBinaryOperator(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof RPQVisitor) return ((RPQVisitor<? extends T>)visitor).visitBinaryOperator(this);
			else return visitor.visitChildren(this);
		}
	}

	public final BinaryOperatorContext binaryOperator() throws RecognitionException {
		BinaryOperatorContext _localctx = new BinaryOperatorContext(_ctx, getState());
		enterRule(_localctx, 4, RULE_binaryOperator);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(27);
			_la = _input.LA(1);
			if ( !(_la==CONJUNCTION || _la==UNION) ) {
			_errHandler.recoverInline(this);
			} else {
				consume();
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public boolean sempred(RuleContext _localctx, int ruleIndex, int predIndex) {
		switch (ruleIndex) {
		case 0:
			return query_sempred((QueryContext)_localctx, predIndex);
		}
		return true;
	}
	private boolean query_sempred(QueryContext _localctx, int predIndex) {
		switch (predIndex) {
		case 0:
			return precpred(_ctx, 3);
		case 1:
			return precpred(_ctx, 4);
		}
		return true;
	}

	public static final String _serializedATN =
		"\3\u0430\ud6d1\u8206\uad2d\u4417\uaef1\u8d80\uaadd\3\13 \4\2\t\2\4\3\t"+
		"\3\4\4\t\4\3\2\3\2\3\2\3\2\3\2\3\2\5\2\17\n\2\3\2\3\2\3\2\3\2\3\2\3\2"+
		"\7\2\27\n\2\f\2\16\2\32\13\2\3\3\3\3\3\4\3\4\3\4\2\3\2\5\2\4\6\2\4\3\2"+
		"\6\7\3\2\b\t\37\2\16\3\2\2\2\4\33\3\2\2\2\6\35\3\2\2\2\b\t\b\2\1\2\t\17"+
		"\7\5\2\2\n\13\7\3\2\2\13\f\5\2\2\2\f\r\7\4\2\2\r\17\3\2\2\2\16\b\3\2\2"+
		"\2\16\n\3\2\2\2\17\30\3\2\2\2\20\21\f\5\2\2\21\22\5\6\4\2\22\23\5\2\2"+
		"\6\23\27\3\2\2\2\24\25\f\6\2\2\25\27\5\4\3\2\26\20\3\2\2\2\26\24\3\2\2"+
		"\2\27\32\3\2\2\2\30\26\3\2\2\2\30\31\3\2\2\2\31\3\3\2\2\2\32\30\3\2\2"+
		"\2\33\34\t\2\2\2\34\5\3\2\2\2\35\36\t\3\2\2\36\7\3\2\2\2\5\16\26\30";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}