package acal_lab05.Hw2

import chisel3._
import chisel3.util._

class Infix2Postfix extends Module{
    val io = IO(new Bundle{
        val begin = Input(Bool())
        val key_in = Input(UInt(5.W))
        val key_out = Output(UInt(5.W))
        val key_out_valid = Output(Bool())
        val done = Output(Bool())
    })

    // I have implemented a infix calculator C++ program first,
    // Then I rewrote and modify the C code to this chisel code

    // It's recommanded to read the C code `calculator.cpp` first, to understand my implementation  

    def isRightAssociative(_token: UInt):  Bool = (_token===exp)
    def isOperator(_token: UInt):          Bool = (_token===add || _token===sub || _token===mul || _token===exp)
    def isNumber(_token: UInt):            Bool = (_token===sub&&expectNumber) || _token<10.U
    def isLparenthesis(_token: UInt):      Bool = (_token===lpr)
    def isRparenthesis(_token: UInt):      Bool = (_token===rpr)
    def isEqual(_token: UInt):             Bool = (_token===equ)
    def precedence(_token: UInt):     UInt = MuxCase(0.U, Array(
                                                        (_token===add || _token===sub) -> 1.U,
                                                        (_token===mul) -> 2.U,
                                                        (_token===exp) -> 3.U)
                                                    )

    val add = 10.U
    val sub = 11.U
    val mul = 12.U
    val lpr = 13.U
    val rpr = 14.U
    val equ = 15.U
    val exp = 16.U

    val sIdle :: sPrefill :: sPPL_II1 :: sPPL_II2 :: sNum :: sLpar :: sRpar :: sOp :: sEqu :: sClrStack :: sPrtEqu :: sDone :: Nil = Enum(12)
    val nstate = WireDefault(sIdle)
    val cstate = RegInit(sIdle)

    val operators = Module(new Stack(512, 5)).io
    val infixExpr = Mem(512, UInt(5.W))
    val ptr = RegInit(0.U((log2Ceil(512+1).W)))
    val streamInEn = RegInit(false.B)
    val streamOutEn = RegInit(false.B)
    val r_streamOutEn = RegNext(streamOutEn)

    val token = WireDefault(infixExpr(ptr))
    val r_token = RegNext(token)
    val postfix = RegInit(0.U)
    val op = RegInit(0.U(5.W))
    val expectNumber = WireDefault(true.B)
     
    token := Mux(cstate>sPrefill, infixExpr(ptr), "b1_1111".U)
// ==================== FSM ====================
    switch(cstate){
        is(sIdle){
            nstate := Mux(io.begin, sPrefill, sIdle)
        }
        // Since the key_in input stream cannot be paused,
        // I first load input into a buffer, and later I could controll the infix stream flow
        is(sPrefill){
            nstate := Mux((io.key_in===equ), sPPL_II1, sPrefill)
        }
        // The pipeline initiation interval 1
        is(sPPL_II1){
            nstate := sPPL_II2
        }
        // The pipeline initiation interval 2
        // The input infix should flow to `r_token` now
        // Then I can check current token and previous toke using `token` and `r_token`
        is(sPPL_II2){
            nstate := MuxCase(  sNum, Array(
                                isNumber(token) -> sNum,
                                isLparenthesis(token) -> sLpar)
                                )
        }
        // If the previous token (r_token) results in the cstate
        // lets check the newly input token and nstate
        is(sNum){
            nstate := MuxCase(  sNum, Array(
                                isRparenthesis(token) -> sRpar,
                                isOperator(token) -> sOp,
                                isEqual(token) -> sEqu)
                                )
        }
        is(sLpar){
            nstate := MuxCase(  sLpar, Array(
                                isNumber(token) -> sNum, 
                                isLparenthesis(token) -> sLpar)
                                )
        }
        is(sRpar){
            nstate := MuxCase(  sRpar, Array(
                                isOperator(token) -> sOp,
                                isRparenthesis(token) -> sRpar,
                                isEqual(token) -> sEqu)
                                )
        }
        is(sOp){
            nstate := MuxCase(  sOp, Array(
                                isNumber(token) -> sNum,
                                isLparenthesis(token) -> sLpar)
                )
        }
        is(sEqu){
            nstate := sClrStack
        }
        is(sClrStack){
            nstate := Mux(operators.lastone||operators.empty, sPrtEqu, sClrStack)
        }
        is(sPrtEqu){
            nstate := sDone
        }
        is(sDone){
            nstate := sIdle
        }
    }
    cstate := nstate


// ==================== Infix Expression Buffer ====================
// Use a buffer to store input infix expression,
// which is controlled by the index `ptr`
    ptr := ptr
    switch(cstate) {
        is(sIdle) { 
            ptr := 0.U 
        }
        is(sPrefill) { 
            infixExpr(ptr) := io.key_in
            ptr := ptr + 1.U
        }
        is(sPPL_II1) { 
            ptr := 0.U 
        }
        is(sPPL_II2) { 
            ptr := 1.U 
        }
        is(sNum) { 
            ptr := ptr + 1.U 
        }
        is(sLpar) { 
            ptr := ptr + 1.U 
        }
        is(sRpar) { ptr := Mux(streamInEn, ptr + 1.U, ptr) 
        }
        is(sOp) { 
            ptr := Mux(streamInEn, ptr + 1.U, ptr) 
        }
        is(sEqu) { 
            ptr := Mux(streamInEn, ptr + 1.U, ptr) 
        }
    }


