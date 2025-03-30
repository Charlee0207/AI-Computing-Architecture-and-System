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