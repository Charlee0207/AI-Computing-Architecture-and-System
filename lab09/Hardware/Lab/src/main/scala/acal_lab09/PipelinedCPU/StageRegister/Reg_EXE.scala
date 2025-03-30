package acal_lab09.PiplinedCPU.StageRegister

import chisel3._
import chisel3.util._

class Reg_EXE(addrWidth:Int) extends Module {
    val io = IO(new Bundle{
        val Flush = Input(Bool())
        val Stall = Input(Bool())

        val inst_in = Input(UInt(32.W))
        val pc_in = Input(UInt(addrWidth.W))
        val rs1_rdata_in = Input(UInt(32.W))
        val rs2_rdata_in = Input(UInt(32.W))
        val imm_in = Input(UInt(32.W))


        val inst = Output(UInt(32.W))
        val pc = Output(UInt(addrWidth.W))
        val rs1_rdata = Output(UInt(32.W))
        val rs2_rdata = Output(UInt(32.W))
        val imm = Output(UInt(32.W))

    })

    // stage Registers
    val pcReg =  RegInit(0.U(addrWidth.W))
    val InstReg = RegInit(0.U(32.W))
    val immReg = RegInit(0.U(32.W))
    val rs1Reg = RegInit(0.U(32.W))
    val rs2Reg = RegInit(0.U(32.W))

    /*** stage Registers Action ***/
    when(io.Stall){
        immReg := immReg
        InstReg := InstReg
        pcReg := pcReg
        rs1Reg := rs1Reg
        rs2Reg := rs2Reg
    }.elsewhen(io.Flush){
        immReg := 0.U(32.W)
        InstReg := 0.U(32.W)
        pcReg := 0.U(addrWidth.W)
        rs1Reg := 0.U(32.W)
        rs2Reg := 0.U(32.W)
    }.otherwise{
        InstReg := io.inst_in
        immReg := io.imm_in
        pcReg := io.pc_in
        rs1Reg := io.rs1_rdata_in
        rs2Reg := io.rs2_rdata_in
    }

    io.inst := InstReg
    io.imm := immReg
    io.pc := pcReg
    io.rs1_rdata := rs1Reg
    io.rs2_rdata := rs2Reg
}
