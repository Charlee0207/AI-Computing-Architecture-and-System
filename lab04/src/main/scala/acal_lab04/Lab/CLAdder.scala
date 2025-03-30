package acal_lab04.Lab

import chisel3._

class CLAdder extends Module{
  val io = IO(new Bundle{
      val in1 = Input(UInt(4.W))
      val in2 = Input(UInt(4.W))
      val Cin = Input(UInt(1.W))
      val Sum = Output(UInt(4.W))
      val Cout = Output(UInt(1.W))
  })

  val P = Wire(Vec(4,UInt()))
  val G = Wire(Vec(4,UInt()))
  val C = Wire(Vec(4,UInt()))
  val S = Wire(Vec(4,UInt()))

  for(i <- 0 until 4){
      G(i) := io.in1(i) & io.in2(i)
      P(i) := io.in1(i) | io.in2(i)
  }

  C(0) := io.Cin
  C(1) := G(0)|(P(0)&C(0))
  C(2) := G(1)|(P(1)&G(0))|(P(1)&P(0)&C(0))
  C(3) := G(2)|(P(2)&G(1))|(P(2)&P(1)&G(0))|(P(2)&P(1)&P(0)&C(0))

  val FA_Array = Array.fill(4)(Module(new FullAdder).io)

  for(i <- 0 until 4){
      FA_Array(i).A := io.in1(i)
      FA_Array(i).B := io.in2(i)
      FA_Array(i).Cin := C(i)
      S(i) := FA_Array(i).Sum
  }

  io.Sum := S.asUInt
  io.Cout := FA_Array(3).Cout
}
