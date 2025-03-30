#include <stdio.h>
#include <stdlib.h>
#include <stdint.h>
#include <string.h>
#include <ctype.h>
#include <errno.h>

#ifndef EMULATOR_H__
#define EMULATOR_H__

// 64 KB
#define MEM_BYTES 0x10000
#define TEXT_OFFSET 0
#define DATA_OFFSET 32768

#define MAX_LABEL_COUNT 128
#define MAX_LABEL_LEN 32
#define MAX_SRC_LEN (1024*1024)

#define VECTOR_LEN 512
#define ELEMENT_WIDTH 8
#define VLMAX VECTOR_LEN/ELEMENT_WIDTH

typedef struct {
	char* src;
	int offset;
} source;

/* Vector Struct for more than 64 bit */
typedef struct {
	uint8_t bytes[VLMAX];
} vector_reg;

typedef enum {
	UNIMPL = 0,

	//instruction added
	MUL,
    VLE8_V,
    VSE8_V,
    VADD_VV,
    VMUL_VX,	
	
	ANDN,		// andn rd, rs1, rs2	
	CLMUL,		// clmul rd, rs1, rs2
	CLMULH,		// clmulh rd, rs1, rs2	
	CLMULR,		// clmulr rd, rs1, rs2
	CLZ,		// clz rd, rs	
	CPOP, 		// cpop rd, rs
	CTZ, 		// ctz rd, rs
	MAX,		// max rd, rs1, rs2
	MAXU, 		// maxu rd, rs1, rs2	
	MIN, 		// min rd, rs1, rs2
	MINU,		// minu rd, rs1, rs2
	ORC_B,		// orc.b rd, rs
	ORN,		// orn rd, rs1, rs2
	REV8,		// rev8 rd, rs
	ROL, 		// rol rd, rs1, rs2
	ROR, 		// ror rd, rs1, rs2
	RORI, 		// rori rd, rs1, shamt
	BCLR, 		// bclr rd, rs1, rs2
	BCLRI, 		// bclri rd, rs1, imm
	BEXT, 		// bext rd, rs1, rs2
	BEXTI, 		// bexti rd, rs1, imm
	BINV,       // binv rd, rs1, rs2
    BINVI,      // binvi rd, rs1, imm
    BSET,       // bset rd, rs1, rs2
    BSETI,      // bseti rd, rs1, imm
    SEXT_B,     // sext.b rd, rs
    SEXT_H,     // sext.h rd, rs
    SH1ADD,     // sh1add rd, rs1, rs2
    SH2ADD,     // sh2add rd, rs1, rs2
    SH3ADD,     // sh3add rd, rs1, rs2
    XNOR,       // xnor rd, rs1, rs2
    ZEXT_H,     // zext.h rd, rs
    //*****************

	ADD,
	ADDI,
	AND,
	ANDI,
	AUIPC,
	BEQ,
	BGE,
	BGEU,
	BLT,
	BLTU,
	BNE,
	JAL,
	JALR,
	LB,
	LBU,
	LH,
	LHU,
	LUI,
	LW,
	OR,
	ORI,
	SB,
	SH,
	SLL,
	SLLI,
	SLT,
	SLTI,
	SLTIU,
	SLTU,
	SRA,
	SRAI,
	SRL,
	SRLI,
	SUB,
	SW,
	XOR,
	XORI,
	HCF
} instr_type;

typedef enum {
	OPTYPE_NONE, // more like "don't care"
	OPTYPE_REG,
	OPTYPE_IMM,
	OPTYPE_LABEL,
} operand_type;
typedef struct {
	operand_type type = OPTYPE_NONE;
	char label[MAX_LABEL_LEN];
	int reg;
	uint32_t imm;

} operand;
typedef struct {
	instr_type op;
	operand a1;
	operand a2;
	operand a3;
	char* psrc = NULL;
	int orig_line=-1;
	bool breakpoint = false;
} instr;

typedef struct {
	char label[MAX_LABEL_LEN];
	int loc = -1;
} label_loc;

uint32_t mem_read(uint8_t*, uint32_t, instr_type);

#endif