package acal_lab09.PiplinedCPU

import chisel3._
import chisel3.util._

import acal_lab09.MemIF._
import acal_lab09.PiplinedCPU.StageRegister._
import acal_lab09.PiplinedCPU.Controller._
import acal_lab09.PiplinedCPU.DatapathModule._
import acal_lab09.PiplinedCPU.opcode_map._

class PiplinedCPU(memAddrWidth: Int, memDataWidth: Int) extends Module {
    val io = IO(new Bundle{
        //InstMem
        val InstMem = new MemIF_CPU(memAddrWidth, memDataWidth) 
        
        //DataMem
        val DataMem = new MemIF_CPU(memAddrWidth, memDataWidth) 

        //System
        val regs = Output(Vec(32,UInt(32.W)))
        val Hcf = Output(Bool())

        // Test
        val E_Branch_taken = Output(Bool())
        val Flush = Output(Bool())
        val Stall_MA = Output(Bool())
        val Stall_DH = Output(Bool())
        val IF_PC = Output(UInt(memAddrWidth.W))
        val ID_PC = Output(UInt(memAddrWidth.W))
        val EXE_PC = Output(UInt(memAddrWidth.W))
        val MEM_PC = Output(UInt(memAddrWidth.W))
        val WB_PC = Output(UInt(memAddrWidth.W))
        val EXE_src1 = Output(UInt(32.W))
        val EXE_src2 = Output(UInt(32.W))
        val ALU_src1 = Output(UInt(32.W))
        val ALU_src2 = Output(UInt(32.W))
        val EXE_alu_out = Output(UInt(32.W))
        val WB_rd = Output(UInt(5.W))
        val WB_wdata = Output(UInt(32.W))
        val EXE_Jump = Output(Bool())
        val EXE_Branch = Output(Bool())

        // For performance counter
        val mem_rd_req = Output(Bool())
        val mem_wr_req = Output(Bool())
        val mem_req_funct3 = Output(UInt(3.W))
    })
    /*****  Pipeline Stages Registers Module for holding data *****/
    // stage Registers
    val stage_IF = Module(new Reg_IF(memAddrWidth))
    val stage_ID = Module(new Reg_ID(memAddrWidth))
    val stage_EXE = Module(new Reg_EXE(memAddrWidth))
    val stage_MEM = Module(new Reg_MEM(memAddrWidth))
    val stage_WB = Module(new Reg_WB(memAddrWidth))

    // 5 pipe stage datapath modules
    val datapath_IF = Module(new Path_IF(memAddrWidth))
    val datapath_ID = Module(new Path_ID(memAddrWidth))
    val datapath_EXE = Module(new Path_EXE(memAddrWidth))
    val datapath_MEM = Module(new Path_MEM(memAddrWidth))
    val datapath_WB = Module(new Path_WB(memAddrWidth))

    // 1 controller module
    val controller = Module(new Controller(memAddrWidth))

    /* Wire Connect */
    // === IF stage reg (PC reg) ======================================================
    stage_IF.io.Stall := (controller.io.Hcf || controller.io.Stall_WB_ID_DH || controller.io.Stall_MEM_ID_DH || controller.io.Stall_EXE_ID_DH)       // To Be Modified
    stage_IF.io.next_pc_in := datapath_IF.io.next_pc

    // IF Block Datapath
    datapath_IF.io.PCSel := controller.io.PCSel
    datapath_IF.io.IF_pc_in := stage_IF.io.pc
    datapath_IF.io.EXE_pc_in := stage_EXE.io.pc
    datapath_IF.io.EXE_target_pc_in := datapath_EXE.io.EXE_target_pc_out 
    datapath_IF.io.Mem_data := io.InstMem.rdata(31,0)
    
    // --- Insruction Memory Interface
    io.InstMem.Mem_R := controller.io.IM_Mem_R
    io.InstMem.Mem_W :=  controller.io.IM_Mem_W
    io.InstMem.Length :=  controller.io.IM_Length
    io.InstMem.raddr := datapath_IF.io.Mem_Addr
    io.InstMem.waddr := 0.U // not used
    io.InstMem.wdata := 0.U // not used

    // === ID stage reg ==============================================================
    stage_ID.io.Flush := controller.io.Flush_BH    // To Be Modified
    stage_ID.io.Stall := (controller.io.Hcf || controller.io.Stall_WB_ID_DH || controller.io.Stall_MEM_ID_DH || controller.io.Stall_EXE_ID_DH)      // To Be Modified
    stage_ID.io.inst_in := datapath_IF.io.inst
    stage_ID.io.pc_in := stage_IF.io.pc
    
    // ID Block Datapath
    datapath_ID.io.ID_inst_in := stage_ID.io.inst
    datapath_ID.io.WB_index := stage_WB.io.inst(11,7)
    datapath_ID.io.WB_wdata := datapath_WB.io.WB_wdata
    datapath_ID.io.WB_RegWEn := controller.io.W_RegWEn
    datapath_ID.io.ImmSel := controller.io.D_ImmSel

    // === EXE stage reg ==============================================================
    stage_EXE.io.Flush := (controller.io.Flush_BH || controller.io.Flush_MEM_ID_DH || controller.io.Flush_EXE_ID_DH)   // To Be Modified
    when(controller.io.Stall_MEM_ID_DH && controller.io.Stall_EXE_ID_DH){
        stage_EXE.io.Stall := false.B
    }otherwise{
        stage_EXE.io.Stall := (controller.io.Hcf || controller.io.Stall_WB_ID_DH || controller.io.Stall_MEM_ID_DH)   // To Be Modified
    }
    stage_EXE.io.pc_in := stage_ID.io.pc
    stage_EXE.io.inst_in := stage_ID.io.inst
    stage_EXE.io.imm_in := datapath_ID.io.imm
    stage_EXE.io.rs1_rdata_in := datapath_ID.io.ID_rs1_rdata
    stage_EXE.io.rs2_rdata_in := datapath_ID.io.ID_rs2_rdata
    
