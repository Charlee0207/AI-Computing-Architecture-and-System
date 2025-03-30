package acal_lab05.Hw2

import chisel3._
import chisel3.util._

class Stack(val depth: Int, val bit: Int) extends Module {
  val io = IO(new Bundle {
    val push    = Input(Bool())
    val pop     = Input(Bool())
    val emplace = Input(Bool())
    val clear   = Input(Bool())
    val en      = Input(Bool())
    val dataIn  = Input(UInt(32.W))
    val dataOut = Output(UInt(32.W))
    val empty   = Output(Bool())
    val lastone = Output(Bool())
    val full    = Output(Bool())
  })

  val mem = Mem(depth, UInt(bit.W))
  val sp  = RegInit(0.U(log2Ceil(depth+1).W))
  val out = WireDefault(0.U(bit.W))

  when (io.en) {
    when(io.clear){
      sp := 0.U
    }
    .elsewhen(io.push && (sp<depth.asUInt)) {
      mem(sp) := io.dataIn
      sp := sp + 1.U
    } 
    .elsewhen(io.pop && (sp>0.U)) {
      sp := sp - 1.U
    }
    .elsewhen(io.emplace && (sp>0.U)){
      mem(sp-1.U) := io.dataIn
    }
  }

  when(sp>0.U){
    out := mem(sp-1.U)
  }
  .otherwise{
    out := (-1.S.asUInt)
  }

  io.dataOut := out
  io.empty   := sp === 0.U
  io.lastone := sp === 1.U
  io.full    := sp === depth.asUInt
}

