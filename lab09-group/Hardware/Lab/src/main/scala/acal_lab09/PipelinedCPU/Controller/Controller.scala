package acal_lab09.PiplinedCPU.Controller

import chisel3._
import chisel3.util._

import acal_lab09.PiplinedCPU.opcode_map._
import acal_lab09.PiplinedCPU.condition._
import acal_lab09.PiplinedCPU.inst_type._
import acal_lab09.PiplinedCPU.alu_op_map._
import acal_lab09.PiplinedCPU.pc_sel_map._
import acal_lab09.PiplinedCPU.wb_sel_map._
import acal_lab09.PiplinedCPU.forwarding_sel_map._

class Controller(memAddrWidth: Int) extends Module {
  val io = IO(new Bundle {
    // Memory control signal interface
    val IM_Mem_R = Output(Bool())
    val IM_Mem_W = Output(Bool())
    val IM_Length = Output(UInt(4.W))
    val IM_Valid = Input(Bool())

    val DM_Mem_R = Output(Bool())
    val DM_Mem_W = Output(Bool())
    val DM_Length = Output(UInt(4.W))
    val DM_Valid = Input(Bool())

    // branch Comp.
    val E_BrEq = Input(Bool())
    val E_BrLT = Input(Bool())

    // Branch Prediction
    val E_Branch_taken = Output(Bool())
    val E_En = Output(Bool())

    val ID_pc = Input(UInt(memAddrWidth.W))
    val EXE_target_pc = Input(UInt(memAddrWidth.W))

    // Flush
    val Flush_WB_ID_DH = Output(Bool()) //TBD
    val Flush_MEM_ID_DH = Output(Bool()) //TBD
    val Flush_EXE_ID_DH = Output(Bool()) //TBD
    val Flush_BH = Output(Bool()) //TBD

    // Stall
    // To Be Modified
    val Stall_WB_ID_DH = Output(Bool()) //TBD
    val Stall_MEM_ID_DH = Output(Bool()) //TBD
    val Stall_EXE_ID_DH = Output(Bool()) //TBD
    val Stall_MA = Output(Bool())
    val Stall_DH = Output(Bool())

    // inst
    val IF_Inst = Input(UInt(32.W))
    val ID_Inst = Input(UInt(32.W))
    val EXE_Inst = Input(UInt(32.W))
    val MEM_Inst = Input(UInt(32.W))
    val WB_Inst = Input(UInt(32.W))

    // sel
    val PCSel = Output(UInt(2.W))
    val D_ImmSel = Output(UInt(3.W))
    val W_RegWEn = Output(Bool())
    val E_BrUn = Output(Bool())
    val E_ASel = Output(UInt(2.W))
    val E_BSel = Output(UInt(1.W))
    val E_ALUSel = Output(UInt(15.W))
    val W_WBSel = Output(UInt(2.W))

    val Hcf = Output(Bool())
  })
  // Inst Decode for each stage
  val IF_opcode = io.IF_Inst(6, 0)

  val ID_opcode = io.ID_Inst(6, 0)

  val EXE_opcode = io.EXE_Inst(6, 0)
  val EXE_funct3 = io.EXE_Inst(14, 12)
  val EXE_funct7 = io.EXE_Inst(31, 25)

  val MEM_opcode = io.MEM_Inst(6, 0)
  val MEM_funct3 = io.MEM_Inst(14, 12)

  val WB_opcode = io.WB_Inst(6, 0)

  // Control signal - Branch/Jump
  val E_En = Wire(Bool())
  E_En := (EXE_opcode===BRANCH || EXE_opcode===JAL || EXE_opcode===JALR)         // To Be Modified
  val E_Branch_taken = Wire(Bool())
  E_Branch_taken := MuxLookup(EXE_opcode, false.B, Seq(
          BRANCH -> MuxLookup(EXE_funct3, false.B, Seq(
            "b000".U(3.W) -> io.E_BrEq.asUInt,  // beq
            "b001".U(3.W) -> ~io.E_BrEq.asUInt,  // bne
            "b100".U(3.W) -> io.E_BrLT.asUInt,  // blt  
            "b101".U(3.W) -> (~io.E_BrLT).asUInt,  // bge  
            "b110".U(3.W) -> io.E_BrLT.asUInt,  // bltu  
            "b111".U(3.W) -> (~io.E_BrLT).asUInt   // bgeu  
          )),
          JALR -> true.B,
          JAL -> true.B
        ))    // To Be Modified DONE

  io.E_En := E_En
  io.E_Branch_taken := E_Branch_taken

  // pc predict miss signal
  val Predict_Miss = Wire(Bool())
  Predict_Miss := (E_En && E_Branch_taken && io.ID_pc=/=io.EXE_target_pc)

  // Control signal - PC
  when(Predict_Miss){
    io.PCSel := EXE_T_PC
  }.otherwise{
    io.PCSel := IF_PC_PLUS_4
  }

  // Control signal - Branch comparator
  io.E_BrUn := (io.EXE_Inst(13) === 1.U)

