package acal_lab09.PiplinedCPU

import chisel3._
import chisel3.util._

object opcode_map {
  val LOAD = "b0000011".U
  val STORE = "b0100011".U
  val BRANCH = "b1100011".U
  val JALR = "b1100111".U
  val JAL = "b1101111".U
  val OP_IMM = "b0010011".U
  val OP = "b0110011".U
  val AUIPC = "b0010111".U
  val LUI = "b0110111".U
  val HCF = "b0001011".U
}

object condition { // [func3] inst(14, 12)
  val EQ = "b000".U
  val NE = "b001".U
  val LT = "b100".U
  val GE = "b101".U
  val LTU = "b110".U
  val GEU = "b111".U
}

object inst_type {
  val R_type = 0.U
  val I_type = 1.U
  val S_type = 2.U
  val B_type = 3.U
  val J_type = 4.U
  val U_type = 5.U
}

object alu_op_map {
  val ADD = "b0000000_11111_000".U
  val SLL = "b0000000_11111_001".U
  val SLT = "b0000000_11111_010".U
  val SLTU = "b0000000_11111_011".U
  val XOR = "b0000000_11111_100".U
  val SRL = "b0000000_11111_101".U
  val OR = "b0000000_11111_110".U
  val AND = "b0000000_11111_111".U
  val SUB = "b0100000_11111_000".U
  val SRA = "b0100000_11111_101".U

  // Extended Instructions
  val CLZ = "b0110000_00000_001".U
  val CTZ = "b0110000_00001_001".U
  val CPOP = "b0110000_00010_001".U
  val ANDN = "b0100000_11111_111".U
  val ORN = "b0100000_11111_110".U
  val XNOR = "b0100000_11111_100".U
  val MIN = "b0000101_11111_100".U
  val MAX = "b0000101_11111_110".U
  val MINU = "b0000101_11111_101".U
  val MAXU = "b0000101_11111_111".U
  val SEXT_B = "b0110000_00100_001".U
  val SEXT_H = "b0110000_00101_001".U
  val BSET = "b0010100_11111_001".U
  val BCLR = "b0100100_11111_001".U
  val BINV = "b0110100_11111_001".U
  val BEXT = "b0100100_11111_101".U
  // val BSETI = "b0010100_11111_001".U
  // val BCLRI = "b0100100_11111_001".U
  // val BINVI = "b0110100_11111_001".U
  // val BEXTI = "b0100100_11111_101".U
  val ROR = "b0110000_11111_101".U
  val ROL = "b0110000_11111_001".U
  // val RORI = "b0110000_11111_101".U
  val SH1ADD = "b0010000_11111_010".U
  val SH2ADD = "b0010000_11111_100".U
  val SH3ADD = "b0010000_11111_110".U
  val REV8 = "b0110100_11000_101".U
  val ZEXT_H = "b0000100_11111_100".U
  val ORC_B = "b0010100_00111_101".U
}

object pc_sel_map {
  val IF_PC_PLUS_4 = 0.U    // IF stage PC + 4
  val EXE_PC_PLUS_4 = 1.U   // EXE stage PC + 4
  val EXE_T_PC = 2.U        // EXE stage Target PC
}

object wb_sel_map {
  val PC_PLUS_4 = 0.U
  val ALUOUT = 1.U
  val LD_DATA = 2.U
}

// Bonus: Data forwarding control signal
object forwarding_sel_map {
  val EXE_STAGE = 0.U
  val MEM_STAGE = 1.U
  val WB_STAGE = 2.U
  val WBD_STAGE = 3.U
}

object wide {
  val Byte = "b0000".U
  val Half = "b0001".U
  val Word = "b0010".U
  val UByte = "b0100".U
  val UHalf = "b0101".U
  val Vec_8Bytes = "b1000".U
}