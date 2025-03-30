# See LICENSE for license details.

#*****************************************************************************
# lhu.S
#-----------------------------------------------------------------------------
#
# Test lhu instruction.
#

#include "riscv_test.h"
#include "test_macros.h"

RVTEST_RV32U
#-------------------------------------------------------------
# Single tests
#-------------------------------------------------------------
lui x1, 0x00000008
TEST_INSERT_NOPS_5
lhu x2, 6(x1)

RVTEST_CODE_END
  .data
RVTEST_DATA_BEGIN

  TEST_DATA

tdat:
tdat1:  .half 0x00ff
tdat2:  .half 0xff00
tdat3:  .half 0x0ff0
tdat4:  .half 0xf00f

RVTEST_DATA_END
