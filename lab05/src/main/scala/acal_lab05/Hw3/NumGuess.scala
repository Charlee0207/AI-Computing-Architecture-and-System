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
            nstate := Mux(io.s_valid, sCompare, sReady)
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
            guessDigits := Mux(io.s_valid, VecInit( Seq(io.guess(3, 0), io.guess(7, 4), io.guess(11, 8), io.guess(15, 12)) ), VecInit( Seq.fill(4)(0.U(4.W) )))
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