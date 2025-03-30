package acal_lab04.Lab

import chisel3._
import chisel3.util.log2Ceil

class Queue(val depth: Int) extends Module {
  val io = IO(new Bundle {
    val push    = Input(Bool())
    val pop     = Input(Bool())
    val en      = Input(Bool())
    val dataIn  = Input(UInt(32.W))
    val dataOut = Output(UInt(32.W))
    val empty   = Output(Bool())
    val full    = Output(Bool())
  })

  val queue_mem     = Mem(depth, UInt(32.W))                 
  val dataOutput    = RegInit(0.U(32.W))             
  val dataOutput_FF = RegInit(0.U(32.W))
  val head          = RegInit(0.U(log2Ceil(depth+1).W))
  val length        = RegInit(0.U(log2Ceil(depth+1).W))


  when (io.en) {
    when(io.push && !io.full) {
      queue_mem(head+length) := io.dataIn
      length := length+1.U
    }
    .elsewhen(io.pop && !io.empty) {
      head := (head+1.U) % depth.asUInt
      length := length-1.U

      // dataOutput updated only when pop signal assert
      dataOutput := queue_mem(head)
    }

    // io.dataOut has one cycle latency after pop signal
    // To conform the testbench, add a FF to delay one cycle
    dataOutput_FF := dataOutput
  }

  io.dataOut  := dataOutput_FF
  io.empty    := length === 0.U
  io.full     := length === depth.asUInt

}
