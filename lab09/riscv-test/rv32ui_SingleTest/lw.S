# See LICENSE for license details.

#*****************************************************************************
# lw.S
#-----------------------------------------------------------------------------
#
# Test lw instruction.
#

#include "riscv_test.h"
#include "test_macros.h"

RVTEST_RV32U
#-------------------------------------------------------------
# Single tests
#-------------------------------------------------------------
lui x1, 0x00000008
TEST_INSERT_NOPS_5
lw x2, 4(x1)

RVTEST_CODE_END

  .data
RVTEST_DATA_BEGIN

  TEST_DATA

tdat:
tdat1:  .word 0x00ff00ff
tdat2:  .word 0xff00ff00
tdat3:  .word 0x0ff00ff0
tdat4:  .word 0xf00ff00f

RVTEST_DATA_END