  // Control signal - Immediate generator
  io.D_ImmSel := MuxLookup(ID_opcode, 0.U, Seq(
    OP -> R_type,
    OP_IMM -> I_type,
    LOAD -> I_type,
    STORE -> S_type,
    BRANCH -> B_type,
    JALR -> J_type,
    JAL -> J_type,
    LUI -> U_type,
    AUIPC -> U_type
  )) // To Be Modified DONE

  // Control signal - Scalar ALU
  io.E_ASel := MuxLookup(EXE_opcode, 0.U, Seq(
    OP -> 0.U,
    OP_IMM -> 0.U,
    LOAD -> 0.U,
    STORE -> 0.U,
    BRANCH -> 1.U,
    JALR -> 0.U,
    JAL -> 1.U,
    LUI -> 2.U,
    AUIPC -> 1.U
  ))    // To Be Modified DONE
  io.E_BSel := MuxLookup(EXE_opcode, 0.U, Seq(
    OP -> 0.U,
    OP_IMM -> 1.U,
    LOAD -> 1.U,
    STORE -> 1.U,
    BRANCH -> 1.U,
    JALR -> 1.U,
    JAL -> 1.U,
    LUI -> 1.U,
    AUIPC -> 1.U
  ))    // To Be Modified DONE

  io.E_ALUSel := MuxLookup(EXE_opcode, (Cat(0.U(7.W), "b11111".U, 0.U(3.W))), Seq(
    OP -> (Cat(EXE_funct7, "b11111".U, EXE_funct3)),
    OP_IMM -> MuxLookup(io.EXE_Inst(31, 25), Cat("b0000000_11111".U, EXE_funct3), Seq(
                "b0100000".U -> Mux(EXE_funct3==="b101".U, "b0100000_11111_101".U, "b0000000_11111_101".U),  // If SRAI, else SUB
                "b0110000".U -> MuxLookup(Cat(io.EXE_Inst(24, 20), EXE_funct3), Cat("b0110000_11111".U, EXE_funct3), Seq(
                                  "b00000_001".U -> "b0110000_00000_001".U,   // CLZ
                                  "b00001_001".U -> "b0110000_00001_001".U,   // CTZ
                                  "b00010_001".U -> "b0110000_00010_001".U,   // CPOP
                                  "b00100_001".U -> "b0110000_00100_001".U,   // SEXT_B
                                  "b00101_001".U -> "b0110000_00101_001".U    // SEXT_H
                                )),
                "b0010100".U -> Mux(EXE_funct3==="b001".U, "b0010100_11111_001".U, "b0010100_00111_101".U),   // If BSETI, else ORC_B
                "b0100100".U -> Mux(EXE_funct3==="b001".U ,"b0100100_11111_001".U, "b0100100_11111_101".U),   // If BCLRI, else BEXTI
                "b0110100".U -> Mux(EXE_funct3==="b001".U, "b0110100_11111_001".U, "b0110100_11000_101".U)    // If BINVI, else REV8
              )),
    LOAD ->   "b0000000_11111_000".U,  // ADD
    STORE ->  "b0000000_11111_000".U,  // ADD
    BRANCH -> "b0000000_11111_000".U,  // ADD
    JALR ->   "b0000000_11111_000".U,  // ADD
    JAL ->    "b0000000_11111_000".U,  // ADD
    LUI ->    "b0000000_11111_000".U,  // ADD
    AUIPC ->  "b0000000_11111_000".U,  // ADD
  )) // To Be Modified DONE

  // Control signal - Data Memory
  io.DM_Mem_R := (MEM_opcode===LOAD)
  io.DM_Mem_W := (MEM_opcode===STORE)
  io.DM_Length := Cat(0.U(1.W),MEM_funct3) // length

  // Control signal - Inst Memory
  io.IM_Mem_R := true.B // always true
  io.IM_Mem_W := false.B // always false
  io.IM_Length := "b0010".U // always load a word(inst)

  // Control signal - Scalar Write Back
  io.W_RegWEn := MuxLookup(WB_opcode, false.B, Seq(
    OP -> true.B,
    OP_IMM -> true.B,
    LOAD -> true.B,
    STORE -> false.B,
    BRANCH -> false.B,
    JALR -> true.B,
    JAL -> true.B,
    LUI -> true.B,
    AUIPC -> true.B
  ))  // To Be Modified DONE


  io.W_WBSel := MuxLookup(WB_opcode, ALUOUT, Seq(
    OP -> ALUOUT,
    OP_IMM -> ALUOUT,
    LOAD -> LD_DATA,
    STORE -> ALUOUT,    // By default
    BRANCH -> ALUOUT,   // By default
    JALR -> PC_PLUS_4,
    JAL -> PC_PLUS_4,
    LUI -> ALUOUT,
    AUIPC -> ALUOUT
  )) // To Be Modified DONE

  // Control signal - Others
  io.Hcf := (IF_opcode === HCF)

/****************** Data Hazard ******************/
  val ID_rs1 = io.ID_Inst(19, 15)
  val ID_rs2 = io.ID_Inst(24, 20)
  val WB_rd = io.WB_Inst(11, 7)
  val MEM_rd = io.MEM_Inst(11, 7)
  val EXE_rd = io.EXE_Inst(11, 7)

