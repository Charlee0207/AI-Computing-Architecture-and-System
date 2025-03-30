#include <iostream>
#include <string>
#include <sstream>
#include <vector>
#include <random>
#include <fstream>

using namespace std;

typedef enum{
    BINV=0,       // binv rd, rs1, rs2
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
} instr_type;

const vector<string> instr_name = {
    "BINV",         // binv rd, rs1, rs2
    "BINVI",        // binvi rd, rs1, imm
    "BSET",         // bset rd, rs1, rs2
    "BSETI",        // bseti rd, rs1, imm
    "SEXT_B",       // sext.b rd, rs
    "SEXT_H",       // sext.h rd, rs
    "SH1ADD",       // sh1add rd, rs1, rs2
    "SH2ADD",       // sh2add rd, rs1, rs2
    "SH3ADD",       // sh3add rd, rs1, rs2
    "XNOR",         // xnor rd, rs1, rs2
    "ZEXT_H"        // zext.h rd, rs
};
const vector<string> instr = {
    "binv",         // binv rd, rs1, rs2
    "binvi",        // binvi rd, rs1, imm
    "bset",         // bset rd, rs1, rs2
    "bseti",        // bseti rd, rs1, imm
    "sext.b",       // sext.b rd, rs
    "sext.h",       // sext.h rd, rs
    "sh1add",       // sh1add rd, rs1, rs2
    "sh2add",       // sh2add rd, rs1, rs2
    "sh3add",       // sh3add rd, rs1, rs2
    "xnor",         // xnor rd, rs1, rs2
    "zext.h"        // zext.h rd, rs
};

