# (NTHU_113062559_李承澔) ACAL 2024 Fall Lab 4 Homework Submission Template

###### tags: `AIAS Fall 2024` `AIAS Spring 2024` `Submission Template`


[toc]

## Gitlab code link
:::info
Please paste the link to your private Gitlab repository for this homework submission here. 
:::

- [Gitlab link](https://course.playlab.tw/git/chngh0207/lab04) - 

## Hw4-1 Mix Adder
### Scala Code
> 請放上你的程式碼並加上註解(中英文不限)，讓 TA明白你是如何完成的。
```scala=
package acal_lab04.Hw1

import chisel3._
import acal_lab04.Lab._

class MixAdder (n:Int) extends Module{
  val io = IO(new Bundle{
      val Cin = Input(UInt(1.W))
      val in1 = Input(UInt((4*n).W))
      val in2 = Input(UInt((4*n).W))
      val Sum = Output(UInt((4*n).W))
      val Cout = Output(UInt(1.W))
  })
  //please implement your code below
  
  val CLA_Array = Array.fill(n)(Module(new CLAdder()).io)
  val carry = Wire(Vec(n+1, UInt(1.W)))
  val sum   = Wire(Vec(n, UInt(4.W)))

  // Initialize first carry in
  carry(0) := io.Cin

  for(i <- 0 until n){
    CLA_Array(i).in1  := io.in1((i+1)*4-1, i*4) // Slice the input bits according to the CLA index
    CLA_Array(i).in2  := io.in2((i+1)*4-1, i*4)
    CLA_Array(i).Cin  := carry(i)               
    carry(i+1)        := CLA_Array(i).Cout      
    sum(i)            := CLA_Array(i).Sum
  }
  io.Sum := sum.asUInt
  io.Cout:= carry(n)
}
```
### Test Result
> 請放上你通過test的結果，驗證程式碼的正確性。(螢幕截圖即可)

![](https://course.playlab.tw/md/uploads/73282d19-a65e-4e1d-867b-95bd8b19f80d.png)


## Hw4-2 Add-Suber
### Scala Code
> 請放上你的程式碼並加上註解(中英文不限)，讓 TA明白你是如何完成的。
```scala=
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
```
### Test Result
> 請放上你通過test的結果，驗證程式碼的正確性。(螢幕截圖即可)

![](https://course.playlab.tw/md/uploads/3ebea965-aa9a-430a-a1c7-62226f793e85.png)


## Hw4-3 Booth Multiplier
### Scala Code
> 請放上你的程式碼並加上註解(中英文不限)，讓 TA明白你是如何完成的。
```scala=
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
```
### Test Result
> 請放上你通過test的結果，驗證程式碼的正確性。(螢幕截圖即可)

![](https://course.playlab.tw/md/uploads/22e5c654-ad58-47c2-9163-d803b0c48619.png)


## Hw4-4 Queue Implementation
#### Scala Code
> 請放上你的程式碼並加上註解(中英文不限)，讓 TA明白你是如何完成的。
```scala=
package acal_lab04.Lab

import chisel3._
import chisel3.util.log2Ceil

class Queue(val depth: Int) extends Module {
  val io = IO(new Bundle {
    val push    = Input(Bool())
    val pop     = Input(Bool())
    val en      = Input(Bool())
    val dataIn  = Input(UInt(32.W))
    val dataOut = Output(UInt(32.W))
    val empty   = Output(Bool())
    val full    = Output(Bool())
  })

  val queue_mem     = Mem(depth, UInt(32.W))                 
  val dataOutput    = RegInit(0.U(32.W))             
  val dataOutput_FF = RegInit(0.U(32.W))
  val head          = RegInit(0.U(log2Ceil(depth+1).W))
  val length        = RegInit(0.U(log2Ceil(depth+1).W))


  when (io.en) {
    when(io.push && !io.full) {
      queue_mem(head+length) := io.dataIn
      length := length+1.U
    }
    .elsewhen(io.pop && !io.empty) {
      head := (head+1.U) % depth.asUInt
      length := length-1.U

      // dataOutput updated only when pop signal assert
      dataOutput := queue_mem(head)
    }

    // io.dataOut has one cycle latency after pop signal
    // To conform the testbench, add a FF to delay one cycle
    dataOutput_FF := dataOutput
  }

  io.dataOut  := dataOutput_FF
  io.empty    := length === 0.U
  io.full     := length === depth.asUInt

}

```
### Test Result
> 請放上你通過test的結果，驗證程式碼的正確性。(螢幕截圖即可)

![](https://course.playlab.tw/md/uploads/8036500c-ab0c-429f-b9fa-ce3973958be4.png)


## Hw4-5 Bus Implementation
#### Scala Code
> 請放上你的程式碼並加上註解(中英文不限)，讓 TA明白你是如何完成的。
```scala=
package acal_lab04.Lab

import chisel3._
import chisel3.util._
import chisel3.util.log2Ceil


class MultiShareBus(val addrWidth: Int,val dataWidth: Int,val numMasters: Int,val numSlaves: Int, val addrMap: Seq[(Int, Int)]) extends Module {
  val io = IO(new Bundle {
    val masters = Vec(numMasters, Flipped(Decoupled(new MasterInterface(addrWidth, dataWidth))))
    val slaves = Vec(numSlaves, Decoupled(new SlaveInterface(addrWidth, dataWidth)))
  })
    // decoder
    val decoders = Seq.tabulate(numSlaves) { i =>
      Module(new Decoder(addrWidth, addrMap.slice(i, i+1)))
    }


    // // two master arbiter (round robin)
    // // use one bit to record which master is last granted
    val lastGrant       = RegInit(0.U(1.W)) // only two masters
    val grant           = RegInit(0.U(1.W)) // only two masters
    val timer           = RegInit(0.U(1.W))
    val multiReq        = Wire(Bool())

    // initialize the ready signal
    for(i <- 0 until numMasters){
      io.masters(i).ready   := false.B 
    }
    multiReq := io.masters(0).valid && io.masters(1).valid

    // Since the masters and the slaves never deassert valid and ready signals after asserting,
    // we cannot change the grant FF using valid and ready signal
    // However, the transaction in this bus always cost 2 cycles, 
    // we use timer to update grant register  
    when(multiReq){ timer := timer + 1.U }
    when(timer===1.U){ lastGrant := ~lastGrant}

    // If there're multi request, grant the master using updated lastGrant
    // Else, simply grant the requested master
    grant := Mux( multiReq, 
                  lastGrant, 
                  Mux(io.masters(0).valid, 0.U, 1.U))

    // OR all ready signals, and assign to granted master
    io.masters(grant).ready := io.slaves.map(_.ready).reduce(_ || _)   

    // Initialize signals
    for (i <- 0 until numSlaves) {
      io.slaves(i).valid      := io.masters(grant).valid && decoders(i).io.select
      io.slaves(i).bits.addr  := io.masters(grant).bits.addr
      io.slaves(i).bits.data  := io.masters(grant).bits.data
      io.slaves(i).bits.size  := io.masters(grant).bits.size
      decoders(i).io.addr     := io.masters(grant).bits.addr
    }

}
```
### Decoder
#### Scala Code
> 請放上你的程式碼並加上註解(中英文不限)，讓 TA明白你是如何完成的。

I just import the lab package, which decoder is the same as the decoder in lab4-5.
```scala=
class Decoder(addrWidth: Int, addrMap: Seq[(Int, Int)]) extends Module {
    val io = IO(new Bundle {
        val addr = Input(UInt(addrWidth.W))
        val select = Output(Bool())
    })
    val select = addrMap.zipWithIndex.foldLeft(false.B) { case (result, ((startAddress, size), index)) =>
      result || (io.addr >= startAddress.U && io.addr < (startAddress + size).U)
    }
    io.select := select
}
```
### Aribiter
#### Scala Code
> 請放上你的程式碼並加上註解(中英文不限)，讓 TA明白你是如何完成的。

This snippet of code is excerpted from above.
```scala=
    // // two master arbiter (round robin)
    // // use one bit to record which master is last granted
    val lastGrant       = RegInit(0.U(1.W)) // only two masters
    val grant           = RegInit(0.U(1.W)) // only two masters
    val timer           = RegInit(0.U(1.W))
    val multiReq        = Wire(Bool())

    // initialize the ready signal
    for(i <- 0 until numMasters){
      io.masters(i).ready   := false.B 
    }
    multiReq := io.masters(0).valid && io.masters(1).valid

    // Since the masters and the slaves never deassert valid and ready signals after asserting,
    // we cannot change the grant FF using valid and ready signal
    // However, the transaction in this bus always cost 2 cycles, 
    // we use timer to update grant register  
    when(multiReq){ timer := timer + 1.U }
    when(timer===1.U){ lastGrant := ~lastGrant}

    // If there're multi request, grant the master using updated lastGrant
    // Else, simply grant the requested master
    grant := Mux( multiReq, 
                  lastGrant, 
                  Mux(io.masters(0).valid, 0.U, 1.U))

    // OR all ready signals, and assign to granted master
    io.masters(grant).ready := io.slaves.map(_.ready).reduce(_ || _)   
```
### Test Result
> 請放上你通過test的結果，驗證程式碼的正確性。(螢幕截圖即可)

![](https://course.playlab.tw/md/uploads/af3f7249-8097-49f1-9f1a-27a19fdc95a6.png)

## 意見回饋和心得(可填可不填)