    // EXE Block Datapath
    datapath_EXE.io.EXE_pc_in := stage_EXE.io.pc
    datapath_EXE.io.EXE_imm_in := stage_EXE.io.imm
    datapath_EXE.io.EXE_rs1_rdata_in := stage_EXE.io.rs1_rdata
    datapath_EXE.io.EXE_rs2_rdata_in := stage_EXE.io.rs2_rdata
    datapath_EXE.io.E_ASel := controller.io.E_ASel
    datapath_EXE.io.E_BSel := controller.io.E_BSel
    datapath_EXE.io.E_BrUn := controller.io.E_BrUn
    datapath_EXE.io.E_ALUSel := controller.io.E_ALUSel
    
    // === MEM stage reg ==============================================================
    when(controller.io.Stall_WB_ID_DH && controller.io.Stall_MEM_ID_DH){
        stage_MEM.io.Stall := false.B
    }otherwise{
        stage_MEM.io.Stall := (controller.io.Hcf || controller.io.Stall_WB_ID_DH)   // To Be Modified
    }
    stage_MEM.io.pc_in := stage_EXE.io.pc
    stage_MEM.io.inst_in := stage_EXE.io.inst
    stage_MEM.io.DM_wdata_in := datapath_EXE.io.EXE_rs2_rdata_out
    stage_MEM.io.alu_out_in := datapath_EXE.io.EXE_alu_out

    // MEM Block Datapath
    datapath_MEM.io.MEM_pc_in := stage_MEM.io.pc
    datapath_MEM.io.MEM_alu_out_in := stage_MEM.io.alu_out
    datapath_MEM.io.MEM_DM_wdata_in := stage_MEM.io.DM_wdata
    datapath_MEM.io.Mem_Data := io.DataMem.rdata(31,0)

    // --- Data Memory Interface
    io.DataMem.Mem_R := controller.io.DM_Mem_R
    io.DataMem.Mem_W :=  controller.io.DM_Mem_W
    io.DataMem.Length :=  controller.io.DM_Length
    io.DataMem.raddr := datapath_MEM.io.Mem_Addr
    io.DataMem.waddr := datapath_MEM.io.Mem_Addr
    io.DataMem.wdata := datapath_MEM.io.Mem_Write_Data

    // === WB stage reg ==============================================================
    stage_WB.io.Stall := controller.io.Hcf        // To Be Modified
    stage_WB.io.pc_plus4_in := datapath_MEM.io.MEM_pc_plus_4
    stage_WB.io.inst_in := stage_MEM.io.inst
    stage_WB.io.alu_out_in := datapath_MEM.io.MEM_alu_out
    stage_WB.io.ld_data_in := datapath_MEM.io.MEM_ld_data

    // WB Block Datapath
    datapath_WB.io.WB_pc_plus4_in := stage_WB.io.pc_plus4
    datapath_WB.io.WB_alu_out_in := stage_WB.io.alu_out
    datapath_WB.io.WB_ld_data_in := stage_WB.io.ld_data
    datapath_WB.io.W_WBSel := controller.io.W_WBSel

    /* controller */
    controller.io.IF_Inst := io.InstMem.rdata
    controller.io.ID_Inst := stage_ID.io.inst
    controller.io.EXE_Inst := stage_EXE.io.inst
    controller.io.MEM_Inst := stage_MEM.io.inst
    controller.io.WB_Inst := stage_WB.io.inst

    controller.io.E_BrEq := datapath_EXE.io.E_BrEq
    controller.io.E_BrLT := datapath_EXE.io.E_BrLT

    controller.io.ID_pc := stage_ID.io.pc
    
    controller.io.EXE_target_pc := datapath_EXE.io.EXE_target_pc_out

    controller.io.IM_Valid := io.InstMem.Valid
    controller.io.DM_Valid := io.DataMem.Valid


    /* System */
    io.regs := datapath_ID.io.regs
    io.Hcf := controller.io.Hcf

    /* Test */
    io.E_Branch_taken := controller.io.E_Branch_taken
    io.Flush := controller.io.Flush_BH
    io.Stall_DH := controller.io.Stall_DH
    io.Stall_MA := controller.io.Stall_MA
    io.IF_PC := stage_IF.io.pc
    io.ID_PC := stage_ID.io.pc
    io.EXE_PC := stage_EXE.io.pc
    io.MEM_PC := stage_MEM.io.pc
    io.WB_PC := Mux(stage_WB.io.pc_plus4 > 0.U ,stage_WB.io.pc_plus4 - 4.U,stage_WB.io.pc_plus4)
    io.EXE_alu_out := datapath_EXE.io.EXE_alu_out
    io.EXE_src1 := datapath_EXE.io.EXE_src1
    io.EXE_src2 := datapath_EXE.io.EXE_src2
    io.ALU_src1 := datapath_EXE.io.alu_src1
    io.ALU_src2 := datapath_EXE.io.alu_src2
    io.WB_wdata := datapath_WB.io.WB_wdata
    io.WB_rd := stage_WB.io.inst(11,7)
    io.EXE_Jump := (stage_EXE.io.inst(6, 0)===JAL) || (stage_EXE.io.inst(6, 0)===JALR)
    io.EXE_Branch := (stage_EXE.io.inst(6, 0)===BRANCH)
    
    // For performance counter
    io.mem_rd_req := (stage_EXE.io.inst(6, 0)===LOAD)
    io.mem_wr_req := (stage_EXE.io.inst(6, 0)===STORE)
    io.mem_req_funct3 := stage_EXE.io.inst(14, 12)
}