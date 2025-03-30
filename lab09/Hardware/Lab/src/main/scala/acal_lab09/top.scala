package acal_lab09

import chisel3._
import chisel3.util._

import acal_lab09.PiplinedCPU._
import acal_lab09.Memory._
import acal_lab09.MemIF._

class top extends Module {
    val io = IO(new Bundle{
        val regs = Output(Vec(32,UInt(32.W)))
        val Hcf = Output(Bool())

        //for sure that IM and DM will be synthesized
        val inst = Output(UInt(32.W))
        val rdata = Output(UInt(32.W))

        // For performance counter
        val mem_rd_req = Output(Bool())
        val mem_wr_req = Output(Bool())
        val mem_req_funct3 = Output(UInt(3.W))

        // Test
        val E_Branch_taken = Output(Bool())
        val Flush = Output(Bool())
        val Stall_MA = Output(Bool())
        val Stall_DH = Output(Bool())
        val IF_PC = Output(UInt(32.W))
        val ID_PC = Output(UInt(32.W))
        val EXE_PC = Output(UInt(32.W))
        val MEM_PC = Output(UInt(32.W))
        val WB_PC = Output(UInt(32.W))
        val EXE_alu_out = Output(UInt(32.W))
        val EXE_src1 = Output(UInt(32.W))
        val EXE_src2 = Output(UInt(32.W))
        val ALU_src1 = Output(UInt(32.W))
        val ALU_src2 = Output(UInt(32.W))
        val raddr = Output(UInt(32.W))
        val WB_rd = Output(UInt(5.W))
        val WB_wdata = Output(UInt(32.W))
        val EXE_Jump = Output(Bool())
        val EXE_Branch = Output(Bool())

    })

    val cpu = Module(new PiplinedCPU(15,32))
    val im = Module(new InstMem(15))
    val dm = Module(new DataMem(15))

    // Piplined CPU
    cpu.io.InstMem.rdata := im.io.inst
    cpu.io.DataMem.rdata := dm.io.rdata

    cpu.io.InstMem.Valid := true.B // Direct to Mem
    cpu.io.DataMem.Valid := true.B // Direct to Mem

    // Insruction Memory
    im.io.raddr := cpu.io.InstMem.raddr

    //Data Memory
    dm.io.Length := cpu.io.DataMem.Length
    dm.io.raddr := cpu.io.DataMem.raddr
    dm.io.wen := cpu.io.DataMem.Mem_W
    dm.io.waddr := cpu.io.DataMem.waddr
    dm.io.wdata := cpu.io.DataMem.wdata

    //System
    io.regs := cpu.io.regs
    io.Hcf := cpu.io.Hcf
    io.inst := im.io.inst
    io.rdata := dm.io.rdata

    // Test
    io.E_Branch_taken := cpu.io.E_Branch_taken
    io.Flush := cpu.io.Flush
    io.Stall_MA := cpu.io.Stall_MA
    io.Stall_DH := cpu.io.Stall_DH
    io.IF_PC := cpu.io.IF_PC
    io.ID_PC := cpu.io.ID_PC
    io.EXE_PC := cpu.io.EXE_PC
    io.MEM_PC := cpu.io.MEM_PC
    io.WB_PC := cpu.io.WB_PC
    io.EXE_alu_out := cpu.io.EXE_alu_out
    io.EXE_src1 := cpu.io.EXE_src1
    io.EXE_src2 := cpu.io.EXE_src2
    io.ALU_src1 := cpu.io.ALU_src1
    io.ALU_src2 := cpu.io.ALU_src2
    io.raddr := cpu.io.DataMem.raddr
    io.WB_rd := cpu.io.WB_rd
    io.WB_wdata := cpu.io.WB_wdata
    io.EXE_Jump := cpu.io.EXE_Jump
    io.EXE_Branch := cpu.io.EXE_Branch
    
    // For performance counter
    io.mem_rd_req := cpu.io.mem_rd_req
    io.mem_wr_req := cpu.io.mem_wr_req
    io.mem_req_funct3 := cpu.io.mem_req_funct3
}


import chisel3.stage.ChiselStage
object top extends App {
  (
    new chisel3.stage.ChiselStage).emitVerilog(
      new top(),
      Array("-td","generated/top")
  )
}
