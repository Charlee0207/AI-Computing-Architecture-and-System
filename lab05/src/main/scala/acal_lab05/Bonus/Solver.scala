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