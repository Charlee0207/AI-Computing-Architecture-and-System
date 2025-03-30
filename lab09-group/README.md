# Lab09 Group

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
|   李承澔   | 113062559(nthu) | Project lead |
|   吳明真   | 11262611(nthu)  | Member 1     |


## 指令表
We spread workload by module, not instruction.

  | Num | Module              | Owner |
  | --- | ------------------- | ----- |
  | 1   | `RiscvDefs`         | 李承澔 |
  | 1   | `Controller`        | 李承澔 |
  | 1   | `ALU`               | 吳明真 |

  | Num | Mnemonic            | Owner |
  | --- | ------------------- | ----- |
  | 1   | clz rd, rs          | Both  |
  | 2   | ctz rd, rs          | Both  |
  | 3   | cpop rd, rs         | Both  |
  | 4   | andn rd, rs1, rs2   | Both  |
  | 5   | orn rd, rs1, rs2    | Both  |
  | 6   | xnor rd, rs1, rs2   | Both  |
  | 7   | min rd, rs1, rs2    | Both  |
  | 8   | max rd, rs1, rs2    | Both  |
  | 9   | minu rd, rs1, rs2   | Both  |
  | 10  | maxu rd, rs1, rs2   | Both  |
  | 11  | sext.b rd, rs       | Both  |
  | 12  | sext.h rd, rs       | Both  |
  | 13  | bset rd, rs1, rs2   | Both  |
  | 14  | bclr rd, rs1, rs2   | Both  |
  | 15  | binv rd, rs1, rs2   | Both  |
  | 16  | bext rd, rs1, rs2   | Both  | 
  | 17  | bseti rd, rs1, imm  | Both  |
  | 18  | bclri rd, rs1, imm  | Both  |
  | 19  | binvi rd, rs1, imm  | Both  |
  | 20  | bexti rd, rs1, imm  | Both  |
  | 21  | ror rd, rs1, rs2    | Both  |
  | 22  | rol rd, rs1, rs2    | Both  |
  | 23  | rori rd, rs1, imm   | Both  |
  | 24  | sh1add rd, rs1, rs2 | Both  |
  | 25  | sh2add rd, rs1, rs2 | Both  |
  | 26  | sh3add rd, rs1, rs2 | Both  |
  | 27  | rev8 rd, rs         | Both  |
  | 28  | zext.h rd, rs       | Both  |
  | 29  | orc.b  rd, rs       | Both  |


# rv32emulator

## Building and running

Run using `./obj/emulator [assembly file path]` under `Emulator/`

and load assembly using `bash load_test_data.sh [-s|test assembly file name] [Emulator]` under `Hardware/Lab`

and run simulation using `sbt 'Test/runMain acal_lab09.topTest'` under `Hardware/Lab`

