# Lab07 Group

```
.
├── emulator.cpp
├── linenoise.hpp
├── Makefile ------------ 可根據需要自行修改
├── README.md
└── test_assembly ------- 測試資料請放在這，副檔名為 .txt
```

## Building and running

Build using `make`

Run using `./obj/emulator [assembly file path]`
or `make run` 

Clean using `make clean`

## 組員
| Name | Student ID | Role         |
| ---- | ---------- | ------------ |
|   吳明真   | 11262611(nthu)  | Project lead |
|   李承澔   | 113062559(nthu) | Member 1     |
|   王世揚   | (nycu)          | Member 2     |


## 指令表

| RV32 | RV64     | Mnemonic                  | Instruction       | Zba      | Zbb | Zbc | Zbs |Owner|
| ---- | -------- | ------------------------- | ----------------- | -------- | --- | --- | --- |---|
|&#10003;|&#10003;|andn _rd_, _rs1_, _rs2_|[insns-andn](https://github.com/riscv/riscv-bitmanip/blob/main/bitmanip/insns/andn.adoc)||&#10003;|||王世揚|
|&#10003;|&#10003;|clmul _rd_, _rs1_, _rs2_|[insns-clmul](https://github.com/riscv/riscv-bitmanip/blob/main/bitmanip/insns/clmul.adoc)|||&#10003;||王世揚|
|&#10003;|&#10003;|clmulh _rd_, _rs1_, _rs2_|[insns-clmulh](https://github.com/riscv/riscv-bitmanip/blob/main/bitmanip/insns/clmulh.adoc)|||&#10003;||王世揚|
|&#10003;|&#10003;|clmulr _rd_, _rs1_, _rs2_|[insns-clmulr](https://github.com/riscv/riscv-bitmanip/blob/main/bitmanip/insns/clmulr.adoc)|||&#10003;||王世揚|
|&#10003;|&#10003;|clz _rd_, _rs_|[insns-clz](https://github.com/riscv/riscv-bitmanip/blob/main/bitmanip/insns/clz.adoc)||&#10003;|||王世揚|
|&#10003;|&#10003;|cpop _rd_, _rs_|[insns-cpop](https://github.com/riscv/riscv-bitmanip/blob/main/bitmanip/insns/cpop.adoc)||&#10003;|||王世揚|
|&#10003;|&#10003;|ctz _rd_, _rs_|[insns-ctz](https://github.com/riscv/riscv-bitmanip/blob/main/bitmanip/insns/ctz.adoc)||&#10003;|||吳明真|
|&#10003;|&#10003;|max _rd_, _rs1_, _rs2_|[insns-max](https://github.com/riscv/riscv-bitmanip/blob/main/bitmanip/insns/max.adoc)||&#10003;|||王世揚|
|&#10003;|&#10003;|maxu _rd_, _rs1_, _rs2_|[insns-maxu](https://github.com/riscv/riscv-bitmanip/blob/main/bitmanip/insns/maxu.adoc)||&#10003;|||王世揚|
|&#10003;|&#10003;|min _rd_, _rs1_, _rs2_|[insns-min](https://github.com/riscv/riscv-bitmanip/blob/main/bitmanip/insns/min.adoc)||&#10003;|||王世揚|
|&#10003;|&#10003;|minu _rd_, _rs1_, _rs2_|[insns-minu](https://github.com/riscv/riscv-bitmanip/blob/main/bitmanip/insns/minu.adoc)||&#10003;|||王世揚|
|&#10003;|&#10003;|**orc.b _rd_, _rs_**|[insns-orc_b](https://github.com/riscv/riscv-bitmanip/blob/main/bitmanip/insns/orc_b.adoc)||&#10003;|||王世揚|
|&#10003;|&#10003;|**orn _rd_, _rs1_, _rs2_**|[insns-orn](https://github.com/riscv/riscv-bitmanip/blob/main/bitmanip/insns/orn.adoc)||&#10003;|||吳明真|
|&#10003;|&#10003;|rev8 _rd_, _rs_|[insns-rev8](https://github.com/riscv/riscv-bitmanip/blob/main/bitmanip/insns/rev8.adoc)||&#10003;|||吳明真|
|&#10003;|&#10003;|rol _rd_, _rs1_, _rs2_|[insns-rol](https://github.com/riscv/riscv-bitmanip/blob/main/bitmanip/insns/rol.adoc)||&#10003;|||吳明真|
|&#10003;|&#10003;|ror _rd_, _rs1_, _rs2_|[insns-ror](https://github.com/riscv/riscv-bitmanip/blob/main/bitmanip/insns/ror.adoc)||&#10003;|||吳明真|
|&#10003;|&#10003;|rori _rd_, _rs1_, _shamt_|[insns-rori](https://github.com/riscv/riscv-bitmanip/blob/main/bitmanip/insns/rori.adoc)||&#10003;|||吳明真|
|&#10003;|&#10003;|bclr _rd_, _rs1_, _rs2_|[insns-bclr](https://github.com/riscv/riscv-bitmanip/blob/main/bitmanip/insns/bclr.adoc)||||&#10003;|吳明真|
|&#10003;|&#10003;|bclri _rd_, _rs1_, _imm_|[insns-bclri](https://github.com/riscv/riscv-bitmanip/blob/main/bitmanip/insns/bclri.adoc)||||&#10003;|吳明真|
|&#10003;|&#10003;|bext _rd_, _rs1_, _rs2_|[insns-bext](https://github.com/riscv/riscv-bitmanip/blob/main/bitmanip/insns/bext.adoc)||||&#10003;|吳明真|
|&#10003;|&#10003;|bexti _rd_, _rs1_, _imm_|[insns-bexti](https://github.com/riscv/riscv-bitmanip/blob/main/bitmanip/insns/bexti.adoc)||||&#10003;|吳明真|
|&#10003;|&#10003;|binv _rd_, _rs1_, _rs2_|[insns-binv](https://github.com/riscv/riscv-bitmanip/blob/main/bitmanip/insns/binv.adoc)||||&#10003;|李承澔|
|&#10003;|&#10003;|binvi _rd_, _rs1_, _imm_|[insns-binvi](https://github.com/riscv/riscv-bitmanip/blob/main/bitmanip/insns/binvi.adoc)||||&#10003;|李承澔|
|&#10003;|&#10003;|bset _rd_, _rs1_, _rs2_|[insns-bset](https://github.com/riscv/riscv-bitmanip/blob/main/bitmanip/insns/bset.adoc)||||&#10003;|李承澔|
|&#10003;|&#10003;|bseti _rd_, _rs1_, _imm_|[insns-bseti](https://github.com/riscv/riscv-bitmanip/blob/main/bitmanip/insns/bseti.adoc)||||&#10003;|李承澔|
|&#10003;|&#10003;|**sext.b _rd_, _rs_**|[insns-sext_b](https://github.com/riscv/riscv-bitmanip/blob/main/bitmanip/insns/sext_b.adoc)||&#10003;|||李承澔|
|&#10003;|&#10003;|**sext.h _rd_, _rs_**|[insns-sext_h](https://github.com/riscv/riscv-bitmanip/blob/main/bitmanip/insns/sext_h.adoc)||&#10003;|||李承澔|
|&#10003;|&#10003;|**sh1add _rd_, _rs1_, _rs2_**|[insns-sh1add](https://github.com/riscv/riscv-bitmanip/blob/main/bitmanip/insns/sh1add.adoc)|&#10003;||||李承澔|
|&#10003;|&#10003;|**sh2add _rd_, _rs1_, _rs2_**|[insns-sh2add](https://github.com/riscv/riscv-bitmanip/blob/main/bitmanip/insns/sh2add.adoc)|&#10003;||||李承澔|
|&#10003;|&#10003;|**sh3add _rd_, _rs1_, _rs2_**|[insns-sh3add](https://github.com/riscv/riscv-bitmanip/blob/main/bitmanip/insns/sh3add.adoc)|&#10003;||||李承澔|
|&#10003;|&#10003;|**xnor _rd_, _rs1_, _rs2_**|[insns-xnor](https://github.com/riscv/riscv-bitmanip/blob/main/bitmanip/insns/xnor.adoc)||&#10003;|||李承澔|
|&#10003;|&#10003;|**zext.h _rd_, _rs_**|[insns-zext_h](https://github.com/riscv/riscv-bitmanip/blob/main/bitmanip/insns/zext_h.adoc)||&#10003;|||李承澔|


