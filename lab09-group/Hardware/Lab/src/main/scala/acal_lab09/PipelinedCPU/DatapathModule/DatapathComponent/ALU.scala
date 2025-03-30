package acal_lab09.PiplinedCPU.DatapathModule.DatapathComponent

import chisel3._
import chisel3.util._

import acal_lab09.PiplinedCPU.opcode_map._
import acal_lab09.PiplinedCPU.alu_op_map._

class ALUIO extends Bundle{
  val src1    = Input(UInt(32.W))
  val src2    = Input(UInt(32.W))
  val ALUSel  = Input(UInt(15.W))
  val out  = Output(UInt(32.W))
}

class ALU extends Module{
  val io = IO(new ALUIO)

  io.out := 0.U
  switch(io.ALUSel){
    is(ADD ){io.out := io.src1+io.src2}
    is(SLL ){io.out := io.src1 << io.src2(4,0)}
    is(SLT ){io.out := Mux(io.src1.asSInt < io.src2.asSInt,1.U,0.U)}
    is(SLTU){io.out := Mux(io.src1 < io.src2              ,1.U,0.U)}
    is(XOR ){io.out := io.src1^io.src2}
    is(SRL ){io.out := io.src1 >> io.src2(4,0)}
    is(OR  ){io.out := io.src1|io.src2}
    is(AND ){io.out := io.src1&io.src2}
    is(SUB ){io.out := io.src1-io.src2}
    is(SRA ){io.out := (io.src1.asSInt >> io.src2(4,0)).asUInt}

    // Extended Instructions
    is(CLZ   ){io.out := PriorityEncoder(Reverse(io.src1))}
    is(CTZ   ){io.out := PriorityEncoder(io.src1)}
    is(CPOP  ){io.out := PopCount(io.src1)}
    is(ANDN  ){io.out := io.src1 & ~io.src2}
    is(ORN   ){io.out := io.src1 | ~io.src2}
    is(XNOR  ){io.out := ~(io.src1 ^ io.src2)}
    is(MIN   ){io.out := Mux(io.src1.asSInt>io.src2.asSInt, io.src2, io.src1)}
    is(MAX   ){io.out := Mux(io.src1.asSInt>io.src2.asSInt, io.src1, io.src2)}
    is(MINU  ){io.out := Mux(io.src1.asUInt>io.src2.asUInt, io.src2, io.src1)}
    is(MAXU  ){io.out := Mux(io.src1.asUInt>io.src2.asUInt, io.src1, io.src2)}
    is(SEXT_B){io.out := io.src1(7, 0).asSInt.asUInt}
    is(SEXT_H){io.out := io.src1(15, 0).asSInt.asUInt}
    is(BSET  ){io.out := io.src1 | (1.U << io.src2(4, 0))}
    is(BCLR  ){io.out := io.src1 & ~(1.U << io.src2(4, 0))}
    is(BINV  ){io.out := io.src1 ^ (1.U << io.src2(4, 0))}
    is(BEXT  ){io.out := (io.src1 >> io.src2(4, 0)) & 1.U}
    // is(BSETI ){io.out := io.src1 | (1.U << io.src2(4, 0))}
    // is(BCLRI ){io.out := io.src1 & ~(1.U << io.src2(4, 0))}
    // is(BINVI ){io.out := io.src1 ^ (1.U << io.src2(4, 0))}
    // is(BEXTI ){io.out := (io.src1 >> io.src2(4, 0)) & 1.U}
    is(ROR   ){io.out := (io.src1 >> io.src2(4, 0)) | (io.src1 << (32.U-io.src2(4, 0)))}
    is(ROL   ){io.out := (io.src1 << io.src2(4, 0)) | (io.src1 >> (32.U-io.src2(4, 0)))}
    // is(RORI  ){io.out := (io.src1 >> io.src2(4, 0)) | (io.src2 << (32.U-io.src2(4, 0)))}
    is(SH1ADD){io.out := (io.src1 << 1.U) + io.src2}
    is(SH2ADD){io.out := (io.src1 << 2.U) + io.src2}
    is(SH3ADD){io.out := (io.src1 << 3.U) + io.src2}
    is(REV8  ){io.out := Cat((0 until 4).map(i => io.src1((4-i)*8-1, (4-i-1)*8)).reverse)}
    is(ZEXT_H){io.out := io.src1(15, 0)}
    is(ORC_B ){io.out := Cat((0 until 4).map{i =>
                            val byte = io.src1((i + 1) * 8 - 1, i * 8) // Extract each byte
                            Mux(byte.orR, Fill(8, 1.U), 0.U(8.W))   // Check if any bit is set in the byte
                          }.reverse)}
    // ==============================
  }
}

