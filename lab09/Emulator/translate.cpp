#include <stdio.h>
#include <stdlib.h>
#include <stdint.h>
#include <string.h>
#include <ctype.h>
#include <errno.h>
#include "translate.h"

char* concat(const char *s1, const char *s2){
    const size_t len1 = strlen(s1);
    const size_t len2 = strlen(s2);
    char *result = (char*)malloc(len1 + len2 + 1); // +1 for the null-terminator
    // in real code you would check for errors in malloc here
    memcpy(result, s1, len1);
    memcpy(result + len1, s2, len2 + 1); // +1 to copy the null-terminator
    return result;
}

void copy_str(char *tgt, const char *src) {
	char *ptr;
	ptr = strncpy(tgt, src, strlen(src));
	ptr[strlen(src)] = '\0';
}

void copy_path(char* argv1, char** path){
    char *ptr;

	if(!(*path = (char*)malloc(sizeof(char)*strlen(argv1)))){
		printf("malloc is failed in 'main', copy the argv[1] to char *path.\n");
		exit(2);
	}
        
	copy_str(*path,argv1);

	ptr = *path + strlen(*path);
    
	//find the last '/' in argv1
	while(ptr != *path){
		if(*ptr == '/'){
			*ptr = '\0';
			break;
		}
		ptr--;
	}

    if(ptr == *path)
		**path = '\0';
	
    if(**path)
		strcat(*path,"/");
}

void write_data_hex(uint8_t* mem,FILE* data_file){
    uint8_t ret = 0;

    for(int i = 0; i < (1<<10); i++){  //1kB for DataMem
		ret = (uint8_t) mem_read(mem, i + DATA_OFFSET, LBU);
		fprintf(data_file,"%02x\n",ret);
	}
}

