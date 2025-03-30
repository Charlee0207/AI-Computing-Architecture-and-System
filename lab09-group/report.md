(NTHU_113062559_李承澔)  ACAL 2024 Fall Lab 9 HW Submission Template
===


###### tags: `AIAS Fall 2024`, `AIAS Spring 2024` `Submission Template`

[toc]

## Gitlab code link
:::info
Please paste the link to your private Gitlab repository for this homework submission here. 
:::

- [Gitlab link](https://course.playlab.tw/git/chngh0207/lab09) - 

## Homework 9
- You can create a new branch to modify your homework to make it easy to manage.

### Homework 9-1 5-stage pipelined CPU Implementation
- Please Finish the all others rv32i instrunctions，and pass the **rv32ui_SingleTest/TestALL.S** 

:::warning
Paste your gitlab branch URL link
[Gitlab link](https://course.playlab.tw/git/chngh0207/lab09/-/tree/hw1?ref_type=heads)
:::

:::warning
Paste the **simulate RegFile result**  Here
![](https://course.playlab.tw/md/uploads/8268977a-8caf-4628-acee-b75c6fa2172f.png)


:::
- You can use any single test，and compare to the  **rv32ui_SingleTest/golden.txt** 

### Homework 9-2 Data and Controll Hazards  
- List possible data hazard scenarios and describe how to resolve the hazard in your design.
    ```scala=
  is_D_rs1_W_rd_overlap := is_D_use_rs1 && is_W_use_rd && (ID_rs1 === WB_rd) && (WB_rd =/= 0.U(5.W))
  is_D_rs2_W_rd_overlap := is_D_use_rs2 && is_W_use_rd && (ID_rs2 === WB_rd) && (WB_rd =/= 0.U(5.W))
  is_D_rs1_M_rd_overlap := is_D_use_rs1 && is_M_use_rd && (ID_rs1 === MEM_rd) && (MEM_rd =/= 0.U(5.W))
  is_D_rs2_M_rd_overlap := is_D_use_rs2 && is_M_use_rd && (ID_rs2 === MEM_rd) && (MEM_rd =/= 0.U(5.W))
  is_D_rs1_E_rd_overlap := is_D_use_rs1 && is_E_use_rd && (ID_rs1 === EXE_rd) && (EXE_rd =/= 0.U(5.W))
  is_D_rs2_E_rd_overlap := is_D_use_rs2 && is_E_use_rd && (ID_rs2 === EXE_rd) && (EXE_rd =/= 0.U(5.W))
    ```
    :::info
    1. **Rd of instruction A in EXE stage is the same as Rs of instruction B in ID stage** 
    2. **Rd of instruction A in MEM stage is the same as Rs of instruction B in ID stage** 
    3. **Rd of instruction A in WB stage is the same as Rs of instruction B in ID stage**
    :::
- Please Finish the all others RV32I instrunctions，and pass the **rv32ui_SingleTest/TestDataHazard.S** 
:::warning
Paste your gitlab branch URL link
[Gitlab link](https://course.playlab.tw/git/chngh0207/lab09/-/tree/hw2?ref_type=heads)
:::
:::warning
Paste the **simulate RegFile result**  Here
![](https://course.playlab.tw/md/uploads/06ea56bb-9f3e-4505-9d11-11dd70d650b7.png)
:::
- In the result of register file，if sp(x2) is **zero**，it means you passed the test，otherwise，the value of sp(x2) is the test case that you did not passed.

:::info
- Bonus: data forwarding
    - Please paste your result in the [bonus](#Bonus) part (end of the document)
:::

### Homework 9-3 Performance Counters and Performance Analysis
1. Complete **8.~13.** performance counters listed below.
:::info
### Performance counter - HW
8. Mem Read Stall Cycle Count
Count cycles stalled due to memory read.
9. Mem Write Stall Cycle Count
Count cycles stalled due to memory write.
10. Mem Read Request Count
Count Load-type instruction.
11. Mem Write Request Count
Count Store-type instruction.
12. Mem Read Bytes Count
Count bytes read in Load-type instruction(lw/lh/lb - all 4bytes are occupied).
13. Mem Write Bytes Count
Count bytes write in Store-type instruction(sw/sh/sb - all 4bytes are occupied).
:::
:::warning
Paste your gitlab branch URL link
[Gitlab link](https://course.playlab.tw/git/chngh0207/lab09/-/tree/hw3?ref_type=heads)
:::
3. Complete **2.~4.** Performance analysis listed below.
:::info
### Performance analysis - HW
2. Average Mem Read Request Stall Cycle
Mem Read Stall Cycle Count/Mem Read Request Count
3. Average Mem Write Request Stall Cycle
Mem Write Stall Cycle Count/Mem Write Request Count
4. Total Bus bandwidth requiement (Read + Write, data)
Mem Read Bytes Count + Mem Write Bytes Count
:::
4. Run the **fib.asm** and post the **Performance count and analysis** result.
:::warning
Paste the result Here (Screenshot).
![](https://course.playlab.tw/md/uploads/bdb64f7f-599f-42d6-951f-e03aa6684e87.png)

:::
5. Explain How a 5-stage pipelined  CPU improves performance compared to a single-cycle CPU
:::warning
Explain the result Here.

A 5-stage pipelined CPU improves performance by dividing instruction execution into five stages, allowing multiple instructions to be processed simultaneously. 
Each stage works independently on different instructions, which not only increases throughput and utilizes hardware efficiently, but shorten the critical path.
Such that a 5-stage pipelined CPU has higher hardward utilization and lower clock frequency compared to single-cycle CPU.
:::

:::info
- Bonus: Kadane's algorithm & Merge sort
    - Please paste your result in the [bonus](#Bonus) part (end of the document)
:::


### Homework 9-4 Bitmanip Extension (Group Assignment)
#### Group Members
- List your group members (including you!) -
    - 李承澔
    - 吳明真

#### Gitlab code link
- Gitlab link of your branch - 
    - [link here](https://course.playlab.tw/git/chngh0207/lab09-group/-/tree/chngh0207?ref_type=heads)
- Gitlab link of your group project repo - 
    - [link here](https://course.playlab.tw/git/chngh0207/lab09-group)

#### 硬體架構圖：
- 小組選擇的base CPU架構圖，是誰的呢?
    - ![](https://course.playlab.tw/md/uploads/27c6d310-158e-4195-9d4a-c6b1db1c1bd8.jpg)
    - 李承澔
:::danger
- 小組要統一一張圖喔!!!
:::

- Option 1 - 照指令分工的組別
    - 請描述你負責哪些指令，你如何設計Datapath和Controll signal呢?
        Datapath沿用原先的lab code的架構。
        在`RiscvDefs.scala`增加對應的指令定義，並在`Controller.scala`中根據`EXE_opcode`, `EXE_funct3`, `EXE_funct7`，賦予`io.E_ALUSel`正確的選擇指令選擇訊號，再交由ALU運算。
    - 新增了哪些Blocks，或者因為有相似利用了原本的Datapath呢?
        無新增block，沿用原先Datapath。
    - 根據你的描述，將你的設計畫在小組選擇的base CPU上。

    :::info
    下面部分為擴充單條指令所需的部分，負責幾條指令就要複製幾份喔。
    :::

    :::success

    ### Emulator functionality - < ANDN >
    > 描述該指令的格式以及功能
    
    > **將新增的 code**(case部份就好)放在下方並加上註解, 讓TA明白你是如何完成的。
    ```cpp=
    // X(rd) = X(rs1) & ~X(rs2)
    case ANDN: rf[i.a1.reg] = rf[i.a2.reg] & ~rf[i.a3.reg]; break;
    ```
    ### Emulator functionality - < CLMUL >
    ```cpp=
    // foreach (i from 0 to xlen by 1) {
    // output = if   ((rs2_val >> i) & 1)
    // 			then output ^ (rs1_val << i);
    // 			else output;
    // }
    // X[rd] = output  
    case CLMUL: 
        for(int b=0; b<=32; b++){
            if(rf[i.a3.reg]>>b & 1) 
                 rf[i.a1.reg] ^= (rf[i.a2.reg]<<b);
        }
    break;
    ```
    ### Emulator functionality - < CLMULH >
    ```cpp=
    // foreach (i from 1 to xlen by 1) {
    // output = if   ((rs2_val >> i) & 1)
    // 		 	then output ^ (rs1_val >> (xlen - i));
    // 		 	else output;
    // }
    // X[rd] = output
    case CLMULH:
        for(int b=1; b<=32; b++){
            if(rf[i.a3.reg]>>b & 1) 
                 rf[i.a1.reg] ^= (rf[i.a2.reg]>>(32-b));
        }
    break;
    ```
    ### Emulator functionality - < CLMULR >
    ```cpp=
    // foreach (i from 0 to (xlen - 1) by 1) {
    // output = if   ((rs2_val >> i) & 1)
    // 			then output ^ (rs1_val >> (xlen - i - 1));
    // 			else output;
    // }
    // X[rd] = output
    case CLMULR:
        for(int b=0; b<=31; b++){
            if(rf[i.a3.reg]>>b & 1) 
                 rf[i.a1.reg] ^= (rf[i.a2.reg]>>(32-b-1));
        }
    break;
    ```
    ### Emulator functionality - < CLZ >
    ```cpp=
    // function HighestSetBit x = {
    // foreach (i from (xlen - 1) to 0 by 1 in dec)
    // 		if [x[i]] == 0b1 then return(i) else ();
    // 			return -1;
    // }
    // let rs = X(rs);
    // X[rd] = (xlen - 1) - HighestSetBit(rs);
    case CLZ:
        for(int b=31; b>=0; b--){
            if(rf[i.a2.reg]>>b & 1)
                rf[i.a1.reg] = (32-1)-b;
        }
    break;
    ```
    ### Emulator functionality - < CPOP >
    ```cpp=
    // foreach (i from 0 to (xlen - 1) in inc)
    // 	if rs[i] == 0b1 then bitcount = bitcount + 1 else ();
    // X[rd] = bitcount
    case CPOP:
        uint32_t bitcount;
        bitcount = 0;
        for(int b=0; b<=31; b++){
            if(rf[i.a2.reg]>>b & 1)
                bitcount++;
        }
        rf[i.a1.reg] = bitcount;
    break;
    ```
    ### Emulator functionality - < CTZ >
    ```cpp=
    // function LowestSetBit x = {
    // foreach (i from 0 to (xlen - 1) by 1 in dec)
    // 		if [x[i]] == 0b1 then return(i) else ();
    // return xlen;
    // }
    // let rs = X(rs);
    // X[rd] = LowestSetBit(rs);
    case CTZ:
        for(int b=0; b<=31; b++){
            if(rf[i.a2.reg]>>b & 1){
                rf[i.a1.reg] = b;
                break;
            }
        }
        rf[i.a1.reg] = 32;
    break;
    ```
    ### Emulator functionality - < MAX >
    ```cpp=
    // X(rd) = if   rs1_val <_s rs2_val
    //         then rs2_val
    //         else rs1_val;
    case MAX: rf[i.a1.reg] = (*(int32_t*)&rf[i.a2.reg]<*(int32_t*)&rf[i.a3.reg]) ? rf[i.a3.reg] : rf[i.a2.reg]; break;
    ```
    ### Emulator functionality - < MAXU >
    ```cpp=
    // X(rd) = if   rs1_val <_u rs2_val
    //         then rs2_val
    //         else rs1_val;
    case MAXU: rf[i.a1.reg] = (rf[i.a2.reg]<rf[i.a3.reg]) ? rf[i.a3.reg] : rf[i.a2.reg]; break;
    ```
    ### Emulator functionality - < MIN >
    ```cpp=
    // X(rd) = if   rs1_val <_s rs2_val
    //         then rs1_val
    //         else rs2_val;
    case MIN: rf[i.a1.reg] = (*(int32_t*)&rf[i.a2.reg]>*(int32_t*)&rf[i.a3.reg]) ? rf[i.a3.reg] : rf[i.a2.reg]; break;
    ```
    ### Emulator functionality - < MINU >
    ```cpp=
    // X(rd) = if   rs1_val <_u rs2_val
    //         then rs1_val
    //         else rs2_val;
    case MINU: rf[i.a1.reg] = (rf[i.a2.reg]>rf[i.a3.reg]) ? rf[i.a3.reg] : rf[i.a2.reg]; break;
    ```
    ### Emulator functionality - < ORC_B >
    ```cpp=    // foreach (i from 0 to xlen by 8) {
    // output[(i + 7)..i] = if   input[(i - 7)..i] == 0
    // 						then 0b00000000
    // 						else 0b11111111;
    // }
    // X[rd] = output;
    case ORC_B: 
        uint32_t output;
        for(int b=0; b<32; b+=8){
            uint8_t byte = (rf[i.a2.reg]>>b) & 0xFF;
            if(byte!=0){
                output |= (0xFF<<b);
            }
        }
        rf[i.a1.reg] = output;
    break;
    ```
    ### Emulator functionality - < ORN >
    ```cpp=
    // X(rd) = X(rs1) | ~X(rs2);
    case ORN: rf[i.a1.reg] = rf[i.a2.reg] | ~rf[i.a3.reg]; break;
    ```
    ### Emulator functionality - < REV8 >
    ```cpp=
    // foreach (i from 0 to xlen by 8) {
    // 		output[i..(i + 7)] = input[(j - 7)..j];
    // 		j = j - 8;
    // }
    // X[rd] = output
    case REV8: rf[i.a1.reg] = (rf[i.a2.reg] & 0xFF)<<24 | (rf[i.a2.reg]>>8 & 0xFF)<<16 | (rf[i.a2.reg]>>16 & 0xFF)<<8 | (rf[i.a2.reg]>>24 & 0xFF); break;
    ```
    ### Emulator functionality - < ROL >
    ```cpp=
    // let shamt = 	if   xlen == 32
    // 				then X(rs2)[4..0]
    // 				else X(rs2)[5..0];
    // X(rd) = (X(rs1) << shamt) | (X(rs2) >> (xlen - shamt));
    case ROL: rf[i.a1.reg] = (rf[i.a2.reg]<<(rf[i.a3.reg]&0x1F)) | (rf[i.a2.reg]>>(32-rf[i.a3.reg]&0x1F)); break;
    ```
    ### Emulator functionality - < ROR >
    ```cpp=
    // let shamt = 	if   xlen == 32
    // 				then X(rs2)[4..0]
    // 				else X(rs2)[5..0];
    // X(rd) = (X(rs1) >> shamt) | (X(rs2) << (xlen - shamt));
    case ROR: rf[i.a1.reg] = (rf[i.a2.reg]>>(rf[i.a3.reg]&0x1F)) | (rf[i.a2.reg]<<(32-rf[i.a3.reg]&0x1F)); break;
    ```
    ### Emulator functionality - < RORI >
    ```cpp=
    // let shamt = if   xlen == 32
    // 				then shamt[4..0]
    // 				else shamt[5..0];
    //  X(rd) = (X(rs1) >> shamt) | (X(rs1) << (xlen - shamt));
    case RORI: rf[i.a1.reg] = (rf[i.a2.reg]>>(i.a3.imm&0x1F)) | (rf[i.a2.reg]<<(32-i.a3.imm&0x1F)); break;
    ```
    ### Emulator functionality - < BCLR >
    ```cpp=
    // let index = X(rs2) & (XLEN - 1);
    // X(rd) = X(rs1) & ~(1 << index)
    case BCLR: rf[i.a1.reg] = rf[i.a2.reg] & ~(1<<(rf[i.a3.reg]&0x1F)); break;
    ```
    ### Emulator functionality - < BCLRI >
    ```cpp=
    // let index = shamt & (XLEN - 1);
    // X(rd) = X(rs1) & ~(1 << index)
    case BCLRI: rf[i.a1.reg] = rf[i.a2.reg] & ~(1<<(i.a3.imm&0x1F)); break;
    ```
    ### Emulator functionality - < BEXT >
    ```cpp=
    // let index = X(rs2) & (XLEN - 1);
    // X(rd) = (X(rs1) >> index) & 1;
    case BEXT: rf[i.a1.reg] = (rf[i.a2.reg] >> (rf[i.a3.reg]&0x1F)) & 1; break;
    ```
    ### Emulator functionality - < BEXTI >
    ```cpp=
    // let index = X(rs2) & (XLEN - 1);
    // X(rd) = (X(rs1) >> index) & 1;
    case BEXTI: rf[i.a1.reg] = (rf[i.a2.reg] >> (i.a3.imm&0x1F)) & 1; break;
    ```
    ### Emulator functionality - < BINV >
    ```cpp=
    // let index = X(rs2) & (XLEN - 1);
    // X(rd) = X(rs1) ^ (1 << index)
    case BINV: rf[i.a1.reg] = rf[i.a2.reg] ^ (1 << (rf[i.a3.reg]&0x1F)); break;
    ```
    ### Emulator functionality - < BINVI >
    ```cpp=
    // let index = shamt & (XLEN - 1);
    // X(rd) = X(rs1) ^ (1 << index)
    case BINVI: rf[i.a1.reg] = rf[i.a2.reg] ^ (1 << (i.a3.imm&0x1F)); break;
    ```
    ### Emulator functionality - < BSET >
    ```cpp=
    // let index = X(rs2) & (XLEN - 1);
    // X(rd) = X(rs1) | (1 << index)
    case BSET: rf[i.a1.reg] = rf[i.a2.reg] | (1 << (rf[i.a3.reg]&0x1F)); break;
    ```
    ### Emulator functionality - < BSETI >
    ```cpp=
    // let index = shamt & (XLEN - 1);
    // X(rd) = X(rs1) | (1 << index)
    case BSETI: rf[i.a1.reg] = rf[i.a2.reg] | (1 << (i.a3.imm&0x1F)); break;
    ```
    ### Emulator functionality - < SEXT_B >
    ```cpp=
    // X(rd) = SignExtend(X(rs)[7:0]);
    case SEXT_B: rf[i.a1.reg] = (int8_t)(rf[i.a2.reg] & 0xFF); break;
    ```
    ### Emulator functionality - < SEXT_H >
    ```cpp=
    // X(rd) = SignExtend(X(rs)[15:0]);
    case SEXT_H: rf[i.a1.reg] = (int16_t)(rf[i.a2.reg] & 0xFFFF); break;
    ```
    ### Emulator functionality - < SH1ADD >
    ```cpp=
    // X(rd) = X(rs2) + (X(rs1) << 1);
    case SH1ADD: rf[i.a1.reg] = (rf[i.a2.reg] << 1) + rf[i.a3.reg]; break;
    ```
    ### Emulator functionality - < SH2ADD >
    ```cpp=
    // X(rd) = X(rs2) + (X(rs1) << 2);
    case SH2ADD: rf[i.a1.reg] = (rf[i.a2.reg] << 2) + rf[i.a3.reg]; break
    ```
    ### Emulator functionality - < SH3ADD >
    ```cpp=
    // X(rd) = X(rs2) + (X(rs1) << 3);
    case SH3ADD: rf[i.a1.reg] = (rf[i.a2.reg] << 3) + rf[i.a3.reg]; break;
    ```
    ### Emulator functionality - < XNOR >
    ```cpp=
    // X(rd) = ~(X(rs1) ^ X(rs2));
    case XNOR: rf[i.a1.reg] = ~(rf[i.a2.reg] ^ rf[i.a3.reg]); break;
    ```
    ### Emulator functionality - < ZEXT_H >
    ```cpp=
    // X(rd) = ZeroExtend(X(rs)[15:0]);
    case ZEXT_H: rf[i.a1.reg] = rf[i.a2.reg] & 0xFFFF;  break;
    ```
    
    ### Assembler translation - < ANDN >
    ```cpp=
    case ANDN:		// andn rd, rs1, rs2	
        binary = (0x0C << 2) + 0x03; //opcode
        binary += i.a1.reg << 7;     //rd
        binary += 0b111 << 12;       //funct3
        binary += i.a2.reg << 15;    //rs1
        binary += i.a3.reg << 20;    //rs2
        binary += 0b0100000 << 25;   //funct7
    break;
    ```
    ### Assembler translation - < CLMUL >
    ```cpp=
    case CLMUL:		// clmul rd, rs1, rs2
        binary = (0x0C << 2) + 0x03; //opcode
        binary += i.a1.reg << 7;     //rd
        binary += 0b001 << 12;       //funct3
        binary += i.a2.reg << 15;    //rs1
        binary += i.a3.reg << 20;    //rs2
        binary += 0b0000101 << 25;   //funct7
    break;
    ```
    ### Assembler translation - < CLMULH >
    ```cpp=
    case CLMULH:	// clmulh rd, rs1, rs2	
        binary = (0x0C << 2) + 0x03; //opcode
        binary += i.a1.reg << 7;     //rd
        binary += 0b011 << 12;       //funct3
        binary += i.a2.reg << 15;    //rs1
        binary += i.a3.reg << 20;    //rs2
        binary += 0b0000101 << 25;   //funct7
    break;
    ```
    ### Assembler translation - < CLMULR >
    ```cpp=
    case CLMULR:	// clmulr rd, rs1, rs2
        binary = (0x0C << 2) + 0x03; //opcode
        binary += i.a1.reg << 7;     //rd
        binary += 0b010 << 12;       //funct3
        binary += i.a2.reg << 15;    //rs1
        binary += i.a3.reg << 20;    //rs2
        binary += 0b0000101 << 25;   //funct7
    break;
    ```
    ### Assembler translation - < MAX >
    ```cpp=
    case MAX:		// max rd, rs1, rs2
        binary = (0x0C << 2) + 0x03; //opcode
        binary += i.a1.reg << 7;     //rd
        binary += 0b110 << 12;       //funct3
        binary += i.a2.reg << 15;    //rs1
        binary += i.a3.reg << 20;    //rs2
        binary += 0b0000101 << 25;   //funct7
    break;
    ```
    ### Assembler translation - < MAXU >
    ```cpp=
    case MAXU: 		// maxu rd, rs1, rs2
        binary = (0x0C << 2) + 0x03; //opcode
        binary += i.a1.reg << 7;     //rd
        binary += 0b111 << 12;       //funct3
        binary += i.a2.reg << 15;    //rs1
        binary += i.a3.reg << 20;    //rs2
        binary += 0b0000101 << 25;   //funct7	
    break;
    ```
    ### Assembler translation - < MIN >
    ```cpp=
    case MIN: 		// min rd, rs1, rs2
        binary = (0x0C << 2) + 0x03; //opcode
        binary += i.a1.reg << 7;     //rd
        binary += 0b100 << 12;       //funct3
        binary += i.a2.reg << 15;    //rs1
        binary += i.a3.reg << 20;    //rs2
        binary += 0b0000101 << 25;   //funct7
    break;
    ```
    ### Assembler translation - < MINU >
    ```cpp=
    case MINU:		// minu rd, rs1, rs2
        binary = (0x0C << 2) + 0x03; //opcode
        binary += i.a1.reg << 7;     //rd
        binary += 0b101 << 12;       //funct3
        binary += i.a2.reg << 15;    //rs1
        binary += i.a3.reg << 20;    //rs2
        binary += 0b0000101 << 25;   //funct7
    break;
    ```
    ### Assembler translation - < ORN >
    ```cpp=
    case ORN:		// orn rd, rs1, rs2
        binary = (0x0C << 2) + 0x03; //opcode
        binary += i.a1.reg << 7;     //rd
        binary += 0b110 << 12;       //funct3
        binary += i.a2.reg << 15;    //rs1
        binary += i.a3.reg << 20;    //rs2
        binary += 0b0100000 << 25;   //funct7
    break;
    ```
    ### Assembler translation - < ROL >
    ```cpp=
    case ROL: 		// rol rd, rs1, rs2
        binary = (0x0C << 2) + 0x03; //opcode
        binary += i.a1.reg << 7;     //rd
        binary += 0b001 << 12;       //funct3
        binary += i.a2.reg << 15;    //rs1
        binary += i.a3.reg << 20;    //rs2
        binary += 0b0110000 << 25;   //funct7
    break;
    ```
    ### Assembler translation - < ROR >
    ```cpp=
    case ROR: 		// ror rd, rs1, rs2
        binary = (0x0C << 2) + 0x03; //opcode
        binary += i.a1.reg << 7;     //rd
        binary += 0b101 << 12;       //funct3
        binary += i.a2.reg << 15;    //rs1
        binary += i.a3.reg << 20;    //rs2
        binary += 0b0110000 << 25;   //funct7
    break;
    ```
    ### Assembler translation - < BCLR >
    ```cpp=
    case BCLR: 		// bclr rd, rs1, rs2
        binary = (0x0C << 2) + 0x03; //opcode
        binary += i.a1.reg << 7;     //rd
        binary += 0b001 << 12;       //funct3
        binary += i.a2.reg << 15;    //rs1
        binary += i.a3.reg << 20;    //rs2
        binary += 0b0100100 << 25;   //funct7
    break;
    ```
    ### Assembler translation - < BEXT >
    ```cpp=
    case BEXT: 		// bext rd, rs1, rs2
        binary = (0x0C << 2) + 0x03; //opcode
        binary += i.a1.reg << 7;     //rd
        binary += 0b101 << 12;       //funct3
        binary += i.a2.reg << 15;    //rs1
        binary += i.a3.reg << 20;    //rs2
        binary += 0b0100100 << 25;   //funct7
    break;
    ```
    ### Assembler translation - < BINV >
    ```cpp=
    case BINV:      // binv rd, rs1, rs2
        binary = (0x0C << 2) + 0x03; //opcode
        binary += i.a1.reg << 7;     //rd
        binary += 0b001 << 12;       //funct3
        binary += i.a2.reg << 15;    //rs1
        binary += i.a3.reg << 20;    //rs2
        binary += 0b0110100 << 25;   //funct7
    break;
    ```
    ### Assembler translation - < BSET >
    ```cpp=
    case BSET:      // bset rd, rs1, rs2
        binary = (0x0C << 2) + 0x03; //opcode
        binary += i.a1.reg << 7;     //rd
        binary += 0b001 << 12;       //funct3
        binary += i.a2.reg << 15;    //rs1
        binary += i.a3.reg << 20;    //rs2
        binary += 0b0010100 << 25;   //funct7
    break;
    ```
    ### Assembler translation - < SH1ADD >
    ```cpp=
    case SH1ADD:    // sh1add rd, rs1, rs2
        binary = (0x0C << 2) + 0x03; //opcode
        binary += i.a1.reg << 7;     //rd
        binary += 0b010 << 12;       //funct3
        binary += i.a2.reg << 15;    //rs1
        binary += i.a3.reg << 20;    //rs2
        binary += 0b0010000 << 25;   //funct7
    break;
    ```
    ### Assembler translation - < SH2ADD >
    ```cpp=
    case SH2ADD:    // sh2add rd, rs1, rs2
        binary = (0x0C << 2) + 0x03; //opcode
        binary += i.a1.reg << 7;     //rd
        binary += 0b100 << 12;       //funct3
        binary += i.a2.reg << 15;    //rs1
        binary += i.a3.reg << 20;    //rs2
        binary += 0b0010000 << 25;   //funct7
    break;
    ```
    ### Assembler translation - < SH3ADD >
    ```cpp=
    case SH3ADD:    // sh3add rd, rs1, rs2
        binary = (0x0C << 2) + 0x03; //opcode
        binary += i.a1.reg << 7;     //rd
        binary += 0b110 << 12;       //funct3
        binary += i.a2.reg << 15;    //rs1
        binary += i.a3.reg << 20;    //rs2
        binary += 0b0010000 << 25;   //funct7
    break;
    ```
    ### Assembler translation - < XNOR >
    ```cpp=
    case XNOR:      // xnor rd, rs1, rs2
        binary = (0x0C << 2) + 0x03; //opcode
        binary += i.a1.reg << 7;     //rd
        binary += 0b100 << 12;       //funct3
        binary += i.a2.reg << 15;    //rs1
        binary += i.a3.reg << 20;    //rs2
        binary += 0b0100000 << 25;   //funct7
    break;
    ```
    ### Assembler translation - < CLZ >
    ```cpp=
    case CLZ:		// clz rd, rs	
        binary = (0x04 << 2) + 0x03; //opcode
        binary += i.a1.reg << 7;     //rd
        binary += 0b001 << 12;       //funct3
        binary += i.a2.reg << 15;    //rs1
        binary += 0b00000 << 20;     //funct5
        binary += 0b0110000 << 25;   //funct7
    break;
    ```
    ### Assembler translation - < CPOP >
    ```cpp=
    case CPOP: 		// cpop rd, rs
        binary = (0x04 << 2) + 0x03; //opcode
        binary += i.a1.reg << 7;     //rd
        binary += 0b001 << 12;       //funct3
        binary += i.a2.reg << 15;    //rs1
        binary += 0b00010 << 20;     //funct5
        binary += 0b0110000 << 25;   //funct7
    break;
    ```
    ### Assembler translation - < CTZ >
    ```cpp=
    case CTZ: 		// ctz rd, rs
        binary = (0x04 << 2) + 0x03; //opcode
        binary += i.a1.reg << 7;     //rd
        binary += 0b001 << 12;       //funct3
        binary += i.a2.reg << 15;    //rs1
        binary += 0b00001 << 20;     //funct5
        binary += 0b0110000 << 25;   //funct7
    break;
    ```
    ### Assembler translation - < ORC_B >
    ```cpp=
    case ORC_B:		// orc.b rd, rs
        binary = (0x04 << 2) + 0x03; //opcode
        binary += i.a1.reg << 7;     //rd
        binary += 0b101 << 12;       //funct3
        binary += i.a2.reg << 15;    //rs1
        binary += 0b001010000111 << 20; // 0x287
    break;
    ```
    ### Assembler translation - < REV8 >
    ```cpp=
    case REV8:		// rev8 rd, rs
        binary = (0x04 << 2) + 0x03; //opcode
        binary += i.a1.reg << 7;     //rd
        binary += 0b101 << 12;       //funct3
        binary += i.a2.reg << 15;    //rs1
        binary += 0b011010011000 << 20; // 0x698
    break;
    ```
    ### Assembler translation - < SEXT_B >
    ```cpp=
    case SEXT_B:    // sext.b rd, rs
        binary = (0x04 << 2) + 0x03; //opcode
        binary += i.a1.reg << 7;     //rd
        binary += 0b001 << 12;       //funct3
        binary += i.a2.reg << 15;    //rs1
        binary += 0b00100 << 20; 	 //funct5
        binary += 0b0110000 << 25;   //0x30
    break;
    ```
    ### Assembler translation - < SEXT_H >
    ```cpp=
    case SEXT_H:    // sext.h rd, rs
        binary = (0x04 << 2) + 0x03; //opcode
        binary += i.a1.reg << 7;     //rd
        binary += 0b001 << 12;       //funct3
        binary += i.a2.reg << 15;    //rs1
        binary += 0b00101 << 20; 	 //funct5
        binary += 0b0110000 << 25;   //0x30
    break;
    ```
    ### Assembler translation - < ZEXT_H >
    ```cpp=
    case ZEXT_H:    // zext.h rd, rs
        binary = (0x0C << 2) + 0x03; //opcode
        binary += i.a1.reg << 7;     //rd
        binary += 0b100 << 12;       //funct3
        binary += i.a2.reg << 15;    //rs1
        binary += 0b00000 << 20; 	 //funct5
        binary += 0b0000100 << 25;   //0x04
    break;
    ```
    ### Assembler translation - < RORI >
    ```cpp=
    case RORI: 		// rori rd, rs1, shamt
        binary = (0x04 << 2) + 0x03; //opcode
        binary += i.a1.reg << 7;     //rd
        binary += 0b101 << 12;       //funct3
        binary += i.a2.reg << 15;    //rs1
        binary += (i.a3.imm & 0x1F) << 20;    //shamt
        binary += 0b0110000 << 25;   //funct7
    break;
    ```
    ### Assembler translation - < BCLRI >
    ```cpp=
    case BCLRI: 	// bclri rd, rs1, imm
        binary = (0x04 << 2) + 0x03; //opcode
        binary += i.a1.reg << 7;     //rd
        binary += 0b001 << 12;       //funct3
        binary += i.a2.reg << 15;    //rs1
        binary += (i.a3.imm & 0x1F) << 20;    //shamt
        binary += 0b0100100 << 25;   //funct7
    break;
    ```
    ### Assembler translation - < BEXTI >
    ```cpp=
    case BEXTI: 	// bexti rd, rs1, imm
        binary = (0x04 << 2) + 0x03; //opcode
        binary += i.a1.reg << 7;     //rd
        binary += 0b101 << 12;       //funct3
        binary += i.a2.reg << 15;    //rs1
        binary += (i.a3.imm & 0x1F) << 20;    //shamt
        binary += 0b0100100 << 25;   //funct7
    break;
    ```
    ### Assembler translation - < BINVI >
    ```cpp=
    case BINVI:     // binvi rd, rs1, imm
        binary = (0x04 << 2) + 0x03; //opcode
        binary += i.a1.reg << 7;     //rd
        binary += 0b001 << 12;       //funct3
        binary += i.a2.reg << 15;    //rs1
        binary += (i.a3.imm & 0x1F) << 20;    //shamt
        binary += 0b0110100 << 25;   //funct7
    break;
    ```
    ### Assembler translation - < BSETI >
    ```cpp=
    case BSETI:     // bseti rd, rs1, imm
        binary = (0x04 << 2) + 0x03; //opcode
        binary += i.a1.reg << 7;     //rd
        binary += 0b001 << 12;       //funct3
        binary += i.a2.reg << 15;    //rs1
        binary += (i.a3.imm & 0x1F) << 20;    //shamt
        binary += 0b0010100 << 25;   //funct7
    break;
    ```    
    :::

    ### Hardware Module
    > 如果你有新增Hardware module，貼上**程式碼**，附上說明，讓TA明白你是如何完成的。
    ```scala=

    ```
- Option 2 - 有其他分工方式的組別
   - 根據module分工。
     分別負責`E_ALUSel`的編碼過程，與ALU中針對`E_ALUSel`對應功能的實作
   - 李承澔
   `Controller.scala`
   ```scala=
  io.E_ALUSel := MuxLookup(EXE_opcode, (Cat(0.U(7.W), "b11111".U, 0.U(3.W))), Seq(
        OP -> (Cat(EXE_funct7, "b11111".U, EXE_funct3)),
        OP_IMM -> MuxLookup(io.EXE_Inst(31, 25), Cat("b0000000_11111".U, EXE_funct3), Seq(
                    "b0100000".U -> Mux(EXE_funct3==="b101".U, "b0100000_11111_101".U, "b0000000_11111_101".U),  // If SRAI, else SUB
                    "b0110000".U -> MuxLookup(Cat(io.EXE_Inst(24, 20), EXE_funct3), Cat("b0110000_11111".U, EXE_funct3), Seq(
                                      "b00000_001".U -> "b0110000_00000_001".U,   // CLZ
                                      "b00001_001".U -> "b0110000_00001_001".U,   // CTZ
                                      "b00010_001".U -> "b0110000_00010_001".U,   // CPOP
                                      "b00100_001".U -> "b0110000_00100_001".U,   // SEXT_B
                                      "b00101_001".U -> "b0110000_00101_001".U    // SEXT_H
                                    )),
                    "b0010100".U -> Mux(EXE_funct3==="b001".U, "b0010100_11111_001".U, "b0010100_00111_101".U),   // If BSETI, else ORC_B
                    "b0100100".U -> Mux(EXE_funct3==="b001".U ,"b0100100_11111_001".U, "b0100100_11111_101".U),   // If BCLRI, else BEXTI
                    "b0110100".U -> Mux(EXE_funct3==="b001".U, "b0110100_11111_001".U, "b0110100_11000_101".U)    // If BINVI, else REV8
                  )),
        LOAD ->   "b0000000_11111_000".U,  // ADD
        STORE ->  "b0000000_11111_000".U,  // ADD
        BRANCH -> "b0000000_11111_000".U,  // ADD
        JALR ->   "b0000000_11111_000".U,  // ADD
        JAL ->    "b0000000_11111_000".U,  // ADD
        LUI ->    "b0000000_11111_000".U,  // ADD
        AUIPC ->  "b0000000_11111_000".U,  // ADD
  )) // To Be Modified DONE
   ```
   `RiscvDefs.scala`
   ```scala=
   object alu_op_map {
      val ADD = "b0000000_11111_000".U
      val SLL = "b0000000_11111_001".U
      val SLT = "b0000000_11111_010".U
      val SLTU = "b0000000_11111_011".U
      val XOR = "b0000000_11111_100".U
      val SRL = "b0000000_11111_101".U
      val OR = "b0000000_11111_110".U
      val AND = "b0000000_11111_111".U
      val SUB = "b0100000_11111_000".U
      val SRA = "b0100000_11111_101".U

      // Extended Instructions
      val CLZ = "b0110000_00000_001".U
      val CTZ = "b0110000_00001_001".U
      val CPOP = "b0110000_00010_001".U
      val ANDN = "b0100000_11111_111".U
      val ORN = "b0100000_11111_110".U
      val XNOR = "b0100000_11111_100".U
      val MIN = "b0000101_11111_100".U
      val MAX = "b0000101_11111_110".U
      val MINU = "b0000101_11111_101".U
      val MAXU = "b0000101_11111_111".U
      val SEXT_B = "b0110000_00100_001".U
      val SEXT_H = "b0110000_00101_001".U
      val BSET = "b0010100_11111_001".U
      val BCLR = "b0100100_11111_001".U
      val BINV = "b0110100_11111_001".U
      val BEXT = "b0100100_11111_101".U
      // val BSETI = "b0010100_11111_001".U
      // val BCLRI = "b0100100_11111_001".U
      // val BINVI = "b0110100_11111_001".U
      // val BEXTI = "b0100100_11111_101".U
      val ROR = "b0110000_11111_101".U
      val ROL = "b0110000_11111_001".U
      // val RORI = "b0110000_11111_101".U
      val SH1ADD = "b0010000_11111_010".U
      val SH2ADD = "b0010000_11111_100".U
      val SH3ADD = "b0010000_11111_110".U
      val REV8 = "b0110100_11000_101".U
      val ZEXT_H = "b0000100_11111_100".U
      val ORC_B = "b0010100_00111_101".U
    }
   ```
   
   - 吳明真
   `ALU.scala`
   ```scala=
   class ALU extends Module{
      val io = IO(new ALUIO)

      io.out := 0.U
      switch(io.ALUSel){
            is(ADD ){io.out := io.src1+io.src2}
            is(SLL ){io.out := io.src1 << io.src2(4,0)}
            is(SLT ){io.out := Mux(io.src1.asSInt < io.src2.asSInt,1.U,0.U)}
            is(SLTU){io.out := Mux(io.src1 < io.src2              ,1.U,0.U)}
            is(XOR ){io.out := io.src1^io.src2}
            is(SRL ){io.out := io.src1 >> io.src2(4,0)}
            is(OR  ){io.out := io.src1|io.src2}
            is(AND ){io.out := io.src1&io.src2}
            is(SUB ){io.out := io.src1-io.src2}
            is(SRA ){io.out := (io.src1.asSInt >> io.src2(4,0)).asUInt}

            // Extended Instructions
            is(CLZ   ){io.out := PriorityEncoder(Reverse(io.src1))}
            is(CTZ   ){io.out := PriorityEncoder(io.src1)}
            is(CPOP  ){io.out := PopCount(io.src1)}
            is(ANDN  ){io.out := io.src1 & ~io.src2}
            is(ORN   ){io.out := io.src1 | ~io.src2}
            is(XNOR  ){io.out := ~(io.src1 ^ io.src2)}
            is(MIN   ){io.out := Mux(io.src1.asSInt>io.src2.asSInt, io.src2, io.src1)}
            is(MAX   ){io.out := Mux(io.src1.asSInt>io.src2.asSInt, io.src1, io.src2)}
            is(MINU  ){io.out := Mux(io.src1.asUInt>io.src2.asUInt, io.src2, io.src1)}
            is(MAXU  ){io.out := Mux(io.src1.asUInt>io.src2.asUInt, io.src1, io.src2)}
            is(SEXT_B){io.out := io.src1(7, 0).asSInt.asUInt}
            is(SEXT_H){io.out := io.src1(15, 0).asSInt.asUInt}
            is(BSET  ){io.out := io.src1 | (1.U << io.src2(4, 0))}
            is(BCLR  ){io.out := io.src1 & ~(1.U << io.src2(4, 0))}
            is(BINV  ){io.out := io.src1 ^ (1.U << io.src2(4, 0))}
            is(BEXT  ){io.out := (io.src1 >> io.src2(4, 0)) & 1.U}
            // is(BSETI ){io.out := io.src1 | (1.U << io.src2(4, 0))}
            // is(BCLRI ){io.out := io.src1 & ~(1.U << io.src2(4, 0))}
            // is(BINVI ){io.out := io.src1 ^ (1.U << io.src2(4, 0))}
            // is(BEXTI ){io.out := (io.src1 >> io.src2(4, 0)) & 1.U}
            is(ROR   ){io.out := (io.src1 >> io.src2(4, 0)) | (io.src1 << (32.U-io.src2(4, 0)))}
            is(ROL   ){io.out := (io.src1 << io.src2(4, 0)) | (io.src1 >> (32.U-io.src2(4, 0)))}
            // is(RORI  ){io.out := (io.src1 >> io.src2(4, 0)) | (io.src2 << (32.U-io.src2(4, 0)))}
            is(SH1ADD){io.out := (io.src1 << 1.U) + io.src2}
            is(SH2ADD){io.out := (io.src1 << 2.U) + io.src2}
            is(SH3ADD){io.out := (io.src1 << 3.U) + io.src2}
            is(REV8  ){io.out := Cat((0 until 4).map(i => io.src1((4-i)*8-1, (4-i-1)*8)).reverse)}
            is(ZEXT_H){io.out := io.src1(15, 0)}
            is(ORC_B ){io.out := Cat((0 until 4).map{i =>
                                    val byte = io.src1((i + 1) * 8 - 1, i * 8) // Extract each byte
                                    Mux(byte.orR, Fill(8, 1.U), 0.U(8.W))   // Check if any bit is set in the byte
                                  }.reverse)}
        // ==============================
  }
   ```

#### 測試結果
- 測試檔案
    - ``lab08/emulator/example_code/hw8_3.asm``
- 測試結果
    - ![](https://course.playlab.tw/md/uploads/5f6870b4-18fa-46f4-bf9b-484bf271d954.png)


#### 小組最後完成CPU架構圖
- ![](https://course.playlab.tw/md/uploads/27c6d310-158e-4195-9d4a-c6b1db1c1bd8.jpg)




## Bonus 

- If you have done any bonus, you may paste your results in this section

:::info 
- Bonus: data forwarding
also use **rv32ui_SingleTest/TestDataHazard.S** to test the Hardware.
:::
:::warning
Paste your gitlab branch URL link
:::

:::info 
- Bonus: Kadane's algorithm
:::
:::warning
Paste your gitlab branch URL link
:::

:::info 
- Bonus: Merge sort
:::
:::warning
Paste your gitlab branch URL link
:::


