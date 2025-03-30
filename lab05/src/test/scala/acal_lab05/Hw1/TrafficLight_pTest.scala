package acal_lab05.Hw1

import chisel3.iotesters.{Driver,PeekPokeTester}

class TrafficLight_pTest (tl : TrafficLight_p) extends PeekPokeTester(tl){

  //for first round complete period without the effect of p_button
  step(25)

  //for 2nd round test : consider the effect of p_button
  step(14)
  poke(tl.io.P_button,true)
  step(1)
  poke(tl.io.P_button,false)
  step(17)
  poke(tl.io.P_button,true)
  step(1)
  poke(tl.io.P_button,false)

  step(50)

  println("Simulation completed!! Go to check your vcd file!!!")
}

object TrafficLight_pTest extends App{
  val Ytime = 3
  val Gtime = 7
  val Ptime = 5
  Driver.execute(args,() => new TrafficLight_p(Ytime,Gtime,Ptime)){
    c => new TrafficLight_pTest(c)
  }
}