// See LICENSE from https://github.com/ucb-bar/chisel-tutorial/blob/release/src/main/scala/examples/Stack.scala#L1C1-L33C2
package acal_lab04.Lab

import chisel3._
import chisel3.util.log2Ceil

class Stack(val depth: Int) extends Module {
  val io = IO(new Bundle {
    val push    = Input(Bool())
    val pop     = Input(Bool())
    val en      = Input(Bool())
    val peek    = Input(Bool())
    val dataIn  = Input(UInt(32.W))
    val dataOut = Output(UInt(32.W))
    val empty   = Output(Bool())
    val full    = Output(Bool())
  })

  val stack_mem = Mem(depth, UInt(32.W))
  val sp        = RegInit(0.U(log2Ceil(depth+1).W))
  val out       = RegInit(0.U(32.W))

  when (io.en) {
    when(io.push && (sp < depth.asUInt)) {
      stack_mem(sp) := io.dataIn
      sp := sp + 1.U
    } .elsewhen(io.pop && (sp > 0.U)) {
      sp := sp - 1.U
    }
    when (sp > 0.U) {
      out := stack_mem(sp - 1.U)
    }
    when (io.peek) {
      out := stack_mem(sp - 1.U)
    }
  }

  io.dataOut := out
  io.empty   := sp === 0.U
  io.full    := sp === depth.asUInt
}
