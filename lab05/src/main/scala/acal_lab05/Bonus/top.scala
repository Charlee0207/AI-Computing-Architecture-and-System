package acal_lab05.Bonus

import chisel3._
import chisel3.util._
import acal_lab05.Hw3.NumGuess

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