void translate_to_machine_code(uint8_t* mem,instr* imem, char* argv1){
    uint32_t inst_cnt = 0;
	bool dexit = false;

	char* path;
	copy_path(argv1, &path);

	FILE *mch_file = fopen(concat(path,"inst.hex"), "w");
	FILE *inst_file = fopen(concat(path,"inst.asm"), "w");
	FILE *data_file = fopen(concat(path,"data.hex"), "w");

	while(!dexit){
		instr i = imem[inst_cnt];
		uint32_t binary = 0;
		int offset = 0;

        //follow the ISA and combine the fragment information in binary form
		switch(i.op){

			case ANDN:		// andn rd, rs1, rs2	
				binary = (0x0C << 2) + 0x03; //opcode
				binary += i.a1.reg << 7;     //rd
				binary += 0b111 << 12;       //funct3
				binary += i.a2.reg << 15;    //rs1
				binary += i.a3.reg << 20;    //rs2
				binary += 0b0100000 << 25;   //funct7
			break;
			case CLMUL:		// clmul rd, rs1, rs2
				binary = (0x0C << 2) + 0x03; //opcode
				binary += i.a1.reg << 7;     //rd
				binary += 0b001 << 12;       //funct3
				binary += i.a2.reg << 15;    //rs1
				binary += i.a3.reg << 20;    //rs2
				binary += 0b0000101 << 25;   //funct7
			break;
			case CLMULH:	// clmulh rd, rs1, rs2	
				binary = (0x0C << 2) + 0x03; //opcode
				binary += i.a1.reg << 7;     //rd
				binary += 0b011 << 12;       //funct3
				binary += i.a2.reg << 15;    //rs1
				binary += i.a3.reg << 20;    //rs2
				binary += 0b0000101 << 25;   //funct7
			break;
			case CLMULR:	// clmulr rd, rs1, rs2
				binary = (0x0C << 2) + 0x03; //opcode
				binary += i.a1.reg << 7;     //rd
				binary += 0b010 << 12;       //funct3
				binary += i.a2.reg << 15;    //rs1
				binary += i.a3.reg << 20;    //rs2
				binary += 0b0000101 << 25;   //funct7
			break;
			case MAX:		// max rd, rs1, rs2
				binary = (0x0C << 2) + 0x03; //opcode
				binary += i.a1.reg << 7;     //rd
				binary += 0b110 << 12;       //funct3
				binary += i.a2.reg << 15;    //rs1
				binary += i.a3.reg << 20;    //rs2
				binary += 0b0000101 << 25;   //funct7
			break;
			case MAXU: 		// maxu rd, rs1, rs2
				binary = (0x0C << 2) + 0x03; //opcode
				binary += i.a1.reg << 7;     //rd
				binary += 0b111 << 12;       //funct3
				binary += i.a2.reg << 15;    //rs1
				binary += i.a3.reg << 20;    //rs2
				binary += 0b0000101 << 25;   //funct7	
			break;
			case MIN: 		// min rd, rs1, rs2
				binary = (0x0C << 2) + 0x03; //opcode
				binary += i.a1.reg << 7;     //rd
				binary += 0b100 << 12;       //funct3
				binary += i.a2.reg << 15;    //rs1
				binary += i.a3.reg << 20;    //rs2
				binary += 0b0000101 << 25;   //funct7
			break;
			case MINU:		// minu rd, rs1, rs2
				binary = (0x0C << 2) + 0x03; //opcode
				binary += i.a1.reg << 7;     //rd
				binary += 0b101 << 12;       //funct3
				binary += i.a2.reg << 15;    //rs1
				binary += i.a3.reg << 20;    //rs2
				binary += 0b0000101 << 25;   //funct7
			break;
			case ORN:		// orn rd, rs1, rs2
				binary = (0x0C << 2) + 0x03; //opcode
				binary += i.a1.reg << 7;     //rd
				binary += 0b110 << 12;       //funct3
				binary += i.a2.reg << 15;    //rs1
				binary += i.a3.reg << 20;    //rs2
				binary += 0b0100000 << 25;   //funct7
			break;
			case ROL: 		// rol rd, rs1, rs2
				binary = (0x0C << 2) + 0x03; //opcode
				binary += i.a1.reg << 7;     //rd
				binary += 0b001 << 12;       //funct3
				binary += i.a2.reg << 15;    //rs1
				binary += i.a3.reg << 20;    //rs2
				binary += 0b0110000 << 25;   //funct7
			break;
			case ROR: 		// ror rd, rs1, rs2
				binary = (0x0C << 2) + 0x03; //opcode
				binary += i.a1.reg << 7;     //rd
				binary += 0b101 << 12;       //funct3
				binary += i.a2.reg << 15;    //rs1
				binary += i.a3.reg << 20;    //rs2
				binary += 0b0110000 << 25;   //funct7
			break;
			case BCLR: 		// bclr rd, rs1, rs2
				binary = (0x0C << 2) + 0x03; //opcode
				binary += i.a1.reg << 7;     //rd
				binary += 0b001 << 12;       //funct3
				binary += i.a2.reg << 15;    //rs1
				binary += i.a3.reg << 20;    //rs2
				binary += 0b0100100 << 25;   //funct7
			break;
			case BEXT: 		// bext rd, rs1, rs2
				binary = (0x0C << 2) + 0x03; //opcode
				binary += i.a1.reg << 7;     //rd
				binary += 0b101 << 12;       //funct3
				binary += i.a2.reg << 15;    //rs1
				binary += i.a3.reg << 20;    //rs2
				binary += 0b0100100 << 25;   //funct7
			break;
			case BINV:      // binv rd, rs1, rs2
				binary = (0x0C << 2) + 0x03; //opcode
				binary += i.a1.reg << 7;     //rd
				binary += 0b001 << 12;       //funct3
				binary += i.a2.reg << 15;    //rs1
				binary += i.a3.reg << 20;    //rs2
				binary += 0b0110100 << 25;   //funct7
			break;
			case BSET:      // bset rd, rs1, rs2
				binary = (0x0C << 2) + 0x03; //opcode
				binary += i.a1.reg << 7;     //rd
				binary += 0b001 << 12;       //funct3
				binary += i.a2.reg << 15;    //rs1
				binary += i.a3.reg << 20;    //rs2
				binary += 0b0010100 << 25;   //funct7
			break;
			case SH1ADD:    // sh1add rd, rs1, rs2
				binary = (0x0C << 2) + 0x03; //opcode
				binary += i.a1.reg << 7;     //rd
				binary += 0b010 << 12;       //funct3
				binary += i.a2.reg << 15;    //rs1
				binary += i.a3.reg << 20;    //rs2
				binary += 0b0010000 << 25;   //funct7
			break;
			case SH2ADD:    // sh2add rd, rs1, rs2
				binary = (0x0C << 2) + 0x03; //opcode
				binary += i.a1.reg << 7;     //rd
				binary += 0b100 << 12;       //funct3
				binary += i.a2.reg << 15;    //rs1
				binary += i.a3.reg << 20;    //rs2
				binary += 0b0010000 << 25;   //funct7
			break;
			case SH3ADD:    // sh3add rd, rs1, rs2
				binary = (0x0C << 2) + 0x03; //opcode
				binary += i.a1.reg << 7;     //rd
				binary += 0b110 << 12;       //funct3
				binary += i.a2.reg << 15;    //rs1
				binary += i.a3.reg << 20;    //rs2
				binary += 0b0010000 << 25;   //funct7
			break;
			case XNOR:      // xnor rd, rs1, rs2
				binary = (0x0C << 2) + 0x03; //opcode
				binary += i.a1.reg << 7;     //rd
				binary += 0b100 << 12;       //funct3
				binary += i.a2.reg << 15;    //rs1
				binary += i.a3.reg << 20;    //rs2
				binary += 0b0100000 << 25;   //funct7
			break;

			case CLZ:		// clz rd, rs	
				binary = (0x04 << 2) + 0x03; //opcode
				binary += i.a1.reg << 7;     //rd
				binary += 0b001 << 12;       //funct3
				binary += i.a2.reg << 15;    //rs1
				binary += 0b00000 << 20;     //funct5
				binary += 0b0110000 << 25;   //funct7
			break;
			case CPOP: 		// cpop rd, rs
				binary = (0x04 << 2) + 0x03; //opcode
				binary += i.a1.reg << 7;     //rd
				binary += 0b001 << 12;       //funct3
				binary += i.a2.reg << 15;    //rs1
				binary += 0b00010 << 20;     //funct5
				binary += 0b0110000 << 25;   //funct7
			break;
			case CTZ: 		// ctz rd, rs
				binary = (0x04 << 2) + 0x03; //opcode
				binary += i.a1.reg << 7;     //rd
				binary += 0b001 << 12;       //funct3
				binary += i.a2.reg << 15;    //rs1
				binary += 0b00001 << 20;     //funct5
				binary += 0b0110000 << 25;   //funct7
			break;
			case ORC_B:		// orc.b rd, rs
				binary = (0x04 << 2) + 0x03; //opcode
				binary += i.a1.reg << 7;     //rd
				binary += 0b101 << 12;       //funct3
				binary += i.a2.reg << 15;    //rs1
				binary += 0b001010000111 << 20; // 0x287
			break;
			case REV8:		// rev8 rd, rs
				binary = (0x04 << 2) + 0x03; //opcode
				binary += i.a1.reg << 7;     //rd
				binary += 0b101 << 12;       //funct3
				binary += i.a2.reg << 15;    //rs1
				binary += 0b011010011000 << 20; // 0x698
			break;
			case SEXT_B:    // sext.b rd, rs
				binary = (0x04 << 2) + 0x03; //opcode
				binary += i.a1.reg << 7;     //rd
				binary += 0b001 << 12;       //funct3
				binary += i.a2.reg << 15;    //rs1
				binary += 0b00100 << 20; 	 //funct5
				binary += 0b0110000 << 25;   //0x30
			break;
			case SEXT_H:    // sext.h rd, rs
				binary = (0x04 << 2) + 0x03; //opcode
				binary += i.a1.reg << 7;     //rd
				binary += 0b001 << 12;       //funct3
				binary += i.a2.reg << 15;    //rs1
				binary += 0b00101 << 20; 	 //funct5
				binary += 0b0110000 << 25;   //0x30
			break;
			case ZEXT_H:    // zext.h rd, rs
				binary = (0x0C << 2) + 0x03; //opcode
				binary += i.a1.reg << 7;     //rd
				binary += 0b100 << 12;       //funct3
				binary += i.a2.reg << 15;    //rs1
				binary += 0b00000 << 20; 	 //funct5
				binary += 0b0000100 << 25;   //0x04
			break;

			case RORI: 		// rori rd, rs1, shamt
				binary = (0x04 << 2) + 0x03; //opcode
				binary += i.a1.reg << 7;     //rd
				binary += 0b101 << 12;       //funct3
				binary += i.a2.reg << 15;    //rs1
				binary += (i.a3.imm & 0x1F) << 20;    //shamt
				binary += 0b0110000 << 25;   //funct7
			break;
			case BCLRI: 	// bclri rd, rs1, imm
				binary = (0x04 << 2) + 0x03; //opcode
				binary += i.a1.reg << 7;     //rd
				binary += 0b001 << 12;       //funct3
				binary += i.a2.reg << 15;    //rs1
				binary += (i.a3.imm & 0x1F) << 20;    //shamt
				binary += 0b0100100 << 25;   //funct7
			break;
			case BEXTI: 	// bexti rd, rs1, imm
				binary = (0x04 << 2) + 0x03; //opcode
				binary += i.a1.reg << 7;     //rd
				binary += 0b101 << 12;       //funct3
				binary += i.a2.reg << 15;    //rs1
				binary += (i.a3.imm & 0x1F) << 20;    //shamt
				binary += 0b0100100 << 25;   //funct7
			break;
			case BINVI:     // binvi rd, rs1, imm
				binary = (0x04 << 2) + 0x03; //opcode
				binary += i.a1.reg << 7;     //rd
				binary += 0b001 << 12;       //funct3
				binary += i.a2.reg << 15;    //rs1
				binary += (i.a3.imm & 0x1F) << 20;    //shamt
				binary += 0b0110100 << 25;   //funct7
			break;
			case BSETI:     // bseti rd, rs1, imm
				binary = (0x04 << 2) + 0x03; //opcode
				binary += i.a1.reg << 7;     //rd
				binary += 0b001 << 12;       //funct3
				binary += i.a2.reg << 15;    //rs1
				binary += (i.a3.imm & 0x1F) << 20;    //shamt
				binary += 0b0010100 << 25;   //funct7
			break;



			case ADD:
			    // rf[i.a1.reg] = rf[i.a2.reg] + rf[i.a3.reg]; break;
				binary = (0x0C << 2) + 0x03; //opcode
				binary += i.a1.reg << 7;     //rd
				binary += 0b000 << 12;       //funct3
				binary += i.a2.reg << 15;    //rs1
				binary += i.a3.reg << 20;    //rs2
				binary += 0b0000000 << 25;   //funct7
			break;
			case SUB:
				// rf[i.a1.reg] = rf[i.a2.reg] + rf[i.a3.reg]; break;
				binary = (0x0C << 2) + 0x03; //opcode
				binary += i.a1.reg << 7;     //rd
				binary += 0b000 << 12;       //funct3
				binary += i.a2.reg << 15;    //rs1
				binary += i.a3.reg << 20;    //rs2
				binary += 0b0100000 << 25;   //funct7
			break;
			case SLT: //rf[i.a1.reg] = (*(int32_t*)&rf[i.a2.reg]) < (*(int32_t*)&rf[i.a3.reg]) ? 1 : 0; break;
				binary = (0x0C << 2) + 0x03; //opcode
				binary += i.a1.reg << 7;     //rd
				binary += 0b010 << 12;       //funct3
				binary += i.a2.reg << 15;    //rs1
				binary += i.a3.reg << 20;    //rs2
				binary += 0b0000000 << 25;   //funct7
			break;
			case SLTU: // rf[i.a1.reg] = rf[i.a2.reg] + rf[i.a3.reg]; break;
				binary = (0x0C << 2) + 0x03; //opcode
				binary += i.a1.reg << 7;     //rd
				binary += 0b011 << 12;       //funct3
				binary += i.a2.reg << 15;    //rs1
				binary += i.a3.reg << 20;    //rs2
				binary += 0b0000000 << 25;   //funct7
			break;
			case AND: // rf[i.a1.reg] = rf[i.a2.reg] & rf[i.a3.reg]; break;
				binary = (0x0C << 2) + 0x03; //opcode
				binary += i.a1.reg << 7;     //rd
				binary += 0b111 << 12;       //funct3
				binary += i.a2.reg << 15;    //rs1
				binary += i.a3.reg << 20;    //rs2
				binary += 0b0000000 << 25;   //funct7
			break;
			case OR: // rf[i.a1.reg] = rf[i.a2.reg] | rf[i.a3.reg]; break;
				binary = (0x0C << 2) + 0x03; //opcode
				binary += i.a1.reg << 7;     //rd
				binary += 0b110 << 12;       //funct3
				binary += i.a2.reg << 15;    //rs1
				binary += i.a3.reg << 20;    //rs2
				binary += 0b0000000 << 25;   //funct7
			break;
			case XOR: // rf[i.a1.reg] = rf[i.a2.reg] ^ rf[i.a3.reg]; break;
				binary = (0x0C << 2) + 0x03; //opcode
				binary += i.a1.reg << 7;     //rd
				binary += 0b100 << 12;       //funct3
				binary += i.a2.reg << 15;    //rs1
				binary += i.a3.reg << 20;    //rs2
				binary += 0b0000000 << 25;   //funct7
			break;
			case SLL: // rf[i.a1.reg] = rf[i.a2.reg] << rf[i.a3.reg]; break;
				binary = (0x0C << 2) + 0x03; //opcode
				binary += i.a1.reg << 7;     //rd
				binary += 0b001 << 12;       //funct3
				binary += i.a2.reg << 15;    //rs1
				binary += i.a3.reg << 20;    //rs2
				binary += 0b0000000 << 25;   //funct7
			break;
			case SRL: // rf[i.a1.reg] = rf[i.a2.reg] >> rf[i.a3.reg]; break;
				binary = (0x0C << 2) + 0x03; //opcode
				binary += i.a1.reg << 7;     //rd
				binary += 0b101 << 12;       //funct3
				binary += i.a2.reg << 15;    //rs1
				binary += i.a3.reg << 20;    //rs2
				binary += 0b0000000 << 25;   //funct7
			break;
			case SRA: // rf[i.a1.reg] = (*(int32_t*)&rf[i.a2.reg]) >> rf[i.a3.reg]; break;
				binary = (0x0C << 2) + 0x03; //opcode
				binary += i.a1.reg << 7;     //rd
				binary += 0b101 << 12;       //funct3
				binary += i.a2.reg << 15;    //rs1
				binary += i.a3.reg << 20;    //rs2
				binary += 0b0100000 << 25;   //funct7
			break;


			case ADDI: 
				// rf[i.a1.reg] = rf[i.a2.reg] + i.a3.imm; break;
			    binary = (0x04 << 2) + 0x03; //opcode
				binary += i.a1.reg << 7;     //rd
				binary += 0b000 << 12;       //funct3
				binary += i.a2.reg << 15;    //rs1
				binary += i.a3.imm << 20;    //imm
			break;
			case SLTI: //rf[i.a1.reg] = (*(int32_t*)&rf[i.a2.reg]) < (*(int32_t*)&(i.a3.imm)) ? 1 : 0; break;
				binary = (0x04 << 2) + 0x03; //opcode
				binary += i.a1.reg << 7;     //rd
				binary += 0b010 << 12;       //funct3
				binary += i.a2.reg << 15;    //rs1
				binary += i.a3.imm << 20;    //imm
			break;
			case SLTIU: // rf[i.a1.reg] = rf[i.a2.reg] < i.a3.imm ? 1 : 0; break;
				binary = (0x04 << 2) + 0x03; //opcode
				binary += i.a1.reg << 7;     //rd
				binary += 0b011 << 12;       //funct3
				binary += i.a2.reg << 15;    //rs1
				binary += i.a3.imm << 20;    //imm
			break;
			case ANDI: //rf[i.a1.reg] = rf[i.a2.reg] & i.a3.imm; break;
				binary = (0x04 << 2) + 0x03; //opcode
				binary += i.a1.reg << 7;     //rd
				binary += 0b111 << 12;       //funct3
				binary += i.a2.reg << 15;    //rs1
				binary += i.a3.imm << 20;    //imm
			break;
			case ORI: //rf[i.a1.reg] = rf[i.a2.reg] | i.a3.imm; break;
				binary = (0x04 << 2) + 0x03; //opcode
				binary += i.a1.reg << 7;     //rd
				binary += 0b110 << 12;       //funct3
				binary += i.a2.reg << 15;    //rs1
				binary += i.a3.imm << 20;    //imm
			break;
			case XORI: //rf[i.a1.reg] = rf[i.a2.reg] ^ i.a3.imm; break;
				binary = (0x04 << 2) + 0x03; //opcode
				binary += i.a1.reg << 7;     //rd
				binary += 0b100 << 12;       //funct3
				binary += i.a2.reg << 15;    //rs1
				binary += i.a3.imm << 20;    //imm
			break;
			case SLLI: // rf[i.a1.reg] = rf[i.a2.reg] << i.a3.imm; break;
				binary = (0x04 << 2) + 0x03; //opcode
				binary += i.a1.reg << 7;     //rd
				binary += 0b001 << 12;       //funct3
				binary += i.a2.reg << 15;    //rs1
				binary += (i.a3.imm & 0x1F) << 20;    //shamt
				binary += 0b0000000 << 25;   //funct7
			break;
			case SRLI: //rf[i.a1.reg] = rf[i.a2.reg] >> i.a3.imm; break;
				binary = (0x04 << 2) + 0x03; //opcode
				binary += i.a1.reg << 7;     //rd
				binary += 0b101 << 12;       //funct3
				binary += i.a2.reg << 15;    //rs1
				binary += (i.a3.imm & 0x1F) << 20;    //shamt
				binary += 0b0000000 << 25;   //funct7
			break;
			case SRAI: //rf[i.a1.reg] = (*(int32_t*)&rf[i.a2.reg]) >> i.a3.imm; break;
				binary = (0x04 << 2) + 0x03; //opcode
				binary += i.a1.reg << 7;     //rd
				binary += 0b101 << 12;       //funct3
				binary += i.a2.reg << 15;    //rs1
				binary += (i.a3.imm & 0x1F) << 20;    //shamt
				binary += 0b0100000 << 25;   //funct7
			break;

			case LB:
				binary = 0x03; //opcode
				binary += i.a1.reg << 7; //rd
				binary += 0b000 << 12; //funct3
				binary += i.a2.reg << 15; //rs1
				binary += i.a3.imm << 20; //offset
			break;
			case LBU: 
				binary = 0x03; //opcode
				binary += i.a1.reg << 7; //rd
				binary += 0b100 << 12; //funct3
				binary += i.a2.reg << 15; //rs1
				binary += i.a3.imm << 20; //offset
			break;
			case LH:
				binary = 0x03; //opcode
				binary += i.a1.reg << 7; //rd
				binary += 0b001 << 12; //funct3
				binary += i.a2.reg << 15; //rs1
				binary += i.a3.imm << 20; //offset
			break;
			case LHU:
				binary = 0x03; //opcode
				binary += i.a1.reg << 7; //rd
				binary += 0b101<< 12; //funct3
				binary += i.a2.reg << 15; //rs1
				binary += i.a3.imm << 20; //offset
			break;
			case LW:
				// rf[i.a1.reg] = mem_read(mem, rf[i.a2.reg]+i.a3.imm, i.op); break;
				binary = 0x03; //opcode
				binary += i.a1.reg << 7; //rd
				binary += 0b010 << 12; //funct3
				binary += i.a2.reg << 15; //rs1
				binary += i.a3.imm << 20; //offset
			break;
			


			case SB: //mem[rf[i.a2.reg]+i.a3.imm] = *(uint8_t*)&(rf[i.a1.reg]); break;
				binary = (0x08 << 2) + 0x03; //opcode
				binary += (i.a3.imm << 27)>> 20; //imm[4:0]
				binary += 0b000 << 12;       //funct3
				binary += i.a2.reg << 15;    //rs1
				binary += i.a1.reg << 20;    //rs2
				binary += (i.a3.imm >> 5) << 25;   //imm[11:5]
			break;
			case SH: //*(uint16_t*)&(mem[rf[i.a2.reg]+i.a3.imm]) = *(uint16_t*)&(rf[i.a1.reg]); break;
				binary = (0x08 << 2) + 0x03; //opcode
				binary += (i.a3.imm << 27)>> 20; //imm[4:0]
				binary += 0b001 << 12;       //funct3
				binary += i.a2.reg << 15;    //rs1
				binary += i.a1.reg << 20;    //rs2
				binary += (i.a3.imm >> 5) << 25;   //imm[11:5]
			break;

			case SW: 
			// 	*(uint32_t*)&(mem[rf[i.a2.reg]+i.a3.imm]) = rf[i.a1.reg]; 
			// 	//printf( "Writing %x to addr %x\n", rf[i.a1.reg], rf[i.a2.reg]+i.a3.imm );
			// break;
				binary = (0x08 << 2) + 0x03; //opcode
				binary += (i.a3.imm  & 0x1F) << 7; //imm[4:0]
				binary += 0b010 << 12;       //funct3
				binary += i.a2.reg << 15;    //rs1
				binary += i.a1.reg << 20;    //rs2
				binary += (i.a3.imm & 0xFE0) << 25;   //imm[11:5]
			break;

			

			case BEQ: //if ( rf[i.a1.reg] == rf[i.a2.reg] ) pc_next = i.a3.imm; break;
				binary = (0x18 << 2) + 0x03;  //opcode
				binary += (0b000 << 12);      //funct3
				binary += i.a1.reg << 15;     //rs1
				binary += i.a2.reg << 20;     //rs2
                
				offset = i.a3.imm - (inst_cnt << 2);

				binary += (offset & 0x800) >> 4; // imm[11] -> inst[7]
				binary += (offset & 0x1E) << 7; // imm[4:1] -> inst[11:8]
				binary += (offset & 0x7E0) << 20; // imm[10:5] -> inst[30:25]
				binary += (offset & 0x1000) << 19; // imm[12] -> inst[31]
			break;
			case BGE: // if ( *(int32_t*)&rf[i.a1.reg] >= *(int32_t*)&rf[i.a2.reg] ) pc_next = i.a3.imm; break;
				binary = (0x18 << 2) + 0x03;  //opcode
				binary += (0b101 << 12);      //funct3
				binary += i.a1.reg << 15;     //rs1
				binary += i.a2.reg << 20;     //rs2
                
				offset = i.a3.imm - (inst_cnt << 2);

				binary += (offset & 0x800) >> 4; // imm[11] -> inst[7]
				binary += (offset & 0x1E) << 7; // imm[4:1] -> inst[11:8]
				binary += (offset & 0x7E0) << 20; // imm[10:5] -> inst[30:25]
				binary += (offset & 0x1000) << 19; // imm[12] -> inst[31]
			break;
			case BGEU: // if ( rf[i.a1.reg] >= rf[i.a2.reg] ) pc_next = i.a3.imm; break;
				binary = (0x18 << 2) + 0x03;  //opcode
				binary += (0b111 << 12);      //funct3
				binary += i.a1.reg << 15;     //rs1
				binary += i.a2.reg << 20;     //rs2
                
				offset = i.a3.imm - (inst_cnt << 2);

				binary += (offset & 0x800) >> 4; // imm[11] -> inst[7]
				binary += (offset & 0x1E) << 7; // imm[4:1] -> inst[11:8]
				binary += (offset & 0x7E0) << 20; // imm[10:5] -> inst[30:25]
				binary += (offset & 0x1000) << 19; // imm[12] -> inst[31]
			break;
			case BLT: // if ( *(int32_t*)&rf[i.a1.reg] < *(int32_t*)&rf[i.a2.reg] ) pc_next = i.a3.imm; break;
				binary = (0x18 << 2) + 0x03;  //opcode
				binary += (0b100 << 12);      //funct3
				binary += i.a1.reg << 15;     //rs1
				binary += i.a2.reg << 20;     //rs2
                
				offset = i.a3.imm - (inst_cnt << 2);

				binary += (offset & 0x800) >> 4; // imm[11] -> inst[7]
				binary += (offset & 0x1E) << 7; // imm[4:1] -> inst[11:8]
				binary += (offset & 0x7E0) << 20; // imm[10:5] -> inst[30:25]
				binary += (offset & 0x1000) << 19; // imm[12] -> inst[31]
			break;
			case BLTU: // if ( rf[i.a1.reg] < rf[i.a2.reg] ) pc_next = i.a3.imm; break;
				binary = (0x18 << 2) + 0x03;  //opcode
				binary += (0b110 << 12);      //funct3
				binary += i.a1.reg << 15;     //rs1
				binary += i.a2.reg << 20;     //rs2
                
				offset = i.a3.imm - (inst_cnt << 2);

				binary += (offset & 0x800) >> 4; // imm[11] -> inst[7]
				binary += (offset & 0x1E) << 7; // imm[4:1] -> inst[11:8]
				binary += (offset & 0x7E0) << 20; // imm[10:5] -> inst[30:25]
				binary += (offset & 0x1000) << 19; // imm[12] -> inst[31]
			break;
			case BNE:
				// if ( rf[i.a1.reg] != rf[i.a2.reg] ) pc_next = i.a3.imm; break;
				binary = (0x18 << 2) + 0x03;  //opcode
				binary += (0b001 << 12);      //funct3
				binary += i.a1.reg << 15;     //rs1
				binary += i.a2.reg << 20;     //rs2
                
				offset = i.a3.imm - (inst_cnt << 2);

				binary += (offset & 0x800) >> 4; // imm[11] -> inst[7]
				binary += (offset & 0x1E) << 7; // imm[4:1] -> inst[11:8]
				binary += (offset & 0x7E0) << 20; // imm[10:5] -> inst[30:25]
				binary += (offset & 0x1000) << 19; // imm[12] -> inst[31]
			break;

			case JAL:
			// 	rf[i.a1.reg] = pc + 4;
			// 	pc_next = i.a2.imm;
			// 	//printf( "jal %d %x\n", pc+4, pc_next );
			// 	break;
				binary = (0x1B << 2) + 0x03;
				binary += i.a1.reg << 7;

				offset = i.a2.imm - (inst_cnt << 2);

				binary += (offset & 0xFF000); // imm[19:12] -> inst[19:12]
				binary += (offset & 0x800) << 9; // imm[11] -> inst[20]
				binary += (offset & 0x7FE) << 20; // imm[10:1] -> inst[30:21]
				binary += (offset & 0x100000) << 11; // imm[20] -> inst[31]
			break;
			case JALR:
			// 	rf[i.a1.reg] = pc + 4;
			// 	pc_next = rf[i.a2.reg] + i.a3.imm;
			// 	//printf( "jalr %d %d(%d)\n", i.a1.reg, i.a3.imm, i.a2.reg );
			// 	break;
				binary = (0x19 << 2) + 0x03; //opcode
				binary += i.a1.reg << 7; //rd
				binary += 0b000 << 12; //funct3
				binary += i.a2.reg << 15; //rs1
				binary += i.a3.imm << 20; //offset
			break;
			case AUIPC:
			// 	rf[i.a1.reg] = pc + (i.a2.imm<<12);
			// 	//printf( "auipc %x \n", rf[i.a1.reg] );
			// 	break;
				binary = (0x05 << 2) + 0x03;
				binary += i.a1.reg << 7;
				binary += (i.a2.imm << 12);
			break;

			case LUI:
				// rf[i.a1.reg] = (i.a2.imm<<12);
				binary = (0x0D << 2) + 0x03;
				binary += i.a1.reg << 7;
				binary += (i.a2.imm << 12);
			break;
			
			case HCF:
			    binary = 0x0000000B;
				dexit = true;
			break;

			case MUL:
				binary = (0x0C << 2) + 0x03; //opcode
				binary += i.a1.reg << 7;     //rd
				binary += 0b000 << 12;       //funct3
				binary += i.a2.reg << 15;    //rs1
				binary += i.a3.reg << 20;    //rs2
				binary += 0b0000001 << 25;   //funct7
			break;

			case VADD_VV:
			    binary = 0x57; //opcode
				binary += i.a1.reg << 7;     //vd
				binary += 0b000 << 12;       //funct3
				binary += i.a2.reg << 15;    //vs1
				binary += i.a3.reg << 20;    //vs2
				binary += 0b0000000 << 25;   //funct7
			break;

			case VMUL_VX:
			    binary = 0x57; //opcode
				binary += i.a1.reg << 7;     //vd
				binary += 0b100 << 12;       //funct3
				binary += i.a2.reg << 15;    //vs1
				binary += i.a3.reg << 20;    //rs2
				binary += 0b0000000 << 25;   //funct7
			break;

			case VLE8_V:
			    binary = 0x07; //opcode
				binary += i.a1.reg << 7;     //vd
				binary += 0b000 << 12;       //funct3
				binary += i.a2.reg << 15;    //rs1
				binary += 0b00000 << 20;     //lumop
				binary += 0b0000000 << 25;   //funct7
			break;

			case VSE8_V:
			    binary = 0x27; //opcode
				binary += i.a1.reg << 7;     //vd
				binary += 0b000 << 12;       //funct3
				binary += i.a2.reg << 15;    //rs1
				binary += 0b00000 << 20;     //lumop
				binary += 0b0000000 << 25;   //funct7
			break;

			case UNIMPL:
			default:
				printf( "Reached an unimplemented instruction!\n" );
				if ( i.psrc ) printf( "Instruction: %s\n", i.psrc );
				// printf( "inst: %6d pc: %6d src line: %d\n", inst_cnt, pc, i.orig_line );
				// print_regfile(rf);
				dexit = true;
			break;
		}

		if (!dexit) {
			if ( i.psrc ) fprintf(inst_file, "%s\n", i.psrc );

			fprintf(mch_file, "%02x\n", (binary>>0 ) & 0xff);
			fprintf(mch_file, "%02x\n", (binary>>8 ) & 0xff);
			fprintf(mch_file, "%02x\n", (binary>>16) & 0xff);
			fprintf(mch_file, "%02x\n", (binary>>24) & 0xff);
		}

		inst_cnt++;
	}
    
    
	//write five "nop" instructions at the end of the inst_file
	for(int t=0; t<5;t++)
	{
		fprintf(inst_file, "nop\n");
		fprintf(mch_file, "%02x\n", 0x00);
		fprintf(mch_file, "%02x\n", 0x00);
		fprintf(mch_file, "%02x\n", 0x00);
		fprintf(mch_file, "%02x\n", 0x00);
	}
	//write "hcf" in the inst_file
	fprintf(inst_file, "hcf\n");
	fprintf(mch_file, "%02x\n", 0x0B);
	fprintf(mch_file, "%02x\n", 0x00);
	fprintf(mch_file, "%02x\n", 0x00);
	fprintf(mch_file, "%02x\n", 0x00);

	//write data to data.hex
	write_data_hex(mem,data_file);

    fclose(inst_file);
	fclose(mch_file);
	fclose(data_file);
}
