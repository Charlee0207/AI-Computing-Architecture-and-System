package acal_lab04.Lab

import chisel3._

class HalfAdder extends Module{
  val io = IO(new Bundle{
    val A = Input(UInt(1.W))
    val B = Input(UInt(1.W))
    val Sum = Output(UInt(1.W))
    val Carry = Output(UInt(1.W))
  })
  //the behavior of circuit
  io.Sum := io.A ^ io.B
  io.Carry := io.A & io.B
}

class FullAdder extends Module{
  val io = IO(new Bundle{
    val A = Input(UInt(1.W))
    val B = Input(UInt(1.W))
    val Cin = Input(UInt(1.W))
    val Sum = Output(UInt(1.W))
    val Cout = Output(UInt(1.W))
  })

  //Module Declaration
  val ha1 = Module(new HalfAdder())
  val ha2 = Module(new HalfAdder())

  //Wiring
  ha1.io.A := io.A
  ha1.io.B := io.B

  ha2.io.A := ha1.io.Sum
  ha2.io.B := io.Cin

  io.Sum := ha2.io.Sum
  io.Cout := ha1.io.Carry | ha2.io.Carry
}

