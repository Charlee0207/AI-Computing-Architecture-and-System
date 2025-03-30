package acal_lab05.Hw2

import chisel3._
import chisel3.util._

class NegIntGen extends Module{
    val io = IO(new Bundle{
        val key_in = Input(UInt(4.W))
        val value = Output(Valid(UInt(32.W)))
    })

    //please implement your code below

    val add = 10.U
    val mns = 11.U
    val mul = 12.U
    val lpr = 13.U
    val rpr = 14.U
    val equ = 15.U

    val sAccept :: sLpar :: sRpar :: sSign :: sNum :: sEqual :: Nil = Enum(6)
    val state = RegInit(sAccept)

    val in = WireDefault(io.key_in)
    val r_in = RegNext(io.key_in)
    val sign = RegInit(false.B)
    val number = RegInit(0.U(32.W))

    switch(state){
        // is(state): The current state that cursor at
        // in: next input token, ahead of the cursor 

        is(sAccept){
            when        (in===lpr) {state := sLpar}
            .elsewhen   (in<10.U)  {state := sNum}
        }
        // Left parenthese is always followed by posetive, negative sign or digit
        is(sLpar){
            when        (in===add) {state := sSign}
            .elsewhen   (in===mns) {state := sSign}
            .elsewhen   (in<10.U)  {state := sNum}
        }
        // Sign is always followed by digit
        is(sSign){
            when        (in<10.U)  {state := sNum}
        }
        // Num is always followed by right parenthese or equal
        is(sNum){
            when        (in===rpr) {state := sRpar}
            .elsewhen   (in===equ) {state := sEqual}
        }
        // Right parenthese is always followed by equal
        is(sRpar){
            when        (in===equ) {state := sEqual}
        }
        is(sEqual){
            state := sAccept
        }
    }

    when(state===sSign){                                // Use `sign` to record the sign of numbers
        sign := Mux(r_in===mns, true.B, false.B)
    }.elsewhen(state===sNum){                           // Shift the decimal digits
        number := (number<<3.U) + (number<<1.U) + r_in
    }.elsewhen(state===sEqual){                         // Output result
        number := 0.U
        r_in := 0.U
        sign := false.B
    }

    io.value.valid := Mux(state===sEqual, true.B, false.B)
    io.value.bits := Mux(sign, ~number+1.U, number)     // Remember the sign
}