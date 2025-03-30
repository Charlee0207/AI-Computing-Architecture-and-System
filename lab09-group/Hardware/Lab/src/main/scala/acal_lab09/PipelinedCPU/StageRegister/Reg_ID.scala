package acal_lab09.PiplinedCPU.StageRegister

import chisel3._
import chisel3.util._

class Reg_ID(addrWidth:Int) extends Module {
    val io = IO(new Bundle{
        val Flush = Input(Bool())
        val Stall = Input(Bool())

        val pc_in = Input(UInt(addrWidth.W))
        val inst_in = Input(UInt(32.W))

        val pc = Output(UInt(addrWidth.W))
        val inst = Output(UInt(32.W))
    })

    // stage Registers
    val InstReg = RegInit(0.U(32.W))
    val pcReg =  RegInit(0.U(15.W))

    /*** stage Registers Action ***/

    when(io.Stall){
        InstReg := InstReg
        pcReg := pcReg
    }.elsewhen(io.Flush){
        InstReg := 0.U(32.W)
        pcReg := 0.U(addrWidth.W)
    }.otherwise{
        InstReg := io.inst_in
        pcReg := io.pc_in
    }

    io.inst := InstReg
    io.pc := pcReg
}
