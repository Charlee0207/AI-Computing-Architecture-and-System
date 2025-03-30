package acal_lab05.Lab3

import chisel3.iotesters.{Driver,PeekPokeTester}

class LFSR_FibonacciTest(dut:LFSR_Fibonacci) extends PeekPokeTester(dut){

    def int2string(i: Int) : String ={
      String.format("%" + 4 + "s", i.toBinaryString).replace(' ', '0')
    }

    poke(dut.io.seed.bits,9)
    poke(dut.io.seed.valid,true)
    step(1)

    for(i <- 0 until 16){
      poke(dut.io.seed.valid,false)
      var out = peek(dut.io.rndNum).toInt
      println(int2string(out))
      step(1)
    }
}

object LFSR_FibonacciTest extends App{
    Driver.execute(args,()=>new LFSR_Fibonacci(4)){
        dut:LFSR_Fibonacci => new LFSR_FibonacciTest(dut)
    }
}

// class LFSR_Fibonacci (n:Int, seed:Int = 1)extends Module
