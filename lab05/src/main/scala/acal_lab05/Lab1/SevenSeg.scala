package acal_lab05.Lab1

import chisel3._
import chisel3.util._

class SevenSeg extends Module{
    val io = IO(new Bundle{
        val num = Input(UInt(4.W)) //0~9 need 4 bit
        val display = Output(UInt(7.W))
    })

    val segs = Wire(UInt(7.W))
    //Default
    segs := 0.U
    switch(io.num){
        is(0.U){segs := "b1111110".U}
        is(1.U){segs := "b0110000".U}
        is(2.U){segs := "b1101101".U}
        is(3.U){segs := "b1111001".U}
        is(4.U){segs := "b0110011".U}
        is(5.U){segs := "b1011011".U}
        is(6.U){segs := "b1011111".U}
        is(7.U){segs := "b1110000".U}
        is(8.U){segs := "b1111111".U}
        is(9.U){segs := "b1111011".U}
    }

    io.display := segs.asUInt
}