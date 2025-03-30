package acal_lab05.Hw2

import chisel3._
import chisel3.util._

class LongCal extends Module{
    val io = IO(new Bundle{
        val key_in = Input(UInt(4.W))
        val value = Output(Valid(UInt(32.W)))
    })

    //please implement your code below

    val add = 10.U
    val sub = 11.U
    val mul = 12.U
    val lpr = 13.U
    val rpr = 14.U
    val equ = 15.U

    val sSrc1 :: sSrc2 :: sSgn1 :: sSgn2 :: sOp :: sEqual :: Nil = Enum(6)
    val state = RegInit(sSrc1)

    val in = WireDefault(io.key_in)
    val r_in = RegNext(io.key_in)
    val sgn1 = RegInit(false.B)
    val sgn2 = RegInit(false.B)
    val src1 = RegInit(0.U(32.W))
    val src2 = RegInit(0.U(32.W))
    val op = RegInit(equ)
    val result = RegInit(0.U(32.W))

    
    val isNum = Wire(Bool())
    val isOp = Wire(Bool())
    val isEqu = Wire(Bool())
    val r_isNum = Wire(Bool())
    val r_isOp = Wire(Bool())
    val r_isEqu = Wire(Bool())
    isNum := (in<10.U)
    isOp := (in>=10.U && in<=12.U) 
    isEqu := (in===15.U)
    r_isNum := (r_in<10.U)
    r_isOp := (r_in>=10.U && r_in<=12.U) 
    r_isEqu := (r_in===15.U)

    switch(state){
        // is(state): The current state that cursor at
        // in: next input token, ahead of the cursor 

        // At the state Src1 (the default state), 
        // the next token may be `(` `op` or `=` 
        is(sSrc1){
            when        (in===lpr)  {state := sSgn1}
            .elsewhen   (isOp)      {state := sOp}
            .elsewhen   (isEqu)     {state := sEqual}
        }
        // If previous token is `(`, we may need to handle negative number
        is(sSgn1){
            when        (isNum)     {state := sSrc1}
        }
        // If operator is followed by `(`, we may need to handle negative number
        // otherwise, it would be simple positive number
        is(sOp){
            when        (in===lpr)  {state := sSgn2}
            .otherwise              {state := sSrc2}      
        }
        // src2 would be only followed by operator or equality 
        is(sSrc2){
            when        (isOp)      {state := sOp}
            .elsewhen   (isEqu)     {state := sEqual}
        }
        // Sign of src2 would be only followed by digits of src2
        is(sSgn2){
            when        (isNum)     {state := sSrc2}
        }
        // Go to sSrc1 state to be ready for receiving next testcase
        is(sEqual){
            state := sSrc1
        }
    }


    when(state===sSrc1){
        when(r_isNum){                                      // Shift decimal digits
            src1 := (src1<<3.U) + (src1<<1.U) + r_in
        }
        .elsewhen(r_in===rpr){                              // Record the SInt representation of number 
            src1 := Mux(sgn1, ~src1+1.U, src1)  
        }
    }
    .elsewhen(state===sSgn1){                               // If registered input has minus sign, assert sgn flag
        sgn1 := Mux((r_in===sub), true.B, false.B)
    }
    .elsewhen(state===sOp){                                 // Do operations
        when(op>=10.U && op<=12.U){
            switch(op){
                is(add) {src1 := src1 + src2}
                is(sub) {src1 := src1 - src2}
                is(mul) {src1 := src1 * src2}
            }
        }
        op := r_in
        sgn1 := false.B
        sgn2 := false.B
        src2 := 0.U
    }
    .elsewhen(state===sSrc2){                               
        when(r_isNum){                                      // Shift decimal digits
            src2 := (src2<<3.U) + (src2<<1.U) + r_in
        }
        .elsewhen(r_in===rpr){                              // Record the SInt representation of number 
            src2 := Mux(sgn2, ~src2+1.U, src2)
        }
    }
    .elsewhen(state===sSgn2){                               // If registered input has minus sign, assert sgn flag
        sgn2 := Mux((r_in===sub), true.B, false.B)
    }
    .elsewhen(state===sEqual){                              // Do operations
        switch(op){
            is(add) {result := src1 + src2}
            is(sub) {result := src1 - src2}
            is(mul) {result := src1 * src2}
        }        
        sgn1 := false.B
        sgn2 := false.B
        src1 := 0.U
        src2 := 0.U
    }

    
    val valid = RegNext(state===sEqual)

    

    io.value.valid := valid
    io.value.bits := result
}