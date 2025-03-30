# See LICENSE for license details.

#*****************************************************************************
# sw.S
#-----------------------------------------------------------------------------
#
# Test sw instruction.
#

#include "riscv_test.h"
#include "test_macros.h"

RVTEST_RV32U
#-------------------------------------------------------------
# Single tests
#-------------------------------------------------------------
lui x1, 0x00000008
lui x2, 0x000deadc
TEST_INSERT_NOPS_5
addi x2, x2, 0xfffffeef
TEST_INSERT_NOPS_5
sw x2, 0(x1)
TEST_INSERT_NOPS_5
lw x3, 0(x1)

RVTEST_CODE_END

  .data
RVTEST_DATA_BEGIN

  TEST_DATA

tdat:
tdat1:  .word 0x00000000


RVTEST_DATA_END
