package it.unibo.field4s.language.semantics.exchange.syntaxes

import it.unibo.field4s.language.semantics.exchange.ExchangeCalculusSemantics
import it.unibo.field4s.language.syntax.BranchingSyntax

/**
 * This trait witnesses the fact that the exchange calculus semantics can be used to implement the branching syntax.
 */
trait BranchingExchangeSemantics extends BranchingSyntax:
  self: ExchangeCalculusSemantics =>
  override def branch[T](cond: Boolean)(th: => T)(el: => T): T = br(cond)(th)(el)
