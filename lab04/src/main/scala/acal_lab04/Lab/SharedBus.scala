package acal_lab04.Lab

import chisel3._
import chisel3.util._
import chisel3.util.log2Ceil

class MasterInterface(val addrWidth: Int, val dataWidth: Int) extends Bundle {
    val addr = UInt(addrWidth.W)
    val data = UInt(dataWidth.W)
    val size = UInt((log2Ceil(dataWidth / 8)).W)
    override def clone: MasterInterface = {
      new MasterInterface(addrWidth, dataWidth).asInstanceOf[this.type]
    }
}

class SlaveInterface(val addrWidth: Int, val dataWidth: Int) extends Bundle {
    val addr = UInt(addrWidth.W)
    val data = UInt(dataWidth.W)
    val size = UInt((log2Ceil(dataWidth / 8)).W)
    override def clone: SlaveInterface = {
      new SlaveInterface(addrWidth, dataWidth).asInstanceOf[this.type]
    }
}

class Decoder(addrWidth: Int, addrMap: Seq[(Int, Int)]) extends Module {
    val io = IO(new Bundle {
        val addr = Input(UInt(addrWidth.W))
        val select = Output(Bool())
    })
    val select = addrMap.zipWithIndex.foldLeft(false.B) { case (result, ((startAddress, size), index)) =>
      result || (io.addr >= startAddress.U && io.addr < (startAddress + size).U)
    }
    io.select := select
}

class ShareBus( addrWidth: Int, dataWidth: Int, numSlaves: Int, val addrMap: Seq[(Int, Int)]) extends Module {
  val io = IO(new Bundle {
    val masters = Flipped(Decoupled(new MasterInterface(addrWidth, dataWidth)))
    val slaves = Vec(numSlaves, Decoupled(new SlaveInterface(addrWidth, dataWidth)))
  })
    // decoder
    val decoders = Seq.tabulate(numSlaves) { i =>
      Module(new Decoder(addrWidth, addrMap.slice(i, i+1)))
    }

    // Initialize signals
    for (i <- 0 until numSlaves) {
      io.slaves(i).valid := io.masters.valid && decoders(i).io.select
      io.slaves(i).bits.addr  := io.masters.bits.addr
      io.slaves(i).bits.data  := io.masters.bits.data
      io.slaves(i).bits.size  := io.masters.bits.size
      decoders(i).io.addr := io.masters.bits.addr
    }

    io.masters.ready := io.slaves.map(_.ready).reduce(_ || _) // OR all ready signals
}

