package acal_lab04.Hw2

import chisel3._
import chisel3.util._
import acal_lab04.Lab._

class Add_Suber extends Module{
  val io = IO(new Bundle{
  val in_1 = Input(UInt(4.W))
	val in_2 = Input(UInt(4.W))
	val op = Input(Bool()) // 0:ADD 1:SUB
	val out = Output(UInt(4.W))
	val o_f = Output(Bool())
  })

  //please implement your code below
  val n = 4
  val FA_Array  = Array.fill(n)(Module(new FullAdder()).io)
  val carry     = Wire(Vec(n+1, UInt(1.W)))
  val sum       = Wire(Vec(n, UInt(1.W)))
  val addend    = Wire(UInt(n.W))

  // Initialize first carry in
  carry(0)  := 0.U

  // Convert in_2 to 2's complement if op==1
  addend    := Mux(io.op, ~io.in_2+1.U, io.in_2) 

  for(i <- 0 until n){
    FA_Array(i).A   := io.in_1(i)
    FA_Array(i).B   := addend(i)
    FA_Array(i).Cin := carry(i)
    carry(i+1)      := FA_Array(i).Cout
    sum(i)          := FA_Array(i).Sum
  }
  // Concat the sign bit (carry(n)) and the sum in signed int
  val catResult = Cat(carry(n), sum.asUInt).asSInt
  io.out := sum.asUInt

  // overflow
  io.o_f := (catResult < -8.S || catResult > 7.S)
}
