# See LICENSE for license details.

#*****************************************************************************
# bge.S
#-----------------------------------------------------------------------------
#
# Test bge instruction.
#

#include "riscv_test.h"
#include "test_macros.h"

RVTEST_RV32U
#-------------------------------------------------------------
# Single tests
#-------------------------------------------------------------
  addi x1, x0, 16
  addi x2, x0, -16
  TEST_INSERT_NOPS_5
  bge  x1, x2, branch_target
  addi x3, x0, 0x00000000
branch_target:
  addi x3, x0, 0x00000001

RVTEST_CODE_END

  .data
RVTEST_DATA_BEGIN

  TEST_DATA

RVTEST_DATA_END
