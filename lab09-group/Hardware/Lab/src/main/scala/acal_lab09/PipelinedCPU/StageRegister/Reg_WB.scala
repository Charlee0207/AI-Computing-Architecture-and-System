package acal_lab09.PiplinedCPU.StageRegister

import chisel3._
import chisel3.util._

class Reg_WB(addrWidth:Int) extends Module {
    val io = IO(new Bundle{
        val Stall = Input(Bool())

        val pc_plus4_in = Input(UInt(addrWidth.W))
        val inst_in = Input(UInt(32.W))
        val alu_out_in = Input(UInt(32.W))
        val ld_data_in = Input(UInt(32.W))

        val pc_plus4 = Output(UInt(addrWidth.W))
        val inst = Output(UInt(32.W))
        val alu_out = Output(UInt(32.W))
        val ld_data = Output(UInt(32.W))
    })

    // stage Registers
    val pc_plus4_Reg =  RegInit(0.U(addrWidth.W))
    val InstReg = RegInit(0.U(32.W))
    val alu_out_Reg = RegInit(0.U(32.W))
    val ld_data_Reg = RegInit(0.U(32.W))

    /*** stage Registers Action ***/
    when(io.Stall){
        pc_plus4_Reg := pc_plus4_Reg
        InstReg := InstReg
        alu_out_Reg := alu_out_Reg
        ld_data_Reg := ld_data_Reg
    }.otherwise{
        pc_plus4_Reg := io.pc_plus4_in
        InstReg := io.inst_in
        alu_out_Reg := io.alu_out_in
        ld_data_Reg := io.ld_data_in
    }

    io.pc_plus4 := pc_plus4_Reg
    io.inst := InstReg
    io.alu_out := alu_out_Reg
    io.ld_data := ld_data_Reg
}
