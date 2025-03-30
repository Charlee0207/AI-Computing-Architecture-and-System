package acal_lab04.Lab

import chisel3.iotesters.{PeekPokeTester, Driver, ChiselFlatSpec}
import scala.collection.mutable.{Queue => ScalaQueue}

class QueueTests(c: Queue) extends PeekPokeTester(c) {
  var nxtDataOut = 0
  var dataOut = 0
  val queue = new ScalaQueue[Int]()

  for (t <- 0 until 16) {
    println(s"Tick $t")
    val enable  = rnd.nextInt(2)
    val push    = rnd.nextInt(2)
    val pop     = rnd.nextInt(2)
    val dataIn  = rnd.nextInt(256)
    println(s"enable $enable push $push pop $pop dataIn $dataIn")
    if (enable == 1) {
      dataOut = nxtDataOut
      if (push == 1) {
        queue.enqueue(dataIn)
      } else if (pop == 1 && queue.length > 0) {
        nxtDataOut = queue.dequeue()
      }
    }

    poke(c.io.pop,    pop)
    poke(c.io.push,   push)
    poke(c.io.en,     enable)
    poke(c.io.dataIn, dataIn)
    step(1)
    expect(c.io.dataOut, dataOut)
    expect(c.io.empty, queue.isEmpty)
    expect(c.io.full, queue.length == c.depth)
  }
}

object QueueTest extends App {
  Driver.execute(Array("-td","./generated","-tbn","verilator"),() => new Queue(8)) {
    c => new QueueTests(c)
  }
}