    // If postfix translation block is handling operators stack
    // (e.g. pop out operator, compare the precedence between current token and operator on the stack)
    // the `streamInEn` would be set to false, such that the input streaming would be paused
    streamInEn := true.B
    switch(cstate){
        is(sNum)    {}
        is(sLpar)   {}
        is(sRpar)   {}
        // If operator stack isn't empty, and
        // the token is right associative (exp^) and the top operator in stack has large precedence than (exp^) or
        // the token is left associative (+, -, *) and the top operator in stack has large or equal precedence than (+, -, *)
        // Pop out the operator and pause stream of input infix
        is(sOp){
            when(   !operators.empty &&
                    (   (isRightAssociative(r_token) && precedence(operators.dataOut)>precedence(r_token)) || 
                        (!isRightAssociative(r_token) && precedence(operators.dataOut)>=precedence(r_token)) )){
                streamInEn := false.B
            }
        }
        is(sEqu){
            when(operators.empty){
                streamInEn := false.B
            }
        }
    }


// ==================== Operator Stack ====================
// Push current token to stack, 
// or compare precedence between current token and operator on stack,
// or pop out the operator when it has prior precedence

    operators.push := false.B
    operators.pop := false.B
    operators.emplace := false.B
    operators.clear := false.B
    operators.en := (cstate>sPrefill)
    operators.dataIn := 0.U

    switch(cstate){
        is(sNum)    {}
        is(sLpar){
            operators.push := true.B
            operators.dataIn := r_token
        }
        is(sRpar){
            operators.pop := true.B
        }
        // If operator stack isn't empty, and
        // the token is right associative (exp^) and the top operator in stack has large precedence than (exp^) or
        // the token is left associative (+, -, *) and the top operator in stack has large or equal precedence than (+, -, *)
        // Pop out the operator and pause stream of input infix
        is(sOp){
            when(   !operators.empty &&
                    (   (isRightAssociative(r_token) && precedence(operators.dataOut)>precedence(r_token)) || 
                        (!isRightAssociative(r_token) && precedence(operators.dataOut)>=precedence(r_token)) )){
                operators.emplace := true.B
                operators.dataIn := r_token
            }
            .otherwise{
                operators.push := true.B
                operators.dataIn := r_token
            }
        }
        is(sEqu)    {}
        is(sClrStack){
            when(!operators.empty){
                operators.pop := true.B
            }
        }
        is(sPrtEqu){}
    }


// ==================== Postfix Translation ====================
// Output postfix expression based on current state

    streamOutEn := false.B
    switch(cstate){
        is(sNum){
            streamOutEn := true.B
            postfix := r_token
            when(isOperator(token)){
                expectNumber := false.B
            }
        }
        is(sLpar){
            streamOutEn := false.B
            expectNumber := true.B
        }
        is(sRpar){
            when(!operators.empty && operators.dataOut=/=lpr){
                streamOutEn := true.B
                postfix := operators.dataOut
            }
            .otherwise{
                streamOutEn := false.B
                expectNumber := false.B
            }
        }
        is(sOp){
            // If current input token is right associative, 
            // or op on stack has prior precedence
            // then output op on stack
            when(   !operators.empty &&
                    (   (isRightAssociative(r_token) && precedence(operators.dataOut)>precedence(r_token)) || 
                        (!isRightAssociative(r_token) && precedence(operators.dataOut)>=precedence(r_token)) )){ 
                streamOutEn := true.B
                postfix := operators.dataOut
            }
            // Else output nothing
            // and push the op to stack in Operator Stack block
            .otherwise{
                streamOutEn := false.B
                expectNumber := true.B
            }
        }
        is(sEqu)    {}
        is(sClrStack){
            when(!operators.empty){
                streamOutEn := Mux(operators.dataOut===lpr, false.B, true.B)
                postfix := operators.dataOut
            }
            .otherwise{
                streamOutEn := false.B
            }
        }
        is(sPrtEqu){
            streamOutEn := true.B
            postfix := equ
        }
    }

io.key_out := postfix 
io.key_out_valid := streamOutEn
io.done := (cstate===sDone)

}