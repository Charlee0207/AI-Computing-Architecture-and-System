package acal_lab04.Hw3

import chisel3._
import chisel3.util._
import scala.annotation.switch

//------------------Radix 4---------------------
class Booth_Mul(width:Int) extends Module {
  val io = IO(new Bundle{
    val in1 = Input(UInt(width.W))      //Multiplicand
    val in2 = Input(UInt(width.W))      //Multiplier
    val out = Output(UInt((2*width).W)) //product
  })
  //please implement your code below

  val multiplicand = Wire(SInt(width.W))
  val multiplier = Wire(SInt((width+1).W))       // To handle the extra bit, i.e. y[-1]=0    
  val weight = Wire(Vec((width/2), SInt(width.W)) ) 
  val partialSum = Wire(Vec((width/2), SInt((2*width).W)))  
  val result = Wire(SInt((2*width).W))
  multiplicand := io.in1.asSInt
  multiplier := io.in2.asSInt<<1

  // Slide window with stride of 2
  for(idx <- 0 until width/2){    

    // Since there is an extra bit y[-1], which has actual index 0 in multiplier,
    // the sliding window has to scan the multilpier with offset index

    // Y[1] => multiplier[n+2], Y[0] => multiplier[n+1], Y[-1] => multiplier[n]
    // where n starts from 0
    weight(idx) := (-2.S*multiplier(idx*2+2) + 1.S*multiplier(idx*2+1) + 1.S*multiplier(idx*2))

    // And multiply with the idx power of 2
    partialSum(idx) := (weight(idx)*multiplicand) << (idx*2)
  }
  // Accumulate all partial sum
  result := partialSum.foldLeft(0.S((2*width).W))(_ + _)
  io.out := result.asUInt;
}


