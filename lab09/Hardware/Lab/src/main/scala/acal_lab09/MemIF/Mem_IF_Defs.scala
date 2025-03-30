package acal_lab09.MemIF

import chisel3._
import chisel3.util._

class MemIF_CPU(val addrWidth: Int, val dataWidth: Int) extends Bundle {
  val Mem_R = Output(Bool())
  val Mem_W = Output(Bool())
  val Length = Output(UInt(4.W))
	val Valid = Input(Bool())

	val raddr = Output(UInt(addrWidth.W))
	val rdata = Input(UInt(dataWidth.W))

	val waddr = Output(UInt(addrWidth.W))
	val wdata = Output(UInt(dataWidth.W))

  override def clone = { new MemIF_CPU(addrWidth, dataWidth).asInstanceOf[this.type] }
}

class MemIF_MEM(val addrWidth: Int, val dataWidth: Int) extends Bundle {
  val Mem_R = Input(Bool())
  val Mem_W = Input(Bool())
  val Length = Input(UInt(4.W))
	val Valid = Output(Bool())

	val raddr = Input(UInt(addrWidth.W))
	val rdata = Output(UInt(dataWidth.W))

	val waddr = Input(UInt(addrWidth.W))
	val wdata = Input(UInt(dataWidth.W))

  override def clone = { new MemIF_MEM(addrWidth, dataWidth).asInstanceOf[this.type] }
}

class MemIF extends MultiIOModule {
  val io = IO(new Bundle {
    val cpu = new MemIF_CPU(15,32)
    val mem = new MemIF_MEM(15,32)
  })

	io.mem.Mem_R := io.cpu.Mem_R
  io.mem.Mem_W := io.cpu.Mem_W
  io.mem.Length := io.cpu.Length
	io.cpu.Valid := io.mem.Valid

	io.mem.raddr := io.cpu.raddr
	io.cpu.rdata := io.mem.rdata

	io.mem.waddr := io.cpu.waddr
	io.mem.wdata := io.cpu.wdata
}

object MemIF extends App {
  (new stage.ChiselStage).emitVerilog(
    new MemIF(),
    Array("-td", "./generated/MemIF")
  )
}