# rv32emulator

## Building and running

Build using `make`

Run using `./obj/emulator [assembly file path]`

## Assembly Syntax

Terms should be separated by whitespace! e.g., "add s0 s1 s2"
Commas and other delimiters are not support at the moment.

## Debugging commands

Execution will result in a command prompt, preceded by some information about the instruction to execute next.
For example:

```
Next: lui x02, 0x00000010
[inst:      1 pc:      0, src line    3] 
>> 
```

The following are the supported commands:

* s, or [blank] : Step to the next instruction
* c : Continue execution until termination
* r : Reads the value of the registers. Both register number and alas formats can be used, without space after 'r'. e.g., `rx0`, `rra`. A simple `r` with no trailing register name will print all registers
* m : Reads memory values. Memory address prefixed with m (e.g., `m0x1000` or `m1024`) will print four bytes starting from that address. If you want to read more values, give the number of bytes as a parameger. e.g., `m0x1000 16`.
* b : Set breakpoint. e.g., `b17` to set a breakpoint at source code line 17. The list of existing breakpoints can be seen with a simple `b` with no suffixes. Breakpoints can be removed with an uppercase `B`. e.g., `B16`.
* l : `l` with no arguments will print the compiled hardware instructions (after translating pseudo-instructions), each with its line number from the original source file.
* q : `q` with no arguments will exit the emulator


## Caveats

* Pseudo-instructions are not elegantly compiled, yet
* Only one action per line supported! For example, comments and labels shoud go on their own line
