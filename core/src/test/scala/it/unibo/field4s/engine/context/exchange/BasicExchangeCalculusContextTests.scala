package it.unibo.field4s.engine.context.exchange

import it.unibo.field4s.engine.context.ValueTreeProbingContextMixin
import it.unibo.field4s.engine.context.exchange.libraries.*
import it.unibo.field4s.UnitTest
import it.unibo.field4s.collections.{ MapWithDefault, ValueTree }
import it.unibo.field4s.engine.context.common.InvocationCoordinate
import it.unibo.field4s.engine.context.exchange.BasicExchangeCalculusContext.ExportValue
import it.unibo.field4s.engine.context.exchange.libraries.*
import it.unibo.field4s.engine.network.Import
import it.unibo.field4s.language.exchange.semantics.{
  ExchangeCalculusSemanticsTestHelper,
  ExchangeCalculusSemanticsTests,
}

class BasicExchangeCalculusContextTests
    extends UnitTest
    with ValueTreeProbingContextMixin
    with BasicFactoryMixin
    with ExchangeCalculusSemanticsTests
    with BranchingTests
    with ExchangeCalculusTests
    with FieldCalculusTests
    with FoldingTests
    with GradientTests
    with MathTests
    with FoldhoodLibraryTests
    with AlignTest:

  class BasicExchangeCalculusContextWithTestHelpers(
      self: Int,
      inboundMessages: Import[Int, ExportValue],
  ) extends BasicExchangeCalculusContext[Int](self, inboundMessages)
      with ExchangeCalculusSemanticsTestHelper:

    override def mockNValues[T](default: T, values: Map[DeviceId, T]): SharedData[T] =
      Field(default, values)

    override def unalignedDeviceId: Int = neighbors.maxOption.getOrElse(1) + 1

  given context: BasicExchangeCalculusContextWithTestHelpers = BasicExchangeCalculusContextWithTestHelpers(
    self = 0,
    inboundMessages = (0 until 10)
      .map(id =>
        id -> probe(
          localId = id,
          factory = factory,
          program = () => (),
        )(0),
      )
      .toMap,
  )

  "Basic ExchangeCalculusContext" should behave like exchangeCalculusSemanticsWithAtLeast10AlignedDevices
  "Basic ExchangeCalculusContext 'branch'" should behave like branchingSemantics()
  "Basic ExchangeCalculusContext 'exchange'" should behave like exchangeSemantics()
  "Basic ExchangeCalculusContext field calculus" should behave like fieldCalculusSemantics()
  "Basic ExchangeCalculusContext folding" should behave like foldingSemantics()
  "Basic ExchangeCalculusContext gradient library" should behave like gradientSemantics()
  "Basic ExchangeCalculusContext math library" should behave like mathLibrarySemantics()
  "Basic ExchangeCalculusContext foldhood library" should behave like foldhoodSemantics()
  "Align should align the same devices" should behave like testAlign()
end BasicExchangeCalculusContextTests
