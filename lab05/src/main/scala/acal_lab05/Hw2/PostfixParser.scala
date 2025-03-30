package acal_lab05.Hw2

import chisel3._
import chisel3.util._


// This module would parse outputed postfix expression,
// such that oprands would be stored at a signed 32-bit int
// Moreover, I have draw the waveform in `PostfixParser_wave.json`
// Please install Waveform Render to view it
// https://marketplace.visualstudio.com/items?itemName=bmpenuelas.waveform-render

class PostfixParser extends Module{
    val io = IO(new Bundle{
        val begin = Input(Bool())
        val key_in = Input(UInt(5.W))
        val key_in_valid = Input(Bool())
        val key_out = Output(UInt(32.W))
        val key_out_valid = Output(Bool())
        val key_out_isOperator = Output(Bool())
        val done = Output(Bool())
    })

    def isNumber(_token: UInt, _r_token: UInt):     Bool = ((r_token===sub && r_tokenVld) && (token<10.U && tokenVld)) || 
                                                            (r_token<10.U && r_tokenVld)
    def isOperator(_r_token: UInt):                 Bool = (r_token===add || r_token===sub || r_token===mul || r_token===exp) && r_tokenVld
    def isEqual(_r_token: UInt):                    Bool = (r_token===equ) && r_tokenVld
   
    val add = 10.U
    val sub = 11.U
    val mul = 12.U
    val lpr = 13.U
    val rpr = 14.U
    val equ = 15.U
    val exp = 16.U

    val sIdle :: sAccept :: sNum :: sOp :: sEqu :: sDone :: Nil = Enum(6)
    val nstate = WireDefault(sIdle)
    val cstate = RegNext(nstate)

    val token = WireDefault(io.key_in)
    val r_token = RegNext(token)
    val rr_token = RegNext(r_token)
    val tokenVld = WireDefault(io.key_in_valid)
    val r_tokenVld = RegNext(tokenVld)

// ==================== FSM ====================
    switch(cstate){
        is(sIdle){
            nstate := Mux(io.begin, sAccept, sIdle)
        }
        is(sAccept){
            nstate := MuxCase(  sAccept, Array(
                                isNumber(token, r_token) -> sNum,
                                isOperator(r_token) -> sOp,
                                isEqual(r_token) -> sEqu)
                                )
        }
        is(sNum){
            nstate := MuxCase(  sAccept, Array(
                                isNumber(token, r_token) -> sNum,
                                isOperator(r_token) -> sOp,
                                isEqual(r_token) -> sEqu)
                                )
        }
        is(sOp){
            nstate := MuxCase(  sAccept, Array(
                                isNumber(token, r_token) -> sNum,
                                isOperator(r_token) -> sOp,
                                isEqual(r_token) -> sEqu)
                                )
        }
        is(sEqu){
            nstate := sDone
        }
        is(sDone){
            nstate := sIdle
        }
    }

// ==================== Single Reg of Postfix Expression ====================

    // Record current number and shift digit in decimal
    val negative = RegInit(false.B)
    val digits = RegInit(0.U(32.W))
    val shiftedDigits = WireDefault(digits)

    when(cstate===sNum){
        // If encounter the minus sign, record it using negative
        when(rr_token===sub){
            negative := true.B
            digits := 0.U
        }
        .otherwise{
            negative := negative
            digits := shiftedDigits
        }
    }
    .otherwise{
        negative := false.B
        digits := 0.U
    }
    // Shift decimal digits
    shiftedDigits := (digits<<3.U) + (digits<<1.U) + rr_token


    // Handle the output
    val token_out = RegInit(0.U(32.W))
    val token_out_valid = RegInit(false.B)
    val token_out_isOperator = RegInit(false.B)

    token_out := 0.U
    token_out_valid := false.B
    token_out_isOperator := false.B

    switch(cstate){
        is(sIdle)   {}
        is(sAccept) {}
        is(sNum){
            token_out := Mux(negative, ~shiftedDigits+1.U, shiftedDigits)
            
            // If next state is not sNum, 
            // which represents the decimal digits has been shifted to correct position
            // and the `token_out` contains correct single SInt of this number
            // Assert valid at this moment
            token_out_valid := Mux(nstate=/=sNum, true.B, false.B)
            token_out_isOperator := false.B
        }
        is(sOp){
            token_out := rr_token
            token_out_valid := true.B
            token_out_isOperator := true.B
        }
        is(sEqu){
            token_out := rr_token
            token_out_valid := true.B
            token_out_isOperator := true.B
        }
        is(sDone){}
    }

    io.key_out := token_out
    io.key_out_valid := token_out_valid
    io.key_out_isOperator := token_out_isOperator
    io.done := cstate===sDone

}