stringstream testcase_gen(instr_type i, int testcase_cnt){
    uint32_t rd, rs1, rs2, imm;
    string lui_rd = "lui x30, ";
    string addi_rd = "addi x30, x30, ";
    string lui_rs1 = "lui x28, ";
    string addi_rs1 = "addi x28, x28, ";
    string lui_rs2 = "lui x29, ";
    string addi_rs2 = "addi x29, x29, ";
    stringstream assembly;

    switch(i){
        case BINV:
            rs1 = rand() & 0xFFFFFFFF;  rs2 = rand() & 0xFFFFFFFF;
            rd = rs1 ^ (1 << (rs2&0x1F));
            // load question and answer
            assembly << lui_rs1 << ((rs1&0x800) ? (rs1>>12)+1 : rs1>>12) << "\n"
                     << addi_rs1 << (rs1&0xFFF) << "\n"
                     << lui_rs2 << ((rs2&0x800) ? (rs2>>12)+1 : rs2>>12) << "\n"
                     << addi_rs2 << (rs2&0xFFF) << "\n"
                     << lui_rd << ((rd&0x800) ? (rd>>12)+1 : rd>>12) << "\n"
                     << addi_rd << (rd&0xFFF) << "\n\n";
            // load test instruction
            assembly << instr[i] << " x5, x28, x29\n";
        break;
        case BINVI:
            rs1 = rand() & 0xFFFFFFFF;  imm = rand() & 0x1F; 
            rd = rs1 ^ (1 << (imm&0x1F));
            // load question and answer
            assembly << lui_rs1 << ((rs1&0x800) ? (rs1>>12)+1 : rs1>>12) << "\n"
                     << addi_rs1 << (rs1&0xFFF) << "\n"
                     << lui_rd << ((rd&0x800) ? (rd>>12)+1 : rd>>12) << "\n"
                     << addi_rd << (rd&0xFFF) << "\n\n";
            // load test instruction
            assembly << instr[i] << " x5, x28, " << imm << "\n";
        break;
        case BSET:
            rs1 = rand() & 0xFFFFFFFF;  rs2 = rand() & 0xFFFFFFFF;
            rd = rs1 | (1 << (rs2&0x1F));
            // load question and answer
            assembly << lui_rs1 << ((rs1&0x800) ? (rs1>>12)+1 : rs1>>12) << "\n"
                     << addi_rs1 << (rs1&0xFFF) << "\n"
                     << lui_rs2 << ((rs2&0x800) ? (rs2>>12)+1 : rs2>>12) << "\n"
                     << addi_rs2 << (rs2&0xFFF) << "\n"
                     << lui_rd << ((rd&0x800) ? (rd>>12)+1 : rd>>12) << "\n"
                     << addi_rd << (rd&0xFFF) << "\n\n";
            // load test instruction
            assembly << instr[i] << " x5, x28, x29\n";
        break;
        case BSETI:
            rs1 = rand() & 0xFFFFFFFF;  imm = rand() & 0x1F;
            rd = rs1 | (1 << (imm&0x1F));
            // load question and answer
            assembly << lui_rs1 << ((rs1&0x800) ? (rs1>>12)+1 : rs1>>12) << "\n"
                     << addi_rs1 << (rs1&0xFFF) << "\n"
                     << lui_rd << ((rd&0x800) ? (rd>>12)+1 : rd>>12) << "\n"
                     << addi_rd << (rd&0xFFF) << "\n\n";
            // load test instruction
            assembly << instr[i] << " x5, x28, " << imm << "\n";
        break;
        case SEXT_B:
            rs1 = rand() & 0xFFFFFFFF;
            rd = (int8_t)(rs1 & 0xFF);
            // load question and answer
            assembly << lui_rs1 << ((rs1&0x800) ? (rs1>>12)+1 : rs1>>12) << "\n"
                     << addi_rs1 << (rs1&0xFFF) << "\n"
                     << lui_rd << ((rd&0x800) ? (rd>>12)+1 : rd>>12) << "\n"
                     << addi_rd << (rd&0xFFF) << "\n\n";
            // load test instruction
            assembly << instr[i] << " x5, x28\n";
        break;
        case SEXT_H:
            rs1 = rand() & 0xFFFFFFFF;
            rd = (int8_t)(rs1 & 0xFFFF);
            // load question and answer
            assembly << lui_rs1 << ((rs1&0x800) ? (rs1>>12)+1 : rs1>>12) << "\n"
                     << addi_rs1 << (rs1&0xFFF) << "\n"
                     << lui_rd << ((rd&0x800) ? (rd>>12)+1 : rd>>12) << "\n"
                     << addi_rd << (rd&0xFFF) << "\n\n";
            // load test instruction
            assembly << instr[i] << " x5, x28\n";
        break;
        case SH1ADD:
            rs1 = rand() & 0xFFFFFFFF;  rs2 = rand() & 0xFFFFFFFF;
            rd = (rs1 << 1) + rs2;
            // load question and answer
            assembly << lui_rs1 << ((rs1&0x800) ? (rs1>>12)+1 : rs1>>12) << "\n"
                     << addi_rs1 << (rs1&0xFFF) << "\n"
                     << lui_rs2 << ((rs2&0x800) ? (rs2>>12)+1 : rs2>>12) << "\n"
                     << addi_rs2 << (rs2&0xFFF) << "\n"
                     << lui_rd << ((rd&0x800) ? (rd>>12)+1 : rd>>12) << "\n"
                     << addi_rd << (rd&0xFFF) << "\n\n";
            // load test instruction
            assembly << instr[i] << " x5, x28, x29\n";
        break;
        case SH2ADD:
            rs1 = rand() & 0xFFFFFFFF;  rs2 = rand() & 0xFFFFFFFF;
            rd = (rs1 << 2) + rs2;
            // load question and answer
            assembly << lui_rs1 << ((rs1&0x800) ? (rs1>>12)+1 : rs1>>12) << "\n"
                     << addi_rs1 << (rs1&0xFFF) << "\n"
                     << lui_rs2 << ((rs2&0x800) ? (rs2>>12)+1 : rs2>>12) << "\n"
                     << addi_rs2 << (rs2&0xFFF) << "\n"
                     << lui_rd << ((rd&0x800) ? (rd>>12)+1 : rd>>12) << "\n"
                     << addi_rd << (rd&0xFFF) << "\n\n";
            // load test instruction
            assembly << instr[i] << " x5, x28, x29\n";
        break;
        case SH3ADD:
            rs1 = rand() & 0xFFFFFFFF;  rs2 = rand() & 0xFFFFFFFF;
            rd = (rs1 << 3) + rs2;
            // load question and answer
            assembly << lui_rs1 << ((rs1&0x800) ? (rs1>>12)+1 : rs1>>12) << "\n"
                     << addi_rs1 << (rs1&0xFFF) << "\n"
                     << lui_rs2 << ((rs2&0x800) ? (rs2>>12)+1 : rs2>>12) << "\n"
                     << addi_rs2 << (rs2&0xFFF) << "\n"
                     << lui_rd << ((rd&0x800) ? (rd>>12)+1 : rd>>12) << "\n"
                     << addi_rd << (rd&0xFFF) << "\n\n";
            // load test instruction
            assembly << instr[i] << " x5, x28, x29\n";
        break;
        case XNOR:
            rs1 = rand() & 0xFFFFFFFF;  rs2 = rand() & 0xFFFFFFFF;
            rd = ~(rs1 ^ rs2);
            // load question and answer
            assembly << lui_rs1 << ((rs1&0x800) ? (rs1>>12)+1 : rs1>>12) << "\n"
                     << addi_rs1 << (rs1&0xFFF) << "\n"
                     << lui_rs2 << ((rs2&0x800) ? (rs2>>12)+1 : rs2>>12) << "\n"
                     << addi_rs2 << (rs2&0xFFF) << "\n"
                     << lui_rd << ((rd&0x800) ? (rd>>12)+1 : rd>>12) << "\n"
                     << addi_rd << (rd&0xFFF) << "\n\n";
            // load test instruction
            assembly << instr[i] << " x5, x28, x29\n";
        break;
        case ZEXT_H:
            rs1 = rand() & 0xFFFFFFFF;
            rd = rs1 & 0xFFFF;
            // load question and answer
            assembly << lui_rs1 << ((rs1&0x800) ? (rs1>>12)+1 : rs1>>12) << "\n"
                     << addi_rs1 << (rs1&0xFFF) << "\n"
                     << lui_rd << ((rd&0x800) ? (rd>>12)+1 : rd>>12) << "\n"
                     << addi_rd << (rd&0xFFF) << "\n\n";
            // load test instruction
            assembly << instr[i] << " x5, x28\n";
            break;
    }

    // check answer
    assembly << "\n";
    assembly << "addi x7, x7, 1\n"
             << "bne x5, x30, QUIT\n";
    assembly << "\n\n\n";

    return assembly;
}

int main(){
    for(int i=BINV; i<=ZEXT_H; i++){
        fstream fout("test_"+instr_name[i]+".txt", ios::out);
        stringstream assembly;
        fout << "## RV32 Emulator Testing Assembly Code for " << instr_name[i] << " function\n\n";
        fout << "main:\n\n";

        for(int testcase_cnt=0; testcase_cnt<100; testcase_cnt++){
            assembly = testcase_gen((instr_type)i, testcase_cnt);
            fout << assembly.rdbuf();
        }
        fout << "\nQUIT:\n"
             << "hcf\n";

        fout.close();
    }
}