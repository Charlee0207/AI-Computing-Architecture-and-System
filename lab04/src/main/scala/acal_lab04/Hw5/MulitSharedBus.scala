package acal_lab04.Lab

import chisel3._
import chisel3.util._
import chisel3.util.log2Ceil


class MultiShareBus(val addrWidth: Int,val dataWidth: Int,val numMasters: Int,val numSlaves: Int, val addrMap: Seq[(Int, Int)]) extends Module {
  val io = IO(new Bundle {
    val masters = Vec(numMasters, Flipped(Decoupled(new MasterInterface(addrWidth, dataWidth))))
    val slaves = Vec(numSlaves, Decoupled(new SlaveInterface(addrWidth, dataWidth)))
  })
    // decoder
    val decoders = Seq.tabulate(numSlaves) { i =>
      Module(new Decoder(addrWidth, addrMap.slice(i, i+1)))
    }


    // // two master arbiter (round robin)
    // // use one bit to record which master is last granted
    val lastGrant       = RegInit(0.U(1.W)) // only two masters
    val grant           = RegInit(0.U(1.W)) // only two masters
    val timer           = RegInit(0.U(1.W))
    val multiReq        = Wire(Bool())

    // initialize the ready signal
    for(i <- 0 until numMasters){
      io.masters(i).ready   := false.B 
    }
    multiReq := io.masters(0).valid && io.masters(1).valid

    // Since the masters and the slaves never deassert valid and ready signals after asserting,
    // we cannot change the grant FF using valid and ready signal
    // However, the transaction in this bus always cost 2 cycles, 
    // we use timer to update grant register  
    when(multiReq){ timer := timer + 1.U }
    when(timer===1.U){ lastGrant := ~lastGrant}

    // If there're multi request, grant the master using updated lastGrant
    // Else, simply grant the requested master
    grant := Mux( multiReq, 
                  lastGrant, 
                  Mux(io.masters(0).valid, 0.U, 1.U))

    // OR all ready signals, and assign to granted master
    io.masters(grant).ready := io.slaves.map(_.ready).reduce(_ || _)   

    // Initialize signals
    for (i <- 0 until numSlaves) {
      io.slaves(i).valid      := io.masters(grant).valid && decoders(i).io.select
      io.slaves(i).bits.addr  := io.masters(grant).bits.addr
      io.slaves(i).bits.data  := io.masters(grant).bits.data
      io.slaves(i).bits.size  := io.masters(grant).bits.size
      decoders(i).io.addr     := io.masters(grant).bits.addr
    }

}