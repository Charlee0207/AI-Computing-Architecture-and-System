package acal_lab05.Hw2

import chisel3._
import chisel3.util._


// This module would calculate postfix expression
// Moreover, I have draw the waveform in `PostCalculation_wave.json`
// Please install Waveform Render to view it
// https://marketplace.visualstudio.com/items?itemName=bmpenuelas.waveform-render

class PostfixCalculation extends Module{
    val io = IO(new Bundle{
        val begin = Input(Bool())
        val key_in = Input(UInt(32.W))
        val key_in_valid = Input(Bool())
        val key_in_isOperator = Input(Bool())
        val answer = Output(UInt(32.W))
        val done = Output(Bool())
    })
    
    def isOperator(_token: UInt):   Bool = (_token(32)===1.U)
    def isNumber(_token: UInt):     Bool = (_token(32)===0.U)
    def isEqual(_token: UInt):     Bool = (_token(32)===1.U && _token(4, 0)===equ)

    val add = 10.U
    val sub = 11.U
    val mul = 12.U
    val lpr = 13.U
    val rpr = 14.U
    val equ = 15.U
    val exp = 16.U

    val sIdle :: sPrefill :: sPPL_II1 :: sPPL_II2 :: sNum :: sPopSrc2 :: sPopSrc1 :: sGetRes :: sPushRes :: sEqu :: sDone :: Nil = Enum(11)
    val nstate = WireDefault(sIdle)
    val cstate = RegNext(nstate)

    val operands = Module(new Stack(512, 32)).io
    val postfixExpr = Mem(512, UInt(33.W))
    val ptr = RegInit(0.U((log2Ceil(512+1).W)))
    val token = WireDefault(postfixExpr(ptr))
    val r_token = RegNext(token)

    val res = RegInit(0.S(32.W))
    val src1 = RegInit(0.S(32.W))
    val src2 = RegInit(0.S(32.W))
    val op = RegInit(0.U(5.W))

// ==================== FSM ====================
    switch(cstate){
        is(sIdle){
            nstate := Mux(io.begin, sPrefill, sIdle)
        }
        // Since the key_in input stream cannot be paused,
        // I first load input into a buffer, and later I could controll the postfix stream flow
        is(sPrefill){
            nstate := Mux(( io.key_in_valid &&
                            io.key_in_isOperator &&
                            io.key_in===equ), sPPL_II1, sPrefill)
        }
        // The pipeline initiation interval 1
        is(sPPL_II1){
            nstate := sPPL_II2
        }
        // The pipeline initiation interval 2
        is(sPPL_II2){
            nstate := sNum
        }
        is(sNum){
            nstate := MuxCase(sNum, Array(
                                    isEqual(token) -> sEqu,
                                    // If encounter operator, pop src2, src1, and evaluate them
                                    (isOperator(token) && !isEqual(token)) -> sPopSrc2,
                                    isNumber(token) -> sNum)
                                )
        }
        is(sPopSrc2){
            nstate := sPopSrc1      // Then pop src1 out
        }
        is(sPopSrc1){
            nstate := sGetRes       // Then do the evaluation
        }
        is(sGetRes){
            nstate := sPushRes      // Store result
        }
        is(sPushRes){               // Push the result operand onto stack
            nstate := MuxCase(sNum, Array(
                                    isEqual(token) -> sEqu,
                                    (isOperator(token) && !isEqual(token)) -> sPopSrc2,
                                    isNumber(token) -> sNum)
                                )
        }
        is(sEqu){
            nstate := sDone
        }
        is(sDone){
            nstate := sIdle
        }
    }    


// ==================== Postfix Expression Buffer ====================

    // Use the index `ptr` to controll input postfix stream flow
    ptr := ptr
    switch(cstate){
        is(sIdle){
            ptr := 0.U
        }
        is(sPrefill){
            when(io.key_in_valid){
                // postfixExpr(ptr)(32) stores bool value, 
                // indicating whether the 32-bit value of postfixExpr(ptr)(31, 0) is operator
                postfixExpr(ptr) := io.key_in | (io.key_in_isOperator<<32)
                ptr := ptr + 1.U
            }
            .otherwise{
                postfixExpr(ptr) := io.key_in | (io.key_in_isOperator<<32)
                ptr := ptr
            }
        }
        is(sPPL_II1){
            ptr := 0.U
        }
        is(sPPL_II2){
            ptr := 1.U 
        }
        is(sNum) { 
            ptr := ptr + 1.U 
        }
        is(sPopSrc2){
            ptr := ptr
        }
        is(sPopSrc1){
            ptr := ptr
        }
        is(sGetRes){
            ptr := ptr
        }
        is(sPushRes){
            ptr := ptr + 1.U
        }
        is(sEqu){
            ptr := 0.U
        }
        is(sDone){
            ptr := 0.U
        }
    }


// ==================== Operand Stack ====================

    operands.push := false.B
    operands.pop := false.B
    operands.emplace := false.B
    operands.clear := false.B
    operands.en := (cstate>sPrefill)
    operands.dataIn := 0.U


    switch(cstate){
        is(sNum){                               // Push operands onto stack
            operands.push := true.B
            operands.dataIn := r_token(31, 0)
        }
        is(sPopSrc2){                           // If encounter operator, pop operand out to src2
            operands.pop := true.B
        }
        is(sPopSrc1){                           // Then pop operand out to src1 
            operands.pop := true.B
        }
        is(sGetRes){}                           // Stack do nothing
        is(sPushRes){                           // Push result operand to stack
            operands.push := true.B
            operands.dataIn := res.asUInt
        }
        is(sEqu){}                              // Stack do nothing
        is(sDone){
            operands.clear := true.B            // Clear stack
        }
    }


// ==================== Calculation ====================
    switch(cstate){
        is(sNum){
            res := r_token(31, 0).asSInt
        }
        is(sPopSrc2){
            src2 := operands.dataOut.asSInt
            op := r_token(5, 0)
        }
        is(sPopSrc1){
            src1 := operands.dataOut.asSInt
        }
        is(sGetRes){
            switch(op){
                is(add){
                    res := (src1+src2)
                }
                is(sub){
                    res := (src1-src2)
                }
                is(mul){
                    res := (src1*src2)
                }
                is(exp){                    // only supprt power of 1, 2, 3, 4, and +-1, +-2 to power of n
                    when(src1===1.S){
                        res := 1.S
                    }
                    .elsewhen(src1=== -1.S){
                        res := Mux(src2(0)===1.U, -1.S, 1.S)
                    }
                    .elsewhen(src1===2.S){
                        res := 1.S << src2(10, 0).asUInt
                    }
                    .elsewhen(src1=== -2.S){
                        res := Mux(src2(0)===1.U, -(1.S<<src2(10, 0).asUInt), 1.S<<src2(10, 0).asUInt)
                    }
                    .elsewhen(src2===0.S){
                        res := 1.S
                    }
                    .elsewhen(src2===1.S){
                        res := src1
                    }
                    .elsewhen(src2===2.S){
                        res := src1*src1
                    }
                    .elsewhen(src2===3.S){
                        res := src1*src1*src1
                    }
                    .elsewhen(src2===4.S){
                        res := src1*src1*src1*src1
                    }
                    .otherwise{
                        res := 0.S
                    }
                }
            }
        }
    }
   



   io.answer := res.asUInt
   io.done := cstate===sDone


}

