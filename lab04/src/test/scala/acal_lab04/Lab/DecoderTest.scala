package acal_lab04.Lab

import chisel3._
import chisel3.iotesters.{PeekPokeTester, Driver, ChiselFlatSpec}

class DecoderTestBench(c: Decoder) extends PeekPokeTester(c) {
    for(t <- 0 until 16){
        val address = 32768 + rnd.nextInt(20000) // Adjust the range to cover both address maps
   
        val addr_map = Seq(
            (32768, 10000), // Range 1: 32768 to 42768
        )
        val expectedSelect = if (addr_map.exists { case (start, size) => address >= start && address < (start + size) }) 1 else 0
        poke(c.io.addr, address)
        step(1) // Step 1 is enough for the logic to settle
        println(s"Test: $t Address: $address, Expected Select: $expectedSelect")
        expect(c.io.select, expectedSelect)
    }
}

object DecoderTest extends App {
  val addrWidth = 16
  val addr_map = Seq(
      (32768, 10000), // Range: 32768 to 42768
  )
  Driver.execute(Array("-td","./generated","-tbn","verilator"),() => new Decoder(addrWidth, addr_map)) {
    c => new DecoderTestBench(c)
  }
}
