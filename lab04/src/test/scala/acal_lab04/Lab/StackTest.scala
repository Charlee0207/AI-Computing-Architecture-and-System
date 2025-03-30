// See LICENSE from https://github.com/ucb-bar/chisel-tutorial/blob/release/src/test/scala/examples/StackTests.scala
package acal_lab04.Lab

import chisel3.iotesters.{PeekPokeTester, Driver, ChiselFlatSpec}
import scala.collection.mutable.{ArrayStack => ScalaStack}

class StackTestsOrig(c: Stack) extends PeekPokeTester(c) {
  var nxtDataOut = 0
  var dataOut = 0
  val stack = new ScalaStack[Int]()

  for (t <- 0 until 16) {
    println(s"Tick $t")
    val enable  = rnd.nextInt(2)
    val push    = rnd.nextInt(2)
    val pop     = rnd.nextInt(2)
    val top     = rnd.nextInt(2)
    val dataIn  = rnd.nextInt(256)
    val empty   = stack.isEmpty
    val full    = stack.length == c.depth
    println(s"enable $enable push $push pop $pop dataIn $dataIn top $top isempty $empty isfull $full")
    if (enable == 1) {
      dataOut = nxtDataOut
      if (push == 1 && stack.length < c.depth) {
        stack.push(dataIn)
      } else if (pop == 1 && stack.length > 0) {
        stack.pop()
      }
      if (stack.length > 0) {
        nxtDataOut = stack.top
      }
    }

    poke(c.io.pop,    pop)
    poke(c.io.push,   push)
    poke(c.io.en,     enable)
    poke(c.io.dataIn, dataIn)
    poke(c.io.peek,   top)
    step(1)
    expect(c.io.dataOut, dataOut)
    expect(c.io.empty, stack.isEmpty)
    expect(c.io.full, stack.length == c.depth)
  }
}

object StackTest extends App {
  Driver.execute(Array("-td","./generated","-tbn","verilator"),() => new Stack(8)) {
    c => new StackTestsOrig(c)
  }
}