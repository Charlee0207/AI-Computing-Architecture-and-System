# See LICENSE for license details.

#*****************************************************************************
# TestALL.S
#-----------------------------------------------------------------------------
#
# Test all instruction in single test.
#

#include "riscv_test.h"
#include "test_macros.h"

RVTEST_RV32U
#-------------------------------------------------------------
# Single tests
#-------------------------------------------------------------
addi x1, x0, 16
TEST_INSERT_NOPS_5
slli x2, x1, 3
TEST_INSERT_NOPS_5
xori x3, x2, 0xfffff800
TEST_INSERT_NOPS_5
slti x4, x2, -3
TEST_INSERT_NOPS_5
sltiu x5, x2, -3
TEST_INSERT_NOPS_5
srli x6, x3, 2
TEST_INSERT_NOPS_5
srai x7, x3, 2
TEST_INSERT_NOPS_5
ori x8, x0, 123
TEST_INSERT_NOPS_5
andi x9, x5, -1
TEST_INSERT_NOPS_5
add x10, x5, x6
TEST_INSERT_NOPS_5
sub x11, x3, x7
TEST_INSERT_NOPS_5
sll x12, x5, x8
TEST_INSERT_NOPS_5
slt x13, x7, x9
TEST_INSERT_NOPS_5
sltu x14, x9, x2
TEST_INSERT_NOPS_5
xor x15, x11, x3
TEST_INSERT_NOPS_5
srl x16, x13, x4
TEST_INSERT_NOPS_5
sra x17, x15, x5
TEST_INSERT_NOPS_5
or x18, x17, x1
TEST_INSERT_NOPS_5
and x19, x13, x10
TEST_INSERT_NOPS_5
sb x3, 3(x0)
TEST_INSERT_NOPS_5
sw x1, 4(x0)
TEST_INSERT_NOPS_5
lb x20, 3(x0)
TEST_INSERT_NOPS_5
lw x21, 4(x0)
TEST_INSERT_NOPS_5
lbu x22, 3(x0)
TEST_INSERT_NOPS_5
lui x24, 0x1238
TEST_INSERT_NOPS_5
beq x1, x2, hello
TEST_INSERT_NOPS_5
bne x1, x1, hello
TEST_INSERT_NOPS_5
blt x1, x0, hello
TEST_INSERT_NOPS_5
bge x3, x0, hello
TEST_INSERT_NOPS_5
bltu x11, x3, hello
TEST_INSERT_NOPS_5
bgeu x3, x11, hello
TEST_INSERT_NOPS_5
jalr x25, 752(x0)
TEST_INSERT_NOPS_5
add x10, x5, x6
TEST_INSERT_NOPS_5
jal x0, exit
hello:
TEST_INSERT_NOPS_5
sll x12, x5, x8
exit:

RVTEST_CODE_END

  .data
RVTEST_DATA_BEGIN

  TEST_DATA

RVTEST_DATA_END
