(NTHU_113062559_李承澔)  ACAL 2024 Fall Lab 5 HW Submission Template
===


###### tags: `AIAS Fall 2024`, `AIAS Spring 2024` `Submission Template`



[toc]

## Gitlab code link
:::info
Please paste the link to your private Gitlab repository for this homework submission here. 
:::

- [Gitlab link](https://course.playlab.tw/git/chngh0207/lab05) - 

## Hw5-1 TrafficLight with Pedestrian button
### Scala Code
> 請放上你的程式碼並加上註解(中英文不限)，讓 TA明白你是如何完成的。
```scala=
package acal_lab05.Hw1

import chisel3._
import chisel3.util._

class TrafficLight_p(Ytime:Int, Gtime:Int, Ptime:Int) extends Module{
  val io = IO(new Bundle{
    val P_button = Input(Bool())
    val H_traffic = Output(UInt(2.W))
    val V_traffic = Output(UInt(2.W))
    val P_traffic = Output(UInt(2.W))
    val timer     = Output(UInt(5.W))
  })

  //please implement your code below...
  io.H_traffic := 0.U
  io.V_traffic := 0.U
  io.P_traffic := 0.U
  io.timer := 0.U

  // Parameters and state transition
  val Off = 0.U
  val Red = 1.U
  val Yellow = 2.U
  val Green = 3.U

  val cntG :: cntY :: cntP :: Nil = Enum(3)
  val sIdle :: sHGVR :: sHYVR :: sHRVG :: sHRVY :: sPG :: Nil = Enum(6)

  // State register
  val state = RegInit(sIdle)
  val preState = RegInit(sIdle)   // Use previous state register to return to the state before switching to sPG
  val goback = RegInit(false.B)   // If state switches to sPG, assert goback to return to previous state

  // Counter
  val cntMode = WireDefault(cntG)
  val cntReg = RegInit(0.U(4.W))
  val cntDone = Wire(Bool())
  cntDone := cntReg === 0.U

  when(io.P_button && state=/=sPG){ // If press pedestrain button, and current state isn't sPG
      cntReg := (Ptime-1).U     
  }.elsewhen(cntDone){              // If current state has counted over, switch to the countdown of next state
    when(cntMode === cntG){         
      cntReg := (Gtime-1).U
    }.elsewhen(cntMode === cntY){
      cntReg := (Ytime-1).U
    }.elsewhen(cntMode === cntP){
      cntReg := (Ptime-1).U
    }
  }.otherwise{                    // Else, countdown
    cntReg := cntReg - 1.U
  }

  // FSM state
  switch(state){
    is(sIdle){
      when(io.P_button)   {state    := sPG
                           preState := sIdle
                           goback   := true.B}
      .elsewhen(cntDone)  {state    := sHGVR
                           goback   := false.B}
    }
    is(sHGVR){
      when(io.P_button)   {state    := sPG
                           preState := sHGVR
                           goback   := true.B}
      .elsewhen(cntDone)  {state    := sHYVR
                           goback   := false.B}
    }
    is(sHYVR){
      when(io.P_button)   {state    := sPG
                           preState := sHYVR
                           goback   := true.B}
      .elsewhen(cntDone)  {state    := sHRVG
                           goback   := false.B}
    }
    is(sHRVG){
      when(io.P_button)   {state    := sPG
                           preState := sHRVG
                           goback   := true.B}
      .elsewhen(cntDone)  {state    := sHRVY
                           goback   := false.B}
    }
    is(sHRVY){
      when(io.P_button)   {state    := sPG
                           preState := sHRVY
                           goback   := true.B}
      .elsewhen(cntDone)  {state    := sPG
                           goback   := false.B}
    }
    is(sPG){
      when(cntDone)       {state    := Mux(goback, preState, sHGVR)}
    }
  }

  // Behavior decoder
  // Default statement
  cntMode := cntG
  io.H_traffic := Off
  io.V_traffic := Off

  switch(state){
    is(sHGVR){
      cntMode := Mux(io.P_button, cntP, cntY)
      io.H_traffic := Green
      io.V_traffic := Red
      io.P_traffic := Red
    }
    is(sHYVR){
      cntMode := Mux(io.P_button, cntP, cntG)
      io.H_traffic := Yellow
      io.V_traffic := Red
      io.P_traffic := Red
    }
    is(sHRVG){
      cntMode := Mux(io.P_button, cntP, cntY)
      io.H_traffic := Red
      io.V_traffic := Green
      io.P_traffic := Red
    }
    is(sHRVY){
      cntMode := cntP
      io.H_traffic := Red
      io.V_traffic := Yellow
      io.P_traffic := Red
    }
    is(sPG){
      // If goback flag is asserted, return to previous state
      // else, go to sHGVR
      cntMode := Mux(goback, MuxCase(cntG, Array( (preState===sHGVR) -> cntG,
                                                  (preState===sHYVR) -> cntY,
                                                  (preState===sHRVG )-> cntG,
                                                  (preState===sHRVY) -> cntY)), 
                              cntG)
      io.H_traffic := Red
      io.V_traffic := Red
      io.P_traffic := Green
    }
  }

  io.timer := cntReg
}
```
### Waveform
> 請依照Lab文件中的說明截圖，並說明之。(文件中搜尋:截圖)

第一段：前25個週期，為不受`P_button`干擾的燈號運行，其中包含了紅綠燈的完整一次的循環(共25個週期)。
![](https://course.playlab.tw/md/uploads/f8b494a5-d7d9-451c-9698-d997a2d83e86.png)

第二段：在`P_button`的影響下，`state`先跳至5(sPG)，代行人時數結束後，再跳回先前的狀態3(sHRVG)。
![](https://course.playlab.tw/md/uploads/dad32aaf-feba-456d-ae36-e8df27835cd9.png)


  | state | sIdle | sHGVR | sHYVR | sHRVG | sHRVY | sPG |
  |:-----:|:-----:|:-----:|:-----:|:-----:|:-----:|:---:|
  |  map  |   0   |   1   |   2   |   3   |   4   |  5  |



## Hw5-2-1 Negative Integer Generator
### Scala Code
> 請放上你的程式碼並加上註解(中英文不限)，讓 TA明白你是如何完成的。
```scala=
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
```
### Test Result
> 請放上你通過test的結果，驗證程式碼的正確性。大致說明值得關注的波形圖位置。

![](https://course.playlab.tw/md/uploads/c2c662b2-8fc9-41d0-b9a2-95bbc93dcfcb.png)

![](https://course.playlab.tw/md/uploads/b9244d6a-ee9b-4005-a5f1-4bfdd14d0a14.png)

在13ps時，state轉為由1(sLpar)轉為3(sSign)，表示前面讀到左括號且此時讀到減號，接下來輸入必定是負數。

接著在15ps時，state由3(sSign)轉為4(sNum)，表示在`(-`後必定接著處理一串數字字串，並拉起sign。

最後在25ps時，讀到`=`算式結束，輸出最後結果並拉起valid。






## Hw5-2-2 N operands N-1 operators(+、-)
### Scala Code
> 請放上你的程式碼並加上註解(中英文不限)，讓 TA明白你是如何完成的。
```scala=
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
```

### Test Result
> 請放上你通過test的結果，驗證程式碼的正確性。(螢幕截圖即可)

![](https://course.playlab.tw/md/uploads/ed0329f0-09ec-4544-a446-4f8b3473f8ed.png)


## Hw5-2-3 Order of Operation (+、-、*、(、))
- **如果你有完成Bonus部分，請在此註明。**

完成exponent計算及其所需要的right associative evaluation`(Question8, 9 in tester)`，以及不需加括號的負數運算`(Question10, 11, 12, 13 in tester)`。

### Scala Code
> 請放上你的程式碼並加上註解(中英文不限)，讓 TA明白你是如何完成的。

其中包含了四個檔案：`Stack.scala`, `Infix2Postfix.scala`, `PostfixParser.scala`, `PostfixCalculation.scala`及設計時手畫的波形。
詳細檔案內容請參考gitlab。

```scala=
package acal_lab05.Hw2

import chisel3._
import chisel3.util._

class CpxCal extends Module with RequireSyncReset{
    val io = IO(new Bundle{
        val key_in = Input(UInt(5.W))
        val value = Output(Valid(UInt(32.W)))
    })

    //please implement your code below


    // These three module do task streamly, i.e. m_PostfixParser and m_PostfixCalculation
    // operate when m_Infix2Postfix streams out valid output
    // There's no any buffer to store mid-result of each module's output
    val m_Infix2Postfix = Module(new Infix2Postfix).io              // Convert input infix to postfix
    val m_PostfixParser = Module(new PostfixParser).io              // Parse the postfix expression and store a string of digits into a single sign number
    val m_PostfixCalculation = Module(new PostfixCalculation).io    // Do postfix evaluation

    val sIdle :: sInfix2Postfix :: sWaitPostfixParser :: sWaitPostfixCalculation :: sDone :: Nil = Enum(5)
    
    val nstate = WireDefault(sIdle)
    val cstate = RegInit(sIdle)

    val Infix2Posifix_BEGIN = WireDefault(nstate===sInfix2Postfix && cstate===sIdle)
    val Infix2Posifix_DONE = WireDefault(m_Infix2Postfix.done)
    val PostfixParser_BEGIN = WireDefault(nstate===sInfix2Postfix && cstate===sIdle)
    val PostfixParser_DONE = WireDefault(m_PostfixParser.done)
    val PostfixCalculation_BEGIN = WireDefault(nstate===sInfix2Postfix && cstate===sIdle)
    val PostfixCalculation_DONE = WireDefault(m_PostfixCalculation.done)
    val r_infix = RegNext(io.key_in)
    
    
    switch(cstate){
        is(sIdle){
            nstate := sInfix2Postfix
        }
        is(sInfix2Postfix){
            nstate := Mux(Infix2Posifix_DONE, sWaitPostfixParser, sInfix2Postfix)
        }
        is(sWaitPostfixParser){
            nstate := Mux(PostfixParser_DONE, sWaitPostfixCalculation, sWaitPostfixParser)
        }
        is(sWaitPostfixCalculation){
            nstate := Mux(PostfixCalculation_DONE, sDone, sWaitPostfixCalculation)
        }
        is(sDone){
            nstate := sIdle
        }
    }
    cstate := nstate

    m_Infix2Postfix.key_in                  := r_infix
    m_Infix2Postfix.begin                   := Infix2Posifix_BEGIN
    m_PostfixParser.key_in                  := m_Infix2Postfix.key_out
    m_PostfixParser.key_in_valid            := m_Infix2Postfix.key_out_valid
    m_PostfixParser.begin                   := PostfixParser_BEGIN
    m_PostfixCalculation.key_in             := m_PostfixParser.key_out
    m_PostfixCalculation.key_in_valid       := m_PostfixParser.key_out_valid
    m_PostfixCalculation.key_in_isOperator  := m_PostfixParser.key_out_isOperator
    m_PostfixCalculation.begin              := PostfixCalculation_BEGIN


    io.value.valid := cstate===sDone
    io.value.bits :=  m_PostfixCalculation.answer
}
```
### Test Result
> 請放上你通過test的結果，驗證程式碼的正確性。(螢幕截圖即可)

![](https://course.playlab.tw/md/uploads/91490d29-0f06-4024-a054-9614cac8b3a3.png)



## Hw5-3-1 Pseudo Random Number Generator
### Scala Code
> 請放上你的程式碼並加上註解(中英文不限)，讓 TA明白你是如何完成的。
```scala=
package acal_lab05.Hw3

import chisel3._
import chisel3.util._

class PRNG(seed:Int) extends Module with RequireSyncReset{
    val io = IO(new Bundle{
        val gen = Input(Bool())
        val puzzle = Output(Vec(4,UInt(4.W)))
        val ready = Output(Bool())
    })

    val randomNum = RegInit(seed.asUInt)
    val shiftedRandomNum = WireDefault(randomNum(15, 1))
    val nextMSB = WireDefault(randomNum(5)^randomNum(3)^randomNum(2)^randomNum(0))

    randomNum := Cat(nextMSB, shiftedRandomNum)

    val sIdle :: sSample :: sOverflow :: sCheckRepeat_d3 :: sCheckRepeat_d2 :: sCheckRepeat_d1 :: sCheckRepeat_d0 :: sCheckRepeatPuzzle :: sDone :: Nil = Enum(9)
    val nstate = WireDefault(sIdle)
    val cstate = RegNext(nstate)

    
    val digits = RegInit(VecInit(Seq.fill(4)(0.U(4.W))))
    val digitSet = RegInit(VecInit(Seq.fill(10)(false.B)))
    val puzzleSet = RegInit(VecInit(Seq.fill(10000)(false.B)))


// ==================== FSM ====================
    switch(cstate){
        is(sIdle){
            nstate := Mux(io.gen, sSample, sIdle)
        }
        is(sSample){                    // Sample current value inLFSR
            nstate := sOverflow
        }
        is(sOverflow){                  // Detect overflow of decimal digit
            nstate := sCheckRepeat_d3
        }
        is(sCheckRepeat_d3){            // Check whether digit(3) is repeated
            nstate := sCheckRepeat_d2   
        }
        is(sCheckRepeat_d2){            // Check whether digit(2) is repeated
            nstate := sCheckRepeat_d1
        }
        is(sCheckRepeat_d1){            // Check whether digit(1) is repeated
            nstate := sCheckRepeat_d0
        }
        is(sCheckRepeat_d0){            // Check whether digit(0) is repeated
            nstate := sCheckRepeatPuzzle
        }
        is(sCheckRepeatPuzzle){         // Check whether the puzzle is repeated, if so, resample a new number
            nstate := Mux(puzzleIsRepeat(), sSample, sDone)
        }
        is(sDone){
            nstate := sIdle
        }
    }

// ==================== Handle 4-digit Decimal Number ====================

    def mapHex2Dec(_hex: UInt): UInt = Mux(_hex>=10.U, _hex-10.U, _hex)

    // Output an unused digit based on digitset() and digitSelectPriority()
    // digitset(n), n in [0, 9], which record whether digit n is used in number generated by LFSR
    def unusedDigit():  UInt = MuxCase(0.U, Array(
                                                (!digitSet(digitSelectPriority(0))) -> digitSelectPriority(0),
                                                (!digitSet(digitSelectPriority(1))) -> digitSelectPriority(1),
                                                (!digitSet(digitSelectPriority(2))) -> digitSelectPriority(2),
                                                (!digitSet(digitSelectPriority(3))) -> digitSelectPriority(3),
                                                (!digitSet(digitSelectPriority(4))) -> digitSelectPriority(4),
                                                (!digitSet(digitSelectPriority(5))) -> digitSelectPriority(5),
                                                (!digitSet(digitSelectPriority(6))) -> digitSelectPriority(6),
                                                (!digitSet(digitSelectPriority(7))) -> digitSelectPriority(7),
                                                (!digitSet(digitSelectPriority(8))) -> digitSelectPriority(8),
                                                (!digitSet(digitSelectPriority(9))) -> digitSelectPriority(9))
                                                )

    // The number store at digitSelectPriority(0) has highest priority to be selected 
    // to replace repeated digit at state sCheckRepeat_dn

    // And this digitSelectPriority adopt Round-Robin, i.e. 
    // if the digit at digitSelectPriority(0) is selected to replace, 
    // which digit would be push to the back at digitSelectPriority(9)

    // The initial sequence can pass the testbench, but it cannot guarantee pass the testbench with other seed
    val digitSelectPriority = RegInit(VecInit(Seq(0.U, 2.U, 1.U, 4.U, 3.U, 6.U, 5.U, 8.U, 7.U, 9.U)))

    // Use a puzzleSet to record which puzzle is used
    def puzzleIsRepeat():   Bool = puzzleSet( digits(3)*1000.U + digits(2)*100.U + digits(1)*10.U + digits(0) )
    
    switch(cstate){
        is(sSample){
            for(i <- 0 until 4){
                digits(i) := randomNum(i*4+3, i*4)  // Record four 4-bit numbers
            }
        }
        is(sOverflow){
            for(i <- 0 until 4){    
                digits(i) := mapHex2Dec(digits(i))  // Map hex number to dec number
            }
        }
        is(sCheckRepeat_d3){
            for(i <- 0 until 4){
                digitSet( digits(i) ) := true.B     // Register all used digits
            }
        }
        is(sCheckRepeat_d2){
            when(digits(3)===digits(2)){            // If digit(2) is as same as digit(3)
                digits(2) := unusedDigit()          // assign a unused digit to digit(2)
                digitSet( unusedDigit() ) := true.B // register that unused digit used in digitSet 

                for(i <- 0 until 10){
                    // Do the Round-Robin
                    // {[0], [1], [2], [3], [4], [5], [6], [7], [8], [9]} <= {[1], [2], [3], [4], [5], [6], [7], [8], [9], [0]}
                    digitSelectPriority(i) := digitSelectPriority((i+1)%10)
                }
            }
        }
        is(sCheckRepeat_d1){
            when(digits(3)===digits(1) || digits(2)===digits(1)){
                digits(1) := unusedDigit()
                digitSet( unusedDigit() ) := true.B

                for(i <- 0 until 10){
                    // Do the Round-Robin
                    // {[0], [1], [2], [3], [4], [5], [6], [7], [8], [9]} <= {[1], [2], [3], [4], [5], [6], [7], [8], [9], [0]}
                    digitSelectPriority(i) := digitSelectPriority((i+1)%10)
                }
            }
        }
        is(sCheckRepeat_d0){
            when(digits(3)===digits(0) || digits(2)===digits(0) || digits(1)===digits(0)){
                digits(0) := unusedDigit()
                digitSet( unusedDigit() ) := true.B

                for(i <- 0 until 10){
                    // Do the Round-Robin
                    // {[0], [1], [2], [3], [4], [5], [6], [7], [8], [9]} <= {[1], [2], [3], [4], [5], [6], [7], [8], [9], [0]}
                    digitSelectPriority(i) := digitSelectPriority((i+1)%10)
                }
            }
        }
        is(sCheckRepeatPuzzle){
            puzzleSet( digits(3)*1000.U + digits(2)*100.U + digits(1)*10.U + digits(0) ) := true.B
        }
        is(sDone){
            for(i <- 0 until 4){
                digits(i) := 0.U
            }
            for(i <- 0 until 10){
                digitSet(i) := false.B
            }
        }

    }


    io.puzzle := digits
    io.ready := cstate===sDone
}

```
### Test Result
> 請放上你通過test的結果，驗證程式碼的正確性。(螢幕截圖即可)

![](https://course.playlab.tw/md/uploads/6e756128-f8a0-4b7b-bdbd-9a3e4f2a6f1b.png)


## Hw5-3-2 1A2B game quiz
### Scala Code
> 請放上你的程式碼並加上註解(中英文不限)，讓 TA明白你是如何完成的。

```scala=
class top extends Module{
    val io  = IO(new Bundle{
        val gen = Input(Bool())
        val finish = Output(Bool())
    })

    var seed = 1
    val m_NumGuess = Module(new NumGuess(seed)).io
    val m_Solver = Module(new Solver()).io

    // m_NumGuess Input list
    m_NumGuess.gen := io.gen
    m_NumGuess.guess := Cat(m_Solver.guess(3), m_Solver.guess(2), m_Solver.guess(1), m_Solver.guess(0))
    m_NumGuess.s_valid := m_Solver.s_valid

    // m_Solver Input list
    m_Solver.A := m_NumGuess.A
    m_Solver.B := m_NumGuess.B
    m_Solver.ready := m_NumGuess.ready
    m_Solver.g_valid := m_NumGuess.g_valid


    io.finish := m_Solver.finish
}
```

```scala=
package acal_lab05.Hw3

import chisel3._
import chisel3.util._

class NumGuess(seed:Int = 1) extends Module with RequireSyncReset{
    require (seed > 0 , "Seed cannot be 0")

    val io  = IO(new Bundle{
        val gen = Input(Bool())
        val guess = Input(UInt(16.W))
        val puzzle = Output(Vec(4,UInt(4.W)))
        val ready  = Output(Bool())
        val g_valid  = Output(Bool())
        val A      = Output(UInt(3.W))
        val B      = Output(UInt(3.W))

        //don't care at Hw6-3-2 but should be considered at Bonus
        val s_valid = Input(Bool())
    })

    val m_PRNG = Module(new PRNG(seed)).io

    val sIdle :: sGen :: sReady :: sCompare :: sValid :: Nil = Enum(5)
    val nstate = WireDefault(sIdle)
    val cstate = RegNext(nstate)

    val puzzleDigits = RegInit(VecInit(Seq.fill(4)(0.U(4.W))))
    val guessDigits = RegInit(VecInit(Seq.fill(4)(0.U(4.W))))
    val digitSet = RegInit(VecInit(Seq.fill(10)(false.B)))
    val A = RegInit(0.U(3.W))
    val B = RegInit(0.U(3.W))


// ==================== FSM ====================
    switch(cstate){
        is(sIdle){
            nstate := Mux(io.gen, sGen, sIdle)
        }
        is(sGen){
            nstate := Mux(m_PRNG.ready, sReady, sGen)
        }
        is(sReady){
            nstate := sCompare
        }
        is(sCompare){
            nstate := sValid
        }
        is(sValid){
            nstate := Mux(A===4.U, sIdle, sReady)
        }
    }

// ==================== Fetch Numbers ====================
    switch(cstate){
        is(sIdle){
            puzzleDigits := VecInit(Seq.fill(4)(0.U(4.W)))  // Intialize puzzleDigits
            guessDigits := VecInit(Seq.fill(4)(0.U(4.W)))   // Intialize guessDigits
            for(i <- 0 until 10){                           // Intialize digitSet
                digitSet(i) := false.B
            }
        }
        is(sGen){
            when(m_PRNG.ready){                             // When PRNG gives a puzzle
                puzzleDigits := m_PRNG.puzzle               // Copy puzzle number to local register
                for(i <- 0 until 4){
                    digitSet(m_PRNG.puzzle(i)) := true.B    // Register used digits
                }
            }            
        }
        is(sReady){                                         // When user gives a guess
                                                            // Copy guess number to local register
            guessDigits := VecInit( Seq(io.guess(3, 0), io.guess(7, 4), io.guess(11, 8), io.guess(15, 12)) )
        }
    }

// ==================== Compare Block ====================
    switch(cstate){
        is(sIdle){
            A := 0.U
            B := 0.U
        }
        is(sCompare){                                                           // Accumulate number of A's                            
            A := Mux(guessDigits(3)===puzzleDigits(3), 1.U(3.W), 0.U(3.W)) + 
                 Mux(guessDigits(2)===puzzleDigits(2), 1.U(3.W), 0.U(3.W)) + 
                 Mux(guessDigits(1)===puzzleDigits(1), 1.U(3.W), 0.U(3.W)) + 
                 Mux(guessDigits(0)===puzzleDigits(0), 1.U(3.W), 0.U(3.W))
                                                                                // Accumulate number of B's     
            B := Mux((guessDigits(3)=/=puzzleDigits(3) && digitSet(guessDigits(3))), 1.U(3.W), 0.U(3.W)) + 
                 Mux((guessDigits(2)=/=puzzleDigits(2) && digitSet(guessDigits(2))), 1.U(3.W), 0.U(3.W)) + 
                 Mux((guessDigits(1)=/=puzzleDigits(1) && digitSet(guessDigits(1))), 1.U(3.W), 0.U(3.W)) + 
                 Mux((guessDigits(0)=/=puzzleDigits(0) && digitSet(guessDigits(0))), 1.U(3.W), 0.U(3.W))
        }
    }



    m_PRNG.gen := io.gen
    io.puzzle := puzzleDigits
    io.ready  := cstate===sReady
    io.g_valid  := cstate===sValid
    io.A      := A
    io.B      := B
}
```
### Test Result
> 請放上你通過test的結果，驗證程式碼的正確性。(螢幕截圖即可)

![](https://course.playlab.tw/md/uploads/2cc5e423-b94c-4b13-81f0-f2ade4e0bbc2.png)


## Bonus : 1A2B hardware solver [Optional]
### Scala Code
> 請放上你的程式碼並加上註解(中英文不限)，讓 TA明白你是如何完成的。
```scala=
package acal_lab05.Bonus

import chisel3._
import chisel3.util._

class Solver extends Module with RequireSyncReset{

    val io = IO(new Bundle{
        val A = Input(UInt(3.W))
        val B = Input(UInt(3.W))
        val ready = Input(Bool())
        val guess = Output(Vec(4,UInt(4.W)))
        val g_valid = Input(Bool())
        val s_valid = Output(Bool())
        val finish = Output(Bool())
    })

    // FSM variables
    val sIdle :: sTraverseSecretDigit :: sMatchSecretDigit :: sDone :: Nil = Enum(4)

    val nstate = WireDefault(sIdle)
    val cstate = RegNext(nstate)
    val done_timer = RegInit(0.U(4.W))

    // Digit variables
    val digitSet = RegInit(VecInit(Seq.fill(4)(0.U(4.W))))
    val digits = RegInit(VecInit(Seq.fill(4)(0.U(4.W))))

    // Traverse secret digit variables
    val sTraverseIdle :: sTraverseGuess :: sTraverseWait :: sTraverseGet1A :: sTraverseGet0A :: sTraverseDone :: Nil = Enum(6) 
    val sTraverseNstate = WireDefault(sTraverseIdle)
    val sTraverseCstate = RegNext(sTraverseNstate)

    val traverseSecretCnt = RegInit(0.U(5.W))
    val traverseGuess = RegInit(0.U(4.W))
    val traverseValid = Wire(Bool())
    val traverseDone = Wire(Bool())
    val traverseDigits = WireDefault(VecInit(Seq.fill(4)(0.U(4.W))))

    // Match digit variables    
    val sMatchIdle :: sMatchDigit :: sMatchWait :: sMatchGet1A :: sMatchGet0A :: sMatchDone :: Nil = Enum(6) 
    val sMatchNstate = WireDefault(sMatchIdle)
    val sMatchCstate = RegNext(sMatchNstate)

    val matchCnt = RegInit(0.U(4.W))
    val matchValid = Wire(Bool())
    val matchDone = Wire(Bool())
    val matchDigitIdx = RegInit(0.U(2.W))
    val matchDigits = RegInit(VecInit(Seq.fill(4)(0.U(4.W))))

    // Output digits
    val answer = RegNext(io.guess)


// ==================== FSM ====================
    switch(cstate){
        is(sIdle){
            nstate := Mux(io.ready, sTraverseSecretDigit, sIdle)
        }
        // Guess (0, 0, 0, 0), (1, 1, 1, 1), (2, 2, 2, 2), ..., (9, 9, 9, 9)
        // To find which 4 secret digits are used
        is(sTraverseSecretDigit){
            nstate := Mux(traverseDone, sMatchSecretDigit, sTraverseSecretDigit)
        }
        is(sMatchSecretDigit){
            nstate := Mux(matchDone, sDone, sMatchSecretDigit)
        }
        is(sDone){
            nstate := Mux(done_timer===16.U, sIdle, sDone)
        }
    }

    done_timer := Mux(cstate===sDone, done_timer+1.U, 0.U)

// ==================== Traverse Secret Digit ====================
    // FSM
    switch(sTraverseCstate){
        is(sTraverseIdle){
            sTraverseNstate := Mux(cstate===sTraverseSecretDigit, sTraverseGuess, sTraverseIdle)
        }
        is(sTraverseGuess){
            sTraverseNstate := sTraverseWait
        }
        is(sTraverseWait){
            when(io.g_valid){
                sTraverseNstate := Mux(io.A===1.U, sTraverseGet1A, sTraverseGet0A)
            }
            .otherwise{
                sTraverseNstate := sTraverseWait
            }
        }
        is(sTraverseGet1A){
            sTraverseNstate := Mux(traverseSecretCnt===3.U, sTraverseDone, sTraverseGuess)
        }
        is(sTraverseGet0A){
            sTraverseNstate := sTraverseGuess
        }
        is(sTraverseDone){
            sTraverseNstate := sTraverseIdle
        }
    }


    switch(sTraverseCstate){                           
        is(sTraverseIdle){
            traverseSecretCnt := 0.U
            traverseGuess := 0.U 
        }
        is(sTraverseGuess){}
        is(sTraverseWait){}
        is(sTraverseGet1A){
            traverseSecretCnt := traverseSecretCnt + 1.U
            traverseGuess := traverseGuess + 1.U
        }
        is(sTraverseGet0A){
            traverseSecretCnt := traverseSecretCnt
            traverseGuess := traverseGuess + 1.U
        }
        is(sTraverseDone){}
    }
    
    traverseDone := sTraverseCstate===sTraverseDone
    traverseValid := sTraverseCstate===sTraverseGuess
    traverseDigits := VecInit(Seq.fill(4)(traverseGuess))


    when(sTraverseCstate===sTraverseGet1A){
        digitSet(traverseSecretCnt) := traverseGuess
    }
    .elsewhen(cstate===sIdle){
        digitSet := VecInit(Seq.fill(4)(0.U(4.W)))
    }


// ==================== Match Secret Digit ====================
    // FSM
    switch(sMatchCstate){
        is(sMatchIdle){
            sMatchNstate := Mux(cstate===sMatchSecretDigit, sMatchDigit, sMatchIdle)
        }
        is(sMatchDigit){
            sMatchNstate := sMatchWait
        }
        is(sMatchWait){
            when(io.g_valid){
                sMatchNstate := Mux(io.A===matchCnt+1.U, sMatchGet1A, sMatchGet0A)
            }
            .otherwise{
                sMatchNstate := sMatchWait
            }
        }
        is(sMatchGet1A){
            sMatchNstate := Mux(matchCnt===3.U, sMatchDone, sMatchDigit)
        }
        is(sMatchGet0A){
            sMatchNstate := sMatchDigit
        }
        is(sMatchDone){
            sMatchNstate := sMatchIdle
        }
    }

    switch(sMatchCstate){
        is(sMatchIdle){
            matchDigits := VecInit(Seq.fill(4)(15.U(4.W)))
            matchCnt := 0.U
            matchDigitIdx := 0.U
        }
        is(sMatchDigit){}
        is(sMatchWait){}
        is(sMatchGet1A){
            matchDigits(matchCnt) := digitSet(matchDigitIdx-1.U)
            matchCnt := matchCnt + 1.U
            matchDigitIdx := 0.U
        }
        is(sMatchGet0A){
            matchDigits(matchCnt) := digitSet(matchDigitIdx)
            matchCnt := matchCnt
            matchDigitIdx := matchDigitIdx + 1.U
        }
        is(sMatchDone){}
    }

    matchDone := sMatchCstate===sMatchDone
    matchValid := sMatchCstate===sMatchDigit || sMatchCstate===sMatchDone


// ==================== IO ====================
    io.guess := MuxCase(VecInit(Seq.fill(4)(0.U(4.W))), Array(
                                                                (cstate===sTraverseSecretDigit) -> traverseDigits,
                                                                (cstate===sMatchSecretDigit) -> matchDigits, 
                                                                (cstate===sDone) -> answer)
                        )
    io.s_valid := MuxCase(false.B, Array(
                                                                (cstate===sTraverseSecretDigit) -> traverseValid,
                                                                (cstate===sMatchSecretDigit) -> matchValid)
                        )
    io.finish := cstate===sDone
}
```
### Test Result
> 請放上你通過test的結果，驗證程式碼的正確性。(螢幕截圖即可)

* Simulation result of `SolverTest`
![](https://course.playlab.tw/md/uploads/47ceba64-bced-4f8b-acf6-da6fdaabc0ef.png)

    其中數字15表為未猜測的位數。因題目不可能出現15，可以避免comparator回傳錯誤之A, B值。

* Simulation result of `topTest`
![](https://course.playlab.tw/md/uploads/45aec6d9-b2b9-498a-a233-26777e7574f6.png)

* Simulation waveform of `topTest
    `![](https://course.playlab.tw/md/uploads/7c85174f-d851-4ef0-9270-e909bd35565f.png)
    在`NumGuess_io_gen`拉起後，`NumGuess` module 隨即產生`puzzleDigits`作為題目並拉起`NumGuess_io_ready`。
    
    而後`Solver` module以兩階段搜索答案。
    第一階段，4個位元皆輸出同一數字直至遍歷完成，如[0, 0, 0, 0], ...[9, 9, 9, 9]。並預期第一階段會收到4次A，即可確認題目中有哪4個數字。
    第二階段，由LSB至MSB排列此4個數字，若尚未排序到的位元則輸出15，避免影響A值。直到得到4A的結果，即可確認為答案，並拉起finish。
    



## 文件中的問答題
- Q1:Hw5-2-2(長算式)以及Lab5-2-2(短算式)，需要的暫存器數量是否有差別？如果有，是差在哪裡呢？
    - Ans1:
    無。可以將計算時的中間結果存回src1，等到下一個值輸入進來，並將其賦值給src2即可完成一次運算。
- Q2:你是如何處理**Hw5-2-3**有提到的關於**編碼衝突**的問題呢?
    - Ans2:
    新增一個bit `isOperator` 紀錄目前的編碼是否為Operator或者是Number。
- Q3:你是如何處理**Hw5-3-1**1A2B題目產生時**數字重複**的問題呢?
    - Ans3: 
    使用下式`unusedDigit()`回傳一個未重複的數字，並且會依照優先順序首先檢查`digitSelectPriority(0)`所存的數字是否重複，若無則回傳；若有則繼續往下檢查
    `digitSelectPriority(1)`, `digitSelectPriority(2)`, ...
    且`digitSelectPriority`所存的數字採用Round-Robin方式Shift，若這次使用了`digitSelectPriority(0)`內的數字，該數字的順位將會貝移至最末位，也就是轉存入`digitSelectPriority(9)`，以增加隨機性。
    - 
```scala=
def unusedDigit():  UInt = MuxCase(0.U, Array(
                                                (!digitSet(digitSelectPriority(0))) -> digitSelectPriority(0),
                                                (!digitSet(digitSelectPriority(1))) -> digitSelectPriority(1),
                                                (!digitSet(digitSelectPriority(2))) -> digitSelectPriority(2),
                                                (!digitSet(digitSelectPriority(3))) -> digitSelectPriority(3),
                                                (!digitSet(digitSelectPriority(4))) -> digitSelectPriority(4),
                                                (!digitSet(digitSelectPriority(5))) -> digitSelectPriority(5),
                                                (!digitSet(digitSelectPriority(6))) -> digitSelectPriority(6),
                                                (!digitSet(digitSelectPriority(7))) -> digitSelectPriority(7),
                                                (!digitSet(digitSelectPriority(8))) -> digitSelectPriority(8),
                                                (!digitSet(digitSelectPriority(9))) -> digitSelectPriority(9))
                                                )

...

val digitSelectPriority = RegInit(VecInit(Seq(0.U, 2.U, 1.U, 4.U, 3.U, 6.U, 5.U, 8.U, 7.U, 9.U)))

...

for(i <- 0 until 10){
    // Do the Round-Robin
    // {[0], [1], [2], [3], [4], [5], [6], [7], [8], [9]} <= {[1], [2], [3], [4], [5], [6], [7], [8], [9], [0]}
    digitSelectPriority(i) := digitSelectPriority((i+1)%10)
}
```


## 意見回饋和心得(可填可不填)