  // Use rs in ID stage 
  val is_D_use_rs1 = Wire(Bool()) 
  val is_D_use_rs2 = Wire(Bool())
  is_D_use_rs1 := MuxLookup(ID_opcode,false.B,Seq(
    OP -> true.B,
    OP_IMM -> true.B,
    LOAD -> true.B,
    STORE -> true.B,
    BRANCH -> true.B,
    JALR -> true.B,
    JAL -> false.B,
    LUI -> false.B,
    AUIPC -> false.B
  ))   // To Be Modified DONE
  is_D_use_rs2 := MuxLookup(ID_opcode,false.B,Seq(
    OP -> true.B,
    OP_IMM -> false.B,
    LOAD -> false.B,
    STORE -> true.B,
    BRANCH -> true.B,
    JALR -> false.B,
    JAL -> false.B,
    LUI -> false.B,
    AUIPC -> false.B
  ))   // To Be Modified DONE

  // Use rd in WB stage
  val is_W_use_rd = Wire(Bool())
  is_W_use_rd := MuxLookup(WB_opcode,false.B,Seq(
    OP -> true.B,
    OP_IMM -> true.B,
    LOAD -> true.B,
    STORE -> false.B,
    BRANCH -> false.B,
    JALR -> true.B,
    JAL -> true.B,
    LUI -> true.B,
    AUIPC -> true.B
  ))   // To Be Modified DONE  
  // Use rd in MA stage
  val is_M_use_rd = Wire(Bool())
  is_M_use_rd := MuxLookup(MEM_opcode,false.B,Seq(
    OP -> true.B,
    OP_IMM -> true.B,
    LOAD -> true.B,
    STORE -> false.B,
    BRANCH -> false.B,
    JALR -> true.B,
    JAL -> true.B,
    LUI -> true.B,
    AUIPC -> true.B
  ))   // To Be Modified DONE
    // Use rd in WB stage
  val is_E_use_rd = Wire(Bool())
  is_E_use_rd := MuxLookup(EXE_opcode,false.B,Seq(
    OP -> true.B,
    OP_IMM -> true.B,
    LOAD -> true.B,
    STORE -> false.B,
    BRANCH -> false.B,
    JALR -> true.B,
    JAL -> true.B,
    LUI -> true.B,
    AUIPC -> true.B
  ))   // To Be Modified DONE

  // Hazard condition (rd, rs overlap)
  val is_D_rs1_W_rd_overlap = Wire(Bool())
  val is_D_rs2_W_rd_overlap = Wire(Bool())
  val is_D_rs1_M_rd_overlap = Wire(Bool())
  val is_D_rs2_M_rd_overlap = Wire(Bool())
  val is_D_rs1_E_rd_overlap = Wire(Bool())
  val is_D_rs2_E_rd_overlap = Wire(Bool())

  is_D_rs1_W_rd_overlap := is_D_use_rs1 && is_W_use_rd && (ID_rs1 === WB_rd) && (WB_rd =/= 0.U(5.W))
  is_D_rs2_W_rd_overlap := is_D_use_rs2 && is_W_use_rd && (ID_rs2 === WB_rd) && (WB_rd =/= 0.U(5.W))
  is_D_rs1_M_rd_overlap := is_D_use_rs1 && is_M_use_rd && (ID_rs1 === MEM_rd) && (MEM_rd =/= 0.U(5.W))
  is_D_rs2_M_rd_overlap := is_D_use_rs2 && is_M_use_rd && (ID_rs2 === MEM_rd) && (MEM_rd =/= 0.U(5.W))
  is_D_rs1_E_rd_overlap := is_D_use_rs1 && is_E_use_rd && (ID_rs1 === EXE_rd) && (EXE_rd =/= 0.U(5.W))
  is_D_rs2_E_rd_overlap := is_D_use_rs2 && is_E_use_rd && (ID_rs2 === EXE_rd) && (EXE_rd =/= 0.U(5.W))


  // Control signal - Stall
  // Stall for Data Hazard
  io.Stall_WB_ID_DH := (is_D_rs1_W_rd_overlap || is_D_rs2_W_rd_overlap)
  io.Stall_MEM_ID_DH := (is_D_rs1_M_rd_overlap || is_D_rs2_M_rd_overlap)
  io.Stall_EXE_ID_DH := (is_D_rs1_E_rd_overlap || is_D_rs2_E_rd_overlap)
  io.Stall_MA := false.B
  io.Stall_DH := (io.Stall_WB_ID_DH || io.Stall_MEM_ID_DH || io.Stall_EXE_ID_DH || io.Stall_MA)
  // Control signal - Flush
  io.Flush_BH := Predict_Miss
  io.Flush_WB_ID_DH := (is_D_rs1_W_rd_overlap || is_D_rs2_W_rd_overlap)
  io.Flush_MEM_ID_DH := (is_D_rs1_M_rd_overlap || is_D_rs2_M_rd_overlap)
  io.Flush_EXE_ID_DH := (is_D_rs1_E_rd_overlap || is_D_rs2_E_rd_overlap)
  // Control signal - Data Forwarding (Bonus)
  /****************** Data Hazard End******************/


}
