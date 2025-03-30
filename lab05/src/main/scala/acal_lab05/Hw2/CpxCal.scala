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