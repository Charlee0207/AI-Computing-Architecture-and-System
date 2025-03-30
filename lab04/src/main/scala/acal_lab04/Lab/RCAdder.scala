package acal_lab04.Lab

import chisel3._

class RCAdder (n:Int) extends Module{
  val io = IO(new Bundle{
      val Cin = Input(UInt(1.W))
      val In1 = Input(UInt(n.W))
      val In2 = Input(UInt(n.W))
      val Sum = Output(UInt(n.W))
      val Cout = Output(UInt(1.W))
  })

  //FullAdder ports: A B Cin Sum Cout
  val FA_Array = Array.fill(n)(Module(new FullAdder()).io)
  val carry = Wire(Vec(n+1, UInt(1.W)))
  val sum   = Wire(Vec(n, Bool()))

  carry(0) := io.Cin

  for (i <- 0 until n) {
    FA_Array(i).A := io.In1(i)
    FA_Array(i).B := io.In2(i)
    FA_Array(i).Cin := carry(i)
    carry(i+1) := FA_Array(i).Cout
    sum(i) := FA_Array(i).Sum
  }

  io.Sum := sum.asUInt
  io.Cout := carry(n)
}