package acal_lab09.PiplinedCPU.DatapathModule

import chisel3._
import chisel3.util._

class Path_MEM(addrWidth:Int) extends Module {
    val io = IO(new Bundle{

        val MEM_pc_in = Input(UInt(addrWidth.W))
        val MEM_alu_out_in = Input(UInt(32.W))
        val MEM_DM_wdata_in = Input(UInt(32.W))

        val MEM_pc_plus_4 = Output(UInt(addrWidth.W))
        val MEM_alu_out = Output(UInt(32.W))
        val MEM_ld_data = Output(UInt(32.W))

        // Data Memory IO
        val Mem_Addr = Output(UInt(addrWidth.W))
        val Mem_Write_Data = Output(UInt(32.W))
        val Mem_Data = Input(UInt(32.W))
    })

    // pc + 4 for write back data
    io.MEM_pc_plus_4 := io.MEM_pc_in + 4.U(addrWidth.W)

    // Memory
    io.Mem_Addr := io.MEM_alu_out_in(addrWidth-1,0)
    io.Mem_Write_Data := io.MEM_DM_wdata_in
    io.MEM_ld_data := io.Mem_Data

    // other IO
    io.MEM_alu_out := io.MEM_alu_out_in



}
