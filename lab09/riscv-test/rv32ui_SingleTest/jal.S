# See LICENSE for license details.

#*****************************************************************************
# jal.S
#-----------------------------------------------------------------------------
#
# Test jal instruction.
#

#include "riscv_test.h"
#include "test_macros.h"

RVTEST_RV32U
#-------------------------------------------------------------
# Single tests
#-------------------------------------------------------------
addi x3, x0, 0x00000001
TEST_INSERT_NOPS_5
jal x1, jump_target
TEST_INSERT_NOPS_5
addi x3, x0, 0x00000000
jump_target:

RVTEST_CODE_END

  .data
RVTEST_DATA_BEGIN

  TEST_DATA

RVTEST_DATA_END
