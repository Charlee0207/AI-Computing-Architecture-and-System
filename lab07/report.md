(NTHU_113062559_李承澔) ACAL 2024 Fall Lab 7 HW Submission Template
===

###### tags: `AIAS Fall 2024`, `AIAS Spring 2024`, `Submission Template`


## Gitlab code link
:::info
Please paste the link to your private Gitlab repository for this homework submission here. 
:::

- [Gitlab link](https://course.playlab.tw/git/chngh0207/lab07) - 

## Hw 7-1 - RISC-V M-Standard Extension
### C code - MUL,MULHU,REM,REMU
> 請參考 Lab6-1和下方範例, **將新增的 code**放在下方並加上註解, 讓 TA明白你是如何完成的。
```cpp=
// C code you add & comment
// Please DON'T copy all your code, just copy the part you add

// Line 48
typedef enum {
	UNIMPL = 0,

	//instruction added
    MUL,
	MULHU,
	REM,
	REMU
    //*****************
} instr_type;

//line 99
instr_type parse_instr(char* tok) {
	//instruction added
    if ( streq(tok , "mul")) return MUL;
    if ( streq(tok , "mulhu")) return MULHU;
    if ( streq(tok , "rem")) return REM;
    if ( streq(tok , "remu")) return REMU;
    //*****************
}

//line 530
switch( op ) {
    case UNIMPL: return 1;

    //instruction added
    case MUL:
        if ( !o1 || !o2 || !o3 || o4 ) print_syntax_error( line,  "Invalid format" );
            i->a1.reg = parse_reg(o1 , line);
            i->a2.reg = parse_reg(o2 , line);
            i->a3.reg = parse_reg(o3 , line);
        return 1;
    case MULHU:
        if ( !o1 || !o2 || !o3 || o4 ) print_syntax_error( line,  "Invalid format" );
            i->a1.reg = parse_reg(o1 , line);
            i->a2.reg = parse_reg(o2 , line);
            i->a3.reg = parse_reg(o3 , line);
        return 1;
    case REM:
        if ( !o1 || !o2 || !o3 || o4 ) print_syntax_error( line,  "Invalid format" );
            i->a1.reg = parse_reg(o1 , line);
            i->a2.reg = parse_reg(o2 , line);
            i->a3.reg = parse_reg(o3 , line);
        return 1;
    case REMU:
        if ( !o1 || !o2 || !o3 || o4 ) print_syntax_error( line,  "Invalid format" );
            i->a1.reg = parse_reg(o1 , line);
            i->a2.reg = parse_reg(o2 , line);
            i->a3.reg = parse_reg(o3 , line);
        return 1;
    //****************
}

//line 797
switch (i.op) {

    //instruction added
    case MUL: rf[i.a1.reg] = (rf[i.a2.reg] * rf[i.a3.reg]); 
    break;
    case MULHU: 
        rf[i.a1.reg] = (((uint64_t)rf[i.a2.reg] * (uint64_t)rf[i.a3.reg]) >> 32); 
    break;
    case MULHSU: 
        rf[i.a1.reg] = (((int64_t)rf[i.a2.reg] * (uint64_t)rf[i.a3.reg]) >> 32); 
    break;
    case REM: 
        if(rf[i.a3.reg]==1 || rf[i.a3.reg]==-1){
            rf[i.a1.reg] = 0;
        }
        else if(rf[i.a3.reg]==0){
            printf("Floating point exception (divided by zero)\n");
            printf("To behave as same as Venus, simply assign rs1 to rd\n");
            rf[i.a1.reg] = rf[i.a2.reg];
        }
        else{
            rf[i.a1.reg] = ((int32_t)rf[i.a2.reg] % (int32_t)rf[i.a3.reg]); 
        }
    break;
    case REMU: 
        if(rf[i.a3.reg]==0){
            printf("Floating point exception (divided by zero)\n");
            printf("To behave as same as Venus, simply assign rs1 to rd\n");
            rf[i.a1.reg] = rf[i.a2.reg];
        }
        else{
            rf[i.a1.reg] = ((uint32_t)rf[i.a2.reg] % (uint32_t)rf[i.a3.reg]); 
        }
    break;
    //*****************
}

```
#### Simulation Result & Assembly Code
> 1. 請放上你用來驗證 instruction的 assembly code, 並加上預期結果的註解。
> 2. 使用 RV32 Emulator模擬, 驗證程式碼的正確性。
> 3. 用以驗證的 assembly code可以有不只一組, 也可以只有一組, 確保 function正確就好。
- Assembly code to test MUL function

The testcases for MUL, MULHU, REM, REMU are all the same, and I check the correctness by comparing to Venus.  
```mipsasm=
## RV32 Emulator Testing Assembly Code for MUL function

## 0x00000001
addi t0, x0, 0

## 0xFFFFFFFF
addi t1, x0, -1

## 0x80000000 (-2147483648) 
addi t2, x0, 1  
slli t2, t2, 31 

## 0x7FFFFFFF (2147483647)     
addi t3, x0, -1      
srli t3, t3, 1   

mul t4, t0, t0
mul t4, t0, t1
mul t4, t0, t2
mul t4, t0, t3

mul t4, t1, t0
mul t4, t1, t1
mul t4, t1, t2
mul t4, t1, t3

mul t4, t2, t0
mul t4, t2, t1
mul t4, t2, t2
mul t4, t2, t3

mul t4, t3, t0
mul t4, t3, t1
mul t4, t3, t2
mul t4, t3, t3

hcf

```
- Simulation result
![](https://course.playlab.tw/md/uploads/7761d6bc-cbf1-4ad7-9a5b-a62a1947046c.png)






## HW7-2 - RISC-V Bit Manipulation Extension
### Gitlab code link (Your own branch)
:::info
Please paste the link to your branch of group Gitlab repository for this homework submission here. 
:::

- [Gitlab link of your branch](https://course.playlab.tw/git/board2/lab07-group/-/tree/chngh0207?ref_type=heads) - 

:::info
Please paste the link to your group project repository for this homework submission here. 
:::
- [Gitlab link of your group project repo](https://course.playlab.tw/git/board2/lab07-group) - 

:::info
Please add a project.md file in your group repo. In project.bd please write down the list of your member & distributions of each member.
:::



### C Code - asm
> 請參考 Lab7-1和範例, **將新增的 code**放在下方並加上註解, 讓 TA明白你是如何完成的, 如果超過一個指令請依照 HW7-1的方式新增作業說明。
```cpp=

// line 48
typedef enum {
    UNIMPL = 0,

    //instruction added
    //MUL,
    BINV,         // binv rd, rs1, rs2
    BINVI,        // binvi rd, rs1, imm
    BSET,         // bset rd, rs1, rs2
    BSETI,        // bseti rd, rs1, imm
    SEXT_B,       // sext.b rd, rs
    SEXT_H,       // sext.h rd, rs
    SH1ADD,       // sh1add rd, rs1, rs2
    SH2ADD,       // sh2add rd, rs1, rs2
    SH3ADD,       // sh3add rd, rs1, rs2
    XNOR,         // xnor rd, rs1, rs2
    ZEXT_H        // zext.h rd, rs
    //*****************
}

// line 106
instr_type parse_instr(char* tok) {
	//instruction added
    // if ( streq(tok , "mul")) return MUL;
    if (streq(tok, "binv")) return BINV;
	if (streq(tok, "binvi")) return BINVI;
	if (streq(tok, "bset")) return BSET;
	if (streq(tok, "bseti")) return BSETI;
	if (streq(tok, "sext.b")) return SEXT_B;
	if (streq(tok, "sext.h")) return SEXT_H;
	if (streq(tok, "sh1add")) return SH1ADD;
	if (streq(tok, "sh2add")) return SH2ADD;
	if (streq(tok, "sh3add")) return SH3ADD;
	if (streq(tok, "xnor")) return XNOR;
	if (streq(tok, "zext.h")) return ZEXT_H;
    //*****************
}

// line 544
switch( op ) {
    case UNIMPL: return 1;

    //instruction added
    // case MUL:
    //     if ( !o1 || !o2 || !o3 || o4 ) print_syntax_error( line,  "Invalid format" );
    // 	    i->a1.reg = parse_reg(o1 , line);
    // 	    i->a2.reg = parse_reg(o2 , line);
    // 	    i->a3.reg = parse_reg(o3 , line);
    //     return 1;

    case BINV:		// binv rd, rs1, rs2
    case BSET:		// bset rd, rs1, rs2
    case SH1ADD:	// sh1add rd, rs1, rs2
    case SH2ADD:	// sh2add rd, rs1, rs2
    case SH3ADD:	// sh3add rd, rs1, rs2
    case XNOR:		// xnor rd, rs1, rs2
        if (!o1 || !o2 || !o3 || o4) print_syntax_error(line, "Invalid format");
        i->a1.reg = parse_reg(o1, line);
        i->a2.reg = parse_reg(o2, line);
        i->a3.reg = parse_reg(o3, line);
        return 1;

    case BINVI: 	// binvi rd, rs1, imm
    case BSETI: 	// bseti rd, rs1, imm
        if (!o1 || !o2 || !o3 || o4) print_syntax_error(line, "Invalid format");
        i->a1.reg = parse_reg(o1, line);
        i->a2.reg = parse_reg(o2, line);
        i->a3.imm = parse_imm(o3, 5, line);
        return 1;

    case SEXT_B:	// sext.b rd, rs
    case SEXT_H:	// sext.h rd, rs
    case ZEXT_H:	// zext.h rd, rs
        if (!o1 || !o2 || o3 || o4) print_syntax_error(line, "Invalid format");
        i->a1.reg = parse_reg(o1, line);
        i->a2.reg = parse_reg(o2, line);
        return 1; 
    //****************
}

// line 815
switch (i.op) {

    //instruction added
    //case MUL: rf[i.a1.reg] = rf[i.a2.reg] * rf[i.a3.reg]; break;

    // let index = X(rs2) & (XLEN - 1);
    // X(rd) = X(rs1) ^ (1 << index)
    case BINV: rf[i.a1.reg] = rf[i.a2.reg] ^ (1 << (rf[i.a3.reg]&0x1F)); break;

    // let index = shamt & (XLEN - 1);
    // X(rd) = X(rs1) ^ (1 << index)
    case BINVI: rf[i.a1.reg] = rf[i.a2.reg] ^ (1 << (i.a3.imm&0x1F)); break;

    // let index = X(rs2) & (XLEN - 1);
    // X(rd) = X(rs1) | (1 << index)
    case BSET: rf[i.a1.reg] = rf[i.a2.reg] | (1 << (rf[i.a3.reg]&0x1F)); break;

    // let index = shamt & (XLEN - 1);
    // X(rd) = X(rs1) | (1 << index)
    case BSETI: rf[i.a1.reg] = rf[i.a2.reg] | (1 << (i.a3.imm&0x1F)); break;

    // X(rd) = SignExtend(X(rs)[7:0]);
    case SEXT_B: rf[i.a1.reg] = (int8_t)(rf[i.a2.reg] & 0xFF); break;

    // X(rd) = SignExtend(X(rs)[15:0]);
    case SEXT_H: rf[i.a1.reg] = (int16_t)(rf[i.a2.reg] & 0xFFFF); break;

    // X(rd) = X(rs2) + (X(rs1) << 1);
    case SH1ADD: rf[i.a1.reg] = (rf[i.a2.reg] << 1) + rf[i.a3.reg]; break;

    // X(rd) = X(rs2) + (X(rs1) << 2);
    case SH2ADD: rf[i.a1.reg] = (rf[i.a2.reg] << 2) + rf[i.a3.reg]; break;

    // X(rd) = X(rs2) + (X(rs1) << 3);
    case SH3ADD: rf[i.a1.reg] = (rf[i.a2.reg] << 3) + rf[i.a3.reg]; break;

    // X(rd) = ~(X(rs1) ^ X(rs2));
    case XNOR: rf[i.a1.reg] = ~(rf[i.a2.reg] ^ rf[i.a3.reg]); break;

    // X(rd) = ZeroExtend(X(rs)[15:0]);
    case ZEXT_H: rf[i.a1.reg] = rf[i.a2.reg] & 0xFFFF;  break;
    //*****************
}

```

#### Simulation Result & Assembly Code
- Assembly code to test instruction you picked
```mipsasm=
## RV32 Emulator Testing Assembly Code for BINV function

main:

##rs1:[1804289383] rs2:[846930886] rd:[1804289319]
lui x28, 440500
addi x28, x28, 1383
lui x29, 206770
addi x29, x29, 966
lui x30, 440500
addi x30, x30, 1319

binv x5, x28, x29

addi x7, x7, 1
bne x5, x30, QUIT



##rs1:[1681692777] rs2:[1714636915] rd:[1681168489]
lui x28, 410570
addi x28, x28, 2153
lui x29, 418613
addi x29, x29, 2163
lui x30, 410442
addi x30, x30, 2153

binv x5, x28, x29

addi x7, x7, 1
bne x5, x30, QUIT

.
.
.

##rs1:[1789366143] rs2:[1987231011] rd:[1789366135]
lui x28, 436857
addi x28, x28, 3967
lui x29, 485164
addi x29, x29, 3363
lui x30, 436857
addi x30, x30, 3959

binv x5, x28, x29

addi x7, x7, 1
bne x5, x30, QUIT



##rs1:[1875335928] rs2:[1784639529] rd:[1875335416]
lui x28, 457846
addi x28, x28, 2808
lui x29, 435703
addi x29, x29, 41
lui x30, 457846
addi x30, x30, 2296

binv x5, x28, x29

addi x7, x7, 1
bne x5, x30, QUIT




QUIT:
hcf
```

- Simulation result
> 請放上模擬結果的螢幕截圖
![](https://course.playlab.tw/md/uploads/45fb45e7-1531-4922-8364-000c984e3fec.png)


## Bonus
> Bonus請依照 lab6 document中的 bonus template進行繳交。

