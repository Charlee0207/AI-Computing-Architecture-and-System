package acal_lab09.PiplinedCPU.StageRegister

import chisel3._
import chisel3.util._

class Reg_IF(addrWidth:Int) extends Module {
    val io = IO(new Bundle{
        val Stall = Input(Bool())

        val next_pc_in = Input(UInt(addrWidth.W))
        val pc = Output(UInt(addrWidth.W))
    })

    // PC
    val pcReg = RegInit(0.U(addrWidth.W))

    when(io.Stall){
        pcReg := pcReg
    }.otherwise{
        pcReg := io.next_pc_in
    }

    io.pc := pcReg
}
